package ru.maksarts.taskservice.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private Employee author_id;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false)
    private Task task_id;

    @Column(name = "content", nullable = false)
    private String content;
}
