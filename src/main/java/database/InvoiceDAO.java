package main.java.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import main.java.model.Invoice;
import main.java.model.ProdukDigital;

public class InvoiceDAO {
    private static final DateTimeFormatter FORMAT_TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Menyimpan invoice, item, dan poin pembeli dalam SATU transaksi.
    // Jika salah satu gagal, semuanya dibatalkan.
    public static boolean simpan(Invoice inv, int poinDidapat) {
        try (Connection conn = PostgreSQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int pembeliId = idPembeli(conn, inv.getPembeli().getUsername());
                int pembayaranId = idPembayaran(conn, inv.getPembayaran().getMetodePembayaran());

                int invoiceId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO invoice (invoice_no, tanggal, pembeli_id, pembayaran_id, "
                                + "subtotal, ppn, total_tagihan, poin_didapat) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {
                    ps.setString(1, inv.getInvoiceNo());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(inv.getTanggal(), FORMAT_TANGGAL)));
                    ps.setInt(3, pembeliId);
                    ps.setInt(4, pembayaranId);
                    ps.setDouble(5, inv.getSubtotal());
                    ps.setDouble(6, inv.getPPN());
                    ps.setDouble(7, inv.getTotalTagihan());
                    ps.setInt(8, poinDidapat);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        invoiceId = rs.getInt(1);
                    }
                }

                for (ProdukDigital p : inv.getListProduk()) {
                    int produkId = idProduk(conn, p.getKodeProduk());
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO invoice_item (invoice_id, produk_id, harga_saat_beli) VALUES (?, ?, ?)")) {
                        ps.setInt(1, invoiceId);
                        ps.setInt(2, produkId);
                        ps.setDouble(3, p.getHarga());
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE pembeli SET poin_stream = ? WHERE id = ?")) {
                    ps.setInt(1, inv.getPembeli().getPoinStream());
                    ps.setInt(2, pembeliId);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException | RuntimeException e) {
            System.out.println("[ERROR] Gagal menyimpan invoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static int idPembeli(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM pembeli WHERE LOWER(username) = LOWER(?)")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Pembeli tidak ditemukan: " + username);
    }

    private static int idProduk(Connection conn, String kode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM produk_digital WHERE kode_produk = ?")) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Produk tidak ditemukan: " + kode);
    }

    // Cari metode pembayaran (tanpa membedakan huruf besar/kecil); buat baru jika belum ada
    private static int idPembayaran(Connection conn, String metode) throws SQLException {
        String bersih = metode.trim();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM pembayaran WHERE LOWER(metode_pembayaran) = LOWER(?)")) {
            ps.setString(1, bersih);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO pembayaran (metode_pembayaran) VALUES (?) RETURNING id")) {
            ps.setString(1, bersih);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}