package ru.maksarts.taskservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import ru.maksarts.taskservice.model.Task;
import ru.maksarts.taskservice.service.auth.JwtService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "spring.main.allow-bean-definition-overriding=true")
@Slf4j
@Import(TestConfig.class)
public class TaskServiceIntegrationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;


    @Test
    public void testUnauthorized() {
        ResponseEntity<Task> response = restTemplate
                .getForEntity("/taskservice/api/v1/getTask?id=1", Task.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAuthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(fakeUserToken());
        headers.set("Accept-Encoding", "gzip, deflate, br");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        ResponseEntity<Task> response = restTemplate
                .exchange("/taskservice/api/v1/getTask?id=1",
                HttpMethod.GET,
                request,
                Task.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
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

    private String fakeUserToken(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User.UserBuilder userBuilder = User.builder().passwordEncoder(passwordEncoder::encode);

        List<String> roles = new ArrayList<>();
        roles.add("USER");
        UserDetails user = userBuilder
                .username("test@gmail.com")
                .password("testpassword")
                .roles(roles.toArray(new String[0]))
                .build();

        return jwtService.generateToken(user);
    }

}
