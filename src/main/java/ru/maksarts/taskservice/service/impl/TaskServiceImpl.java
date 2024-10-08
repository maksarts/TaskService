package ru.maksarts.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.model.TaskStatus;
import ru.maksarts.taskservice.model.dto.EditTaskDto;
import ru.maksarts.taskservice.model.dto.TaskDto;
import ru.maksarts.taskservice.repository.CommentRepository;
import ru.maksarts.taskservice.repository.TaskRepository;
import ru.maksarts.taskservice.service.EmployeeService;
import ru.maksarts.taskservice.service.TaskService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    private final EmployeeService employeeService;
    private final CommentRepository commentRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(@NonNull Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public List<Task> getTaskByAuthor(@NonNull String authorEmail, Integer page, Integer pageSize){
        Employee employee = employeeService.getEmployeeByEmail(authorEmail);
        return taskRepository.getTaskByAuthorEmail(
                employee,
                PageRequest.of(page, pageSize, Sort.by("createdTs").descending()));
    }

    public List<Task> getTaskByExecutor(@NonNull String executorEmail, Integer page, Integer pageSize){
        Employee employee = employeeService.getEmployeeByEmail(executorEmail);
        return taskRepository.getTaskByExecutorEmail(
                employee,
                PageRequest.of(page, pageSize, Sort.by("createdTs").descending()));
    }


    public Task createTask(@NonNull Task task) {
        if(taskRepository.existsByTitle(task.getTitle())){
            throw new ClientSideErrorException(String.format("Task with title %s alredy exists", task.getTitle()));
        }
        return taskRepository.save(task);
    }


    public Task createTask(@NonNull TaskDto taskDto, @NonNull Employee emp) {
        Task newTask = new Task();

        newTask.setTitle(taskDto.getTitle());
        newTask.setDescription(taskDto.getDescription());

        if(taskDto.getPriority() != null){
            newTask.setPriority(taskDto.getPriority());
        } else{
            newTask.setPriority(0); // default priority
        }

        newTask.setAuthorEmail(emp);
        if(taskDto.getExecutorEmail() != null){
            newTask.setExecutorEmail(employeeService.getEmployeeByEmail(taskDto.getExecutorEmail()));
        }

        newTask.setTaskStatus(TaskStatus.OPEN);

        return createTask(newTask);
    }


    public Task updateTaskStatus(@NonNull Task taskToEdit, @NonNull String status){
        taskToEdit.setTaskStatusValue(null); // need to make JPA call @PreUpdate methods
        return updateTask(taskToEdit,
                EditTaskDto.builder()
                        .id(taskToEdit.getId())
                        .taskStatus(status)
                        .build());
    }


    public Task updateTask(@NonNull Task taskToEdit, @NonNull EditTaskDto editTaskDto) {
        if(editTaskDto.getDescription() != null) taskToEdit.setDescription(editTaskDto.getDescription());
        if(editTaskDto.getPriority() != null) taskToEdit.setPriority(editTaskDto.getPriority());
        if(editTaskDto.getTitle() != null && !editTaskDto.getTitle().isBlank()) taskToEdit.setTitle(editTaskDto.getTitle());
        if(editTaskDto.getExecutorEmail() != null){
            if(!editTaskDto.getExecutorEmail().isBlank()) {
                Employee executor = employeeService.getEmployeeByEmail(editTaskDto.getExecutorEmail());
                if (executor == null) {
                    throw new ClientSideErrorException(String.format("User [%s] not found", editTaskDto.getExecutorEmail()));
                }
                taskToEdit.setExecutorEmail(executor);
            } else {
                taskToEdit.setExecutorEmail(null);
            }
        }
        if(editTaskDto.getTaskStatus() != null && !editTaskDto.getTaskStatus().isBlank()){
            taskToEdit.setTaskStatusValue(null); // need to make JPA call @PreUpdate methods
            TaskStatus taskStatus = TaskStatus.of(editTaskDto.getTaskStatus());
            taskToEdit.setTaskStatus(taskStatus);
        }
        return taskRepository.save(taskToEdit);
    }

    @Transactional
    @Modifying
    public void deleteTask(@NonNull Long id) {
        Task taskToDelete = this.getTaskById(id);
        commentRepository.deleteAllByTaskId(taskToDelete); // delete all child comments
        taskRepository.deleteById(id);
    }
}
