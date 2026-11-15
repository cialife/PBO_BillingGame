package main.java.view;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String FXML_LOGIN = "/main/resources/login_form.fxml";
    private static final String CSS = "/main/resources/css/style.css";

    @Override
    public void start(Stage primaryStage) throws Exception {
        tampilkan(primaryStage, FXML_LOGIN, "Stream - Login", 420, 460);
    }

    // Memuat file FXML ke stage yang sama (swap scene) dan mengembalikan loader-nya
    public static FXMLLoader tampilkan(Stage stage, String fxml, String judul,
            double lebar, double tinggi) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
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