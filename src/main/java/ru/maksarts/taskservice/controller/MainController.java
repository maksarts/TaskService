package ru.maksarts.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.webjars.NotFoundException;
import ru.maksarts.taskservice.exception.ClientSideErrorException;
import ru.maksarts.taskservice.model.Employee;
import ru.maksarts.taskservice.model.dto.BasicResponse;
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

import java.util.List;

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
            description = "Return a Task object by specified id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTask")
    public ResponseEntity<Task> getTask(@RequestParam(name = "id") Long id){
        Task task = taskService.getTaskById(id);
        if(task != null){
            return ResponseEntity.ok(task);
        } else{
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Get tasks of author",
            description = "Return all tasks by specified author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTaskByAuthor")
    public ResponseEntity<List<Task>> getTaskByAuthor(@RequestParam(name = "email") String email){
        try {
            List<Task> task = taskService.getTaskByAuthor(email);
            if (task != null && !task.isEmpty()) {
                return ResponseEntity.ok(task);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NotFoundException notFound){
            log.warn("NotFoundException while getting tasks by email: {}", notFound.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("Exception while getting tasks by email: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
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
    public ResponseEntity<BasicResponse> createTask(@Valid @RequestBody TaskDto taskDto,
                                                    @RequestHeader("Authorization") String authHeader) {

        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of task by his JWT token
        Task task = taskService.createTask(taskDto, author);
        if (task != null) {
            log.info("Created task {} by user {}", task.getTitle(), task.getAuthorEmail().getEmail());
            return ResponseEntity.ok(
                        BasicResponse.builder().content(task).build()
                    );

        } else {
            log.error("Cannot create new task: task=null, author={}, task={}", author.getEmail(), taskDto.getTitle());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(value = "/deleteTask")
    public void deleteTask(){

    }

    @PutMapping(value = "/updateTask")
    public void updateTask(){

    }

}
