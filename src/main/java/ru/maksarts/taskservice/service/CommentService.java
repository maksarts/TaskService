package ru.maksarts.taskservice.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CommentService(CommentRepository repository){
        this.commentRepository = repository;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(@NonNull Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getCommentByAuthor(@NonNull Long authorId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> criteriaQuery = criteriaBuilder.createQuery(Comment.class);
        Root<Comment> root = criteriaQuery.from(Comment.class);
        criteriaQuery.select(root);

        ParameterExpression<Long> params = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("author_id"), params));

        TypedQuery<Comment> query = entityManager.createQuery(criteriaQuery);
        query.setParameter(params, authorId);

        return query.getResultList();
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
