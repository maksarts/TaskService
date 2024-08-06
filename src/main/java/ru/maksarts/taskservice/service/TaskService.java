package ru.maksarts.taskservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.Employee;
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
        return taskRepository.findById(id).orElse(null);
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
        if(taskDto.getExecutor_email() != null){
            newTask.setExecutorEmail(employeeService.getEmployeeByEmail(taskDto.getExecutor_email()));
        }

        newTask.setTaskStatus(TaskStatus.OPEN);

        return createTask(newTask);
    }

    public Task updateTask(@NonNull Task task) {
        return taskRepository.save(task);
    }


    public void deleteTask(@NonNull Long id) {
        taskRepository.deleteById(id);
    }
}
