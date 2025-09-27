package systems.lordes.server.data;

public record BucketS3Url(String bucket, String path) {

    @Override
    public String toString() {
        return "s3://" + bucket + path;
    }


}
