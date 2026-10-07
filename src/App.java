import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final String FILE_AKUN = "akun.txt";
    private static final String FILE_GAME = "game.txt";
    private static final String FILE_DLC = "dlc.txt";

    private static ArrayList<Game> muatGame() {
        ArrayList<Game> daftar = new ArrayList<>();
        if (!new File(FILE_GAME).exists()) {
            System.out.println(">> File " + FILE_GAME + " tidak ditemukan di folder program.");
            return daftar;
        }
        try (Scanner reader = new Scanner(new File(FILE_GAME))) {
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (baris.isEmpty() || baris.startsWith("#")) {
                    continue;
                }
                String[] d = baris.split(";");
                if (d.length != 6) {
                    System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    continue;
                }
                try {
                    daftar.add(new Game(d[0].trim(), d[1].trim(), d[2].trim(),
                            Double.parseDouble(d[3].trim()), d[4].trim(), Double.parseDouble(d[5].trim())));
                } catch (NumberFormatException e) {
                    System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal membaca " + FILE_GAME + ": " + e.getMessage());
        }
        return daftar;
    }

    private static void tampilkanDaftarGame(ArrayList<Game> daftar) {
        System.out.println("\n---------------------------------------- DAFTAR GAME ----------------------------------------");
        System.out.printf("%-3s %-26s %-20s %-16s %9s %14s%n",
                "No", "Judul", "Publisher", "Genre", "Ukuran", "Harga");
        int no = 1;
        for (Game g : daftar) {
            System.out.printf("%-3d %-26s %-20s %-16s %6.1f GB Rp %,11.0f%n",
                    no++, g.getJudul(), g.getPublisher(), g.getGenre(), g.getUkuranFile(), g.getHarga());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
    }

    private static ArrayList<DLC> muatDLC(ArrayList<Game> daftarGame) {
        ArrayList<DLC> daftar = new ArrayList<>();
        if (!new File(FILE_DLC).exists()) {
            System.out.println(">> File " + FILE_DLC + " tidak ditemukan di folder program.");
            return daftar;
        }
        try (Scanner reader = new Scanner(new File(FILE_DLC))) {
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (baris.isEmpty() || baris.startsWith("#")) {
                    continue;
                }
                String[] d = baris.split(";");
                if (d.length != 8) {
                    System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    continue;
                }
                try {
                    String kode = d[0].trim();
                    String publisher = d[2].trim();
                    String genre = d[4].trim();
                    String namaBase = d[7].trim();

                    Game baseGame = null;
                    for (Game g : daftarGame) {
                        if (g.getJudul().equalsIgnoreCase(namaBase)) {
                            baseGame = g;
                            break;
                        }
                    }
                    if (baseGame == null) {
                        baseGame = new Game("BASE-" + kode, namaBase, publisher, 0, genre, 0);
                    }

                    daftar.add(new DLC(kode, d[1].trim(), publisher, Double.parseDouble(d[3].trim()),
                            genre, Double.parseDouble(d[5].trim()), d[6].trim(), baseGame));
                } catch (NumberFormatException e) {
                    System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Gagal membaca " + FILE_DLC + ": " + e.getMessage());
        }
        return daftar;
    }

    private static void tampilkanDaftarDLC(ArrayList<DLC> daftar) {
        System.out.println("\n------------------------------------------------------- DAFTAR DLC ------------------------------------------------------------");
        System.out.printf("%-3s %-40s %-25s %-20s %-10s %9s %14s%n",
                "No", "Judul", "Base Game", "Publisher", "Jenis", "Ukuran", "Harga");
        int no = 1;
        for (DLC d : daftar) {
            System.out.printf("%-3d %-40s %-25s %-20s %-10s %6.1f GB Rp %,11.0f%n",
                    no++, d.getJudul(), d.getBaseGame().getJudul(), d.getPublisher(),
                    d.getJenis(), d.getUkuranFile(), d.getHarga());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
    }

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

    private static void simpanSemuaAkun(ArrayList<Pembeli> daftar) {
        try (FileWriter writer = new FileWriter(FILE_AKUN)) {
            for (Pembeli p : daftar) {
                writer.write(p.getAccountID() + ";" + p.getUsername() + ";" + p.getEmail() + ";"
                        + p.getAlamatIP() + ";" + p.getLokasi() + ";" + p.getPoinSteam() + "\n");
            }
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

                        String accID = "ACC" + (int) (Math.random() * 1000);
                        Pembeli baru = new Pembeli(accID, username, email, ip, lokasi, 0);
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
                        pembeli = ditemukan;
                    }
                } else {
                    System.out.println(">> Pilihan tidak valid, silakan ulangi.");
                }
            }

            Invoice invoice = new Invoice();
            invoice.setPembeli(pembeli);

            boolean tambahProduk = true;
            while (tambahProduk) {
                System.out.println("\n------------------- TAMBAH ITEM ------------------");
                System.out.println("1. Tambah Game Utama");
                System.out.println("2. Tambah DLC (Downloadable Content)");
                System.out.println("0. Selesai");
                System.out.print("Pilih Menu                   : ");

                int pilihan = Integer.parseInt(input.nextLine());

                if (pilihan == 0) {
                    tambahProduk = false;
                } else if (pilihan == 1) {
                    ArrayList<Game> daftarGame = muatGame();
                    if (daftarGame.isEmpty()) {
                        System.out.println(">> Tidak ada game yang bisa dipilih.");
                    } else {
                        tampilkanDaftarGame(daftarGame);
                        System.out.print("Pilih nomor game (0 = batal) : ");
                        int no = Integer.parseInt(input.nextLine());
                        if (no >= 1 && no <= daftarGame.size()) {
                            Game game = daftarGame.get(no - 1);
                            invoice.tambahProduk(game);
                            System.out.println(">> Game " + game.getJudul() + " berhasil ditambahkan ke keranjang!");
                        } else if (no != 0) {
                            System.out.println(">> Nomor tidak valid.");
                        }
                    }
                } else if (pilihan == 2) {
                    ArrayList<DLC> daftarDLC = muatDLC(muatGame());
                    if (daftarDLC.isEmpty()) {
                        System.out.println(">> Tidak ada DLC yang bisa dipilih.");
                    } else {
                        tampilkanDaftarDLC(daftarDLC);
                        System.out.print("Pilih nomor DLC (0 = batal)  : ");
                        int no = Integer.parseInt(input.nextLine());
                        if (no >= 1 && no <= daftarDLC.size()) {
                            DLC dlc = daftarDLC.get(no - 1);
                            invoice.tambahProduk(dlc);
                            System.out.println(">> DLC " + dlc.getJudul() + " berhasil ditambahkan ke keranjang!");
                        } else if (no != 0) {
                            System.out.println(">> Nomor tidak valid.");
                        }
                    }
                } else {
                    System.out.println(">> Pilihan tidak valid, silakan ulangi.");
                }
            }

            System.out.print("Metode Pembayaran            : ");
            String metode = input.nextLine();
            invoice.setPembayaran(new Pembayaran(metode));

            LocalDateTime waktuSekarang = LocalDateTime.now();
            DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            invoice.setTanggal(waktuSekarang.format(formatTanggal));
            DateTimeFormatter formatTanggalInvoice = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String invNo = waktuSekarang.format(formatTanggalInvoice);
            invoice.setInvoiceNo(invNo);

            int poinDidapat = (int) (invoice.getSubtotal() / 1000);
            pembeli.setPoinSteam(pembeli.getPoinSteam() + poinDidapat);
            simpanSemuaAkun(daftarAkun);
            System.out.println(">> Poin Steam yang didapat dari pembelian ini: " + poinDidapat);

            System.out.println("\nMemproses struk transaksi Anda...\n");
            invoice.tampilkanInvoice();

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