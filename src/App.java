import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class App {

    private static final String FILE_DATA = "DataInvoice.txt";
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Invoice> daftarInvoice = new ArrayList<>();

    public static void main(String[] args) {
        muatDariFile();

        boolean jalan = true;
        while (jalan) {
            System.out.println("\n========== SISTEM INVOICE ==========");
            System.out.println("1. Input transaksi / invoice");
            System.out.println("2. Tampilkan invoice di terminal");
            System.out.println("3. Cetak struk (pop-out)");
            System.out.println("4. Keluar");
            int pilih = bacaInt("Pilih menu (1-4): ", 1, 4);

            switch (pilih) {
                case 1: inputTransaksi(); break;
                case 2: tampilkanInvoiceTerminal(); break;
                case 3: cetakStruk(); break;
                case 4:
                    System.out.println("Terima kasih. Program selesai.");
                    jalan = false;
                    break;
            }
        }
    }

    /* ==================== MENU 1: INPUT TRANSAKSI ==================== */

    private static void inputTransaksi() {
        System.out.println("\n--- INPUT TRANSAKSI ---");

        // Data pembeli
        System.out.println("[Data Pembeli]");
        String accountID = bacaString("Account ID        : ");
        String username = bacaString("Username          : ");
        String email = bacaString("Email             : ");
        String ip = bacaString("IP Address        : ");
        String lokasi = bacaString("Alamat/Wilayah    : ");
        int poin = bacaInt("Poin Steam        : ", 0, Integer.MAX_VALUE);
        Pembeli pembeli = new Pembeli(accountID, username, email, ip, lokasi, poin);
        pembeli.daftarAkun();

        // Pembayaran
        System.out.println("\n[Pembayaran]");
        String metode = bacaString("Metode pembayaran : ");
        Pembayaran pembayaran = new Pembayaran(metode);

        // Invoice
        String tanggal = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Invoice invoice = new Invoice(buatNomorInvoice(), tanggal, pembeli, pembayaran);

        // Produk (bisa lebih dari satu: 1..*)
        boolean tambah = true;
        while (tambah) {
            System.out.println("\n[Produk]");
            System.out.println("1. Game");
            System.out.println("2. DLC");
            int tipe = bacaInt("Pilih tipe produk (1-2): ", 1, 2);

            String kode = bacaString("Kode produk      : ");
            String judul = bacaString("Judul            : ");
            String publisher = bacaString("Publisher        : ");
            double harga = bacaDouble("Harga (Rp)       : ");

            if (tipe == 1) {
                String genre = bacaString("Genre            : ");
                double ukuran = bacaDouble("Ukuran file (GB)  : ");
                invoice.tambahProduk(new Game(kode, judul, publisher, harga, genre, ukuran));
            } else {
                String jenis = bacaString("Jenis DLC        : ");
                System.out.println("-- Data base game dari DLC ini --");
                String bKode = bacaString("Kode base game   : ");
                String bJudul = bacaString("Judul base game  : ");
                String bGenre = bacaString("Genre base game  : ");
                double bUkuran = bacaDouble("Ukuran base game (GB): ");
                Game base = new Game(bKode, bJudul, publisher, 0, bGenre, bUkuran);
                invoice.tambahProduk(new DLC(kode, judul, publisher, harga, jenis, base));
            }

            System.out.print("Tambah produk lain ke invoice ini? (y/n): ");
            tambah = sc.nextLine().trim().equalsIgnoreCase("y");
        }

        daftarInvoice.add(invoice);
        simpanKeFile(invoice);
        System.out.println("\nTransaksi " + invoice.getInvoiceNo() + " berhasil disimpan ke " + FILE_DATA);
    }

    private static String buatNomorInvoice() {
        int max = 0;
        for (Invoice inv : daftarInvoice) {
            try {
                int n = Integer.parseInt(inv.getInvoiceNo().replace("INV-", ""));
                if (n > max) max = n;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("INV-%03d", max + 1);
    }

    /* ==================== MENU 2: TAMPILKAN DI TERMINAL ==================== */

    private static void tampilkanInvoiceTerminal() {
        Invoice inv = pilihInvoice("TAMPILKAN INVOICE");
        if (inv != null) {
            System.out.println();
            inv.tampilkanInvoice();
        }
    }

    /* ==================== MENU 3: CETAK STRUK (POP-OUT) ==================== */

    private static void cetakStruk() {
        Invoice inv = pilihInvoice("CETAK STRUK");
        if (inv == null) return;

        String teks = inv.buatTeksInvoice();
        try {
            JTextArea area = new JTextArea(teks);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(500, 560));
            JOptionPane.showMessageDialog(null, scroll,
                    "Struk " + inv.getInvoiceNo(), JOptionPane.PLAIN_MESSAGE);
        } catch (HeadlessException e) {
            System.out.println("(Lingkungan tanpa layar, struk ditampilkan di terminal)\n");
            System.out.println(teks);
        }
    }

    /* ==================== PILIH INVOICE BERDASARKAN NAMA GAME ==================== */

    private static Invoice pilihInvoice(String judulMenu) {
        if (daftarInvoice.isEmpty()) {
            System.out.println("\nBelum ada transaksi. Silakan input transaksi terlebih dahulu (menu 1).");
            return null;
        }
        System.out.println("\n--- " + judulMenu + " ---");
        System.out.println("Pilih game:");
        for (int i = 0; i < daftarInvoice.size(); i++) {
            Invoice inv = daftarInvoice.get(i);
            System.out.println((i + 1) + ". " + inv.getDaftarJudul() + "  [" + inv.getInvoiceNo() + "]");
        }
        System.out.println("0. Kembali");
        int pilih = bacaInt("Pilihan: ", 0, daftarInvoice.size());
        return (pilih == 0) ? null : daftarInvoice.get(pilih - 1);
    }

    /* ==================== FILE: DataInvoice.txt ====================
     * Satu baris = satu invoice. Pemisah: '|' antar bagian, '#' antar produk, ';' antar field produk.
     * INV|no|tanggal|accountID|username|email|ip|lokasi|poin|metode|produk1#produk2
     * Game: GAME;kode;judul;publisher;harga;genre;ukuran
     * DLC : DLC;kode;judul;publisher;harga;jenis;bKode;bJudul;bGenre;bUkuran
     */

    private static void simpanKeFile(Invoice inv) {
        Pembeli b = inv.getPembeli();
        StringBuilder sb = new StringBuilder();
        sb.append("INV|").append(inv.getInvoiceNo())
          .append("|").append(inv.getTanggal())
          .append("|").append(b.getAccountID())
          .append("|").append(b.getUsername())
          .append("|").append(b.getEmail())
          .append("|").append(b.getAlamatIP())
          .append("|").append(b.getLokasi())
          .append("|").append(b.getPoinSteam())
          .append("|").append(inv.getPembayaran().getMetodePembayaran())
          .append("|");

        ArrayList<ProdukDigital> list = inv.getListProduk();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append("#");
            ProdukDigital p = list.get(i);
            if (p instanceof DLC) {
                DLC d = (DLC) p;
                Game g = d.getBaseGame();
                sb.append("DLC;").append(d.getKodeProduk()).append(";").append(d.getJudul())
                  .append(";").append(d.getPublisher()).append(";").append(d.getHarga())
                  .append(";").append(d.getJenis())
                  .append(";").append(g.getKodeProduk()).append(";").append(g.getJudul())
                  .append(";").append(g.getGenre()).append(";").append(g.getUkuranFile());
            } else if (p instanceof Game) {
                Game g = (Game) p;
                sb.append("GAME;").append(g.getKodeProduk()).append(";").append(g.getJudul())
                  .append(";").append(g.getPublisher()).append(";").append(g.getHarga())
                  .append(";").append(g.getGenre()).append(";").append(g.getUkuranFile());
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_DATA, true))) {
            pw.println(sb);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan ke file: " + e.getMessage());
        }
    }

    private static void muatDariFile() {
        File f = new File(FILE_DATA);
        if (!f.exists()) return;

        int berhasil = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    daftarInvoice.add(parseInvoice(line));
                    berhasil++;
                } catch (Exception e) {
                    System.out.println("Baris dilewati (format tidak valid): " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file: " + e.getMessage());
        }
        if (berhasil > 0) {
            System.out.println(berhasil + " invoice dimuat dari " + FILE_DATA);
        }
    }

    private static Invoice parseInvoice(String line) {
        String[] f = line.split("\\|", -1);
        if (!f[0].equals("INV") || f.length < 11) throw new IllegalArgumentException();

        Pembeli pembeli = new Pembeli(f[3], f[4], f[5], f[6], f[7], Integer.parseInt(f[8]));
        Invoice inv = new Invoice(f[1], f[2], pembeli, new Pembayaran(f[9]));

        for (String prod : f[10].split("#")) {
            if (prod.isEmpty()) continue;
            String[] p = prod.split(";", -1);
            if (p[0].equals("GAME")) {
                inv.tambahProduk(new Game(p[1], p[2], p[3], Double.parseDouble(p[4]),
                        p[5], Double.parseDouble(p[6])));
            } else if (p[0].equals("DLC")) {
                Game base = new Game(p[6], p[7], p[3], 0, p[8], Double.parseDouble(p[9]));
                inv.tambahProduk(new DLC(p[1], p[2], p[3], Double.parseDouble(p[4]), p[5], base));
            } else {
                throw new IllegalArgumentException();
            }
        }
        return inv;
    }

    /* ==================== HELPER INPUT ==================== */

    private static String bacaString(String prompt) {
        while (true) {
            System.out.print(prompt);
            // hapus karakter pemisah file agar format DataInvoice.txt tidak rusak
            String s = sc.nextLine().replace("|", " ").replace("#", " ").replace(";", " ").trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input tidak boleh kosong.");
        }
    }

    private static int bacaInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Masukkan angka yang valid.");
        }
    }

    private static double bacaDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double d = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
                if (d >= 0) return d;
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Masukkan angka yang valid (tidak negatif).");
        }
    }
}