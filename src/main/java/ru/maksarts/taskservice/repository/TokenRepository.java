package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByEmployee(Employee employee);
    Optional<Token> findByToken(String token);
}
