package systems.lordes.server.service;

import io.minio.*;
import jakarta.validation.constraints.NotNull;
import systems.lordes.server.config.MinioConfig;
import systems.lordes.server.data.BucketS3Url;
import systems.lordes.server.exception.BadRequestException;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import kotlin.Pair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class StorageService {

    public record PresignedURL(String presignedURL, Instant expiresAt) {}
    public record UploadResult(String etag) {}

    public record DownloadResource(String bucket, String path, Resource resource, HttpHeaders headers) {
        public boolean isNotFound() {
            return resource == null;
        }
    }

    private static final char DELIMITER_PATH_CHAR = '/';
    public static final String RANGE_IS_NOT_SATISFIABLE = "The requested range is not satisfiable";
    public static final String META_X_AMZ_META = "x-amz-meta-";

    private final MinioClient minioClient;
    private final MinioConfig.MinioProperties minioProperties;
    private final String defaultBucket;

//    private final URI externalDomain;
//    private final String externalDomainHost;

    @Autowired
    public StorageService(MinioClient minioClient, MinioConfig.MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
        this.defaultBucket = minioProperties.getDefaultBucket();
    }

    public DownloadResource downloadResource(@Nullable String bucket, String path, Long offset) {
        if (bucket == null) {
            bucket = defaultBucket;
        }
        try {
            StatObjectResponse objectStat = checkExistsAndGetState(bucket, path);
            HttpHeaders httpHeaders = buildHttpHeaders(objectStat);
            if (httpHeaders.getContentLength() > 0 && offset != null && httpHeaders.getContentLength() > offset) {
                httpHeaders.setContentLength(httpHeaders.getContentLength() - offset);
            }

            InputStream object = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .offset(offset)
                    .build()
            );
            return new DownloadResource(bucket, path, new InputStreamResource(object), httpHeaders);
        } catch (Exception e) {
            if (e instanceof ErrorResponseException && isNotFoundError((ErrorResponseException)e)) {
                return new DownloadResource(bucket, path, null, null);
            } else if (RANGE_IS_NOT_SATISFIABLE.equalsIgnoreCase(e.getMessage())) {
                throw new BadRequestException(RANGE_IS_NOT_SATISFIABLE, e);
            }
            throw new RuntimeException("Cannot serve resource " + bucket + " " + path, e);
        }
    }

    public UploadResult uploadResource(Resource body, @Nullable String bucket, String path, @Nullable Multimap<String, String> xamzMetaHeaders, @Nullable MediaType contentType) {
        if (bucket == null) {
            bucket = defaultBucket;
        }
        try {
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .userMetadata(xamzMetaHeaders)
                    .contentType(Optional.ofNullable(contentType)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM)
                        .toString())
                    .stream(body.getInputStream(), body.contentLength(), 33_554_432) // 30MiB
                    .build()
            );

            return new UploadResult(objectWriteResponse.etag());
        } catch (Exception e) {
            throw new RuntimeException("Cannot upload resource " + bucket + " " + path, e);
        }
    }


    @SneakyThrows
    private StatObjectResponse checkExistsAndGetState(String bucket, String path)  {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(path).build());
        } catch (ErrorResponseException e) {
            if (isNotFoundError(e)) {
                return null;
            }
            throw e;
        }
    }

    private boolean isNotFoundError(ErrorResponseException e) {
        // TODO: test on Amazon S3
        String msg = e.errorResponse().message();
        return "Object does not exist".equals(msg) || "The specified key does not exist.".equals(msg);
    }

    private HttpHeaders buildHttpHeaders(HttpServletRequest httpServletRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Enumeration<String> headerNames = httpServletRequest.getHeaderNames(); headerNames.hasMoreElements();) {
            String headerName = headerNames.nextElement();
            if (!"host".equalsIgnoreCase(headerName)) {
                httpHeaders.add(headerName, httpServletRequest.getHeader(headerName));
            }
        }
        return httpHeaders;
    }

    private HttpHeaders buildHttpHeaders(@Nullable StatObjectResponse objectStat) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (objectStat != null) {
            for (Pair<? extends String, ? extends String> header : objectStat.headers()) {
                httpHeaders.add(header.getFirst(), header.getSecond());
            }
        }
        return httpHeaders;
    }

    public PresignedURL requestDownloadPresignedURL(
            @Nullable String bucket, String path, String filename, Instant expiresAt, boolean inline, String resourceVersionId) {
        if (bucket == null) {
            bucket = defaultBucket;
        }
        return requestPresignedURL(
            new BucketS3Url(bucket, path), expiresAt, false, filename, inline,
                resourceVersionId);
    }

    public PresignedURL requestUploadPresignedURL(String bucket, String path, String filename, Instant expiresAt) {
        BucketS3Url bucketResource = new BucketS3Url(bucket, path);
        return requestPresignedURL(
            bucketResource, expiresAt, true, filename, false,
                null);
    }

    @SneakyThrows
    private PresignedURL requestPresignedURL(BucketS3Url bucketResource, Instant expiresAt,
                                             boolean isUpload, String filename, boolean inline, String resourceVersionId) {
        Instant now = Instant.now();
        int expireInMins = Optional.ofNullable(expiresAt)
                .map(e -> Duration.between(now, e).toMinutes())
                .orElseGet(() -> minioProperties.getDefaultTicketExpiration().toMinutes())
                .intValue();

        if (expireInMins < 1) {
            throw new ValidBeforeException("invalid value for expireInMins (< 1 min)");
        }

        String path = bucketResource.path();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        GetPresignedObjectUrlArgs.Builder presignedObj = GetPresignedObjectUrlArgs.builder()
                .expiry(expireInMins, TimeUnit.MINUTES)
                .bucket(bucketResource.bucket())
                .object(path)
            ;
        if (!Strings.isNullOrEmpty(resourceVersionId)) {
            presignedObj.versionId(resourceVersionId);
        }

        if (isUpload) {
            // Existance has no meaning in distributed contexts: you should resort to Legal Hold to avoid overwrites

//            if (checkExistence && checkExistsAndGetState(bucketResource.bucket(), bucketResource.path()) != null) {
//                throw new RuntimeException("objects exists and overwrite is false");
//            }
            presignedObj.method(Method.PUT);
        } else {
            // Content-Disposition: inline
            if (inline) {
                presignedObj.extraQueryParams(
                    Map.of("response-content-disposition", "inline")
                );
            } else {
                if (!Strings.isNullOrEmpty(filename)) {
                    presignedObj.extraQueryParams(
                        Map.of("response-content-disposition", String.format("attachment; filename=\"%s\"", filename))
                    );
                }
            }
            presignedObj.method(Method.GET);
        }

        GetPresignedObjectUrlArgs request = presignedObj.build();
        String presignedObjectUrl = minioClient.getPresignedObjectUrl(request);
        return new PresignedURL(presignedObjectUrl, now.plus(expireInMins, ChronoUnit.MINUTES));
    }

    public void storageDelete(BucketS3Url bucketResource) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketResource.bucket()).object(bucketResource.path()).build());
        } catch (Exception e) {
            if (e instanceof ErrorResponseException && isNotFoundError((ErrorResponseException)e)) {
                return;
            }
            throw new RuntimeException(e);
        }
    }

    public final static class ValidBeforeException extends RuntimeException {
        public ValidBeforeException(String message) {
            super(message);
        }
    }

    public void setLegalHold(String bucket, String path, String versionId) {
        if (bucket == null) {
            bucket = defaultBucket;
        }
        BucketS3Url bucketResource = new BucketS3Url(bucket, path);
        try {
            minioClient.enableObjectLegalHold(
                    EnableObjectLegalHoldArgs.builder()
                            .bucket(bucketResource.bucket())
                            .object(bucketResource.path())
                            .versionId(versionId)
                            .build()
            );
        } catch (Exception e) {
            if ("The specified key does not exist.".equals(e.getMessage())) {
                throw new BadRequestException("The specified resource was not found on bucket");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public @NotNull URL getEndpoint() {
        return minioProperties.getEndpoint();
    }

    public @NotNull String getDefaultBucket() {
        return defaultBucket;
    }

}
