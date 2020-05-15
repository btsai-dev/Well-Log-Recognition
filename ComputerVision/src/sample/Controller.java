package sample;

import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.HashSet;

public class Controller {
    private static String configPath = null;
    private static HashMap<String, String> azureData = new HashMap<String, String>();
    private static HashSet<String> imageFilter = new HashSet<String>();

    private static String scanDirectory = null;

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


}
