package main.java.controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.java.database.DeveloperDAO;
import main.java.database.PembeliDAO;
import main.java.model.Developer;
import main.java.model.Pembeli;
import main.java.view.Main;

public class LoginController {
    private static final String PESAN_AKUN_TIDAK_VALID = ">> Data tidak valid. Username 3-30 karakter "
        + "(huruf, angka, _ atau .) dan email harus valid.";

    @FXML
    private ComboBox<String> loginRoleCombo;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private ComboBox<String> daftarRoleCombo;
    @FXML
    private TextField daftarUsernameField;
    @FXML
    private TextField daftarEmailField;
    @FXML
    private PasswordField daftarPasswordField;
    @FXML
    private PasswordField daftarKonfirmasiField;
    @FXML
    private GridPane pembeliFields;
    @FXML
    private TextField daftarIpField;
    @FXML
    private TextField daftarLokasiField;
    @FXML
    private GridPane developerFields;
    @FXML
    private TextField daftarNamaDeveloperField;

    @FXML
    private Label lblStatus;

    @FXML
    public void initialize() {
        aturFormDaftar();
    }

    @FXML
    private void handleGantiRoleDaftar() {
        aturFormDaftar();
    }

    private void aturFormDaftar() {
        boolean developer = "Developer".equals(daftarRoleCombo.getValue());
        pembeliFields.setVisible(!developer);
        pembeliFields.setManaged(!developer);
        developerFields.setVisible(developer);
        developerFields.setManaged(developer);
    }

    @FXML
    private void handleDaftar() {
        String username = daftarUsernameField.getText().trim();
        String email = daftarEmailField.getText().trim();
        String password = daftarPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblStatus.setText(">> Username, email, dan password wajib diisi.");
            return;
        }
        if (!password.equals(daftarKonfirmasiField.getText())) {
            lblStatus.setText(">> Konfirmasi password tidak sama.");
            return;
        }
        try {
            if ("Developer".equals(daftarRoleCombo.getValue())) {
                daftarSebagaiDeveloper(username, email, password);
            } else {
                daftarSebagaiPembeli(username, email, password);
            }
        } catch (SQLException e) {
            tampilkanErrorDatabase(e);
        }
    }

    private void daftarSebagaiPembeli(String username, String email, String password) throws SQLException {
        Pembeli baru = new Pembeli("", username, email, daftarIpField.getText().trim(),
                daftarLokasiField.getText().trim(), 0);
        baru.setPassword(password);

        if (!baru.validate()) {
            lblStatus.setText(PESAN_AKUN_TIDAK_VALID + " Alamat IP harus IPv4 yang valid dan lokasi wajib diisi.");
            return;
        }
        if (PembeliDAO.usernameTerpakai(username)) {
            lblStatus.setText(">> Username sudah terdaftar.");
        } else if (PembeliDAO.emailTerpakai(email)) {
            lblStatus.setText(">> Email sudah terdaftar.");
        } else if (PembeliDAO.tambah(baru)) {
            baru.daftarAkun();
            bersihkanFormDaftar();
            lblStatus.setText(">> Akun pembeli berhasil didaftarkan. Silakan masuk dengan akun Anda.");
        } else {
            lblStatus.setText("[ERROR] Gagal menyimpan akun ke database.");
        }
    }

    private void daftarSebagaiDeveloper(String username, String email, String password) throws SQLException {
        String nama = daftarNamaDeveloperField.getText().trim();
        if (nama.isEmpty()) {
            lblStatus.setText(">> Nama developer wajib diisi.");
            return;
        }

        Developer baru = new Developer("", username, email, nama);
        baru.setPassword(password);

        if (!baru.validate()) {
            lblStatus.setText(PESAN_AKUN_TIDAK_VALID);
            return;
        }
        if (DeveloperDAO.usernameTerpakai(username)) {
            lblStatus.setText(">> Username developer sudah terdaftar.");
        } else if (DeveloperDAO.emailTerpakai(email)) {
            lblStatus.setText(">> Email sudah terdaftar.");
        } else if (DeveloperDAO.namaSudahPunyaAkun(nama)) {
            lblStatus.setText(">> Nama developer tersebut sudah memiliki akun.");
        } else if (DeveloperDAO.tambah(baru)) {
            baru.daftarAkun();
            bersihkanFormDaftar();
            lblStatus.setText(">> Akun developer berhasil didaftarkan. Silakan masuk dengan akun Anda.");
        } else {
            lblStatus.setText("[ERROR] Gagal menyimpan akun developer ke database.");
        }
    }

    private void bersihkanFormDaftar() {
        daftarUsernameField.clear();
        daftarEmailField.clear();
        daftarPasswordField.clear();
        daftarKonfirmasiField.clear();
        daftarIpField.clear();
        daftarLokasiField.clear();
        daftarNamaDeveloperField.clear();
    }

    @FXML
    private void handleMasuk() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText(">> Username dan password wajib diisi.");
            return;
        }

        try {
            if ("Developer".equals(loginRoleCombo.getValue())) {
                Developer ditemukan = DeveloperDAO.masuk(username, password);
                if (ditemukan == null) {
                    gagalMasuk();
                } else {
                    ditemukan.masukAkun();
                    bukaJendelaDeveloper(ditemukan);
                }
            } else {
                Pembeli ditemukan = PembeliDAO.masuk(username, password);
                if (ditemukan == null) {
                    gagalMasuk();
                } else {
                    ditemukan.masukAkun();
                    bukaJendelaUtama(ditemukan);
                }
            }
        } catch (SQLException e) {
            tampilkanErrorDatabase(e);
        }
    }

    private void gagalMasuk() {
        loginPasswordField.clear();
        lblStatus.setText(">> Username atau password salah.");
    }

    private void tampilkanErrorDatabase(SQLException e) {
        e.printStackTrace();
        lblStatus.setText("Status: Koneksi database gagal! " + e.getMessage());
    }

    @FXML
    private void handleKeluar() {
        Platform.exit();
    }

    private void bukaJendelaUtama(Pembeli pembeli) {
        try {
            Stage stage = (Stage) lblStatus.getScene().getWindow();
            FXMLLoader loader = Main.tampilkan(stage, Main.FXML_UTAMA,
                    "Stream - Invoice Pembelian Game", 1100, 650);
            MainWindowController controller = loader.getController();
            controller.setPembeli(pembeli);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka jendela utama: " + e.getMessage()).showAndWait();
        }
    }

    private void bukaJendelaDeveloper(Developer developer) {
        try {
            Stage stage = (Stage) lblStatus.getScene().getWindow();
            FXMLLoader loader = Main.tampilkan(stage, Main.FXML_DEVELOPER,
                    "Stream - Developer", 1100, 650);
            DeveloperWindowController controller = loader.getController();
            controller.setDeveloper(developer);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka jendela developer: " + e.getMessage()).showAndWait();
        }
    }
}