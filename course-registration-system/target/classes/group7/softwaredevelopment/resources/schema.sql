CREATE TABLE IF NOT EXISTS students (
    matric_number VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    course_code VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    credit_hours INT NOT NULL,
    max_seats INT NOT NULL,
    enrolled_seats INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS student_courses (
    matric_number VARCHAR(20),
    course_code VARCHAR(20),
    PRIMARY KEY (matric_number, course_code),
    FOREIGN KEY (matric_number) REFERENCES students(matric_number) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE
);
