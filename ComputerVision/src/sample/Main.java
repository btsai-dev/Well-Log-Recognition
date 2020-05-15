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
        // Application.launch(Configuration.class, args);
        // Gets the config file
        String path = getConfigPath();
        System.out.println(path);
        File config = new File(path);
        //if (!config.exists() || config.length() <= 0){
            config.getParentFile().mkdirs();
            ConfigurationController.setConfigPath(getConfigPath());
            Application.launch(Configuration.class, args);
        //}
        Application.launch(MainMenu.class, args);
    }

    public static String getConfigPath() {
        return System.getProperty("user.home")+File.separator+ "WellLogAnalysis"+File.separator+"config.properties";
    }

}
