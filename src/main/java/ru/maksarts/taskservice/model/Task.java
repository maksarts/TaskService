package ru.maksarts.taskservice.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.*;

@Entity
@Table(name = "task")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    private Employee author_id;

    @ManyToOne
    @JoinColumn(name = "executor_id", referencedColumnName = "id")
    private Employee executor_id;

    @Basic
    @Column(name = "task_status", nullable = false)
    private Integer taskStatusValue;

    @Transient
    private TaskStatus taskStatus;

    @PostLoad
    void fillTransient() {
        if (taskStatusValue > 0) {
            this.taskStatus = TaskStatus.of(taskStatusValue);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (taskStatus != null) {
            this.taskStatusValue = taskStatus.getStatus();
        }
    }
}
