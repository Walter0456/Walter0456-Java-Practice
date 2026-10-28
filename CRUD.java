<dependecy>
    <groudID>org.xerial</groudID>
    <artificatID>sqlite-jdbc</artificatID>
    <version>3.46.0.0</version>
</dependecy>

import java.sql*;

public class DB {
    private static final String URL = "jdbc:sqlite:students.db"
    private static final String USER = "";
    private static final String PASSS=***

    static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

try (Connection c = DB.get();
    Statement s = c.createStatement()) {
        s.execute(""" 
        CREATE TABLE IF NOT EXISTS student (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        course TEXT,
        year_level INT
        )
        """);
    }

String sql = "SELECT id, name, course, year_level FROM students WHERE course = ?";
try (Connection c = DB.get();
    PreparedStatement ps = c.prepa
)
