package ru.maksarts.taskservice.model;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_email", referencedColumnName = "email", nullable = false)
    private Employee author_email;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private Task task_id;

    @Column(name = "content", nullable = false)
    private String content;
}
