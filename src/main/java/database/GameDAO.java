package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.java.model.Developer;
import main.java.model.Game;

public class GameDAO {

    public static Developer cariAtauBuatDeveloper(ArrayList<Developer> daftarDeveloper, String nama) {
        for (Developer d : daftarDeveloper) {
            if (d.getNamaDeveloper().equals(nama)) {
                return d;
            }
        }
        Developer baru = new Developer();
        baru.setNamaDeveloper(nama);
        daftarDeveloper.add(baru);
        return baru;
    }

    // Hanya game utama: baris game yang BUKAN DLC
    public static ArrayList<Game> muatGame(ArrayList<Developer> daftarDeveloper) throws SQLException {
        ArrayList<Game> daftar = new ArrayList<>();
        String sql = "SELECT p.kode_produk, p.judul, d.nama_developer, p.harga, g.genre, g.ukuran_file "
                + "FROM produk_digital p "
                + "JOIN game g ON g.id = p.id "
                + "JOIN developer d ON d.id = p.developer_id "
                + "WHERE NOT EXISTS (SELECT 1 FROM dlc x WHERE x.id = g.id) "
                + "ORDER BY p.id";
        try (Connection conn = PostgreSQLConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Developer dev = cariAtauBuatDeveloper(daftarDeveloper, rs.getString("nama_developer"));
                daftar.add(new Game(rs.getString("kode_produk"), rs.getString("judul"), dev,
                        rs.getDouble("harga"), rs.getString("genre"), rs.getDouble("ukuran_file")));
            }
        }
        return daftar;
    }

    // ---- Insert (dipakai ImportData; nanti juga bisa dipakai fitur tambah game)
    // ----

    static int idDeveloper(Connection conn, String nama) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM developer WHERE nama_developer = ?")) {
            ps.setString(1, nama);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO developer (nama_developer) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nama);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // Mengisi tabel induk (produk_digital) lalu tabel anak (game); mengembalikan id
    // baru
    static int sisipProdukDanGame(Connection conn, Game g) throws SQLException {
        int devId = idDeveloper(conn, g.getDeveloper().getNamaDeveloper());
        int id;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO produk_digital (kode_produk, judul, developer_id, harga) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getKodeProduk());
            ps.setString(2, g.getJudul());
            ps.setInt(3, devId);
            ps.setDouble(4, g.getHarga());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO game (id, genre, ukuran_file) VALUES (?, ?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, g.getGenre());
            ps.setDouble(3, g.getUkuranFile());
            ps.executeUpdate();
        }
        return id;
    }

    public static boolean simpanGame(Game g) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                sisipProdukDanGame(conn, g);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Gagal menyimpan game " + g.getKodeProduk() + ": " + e.getMessage());
            return false;
        }
    }
}