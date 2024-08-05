package ru.maksarts.taskservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EmployeeService employeeService;
    private final TaskService taskService;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(@NonNull Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getCommentByAuthor(@NonNull String authorEmail) {
        Employee employee = employeeService.getEmployeeByEmail(authorEmail);
        return commentRepository.getCommentByAuthorEmail(employee);
    }

    public List<Comment> getCommentByTaskId(@NonNull Long taskId) {
       Task task = taskService.getTaskById(taskId);
       return commentRepository.getCommentByTaskId(task);
    }


    public Comment createComment(@NonNull Comment comment) {
        return commentRepository.save(comment);
    }


    public Comment updateComment(@NonNull Comment comment) {
        return commentRepository.save(comment);
    }


    public void deleteComment(@NonNull Long id) {
        commentRepository.deleteById(id);
    }
}
