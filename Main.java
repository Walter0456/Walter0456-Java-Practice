
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:sqlite:school.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("Connected!");

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}