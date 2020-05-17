package sample;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.HashSet;

public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /*
     * Get the extension of a file.
     */
    public static boolean confirmType(File file, HashSet<String> filter) {
        String extension = getExtension(file);
        if (filter.contains(extension))
            return true;
        return false;
    }

    /*
     * Get the extension of a file.
     */
    public static boolean confirmType(String filepath, HashSet<String> filter) {
        File file = new File(filepath);
        String extension = getExtension(file);
        if (filter.contains(extension))
            return true;
        return false;
    }

    public void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    public Rectangle getRect(Point2D p1, Point2D p2) {
        return new Rectangle(
                Math.min(p1.getX(), p2.getX()),
                Math.min(p1.getY(), p2.getY()),
                Math.abs(p1.getX() - p2.getX()),
                Math.abs(p1.getY() - p2.getY())
        );
    }

}
