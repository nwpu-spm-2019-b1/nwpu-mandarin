package mandarin.controllers.api;

import mandarin.auth.exceptions.AuthenticationException;
import mandarin.utils.BasicResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice("mandarin.controllers.api")
public class APIControllerAdvice {
    @ExceptionHandler(Exception.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ResponseBody
    public ResponseEntity<BasicResponse> onException(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
        }
        String message = e.getMessage();
        if (e instanceof MethodArgumentTypeMismatchException) {
            message = "The data you have entered is invalid. Please check again.";
        }
        return ResponseEntity.status(status).body(BasicResponse.fail().message(message));
    }
}
