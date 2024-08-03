package ru.maksarts.taskservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${taskservice.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in development environment");

        Info info = new Info()
                .title("TaskService API")
                .version("1.0")
                .description("This API exposes endpoints to manage personal tasks and to view other employees' tasks.");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
