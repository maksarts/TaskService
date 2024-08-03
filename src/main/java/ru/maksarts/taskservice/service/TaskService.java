package ru.maksarts.taskservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.repository.TaskRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TaskService(TaskRepository repository){
        this.taskRepository = repository;
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


    public Task updateTask(@NonNull Task task) {
        return taskRepository.save(task);
    }


    public void deleteTask(@NonNull Long id) {
        taskRepository.deleteById(id);
    }
}
