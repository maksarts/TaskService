package ru.maksarts.taskservice.service;

import org.springframework.lang.NonNull;
import ru.maksarts.taskservice.model.Employee;

public interface EmployeeService {
    Employee getEmployeeByEmail(@NonNull String email);
    Employee createEmployee(@NonNull Employee employee);
    Employee updateEmployee(@NonNull Employee employee);
    void deleteEmployee(@NonNull String id);
}
