import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane; // Library untuk pop-out GUI

public class App {
    private static final String FILE_NAME = "data_invoice.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Invoice> daftarInvoice;

        // Muat data dari file saat aplikasi pertama kali dibuka
        daftarInvoice = muatDataDariFile();

        int pilihan;
        do {
            System.out.println("\n=== APLIKASI TOKO PRODUK DIGITAL ===");
            System.out.println("1. Buat Transaksi / Invoice Baru");
            System.out.println("2. Tampilkan Semua Invoice");
            System.out.println("3. Cetak Struk");
            System.out.println("4. Keluar");
            System.out.print("Pilih menu (1-4): ");
            pilihan = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (pilihan) {
                case 1:
                    System.out.print("Masukkan No Invoice: ");
                    String noInv = scanner.nextLine();
                    System.out.print("Masukkan Tanggal (cth: 2026-06-07): ");
                    String tgl = scanner.nextLine();
                    
                    // Input Pembeli (Association)
                    System.out.print("Masukkan Alamat IP Pembeli: ");
                    String ip = scanner.nextLine();
                    System.out.print("Masukkan Lokasi Pembeli: ");
                    String lokasi = scanner.nextLine();
                    Pembeli pembeli = new Pembeli(ip, lokasi, 100);

                    // Input Pembayaran (Association)
                    System.out.print("Masukkan Metode Pembayaran (e.g. Transfer Bank, QRIS): ");
                    String metode = scanner.nextLine();
                    Pembayaran bayar = new Pembayaran(metode);

                    // Buat Invoice Object
                    Invoice invoiceBaru = new Invoice(noInv, tgl, pembeli, bayar);

                    // Input Produk (Inheritance Game extends ProdukDigital)
                    boolean tambahLagi = true;
                    while (tambahLagi) {
                        System.out.print("Masukkan Judul Game: ");
                        String judulGame = scanner.nextLine();
                        System.out.print("Masukkan Publisher: ");
                        String publisher = scanner.nextLine();
                        System.out.print("Masukkan Harga: ");
                        double harga = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Masukkan Genre Game: ");
                        String genre = scanner.nextLine();

                        Game game = new Game("G001", judulGame, publisher, harga, genre, 15.5);
                        invoiceBaru.tambahProduk(game);

                        System.out.print("Tambah produk game lagi dalam invoice ini? (y/n): ");
                        String jawab = scanner.nextLine();
                        if (jawab.equalsIgnoreCase("n")) {
                            tambahLagi = false;
                        }
                    }

                    daftarInvoice.add(invoiceBaru);
                    
                    // Otomatis simpan ke file setiap ada transaksi baru
                    simpanDataKeFile(daftarInvoice);
                    System.out.println("Transaksi berhasil dibuat dan otomatis tersimpan ke file!");
                    break;

                case 2:
                    if (daftarInvoice.isEmpty()) {
                        System.out.println("Belum ada data invoice.");
                    } else {
                        for (Invoice inv : daftarInvoice) {
                            inv.tampilkanInvoice();
                        }
                    }
                    break;

                case 3:
                    // FITUR CETAK STRUK DENGAN PILIHAN & POP-OUT
                    if (daftarInvoice.isEmpty()) {
                        System.out.println("Belum ada data invoice untuk dicetak.");
                        break;
                    }

                    System.out.println("\n--- DAFTAR INVOICE TERSEDIA ---");
                    for (int i = 0; i < daftarInvoice.size(); i++) {
                        System.out.println("[" + (i + 1) + "] No Invoice: " + daftarInvoice.get(i).getInvoiceNo());
                    }

                    System.out.print("Pilih nomor urut invoice yang ingin dicetak struknya: ");
                    int indexPilihan = scanner.nextInt() - 1;
                    scanner.nextLine();

                    if (indexPilihan >= 0 && indexPilihan < daftarInvoice.size()) {
                        Invoice invTerpilih = daftarInvoice.get(indexPilihan);
                        
                        // Ambil string struk dari method milik invoice
                        String teksStruk = invTerpilih.formatStruk();
                        
                        // Tampilkan Pop-out Jendela Grafis
                        JOptionPane.showMessageDialog(null, teksStruk, "Struk Pembelian Produk Digital", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("Struk berhasil dimunculkan sebagai pop-out!");
                    } else {
                        System.out.println("Pilihan nomor invoice tidak valid!");
                    }
                    break;

                case 4:
                    System.out.println("Keluar dari program. Terima kasih!");
                    break;

                default:
                    System.out.println("Pilihan tidak valid!");
            }
        } while (pilihan != 4);

        scanner.close();
    }

    public static void simpanDataKeFile(ArrayList<Invoice> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan data ke file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Invoice> muatDataDariFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ArrayList<Invoice>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Gagal membaca file data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}