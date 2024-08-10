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
import ru.maksarts.taskservice.model.Comment;
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
            description = "Returns all tasks by specified author, paginated, sorted by created timestamp desc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTaskByAuthor/{email}/{page}")
    public ResponseEntity<List<Task>> getTaskByAuthor(@PathVariable(name = "email") String email,
                                                      @PathVariable(name = "page") Integer page,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        List<Task> task = taskService.getTaskByAuthor(email, page, pageSize);
        if (task != null && !task.isEmpty()) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }






    @Operation(
            summary = "Get tasks of executor",
            description = "Returns all tasks by specified executor, paginated, sorted by created timestamp desc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getTaskByExecutor/{email}/{page}")
    public ResponseEntity<List<Task>> getTaskByExecutor(@PathVariable(name = "email") String email,
                                                        @PathVariable(name = "page") Integer page,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        List<Task> task = taskService.getTaskByExecutor(email, page, pageSize);
        if (task != null && !task.isEmpty()) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
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






    @Operation(
            summary = "Create new comment",
            description = "Creates new comment under specified task by authorised author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @PostMapping(value = "/createComment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDto commentDto,
                                            @RequestHeader("Authorization") String authHeader) {

        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of task by his JWT token
        Comment comment = commentService.createComment(commentDto, author);
        if (comment != null) {
            log.info("Created comment: id={} by user={}, task={}", comment.getId(), comment.getAuthorEmail().getEmail(), comment.getTaskId());
            return ResponseEntity.ok(comment);

        } else {
            log.error("Cannot create commen by user={}, task={}", author.getEmail(), commentDto.getTaskId());
            return ResponseEntity.internalServerError().build();
        }
    }




    @Operation(
            summary = "Get comments of author",
            description = "Returns all comments by specified author, paginated, sorted by created timestamp desc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getCommentsByAuthor/{email}/{page}")
    public ResponseEntity<List<Comment>> getCommentsByAuthor(@PathVariable(name = "email") String email,
                                                             @PathVariable(name = "page") Integer page,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        List<Comment> comments = commentService.getCommentByAuthor(email, page, pageSize);
        if (comments != null && !comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }





    @Operation(
            summary = "Get comments by task ID",
            description = "Returns all comments under specified taskId, paginated, sorted by created timestamp desc"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = List.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @GetMapping(value = "/getCommentsByTask/{taskId}/{page}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable(name = "taskId") Long taskId,
                                                           @PathVariable(name = "page") Integer page,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        List<Comment> comments = commentService.getCommentByTaskId(taskId, page, pageSize);
        if (comments != null && !comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @Operation(
            summary = "Delete comment",
            description = "Deletes comment in TaskService by specified id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "403", content = { @Content(schema = @Schema(implementation = BasicResponse.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "401", content = { @Content(schema = @Schema()) })
    })
    @DeleteMapping(value = "/deleteComment")
    public ResponseEntity<BasicResponse> deleteComment(@RequestParam(name = "id") Long id,
                                                       @RequestHeader("Authorization") String authHeader){
        Employee author = tokenService.getEmployeeByAuthHeader(authHeader); // get author of request by his JWT token
        Comment commentToDelete = commentService.getCommentById(id);
        if(commentToDelete.getAuthorEmail().getEmail().equals(author.getEmail())){
            commentService.deleteComment(id);
            return ResponseEntity.ok(
                    BasicResponse.builder().content(String.format("Comment with id=%s was deleted", id)).build()
            );
        } else {
            return new ResponseEntity<>(
                    BasicResponse.builder()
                            .errMsg("Access denied")
                            .errDesc("You can delete only your comments")
                            .build(),
                    HttpStatus.FORBIDDEN);
        }

    }

}
