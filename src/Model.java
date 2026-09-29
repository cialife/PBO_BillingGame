import java.util.ArrayList;

/* ===================== INTERFACE ===================== */

interface Login {
    void daftarAkun();
    void masukAkun();
}

interface Cetak {
    // Data penerbit (konstanta) - silakan ubah sesuai kebutuhan
    String penerbit = "Toko Produk Digital";
    String alamat = "Jl. Jenderal Sudirman No. 1, Jakarta";
    String IDPPN = "PPN-0000000001";

    void tampilkanInvoice();
}

/* ===================== PRODUK ===================== */

class ProdukDigital {
    public static final double TARIF_PPN = 0.11; // PPN 11%

    private String kodeProduk;
    private String judul;
    private String publisher;
    private double harga;

    public ProdukDigital(String kodeProduk, String judul, String publisher, double harga) {
        this.kodeProduk = kodeProduk;
        this.judul = judul;
        this.publisher = publisher;
        this.harga = harga;
    }

    public ProdukDigital() {
    }

    public void setKodeProduk(String kodeProduk) { this.kodeProduk = kodeProduk; }
    public void setJudul(String judul) { this.judul = judul; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setHarga(double harga) { this.harga = harga; }

    public String getKodeProduk() { return kodeProduk; }
    public String getJudul() { return judul; }
    public String getPublisher() { return publisher; }
    public double getHarga() { return harga; }

    public double hitungPPN(double harga) {
        return harga * TARIF_PPN;
    }
}

class Game extends ProdukDigital {
    private String genre;
    private double ukuranFile; // dalam GB

    public Game(String kodeProduk, String judul, String publisher, double harga,
                String genre, double ukuranFile) {
        super(kodeProduk, judul, publisher, harga);
        this.genre = genre;
        this.ukuranFile = ukuranFile;
    }

    public void setGenre(String genre) { this.genre = genre; }
    public void setUkuranFile(double ukuranFile) { this.ukuranFile = ukuranFile; }
    public String getGenre() { return genre; }
    public double getUkuranFile() { return ukuranFile; }
}

class DLC extends Game {
    private String jenis;
    private Game baseGame;

    public DLC(String kodeProduk, String judul, String publisher, double harga,
               String jenis, Game baseGame) {
        super(kodeProduk, judul, publisher, harga, baseGame.getGenre(), baseGame.getUkuranFile());
        this.jenis = jenis;
        this.baseGame = baseGame;
    }

    public void setBaseGame(Game baseGame) { this.baseGame = baseGame; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public Game getBaseGame() { return baseGame; }
    public String getJenis() { return jenis; }
}

/* ===================== PENGGUNA ===================== */

class Pengguna implements Login {
    private String accountID;
    private String username;
    private String email;

    public Pengguna(String accountID, String username, String email) {
        this.accountID = accountID;
        this.username = username;
        this.email = email;
    }

    public Pengguna() {
    }

    public void setAccountID(String accountID) { this.accountID = accountID; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public String getAccountID() { return accountID; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    @Override
    public void daftarAkun() {
        System.out.println("Akun '" + username + "' (" + accountID + ") berhasil didaftarkan.");
    }

    @Override
    public void masukAkun() {
        System.out.println("Pengguna '" + username + "' berhasil masuk.");
    }
}

class Developer extends Pengguna {
    private String namaDeveloper;

    public Developer(String accountID, String username, String email, String namaDeveloper) {
        super(accountID, username, email);
        this.namaDeveloper = namaDeveloper;
    }

    public void setNamaDeveloper(String namaDeveloper) { this.namaDeveloper = namaDeveloper; }
    public String getNamaDeveloper() { return namaDeveloper; }
}

class Pembeli extends Pengguna {
    private String alamatIP;
    private String lokasi;
    private int poinSteam;

    public Pembeli(String accountID, String username, String email,
                   String alamatIP, String lokasi, int poinSteam) {
        super(accountID, username, email);
        this.alamatIP = alamatIP;
        this.lokasi = lokasi;
        this.poinSteam = poinSteam;
    }

    public void setAlamatIP(String alamatIP) { this.alamatIP = alamatIP; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public void setPoinSteam(int poinSteam) { this.poinSteam = poinSteam; }
    public String getAlamatIP() { return alamatIP; }
    public String getLokasi() { return lokasi; }
    public int getPoinSteam() { return poinSteam; }
}

/* ===================== PEMBAYARAN ===================== */

class Pembayaran {
    private String metodePembayaran;

    public Pembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public Pembayaran() {
    }

    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }
    public String getMetodePembayaran() { return metodePembayaran; }
}

/* ===================== INVOICE ===================== */

class Invoice implements Cetak {
    private String invoiceNo;
    private String tanggal;
    private double subtotal;
    private double ppn;
    private double totalTagihan;
    private ArrayList<ProdukDigital> listProduk = new ArrayList<>();
    private Pembeli pembeli;
    private Pembayaran pembayaran;

    public Invoice(String invoiceNo, String tanggal, Pembeli pembeli, Pembayaran pembayaran) {
        this.invoiceNo = invoiceNo;
        this.tanggal = tanggal;
        this.pembeli = pembeli;
        this.pembayaran = pembayaran;
    }

    public Invoice() {
    }

    public void tambahProduk(ProdukDigital produk) {
        listProduk.add(produk);
        hitungTotal();
    }

    public void hitungTotal() {
        subtotal = 0;
        ppn = 0;
        for (ProdukDigital p : listProduk) {
            subtotal += p.getHarga();
            ppn += p.hitungPPN(p.getHarga());
        }
        totalTagihan = subtotal + ppn;
    }

    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
    public void setPembeli(Pembeli pembeli) { this.pembeli = pembeli; }
    public void setPembayaran(Pembayaran pembayaran) { this.pembayaran = pembayaran; }

    public ArrayList<ProdukDigital> getListProduk() { return listProduk; }
    public String getInvoiceNo() { return invoiceNo; }
    public String getTanggal() { return tanggal; }
    public double getSubtotal() { return subtotal; }
    public double getPPN() { return ppn; }
    public double getTotalTagihan() { return totalTagihan; }
    public Pembeli getPembeli() { return pembeli; }
    public Pembayaran getPembayaran() { return pembayaran; }

    /** Judul semua produk dalam invoice, dipisah koma (dipakai untuk menu pilihan). */
    public String getDaftarJudul() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listProduk.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(listProduk.get(i).getJudul());
        }
        return sb.toString();
    }

