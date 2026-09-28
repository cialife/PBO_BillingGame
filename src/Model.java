import java.io.Serializable;
import java.util.ArrayList;

// --- INTERFACE ---
interface Login {
    void daftarAkun();
    void masukAkun();
}

interface Cetak {
    void tampilkanInvoice();
}

// --- INHERITANCE: Parent Class ---
class ProdukDigital implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String kodeProduk;
    protected String judul;
    protected String publisher;
    protected double harga;

    public ProdukDigital(String kodeProduk, String judul, String publisher, double harga) {
        this.kodeProduk = kodeProduk;
        this.judul = judul;
        this.publisher = publisher;
        this.harga = harga;
    }

    public String getJudul() { return judul; }
    public double getHarga() { return harga; }
    public String getKodeProduk() { return kodeProduk; }
    public String getPublisher() { return publisher; }
}

// --- INHERITANCE: Subclass Game ---
class Game extends ProdukDigital {
    private static final long serialVersionUID = 1L;
    private String genre;
    private double ukuranFile;

    public Game(String kodeProduk, String judul, String publisher, double harga, String genre, double ukuranFile) {
        super(kodeProduk, judul, publisher, harga);
        this.genre = genre;
        this.ukuranFile = ukuranFile;
    }

    public String getGenre() { return genre; }
    public double getUkuranFile() { return ukuranFile; }
}

// --- CLASS PEMBELI ---
class Pembeli implements Serializable {
    private static final long serialVersionUID = 1L;
    private String alamatIP;
    private String lokasi;
    private int poinSteam;

    public Pembeli(String alamatIP, String lokasi, int poinSteam) {
        this.alamatIP = alamatIP;
        this.lokasi = lokasi;
        this.poinSteam = poinSteam;
    }

    public String getLokasi() { return lokasi; }
    public String getAlamatIP() { return alamatIP; }
    public int getPoinSteam() { return poinSteam; }
}

// --- CLASS PEMBAYARAN ---
class Pembayaran implements Serializable {
    private static final long serialVersionUID = 1L;
    private String metodePembayaran;

    public Pembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public String getMetodePembayaran() { return metodePembayaran; }
}

// --- CLASS INVOICE (Mengandung Association & implements Cetak) ---
class Invoice implements Cetak, Serializable {
    private static final long serialVersionUID = 1L;
    private String invoiceNo;
    private String tanggal;
    
    // Asosiasi dengan Pembeli, Pembayaran, dan List ProdukDigital
    private Pembeli pembeli;
    private Pembayaran metodePembayaran;
    private ArrayList<ProdukDigital> listProduk;

    public Invoice(String invoiceNo, String tanggal, Pembeli pembeli, Pembayaran metodePembayaran) {
        this.invoiceNo = invoiceNo;
        this.tanggal = tanggal;
        this.pembeli = pembeli;
        this.metodePembayaran = metodePembayaran;
        this.listProduk = new ArrayList<>();
    }

    public void tambahProduk(ProdukDigital produk) {
        listProduk.add(produk);
    }

    // Method Getter untuk nomor invoice
    public String getInvoiceNo() {
        return invoiceNo;
    }

    @Override
    public void tampilkanInvoice() {
        System.out.println("\n========== INVOICE PEMBELIAN ==========");
        System.out.println("No Invoice : " + invoiceNo);
        System.out.println("Tanggal    : " + tanggal);
        System.out.println("Lokasi Pembeli: " + pembeli.getLokasi());
        System.out.println("Metode Bayar  : " + metodePembayaran.getMetodePembayaran());
        System.out.println("---------------------------------------");
        System.out.println("Daftar Produk Digital:");
        double total = 0;
        for (ProdukDigital p : listProduk) {
            System.out.println("- " + p.getJudul() + " | Harga: Rp " + p.getHarga());
            total += p.getHarga();
        }
        System.out.println("---------------------------------------");
        System.out.println("Total Tagihan : Rp " + total);
        System.out.println("=======================================\n");
    }

    // Method khusus untuk merangkai string yang digunakan pada pop-out struk
    public String formatStruk() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("                 STEAM                  \n");
        sb.append("========================================\n");
        sb.append("No Invoice    : ").append(invoiceNo).append("\n");
        sb.append("Tanggal       : ").append(tanggal).append("\n");
        sb.append("Lokasi Pembeli: ").append(pembeli.getLokasi()).append("\n");
        sb.append("Metode Bayar  : ").append(metodePembayaran.getMetodePembayaran()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Daftar Game / Produk Digital:\n");
        
        double total = 0;
        for (ProdukDigital p : listProduk) {
            sb.append("- ").append(p.getJudul()).append(" | Rp ").append(p.getHarga()).append("\n");
            total += p.getHarga();
        }
        
        sb.append("----------------------------------------\n");
        sb.append("Total Tagihan : Rp ").append(total).append("\n");
        sb.append("========================================\n");
        sb.append("      TERIMA KASIH TELAH BERBELANJA     ");
        
        return sb.toString();
    }
}