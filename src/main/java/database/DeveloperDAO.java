package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.java.model.Developer;

public class DeveloperDAO {
    public static String buatAccountID(int nomor) {
        return String.format("DEV%03d", nomor);
    }

    public static Developer cariAtauBuat(ArrayList<Developer> daftar, String nama) {
        for (Developer d : daftar) {
            if (d.getNamaDeveloper().equalsIgnoreCase(nama)) {
                return d;
            }
        }
        Developer baru = new Developer();
        baru.setNamaDeveloper(nama);
        daftar.add(baru);
        return baru;
    }

    static int idDeveloper(Connection conn, String nama) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM developer WHERE LOWER(nama_developer) = LOWER(?)")) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO developer (nama_developer) VALUES (?)", new String[] { "id" })) {
            ps.setString(1, nama);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public static boolean usernameTerpakai(String username) throws SQLException {
        String sql = "SELECT 1 FROM developer WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean emailTerpakai(String email) throws SQLException {
        String sql = "SELECT 1 FROM developer WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean namaSudahPunyaAkun(String nama) throws SQLException {
        String sql = "SELECT 1 FROM developer WHERE LOWER(nama_developer) = LOWER(?) AND username IS NOT NULL";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean tambah(Developer d) {
        if (d.getPassword() == null) {
            return false;
        }
        String passwordTersimpan = PasswordUtil.untukDisimpan(d.getPassword());
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            int id = -1;
            String namaDb = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, nama_developer FROM developer WHERE LOWER(nama_developer) = LOWER(?)")) {
                ps.setString(1, d.getNamaDeveloper());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        id = rs.getInt("id");
                        namaDb = rs.getString("nama_developer");
                    }
                }
            }

            if (id != -1) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE developer SET username = ?, email = ?, password = ? "
                                + "WHERE id = ? AND username IS NULL")) {
                    ps.setString(1, d.getUsername());
                    ps.setString(2, d.getEmail());
                    ps.setString(3, passwordTersimpan);
                    ps.setInt(4, id);
                    if (ps.executeUpdate() == 0) {
                        return false;
                    }
                }
                d.setNamaDeveloper(namaDb);
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO developer (nama_developer, username, email, password) VALUES (?, ?, ?, ?)",
                        new String[] { "id" })) {
                    ps.setString(1, d.getNamaDeveloper());
                    ps.setString(2, d.getUsername());
                    ps.setString(3, d.getEmail());
                    ps.setString(4, passwordTersimpan);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }
            }
            d.setAccountID(buatAccountID(id));
            d.setPassword(null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Developer masuk(String username, String password) throws SQLException {
        String sql = "SELECT id, username, email, password, nama_developer FROM developer "
                + "WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.cocok(password, rs.getString("password"))) {
                    return new Developer(buatAccountID(rs.getInt("id")), rs.getString("username"),
                            rs.getString("email"), rs.getString("nama_developer"));
                }
            }
        }
        return null;
    }
}