package ru.maksarts.taskservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8)
    private String password;
}
