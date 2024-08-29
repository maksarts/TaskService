CREATE TABLE employee (
                          email VARCHAR(100) PRIMARY KEY NOT NULL,
                          name VARCHAR(100),
                          password VARCHAR(500)
);


CREATE TABLE task (
                      id SERIAL PRIMARY KEY NOT NULL,
                      title VARCHAR(500) UNIQUE  NOT NULL,
                      description TEXT,
                      priority INTEGER,
                      author_email VARCHAR(100) NOT NULL,
                      executor_email VARCHAR(100),
                      task_status INTEGER,
                      created_ts TIMESTAMP WITH TIME ZONE
);

CREATE TABLE comment (
                         id SERIAL PRIMARY KEY NOT NULL,
                         author_email VARCHAR(100) NOT NULL,
                         task_id INTEGER NOT NULL,
                         content TEXT,
                         created_ts TIMESTAMP WITH TIME ZONE
);

CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY NOT NULL,
                               employee_email VARCHAR(100) NOT NULL,
                               token VARCHAR(500),
                               expiry_date TIMESTAMP WITH TIME ZONE,
                               revoked BOOLEAN
);

CREATE TABLE token (
                       id SERIAL PRIMARY KEY NOT NULL,
                       employee_email VARCHAR(100) NOT NULL,
                       token VARCHAR(500)
);