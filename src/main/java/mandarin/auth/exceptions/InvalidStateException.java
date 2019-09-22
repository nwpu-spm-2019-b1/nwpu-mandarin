package mandarin.auth.exceptions;

public class InvalidStateException extends AuthenticationException {
    public InvalidStateException() {
        super("Session state invalid");
    }

    public InvalidStateException(String message) {
        super(message);
    }
}
