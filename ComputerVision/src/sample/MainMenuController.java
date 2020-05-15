package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class MainMenuController {
    public static String directoryPath;
    public static void setDirectoryPath(String path){
        directoryPath = path;
    }

    public static void chooseDirectory(){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(userDirectory);
        fc.setTitle("Opening the location..");
        File chosen = fc.showOpenDialog(null);
        if(chosen != null) {
            directoryPath = chosen.getPath();
        } else {
            directoryPath = null;
        }
    }


}
