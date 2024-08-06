package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getTaskByAuthorEmail(Employee employee);
    List<Task> getTaskByExecutorEmail(Employee employee);
    Boolean existsByTitle(String title);
}
