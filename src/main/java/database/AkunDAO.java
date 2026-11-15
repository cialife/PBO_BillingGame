package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.java.model.Pembeli;

public class AkunDAO {

    public static ArrayList<Pembeli> muatAkun() throws SQLException {
        ArrayList<Pembeli> daftar = new ArrayList<>();
        String sql = "SELECT id, username, email, alamat_ip, lokasi, poin_stream "
                + "FROM pembeli ORDER BY id";
        try (Connection conn = PostgreSQLConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                daftar.add(new Pembeli(buatAccountID(rs.getInt("id")), rs.getString("username"),
                        rs.getString("email"), rs.getString("alamat_ip"), rs.getString("lokasi"),
                        rs.getInt("poin_stream")));
            }
        }
        return daftar;
    }

    public static boolean tambahAkun(Pembeli p) {
        String sql = "INSERT INTO pembeli (username, email, alamat_ip, lokasi, poin_stream) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getUsername());
            pstmt.setString(2, p.getEmail());
            pstmt.setString(3, p.getAlamatIP());
            pstmt.setString(4, p.getLokasi());
            pstmt.setInt(5, p.getPoinStream());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // id dari database dipakai untuk membentuk accountID (misal 1 -> ACC001)
                    p.setAccountID(buatAccountID(rs.getInt(1)));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePoin(Pembeli p) {
        String sql = "UPDATE pembeli SET poin_stream = ? WHERE username = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getPoinStream());
            pstmt.setString(2, p.getUsername());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Pembeli cariAkun(ArrayList<Pembeli> daftar, String username, String email) {
        for (Pembeli p : daftar) {
            if (p.getUsername().toLowerCase().equals(username.toLowerCase())
                    && p.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    public static boolean usernameTerpakai(ArrayList<Pembeli> daftar, String username) {
        for (Pembeli p : daftar) {
            if (p.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String buatAccountID(int nomor) {
        if (nomor < 10) {
            return "ACC00" + nomor;
        } else if (nomor < 100) {
            return "ACC0" + nomor;
        } else {
            return "ACC" + nomor;
        }
    }
}