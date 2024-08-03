CREATE TABLE employee (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100)
);


CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(500),
                      description VARCHAR(1000),
                      priority INTEGER,
                      author_id INTEGER REFERENCES employee(id),
                      executor_id INTEGER REFERENCES employee(id),
                      task_status INTEGER
);

CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         author_id INTEGER REFERENCES employee(id),
                         content VARCHAR(1000)
)