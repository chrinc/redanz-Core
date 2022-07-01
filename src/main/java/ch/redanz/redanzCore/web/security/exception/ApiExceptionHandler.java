package ch.redanz.redanzCore.web.security.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
@Getter
@Setter
public class ApiExceptionHandler {

    @ExceptionHandler(value={ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException apiRequestException) {

        // 1. create payload containing exception details
        HttpStatus badRequest = apiRequestException.httpStatus == null ?HttpStatus.BAD_REQUEST : apiRequestException.httpStatus;
        ApiException apiException = new ApiException(
                apiRequestException.getMessage(),
                badRequest,
                ZonedDateTime.now()
        );

        // 2. return response entity
        return new ResponseEntity<>(apiException, badRequest);
    }
}
