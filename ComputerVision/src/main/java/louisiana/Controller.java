package louisiana;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import louisiana.Utils.*;

public class Controller {
    private static HashMap<String, String> azureData = new HashMap<>();
    private static File targetDir;
    private static File reviewDir;
    private static File cacheDir;

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

    public static void setCacheDir(File cacheDir) {
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



}
