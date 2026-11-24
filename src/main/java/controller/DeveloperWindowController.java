package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
    @FXML
    private TextField searchField;

    private final ObservableList<ProdukDigital> dataProduk = FXCollections.observableArrayList();
    private final ObservableList<Game> dataGameSaya = FXCollections.observableArrayList();
    private final FilteredList<ProdukDigital> dataTersaring = new FilteredList<>(dataProduk, p -> true);

    private Developer developer;

    @FXML
    public void initialize() {
        colKode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKodeProduk()));
        colJudul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJudul()));
        colTipe.setCellValueFactory(c -> new SimpleStringProperty(c.getValue() instanceof DLC ? "DLC" : "Game"));
        colHarga.setCellValueFactory(c -> new SimpleStringProperty(formatRupiah(c.getValue().getHarga())));
        produkTable.setItems(dataTersaring);
        searchField.textProperty().addListener((obs, lama, baru) -> {
            String kata = baru.trim().toLowerCase();
            dataTersaring.setPredicate(p -> kata.isEmpty()
                    || p.getJudul().toLowerCase().contains(kata)
                    || p.getKodeProduk().toLowerCase().contains(kata));
        });

        dlcBaseGameCombo.setItems(dataGameSaya);
        dlcBaseGameCombo.valueProperty().addListener((obs, lama, baru) -> {
            if (baru != null) {
                dlcGenreField.setText(baru.getGenre());
            }
        });
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
        lblDeveloper.setText("Developer: " + developer.getNamaDeveloper()
                + " (" + developer.getUsername() + ")");
        muatProdukSaya();
    }

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
    private void handleUbah() {
        ProdukDigital terpilih = produkTable.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            lblStatus.setText(">> Pilih produk pada tabel terlebih dahulu.");
            return;
        }
        Game g = (Game) terpilih;
        boolean adalahDLC = g instanceof DLC;

        TextField fJudul = new TextField(g.getJudul());
        TextField fHarga = new TextField(String.valueOf((long) g.getHarga()));
        TextField fGenre = new TextField(g.getGenre());
        TextField fUkuran = new TextField(String.valueOf(g.getUkuranFile()));
        TextField fJenis = new TextField(adalahDLC ? ((DLC) g).getJenis() : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.addRow(0, new Label("Judul"), fJudul);
        grid.addRow(1, new Label("Harga"), fHarga);
        grid.addRow(2, new Label("Genre"), fGenre);
        grid.addRow(3, new Label("Ukuran (GB)"), fUkuran);
        if (adalahDLC) {
            grid.addRow(4, new Label("Jenis"), fJenis);
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ubah " + g.getKodeProduk());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> hasil = dialog.showAndWait();
        if (hasil.isEmpty() || hasil.get() != ButtonType.OK) {
            return;
        }

        String judul = fJudul.getText().trim();
        String genre = fGenre.getText().trim();
        String jenis = adalahDLC ? fJenis.getText().trim() : null;
        Double harga = parseAngka(fHarga.getText());
        Double ukuran = parseAngka(fUkuran.getText());
        if (judul.isEmpty() || genre.isEmpty() || (adalahDLC && jenis.isEmpty())) {
            lblStatus.setText(">> Judul, genre, dan jenis tidak boleh kosong.");
            return;
        }
        if (harga == null || ukuran == null) {
            lblStatus.setText(">> Harga dan ukuran harus berupa angka yang valid.");
            return;
        }
        if (GameDAO.ubahProduk(g.getKodeProduk(), judul, harga, genre, ukuran, jenis)) {
            muatProdukSaya();
            lblStatus.setText(">> " + judul + " berhasil diubah.");
        } else {
            lblStatus.setText("[ERROR] Gagal mengubah produk (lihat terminal).");
        }
    }

    @FXML
    private void handleHapus() {
        ProdukDigital terpilih = produkTable.getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            lblStatus.setText(">> Pilih produk pada tabel terlebih dahulu.");
            return;
        }
        String pesan = "Hapus \"" + terpilih.getJudul() + "\"?";
        if (!(terpilih instanceof DLC)) {
            pesan += "\nSemua DLC milik game ini juga akan ikut terhapus.";
        }
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        konfirmasi.setHeaderText(null);
        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isEmpty() || hasil.get() != ButtonType.YES) {
            return;
        }
        if (GameDAO.hapusProduk(terpilih.getKodeProduk())) {
            muatProdukSaya();
            lblStatus.setText(">> " + terpilih.getJudul() + " berhasil dihapus.");
        } else {
            lblStatus.setText("[ERROR] Gagal menghapus produk (lihat terminal).");
        }
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