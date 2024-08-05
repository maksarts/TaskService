package ru.maksarts.taskservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Структура запроса на добавление новой задачи в TaskService")
public class TaskDto {
    @Schema(description = "Заголовок задачи")
    @NotNull
    private String title;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Приоритет задачи (0 - по умолчанию, чем выше значение, тем выше приоритет)")
    @Min(0)
    private Integer priority;

    @Schema(description = "Исполнитель задачи")
    @Email
    private String executor_email;
}
