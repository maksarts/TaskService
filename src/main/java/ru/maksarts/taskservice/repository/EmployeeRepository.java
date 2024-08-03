package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
