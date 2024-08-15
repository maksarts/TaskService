CREATE TABLE employee (
                          email VARCHAR(100) PRIMARY KEY NOT NULL,
                          name VARCHAR(100),
                          password VARCHAR(500)
);


CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(500) UNIQUE,
                      description VARCHAR(1000),
                      priority INTEGER,
                      author_email VARCHAR(100) REFERENCES employee(email),
                      executor_email VARCHAR(100) REFERENCES employee(email),
                      task_status INTEGER,
                      created_ts TIMESTAMP
);

CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         author_email VARCHAR(100) REFERENCES employee(email),
                         task_id INTEGER REFERENCES task(id),
                         content VARCHAR(1000),
                         created_ts TIMESTAMP
);

CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               employee_email VARCHAR(100) REFERENCES employee(email),
                               token VARCHAR(500),
                               expiry_date TIMESTAMP,
                               revoked BOOLEAN
);

CREATE TABLE token (
                       id SERIAL PRIMARY KEY,
                       employee_email VARCHAR(100) REFERENCES employee(email),
                       token VARCHAR(500)
);