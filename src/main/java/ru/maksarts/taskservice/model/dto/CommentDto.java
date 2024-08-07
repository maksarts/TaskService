package ru.maksarts.taskservice.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.Task;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotNull
    private Long taskId;
    @NotBlank
    private String content;
}
