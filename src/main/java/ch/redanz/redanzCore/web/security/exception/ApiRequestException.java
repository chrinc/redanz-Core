package ch.redanz.redanzCore.web.security.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

public class ApiRequestException extends RuntimeException{
    HttpStatus httpStatus;
    public ApiRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiRequestException(String message
    ) {
        super(message);
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
