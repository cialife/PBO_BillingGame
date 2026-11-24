package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.model.Pembeli;

public class PembeliDAO {
    public static String buatAccountID(int nomor) {
        return String.format("ACC%03d", nomor);
    }

    public static boolean usernameTerpakai(String username) throws SQLException {
        String sql = "SELECT 1 FROM pembeli WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean emailTerpakai(String email) throws SQLException {
        String sql = "SELECT 1 FROM pembeli WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static boolean tambah(Pembeli p) {
        if (p.getPassword() == null) {
            return false;
        }
        String sql = "INSERT INTO pembeli (username, email, password, alamat_ip, lokasi, poin_stream) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" })) {
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getEmail());
            ps.setString(3, PasswordUtil.untukDisimpan(p.getPassword()));
            ps.setString(4, p.getAlamatIP());
            ps.setString(5, p.getLokasi());
            ps.setInt(6, p.getPoinStream());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setAccountID(buatAccountID(rs.getInt(1)));
                }
            }
            p.setPassword(null);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Pembeli masuk(String username, String password) throws SQLException {
        String sql = "SELECT id, username, email, password, alamat_ip, lokasi, poin_stream "
                + "FROM pembeli WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.cocok(password, rs.getString("password"))) {
                    return new Pembeli(buatAccountID(rs.getInt("id")), rs.getString("username"),
                            rs.getString("email"), rs.getString("alamat_ip"), rs.getString("lokasi"),
                            rs.getInt("poin_stream"));
                }
            }
        }
        return null;
    }

    public static boolean updatePoin(Pembeli p) {
        String sql = "UPDATE pembeli SET poin_stream = ? WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getPoinStream());
            ps.setString(2, p.getUsername());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}