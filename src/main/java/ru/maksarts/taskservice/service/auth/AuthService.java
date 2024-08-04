package ru.maksarts.taskservice.service.auth;

import ru.maksarts.taskservice.model.dto.LoginRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
}
