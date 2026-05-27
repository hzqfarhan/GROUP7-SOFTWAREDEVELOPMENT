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
                case 2: studentMenu(); break;
                case 3: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid option.");
            }
        } while (choice != 3);
    }
    
    public static void adminMenu() { System.out.println("Admin menu coming soon..."); }
    public static void studentMenu() { System.out.println("Student menu coming soon..."); }
}