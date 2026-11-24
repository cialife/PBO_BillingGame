package main.java.model;

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
        this();
        setInvoiceNo(invoiceNo);
        setTanggal(tanggal);
        setPembeli(pembeli);
        setPembayaran(pembayaran);
    }

    public Invoice() {
        this.listProduk = new ArrayList<>();
    }

    public void tambahProduk(ProdukDigital produk) {
        if (produk != null) {
            listProduk.add(produk);
            hitungTotal();
        }
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
        this.listProduk = (listProduk == null) ? new ArrayList<>() : listProduk;
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
        return String.format("Rp %,.0f", nilai);
    }

    private String baris(String label, String nilai) {
        return String.format("%-30s : %s\n", label, nilai);
    }

    private String buatTeksInvoice() {
        String garisTebal = "=".repeat(63) + "\n";
        String garisTipis = "-".repeat(63) + "\n";
        StringBuilder teks = new StringBuilder();

        teks.append(garisTebal);
        teks.append("        Terima kasih atas transaksi terbarumu di Stream\n");
        teks.append(garisTebal);
        teks.append("Item di bawah ini telah ditambahkan ke Perpustakaan Stream-mu.\n\n");

        if (pembeli != null) {
            teks.append("POIN STREAM-MU: ").append(pembeli.getPoinStream()).append("\n\n");
        }

        teks.append(garisTipis);
        for (ProdukDigital p : listProduk) {
            double itemPPN = p.hitungPPN(p.getHarga());
            teks.append(p.getJudul()).append("\n");
            teks.append(baris("   Subtotal (tanpa PPN)", rupiah(p.getHarga())));
            teks.append(baris("   PPN di " + ProdukDigital.PERSEN_PPN + "%", rupiah(itemPPN)));
            teks.append(baris("   Total", rupiah(p.getHarga() + itemPPN)));
            teks.append("\n");
        }
        teks.append(garisTipis);
        if (pembeli != null) {
            teks.append(baris("Username", pembeli.getUsername()));
        }
        teks.append(baris("Invoice No", invoiceNo));
        teks.append(baris("Tanggal dibuat", tanggal));
        teks.append(garisTipis);
        teks.append(baris("Subtotal (tanpa PPN)", rupiah(subtotal)));
        teks.append(baris("PPN di " + ProdukDigital.PERSEN_PPN + "%", rupiah(ppn)));
        teks.append(baris("Total", rupiah(totalTagihan)));
        teks.append(garisTipis);
        if (pembeli != null) {
            teks.append("Pesanan ini dipesan dari alamat IP:\n");
            teks.append(pembeli.getAlamatIP()).append("\n");
            teks.append(pembeli.getLokasi()).append("\n");
        }
        teks.append(garisTipis);
        teks.append(PENERBIT).append("\n");
        teks.append(ALAMAT).append("\n");
        teks.append("ID PPN: ").append(IDPPN).append("\n");
        teks.append(garisTipis);
        if (pembeli != null && pembayaran != null) {
            teks.append(baris("Username", pembeli.getUsername()));
            teks.append(baris("Metode Pembayaran", pembayaran.getMetodePembayaran()));
            teks.append(baris("Total Transaksimu", rupiah(totalTagihan)));
        }
        teks.append(garisTebal);
        return teks.toString();
    }

    public String getTeksInvoice() {
        return buatTeksInvoice();
    }

    @Override
    public void tampilkanInvoice() {
        System.out.print(buatTeksInvoice());
    }

    public void simpanKeFile(String namaFile) {
        try (FileWriter fw = new FileWriter(namaFile)) {
            fw.write(buatTeksInvoice());
            System.out.println(">> [SUCCESS] Invoice berhasil disimpan ke file: " + namaFile);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis ke file: " + namaFile);
            e.printStackTrace();
        }
    }

    public static String bacaIsiFile(String namaFile) throws FileNotFoundException {
        StringBuilder isi = new StringBuilder();
        try (Scanner reader = new Scanner(new File(namaFile))) {
            while (reader.hasNextLine()) {
                isi.append(reader.nextLine()).append("\n");
            }
        }
        return isi.toString();
    }

    public static void bacaDariFile(String namaFile) {
        try (Scanner reader = new Scanner(new File(namaFile))) {
            System.out.println("\n-------- MEMBACA ISI FILE (" + namaFile + ") --------");
            while (reader.hasNextLine()) {
                System.out.println(reader.nextLine());
            }
            System.out.println("----------------------- AKHIR DARI FILE -----------------------\n");
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File '" + namaFile + "' tidak ditemukan!");
        }
    }
}