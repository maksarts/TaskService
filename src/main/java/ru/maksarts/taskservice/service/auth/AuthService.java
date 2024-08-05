package ru.maksarts.taskservice.service.auth;

import ru.maksarts.taskservice.model.dto.LoginRequest;
import ru.maksarts.taskservice.model.dto.LoginResponse;
import ru.maksarts.taskservice.model.dto.RegisterRequest;

public interface AuthService {
    LoginResponse register(RegisterRequest request);
    LoginResponse authenticate(LoginRequest request);
}
