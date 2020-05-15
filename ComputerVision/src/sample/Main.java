package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class Main {

    public static void main(String[] args){
        LaunchConfiguration(args);
        Application.launch(MainMenu.class, args);
    }

    public static void LaunchConfiguration(String[] args){
        String path = System.getProperty("user.home") +
                File.separator+ "WellLogAnalysis" +
                File.separator+"config.properties";
        File config = new File(path);
        config.getParentFile().mkdirs();
        ConfigurationController.setConfigPath(path);
        Application.launch(Configuration.class, args);
    }
}
