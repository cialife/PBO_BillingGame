import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final String FILE_AKUN = "akun.txt";
    private static ArrayList<Pembeli> muatAkun() {
        ArrayList<Pembeli> daftar = new ArrayList<>();
        File file = new File(FILE_AKUN);
        if (!file.exists()) {
            return daftar;
        }
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String[] d = reader.nextLine().split(";");
                if (d.length == 6) {
                    daftar.add(new Pembeli(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal membaca data akun: " + e.getMessage());
        }
        return daftar;
    }

    private static void simpanAkun(Pembeli p) {
        try (FileWriter writer = new FileWriter(FILE_AKUN, true)) {
            writer.write(p.getAccountID() + ";" + p.getUsername() + ";" + p.getEmail() + ";"
                    + p.getAlamatIP() + ";" + p.getLokasi() + ";" + p.getPoinSteam() + "\n");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan akun: " + e.getMessage());
        }
    }

    private static Pembeli cariAkun(ArrayList<Pembeli> daftar, String username, String email) {
        for (Pembeli p : daftar) {
            if (p.getUsername().equalsIgnoreCase(username) && p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }

    private static boolean usernameTerpakai(ArrayList<Pembeli> daftar, String username) {
        for (Pembeli p : daftar) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println("      Sistem Pembuatan Invoice Pembelian Game");
        System.out.println("=================================================");

        try {
            // 0. Daftar / Masuk Akun
            ArrayList<Pembeli> daftarAkun = muatAkun();
            Pembeli pembeli = null;

            while (pembeli == null) {
                System.out.println("\n---------------------- AKUN ---------------------");
                System.out.println("1. Daftar Akun");
                System.out.println("2. Masuk");
                System.out.println("0. Keluar");
                System.out.print("Pilih Menu: ");
                int menuAkun = Integer.parseInt(input.nextLine());

                if (menuAkun == 0) {
                    System.out.println("Terima kasih.");
                    return;
                } else if (menuAkun == 1) {
                    System.out.print("Masukkan Username           : ");
                    String username = input.nextLine().trim();
                    System.out.print("Masukkan Email              : ");
                    String email = input.nextLine().trim();

                    if (username.isEmpty() || email.isEmpty() || username.contains(";") || email.contains(";")) {
                        System.out.println(">> Username/Email tidak boleh kosong atau mengandung tanda ';'.");
                    } else if (usernameTerpakai(daftarAkun, username)) {
                        System.out.println(">> Username sudah terdaftar.");
                    } else {
                        System.out.print("Masukkan Alamat IP          : ");
                        String ip = input.nextLine();
                        System.out.print("Masukkan Lokasi Pembelian   : ");
                        String lokasi = input.nextLine();
                        System.out.print("Masukkan Poin Steam saat ini: ");
                        int poin = Integer.parseInt(input.nextLine());

                        String accID = "ACC" + (int) (Math.random() * 1000);
                        Pembeli baru = new Pembeli(accID, username, email, ip, lokasi, poin);
                        baru.daftarAkun();
                        simpanAkun(baru);
                        daftarAkun.add(baru);
                        System.out.println(">> Silakan masuk dengan akun Anda.");
                    }
                } else if (menuAkun == 2) {
                    System.out.print("Username                    : ");
                    String username = input.nextLine().trim();
                    System.out.print("Email                       : ");
                    String email = input.nextLine().trim();

                    Pembeli ditemukan = cariAkun(daftarAkun, username, email);
                    if (ditemukan == null) {
                        System.out.println(">> Username atau email salah / akun belum terdaftar.");
                    } else {
                        ditemukan.masukAkun();
                        // IP & lokasi bisa berbeda tiap pembelian, perbarui lewat setter yang sudah ada
                        System.out.print("Masukkan Alamat IP          : ");
                        ditemukan.setAlamatIP(input.nextLine());
                        System.out.print("Masukkan Lokasi Pembelian   : ");
                        ditemukan.setLokasi(input.nextLine());
                        pembeli = ditemukan;
                    }
                } else {
                    System.out.println(">> Pilihan tidak valid, silakan ulangi.");
                }
            }

            // 2. Input Data Pembayaran & Invoice
            System.out.println("\n--------------- INFORMASI TRANSAKSI --------------");
            System.out.print("Metode Pembayaran            : ");
            String metode = input.nextLine();
            Pembayaran pembayaran = new Pembayaran(metode);

            LocalDateTime waktuSekarang = LocalDateTime.now();

            // Format Tanggal untuk dicetak di struk
            DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String tanggal = waktuSekarang.format(formatTanggal);

            // Format 14 digit murni dari waktu
            DateTimeFormatter formatTanggalInvoice = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String invNo = waktuSekarang.format(formatTanggalInvoice);

            System.out.println("Nomor Invoice                : " + invNo);
            System.out.println("Tanggal Pembelian            : " + tanggal);

            // Membuat objek Invoice dengan data yang sudah otomatis
            Invoice invoice = new Invoice(invNo, tanggal, pembeli, pembayaran);

            // 3. Looping Input Produk
            boolean tambahProduk = true;
            while (tambahProduk) {
                System.out.println("\n------------------- TAMBAH ITEM ------------------");
                System.out.println("1. Tambah Game Utama");
                System.out.println("2. Tambah DLC (Downloadable Content)");
                System.out.println("0. Selesai");
                System.out.print("Pilih Menu: ");

                int pilihan = Integer.parseInt(input.nextLine());

                if (pilihan == 0) {
                    tambahProduk = false;
                } else if (pilihan == 1 || pilihan == 2) {
                    System.out.print("Kode Produk                  : ");
                    String kode = input.nextLine();

                    System.out.print("Judul Produk                 : ");
                    String judul = input.nextLine();

                    System.out.print("Publisher                    : ");
                    String publisher = input.nextLine();

                    System.out.print("Harga (Rp)                   : ");
                    double harga = Double.parseDouble(input.nextLine());

                    System.out.print("Genre                        : ");
                    String genre = input.nextLine();

                    System.out.print("Ukuran File (GB)             : ");
                    double ukuran = Double.parseDouble(input.nextLine());

                    if (pilihan == 1) {
                        Game game = new Game(kode, judul, publisher, harga, genre, ukuran);
                        invoice.tambahProduk(game);
                        System.out.println(">> Game berhasil ditambahkan ke keranjang!");
                    } else {
                        System.out.print("Jenis DLC                    : ");
                        String jenisDLC = input.nextLine();

                        System.out.print("Base Game                    : ");
                        String namaBase = input.nextLine();

                        Game baseGame = new Game("BASE-" + kode, namaBase, publisher, 0, genre, 0);
                        DLC dlc = new DLC(kode, judul, publisher, harga, genre, ukuran, jenisDLC, baseGame);

                        invoice.tambahProduk(dlc);
                        System.out.println(">> DLC berhasil ditambahkan ke keranjang!");
                    }
                } else {
                    System.out.println(">> Pilihan tidak valid, silakan ulangi.");
                }
            }

            // 4. Tampilkan Invoice di Konsol
            System.out.println("\nMemproses struk transaksi Anda...\n");
            invoice.tampilkanInvoice();

            // 5. Opsi I/O File (Simpan & Baca)
            String namaFile = "Invoice_" + invNo + ".txt";

            System.out.print("\nSimpan Invoice? (y/n): ");
            String opsiSimpan = input.nextLine();

            if (opsiSimpan.equalsIgnoreCase("y")) {
                invoice.simpanKeFile(namaFile);

                System.out.print("Lihat lagi Invoice? (y/n): ");
                String opsiBaca = input.nextLine();

                if (opsiBaca.equalsIgnoreCase("y")) {
                    Invoice.bacaDariFile(namaFile);
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Input gagal! Format angka tidak valid.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Terjadi kesalahan: " + e.getMessage());
        } finally {
            input.close();
        }
    }
}