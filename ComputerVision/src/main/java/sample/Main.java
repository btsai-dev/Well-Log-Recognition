package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Utility.ImageUtils;

import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /**
        File fileFullImage = new File("C:\\Users\\godon\\__LMAOOO\\001-5032Z.png");
        HashSet<String> keywords = new HashSet<>();
        keywords.add("OFFICE USE ONLY");
        keywords.add("DRILLER");
        AnalysisKeywords.analyze(fileFullImage, keywords);
         */
        LaunchMainMenu();
    }

    public void LaunchMainMenu() throws IOException {
        Stage menuStage = new Stage();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/MainMenu.fxml"
                )
        );
        menuStage.setTitle("Main Menu");
        menuStage.setScene(
                new Scene(
                        loader.load()
                )
        );
        /**
        Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
        menuStage.setTitle("Main Menu");
        menuStage.setScene(new Scene(root));
         **/
        menuStage.setMaximized(true);
        menuStage.setMinWidth(800);
        menuStage.setMinHeight(600);
        Controller.mainStage = menuStage;
        menuStage.showAndWait();
    }

    /**
     * Launches the configuration window
     * @throws IOException
     */
    public void LaunchConfiguration() throws IOException {
        Stage configWindow = new Stage();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Configurator.fxml"));
            configWindow.setTitle("Configuration");
            configWindow.setScene(new Scene(root, 600, 400));
            configWindow.setResizable(false);
            configWindow.showAndWait();
        } catch(IOException e){
            System.out.println(e.getStackTrace());
            System.exit(0);
        } catch(Exception e){
            System.out.println(e.getStackTrace());
            System.exit(0);
        }

        Controller.setImageFilter(ImageUtils.jpeg, ImageUtils.jpg, ImageUtils.png, ImageUtils.gif, ImageUtils.tif, ImageUtils.tiff);
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}


