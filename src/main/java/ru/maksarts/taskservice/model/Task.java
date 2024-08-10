package ru.maksarts.taskservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Schema(description = "Задача в менеджере TaskService")
@Entity
@Table(name = "task")
@Data
public class Task {
    @Schema(description = "ID задачи")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Schema(description = "Заголовок задачи")
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Schema(description = "Описание задачи")
    @Column(name = "description")
    private String description;

    @Schema(description = "Приоритет задачи, чем выше значение - тем выше приориет")
    @Column(name = "priority", nullable = false)
    @Min(0)
    private Integer priority;

    @Schema(description = "Автор задачи")
    @ManyToOne
    @JoinColumn(name = "author_email", referencedColumnName = "email", nullable = false, updatable = false)
    private Employee authorEmail;

    @Schema(description = "Исполнитель задачи")
    @ManyToOne
    @JoinColumn(name = "executor_email", referencedColumnName = "email")
    private Employee executorEmail;

    @Schema(description = "Дата создания")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_ts", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTs;

    @JsonIgnore
    @Basic(optional = false)
    @Column(name = "task_status", nullable = false)
    private Integer taskStatusValue; // needed to correct representating ENUM values in DB

    @Schema(description = "Статус задачи")
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
