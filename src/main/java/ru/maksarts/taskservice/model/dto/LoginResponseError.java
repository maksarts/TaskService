package ru.maksarts.taskservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseError {
    private int status;
    private String error;
    private Instant timestamp;
    private String message;
    @JsonIgnore
    private String path;
}
