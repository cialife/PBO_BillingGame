package main.java.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.database.AkunDAO;
import main.java.model.Pembeli;
import main.java.view.Main;

public class LoginController {
    // Injecting nodes using @FXML based on fx:id markers
    @FXML
    private TextField loginUsernameField;
    @FXML
    private TextField loginEmailField;
    @FXML
    private TextField daftarUsernameField;
    @FXML
    private TextField daftarEmailField;
    @FXML
    private TextField daftarIpField;
    @FXML
    private TextField daftarLokasiField;
    @FXML
    private Label lblStatus;

    private ArrayList<Pembeli> daftarAkun;

    @FXML
    public void initialize() {
        try {
            daftarAkun = AkunDAO.muatAkun();
        } catch (SQLException e) {
            daftarAkun = new ArrayList<>();
            lblStatus.setText("Status: Koneksi database gagal! " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDaftar() {
        String username = daftarUsernameField.getText().trim();
        String email = daftarEmailField.getText().trim();
        String ip = daftarIpField.getText().trim();
        String lokasi = daftarLokasiField.getText().trim();

        if (username.equals("") || email.equals("") || username.contains(";") || email.contains(";")) {
            lblStatus.setText(">> Username/Email tidak boleh kosong atau mengandung tanda ';'.");
        } else if (AkunDAO.usernameTerpakai(daftarAkun, username)) {
            lblStatus.setText(">> Username sudah terdaftar.");
        } else if (ip.contains(";") || lokasi.contains(";")) {
            lblStatus.setText(">> Alamat IP/Lokasi tidak boleh mengandung tanda ';'.");
        } else {
            Pembeli baru = new Pembeli(AkunDAO.buatAccountID(daftarAkun.size() + 1),
                    username, email, ip, lokasi, 0);
            if (AkunDAO.tambahAkun(baru)) {
                baru.daftarAkun();
                daftarAkun.add(baru);
                daftarUsernameField.clear();
                daftarEmailField.clear();
                daftarIpField.clear();
                daftarLokasiField.clear();
                lblStatus.setText(">> Akun berhasil didaftarkan. Silakan masuk dengan akun Anda.");
            } else {
                lblStatus.setText("[ERROR] Gagal menyimpan akun ke database.");
            }
        }
    }

    @FXML
    private void handleMasuk() {
        String username = loginUsernameField.getText().trim();
        String email = loginEmailField.getText().trim();

        Pembeli ditemukan = AkunDAO.cariAkun(daftarAkun, username, email);
        if (ditemukan == null) {
            lblStatus.setText(">> Username atau email salah / akun belum terdaftar.");
        } else {
            ditemukan.masukAkun();
            bukaJendelaUtama(ditemukan);
        }
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }

    private void bukaJendelaUtama(Pembeli pembeli) {
        try {
            Stage stage = (Stage) lblStatus.getScene().getWindow();
            FXMLLoader loader = Main.tampilkan(stage, "/main/resources/main_window.fxml",
                    "Stream - Invoice Pembelian Game", 1100, 650);
            MainWindowController controller = loader.getController();
            controller.setPembeli(pembeli, daftarAkun);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal membuka jendela utama: " + e.getMessage());
            alert.showAndWait();
        }
    }
}