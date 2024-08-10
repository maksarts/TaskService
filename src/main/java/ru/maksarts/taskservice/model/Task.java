package ru.maksarts.taskservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "task")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "priority", nullable = false)
    @Min(0)
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "author_email", referencedColumnName = "email", nullable = false, updatable = false)
    private Employee authorEmail;

    @ManyToOne
    @JoinColumn(name = "executor_email", referencedColumnName = "email")
    private Employee executorEmail;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_ts", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTs;

    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "task_status", nullable = false)
    private Integer taskStatusValue; // needed to correct representating ENUM values in DB

    @Transient
    private TaskStatus taskStatus;

    @PostLoad
    void fillTransient() {
        if (taskStatusValue > 0) {
            this.taskStatus = TaskStatus.of(taskStatusValue);
        }
    }

    @PrePersist
    @PreUpdate
    void fillPersistent() {
        if (taskStatus != null) {
            this.taskStatusValue = taskStatus.getStatus();
        }
    }
}
