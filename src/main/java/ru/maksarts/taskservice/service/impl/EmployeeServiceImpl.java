package ru.maksarts.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.repository.EmployeeRepository;
import ru.maksarts.taskservice.service.EmployeeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;


    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeByEmail(@NonNull String email) {
        return employeeRepository.findById(email).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public Employee createEmployee(@NonNull Employee employee) {
        return employeeRepository.save(employee);
    }


    public Employee updateEmployee(@NonNull Employee employee) {
        return employeeRepository.save(employee);
    }


    public void deleteEmployee(@NonNull String id) {
        employeeRepository.deleteById(id);
    }
}
