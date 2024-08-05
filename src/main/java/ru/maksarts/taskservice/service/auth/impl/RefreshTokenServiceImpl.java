package ru.maksarts.taskservice.service.auth.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import ru.maksarts.taskservice.exception.TokenException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.RefreshToken;
import ru.maksarts.taskservice.model.dto.RefreshTokenRequest;
import ru.maksarts.taskservice.model.dto.RefreshTokenResponse;
import ru.maksarts.taskservice.repository.RefreshTokenRepository;
import ru.maksarts.taskservice.service.EmployeeService;
import ru.maksarts.taskservice.service.auth.JwtService;
import ru.maksarts.taskservice.service.auth.RefreshTokenService;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final EmployeeService employeeService;
    private final EmployeeUserDetailsService employeeUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${taskservice.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Value("${taskservice.security.jwt.refresh-token.cookie-name}")
    private String refreshTokenName;
    @Override
    public RefreshToken createRefreshToken(String email) {
        Employee emp = employeeService.getEmployeeByEmail(email);
        RefreshToken refreshToken = RefreshToken.builder()
                .revoked(false)
                .employee(emp)
                .token(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token == null){
            log.error("Token is null");
            throw new TokenException(null, "Token is null");
        }
        if(token.getExpiryDate().compareTo(Instant.now()) < 0 ){
            refreshTokenRepository.delete(token);
            throw new TokenException(token.getToken(), "Refresh token was expired. Please make a new authentication request");
        }
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshTokenResponse generateNewToken(RefreshTokenRequest request) {
        Employee emp = refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getEmployee)
                .orElseThrow(() -> new TokenException(request.getRefreshToken(),"Refresh token does not exist"));

        String token = jwtService.generateToken(employeeUserDetailsService.loadUserByUsername(emp.getEmail()));
        return RefreshTokenResponse.builder()
                .accessToken(token)
                .refreshToken(request.getRefreshToken())
                .tokenType("BEARER")
                .build();
    }

    @Override
    public ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenName, token)
                .path("/")
                .maxAge(refreshExpiration/1000) // 15 days in seconds
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    @Override
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenName);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return "";
        }
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenName, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)
                .build();
    }
}
