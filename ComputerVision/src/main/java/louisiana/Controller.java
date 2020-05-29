package louisiana;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;

import louisiana.Utils.*;

public class Controller {
    private static HashMap<String, String> azureData = new HashMap<>();
    private static HashSet<String> filesReviewed;
    private static File targetDir;
    private static File reviewDir;
    private static File cacheDir;
    private static File writeTo;

    public static void initData(){
        filesReviewed = new HashSet<>();
    }

    public static String getEndpoint() {
        return azureData.get("endpoint");
    }

    public static String getKey(){
        return azureData.get("key");
    }
    public static void setAzureData(String key, String endpoint) {
        if (key != null)
            azureData.put("key", key);
        if (endpoint != null)
            azureData.put("endpoint", endpoint);
    }

    public static File getTargetDir() {
        return targetDir;
    }

    public static void setTargetDir(File targetDir) {
        Controller.targetDir = targetDir;
    }

    public static File getReviewDir() {
        return reviewDir;
    }

    public static void setReviewDir(File reviewDir) {
        Controller.reviewDir = reviewDir;
    }

    public static File getCacheDir() {
        return cacheDir;
    }

    public static void setCacheDir() {
        if (Controller.reviewDir != null){
            Utils.purgeDirectory(Controller.reviewDir);
            String saveFolderPath = reviewDir.toPath() + "/Cache/";
            cacheDir = new File(saveFolderPath);
            cacheDir.mkdirs();
            Utils.purgeDirectory(cacheDir);
        }
    }

    public static void purgeCache(){
        if (Controller.cacheDir != null){
            Utils.purgeDirectory(Controller.cacheDir);
            Controller.cacheDir.delete();
        }
    }

    public static void submitFileForReview(File source, String cause){
        if (filesReviewed.contains(source.getName()))
            return;
        filesReviewed.add(source.getName());
        System.out.printf("%s submitted for review.\n", source.getName());
        if (cause != null)
            System.out.printf("Reason: %s\n", cause);
        String destinationPath = reviewDir.getPath()
                + "\\"
                + source.getName();
        Path destination = Paths.get(destinationPath);
        Path originalPath = source.toPath();
        try{
            Files.copy(originalPath, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public static File getWriteTo() {
        return writeTo;
    }

    public static void setWriteTo() throws IOException {
        if (reviewDir != null){
            String filePath = reviewDir.toPath() + "/results.txt";
            writeTo = new File(filePath);
            writeTo.createNewFile();
            new PrintWriter("filePath").close();
        }

    }

    public static void setWriteTo(File writeTo) {
        Controller.writeTo = writeTo;
    }
}
