package ru.maksarts.taskservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.model.dto.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@AutoConfigureOrder
public class TaskServiceIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(0)
    public void testUnauthorized() {
        ResponseEntity<Task> response = restTemplate
                .getForEntity("/taskservice/api/v1/getTask?id=1", Task.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(1)
    public void testRegister() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("testpassword")
                .name("TestUser")
                .build();

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/taskservice/api/v1/auth/register",
                new HttpEntity<>(registerRequest),
                LoginResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getEmail());
        assertNotNull(response.getBody().getToken());
        assertEquals("test@gmail.com", response.getBody().getEmail());
    }

    @Test
    @Order(4)
    public void testWrongToken() {
        String wrongToken = "wrong-token";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(wrongToken);
        headers.set("Accept-Encoding", "gzip, deflate, br");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        ResponseEntity<Task> response = restTemplate
                .exchange("/taskservice/api/v1/getTask?id=1",
                        HttpMethod.GET,
                        request,
                        Task.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void testValidateRequest() {
        String token = auth();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");

        // title should be NotBlank
        TaskDto body = new TaskDto(null, "Description", 10, null);

        HttpEntity<TaskDto> request = new HttpEntity<>(body, headers);
        ResponseEntity<Task> response = restTemplate.postForEntity(
                "/taskservice/api/v1/createTask",
                request,
                Task.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testCreateTask() {
        String token = auth();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");

        TaskDto body = new TaskDto("TestTitle", "TestDescription", 10, null);

        HttpEntity<TaskDto> request = new HttpEntity<>(body, headers);
        ResponseEntity<Task> response = restTemplate.postForEntity(
                "/taskservice/api/v1/createTask",
                request,
                Task.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@gmail.com", response.getBody().getAuthorEmail().getEmail());
        assertEquals("TestTitle", response.getBody().getTitle());
    }

    @Test
    @Order(7)
    public void testGetTaskByAuthor() throws JsonProcessingException {
        String token = auth();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");


        HttpEntity<?> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/taskservice/api/v1/getTaskByAuthor/test@gmail.com/0",
                        HttpMethod.GET,
                        request,
                        String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        List<Task> body = mapper.readValue(response.getBody(), new TypeReference<List<Task>>(){});
        Task task = body.get(0);

        assertEquals("test@gmail.com", task.getAuthorEmail().getEmail());
    }


    @Test
    @Order(6)
    public void testCreateTaskWithSameTitle() {
        String token = auth();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");

        TaskDto body = new TaskDto("TestTitle", "TestDescription", 10, null);

        HttpEntity<TaskDto> request = new HttpEntity<>(body, headers);
        ResponseEntity<Task> response = restTemplate.postForEntity(
                "/taskservice/api/v1/createTask",
                request,
                Task.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    @Order(100)
    public void deleteTask() throws JsonProcessingException {
        // Auth
        String token = auth();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept-Encoding", "gzip, deflate, br");


        // get task
        HttpEntity<?> getRequestWithBearerAuth = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate
                .exchange("/taskservice/api/v1/getTaskByAuthor/test@gmail.com/0",
                        HttpMethod.GET,
                        getRequestWithBearerAuth,
                        String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        List<Task> body = mapper.readValue(response.getBody(), new TypeReference<List<Task>>(){});
        Task task = body.get(0);

        assertEquals("test@gmail.com", task.getAuthorEmail().getEmail());

        // delete task
        ResponseEntity<BasicResponse> deleteResponse = restTemplate
                .exchange("/taskservice/api/v1/deleteTask?id=" + task.getId(),
                        HttpMethod.DELETE,
                        getRequestWithBearerAuth,
                        BasicResponse.class);

        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // check if it successfully deleted
        ResponseEntity<Object> responseAfterDelete = restTemplate
                .exchange("/taskservice/api/v1/getTask?id=" + task.getId(),
                        HttpMethod.GET,
                        getRequestWithBearerAuth,
                        Object.class);

        assertEquals(HttpStatus.NOT_FOUND, responseAfterDelete.getStatusCode());
    }



    public String auth() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("testpassword")
                .build();

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/taskservice/api/v1/auth/authenticate",
                new HttpEntity<>(loginRequest),
                LoginResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getEmail());
        assertNotNull(response.getBody().getToken());
        assertEquals("test@gmail.com", response.getBody().getEmail());

        return (response.getBody().getToken());
    }
}
