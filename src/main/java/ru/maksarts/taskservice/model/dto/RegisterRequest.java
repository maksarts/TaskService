package ru.maksarts.taskservice.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotNull
    private String email;

    @NotNull
    private String name;

    @NotNull
    @Size(min = 8)
    private String password;
}
