package ru.maksarts.taskservice.service;

import org.hibernate.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.maksarts.taskservice.model.Comment;
import ru.maksarts.taskservice.repository.CommentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
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
