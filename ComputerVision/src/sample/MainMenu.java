package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class MainMenu {
    private static String directoryPath;
    private static String filePath;

    @FXML
    private Text ScanDirectory;

    @FXML
    private Text DefaultFile;

    @FXML
    private ImageView ScanImageDisplay;

    public static void setDirectoryPath(String path){
        directoryPath = path;
    }

    public void chooseDirectory(ActionEvent actionEvent){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(userDirectory);
        dc.setTitle("Opening the location..");
        File chosen = dc.showDialog(null);
        if(chosen != null) {
            directoryPath = chosen.getPath();
            ScanDirectory.setText(directoryPath);
        } else {
            directoryPath = null;
        }
    }

    public void chooseImageFile(ActionEvent actionEvent){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(userDirectory);
        fc.setTitle("Opening the location..");
        File chosen = fc.showOpenDialog(null);
        if(chosen != null && ImageUtils.confirmType(chosen, Controller.getImageFilter() )) {
            filePath = chosen.getPath();
            DefaultFile.setText(filePath);
            setImage(filePath);
        } else {
            filePath = null;
        }
    }

    public void setImage(String filePath){
        if (filePath != null) {
            try {
                FileInputStream input = new FileInputStream(filePath);
                Image image = new Image(input);
                ScanImageDisplay.setImage(image);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void configureScan(ActionEvent actionEvent){

    }


}