    /** Format angka menjadi rupiah dengan titik pemisah ribuan, tanpa library tambahan. */
    public static String rupiah(double nilai) {
        long bulat = Math.round(nilai);
        String angka = String.valueOf(bulat);
        StringBuilder hasil = new StringBuilder();
        int hitung = 0;
        for (int i = angka.length() - 1; i >= 0; i--) {
            hasil.insert(0, angka.charAt(i));
            hitung++;
            if (hitung % 3 == 0 && i != 0) {
                hasil.insert(0, ".");
            }
        }
        return "Rp " + hasil;
    }

    private static String baris(String label, String isi) {
        return String.format("%-17s: %s%n", label, isi);
    }

    /** Teks invoice lengkap untuk ditampilkan di terminal. */
    public String buatTeksInvoice() {
        hitungTotal();
        String garisTebal = "=".repeat(52) + "\n";
        String garis = "-".repeat(52) + "\n";
        StringBuilder sb = new StringBuilder();

        sb.append(garisTebal);
        sb.append("                 INVOICE PEMBELIAN\n");
        sb.append(garisTebal);
        sb.append(baris("Penerbit", penerbit));
        sb.append(baris("Alamat", alamat));
        sb.append(baris("ID PPN", IDPPN));
        sb.append(garis);
        sb.append(baris("No. Invoice", invoiceNo));
        sb.append(baris("Tanggal", tanggal));
        sb.append(baris("Pembeli", pembeli.getUsername() + " (" + pembeli.getAccountID() + ")"));
        sb.append(baris("Email", pembeli.getEmail()));
        sb.append(baris("Poin Steam", String.valueOf(pembeli.getPoinSteam())));
        sb.append(baris("Metode Bayar", pembayaran.getMetodePembayaran()));
        sb.append(garis);
        sb.append("DAFTAR PRODUK\n");

        int no = 1;
        for (ProdukDigital p : listProduk) {
            String tipe = (p instanceof DLC) ? "DLC" : (p instanceof Game) ? "Game" : "Produk";
            sb.append(no++).append(". [").append(tipe).append("] ")
              .append(p.getJudul()).append(" (").append(p.getKodeProduk()).append(")\n");
            sb.append("   Publisher : ").append(p.getPublisher()).append("\n");
            if (p instanceof DLC) {
                DLC d = (DLC) p;
                sb.append("   Jenis DLC : ").append(d.getJenis()).append("\n");
                sb.append("   Base Game : ").append(d.getBaseGame().getJudul()).append("\n");
            }
            if (p instanceof Game) {
                Game g = (Game) p;
                sb.append("   Genre     : ").append(g.getGenre()).append("\n");
                sb.append("   Ukuran    : ").append(g.getUkuranFile()).append(" GB\n");
            }
            sb.append("   Harga     : ").append(rupiah(p.getHarga())).append("\n");
        }

        sb.append(garis);
        sb.append(baris("Subtotal", rupiah(subtotal)));
        sb.append(baris("PPN (11%)", rupiah(ppn)));
        sb.append(baris("TOTAL TAGIHAN", rupiah(totalTagihan)));
        sb.append(baris("IP Address", pembeli.getAlamatIP()));
        sb.append(baris("Lokasi Pembelian", pembeli.getLokasi()));
        sb.append(garisTebal);
        sb.append("        Terima kasih atas pembelian Anda!\n");
        sb.append(garisTebal);
        return sb.toString();
    }

    @Override
    public void tampilkanInvoice() {
        System.out.println(buatTeksInvoice());
    }
}