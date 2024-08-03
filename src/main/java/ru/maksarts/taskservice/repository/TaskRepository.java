package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
