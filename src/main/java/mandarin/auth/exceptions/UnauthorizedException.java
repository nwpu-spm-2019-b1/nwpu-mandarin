package mandarin.auth.exceptions;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException() {
        super("Unauthorized access");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
