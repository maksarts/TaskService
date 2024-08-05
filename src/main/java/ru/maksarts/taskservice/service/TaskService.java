package ru.maksarts.taskservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
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

    @PersistenceContext
    private EntityManager entityManager;

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

    public List<Task> getTaskByAuthor(@NonNull Long authorId){
        return getTaskByEmployee(authorId, "author_id");
    }

    public List<Task> getTaskByExecutor(@NonNull Long authorId){
        return getTaskByEmployee(authorId, "executor_id");
    }

    private List<Task> getTaskByEmployee(@NonNull Long employeeId, @NonNull String field) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = criteriaBuilder.createQuery(Task.class);
        Root<Task> root = criteriaQuery.from(Task.class);
        criteriaQuery.select(root);

        ParameterExpression<Long> params = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get(field), params));

        TypedQuery<Task> query = entityManager.createQuery(criteriaQuery);
        query.setParameter(params, employeeId);

        return query.getResultList();
    }


    public Task createTask(@NonNull Task task) {
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

        newTask.setAuthor_email(emp);
        if(taskDto.getExecutor_email() != null){
            newTask.setExecutor_email(employeeService.getEmployeeByEmail(taskDto.getExecutor_email()));
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
