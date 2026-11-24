package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.java.model.DLC;
import main.java.model.Developer;
import main.java.model.Game;

public class DLCDAO {

    public static ArrayList<DLC> muatDLC(ArrayList<Game> daftarGame, ArrayList<Developer> daftarDeveloper)
            throws SQLException {
        ArrayList<DLC> daftar = new ArrayList<>();
        String sql = "SELECT p.kode_produk, p.judul, d.nama_developer, p.harga, g.genre, g.ukuran_file, "
                + "x.jenis, bp.kode_produk AS kode_base, bp.judul AS judul_base "
                + "FROM dlc x "
                + "JOIN game g ON g.id = x.id "
                + "JOIN produk_digital p ON p.id = g.id "
                + "JOIN developer d ON d.id = p.developer_id "
                + "JOIN produk_digital bp ON bp.id = x.base_game_id "
                + "WHERE p.aktif = TRUE AND bp.aktif = TRUE "
                + "ORDER BY p.id";
        try (Connection conn = PostgreSQLConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String genre = rs.getString("genre");
                String kodeBase = rs.getString("kode_base");
                Developer dev = DeveloperDAO.cariAtauBuat(daftarDeveloper, rs.getString("nama_developer"));

                Game baseGame = null;
                for (Game g : daftarGame) {
                    if (g.getKodeProduk().equalsIgnoreCase(kodeBase)) {
                        baseGame = g;
                        break;
                    }
                }
                if (baseGame == null) {
                    baseGame = new Game(kodeBase, rs.getString("judul_base"), dev, 0, genre, 0);
                }

                daftar.add(new DLC(rs.getString("kode_produk"), rs.getString("judul"), dev,
                        rs.getDouble("harga"), genre, rs.getDouble("ukuran_file"), rs.getString("jenis"),
                        baseGame));
            }
        }
        return daftar;
    }

    private static int idBaseGame(Connection conn, Game base) throws SQLException {
        boolean pakaiKode = base.getKodeProduk() != null && !base.getKodeProduk().isBlank();
        String kolom = pakaiKode ? "p.kode_produk" : "p.judul";
        String sql = "SELECT p.id FROM produk_digital p JOIN game g ON g.id = p.id "
                + "WHERE LOWER(" + kolom + ") = LOWER(?) AND p.aktif = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pakaiKode ? base.getKodeProduk() : base.getJudul());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public static boolean simpanDLC(DLC d) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int baseId = idBaseGame(conn, d.getBaseGame());
                if (baseId == -1) {
                    System.out.println("[PERINGATAN] DLC " + d.getKodeProduk() + " dilewati: base game '"
                            + d.getBaseGame().getJudul() + "' belum ada di tabel game.");
                    conn.rollback();
                    return false;
                }

                int id = GameDAO.sisipProdukDanGame(conn, d);
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO dlc (id, jenis, base_game_id) VALUES (?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setString(2, d.getJenis());
                    ps.setInt(3, baseId);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Gagal menyimpan DLC " + d.getKodeProduk() + ": " + e.getMessage());
            return false;
        }
    }
}