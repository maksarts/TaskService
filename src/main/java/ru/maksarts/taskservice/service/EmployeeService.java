package ru.maksarts.taskservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.repository.EmployeeRepository;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository repository){
        this.employeeRepository = repository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


    public Employee getEmployeeById(@NonNull Long id) {
        return employeeRepository.findById(id).orElse(null);
    }


    public Employee createEmployee(@NonNull Employee employee) {
        return employeeRepository.save(employee);
    }


    public Employee updateEmployee(@NonNull Employee employee) {
        return employeeRepository.save(employee);
    }


    public void deleteEmployee(@NonNull Long id) {
        employeeRepository.deleteById(id);
    }
}
