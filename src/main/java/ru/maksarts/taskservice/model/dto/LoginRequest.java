package ru.maksarts.taskservice.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotNull
    private String email;

    @NotNull
    @Size(min = 8)
    private String password;
}
