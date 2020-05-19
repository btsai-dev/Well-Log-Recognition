package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

public class ImageUtils {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    /*
     * Get the extension of a file.
     */
    private static String getExtension(File f) {
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

    public static boolean confirmType(File file) {
        String extension = getExtension(file);
        HashSet<String> filter = new HashSet<>();
        filter.add("tiff");
        filter.add("tif");
        filter.add("png");
        filter.add("jpg");
        if (filter.contains(extension))
            return true;
        return false;
    }

    public static boolean convertFormat(String inputImagePath,
                                        String outputImagePath,
                                        String formatName) throws IOException {
        FileInputStream inputStream = new FileInputStream(inputImagePath);
        FileOutputStream outputStream = new FileOutputStream(outputImagePath);

        // reads input image from file
        BufferedImage inputImage = ImageIO.read(inputStream);

        // writes to the output image in specified format
        boolean result = ImageIO.write(inputImage, formatName, outputStream);

        // needs to close the streams
        outputStream.close();
        inputStream.close();

        return result;
    }
}
