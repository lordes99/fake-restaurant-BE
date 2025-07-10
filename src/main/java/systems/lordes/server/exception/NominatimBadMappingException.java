package systems.lordes.server.exception;

public class NominatimBadMappingException extends RuntimeException {
    public NominatimBadMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NominatimBadMappingException(String message) {
        super(message);
    }
}
