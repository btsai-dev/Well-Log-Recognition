package louisiana;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Utils {
    public static final String CAUSE_UNKNOWN = "Cause Unknown.";
    public static final String NULL_RESPONSE = "Null Response.";
    public static final String NOT_KNOWN_FORM = "No Valid Forms.";
    public static final String KEYWORD_NOT_FOUND = "Keyword not found.";
    public static final String CROP_FAILURE = "Crop Failure.";
    public static final String CANNOT_READ = "Unable to read selected Directory/File.";
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static File getUserDirectory(){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        return userDirectory;
    }

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

    public static String filter(String input){
        return (input.replaceAll("[^a-zA-Z0-9]", "")).toUpperCase();
    }

    public static int[] arrayFromBounding(JSONArray jsonArray){
        if (jsonArray.length() != 8)
            return null;
        int[] boundingBox = new int[8];
        for (int index = 0; index < 8; index++)
            boundingBox[index] = jsonArray.getInt(index);
        return boundingBox;
    }
    public static Rectangle rectFromBounding(int[] boundingBox){
        if (boundingBox.length != 8)
            return null;
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

    public static Rectangle rectFromBounding(JSONArray jsonArray){
        if (jsonArray.length() != 8)
            return null;
        int[] boundingBox = new int[8];
        for (int index = 0; index < 8; index++)
            boundingBox[index] = jsonArray.getInt(index);
        return rectFromBounding(boundingBox);
    }

    public static void resize(BufferedImage inputImage,
                              String outputImagePath, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);

        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }

    public static File applyCropping(File fullImageFile, Rectangle cropArea, String fileName, double resize){
        final double MIN_SIZE = 60;
        String cachePath = Controller.getCacheDir().getPath();
        String folderName = FilenameUtils.removeExtension(fullImageFile.getName());
        try {
            BufferedImage src = ImageIO.read(fullImageFile);
            // Get Target from the ScanPair
            BufferedImage cropped = src.getSubimage(
                    (int) cropArea.getX(),
                    (int) cropArea.getY(),
                    (int) cropArea.getWidth(),
                    (int) cropArea.getHeight()
            );

            String storePath = String.format("%s/%s/%s.png", cachePath, folderName, fileName);
            File result = new File(storePath);
            result.mkdirs();
            int scaledWidth = (int) (cropped.getWidth() * resize);
            int scaledHeight = (int) (cropped.getHeight() * resize);
            resize(cropped, result.getPath(), scaledWidth, scaledHeight);
            if (cropped.getWidth() < MIN_SIZE){
                double scale = MIN_SIZE / cropped.getWidth();
                scaledWidth = (int) (cropped.getWidth() * scale);
                scaledHeight = (int) (cropped.getHeight() * scale);
                resize(cropped, result.getPath(), scaledWidth, scaledHeight);
                System.out.printf("Wrote to cache, filename %s\n", storePath);
                return result;
            } else if (cropped.getHeight() < MIN_SIZE) {
                double scale = MIN_SIZE / cropped.getHeight();
                scaledWidth = (int) (cropped.getWidth() * scale);
                scaledHeight = (int) (cropped.getHeight() * scale);
                resize(cropped, result.getPath(), scaledWidth, scaledHeight);
                System.out.printf("Wrote to cache, filename %s\n", storePath);
                return result;
            }
            return result;
            /**
            else {
                ImageIO.write(cropped,
                        "PNG",
                        result
                );
                System.out.printf("Wrote to cache, filename %s\n", storePath);
                return result;
            }
             */
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets image dimensions for given file
     * @param imgFile image file
     * @return dimensions of image
     * @throws IOException if the file is not a known image
     */
    public static Dimension getImageDimension(File imgFile) throws IOException {
        int pos = imgFile.getName().lastIndexOf(".");
        if (pos == -1)
            throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
        String suffix = imgFile.getName().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        while(iter.hasNext()) {
            ImageReader reader = iter.next();
            ImageInputStream stream = new FileImageInputStream(imgFile);
            try {
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.dispose();
                stream.close();
            }
        }

        throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }


    public static int levenshteinDistance(
            CharSequence string1, CharSequence string2)
    {
        final int length1 = string1.length();
        final int length2 = string2.length();
        int[][] distance = new int[length1 + 1][length2 + 1];

        for (int i=0; i<=length1; i++)
        {
            distance[i][0] = i;
        }
        for (int j = 0; j <= length2; j++)
        {
            distance[0][j] = j;
        }

        for (int i = 1; i <= length1; i++)
        {
            for (int j = 1; j <= length2; j++)
            {
                final char char1 = string1.charAt(i - 1);
                final char char2 = string2.charAt(j - 1);
                final int offset = (char1 == char2) ? 0 : 1;
                distance[i][j] = Math.min(Math.min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + offset);
            }
        }
        return distance[length1][length2];
    }



}
