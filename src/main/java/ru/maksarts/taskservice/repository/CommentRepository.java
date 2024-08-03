package ru.maksarts.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maksarts.taskservice.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
