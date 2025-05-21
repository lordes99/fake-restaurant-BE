package systems.lordes.server.config;

import io.minio.MinioClient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.net.URL;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MinioConfig.MinioProperties.class)
public class MinioConfig {

    @ConfigurationProperties("storage")
    @Validated // Necessario per validazione!
    @Data
    public static class MinioProperties {
        @NotNull
        private URL endpoint;
        @NotNull
        private String accessKey;
        private Duration defaultTicketExpiration;
        private String defaultBucket;
    }

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties,
                                   @Value("${storage.secret-key}") String secretKey) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), secretKey)
                .build();
    }

}
