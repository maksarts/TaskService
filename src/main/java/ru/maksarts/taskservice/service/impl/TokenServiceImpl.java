package ru.maksarts.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Token;
import ru.maksarts.taskservice.repository.TokenRepository;
import ru.maksarts.taskservice.service.TokenService;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    public Employee getEmployeeByToken(String token){
        Token tokenEntity = tokenRepository.findByToken(token).orElseThrow(() -> new NotFoundException("User with this token not found"));
        return tokenEntity.getEmployee();
    }

    public Employee getEmployeeByAuthHeader(String header){
        if (header.startsWith("Bearer ")) {
            return getEmployeeByToken(header.substring(7));
        }
        return null;
    }
}
