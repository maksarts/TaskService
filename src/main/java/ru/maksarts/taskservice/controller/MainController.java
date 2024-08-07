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
import ru.maksarts.taskservice.model.dto.*;
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
            description = "Returns a Task object by specified id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
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
            description = "Returns all tasks by specified author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
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
            summary = "Get tasks of executor",
            description = "Returns all tasks by specified executor"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTaskByExecutor")
    public ResponseEntity<List<Task>> getTaskByExecutor(@RequestParam(name = "email") String email){
        try {
            List<Task> task = taskService.getTaskByExecutor(email);
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
            description = "Creates new task in TaskService with specified author and priority"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @PostMapping(value = "/createTask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDto taskDto,
                                                    @RequestHeader("Authorization") String authHeader) {

        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of task by his JWT token
        Task task = taskService.createTask(taskDto, author);
        if (task != null) {
            log.info("Created task {} by user {}", task.getTitle(), task.getAuthorEmail().getEmail());
            return ResponseEntity.ok(task);

        } else {
            log.error("Cannot create new task: task=null, author={}, task={}", author.getEmail(), taskDto.getTitle());
            return ResponseEntity.internalServerError().build();
        }
    }




    @Operation(
            summary = "Update task",
            description = "Edits task in TaskService"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema(implementation = BasicResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @PutMapping(value = "/updateTask")
    public ResponseEntity<?> updateTask(@Valid @RequestBody EditTaskDto editTaskDto,
                                                    @RequestHeader("Authorization") String authHeader){
        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of request by his JWT token
        Task taskToEdit = taskService.getTaskById(editTaskDto.getId());
        if(taskToEdit == null){
            log.info("Task with id={} not found", editTaskDto.getId());
            return ResponseEntity.notFound().build();
        }
        if(taskToEdit.getAuthorEmail().getEmail().equals(author.getEmail())){
            taskToEdit = taskService.updateTask(taskToEdit, editTaskDto);
            return ResponseEntity.ok(taskToEdit);
        } else {
            return new ResponseEntity<>(
                    BasicResponse.builder()
                            .errMsg("Access denied")
                            .errDesc("You can edit only your tasks")
                            .build(),
                    HttpStatus.FORBIDDEN);
        }
    }






    @Operation(
            summary = "Update status of task",
            description = "Changes task status in TaskService"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema(implementation = BasicResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @PutMapping(value = "/updateTaskStatus")
    public ResponseEntity<?> updateTaskStatus(@RequestParam(name = "id") Long id,
                                              @RequestParam(name = "status") String status,
                                              @RequestHeader("Authorization") String authHeader){
        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of request by his JWT token
        Task taskToEdit = taskService.getTaskById(id);
        if(taskToEdit == null){
            log.info("Task with id={} not found", id);
            return ResponseEntity.notFound().build();
        }
        if(taskToEdit.getAuthorEmail().getEmail().equals(author.getEmail()) ||
                taskToEdit.getExecutorEmail() != null && taskToEdit.getExecutorEmail().getEmail().equals(author.getEmail())){

            taskToEdit = taskService.updateTaskStatus(taskToEdit, status);
            return ResponseEntity.ok(taskToEdit);

        } else {
            return new ResponseEntity<>(
                    BasicResponse.builder()
                            .errMsg("Access denied")
                            .errDesc("You can change status only if you are author or executor")
                            .build(),
                    HttpStatus.FORBIDDEN);
        }
    }






    @Operation(
            summary = "Delete task",
            description = "Deletes task in TaskService by specified id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema(implementation = BasicResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping(value = "/deleteTask")
    public ResponseEntity<BasicResponse> deleteTask(@RequestParam(name = "id") Long id,
                           @RequestHeader("Authorization") String authHeader){
        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of request by his JWT token
        Task taskToDelete = taskService.getTaskById(id);
        if(taskToDelete == null){
            log.info("Task with id={} not found", id);
            return ResponseEntity.notFound().build();
        }
        if(taskToDelete.getAuthorEmail().getEmail().equals(author.getEmail())){
            taskService.deleteTask(id);
            return ResponseEntity.ok(
                    BasicResponse.builder().content(String.format("Task with id=%s was deleted", id)).build()
            );
        } else {
            return new ResponseEntity<>(
                    BasicResponse.builder()
                            .errMsg("Access denied")
                            .errDesc("You can delete only your tasks")
                            .build(),
                    HttpStatus.FORBIDDEN);
        }

    }

}
