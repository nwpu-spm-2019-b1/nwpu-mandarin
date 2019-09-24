package mandarin.exceptions;

public class APIException extends RuntimeException {
    public APIException() {
        super("An exception has occurred during request processing");
    }

    public APIException(String message) {
        super(message);
    }
}
