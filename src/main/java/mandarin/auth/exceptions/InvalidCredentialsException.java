package mandarin.auth.exceptions;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(){
        super("Username or password incorrect");
    }
    public InvalidCredentialsException(String message){
        super(message);
    }
}
