package mandarin.controllers.api;

import mandarin.auth.exceptions.AuthenticationException;
import mandarin.utils.BasicResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

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
        if (e instanceof MethodArgumentTypeMismatchException || e instanceof HttpMessageNotReadableException) {
            message = "The data you have entered is invalid. Please check again.";
        } else if (e instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        } else if (e instanceof CannotAcquireLockException) {
            message = "The system is busy. Please try again later.";
        }
        return ResponseEntity.status(status).body(BasicResponse.fail().message(message));
    }
}
