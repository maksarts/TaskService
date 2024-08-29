# TaskService
This repository showcases a project that demonstrates the implementation of Jira-like service 
for task management, using JPA Hibernate, PostgreSQL and provides secure access using 
JSON Web Tokens (JWT) with Spring Boot 3 and Spring Security 6. 
The project includes the following functionalities:

- User Registration and Login with JWT Authentication
- Refresh Token stored in db
- Create tasks with specified description, status and priority
- Assign or reassign executors, change status and manage workflow of tasks
- Writing comments under tasks
- OpenAPI Documentation Integration (Swagger)

# Technologies

- Spring Boot 3.1
- Spring Security
- Spring Web
- Spring Test
- Spring Data JPA
- Hibernate
- Spring Boot Validation
- JSON Web Tokens (JWT)
- BCrypt
- Maven
- OpenAPI Swagger
- Lombok, Jackson
- PostgreSQL
- Docker, dockerfile-maven-plugin, docker-compose

# Getting Started
To get started with this project, you will need to have the following installed on your local machine:
- JDK 17+
- Maven 3+
- Docker engine 27+

## 1. Clone this repository

## 2. Run tests
1. Go to project's root directory (```.../TaskService/```)
2. Setup test database with 
```bash
docker-compose -f ./docker-compose-test.yaml up -d
```
3. Run
```bash
mvn test
```
5. If everything done well you will see something like this:
```   
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------
[INFO] Total time:  43.252 s
```

### Tested on:
```bash
mvn --version
``` 
=>
```
Apache Maven 3.8.7 (b89d5959fcde851dcb1c8946a785a163f14e1e29)
Maven home: C:\Program Files\Maven\apache-maven-3.8.7
Java version: 17.0.11, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-17
Default locale: ru_RU, platform encoding: Cp1251
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```
```bash 
docker --version
``` 
=>
```Docker version 27.1.1, build 6312585```

## 3. Service setup (local)
1. Go to project's root directory (```.../TaskService/```)
2. Configure ```.env``` file according scheme below and place it into project's root directory
```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=password
SECURITY_USER=user
SECURITY_PASSWORD=password
```
3. Build project and create image using 
```bash
mvn package -DskipTests=true dockerfile:build
```
If evererything is fine you will see
```
[INFO] ----------------
[INFO] BUILD SUCCESS
[INFO] ----------------
```
3. Run whole application (Spring Boot service + PostgreSQL DB) using command
###### *Note: you may need to delete container with database used for tests because it uses same 5432 port with dev database*
```bash
docker-compose up -d
```
If it executes successfully you will see
```
[+] Running 3/3
    ✔ Network taskservice_core  Created                                                                                                       1.5s 
    ✔ Container taskservice_db  Started                                                                                                      15.7s 
    ✔ Container taskservice     Started
```
## 4. TaskService workflow (local)
Swagger API definition may be found on
```link 
localhost:8080/taskservice-doc
```
### Simple examples using Postman:
#### 1. Register new user:
```REST
POST http://localhost:8080/taskservice/api/v1/auth/register
```
```json
{
  "email": "user@gmail.com",
  "name": "User",
  "password": "password"
}
```
Response:
```json
{
    "email": "user@gmail.com",
    "token": "<JWT-token>",
    "errMsg": null,
    "refreshToken": "<refresh-token>",
    "tokenType": "BEARER"
}
```
#### 2. Authenticate with created user credentials:
```REST
POST http://localhost:8080/taskservice/api/v1/auth/authenticate
```
```json
{
  "email": "user@gmail.com",
  "password": "password"
}
```
Response:
```json
{
    "email": "user@gmail.com",
    "token": "<JWT-token>",
    "errMsg": null,
    "refreshToken": "<refresh-token>",
    "tokenType": "BEARER"
}
```
##### *Note: next requests requires Bearer auth in each of them*
#### 3. Create new tasks 
(if you include ```"executorEmail"``` in your requests this user should be already registered)
```REST
POST http://localhost:8080/taskservice/api/v1/createTask
```
```json
{
  "title": "My task",
  "description": "Some description",
  "priority": 10,
  "executorEmail": "anotheruser@gmail.com"
}
```
Response:
```json
{
  "id": 1,
  "title": "My task",
  "description": "Some description",
  "priority": 10,
  "authorEmail": {
    "email": "user@gmail.com",
    "name": "User"
  },
  "executorEmail": {
    "email": "anotheruser@gmail.com",
    "name": "anotheruser"
  },
  "createdTs": "2024-08-15T12:41:47.493+00:00",
  "taskStatus": "OPEN"
}
```
#### 4. Get tasks of author
Getting tasks of author ```user@gmail.com```, 
response is pageable as well so we looking for first page (0-indexed) and using
standart ```pageSize=10```
```REST
GET http://localhost:8080/taskservice/api/v1/getTaskByAuthor/user@gmail.com/0?pageSize=10
```
Response represented as list:
```json
[
  {
    "id": 4,
    "title": "My task 2",
    "description": "Some description",
    "priority": 10,
    "authorEmail": {
      "email": "user@gmail.com",
      "name": "User"
    },
    "executorEmail": {
      "email": "anotheruser@gmail.com",
      "name": "anotheruser"
    },
    "createdTs": "2024-08-15T12:41:47.493+00:00",
    "taskStatus": "OPEN"
  },
  {
    "id": 3,
    "title": "My task",
    "description": "Some description",
    "priority": 10,
    "authorEmail": {
      "email": "user@gmail.com",
      "name": "User"
    },
    "executorEmail": {
      "email": "anotheruser@gmail.com",
      "name": "anotheruser"
    },
    "createdTs": "2024-08-15T12:37:28.448+00:00",
    "taskStatus": "OPEN"
  },
  {
    "id": 2,
    "title": "UserTask2",
    "description": "Desc 2",
    "priority": 0,
    "authorEmail": {
      "email": "user@gmail.com",
      "name": "User"
    },
    "executorEmail": null,
    "createdTs": "2024-08-15T12:21:57.233+00:00",
    "taskStatus": "OPEN"
  },
  ... etc.
```

#### 5. Change task status
Possible statuses (case-insensetive):
```OPEN, PROGRESS, WAITING, RESOLVED```
```REST
PUT http://localhost:8080/taskservice/api/v1/updateTaskStatus?id=4&status=PROGRESS
```
Response:
```json
{
  "id": 4,
  "title": "My task 2",
  "description": "Some description",
  "priority": 10,
  "authorEmail": {
    "email": "user@gmail.com",
    "name": "User"
  },
  "executorEmail": {
    "email": "anotheruser@gmail.com",
    "name": "anotheruser"
  },
  "createdTs": "2024-08-15T12:41:47.493+00:00",
  "taskStatus": "PROGRESS"
}
```

### All possible actions you will find in Swagger, thank you for your attention :)