package sample;

import javafx.stage.Stage;
import sample.Utility.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

public class Controller {
    // Public fields
    public static Stage mainStage;                                      // Stage for the main GUI
    public static String configPath;                                    // Path to the config file
    public static HashSet<String> imageFilter = new HashSet<>();        // Set of allowed image extensions

    // Private fields
    private static HashMap<String, String> azureData = new HashMap<>(); // Microsoft Azure authentication data
    private static HashSet<String> listOfKeywords = new HashSet<>();    // List of keywords to check
    private static File targetAnalysisDirectory;                    // Path to the directory for analysis
    private static File reviewSubmissionDirectory;
    private static File cacheFolder;
    public static File getReviewSubmissionDirectory() {
        return reviewSubmissionDirectory;
    }

    public static void setReviewSubmissionDirectory(File reviewSubmissionDirectory) {
        Controller.reviewSubmissionDirectory = reviewSubmissionDirectory;
    }

    public static void makeCacheFolder(){
        if (reviewSubmissionDirectory != null){
            Utils.purgeDirectory(reviewSubmissionDirectory);
            String saveFolderPath = reviewSubmissionDirectory.toPath() + "/Cache/";
            cacheFolder = new File(saveFolderPath);
            cacheFolder.mkdirs();
            Utils.purgeDirectory(cacheFolder);
        }
    }

    public static File getCacheFolder(){
        return cacheFolder;
    }

    public static void destroyCacheFolder(){
        if (cacheFolder != null){
            Utils.purgeDirectory(cacheFolder);
            cacheFolder.delete();
        }
    }

    /**
     * Add keys and endpoint to the hashmap
     * @param key Microsoft Azure subscription key
     * @param endpoint Microsoft Azure endpoint
     */
    public static void setAzureData(String key, String endpoint) {
        if (key != null) // prevents accidental overriding
            azureData.put("key", key);
        if (endpoint != null) // prevents accidental overriding
            azureData.put("endpoint", endpoint);
    }

    /**
     * Returns the hashmap of authentication details
     * @return
     */
    public static HashMap loadAzureData() {
        return azureData;
    }

    /**
     * Adds whitelisted extensions to the ImageFilter
     * @param args
     */
    public static void setImageFilter(String... args) {
        for (String extension : args) {
            imageFilter.add(extension);
        }
    }

    public static void addKeyword(String... keywords){
        for (String keyword : keywords){
            listOfKeywords.add(keyword);
        }
    }

    public static void clearKeywordList(){
        listOfKeywords.clear();
    }

    public static boolean isInKeywordList(String keyword){
        return listOfKeywords.contains(keyword);
    }

    public static HashSet<String> getListOfKeywords(){
        return listOfKeywords;
    }

    public static File getTargetAnalysisDirectory() {
        return targetAnalysisDirectory;
    }

    public static void setTargetAnalysisDirectory(File targetAnalysisDirectory) {
        Controller.targetAnalysisDirectory = targetAnalysisDirectory;
    }

    public static void submitFileForReview(File source){
        System.out.printf("%s submitted for review.", source.getName());
        String destinationPath = reviewSubmissionDirectory.getPath()
                + "\\"
                + source.getName();
        Path destination = Paths.get(destinationPath);
        Path originalPath = source.toPath();
        try{
            Files.copy(originalPath, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
