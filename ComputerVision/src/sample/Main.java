package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        LaunchConfiguration();
        LaunchMainMenu();
    }

    public void LaunchConfiguration() throws IOException {
        String path = System.getProperty("user.home") +
                File.separator+ "WellLogAnalysis" +
                File.separator+"config.properties";
        File config = new File(path);
        config.getParentFile().mkdirs();
        Controller.setConfigPath(path);

        Stage configWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Configurator.fxml"));
        configWindow.setTitle("Configuration");
        configWindow.setScene(new Scene(root, 800, 600));
        configWindow.showAndWait();

        Controller.setImageFilter(ImageUtils.jpeg, ImageUtils.jpg, ImageUtils.png, ImageUtils.gif, ImageUtils.tif, ImageUtils.tiff);
    }

    public void LaunchMainMenu() throws IOException {
        Stage menuStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        menuStage.setTitle("Main Menu");
        menuStage.setScene(new Scene(root, 800, 600));
        menuStage.showAndWait();
    }

    public static void main(String[] args){
        launch(args);
    }
}
