import java.sql.*;

public class Database {

    private static final String URL =
            "jdbc:sqlite:database/tourism.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {

    
        try (Connection con = connect();
             Statement st = con.createStatement()) {

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS destinations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    location TEXT NOT NULL,
                    description TEXT,
                    daily_cost REAL,
                    distance REAL
                )
            """);

            ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM destinations");

            if (rs.next() && rs.getInt(1) == 0) {

                insertDestination(
                        con,
                        "Araku Valley",
                        "Andhra Pradesh",
                        "Beautiful hill station with valleys and waterfalls.",
                        1800,
                        280
                );

                insertDestination(
                        con,
                        "Visakhapatnam",
                        "Andhra Pradesh",
                        "Beautiful coastal city with beaches and hills.",
                        1700,
                        150
                );

                insertDestination(
                        con,
                        "Tirupati",
                        "Andhra Pradesh",
                        "Popular pilgrimage and tourist destination.",
                        1500,
                        250
                );

                insertDestination(
                        con,
                        "Hyderabad",
                        "Telangana",
                        "Historic city with Charminar and many attractions.",
                        2000,
                        500
                );

                insertDestination(
                        con,
                        "Goa",
                        "Goa",
                        "Famous beaches and tourist attractions.",
                        2500,
                        650
                );

                insertDestination(
                        con,
                        "Ooty",
                        "Tamil Nadu",
                        "Beautiful hill station surrounded by nature.",
                        2200,
                        850
                );

                insertDestination(
                        con,
                        "Bengaluru",
                        "Karnataka",
                        "Technology city with parks and attractions.",
                        2200,
                        700
                );

                insertDestination(
                        con,
                        "Munnar",
                        "Kerala",
                        "Famous for tea gardens and mountains.",
                        2500,
                        1100
                );

                insertDestination(
                        con,
                        "pithapuram",
                        "andhra pradesh",
                        "Famous for shiva temple.",
                        2500,
                        1100
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertDestination(
            Connection con,
            String name,
            String location,
            String description,
            double cost,
            double distance) throws SQLException {

        String sql = """
                INSERT INTO destinations
                (name, location, description, daily_cost, distance)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, description);
            ps.setDouble(4, cost);
            ps.setDouble(5, distance);

            ps.executeUpdate();
        }
    }
}