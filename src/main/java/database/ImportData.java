package main.java.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import main.java.model.DLC;
import main.java.model.Developer;
import main.java.model.Game;
import main.java.model.Pembeli;

// Dijalankan SEKALI untuk memindahkan isi akun.txt, game.txt, dan dlc.txt ke database
public class ImportData {

    public static void main(String[] args) {
        ArrayList<Developer> daftarDeveloper = new ArrayList<>();
        int sukses;

        // 1. akun.txt -> tabel pembeli
        sukses = 0;
        try {
            Scanner reader = new Scanner(new File("akun.txt"));
            while (reader.hasNextLine()) {
                String[] d = reader.nextLine().split(";");
                if (d.length == 6) {
                    try {
                        Pembeli p = new Pembeli(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5]));
                        if (AkunDAO.tambahAkun(p)) {
                            sukses++;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[PERINGATAN] Akun dilewati (poin bukan angka).");
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(">> akun.txt tidak ditemukan, dilewati.");
        }
        System.out.println(">> Akun berhasil diimpor : " + sukses);

        // 2. game.txt -> tabel developer, produk_digital, game
        sukses = 0;
        try {
            Scanner reader = new Scanner(new File("game.txt"));
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (!baris.equals("")) {
                    String[] d = baris.split(";");
                    if (d.length != 6) {
                        System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    } else {
                        try {
                            Developer dev = GameDAO.cariAtauBuatDeveloper(daftarDeveloper, d[2].trim());
                            Game g = new Game(d[0].trim(), d[1].trim(), dev,
                                    Double.parseDouble(d[3].trim()), d[4].trim(), Double.parseDouble(d[5].trim()));
                            if (GameDAO.simpanGame(g)) {
                                sukses++;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(">> game.txt tidak ditemukan, dilewati.");
        }
        System.out.println(">> Game berhasil diimpor  : " + sukses);

        // 3. dlc.txt -> tabel produk_digital, game, dlc (game.txt harus sudah diimpor
        // lebih dulu)
        sukses = 0;
        try {
            Scanner reader = new Scanner(new File("dlc.txt"));
            while (reader.hasNextLine()) {
                String baris = reader.nextLine().trim();
                if (!baris.equals("")) {
                    String[] d = baris.split(";");
                    if (d.length != 8) {
                        System.out.println("[PERINGATAN] Baris dilewati (format salah): " + baris);
                    } else {
                        try {
                            Developer dev = GameDAO.cariAtauBuatDeveloper(daftarDeveloper, d[2].trim());
                            Game base = new Game("", d[7].trim(), dev, 0, "", 0);
                            DLC dlc = new DLC(d[0].trim(), d[1].trim(), dev, Double.parseDouble(d[3].trim()),
                                    d[4].trim(), Double.parseDouble(d[5].trim()), d[6].trim(), base);
                            if (DLCDAO.simpanDLC(dlc)) {
                                sukses++;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[PERINGATAN] Baris dilewati (harga/ukuran bukan angka): " + baris);
                        }
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(">> dlc.txt tidak ditemukan, dilewati.");
        }
        System.out.println(">> DLC berhasil diimpor   : " + sukses);
    }
}