package ru.maksarts.taskservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.service.EmployeeService;

@TestConfiguration
public class TestConfig {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Bean
    public EmployeeService employeeService(){
        return new EmployeeService(null){
            @Override
            public Employee getEmployeeByEmail(@NonNull String email) {
                return Employee.builder()
                        .email(email)
                        .password(encoder.encode("testpassword"))
                        .build();
            }
        };
    }
}
