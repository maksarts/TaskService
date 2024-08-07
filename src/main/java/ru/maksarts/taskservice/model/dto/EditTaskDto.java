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
@Schema(description = "Структура запроса на изменение задачи в TaskService")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditTaskDto {
    @Schema(description = "ID задачи")
    @NotNull(message = "ID is mandatory")
    private Long id;

    @Schema(description = "Заголовок задачи")
    private String title;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Приоритет задачи (0 - по умолчанию, чем выше значение, тем выше приоритет)")
    @Min(0)
    private Integer priority;

    @Schema(description = "Исполнитель задачи")
    @Email
    private String executorEmail;

    @Schema(description = "Статус задачи")
    private String taskStatus;
}
