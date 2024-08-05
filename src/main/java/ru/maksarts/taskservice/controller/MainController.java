package ru.maksarts.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.dto.LoginRequest;
import ru.maksarts.taskservice.model.dto.LoginResponse;
import ru.maksarts.taskservice.model.dto.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.service.TokenService;
import ru.maksarts.taskservice.service.auth.AuthService;
import ru.maksarts.taskservice.service.CommentService;
import ru.maksarts.taskservice.service.TaskService;

@Slf4j
@RestController
@RequestMapping("/taskservice/api/v1")
@RequiredArgsConstructor
@Tag(name = "TaskService", description = "Managing tasks service")
public class MainController {
    private final TaskService taskService;
    private final CommentService commentService;
    private final TokenService tokenService;

    @Operation(
            summary = "Get a task by Id",
            description = "Get a Task object by specifying its id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTask")
    public void getTask(){

    }



    @Operation(
            summary = "Create new task",
            description = "Creating new task in TaskService with specified author and priority"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })
    })
    @PostMapping(value = "/createTask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTask(@RequestBody @Valid TaskDto taskDto,
                                             @RequestHeader("Authorization") String authHeader,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().toString()); // TODO нормальное тело ошибки
        }

        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of task by his JWT token
        Task task = taskService.createTask(taskDto, author);
        if (task != null) {
            log.info("Created task {} by user {}", task.getAuthor_email(), task.getTitle());
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.internalServerError().build(); // TODO создать body и описание ошибки
        }
    }

    @DeleteMapping(value = "/deleteTask")
    public void deleteTask(){

    }

    @PutMapping(value = "/updateTask")
    public void updateTask(){

    }

}
