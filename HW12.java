import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class HW12 {
    public static void main(String[] args) {
        readFormFile();
        writeToFile();
        databaseOperation();
    }
    private static void readFormFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("my_test_file.txt"))) {
            System.out.println("Read from file: " + reader.readLine());

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    private static void writeToFile() {
        try (Scanner scanner = new Scanner(System.in)) {
        System.out.print("Enter phrase: ");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("my_test_file.txt"))) {
                writer.write(scanner.nextLine());
                System.out.println("Written to file successfully");
                
            } catch (IOException e) {
                System.out.println("Error writing file: " + e.getMessage());
            }
        } 
    }

    private static void databaseOperation() {
        String url = "jdbc:sqlite:test.db";

        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {

                //Setup: Create table with initial data
                stmt.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, name TEXT)");
                ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM students");
                if (countRs.next() && countRs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO students VALUES (1, 'John Smith')");
                    System.out.println("Initial data created");
                }
                countRs.close();

                conn.setAutoCommit(false);

                // Read original record
                ResultSet rs = stmt.executeQuery("SELECT * FROM students WHERE id = 1");
                if (rs.next()) {
                    System.out.println("Orginal record: ID=" + rs.getInt("id") + ", Name=" + rs.getString("name"));

                    // Modify and update record
                    stmt.executeUpdate("UPDATE students SET name = 'Modified Name' WHERE id = 1");
                    System.out.println("Record updated in transaction");

                    // Verify the update within transaction
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM students WHERE id = 1");
                    if (rs2.next()) {
                        System.out.println("Modified record: ID=" + rs2.getInt("id") + ", Name=" + rs2.getString("name"));

                    }
                    rs2.close();

                    // Rollback - restore to original state
                    conn.rollback();
                    System.out.println("Changes rolled back successfully");

                    // Verify rollback worked
                    ResultSet rs3 = stmt.executeQuery("SELECT * FROM students WHERE id = 1");
                    if (rs3.next()) {
                        System.out.println("After rollback: ID=" + rs3.getInt("id") + ", Name=" + rs3.getString("name"));

                        
                    }
                    rs3.close();                

                }
                rs.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }

    }

    
}
