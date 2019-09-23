package mandarin.controllers.api;

import mandarin.auth.exceptions.UnauthorizedException;
import mandarin.utils.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice("mandarin.controllers.api")
public class APIControllerAdvice {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public BasicResponse onException(Exception e) {
        return BasicResponse.fail().message(e.getMessage());
    }
}
