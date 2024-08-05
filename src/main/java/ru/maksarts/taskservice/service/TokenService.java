package ru.maksarts.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Token;
import ru.maksarts.taskservice.repository.TokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Employee getEmployeeByToken(String token){
        Token tokenEntity = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("User with this token not found"));
        return tokenEntity.getEmployee();
    }

    public Employee getEmployeeByAuthHeader(String header){
        if (header.startsWith("Bearer ")) {
            return getEmployeeByToken(header.substring(7));
        }
        return null;
    }
}
