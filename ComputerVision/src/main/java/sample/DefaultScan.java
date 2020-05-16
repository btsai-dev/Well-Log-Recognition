package sample;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DefaultScan {
    // Points located are fitted to be between 0 and 1
    Image image;
    double startX;
    double startY;
    double endX;
    double endY;

    public DefaultScan(Point2D p1, Point2D p2, ImageView imageView){
        image = Controller.getScanImageForCropping();
        double aspectRatio = image.getWidth() / image.getHeight();
        double realWidth = Math.min(imageView.getFitWidth(), imageView.getFitHeight() * aspectRatio);
        double realHeight = Math.min(imageView.getFitHeight(), imageView.getFitWidth() / aspectRatio);
        startX = p1.getX() / realWidth;
        startY = p1.getY() / realHeight;
        endX = p2.getX() / realWidth;
        endY = p2.getY() / realHeight;
    }

    public Point2D[] returnCornersImageReference(){
        return new Point2D[]{
                new Point2D(startX * image.getWidth(), startY * image.getHeight()),
                new Point2D(endX * image.getWidth(), endY * image.getHeight())
        };
    }
}
