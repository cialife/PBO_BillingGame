package main.java.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.database.AkunDAO;
import main.java.database.DLCDAO;
import main.java.database.GameDAO;
import main.java.model.DLC;
import main.java.model.Developer;
import main.java.model.Game;
import main.java.model.Invoice;
import main.java.model.Pembayaran;
import main.java.model.Pembeli;
import main.java.model.ProdukDigital;
import main.java.view.Main;

public class MainWindowController {
    @FXML
    private Label lblUser;
    @FXML
    private Label lblPoin;

    @FXML
    private TableView<Game> gameTable;
    @FXML
    private TableColumn<Game, String> colGameJudul;
    @FXML
    private TableColumn<Game, String> colGameDeveloper;
    @FXML
    private TableColumn<Game, String> colGameGenre;
    @FXML
    private TableColumn<Game, String> colGameUkuran;
    @FXML
    private TableColumn<Game, String> colGameHarga;

    @FXML
    private TableView<DLC> dlcTable;
    @FXML
    private TableColumn<DLC, String> colDlcJudul;
    @FXML
    private TableColumn<DLC, String> colDlcBase;
    @FXML
    private TableColumn<DLC, String> colDlcDeveloper;
    @FXML
    private TableColumn<DLC, String> colDlcJenis;
    @FXML
    private TableColumn<DLC, String> colDlcUkuran;
    @FXML
    private TableColumn<DLC, String> colDlcHarga;

    @FXML
    private TableView<ProdukDigital> keranjangTable;
    @FXML
    private TableColumn<ProdukDigital, String> colKeranjangJudul;
    @FXML
    private TableColumn<ProdukDigital, String> colKeranjangTipe;
    @FXML
    private TableColumn<ProdukDigital, String> colKeranjangHarga;

    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblPpn;
    @FXML
    private Label lblTotal;
    @FXML
    private TextField metodeField;
    @FXML
    private Label lblStatus;

    // Sumber data untuk TableView
    private final ObservableList<Game> dataGame = FXCollections.observableArrayList();
    private final ObservableList<DLC> dataDLC = FXCollections.observableArrayList();
    private final ObservableList<ProdukDigital> dataKeranjang = FXCollections.observableArrayList();

    private final ArrayList<Developer> daftarDeveloper = new ArrayList<>();
    private ArrayList<Pembeli> daftarAkun;
    private Pembeli pembeli;
    private Invoice invoice;

