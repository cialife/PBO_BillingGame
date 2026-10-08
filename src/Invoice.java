import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Invoice implements Cetak {
    private String invoiceNo;
    private String tanggal;
    private double subtotal;
    private double ppn;
    private double totalTagihan;
    private ArrayList<ProdukDigital> listProduk;
    private Pembeli pembeli;
    private Pembayaran pembayaran;

    public Invoice(String invoiceNo, String tanggal, Pembeli pembeli, Pembayaran pembayaran) {
        this.invoiceNo = invoiceNo;
        this.tanggal = tanggal;
        this.pembeli = pembeli;
        this.pembayaran = pembayaran;
        this.listProduk = new ArrayList<>();
    }

    public Invoice() {
        this.listProduk = new ArrayList<>();
    }

    public void tambahProduk(ProdukDigital produk) {
        listProduk.add(produk);
        hitungTotal();
    }

    public void hitungTotal() {
        this.subtotal = 0;
        this.ppn = 0;
        for (ProdukDigital p : listProduk) {
            this.subtotal += p.getHarga();
            this.ppn += p.hitungPPN(p.getHarga());
        }
        this.totalTagihan = this.subtotal + this.ppn;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setPembeli(Pembeli pembeli) {
        this.pembeli = pembeli;
    }

    public Pembeli getPembeli() {
        return pembeli;
    }

    public void setPembayaran(Pembayaran pembayaran) {
        this.pembayaran = pembayaran;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    public void setListProduk(ArrayList<ProdukDigital> listProduk) {
        this.listProduk = listProduk;
        hitungTotal();
    }

    public ArrayList<ProdukDigital> getListProduk() {
        return listProduk;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getPPN() {
        return ppn;
    }

    public double getTotalTagihan() {
        return totalTagihan;
    }

    private String rupiah(double nilai) {
        return "Rp " + Math.round(nilai);
    }

    private String buatTeksInvoice() {
        String garisTebal = "===============================================================\n";
        String garisTipis = "---------------------------------------------------------------\n";
        String teks = "";

        teks = teks + garisTebal;
        teks = teks + "        Terima kasih atas transaksi terbarumu di Steam\n";
        teks = teks + garisTebal;
        teks = teks + "Item di bawah ini telah ditambahkan ke Perpustakaan Steam-mu.\n\n";

        if (pembeli != null) {
            teks = teks + "POIN STEAM-MU: " + pembeli.getPoinSteam() + "\n\n";
        }

        teks = teks + garisTipis;
        for (ProdukDigital p : listProduk) {
            double itemPPN = p.hitungPPN(p.getHarga());
            teks = teks + p.getJudul() + "\n";
            teks = teks + "   Subtotal (tanpa PPN)         : " + rupiah(p.getHarga()) + "\n";
            teks = teks + "   PPN di " + ProdukDigital.PERSEN_PPN + "%                   : " + rupiah(itemPPN) + "\n";
            teks = teks + "   Total                        : " + rupiah(p.getHarga() + itemPPN) + "\n\n";
        }
        teks = teks + garisTipis;
        if (pembeli != null) {
            teks = teks + "Username                        : " + pembeli.getUsername() + "\n";
        }
        teks = teks + "Invoice No                      : " + invoiceNo + "\n";
        teks = teks + "Tanggal dibuat                  : " + tanggal + "\n";
        teks = teks + garisTipis;
        teks = teks + "Subtotal (tanpa PPN)            : " + rupiah(subtotal) + "\n";
        teks = teks + "PPN di " + ProdukDigital.PERSEN_PPN + "%                      : " + rupiah(ppn) + "\n";
        teks = teks + "Total                           : " + rupiah(totalTagihan) + "\n";
        teks = teks + garisTipis;
        if (pembeli != null) {
            teks = teks + "Pesanan ini dipesan dari alamat IP:\n";
            teks = teks + pembeli.getAlamatIP() + "\n";
            teks = teks + pembeli.getLokasi() + "\n";
        }
        teks = teks + garisTipis;
        teks = teks + penerbit + "\n";
        teks = teks + alamat + "\n";
        teks = teks + "ID PPN: " + IDPPN + "\n";
        teks = teks + garisTipis;
        if (pembeli != null && pembayaran != null) {
            teks = teks + "Username                        : " + pembeli.getUsername() + "\n";
            teks = teks + "Metode Pembayaran               : " + pembayaran.getMetodePembayaran() + "\n";
            teks = teks + "Total Transaksimu               : " + rupiah(totalTagihan) + "\n";
        }
        teks = teks + garisTebal;
        return teks;
    }

    @Override
    public void tampilkanInvoice() {
        System.out.print(buatTeksInvoice());
    }

    public void simpanKeFile(String namaFile) {
        try {
            FileWriter fw = new FileWriter(namaFile);
            fw.write(buatTeksInvoice());
            fw.close();
            System.out.println(">> [SUCCESS] Invoice berhasil disimpan ke file: " + namaFile);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis ke file: " + namaFile);
            e.printStackTrace();
        }
    }

    public static void bacaDariFile(String namaFile) {
        try {
            File file = new File(namaFile);
            Scanner reader = new Scanner(file);
            System.out.println("\n-------- MEMBACA ISI FILE (" + namaFile + ") --------");
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                System.out.println(line);
            }
            System.out.println("----------------------- AKHIR DARI FILE -----------------------\n");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File '" + namaFile + "' tidak ditemukan!");
        }
    }
}