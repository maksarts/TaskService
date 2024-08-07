package ru.maksarts.taskservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.dto.EditTaskDto;
import ru.maksarts.taskservice.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.model.TaskStatus;
import ru.maksarts.taskservice.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    private final EmployeeService employeeService;

    @Autowired
    public TaskService(TaskRepository repository, EmployeeService employeeService){
        this.taskRepository = repository;
        this.employeeService = employeeService;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(@NonNull Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public List<Task> getTaskByAuthor(@NonNull String authorEmail){
        Employee employee = employeeService.getEmployeeByEmail(authorEmail);
        return taskRepository.getTaskByAuthorEmail(employee).stream().toList();
    }

    public List<Task> getTaskByExecutor(@NonNull String executorEmail){
        Employee employee = employeeService.getEmployeeByEmail(executorEmail);
        return taskRepository.getTaskByExecutorEmail(employee).stream().toList();
    }


    public Task createTask(@NonNull Task task) {
        if(taskRepository.existsByTitle(task.getTitle())){
            throw new ClientSideErrorException(String.format("Task with title %s alredy exists", task.getTitle()));
        }
        return taskRepository.save(task);
    }

    @Transactional
    @Modifying
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


    @Transactional
    @Modifying
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
        taskRepository.deleteById(id);
    }
}
