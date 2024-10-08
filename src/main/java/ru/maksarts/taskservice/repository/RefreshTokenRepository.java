package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByEmployee(Employee employee);
}
