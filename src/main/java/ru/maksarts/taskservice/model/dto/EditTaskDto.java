package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.maksarts.taskservice.model.TaskStatus;

@Data
@Schema(description = "Request of edit task")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditTaskDto {
    @Schema(description = "ID of the task")
    @NotNull(message = "ID is mandatory")
    private Long id;

    @Schema(description = "Task title")
    private String title;

    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Priority of task (0 - by default, higher priority -> higher value)")
    @Min(0)
    private Integer priority;

    @Schema(description = "Executor of the task")
    @Email
    private String executorEmail;

    @Schema(description = "Status of the task")
    private String taskStatus;
}
