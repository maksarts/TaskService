package ru.maksarts.taskservice.service;

import org.springframework.lang.NonNull;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.dto.CommentDto;

import java.util.List;

public interface CommentService {
    Comment getCommentById(@NonNull Long id);
    List<Comment> getCommentByAuthor(@NonNull String authorEmail, Integer page, Integer pageSize);
    List<Comment> getCommentByTaskId(@NonNull Long taskId, Integer page, Integer pageSize);
    Comment createComment(@NonNull CommentDto commentDto, @NonNull Employee author);
    void deleteComment(@NonNull Long id);
    Comment updateComment(@NonNull Comment comment);
}
