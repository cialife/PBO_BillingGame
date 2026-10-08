import java.io.File;
import java.io.FileNotFoundException;
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

    private static int bacaAngka(Scanner input, String pesan) {
        int angka = 0;
        boolean isNumeric;
        do {
            isNumeric = true;
            System.out.print(pesan);
            String cek = input.nextLine();
            try {
                angka = Integer.parseInt(cek);
            } catch (NumberFormatException e) {
                System.out.println("Salah input, ulangi lagi!");
                isNumeric = false;
            }
        } while (!isNumeric);
        return angka;
    }

    private static Developer cariAtauBuatDeveloper(ArrayList<Developer> daftarDeveloper, String nama) {
        for (Developer d : daftarDeveloper) {
            if (d.getNamaDeveloper().equals(nama)) {
                return d;
            }
        }
        Developer baru = new Developer();
        baru.setNamaDeveloper(nama);
        daftarDeveloper.add(baru);
        return baru;
    }

    private static ArrayList<Game> muatGame(ArrayList<Developer> daftarDeveloper) {
        ArrayList<Game> daftar = new ArrayList<>();
        try {
            File file = new File(FILE_GAME);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (!baris.equals("")) {
                    String[] d = baris.split(";");
                    if (d.length != 6) {
                        System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    } else {
                        try {
                            double harga = Double.parseDouble(d[3].trim());
                            double ukuran = Double.parseDouble(d[5].trim());
                            Developer dev = cariAtauBuatDeveloper(daftarDeveloper, d[2].trim());
                            daftar.add(new Game(d[0].trim(), d[1].trim(), dev, harga, d[4].trim(), ukuran));
                        } catch (NumberFormatException e) {
                            System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(">> File " + FILE_GAME + " tidak ditemukan di folder program.");
        }
        return daftar;
    }

    private static void tampilkanDaftarGame(ArrayList<Game> daftar) {
        System.out.println("\n---------------------------------------- DAFTAR GAME ----------------------------------------");
        System.out.printf("%-3s %-26s %-20s %-16s %9s %14s%n",
                "No", "Judul", "Developer", "Genre", "Ukuran", "Harga");
        int no = 1;
        for (Game g : daftar) {
            System.out.printf("%-3d %-26s %-20s %-16s %6.1f GB Rp %,11.0f%n",
                    no++, g.getJudul(), g.getDeveloper().getNamaDeveloper(), g.getGenre(), g.getUkuranFile(), g.getHarga());
        }
        System.out.println("---------------------------------------------------------------------------------------------");
    }
    
    private static ArrayList<DLC> muatDLC(ArrayList<Game> daftarGame, ArrayList<Developer> daftarDeveloper) {
        ArrayList<DLC> daftar = new ArrayList<>();
        try {
            File file = new File(FILE_DLC);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (!baris.equals("")) {
                    String[] d = baris.split(";");
                    if (d.length != 8) {
                        System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    } else {
                        try {
                            String kode = d[0].trim();
                            String genre = d[4].trim();
                            String namaBase = d[7].trim();
                            double harga = Double.parseDouble(d[3].trim());
                            double ukuran = Double.parseDouble(d[5].trim());
                            Developer dev = cariAtauBuatDeveloper(daftarDeveloper, d[2].trim());

                            Game baseGame = null;
                            for (Game g : daftarGame) {
                                if (g.getJudul().toLowerCase().equals(namaBase.toLowerCase())) {
                                    baseGame = g;
                                }
                            }
                            if (baseGame == null) {
                                baseGame = new Game("BASE-" + kode, namaBase, dev, 0, genre, 0);
                            }

                            daftar.add(new DLC(kode, d[1].trim(), dev, harga, genre, ukuran, d[6].trim(), baseGame));
                        } catch (NumberFormatException e) {
                            System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(">> File " + FILE_DLC + " tidak ditemukan di folder program.");
        }
        return daftar;
    }

    private static void tampilkanDaftarDLC(ArrayList<DLC> daftar) {
        System.out.println("\n------------------------------------------------------- DAFTAR DLC ------------------------------------------------------------");
        System.out.printf("%-3s %-40s %-25s %-20s %-10s %9s %14s%n",
                "No", "Judul", "Base Game", "Developer", "Jenis", "Ukuran", "Harga");
        int no = 1;
        for (DLC d : daftar) {
            System.out.printf("%-3d %-40s %-25s %-20s %-10s %6.1f GB Rp %,11.0f%n",
                    no++, d.getJudul(), d.getBaseGame().getJudul(), d.getDeveloper().getNamaDeveloper(),
                    d.getJenis(), d.getUkuranFile(), d.getHarga());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
    }
    
    private static ArrayList<Pembeli> muatAkun() {
        ArrayList<Pembeli> daftar = new ArrayList<>();
        try {
            File file = new File(FILE_AKUN);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String[] d = reader.nextLine().split(";");
                if (d.length == 6) {
                    try {
                        daftar.add(new Pembeli(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
                    } catch (NumberFormatException e) {
                        System.out.println("[PERINGATAN] Satu data akun dilewati (poin bukan angka).");
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // File akun belum ada (belum ada yang mendaftar): daftar dibiarkan kosong
        }
        return daftar;
    }

    private static void simpanAkun(ArrayList<Pembeli> daftar) {
        try {
            FileWriter fw = new FileWriter(FILE_AKUN);
            for (Pembeli p : daftar) {
                fw.write(p.getAccountID() + ";" + p.getUsername() + ";" + p.getEmail() + ";"
                        + p.getAlamatIP() + ";" + p.getLokasi() + ";" + p.getPoinStream() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan akun.");
            e.printStackTrace();
        }
    }

    private static Pembeli cariAkun(ArrayList<Pembeli> daftar, String username, String email) {
        for (Pembeli p : daftar) {
            if (p.getUsername().toLowerCase().equals(username.toLowerCase())
                    && p.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    private static boolean usernameTerpakai(ArrayList<Pembeli> daftar, String username) {
        for (Pembeli p : daftar) {
            if (p.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static String buatAccountID(int nomor) {
        if (nomor < 10) {
            return "ACC00" + nomor;
        } else if (nomor < 100) {
            return "ACC0" + nomor;
        } else {
            return "ACC" + nomor;
        }
    }

    private static Pembeli prosesAkun(Scanner input, ArrayList<Pembeli> daftarAkun) {
        Pembeli pembeli = null;
        boolean keluar = false;

        while (pembeli == null && !keluar) {
            System.out.println("\n---------------------- AKUN ---------------------");
            System.out.println("1. Daftar Akun");
            System.out.println("2. Masuk");
            System.out.println("0. Keluar");
            int menuAkun = bacaAngka(input, "Pilih Menu: ");

            if (menuAkun == 0) {
                keluar = true;
            } else if (menuAkun == 1) {
                System.out.print("Masukkan Username           : ");
                String username = input.nextLine().trim();
                System.out.print("Masukkan Email              : ");
                String email = input.nextLine().trim();

                if (username.equals("") || email.equals("") || username.contains(";") || email.contains(";")) {
                    System.out.println(">> Username/Email tidak boleh kosong atau mengandung tanda ';'.");
                } else if (usernameTerpakai(daftarAkun, username)) {
                    System.out.println(">> Username sudah terdaftar.");
                } else {
                    System.out.print("Masukkan Alamat IP          : ");
                    String ip = input.nextLine().trim();
                    System.out.print("Masukkan Lokasi Pembelian   : ");
                    String lokasi = input.nextLine().trim();

                    if (ip.contains(";") || lokasi.contains(";")) {
                        System.out.println(">> Alamat IP/Lokasi tidak boleh mengandung tanda ';'.");
                    } else {
                        Pembeli baru = new Pembeli(buatAccountID(daftarAkun.size() + 1), username, email, ip, lokasi, 0);
                        baru.daftarAkun();
                        daftarAkun.add(baru);
                        simpanAkun(daftarAkun);
                        System.out.println(">> Silakan masuk dengan akun Anda.");
                    }
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
        return pembeli;
    }

    private static void jalankanProgram(Scanner input) {
        System.out.println("=================================================");
        System.out.println("      Sistem Pembuatan Invoice Pembelian Game");
        System.out.println("=================================================");

        ArrayList<Developer> daftarDeveloper = new ArrayList<>();
        ArrayList<Pembeli> daftarAkun = muatAkun();

        Pembeli pembeli = prosesAkun(input, daftarAkun);
        if (pembeli == null) {
            System.out.println("Terima kasih.");
            return;
        }

        Invoice invoice = new Invoice();
        invoice.setPembeli(pembeli);

        boolean tambahProduk = true;
        while (tambahProduk) {
            System.out.println("\n------------------- TAMBAH ITEM ------------------");
            System.out.println("1. Tambah Game Utama");
            System.out.println("2. Tambah DLC (Downloadable Content)");
            System.out.println("0. Selesai");
            int pilihan = bacaAngka(input, "Pilih Menu                   : ");

            if (pilihan == 0) {
                if (invoice.getListProduk().isEmpty()) {
                    System.out.println(">> Keranjang masih kosong. Transaksi dibatalkan.");
                    return;
                }
                tambahProduk = false;
            } else if (pilihan == 1) {
                ArrayList<Game> daftarGame = muatGame(daftarDeveloper);
                if (daftarGame.isEmpty()) {
                    System.out.println(">> Tidak ada game yang bisa dipilih.");
                } else {
                    tampilkanDaftarGame(daftarGame);
                    int no = bacaAngka(input, "Pilih nomor game (0 = batal) : ");
                    if (no >= 1 && no <= daftarGame.size()) {
                        Game game = daftarGame.get(no - 1);
                        invoice.tambahProduk(game);
                        System.out.println(">> Game " + game.getJudul() + " berhasil ditambahkan ke keranjang!");
                    } else if (no != 0) {
                        System.out.println(">> Nomor tidak valid.");
                    }
                }
            } else if (pilihan == 2) {
                ArrayList<DLC> daftarDLC = muatDLC(muatGame(daftarDeveloper), daftarDeveloper);
                if (daftarDLC.isEmpty()) {
                    System.out.println(">> Tidak ada DLC yang bisa dipilih.");
                } else {
                    tampilkanDaftarDLC(daftarDLC);
                    int no = bacaAngka(input, "Pilih nomor DLC (0 = batal)  : ");
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

        String metode;
        do {
            System.out.print("Metode Pembayaran            : ");
            metode = input.nextLine().trim();
            if (metode.equals("")) {
                System.out.println("Metode pembayaran tidak boleh kosong!");
            }
        } while (metode.equals(""));
        invoice.setPembayaran(new Pembayaran(metode));

        LocalDateTime waktuSekarang = LocalDateTime.now();
        DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        invoice.setTanggal(waktuSekarang.format(formatTanggal));
        DateTimeFormatter formatNomorInvoice = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String invNo = waktuSekarang.format(formatNomorInvoice);
        invoice.setInvoiceNo(invNo);

        int poinLama = pembeli.getPoinStream();
        int poinDidapat = (int) (invoice.getSubtotal() / 1000);
        pembeli.setPoinStream(poinLama + poinDidapat);

        System.out.println("\nMemproses struk transaksi Anda...\n");
        invoice.tampilkanInvoice();

        System.out.print("\nKonfirmasi pembelian? (y/n): ");
        String konfirmasi = input.nextLine();

        if (konfirmasi.toLowerCase().equals("y")) {
            simpanAkun(daftarAkun);
            System.out.println(">> Pembelian dikonfirmasi. Poin Stream yang didapat: " + poinDidapat);

            System.out.print("\nSimpan Invoice? (y/n): ");
            String opsiSimpan = input.nextLine();

            if (opsiSimpan.toLowerCase().equals("y")) {
                String namaFile = "Invoice_" + invNo + ".txt";
                invoice.simpanKeFile(namaFile);

                System.out.println("");
                System.out.print("Lihat lagi Invoice? (y/n): ");
                String opsiBaca = input.nextLine();

                if (opsiBaca.toLowerCase().equals("y")) {
                    Invoice.bacaDariFile(namaFile);
                }
            }
        } else {
            pembeli.setPoinStream(poinLama);
            System.out.println(">> Pembelian dibatalkan. Poin tidak ditambahkan.");
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        jalankanProgram(input);
        input.close();
    }
}