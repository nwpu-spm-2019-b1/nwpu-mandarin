package mandarin.auth.exceptions;

public class WrongUserTypeException extends AuthenticationException {
    public WrongUserTypeException(){
        super("User has wrong type");
    }
}
