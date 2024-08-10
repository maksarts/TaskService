package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request of create task")
public class TaskDto {
    @Schema(description = "Task title")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Priority of task (0 - by default, higher priority -> higher value)")
    @Min(0)
    private Integer priority;

    @Schema(description = "Executor of the task")
    @Email
    private String executorEmail;
}
