package ru.maksarts.taskservice.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import ru.maksarts.taskservice.model.RefreshToken;
import ru.maksarts.taskservice.model.dto.RefreshTokenRequest;
import ru.maksarts.taskservice.model.dto.RefreshTokenResponse;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);
    RefreshToken verifyExpiration(RefreshToken token);
    Optional<RefreshToken> findByToken(String token);
    RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
    ResponseCookie generateRefreshTokenCookie(String token);
    String getRefreshTokenFromCookies(HttpServletRequest request);
    void deleteByToken(String token);
    ResponseCookie getCleanRefreshTokenCookie();
}
