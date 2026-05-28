import java.sql.*;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

// Manages the connection to the MySQL database
class DatabaseConnection {
    // Loads environment variables from a .env file securely
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Method to establish connection to the database
    public static Connection getConnection() throws SQLException {
        // Read environment variables for database connection details
        String url = dotenv.get("MYSQL_URL");
        String user = dotenv.get("MYSQL_USER");
        String password = dotenv.get("MYSQL_PASSWORD");

        // Returns a Connection object that other parts of the program use to execute SQL queries
        return DriverManager.getConnection(url, user, password);
    }
}

// Base class representing a general person
class Person {
    String name;

    // Default constructor
    public Person() {
    }

    // Parameterized constructor to set the name
    public Person(String name) {
        this.name = name;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }
}

// Student class that inherits from Person
class Student extends Person {
    String matricNumber;

    // Constructor to initialize a student with a name and matric number
    public Student(String name, String matricNumber) {
        super(name); // Calls the constructor of the parent Person class
        this.matricNumber = matricNumber;
    }

    // Getter method for matric number
    public String getMatricNumber() {
        return matricNumber;
    }

    // Method to calculate the total credit hours a student is currently registered for
    public int getTotalCreditHours() {
        int total = 0;
        // SQL query to sum up credit hours of all courses registered by this student
        String query = "SELECT SUM(c.credit_hours) AS total_credits FROM student_courses sc JOIN courses c ON sc.course_code = c.course_code WHERE sc.matric_number = ?";
        
        // Try-with-resources automatically closes database connections when done
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.matricNumber); // Insert matric number into the query
            ResultSet rs = stmt.executeQuery();
            
            // Get the total credit if result is found
            if (rs.next())
                total = rs.getInt("total_credits");
        } catch (SQLException e) {
            // Silently ignoring exceptions here (ideally should log them)
        }
        return total;
    }

    // Method to check if the student is already registered for a specific course
    public boolean isRegistered(String courseCode) {
        // SQL query to look for a matching record
        String query = "SELECT 1 FROM student_courses WHERE matric_number = ? AND course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.matricNumber);
            stmt.setString(2, courseCode);
            // Returns true if a record exists, false otherwise
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }
}

// Represents a university course
class Course {
    String courseCode;
    String courseName;
    int creditHours;
    int maxSeats;
    int enrolledSeats;

    // Constructor to initialize course details
    public Course(String courseCode, String courseName, int creditHours, int maxSeats, int enrolledSeats) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.creditHours = creditHours;
        this.maxSeats = maxSeats;
        this.enrolledSeats = enrolledSeats;
    }

    // Getters for course attributes
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCreditHours() { return creditHours; }
    public int getMaxSeats() { return maxSeats; }
    public int getEnrolledSeats() { return enrolledSeats; }

    // Helper method to check if the course has reached maximum capacity
    public boolean isFull() {
        return enrolledSeats >= maxSeats;
    }
}

// Main application class
public class UTHMCourseRegistrationSystem {
    // Scanner for reading user input from the console
    static Scanner input = new Scanner(System.in);
    // Global constant defining the maximum credits a student can take
    static final int MAX_CREDITS = 20;

    // The entry point of the program
    public static void main(String[] args) {
        int choice = 0;
        // Main menu loop
        do {
            System.out.println("\n=== UTHM Course Registration System ===");
            System.out.println("1. Admin Menu");
            System.out.println("2. Student Menu");
            System.out.println("3. Exit");
            System.out.print("\nSelect an option: ");
            choice = input.nextInt();
            input.nextLine(); // Consume the newline character left by nextInt()

            // Navigate to the appropriate menu based on user input
            switch (choice) {
                case 1:
                    adminMenu();
                    break;
                case 2:
                    studentMenu();
                    break;
                case 3:
                    System.out.println("Exiting the system. Thank you!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 3); // Keep running until the user selects 'Exit'
    }

    // Handles administrator operations
    public static void adminMenu() {
        // Admin Authentication
        System.out.println("\n--- Admin Login ---");
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();

        boolean isAuthenticated = false;
        // Query to verify admin credentials
        String loginQuery = "SELECT 1 FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(loginQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                isAuthenticated = true; // Login successful
        } catch (SQLException e) {
            // Detailed error reporting if the database connection fails
            System.err.println("\n================ [ CONNECTION ERROR ] ================");
            System.err.println("Failed to connect to the database or execute login query!");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }

        if (!isAuthenticated) {
            System.out.println("Error: Invalid username or password.");
            return; // Exit the admin menu if authentication fails
        }

        System.out.println("\nLogin successful!");
        int choice = 0;
        
        // Admin Operations Loop
        do {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add New Student\n2. Remove Student\n3. View All Students\n4. Search Student\n5. Add New Course\n6. Remove Course\n7. View All Courses\n8. Search Course\n9. Back to Main Menu");
            System.out.print("\nSelect operation: ");
            choice = input.nextInt();
            input.nextLine();

            // Route to specific admin functions
            switch (choice) {
                case 1: addStudent(); break;
                case 2: removeStudent(); break;
                case 3: viewAllStudents(); break;
                case 4: searchStudent(); break;
                case 5: addCourse(); break;
                case 6: removeCourse(); break;
                case 7: viewAllCourses(); break;
                case 8: searchCourse(); break;
                case 9: break; // Returns to main menu
                default: System.out.println("Invalid selection.");
            }
        } while (choice != 9);
    }

    // --- Admin Database Operations ---

    // Inserts a new student record into the database
    public static void addStudent() {
        System.out.print("Enter Student Name: ");
        String name = input.nextLine();
        System.out.print("Enter Matric Number: ");
        String matric = input.nextLine();
        
        String query = "INSERT INTO students (matric_number, name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matric);
            stmt.setString(2, name);
            stmt.executeUpdate(); // Execute the INSERT command
            System.out.println("\nSuccess: Student added.");
        } catch (SQLException e) {
            System.out.println("Error adding student.");
        }
    }

    // Deletes a student record based on matric number
    public static void removeStudent() {
        System.out.print("Enter Matric Number to remove: ");
        String matric = input.nextLine();
        
        String query = "DELETE FROM students WHERE matric_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matric);
            stmt.executeUpdate(); // Execute the DELETE command
            System.out.println("\nSuccess: Student removed.");
        } catch (SQLException e) {
            System.out.println("Error removing student.");
        }
    }

