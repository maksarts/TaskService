package ru.maksarts.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.model.dto.CommentDto;
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
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    public List<Comment> getCommentByAuthor(@NonNull String authorEmail, Integer page, Integer pageSize) {
        Employee employee = employeeService.getEmployeeByEmail(authorEmail);
        return commentRepository.getCommentByAuthorEmail(
                employee,
                PageRequest.of(page, pageSize, Sort.by("createdTs").descending()));
    }

    public List<Comment> getCommentByTaskId(@NonNull Long taskId, Integer page, Integer pageSize) {
       Task task = taskService.getTaskById(taskId);
       return commentRepository.getCommentByTaskId(
               task,
               PageRequest.of(page, pageSize, Sort.by("createdTs").descending()));
    }


    public Comment createComment(@NonNull CommentDto commentDto, @NonNull Employee author) {
        Task task = taskService.getTaskById(commentDto.getTaskId());
        Comment comment = Comment.builder()
                .authorEmail(author)
                .taskId(task)
                .content(commentDto.getContent())
                .build();
        return commentRepository.save(comment);
    }


    public Comment updateComment(@NonNull Comment comment) {
        return commentRepository.save(comment);
    }


    public void deleteComment(@NonNull Long id) {
        commentRepository.deleteById(id);
    }

}
