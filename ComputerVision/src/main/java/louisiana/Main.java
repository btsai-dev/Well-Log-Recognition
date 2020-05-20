package louisiana;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage){
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
