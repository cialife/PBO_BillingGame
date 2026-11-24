package main.java.view;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String FXML_LOGIN = "/main/resources/login_form.fxml";
    public static final String FXML_UTAMA = "/main/resources/main_window.fxml";
    public static final String FXML_DEVELOPER = "/main/resources/developer_window.fxml";
    private static final String CSS = "/main/resources/css/style.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        tampilkanLogin(primaryStage);
    }

    public static FXMLLoader tampilkanLogin(Stage stage) throws IOException {
        return tampilkan(stage, FXML_LOGIN, "Stream - Login", 480, 640);
    }

    public static FXMLLoader tampilkan(Stage stage, String fxml, String judul,
            double lebar, double tinggi) throws IOException {
        URL lokasi = Main.class.getResource(fxml);
        if (lokasi == null) {
            throw new IOException("File FXML tidak ditemukan: " + fxml);
        }
        FXMLLoader loader = new FXMLLoader(lokasi);
        Parent root = loader.load();
        Scene scene = new Scene(root, lebar, tinggi);

        URL css = Main.class.getResource(CSS);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle(judul);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
        return loader;
    }

    public static void main(String[] args) {
        launch(args);
    }
}