import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TourismServer {

    public static void start() throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );

        server.createContext("/", TourismServer::staticFiles);

        server.createContext(
                "/api/register",
                TourismServer::register
        );

        server.createContext(
                "/api/login",
                TourismServer::login
        );

        server.createContext(
                "/api/destinations",
                TourismServer::destinations
        );

        server.setExecutor(null);

        server.start();

        System.out.println();
        System.out.println("==================================");
        System.out.println(" TOURISM PLANNER STARTED");
        System.out.println(" http://localhost:8080");
        System.out.println("==================================");
    }

    private static void staticFiles(
            HttpExchange exchange) throws IOException {

        String path =
                exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        File file =
                new File("web" + path);

        if (!file.exists() || file.isDirectory()) {

            send(
                    exchange,
                    "404 Page Not Found",
                    404,
                    "text/plain"
            );

            return;
        }

        String contentType =
                "text/html; charset=UTF-8";

        if (path.endsWith(".css")) {
            contentType =
                    "text/css; charset=UTF-8";
        }

        if (path.endsWith(".js")) {
            contentType =
                    "application/javascript; charset=UTF-8";
        }

        byte[] data =
                Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders()
                .set("Content-Type", contentType);

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(data);
        }
    }

    private static void register(
            HttpExchange exchange) throws IOException {

        Map<String, String> data =
                parseForm(exchange);

        String name = data.get("name");
        String email = data.get("email");
        String password = data.get("password");

        if (name == null ||
                email == null ||
                password == null) {

            send(
                    exchange,
                    "{\"success\":false,\"message\":\"All fields are required\"}",
                    400,
                    "application/json"
            );

            return;
        }

        try (Connection con =
                     Database.connect()) {

            String sql =
                    "INSERT INTO users(name,email,password) VALUES(?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            send(
                    exchange,
                    "{\"success\":true,\"message\":\"Registration successful\"}",
                    200,
                    "application/json"
            );

        } catch (SQLException e) {

            send(
                    exchange,
                    "{\"success\":false,\"message\":\"Email already registered\"}",
                    400,
                    "application/json"
            );
        }
    }

    private static void login(
            HttpExchange exchange) throws IOException {

        Map<String, String> data =
                parseForm(exchange);

        String email =
                data.get("email");

        String password =
                data.get("password");

        try (Connection con =
                     Database.connect()) {

            String sql =
                    "SELECT id,name,email FROM users " +
                    "WHERE email=? AND password=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                String json =
                        "{\"success\":true," +
                        "\"name\":\"" +
                        escape(rs.getString("name")) +
                        "\"," +
                        "\"email\":\"" +
                        escape(rs.getString("email")) +
                        "\"}";

                send(
                        exchange,
                        json,
                        200,
                        "application/json"
                );

            } else {

                send(
                        exchange,
                        "{\"success\":false,\"message\":\"Invalid email or password\"}",
                        401,
                        "application/json"
                );
            }

        } catch (SQLException e) {

            send(
                    exchange,
                    "{\"success\":false,\"message\":\"Database error\"}",
                    500,
                    "application/json"
            );
        }
    }

    private static void destinations(
            HttpExchange exchange) throws IOException {

        StringBuilder json =
                new StringBuilder("[");

        boolean first = true;

        try (Connection con =
                     Database.connect();
             Statement st =
                     con.createStatement();
             ResultSet rs =
                     st.executeQuery(
                             "SELECT * FROM destinations")) {

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{");

                json.append("\"id\":")
                        .append(rs.getInt("id"))
                        .append(",");

                json.append("\"name\":\"")
                        .append(
                                escape(
                                        rs.getString("name")
                                )
                        )
                        .append("\",");

                json.append("\"location\":\"")
                        .append(
                                escape(
                                        rs.getString("location")
                                )
                        )
                        .append("\",");

                json.append("\"description\":\"")
                        .append(
                                escape(
                                        rs.getString("description")
                                )
                        )
                        .append("\",");

                json.append("\"dailyCost\":")
                        .append(
                                rs.getDouble("daily_cost")
                        )
                        .append(",");

                json.append("\"distance\":")
                        .append(
                                rs.getDouble("distance")
                        );

                json.append("}");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        json.append("]");

        send(
                exchange,
                json.toString(),
                200,
                "application/json"
        );
    }

    private static Map<String, String> parseForm(
            HttpExchange exchange) throws IOException {

        String body =
                new String(
                        exchange.getRequestBody()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> map =
                new HashMap<>();

        for (String pair :
                body.split("&")) {

            String[] parts =
                    pair.split("=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8
                        );

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8
                        );

                map.put(key, value);
            }
        }

        return map;
    }

    private static void send(
            HttpExchange exchange,
            String response,
            int status,
            String type) throws IOException {

        byte[] data =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set("Content-Type", type);

        exchange.sendResponseHeaders(
                status,
                data.length
        );

        try (OutputStream os =
                     exchange.getResponseBody()) {

            os.write(data);
        }
    }

    private static String escape(
            String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}