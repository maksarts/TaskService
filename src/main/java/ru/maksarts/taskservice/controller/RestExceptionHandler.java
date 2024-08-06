package ru.maksarts.taskservice.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.PropertyValueException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.dto.BasicResponse;

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
    protected ResponseEntity<Object> handleClientSideErrorException(ClientSideErrorException ex, WebRequest request) {
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

    @ExceptionHandler({PropertyValueException.class})
    protected ResponseEntity<Object> handleInvalidArgument(PropertyValueException ex) {
        return new ResponseEntity<>(
                BasicResponse.builder()
                        .errMsg("Invalid request")
                        .errDesc(ex.getLocalizedMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

}
