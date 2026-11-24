package main.java.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection {
    private static final String FILE_KONFIG = "db.properties";
    private static Properties konfigurasi;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver jar missing from lib/ folder", e);
        }
        Properties config = muatKonfigurasi();
        return DriverManager.getConnection(config.getProperty("url"), config.getProperty("user"),
                config.getProperty("password"));
    }

    private static synchronized Properties muatKonfigurasi() throws SQLException {
        if (konfigurasi == null) {
            Properties config = new Properties();
            try (FileInputStream in = new FileInputStream(FILE_KONFIG)) {
                config.load(in);
            } catch (IOException e) {
                throw new SQLException("File " + FILE_KONFIG + " tidak ditemukan di folder project.", e);
            }
            if (config.getProperty("url") == null || config.getProperty("user") == null
                    || config.getProperty("password") == null) {
                throw new SQLException("Isi " + FILE_KONFIG + " harus memuat url, user, dan password.");
            }
            konfigurasi = config;
        }
        return konfigurasi;
    }
}