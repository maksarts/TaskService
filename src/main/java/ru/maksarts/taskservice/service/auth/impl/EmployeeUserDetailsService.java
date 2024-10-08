package ru.maksarts.taskservice.service.auth.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.service.EmployeeService;
import ru.maksarts.taskservice.service.impl.EmployeeServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeUserDetailsService implements UserDetailsService {

    private final EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee emp = employeeService.getEmployeeByEmail(email);
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        return User.builder()
            .username(emp.getEmail())
            .password(emp.getPassword())
            .roles(roles.toArray(new String[0]))
            .build();
    }

    public UserDetails createUserFromEmployee(Employee emp){
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        return User.builder()
                .username(emp.getEmail())
                .password(emp.getPassword())
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
