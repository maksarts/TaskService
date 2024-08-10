package ru.maksarts.taskservice.controller;

import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.dto.BasicResponse;
import ru.maksarts.taskservice.model.dto.LoginResponseError;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(
                BasicResponse.builder()
                    .errMsg("Malformed JSON Request")
                    .errDesc(ex.getLocalizedMessage())
                    .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ClientSideErrorException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleClientSideErrorException(ClientSideErrorException ex) {
        return new ResponseEntity<>(
                BasicResponse.builder().errMsg(ex.getLocalizedMessage()).build(),
                HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(
                BasicResponse.builder()
                        .errMsg("Invalid arguments")
                        .errDesc(ex.getLocalizedMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PropertyValueException.class, IllegalArgumentException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleInvalidArgument(Exception ex) {
        return new ResponseEntity<>(
                BasicResponse.builder()
                        .errMsg("Invalid request")
                        .errDesc(ex.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseBody
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return new ResponseEntity<>(
                BasicResponse.builder()
                        .errMsg("Not found")
                        .errDesc(ex.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AuthenticationException.class, SignatureException.class})
    @ResponseBody
    public ResponseEntity<LoginResponseError> handleAuthenticationException(Exception authException, WebRequest request) {
        log.error("Unauthorized error: {}", authException.getMessage());
        LoginResponseError body = LoginResponseError.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .timestamp(Instant.now())
                .message(authException.getMessage())
                .build();
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

}