    // Retrieves and prints all registered students
    public static void viewAllStudents() {
        String query = "SELECT matric_number, name FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\nRegistered Students:");
            // Loop through all results and print them
            while (rs.next())
                System.out.println("- " + rs.getString("name") + " (" + rs.getString("matric_number") + ")");
        } catch (SQLException e) {
        }
    }

    // Looks up a specific student by their matric number
    public static void searchStudent() {
        System.out.print("Enter Matric Number to search: ");
        String matric = input.nextLine();
        
        String query = "SELECT name, matric_number FROM students WHERE matric_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matric);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                System.out.println("Found: " + rs.getString("name"));
            else
                System.out.println("Not found.");
        } catch (SQLException e) {
        }
    }

    // Inserts a new course into the database
    public static void addCourse() {
        System.out.print("Enter Course Code: ");
        String code = input.nextLine();
        System.out.print("Enter Course Name: ");
        String name = input.nextLine();
        System.out.print("Enter Credit Hours: ");
        int credits = input.nextInt();
        System.out.print("Enter Maximum Seats: ");
        int seats = input.nextInt();
        input.nextLine();
        
        // enrolled_seats starts at 0 by default
        String query = "INSERT INTO courses (course_code, course_name, credit_hours, max_seats, enrolled_seats) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, credits);
            stmt.setInt(4, seats);
            stmt.executeUpdate();
            System.out.println("\nSuccess: Course added.");
        } catch (SQLException e) {
            System.out.println("Error adding course.");
        }
    }

    // Deletes a course from the database
    public static void removeCourse() {
        System.out.print("Enter Course Code to remove: ");
        String code = input.nextLine();
        
        String query = "DELETE FROM courses WHERE course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            stmt.executeUpdate();
            System.out.println("\nSuccess: Course removed.");
        } catch (SQLException e) {
        }
    }

    // Retrieves and prints all available courses
    public static void viewAllCourses() {
        String query = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\nAvailable Courses:");
            while (rs.next())
                System.out.println("- " + rs.getString("course_code") + ": " + rs.getString("course_name")
                        + " | Seats: " + rs.getInt("enrolled_seats") + "/" + rs.getInt("max_seats"));
        } catch (SQLException e) {
        }
    }

    // Looks up a specific course by its code
    public static void searchCourse() {
        System.out.print("Enter Course Code to search: ");
        String code = input.nextLine();
        
        String query = "SELECT * FROM courses WHERE course_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                System.out.println("Found: " + rs.getString("course_code"));
            else
                System.out.println("Not found.");
        } catch (SQLException e) {
        }
    }

    // Handles operations for individual students
    public static void studentMenu() {
        // Authenticate student by matric number
        System.out.print("\nEnter your Matric Number: ");
        String matric = input.nextLine(); // Get input from user
        Student currentStudent = null;
        
        String query = "SELECT name, matric_number FROM students WHERE matric_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, matric);
            ResultSet rs = stmt.executeQuery();
            // If student exists (in database), create a Student object to manage their session
            if (rs.next())
                currentStudent = new Student(rs.getString("name"), rs.getString("matric_number"));
        } catch (SQLException e) {
        }

        // Show error if student does not exist
        if (currentStudent == null) {
            System.out.println("Error: Student record not found.");
            return;
        }

        int choice = 0;
        // Student Operations Menu Loop
        do {
            System.out.println("\n--- Student Menu --- (" + currentStudent.getName() + ")");
            System.out.println("1. View Available Courses\n2. Register for a Course\n3. Drop a Course\n4. View My Courses\n5. Back to Main Menu");
            System.out.print("Select operation: ");
            choice = input.nextInt();
            input.nextLine(); // Clear the scanner buffer
            
            // Route to specific student functions
            switch (choice) {
                case 1: viewAllCourses(); break;
                case 2: registerCourseAction(currentStudent); break;
                case 3: dropCourseAction(currentStudent); break;
                case 4: viewMyCourses(currentStudent); break;
                case 5: break;
            }
        } while (choice != 5);
    }

    // Logic for a student attempting to register for a course
    public static void registerCourseAction(Student s) {
        System.out.print("Enter Course Code to register: ");
        String code = input.nextLine();
        Course c = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, fetch the course details to validate the request
            String courseQuery = "SELECT * FROM courses WHERE course_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(courseQuery)) {
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    c = new Course(rs.getString("course_code"), rs.getString("course_name"), rs.getInt("credit_hours"),
                            rs.getInt("max_seats"), rs.getInt("enrolled_seats"));
            }
            
            // Validation Checks
            if (c == null) {
                System.out.println("Error: Course does not exist.");
                return;
            }
            if (s.isRegistered(code)) {
                System.out.println("Error: Already registered.");
            } else if (c.isFull()) {
                System.out.println("Error: Course full.");
            } else if ((s.getTotalCreditHours() + c.getCreditHours()) > MAX_CREDITS) {
                System.out.println("Error: Exceeds " + MAX_CREDITS + " credits.");
            } else {
                // All checks passed; proceed with registration
                
                // Disable auto-commit to run multiple queries as a single transaction
                conn.setAutoCommit(false); 
                try {
                    // Step 1: Map the student to the course
                    String insertMapping = "INSERT INTO student_courses (matric_number, course_code) VALUES (?, ?)";
                    try (PreparedStatement stmt1 = conn.prepareStatement(insertMapping)) {
                        stmt1.setString(1, s.getMatricNumber());
                        stmt1.setString(2, code);
                        stmt1.executeUpdate();
                    }
                    
                    // Step 2: Increase the enrolled seat count for the course
                    String updateSeats = "UPDATE courses SET enrolled_seats = enrolled_seats + 1 WHERE course_code = ?";
                    try (PreparedStatement stmt2 = conn.prepareStatement(updateSeats)) {
                        stmt2.setString(1, code);
                        stmt2.executeUpdate();
                    }
                    
                    // If both queries succeed, commit the transaction to save changes
                    conn.commit();
                    System.out.println("\nSuccess! Registered for " + c.getCourseCode());
                } catch (SQLException ex) {
                    // If any query fails, undo (rollback) all changes to prevent corrupted data
                    conn.rollback();
                } finally {
                    // Always turn auto-commit back on to avoid affecting future operations
                    conn.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
        }
    }

    // Logic for a student attempting to drop a course
    public static void dropCourseAction(Student s) {
        viewMyCourses(s); // Show current classes first
        System.out.print("Enter Course Code to drop: ");
        String code = input.nextLine();
        
        // Ensure they are actually registered for it
        if (s.isRegistered(code)) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Use transactions (like in registration) to ensure database consistency
                conn.setAutoCommit(false);
                try {
                    // Step 1: Remove the student-course link (delete registration record)
                    String deleteMapping = "DELETE FROM student_courses WHERE matric_number = ? AND course_code = ?";
                    try (PreparedStatement stmt1 = conn.prepareStatement(deleteMapping)) {
                        stmt1.setString(1, s.getMatricNumber());
                        stmt1.setString(2, code);
                        stmt1.executeUpdate();
                    }
                    
                    // Step 2: Decrease the enrolled seat count for the course
                    String updateSeats = "UPDATE courses SET enrolled_seats = enrolled_seats - 1 WHERE course_code = ?";
                    try (PreparedStatement stmt2 = conn.prepareStatement(updateSeats)) {
                        stmt2.setString(1, code);
                        stmt2.executeUpdate();
                    }
                    
                    // Apply changes
                    conn.commit();
                    System.out.println("\nCourse dropped successfully.");
                } catch (SQLException ex) {
                    // Undo changes on failure
                    conn.rollback();
                } finally {
                    // Restore default connection behavior
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
            }
        }
    }

    // Shows only the courses that a specific student is registered for
    public static void viewMyCourses(Student s) {
        // Joins the courses and student_courses tables to find matching records for this student
        String query = "SELECT c.course_code, c.course_name, c.credit_hours FROM courses c JOIN student_courses sc ON c.course_code = sc.course_code WHERE sc.matric_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, s.getMatricNumber());
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nYour Registered Courses:");
            while (rs.next())
                System.out.println("- " + rs.getString("course_code") + ": " + rs.getString("course_name"));
        } catch (SQLException e) {
        }
    }
}