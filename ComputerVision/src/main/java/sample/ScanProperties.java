package sample;

import javafx.scene.shape.Rectangle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScanProperties {
    private File referencePosImageFile;
    private int imageWidth;
    private int imageHeight;

    private double scanRelativeWidth;
    private double scanRelativeHeight;
    private double scanRelativeX1;
    private double scanRelativeY1;
    private double scanRelativeX2;
    private double scanRelativeY2;

    public ScanProperties(File referencePosImageFile) throws IOException {
        this.referencePosImageFile = referencePosImageFile;
        try {
            BufferedImage bimg = ImageIO.read(referencePosImageFile);
            this.imageWidth = bimg.getWidth();
            this.imageHeight = bimg.getHeight();
        } catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    public ScanProperties(File referencePosImageFile, int width, int height){
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public ScanProperties(int width, int height){
        this.imageWidth = width;
        this.imageHeight = height;
    }

    /**
     * Adds a scan
     * @param boundingBox
     */
    public void addScan(int[] boundingBox){
        // Bounding box has format (x,y) in [top-right, top-left, bottom-left, bottom-right]
        scanRelativeX1 = Math.min(boundingBox[0], boundingBox[6]) / (double) imageWidth;
        scanRelativeY1 =  Math.min(boundingBox[1], boundingBox[7]) / (double) imageHeight;
        scanRelativeX2 = Math.max(boundingBox[2], boundingBox[4]) / (double) imageWidth;
        scanRelativeY2 = Math.max(boundingBox[3], boundingBox[5]) / (double) imageHeight;
        scanRelativeWidth = Math.abs(scanRelativeX1 - scanRelativeX2);
        scanRelativeHeight = Math.abs(scanRelativeY1 - scanRelativeY2);
    }

    /**
     * Adds a scan
     */
    public void addScan(Rectangle rectangle){
        scanRelativeX1 = rectangle.getX() / (double) imageWidth;
        scanRelativeY1 = rectangle.getY() / (double) imageHeight;
        scanRelativeX2 = (rectangle.getX() + rectangle.getWidth()) / (double) imageWidth;
        scanRelativeY2 = (rectangle.getY() + rectangle.getHeight()) / (double) imageHeight;
        scanRelativeWidth = Math.abs(scanRelativeX1 - scanRelativeX2);
        scanRelativeHeight = Math.abs(scanRelativeY1 - scanRelativeY2);
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * Returns percentage results of difference between scans
     * @param scan
     * @return
     */
    public double[] compareTo(ScanProperties scan){
        double[] arr = { 100 * Math.abs(scan.scanRelativeX1 - this.scanRelativeX1),
                100 * Math.abs(scan.scanRelativeY1 - this.scanRelativeY1),
                100 * Math.abs(scan.scanRelativeX2 - this.scanRelativeX2),
                100 * Math.abs(scan.scanRelativeY2 - this.scanRelativeY2)
        };
        return arr;
    }

    public Rectangle getRectangle(int imgWidth, int imgHeight){
         int X = (int) Math.floor(scanRelativeX1 * imgWidth);
         int Y = (int) Math.floor(scanRelativeY1 * imgHeight);
         int width = (int) Math.ceil(scanRelativeWidth * imgWidth);
         int height = (int) Math.ceil(scanRelativeHeight * imgHeight);
         return new Rectangle(X, Y, width, height);
    }

    @Override
    public String toString(){
        return String.format("X1: %f, Y1: %f, X2: %f, Y2: %f",
                scanRelativeX1, scanRelativeY1, scanRelativeX2, scanRelativeY2);
    }
}
