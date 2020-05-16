package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;

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
        Controller.configPath = path;

        Stage configWindow = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("Configurator.fxml"));
        configWindow.setTitle("Configuration");
        configWindow.setScene(new Scene(root, 600, 400));
        configWindow.setResizable(false);
        configWindow.showAndWait();

        Controller.setImageFilter(ImageUtils.jpeg, ImageUtils.jpg, ImageUtils.png, ImageUtils.gif, ImageUtils.tif, ImageUtils.tiff);
    }

    public void LaunchMainMenu() throws IOException {
        Stage menuStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        menuStage.setTitle("Main Menu");
        menuStage.setScene(new Scene(root, 600, 400));
        menuStage.setResizable(false);
        Controller.mainStage = menuStage;
        menuStage.showAndWait();
    }

    public static void main(String[] args){
        String key = "ffcd9ea1d1104c17b794879fa4262228";
        String endpoint = "https://lsu-frank-tsai.cognitiveservices.azure.com/";
        AnalysisMicrosoftAzure test = new AnalysisMicrosoftAzure("C:\\Users\\godon\\__LMAOOO\\001-5032Z.png", endpoint, key);
        test.analyze();



        final boolean LAUNCH = false;
        if (LAUNCH)
            launch(args);
    }
}
