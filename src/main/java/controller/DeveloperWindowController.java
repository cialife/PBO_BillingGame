package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.java.database.DLCDAO;
import main.java.database.GameDAO;
import main.java.model.DLC;
import main.java.model.Developer;
import main.java.model.Game;
import main.java.model.ProdukDigital;
import main.java.view.Main;

public class DeveloperWindowController {
    @FXML
    private Label lblDeveloper;
    @FXML
    private Label lblStatus;

    // Form Game
    @FXML
    private TextField gameKodeField;
    @FXML
    private TextField gameJudulField;
    @FXML
    private TextField gameHargaField;
    @FXML
    private TextField gameGenreField;
    @FXML
    private TextField gameUkuranField;

    // Form DLC
    @FXML
    private ComboBox<Game> dlcBaseGameCombo;
    @FXML
    private TextField dlcKodeField;
    @FXML
    private TextField dlcJudulField;
    @FXML
    private TextField dlcHargaField;
    @FXML
    private TextField dlcGenreField;
    @FXML
    private TextField dlcUkuranField;
    @FXML
    private TextField dlcJenisField;

    // Tabel "Produk Saya"
    @FXML
    private TableView<ProdukDigital> produkTable;
    @FXML
    private TableColumn<ProdukDigital, String> colKode;
    @FXML
    private TableColumn<ProdukDigital, String> colJudul;
    @FXML
    private TableColumn<ProdukDigital, String> colTipe;
    @FXML
    private TableColumn<ProdukDigital, String> colHarga;

    private final ObservableList<ProdukDigital> dataProduk = FXCollections.observableArrayList();
    private final ObservableList<Game> dataGameSaya = FXCollections.observableArrayList();

    private Developer developer;

