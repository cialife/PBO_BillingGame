package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/OOP";
    private static final String USER = "Melvina";
    private static final String PASSWORD = "hai";

    public static Connection getConnection() throws SQLException {
        // Explicitly register driver to prevent classpath issues in no-build setups
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver jar missing from lib/ folder", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}