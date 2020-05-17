package sample;

import java.io.File;

public class ScanProperties {
    private File imageFile;
    private int imageWidth;
    private int imageHeight;
    private double scanRelativeWidth;
    private double scanRelativeHeight;
    private double scanRelativeX1;
    private double scanRelativeY1;
    private double scanRelativeX2;
    private double scanRelativeY2;

    public ScanProperties(File imageFile){
        this.imageFile = imageFile;
    }

    public ScanProperties(File imageFile, int width, int height){
        this.imageFile = imageFile;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public void addScan(int[] boundingBox){
        // Bounding box has format (x,y) in [top-right, top-left, bottom-left, bottom-right]
        scanRelativeX1 = Math.min(boundingBox[0], boundingBox[6]) / (double) imageWidth;
        scanRelativeY1 =  Math.min(boundingBox[1], boundingBox[7]) / (double) imageHeight;
        scanRelativeX2 = Math.max(boundingBox[2], boundingBox[4]) / (double) imageWidth;
        scanRelativeY2 = Math.max(boundingBox[3], boundingBox[5]) / (double) imageHeight;
        scanRelativeWidth = Math.abs(scanRelativeX1 - scanRelativeX2);
        scanRelativeHeight = Math.abs(scanRelativeY1 - scanRelativeY2);
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public double[] compareTo(ScanProperties scan){
        double[] arr = { 100 * Math.abs(scan.scanRelativeX1 - this.scanRelativeX1),
                100 * Math.abs(scan.scanRelativeY1 - this.scanRelativeY1),
                100 * Math.abs(scan.scanRelativeX2 - this.scanRelativeX2),
                100 * Math.abs(scan.scanRelativeY2 - this.scanRelativeY2)
        };
        return arr;
    }
}