    @FXML
    public void initialize() {
        colGameJudul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJudul()));
        colGameDeveloper
                .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeveloper().getNamaDeveloper()));
        colGameGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenre()));
        colGameUkuran.setCellValueFactory(c -> new SimpleStringProperty(formatUkuran(c.getValue().getUkuranFile())));
        colGameHarga.setCellValueFactory(c -> new SimpleStringProperty(formatRupiah(c.getValue().getHarga())));
        gameTable.setItems(dataGame);

        colDlcJudul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJudul()));
        colDlcBase.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBaseGame().getJudul()));
        colDlcDeveloper
                .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeveloper().getNamaDeveloper()));
        colDlcJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenis()));
        colDlcUkuran.setCellValueFactory(c -> new SimpleStringProperty(formatUkuran(c.getValue().getUkuranFile())));
        colDlcHarga.setCellValueFactory(c -> new SimpleStringProperty(formatRupiah(c.getValue().getHarga())));
        dlcTable.setItems(dataDLC);

        colKeranjangJudul.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJudul()));
        colKeranjangTipe.setCellValueFactory(c -> {
            ProdukDigital p = c.getValue();
            String tipe = "Produk";
            if (p instanceof DLC) {
                tipe = "DLC";
            } else if (p instanceof Game) {
                tipe = "Game";
            }
            return new SimpleStringProperty(tipe);
        });
        colKeranjangHarga.setCellValueFactory(c -> new SimpleStringProperty(formatRupiah(c.getValue().getHarga())));
        keranjangTable.setItems(dataKeranjang);
    }

    // Dipanggil oleh LoginController setelah berhasil masuk
    public void setPembeli(Pembeli pembeli, ArrayList<Pembeli> daftarAkun) {
        this.pembeli = pembeli;
        this.daftarAkun = daftarAkun;
        lblUser.setText("Masuk sebagai: " + pembeli.getUsername());
        perbaruiPoin();
        muatProduk();
        transaksiBaru();
    }

    private void muatProduk() {
        try {
            ArrayList<Game> daftarGame = GameDAO.muatGame(daftarDeveloper);
            ArrayList<DLC> daftarDLC = DLCDAO.muatDLC(daftarGame, daftarDeveloper);
            dataGame.setAll(daftarGame);
            dataDLC.setAll(daftarDLC);

            if (daftarGame.isEmpty()) {
                lblStatus.setText(">> Tidak ada game yang bisa dipilih (tabel game masih kosong).");
            } else if (daftarDLC.isEmpty()) {
                lblStatus.setText(">> Tidak ada DLC yang bisa dipilih (tabel dlc masih kosong).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblStatus.setText("Status: Koneksi database gagal! " + e.getMessage());
        }
    }

    private void transaksiBaru() {
        invoice = new Invoice();
        invoice.setPembeli(pembeli);
        dataKeranjang.clear();
        metodeField.clear();
        perbaruiRingkasan();
    }

    @FXML
    private void handleTambahGame() {
        Game game = gameTable.getSelectionModel().getSelectedItem();
        if (game == null) {
            lblStatus.setText(">> Pilih salah satu game pada tabel terlebih dahulu.");
            return;
        }
        invoice.tambahProduk(game);
        dataKeranjang.add(game);
        perbaruiRingkasan();
        lblStatus.setText(">> Game " + game.getJudul() + " berhasil ditambahkan ke keranjang!");
    }

    @FXML
    private void handleTambahDLC() {
        DLC dlc = dlcTable.getSelectionModel().getSelectedItem();
        if (dlc == null) {
            lblStatus.setText(">> Pilih salah satu DLC pada tabel terlebih dahulu.");
            return;
        }
        invoice.tambahProduk(dlc);
        dataKeranjang.add(dlc);
        perbaruiRingkasan();
        lblStatus.setText(">> DLC " + dlc.getJudul() + " berhasil ditambahkan ke keranjang!");
    }

    @FXML
    private void handleCheckout() {
        if (invoice.getListProduk().isEmpty()) {
            tampilkanAlert(Alert.AlertType.WARNING, "Keranjang Kosong",
                    "Keranjang masih kosong. Tambahkan game atau DLC terlebih dahulu.");
            return;
        }

        String metode = metodeField.getText().trim();
        if (metode.equals("")) {
            tampilkanAlert(Alert.AlertType.WARNING, "Metode Pembayaran",
                    "Metode pembayaran tidak boleh kosong!");
            return;
        }
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

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Pembelian");
        konfirmasi.setHeaderText("Konfirmasi pembelian?");
        konfirmasi.getDialogPane().setContent(buatAreaTeks(invoice.getTeksInvoice()));
        konfirmasi.setResizable(true);
        ButtonType tombolYa = new ButtonType("Ya, Konfirmasi", ButtonBar.ButtonData.OK_DONE);
        ButtonType tombolBatal = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        konfirmasi.getButtonTypes().setAll(tombolYa, tombolBatal);

        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isPresent() && hasil.get() == tombolYa) {
            prosesKonfirmasi(poinDidapat, invNo);
        } else {
            pembeli.setPoinStream(poinLama);
            lblStatus.setText(">> Pembelian dibatalkan. Poin tidak ditambahkan.");
        }
    }

    private void prosesKonfirmasi(int poinDidapat, String invNo) {
        if (!AkunDAO.updatePoin(pembeli)) {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal Menyimpan",
                    "Gagal menyimpan poin Stream ke database.");
        }
        tampilkanAlert(Alert.AlertType.INFORMATION, "Pembelian Dikonfirmasi",
                "Pembelian dikonfirmasi.\nPoin Stream yang didapat: " + poinDidapat);

        if (tanya("Simpan Invoice", "Simpan Invoice?")) {
            String namaFile = "Invoice_" + invNo + ".txt";
            invoice.simpanKeFile(namaFile);

            if (new File(namaFile).exists()) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Invoice Disimpan",
                        "Invoice berhasil disimpan ke file: " + namaFile);
                if (tanya("Lihat Invoice", "Lihat lagi Invoice?")) {
                    bacaInvoiceDariFile(namaFile);
                }
            } else {
                tampilkanAlert(Alert.AlertType.ERROR, "Gagal Menyimpan",
                        "Gagal menulis ke file: " + namaFile);
            }
        }

        perbaruiPoin();
        transaksiBaru();
        lblStatus.setText(">> Transaksi selesai. Poin Stream yang didapat: " + poinDidapat);
    }

    private void bacaInvoiceDariFile(String namaFile) {
        try {
            String isi = Files.readString(Path.of(namaFile));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Isi File Invoice");
            alert.setHeaderText(namaFile);
            alert.getDialogPane().setContent(buatAreaTeks(isi));
            alert.setResizable(true);
            alert.showAndWait();
        } catch (IOException e) {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal Membaca File",
                    "File '" + namaFile + "' tidak ditemukan atau tidak bisa dibaca!");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) lblStatus.getScene().getWindow();
            Main.tampilkan(stage, "/main/resources/login_form.fxml", "Stream - Login", 420, 460);
        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal", "Gagal membuka form login: " + e.getMessage());
        }
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }

    private void perbaruiPoin() {
        lblPoin.setText("Poin Stream: " + pembeli.getPoinStream());
    }

    private void perbaruiRingkasan() {
        lblSubtotal.setText(formatRupiah(invoice.getSubtotal()));
        lblPpn.setText(formatRupiah(invoice.getPPN()));
        lblTotal.setText(formatRupiah(invoice.getTotalTagihan()));
    }

    private String formatRupiah(double nilai) {
        return String.format("Rp %,.0f", nilai);
    }

    private String formatUkuran(double ukuran) {
        return String.format("%.1f GB", ukuran);
    }

    private TextArea buatAreaTeks(String teks) {
        TextArea area = new TextArea(teks);
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'Monospaced';");
        area.setPrefColumnCount(66);
        area.setPrefRowCount(30);
        return area;
    }

    private boolean tanya(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, pesan, ButtonType.YES, ButtonType.NO);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        Optional<ButtonType> hasil = alert.showAndWait();
        return hasil.isPresent() && hasil.get() == ButtonType.YES;
    }

    private void tampilkanAlert(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}