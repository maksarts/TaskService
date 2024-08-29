package ru.maksarts.taskservice.service;

import org.springframework.lang.NonNull;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.model.dto.EditTaskDto;
import ru.maksarts.taskservice.model.dto.TaskDto;

import java.util.List;

public interface TaskService {
    Task getTaskById(@NonNull Long id);
    List<Task> getTaskByAuthor(@NonNull String authorEmail, Integer page, Integer pageSize);
    List<Task> getTaskByExecutor(@NonNull String executorEmail, Integer page, Integer pageSize);
    Task createTask(@NonNull Task task);
    Task createTask(@NonNull TaskDto taskDto, @NonNull Employee emp);
    Task updateTaskStatus(@NonNull Task taskToEdit, @NonNull String status);
    Task updateTask(@NonNull Task taskToEdit, @NonNull EditTaskDto editTaskDto);
    void deleteTask(@NonNull Long id);
}
