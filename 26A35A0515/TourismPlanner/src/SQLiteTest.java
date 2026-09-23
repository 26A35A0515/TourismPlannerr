
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteTest {

    public static void main(String[] args) {

        try {

            Connection connection =
                DriverManager.getConnection(
                    "jdbc:sqlite:database/test.db"
                );

            System.out.println(
                "SQLite JDBC connection successful!"
            );

            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}