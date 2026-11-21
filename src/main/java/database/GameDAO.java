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
                Developer dev = DeveloperDAO.cariAtauBuat(daftarDeveloper, rs.getString("nama_developer"));
                daftar.add(new Game(rs.getString("kode_produk"), rs.getString("judul"), dev,
                        rs.getDouble("harga"), rs.getString("genre"), rs.getDouble("ukuran_file")));
            }
        }
        return daftar;
    }

    public static boolean kodeProdukTerpakai(String kode) throws SQLException {
        String sql = "SELECT 1 FROM produk_digital WHERE LOWER(kode_produk) = LOWER(?)";
        try (Connection conn = PostgreSQLConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Mengisi tabel induk (produk_digital) lalu tabel anak (game); mengembalikan id
    // baru.
    // Dipakai juga oleh DLCDAO karena DLC adalah turunan Game.
    static int sisipProdukDanGame(Connection conn, Game g) throws SQLException {
        int devId = DeveloperDAO.idDeveloper(conn, g.getDeveloper().getNamaDeveloper());
        int id;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO produk_digital (kode_produk, judul, developer_id, harga) VALUES (?, ?, ?, ?)",
                new String[] { "id" })) {
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

    // Ubah produk (game atau DLC). Kode produk tidak diubah. jenis hanya dipakai untuk DLC (null untuk game).
    public static boolean ubahProduk(String kode, String judul, double harga, String genre,
            double ukuran, String jenis) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE produk_digital SET judul = ?, harga = ? WHERE kode_produk = ? RETURNING id")) {
                    ps.setString(1, judul);
                    ps.setDouble(2, harga);
                    ps.setString(3, kode);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE game SET genre = ?, ukuran_file = ? WHERE id = ?")) {
                    ps.setString(1, genre);
                    ps.setDouble(2, ukuran);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                }
                if (jenis != null) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE dlc SET jenis = ? WHERE id = ?")) {
                        ps.setString(1, jenis);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Gagal mengubah produk " + kode + ": " + e.getMessage());
            return false;
        }
    }

    // Hapus produk. Jika yang dihapus adalah game utama, DLC miliknya ikut dihapus
    // (kolom base_game_id tidak punya ON DELETE CASCADE, jadi harus dihapus dulu).
    public static boolean hapusProduk(String kode) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM produk_digital WHERE id IN (SELECT x.id FROM dlc x "
                                + "JOIN produk_digital b ON b.id = x.base_game_id WHERE b.kode_produk = ?)")) {
                    ps.setString(1, kode);
                    ps.executeUpdate();
                }
                int terhapus;
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM produk_digital WHERE kode_produk = ?")) {
                    ps.setString(1, kode);
                    terhapus = ps.executeUpdate();
                }
                conn.commit();
                return terhapus > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Gagal menghapus produk " + kode + ": " + e.getMessage());
            return false;
        }
    }
}