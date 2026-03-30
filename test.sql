CREATE TABLE users (
   id BIGINT PRIMARY KEY,
   username VARCHAR(100) NOT NULL,
   email VARCHAR(200) UNIQUE
);

CREATE TABLE posts (
   id BIGINT PRIMARY KEY,
   title VARCHAR(200) NOT NULL,
   content TEXT,
   user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE courses (
   id BIGINT PRIMARY KEY,
   name VARCHAR(100) NOT NULL
);

CREATE TABLE user_course (
   user_id BIGINT NOT NULL,
   course_id BIGINT NOT NULL,
   CONSTRAINT pk_user_course PRIMARY KEY (user_id, course_id),
   CONSTRAINT fk_uc_user FOREIGN KEY (user_id) REFERENCES users(id),
   CONSTRAINT fk_uc_course FOREIGN KEY (course_id) REFERENCES courses(id)
);