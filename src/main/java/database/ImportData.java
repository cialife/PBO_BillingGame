package main.java.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import main.java.model.DLC;
import main.java.model.Developer;
import main.java.model.Game;

// Dijalankan SEKALI untuk memindahkan isi game.txt dan dlc.txt ke database
public class ImportData {

    public static void main(String[] args) {
        ArrayList<Developer> daftarDeveloper = new ArrayList<>();

        System.out.println(">> Game berhasil diimpor  : " + imporGame(daftarDeveloper));
        // game.txt harus sudah diimpor lebih dulu karena DLC membutuhkan base game
        System.out.println(">> DLC berhasil diimpor   : " + imporDLC(daftarDeveloper));
    }

    // game.txt -> tabel developer, produk_digital, game
    private static int imporGame(ArrayList<Developer> daftarDeveloper) {
        int sukses = 0;
        try (Scanner reader = new Scanner(new File("game.txt"))) {
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (baris.isEmpty()) {
                    continue;
                }
                String[] d = baris.split(";");
                if (d.length != 6) {
                    System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    continue;
                }
                try {
                    Developer dev = DeveloperDAO.cariAtauBuat(daftarDeveloper, d[2].trim());
                    Game g = new Game(d[0].trim(), d[1].trim(), dev, Double.parseDouble(d[3].trim()),
                            d[4].trim(), Double.parseDouble(d[5].trim()));
                    if (GameDAO.simpanGame(g)) {
                        sukses++;
                    }
                } catch (IllegalArgumentException e) { // termasuk NumberFormatException
                    System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran tidak valid): " + baris);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(">> game.txt tidak ditemukan, dilewati.");
        }
        return sukses;
    }

    // dlc.txt -> tabel produk_digital, game, dlc (kolom terakhir = judul base game)
    private static int imporDLC(ArrayList<Developer> daftarDeveloper) {
        int sukses = 0;
        try (Scanner reader = new Scanner(new File("dlc.txt"))) {
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (baris.isEmpty()) {
                    continue;
                }
                String[] d = baris.split(";");
                if (d.length != 8) {
                    System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    continue;
                }
                try {
                    Developer dev = DeveloperDAO.cariAtauBuat(daftarDeveloper, d[2].trim());
                    Game base = new Game("", d[7].trim(), dev, 0, "", 0); // kode kosong -> dicari lewat judul
                    DLC dlc = new DLC(d[0].trim(), d[1].trim(), dev, Double.parseDouble(d[3].trim()),
                            d[4].trim(), Double.parseDouble(d[5].trim()), d[6].trim(), base);
                    if (DLCDAO.simpanDLC(dlc)) {
                        sukses++;
                    }
                } catch (IllegalArgumentException e) { // termasuk NumberFormatException
                    System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran tidak valid): " + baris);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(">> dlc.txt tidak ditemukan, dilewati.");
        }
        return sukses;
    }
}