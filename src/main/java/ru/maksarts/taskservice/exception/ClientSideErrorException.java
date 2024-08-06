package ru.maksarts.taskservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ClientSideErrorException extends RuntimeException {
    public ClientSideErrorException(String message) {
        super(String.format("Invalid query: %s", message));
    }
}
