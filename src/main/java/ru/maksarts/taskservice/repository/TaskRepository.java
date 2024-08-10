package ru.maksarts.taskservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> getTaskByAuthorEmail(Employee employee, Pageable pageable);
    List<Task> getTaskByExecutorEmail(Employee employee, Pageable pageable);
    Boolean existsByTitle(String title);
}
