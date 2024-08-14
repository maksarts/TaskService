package ru.maksarts.taskservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> getCommentByAuthorEmail(Employee author, Pageable pageable);
    List<Comment> getCommentByTaskId(Task task, Pageable pageable);
    void deleteAllByTaskId(Task task);
}
