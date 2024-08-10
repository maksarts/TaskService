package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    @Schema(description = "email of authorised user")
    private String email;

    @Schema(description = "JWT token, needed for the API access")
    private String token;

    @Schema(description = "Error description in case of fail")
    private String errMsg;

    private String refreshToken;
    private String tokenType;
}
