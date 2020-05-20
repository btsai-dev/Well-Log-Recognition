package louisiana;

import javafx.scene.shape.Rectangle;

import java.io.File;
import java.nio.file.FileSystem;

public class Utils {
    public static final String CAUSE_UNKNOWN = "Cause Unknown.";


    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    public static String filter(String input){
        return (input.replaceAll("[^a-zA-Z0-9]", "")).toUpperCase();
    }

    public static Rectangle rectFromBounding(int[] boundingBox){
        Rectangle rect = new Rectangle();
        // Bounding box has format (x,y) in [top-right, top-left, bottom-left, bottom-right]
        int x1 = Math.min(boundingBox[0], boundingBox[6]);
        int y1 = Math.min(boundingBox[1], boundingBox[7]);
        int x2 = Math.max(boundingBox[2], boundingBox[4]);
        int y2 = Math.max(boundingBox[3], boundingBox[5]);
        rect.setX(x1);
        rect.setY(y1);
        rect.setWidth(Math.abs(x1-x2));
        rect.setHeight(Math.abs(y1-y2));
        return rect;
    }
}
