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
        for (ProdukDigital produk : listProduk) {
            this.subtotal += produk.getHarga();
        }
        if (!listProduk.isEmpty()) {
            this.ppn = listProduk.get(0).hitungPPN(this.subtotal);
        } else {
            this.ppn = 0;
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

    @Override
    public void tampilkanInvoice() {
        System.out.println("===============================================================");
        System.out.println("        Terima kasih atas transaksi terbarumu di Steam");
        System.out.println("===============================================================");
        System.out.println("Item di bawah ini telah ditambahkan ke Perpustakaan Steam-mu.\n");
        
        if (pembeli != null) {
            System.out.println("POIN STEAM-MU: " + pembeli.getPoinSteam() + "\n");
        }

        System.out.println("---------------------------------------------------------------");
        for (ProdukDigital p : listProduk) {
            System.out.println(p.getJudul());
            double itemPPN = p.hitungPPN(p.getHarga());
            System.out.printf("   Subtotal (tanpa PPN)         : Rp %,.0f\n", p.getHarga());
            System.out.printf("   PPN di 11%%                   : Rp %,.0f\n", itemPPN);
            System.out.printf("   Total                        : Rp %,.0f\n\n", (p.getHarga() + itemPPN));
        }
        System.out.println("---------------------------------------------------------------");
        if (pembeli != null) {
            System.out.println("Username                        : " + pembeli.getUsername());
        }
        System.out.println("Invoice No                      : " + invoiceNo);
        System.out.println("Tanggal dibuat                  : " + tanggal);
        System.out.println("---------------------------------------------------------------");
        System.out.printf("Subtotal (tanpa PPN)            : Rp %,.0f\n", subtotal);
        System.out.printf("PPN di 11%%                      : Rp %,.0f\n", ppn);
        System.out.printf("Total                           : Rp %,.0f\n", totalTagihan);
        System.out.println("---------------------------------------------------------------");
        if (pembeli != null) {
            System.out.println("Pesanan ini dipesan dari alamat IP:");
            System.out.println(pembeli.getAlamatIP());
            System.out.println(pembeli.getLokasi());
        }
        System.out.println("---------------------------------------------------------------");
        System.out.println(penerbit);
        System.out.println(alamat);
        System.out.println("ID PPN: " + IDPPN);
        System.out.println("---------------------------------------------------------------");
        if (pembeli != null && pembayaran != null) {
            System.out.println("Username                        : " + pembeli.getUsername());
            System.out.println("Metode Pembayaran               : " + pembayaran.getMetodePembayaran());
            System.out.printf("Total Transaksimu               : Rp %,.0f\n", totalTagihan);
        }
        System.out.println("===============================================================");
    }

    public void simpanKeFile(String namaFile) {
        try (FileWriter writer = new FileWriter(namaFile)) {
            writer.write("===============================================================\n");
            writer.write("        Terima kasih atas transaksi terbarumu di Steam\n");
            writer.write("===============================================================\n");
            writer.write("Item di bawah ini telah ditambahkan ke Perpustakaan Steam-mu.\n\n");

            if (pembeli != null) {
                writer.write("POIN STEAM-MU: " + pembeli.getPoinSteam() + "\n\n");
            }

            writer.write("---------------------------------------------------------------\n");
            for (ProdukDigital p : listProduk) {
                writer.write(p.getJudul() + "\n");
                double itemPPN = p.hitungPPN(p.getHarga());
                writer.write(String.format("   Subtotal (tanpa PPN)         : Rp %,.0f\n", p.getHarga()));
                writer.write(String.format("   PPN di 11%%                   : Rp %,.0f\n", itemPPN));
                writer.write(String.format("   Total                        : Rp %,.0f\n\n", (p.getHarga() + itemPPN)));
            }
            writer.write("---------------------------------------------------------------\n");
            if (pembeli != null) {
                writer.write("Username                        : " + pembeli.getUsername() + "\n");
            }
            writer.write("Invoice No                      : " + invoiceNo + "\n");
            writer.write("Tanggal dibuat                  : " + tanggal + "\n");
            writer.write("---------------------------------------------------------------\n");
            writer.write(String.format("Subtotal (tanpa PPN)            : Rp %,.0f\n", subtotal));
            writer.write(String.format("PPN di 11%%                      : Rp %,.0f\n", ppn));
            writer.write(String.format("Total                           : Rp %,.0f\n", totalTagihan));
            writer.write("---------------------------------------------------------------\n");
            if (pembeli != null) {
                writer.write("Pesanan ini dipesan dari alamat IP:\n");
                writer.write(pembeli.getAlamatIP() + "\n");
                writer.write(pembeli.getLokasi() + "\n");
            }
            writer.write("---------------------------------------------------------------\n");
            writer.write(penerbit + "\n");
            writer.write(alamat + "\n");
            writer.write("ID PPN: " + IDPPN + "\n");
            writer.write("---------------------------------------------------------------\n");
            if (pembeli != null && pembayaran != null) {
                writer.write("Username                        : " + pembeli.getUsername() + "\n");
                writer.write("Metode Pembayaran               : " + pembayaran.getMetodePembayaran() + "\n");
                writer.write(String.format("Total Transaksimu               : Rp %,.0f\n", totalTagihan));
            }
            writer.write("===============================================================\n");
            System.out.println(">> [SUCCESS] Invoice berhasil disimpan ke file: " + namaFile);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis ke file: " + e.getMessage());
        }
    }

    public static void bacaDariFile(String namaFile) {
        File file = new File(namaFile);
        try (Scanner reader = new Scanner(file)) {
            System.out.println("\n-------- MEMBACA ISI FILE (" + namaFile + ") --------");
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                System.out.println(line);
            }
            System.out.println("----------------------- AKHIR DARI FILE -----------------------\n");
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File '" + namaFile + "' tidak ditemukan!");
        }
    }
}