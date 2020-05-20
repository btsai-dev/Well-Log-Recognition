package sample.Utility;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.HashSet;

public class Utils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    public final static int MAX_KEYWORD_LENGTH = 50;

    public static String filter(String input){
        return (input.replaceAll("[^a-zA-Z0-9]", "")).toUpperCase();
    }

    /**
     * Gets a file extension File Object
     * @param f File to extract extension from
     * @return
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

    /**
     * Check to see if file type of File Object is whitelisted
     * @param file
     * @param filter
     * @return
     */
    public static boolean fileTypeIsAllowed(File file, HashSet<String> filter) {
        String extension = getExtension(file);
        if (filter.contains(extension))
            return true;
        return false;
    }

    /**
     * Recursively deletes everything within a directory, excluding the directory itself
     * @param dir directory whose children are to be purged
     */
    public static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    /**
     * Creates a rectangle object out of two Point2D objects representing corners
     * @param p1 First corner
     * @param p2 Second corner
     * @return
     */
    public static Rectangle getRect(Point2D p1, Point2D p2) {
        return new Rectangle(
                Math.min(p1.getX(), p2.getX()),
                Math.min(p1.getY(), p2.getY()),
                Math.abs(p1.getX() - p2.getX()),
                Math.abs(p1.getY() - p2.getY())
        );
    }

    /**
     * Returns File Object representation of the userdirectory.
     * @return
     */
    public static File getUserDirectory(){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        return userDirectory;
    }

    /**
     * Converts image file to bytes
     * @return
     * @throws IOException
     */
    private byte[] convertImageToBytes(File img) throws IOException {
        BufferedImage bImage = ImageIO.read(img);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "PNG", bos );
        return bos.toByteArray();
    }

    public static Path getCacheFolder(final FileSystem fs) {
        String osName = System.getProperty("os.name");
        // macOS
        if (osName.toLowerCase().contains("mac")) {
            return fs.getPath(System.getProperty("user.home"), "Library", "Caches");
        }

        // Linux
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return fs.getPath(System.getProperty("user.home"), ".cache");
        }

        // Windows
        if (osName.contains("indows")) {
            return fs.getPath(System.getenv("LOCALAPPDATA"), "Caches");
        }

        // A reasonable fallback
        return fs.getPath(System.getProperty("user.home"), "caches");
    }

}
