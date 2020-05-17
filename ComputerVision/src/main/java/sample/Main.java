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
        //LaunchConfiguration();
        LaunchMainMenu();
    }

    public void LaunchConfiguration() throws IOException {
        String path = System.getProperty("user.home") +
                File.separator+ "WellLogAnalysis" +
                File.separator+"config.properties";
        File config = new File(path);
        config.getParentFile().mkdirs();
        Controller.configPath = path;

        Stage configWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Configurator.fxml"));
        configWindow.setTitle("Configuration");
        configWindow.setScene(new Scene(root, 600, 400));
        configWindow.setResizable(false);
        configWindow.showAndWait();

        Controller.setImageFilter(ImageUtils.jpeg, ImageUtils.jpg, ImageUtils.png, ImageUtils.gif, ImageUtils.tif, ImageUtils.tiff);
    }

    public void LaunchMainMenu() throws IOException {
        Stage menuStage = new Stage();
        // FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainMenu.fxml"));
        menuStage.setTitle("Main Menu");
        menuStage.setScene(new Scene(root, 800, 600));
        //menuStage.setResizable(false);
        menuStage.setMaximized(true);
        menuStage.setMinWidth(800);
        menuStage.setMinHeight(600);
        Controller.mainStage = menuStage;
        menuStage.showAndWait();
    }

    public static void main(String[] args){

        final boolean LAUNCH = true;
        if (LAUNCH)
            launch(args);
    }
}
