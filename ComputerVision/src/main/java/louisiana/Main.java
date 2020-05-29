package louisiana;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage){
        File targetDir = new File("C:\\Users\\godon\\__LMAOOO\\Well Logs");
        File reviewDir = new File("C:\\Users\\godon\\__LMAOOO\\Well Logs Review");
        Controller.setTargetDir(targetDir);
        Controller.setReviewDir(reviewDir);
        Controller.setCacheDir();
        try {
            Controller.setWriteTo();
        } catch (Exception e){
            e.printStackTrace();
        }
        Controller.initData();
        Execute.executeLouisiana();
    }


    public void loadMainMenu(){
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource(
                        "/MainMenu.fxml"
                )
        );
        Stage window = new Stage();
        window.setTitle("Louisiana Well Log Analysis");
        window.setMaximized(true);
        try {
            window.setScene(new Scene(loader.load()));
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        window.showAndWait();
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}
