package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginResponse {
    @Schema(description = "email авторизованного пользователя")
    private String email;

    @Schema(description = "JWT token, необходимый для доступа к API")
    private String token;

    @Schema(description = "Описание ошибки в случае неудачной авторизации")
    private String errMsg;
}
