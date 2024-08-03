package ru.maksarts.taskservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.service.CommentService;
import ru.maksarts.taskservice.service.EmployeeService;
import ru.maksarts.taskservice.service.TaskService;

@Slf4j
@RestController("/taskservice/api/v1")
@Tag(name = "TaskService", description = "Managing tasks service")
public class MainController {

    private final EmployeeService employeeService;
    private final TaskService taskService;
    private final CommentService commentService;

    @Autowired
    public MainController(EmployeeService employeeService,
                          TaskService taskService,
                          CommentService commentService){
        this.employeeService = employeeService;
        this.taskService = taskService;
        this.commentService = commentService;
    }

    @PostMapping("/auth")
    public void auth(){

    }

    @Operation(
            summary = "Get a task by Id",
            description = "Get a Task object by specifying its id.",
            tags = { "task", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Task.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })
    })
    @GetMapping("/getTask")
    public void getTask(){

    }

    @PostMapping("/createTask")
    public void createTask(){

    }
    @DeleteMapping("/deleteTask")
    public void deleteTask(){

    }

    @PutMapping("/updateTask")
    public void updateTask(){

    }

}
