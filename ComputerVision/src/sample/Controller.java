package sample;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class Controller {
    private static String configPath;
    private static HashMap<String, String> azureData = new HashMap<String, String>();
    private static HashSet<String> imageFilter = new HashSet<String>();
    public static Stage mainStage;
    private static String scanDirectory;
    private static Image scanImage;

    public static Stack<DefaultScan> defaultPositions;

    public static void loadAzureData(String key, String endpoint){
        if (key != null)
            azureData.put("key", key);
        if (endpoint != null)
            azureData.put("endpoint", endpoint);
    }

    public static HashMap extractAzureData(){
        return azureData;
    }

    public static void setConfigPath(String path){
        configPath = path;
    }

    public static String getConfigPath(){
        return configPath;
    }

    public static void setScanDirectory(String path){
        scanDirectory = path;
    }

    public static void setImageFilter(String... args){
        for (String extension : args){
            imageFilter.add(extension);
        }
    }

    public static HashSet getImageFilter(){
        return imageFilter;
    }

    public static void setScanImage(Image image){
        scanImage = image;
    }

    public static Image getScanImage(){
        return scanImage;
    }
}
