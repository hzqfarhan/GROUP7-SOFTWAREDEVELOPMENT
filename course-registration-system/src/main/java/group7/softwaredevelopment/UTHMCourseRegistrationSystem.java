import java.sql.*;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();


    
    //method to establish connection to the database
    public static Connection getConnection() throws SQLException {
        //read environment variables from .env file for database connection
        String url = dotenv.get("MYSQL_URL");
        String user = dotenv.get("MYSQL_USER");
        String password = dotenv.get("MYSQL_PASSWORD");

        //returns a Connection object that other parts of the program use to execute SQL queries.
        return DriverManager.getConnection(url, user, password);
    }
}


class Person {
    String name;
    public Person() {}
    public Person(String name) { this.name = name; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class Course {
       String courseCode;
       String courseName;
       int creditHours;
       int maxSeats;
       int enrolledSeats;
   
       public Course(String courseCode, String courseName, int creditHours, int maxSeats, int enrolledSeats) {
           this.courseCode = courseCode;
           this.courseName = courseName;
           this.creditHours = creditHours;
           this.maxSeats = maxSeats;
           this.enrolledSeats = enrolledSeats;
       }
   
       public String getCourseCode() { return courseCode; }
       public String getCourseName() { return courseName; }
       public int getCreditHours() { return creditHours; }
       public int getMaxSeats() { return maxSeats; }
       public int getEnrolledSeats() { return enrolledSeats; }
       public boolean isFull() { return enrolledSeats >= maxSeats; }
   }

public class UTHMCourseRegistrationSystem {
    static Scanner input = new Scanner(System.in);
    static final int MAX_CREDITS = 20;

    public static void main(String[] args) {
           int choice = 0;
           do {
               System.out.println("\n=== UTHM Course Registration System ===");
               System.out.println("1. Admin Menu");
               System.out.println("2. Student Menu");
               System.out.println("3. Exit");
               System.out.print("\nSelect an option: ");
               choice = input.nextInt();
               input.nextLine();
   
               switch (choice) {
                   case 1: adminMenu(); break;
                   case 2: System.out.println("Student menu not implemented yet."); break;
                   case 3: System.out.println("Exiting the system. Thank you!"); break;
                   default: System.out.println("Invalid option. Please try again.");
               }
           } while (choice != 3);
       }
   
       public static void adminMenu() {
           System.out.println("\n--- Admin Login ---");
           System.out.print("Username: ");
           String username = input.nextLine();
           System.out.print("Password: ");
           String password = input.nextLine();
           
           boolean isAuthenticated = false;
           String loginQuery = "SELECT 1 FROM admins WHERE username = ? AND password = ?";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(loginQuery)) {
               stmt.setString(1, username);
               stmt.setString(2, password);
               ResultSet rs = stmt.executeQuery();
               if (rs.next()) isAuthenticated = true;
           } catch (SQLException e) { System.out.println("DB error: " + e.getMessage()); }
           
           if (!isAuthenticated) {
               System.out.println("Error: Invalid username or password.");
               return;
           }
           
           System.out.println("\nLogin successful!");
           int choice = 0;
           do {
               System.out.println("\n--- Admin Menu ---");
               System.out.println("1. Add New Student\n2. Remove Student\n3. View All Students\n4. Search Student\n5. Add New Course\n6. Remove Course\n7. View All Courses\n8. Search Course\n9. Back to Main Menu");
               System.out.print("\nSelect operation: ");
               choice = input.nextInt();
               input.nextLine();
               
               switch (choice) {
                   case 1: addStudent(); break;
                   case 2: removeStudent(); break;
                   case 3: viewAllStudents(); break;
                   case 4: searchStudent(); break;
                   case 5: addCourse(); break;
                   case 6: removeCourse(); break;
                   case 7: viewAllCourses(); break;
                   case 8: searchCourse(); break;
                   case 9: break;
                   default: System.out.println("Invalid selection.");
               }
           } while (choice != 9);
       }
   
       public static void addStudent() {
           System.out.print("Enter Student Name: "); String name = input.nextLine();
           System.out.print("Enter Matric Number: "); String matric = input.nextLine();
           String query = "INSERT INTO students (matric_number, name) VALUES (?, ?)";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, matric); stmt.setString(2, name);
               stmt.executeUpdate(); System.out.println("\nSuccess: Student added.");
           } catch (SQLException e) { System.out.println("Error adding student."); }
       }
   
       public static void removeStudent() {
           System.out.print("Enter Matric Number to remove: "); String matric = input.nextLine();
           String query = "DELETE FROM students WHERE matric_number = ?";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, matric); stmt.executeUpdate();
               System.out.println("\nSuccess: Student removed.");
           } catch (SQLException e) { System.out.println("Error removing student."); }
       }
   
       public static void viewAllStudents() {
           String query = "SELECT matric_number, name FROM students";
           try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
               System.out.println("\nRegistered Students:");
               while (rs.next()) System.out.println("- " + rs.getString("name") + " (" + rs.getString("matric_number") + ")");
           } catch (SQLException e) {}
       }
       
       public static void searchStudent() {
           System.out.print("Enter Matric Number to search: "); String matric = input.nextLine();
           String query = "SELECT name, matric_number FROM students WHERE matric_number = ?";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, matric); ResultSet rs = stmt.executeQuery();
               if (rs.next()) System.out.println("Found: " + rs.getString("name"));
               else System.out.println("Not found.");
           } catch (SQLException e) {}
       }
       
       public static void addCourse() {
           System.out.print("Enter Course Code: "); String code = input.nextLine();
           System.out.print("Enter Course Name: "); String name = input.nextLine();
           System.out.print("Enter Credit Hours: "); int credits = input.nextInt();
           System.out.print("Enter Maximum Seats: "); int seats = input.nextInt(); input.nextLine();
           String query = "INSERT INTO courses (course_code, course_name, credit_hours, max_seats, enrolled_seats) VALUES (?, ?, ?, ?, 0)";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, code); stmt.setString(2, name); stmt.setInt(3, credits); stmt.setInt(4, seats);
               stmt.executeUpdate(); System.out.println("\nSuccess: Course added.");
           } catch (SQLException e) { System.out.println("Error adding course."); }
       }
   
       public static void removeCourse() {
           System.out.print("Enter Course Code to remove: "); String code = input.nextLine();
           String query = "DELETE FROM courses WHERE course_code = ?";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, code); stmt.executeUpdate(); System.out.println("\nSuccess: Course removed.");
           } catch (SQLException e) {}
       }
       
       public static void viewAllCourses() {
           String query = "SELECT * FROM courses";
           try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
               System.out.println("\nAvailable Courses:");
               while (rs.next()) System.out.println("- " + rs.getString("course_code") + ": " + rs.getString("course_name") + " | Seats: " + rs.getInt("enrolled_seats") + "/" + rs.getInt("max_seats"));
           } catch (SQLException e) {}
       }
       
       public static void searchCourse() {
           System.out.print("Enter Course Code to search: "); String code = input.nextLine();
           String query = "SELECT * FROM courses WHERE course_code = ?";
           try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
               stmt.setString(1, code); ResultSet rs = stmt.executeQuery();
               if (rs.next()) System.out.println("Found: " + rs.getString("course_code"));
               else System.out.println("Not found.");
               } catch (SQLException e) {}
       }
    
    public static void adminMenu() { System.out.println("Admin menu coming soon..."); }
    public static void studentMenu() { System.out.println("Student menu coming soon..."); }
}