package ru.maksarts.taskservice.service;

import ru.maksarts.taskservice.model.Employee;

public interface TokenService {
    Employee getEmployeeByToken(String token);
    Employee getEmployeeByAuthHeader(String header);
}
