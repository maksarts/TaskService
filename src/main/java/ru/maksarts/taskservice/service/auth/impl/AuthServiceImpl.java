package ru.maksarts.taskservice.service.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.RefreshToken;
import ru.maksarts.taskservice.model.Token;
import ru.maksarts.taskservice.model.dto.LoginRequest;
import ru.maksarts.taskservice.model.dto.LoginResponse;
import ru.maksarts.taskservice.model.dto.RegisterRequest;
import ru.maksarts.taskservice.repository.TokenRepository;
import ru.maksarts.taskservice.service.EmployeeService;
import ru.maksarts.taskservice.service.auth.AuthService;
import ru.maksarts.taskservice.service.auth.RefreshTokenService;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final EmployeeUserDetailsService employeeUserDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;


    @Override
    public LoginResponse register(RegisterRequest request) {
        Employee emp = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        emp = employeeService.createEmployee(emp);
        UserDetails user = employeeUserDetailsService.createUserFromEmployee(emp);
        String jwt = jwtService.generateToken(user);
        saveToken(emp, jwt);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(emp.getEmail());

//        var roles = user.getRole().getAuthorities()
//                .stream()
//                .map(SimpleGrantedAuthority::getAuthority)
//                .toList();

        return LoginResponse.builder()
                .token(jwt)
                .email(emp.getEmail())
                .refreshToken(refreshToken.getToken())
                .tokenType("BEARER")
                .build();
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        Employee emp = employeeService.getEmployeeByEmail(request.getEmail());
        UserDetails user = employeeUserDetailsService.loadUserByUsername(request.getEmail());
//        var roles = user.getRole().getAuthorities()
//                .stream()
//                .map(SimpleGrantedAuthority::getAuthority)
//                .toList();
        String jwt = jwtService.generateToken(user);
        saveToken(emp, jwt);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(emp.getEmail());
        return LoginResponse.builder()
                .token(jwt)
                .email(emp.getEmail())
                .refreshToken(refreshToken.getToken())
                .tokenType("BEARER")
                .build();
    }

    // save token to DB to be able to identify employee by his token
    private void saveToken(Employee emp, String token){
        Token tokenEntity = tokenRepository.findByEmployee(emp).orElse(new Token());
        tokenEntity.setEmployee(emp);
        tokenEntity.setToken(token);
        tokenRepository.save(tokenEntity);
    }
}
