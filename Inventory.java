import java.sql.*;
import java.util.Scanner;

/**
 * Basic Inventory CRUD (console) using JDBC + SQLite.
 *
 * Run:  java -cp sqlite-jdbc-3.42.0.0.jar Inventory.java
 * (put the jar in the same folder as this file)
 */
public class Inventory {

    // FUNDAMENTAL #3: the connection URL is an address.
    // For SQLite it's just a file name. This creates inventory.db if missing.
    static final String URL = "jdbc:sqlite:inventory.db";

    static final Scanner sc = new Scanner(System.in);

    // ------------------------------------------------------------
    // MENU (the "user side" - this is NOT database code)
    // ------------------------------------------------------------
    public static void main(String[] args) {
        createTable();

        while (true) {
            System.out.println("\n=== INVENTORY ===");
            System.out.println("1. Add item");
            System.out.println("2. View items");
            System.out.println("3. Update quantity");
            System.out.println("4. Delete item");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addItem();        break;
                case "2": viewItems();      break;
                case "3": updateQuantity(); break;
                case "4": deleteItem();     break;
                case "0": System.out.println("Bye!"); return;
                default:  System.out.println("Invalid choice.");
            }
        }
    }

    // ------------------------------------------------------------
    // CREATE TABLE (runs once at startup, safe to run many times)
    // ------------------------------------------------------------
    static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS items ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "name TEXT NOT NULL, "
                   + "quantity INTEGER NOT NULL, "
                   + "price REAL NOT NULL)";

        // FUNDAMENTAL #6: try-with-resources auto-closes conn and st.
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // C = CREATE  (INSERT)
    // ------------------------------------------------------------
    static void addItem() {
        System.out.print("Item name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        int qty = readInt("Quantity: ");
        double price = readDouble("Price: ");

        // FUNDAMENTAL #4: SQL is just a String. The ? marks are holes
        // that get filled safely (this is what blocks SQL injection).
        String sql = "INSERT INTO items (name, quantity, price) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);   // 1st ?
            ps.setInt(2, qty);       // 2nd ?
            ps.setDouble(3, price);  // 3rd ?
            ps.executeUpdate();      // INSERT/UPDATE/DELETE use executeUpdate
            System.out.println("Item added.");
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // R = READ  (SELECT)
    // ------------------------------------------------------------
    static void viewItems() {
        String sql = "SELECT id, name, quantity, price FROM items ORDER BY id";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {   // SELECT uses executeQuery

            System.out.printf("%-5s %-20s %-8s %-10s%n", "ID", "NAME", "QTY", "PRICE");
            System.out.println("-------------------------------------------------");

            // FUNDAMENTAL #5: ResultSet is a cursor. It starts BEFORE row 1.
            // next() moves down one row, returns false when rows run out.
            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("%-5d %-20s %-8d %-10.2f%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
            if (!any) System.out.println("(no items yet)");

        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // U = UPDATE
    // ------------------------------------------------------------
    static void updateQuantity() {
        int id = readInt("Item ID to update: ");
        int qty = readInt("New quantity: ");

        String sql = "UPDATE items SET quantity = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();   // returns how many rows changed
            System.out.println(rows == 0 ? "No item with that ID." : "Updated.");
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // D = DELETE
    // ------------------------------------------------------------
    static void deleteItem() {
        int id = readInt("Item ID to delete: ");

        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows == 0 ? "No item with that ID." : "Deleted.");
        } catch (SQLException e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // INPUT HELPERS (basic validation so typing "abc" doesn't crash)
    // ------------------------------------------------------------
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
