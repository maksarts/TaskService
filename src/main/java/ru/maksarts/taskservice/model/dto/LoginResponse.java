package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    @Schema(description = "email авторизованного пользователя")
    private String email;

    @Schema(description = "JWT token, необходимый для доступа к API")
    private String token;

    @Schema(description = "Описание ошибки в случае неудачной авторизации")
    private String errMsg;

    private String refreshToken;
    private String tokenType;
}