    @FXML
    public void initialize() {
        colKode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKodeProduk()));
        colJudul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJudul()));
        colTipe.setCellValueFactory(c -> new SimpleStringProperty(c.getValue() instanceof DLC ? "DLC" : "Game"));
        colHarga.setCellValueFactory(c -> new SimpleStringProperty(formatRupiah(c.getValue().getHarga())));
        produkTable.setItems(dataProduk);

        dlcBaseGameCombo.setItems(dataGameSaya);
        dlcBaseGameCombo.setConverter(new StringConverter<Game>() {
            @Override
            public String toString(Game g) {
                return g == null ? "" : g.getJudul();
            }

            @Override
            public Game fromString(String s) {
                return null;
            }
        });
        // Genre DLC otomatis terisi dari base game yang dipilih (masih bisa diubah)
        dlcBaseGameCombo.valueProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                dlcGenreField.setText(baru.getGenre());
            }
        });
    }

    // Dipanggil oleh LoginController setelah developer berhasil masuk
    public void setDeveloper(Developer developer) {
        this.developer = developer;
        lblDeveloper.setText("Developer: " + developer.getNamaDeveloper()
                + " (" + developer.getUsername() + ")");
        muatProdukSaya();
    }

    // Memuat semua game dan DLC, lalu hanya menyimpan milik developer yang sedang
    // masuk
    private void muatProdukSaya() {
        try {
            ArrayList<Developer> cache = new ArrayList<>();
            ArrayList<Game> semuaGame = GameDAO.muatGame(cache);
            ArrayList<DLC> semuaDLC = DLCDAO.muatDLC(semuaGame, cache);

            dataGameSaya.clear();
            dataProduk.clear();
            for (Game g : semuaGame) {
                if (milikSaya(g)) {
                    dataGameSaya.add(g);
                    dataProduk.add(g);
                }
            }
            for (DLC d : semuaDLC) {
                if (milikSaya(d)) {
                    dataProduk.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Status: Koneksi database gagal! " + e.getMessage());
        }
    }

    private boolean milikSaya(ProdukDigital produk) {
        return produk.getDeveloper().getNamaDeveloper().equalsIgnoreCase(developer.getNamaDeveloper());
    }

    @FXML
    private void handleSimpanGame() {
        String kode = gameKodeField.getText().trim();
        String judul = gameJudulField.getText().trim();
        String genre = gameGenreField.getText().trim();

        if (kode.equals("") || judul.equals("") || genre.equals("")) {
            lblStatus.setText(">> Kode, judul, dan genre wajib diisi.");
            return;
        }
        Double harga = parseAngka(gameHargaField.getText());
        if (harga == null) {
            lblStatus.setText(">> Harga harus berupa angka tanpa titik/koma, misalnya 500000.");
            return;
        }
        Double ukuran = parseAngka(gameUkuranField.getText());
        if (ukuran == null) {
            lblStatus.setText(">> Ukuran file harus berupa angka (GB), misalnya 40.5");
            return;
        }
        if (kodeSudahDipakai(kode)) {
            return;
        }

        Game baru = new Game(kode, judul, developer, harga, genre, ukuran);
        if (GameDAO.simpanGame(baru)) {
            gameKodeField.clear();
            gameJudulField.clear();
            gameHargaField.clear();
            gameGenreField.clear();
            gameUkuranField.clear();
            muatProdukSaya();
            lblStatus.setText(">> Game " + judul + " berhasil ditambahkan.");
        } else {
            lblStatus.setText("[ERROR] Gagal menyimpan game ke database (lihat terminal).");
        }
    }

    @FXML
    private void handleSimpanDLC() {
        Game baseGame = dlcBaseGameCombo.getValue();
        String kode = dlcKodeField.getText().trim();
        String judul = dlcJudulField.getText().trim();
        String genre = dlcGenreField.getText().trim();
        String jenis = dlcJenisField.getText().trim();

        if (dataGameSaya.isEmpty()) {
            lblStatus.setText(">> Anda belum punya game. Tambahkan game terlebih dahulu di tab Tambah Game.");
            return;
        }
        if (baseGame == null) {
            lblStatus.setText(">> Pilih base game terlebih dahulu.");
            return;
        }
        if (kode.equals("") || judul.equals("") || genre.equals("") || jenis.equals("")) {
            lblStatus.setText(">> Kode, judul, genre, dan jenis wajib diisi.");
            return;
        }
        Double harga = parseAngka(dlcHargaField.getText());
        if (harga == null) {
            lblStatus.setText(">> Harga harus berupa angka tanpa titik/koma, misalnya 150000.");
            return;
        }
        Double ukuran = parseAngka(dlcUkuranField.getText());
        if (ukuran == null) {
            lblStatus.setText(">> Ukuran file harus berupa angka (GB), misalnya 5.5");
            return;
        }
        if (kodeSudahDipakai(kode)) {
            return;
        }

        DLC baru = new DLC(kode, judul, developer, harga, genre, ukuran, jenis, baseGame);
        if (DLCDAO.simpanDLC(baru)) {
            dlcKodeField.clear();
            dlcJudulField.clear();
            dlcHargaField.clear();
            dlcGenreField.clear();
            dlcUkuranField.clear();
            dlcJenisField.clear();
            muatProdukSaya();
            lblStatus.setText(">> DLC " + judul + " berhasil ditambahkan.");
        } else {
            lblStatus.setText("[ERROR] Gagal menyimpan DLC ke database (lihat terminal).");
        }
    }

    // true jika kode sudah dipakai produk lain (atau database tidak bisa diakses)
    private boolean kodeSudahDipakai(String kode) {
        try {
            if (GameDAO.kodeProdukTerpakai(kode)) {
                lblStatus.setText(">> Kode produk " + kode + " sudah dipakai. Gunakan kode lain.");
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Status: Koneksi database gagal! " + e.getMessage());
            return true;
        }
    }

    // Mengembalikan null jika bukan angka yang valid atau bernilai negatif
    private Double parseAngka(String teks) {
        try {
            double nilai = Double.parseDouble(teks.trim());
            if (Double.isNaN(nilai) || Double.isInfinite(nilai) || nilai < 0) {
                return null;
            }
            return nilai;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatRupiah(double nilai) {
        return String.format("Rp %,.0f", nilai);
    }

    @FXML
    private void handleLogout() {
        try {
            Main.tampilkanLogin((Stage) lblStatus.getScene().getWindow());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form login: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }
}