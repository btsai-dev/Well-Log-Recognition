package louisiana.Classes;

import javafx.scene.shape.Rectangle;

import java.awt.*;

public class ReferenceTargetPair {
    private double refX;
    private double refY;
    private double refW;
    private double refH;
    private double tarXMultiplier;
    private double tarYMultiplier;
    private double tarWMultiplier;
    private double tarHMultiplier;

    public ReferenceTargetPair(Rectangle refRect, Dimension fullDim){
        refX = refRect.getX() / fullDim.width;
        refY = refRect.getY() / fullDim.height;
        refW = refRect.getWidth() / fullDim.width;
        refH = refRect.getHeight() / fullDim.height;
    }

    public ReferenceTargetPair(Rectangle refRect, Rectangle tarRect, Dimension fullDim){
        refX = refRect.getX() / fullDim.width;
        refY = refRect.getY() / fullDim.height;
        refW = refRect.getWidth() / fullDim.width;
        refH = refRect.getHeight() / fullDim.height;

        double x = tarRect.getX() / fullDim.width;
        double y = tarRect.getY() / fullDim.height;
        double w = tarRect.getWidth() / fullDim.width;
        double h = tarRect.getHeight() / fullDim.height;
        tarXMultiplier = (x - refX) / refW;
        tarYMultiplier = (y - refY) / refH;
        tarWMultiplier = w / refW;
        tarHMultiplier = h / refH;
    }

    public Rectangle getReferenceRect(Dimension fullDim){
        Rectangle refRect = new Rectangle();
        double x = refX * fullDim.width;
        double y = refY * fullDim.height;
        double w = refW * fullDim.width;
        double h = refH * fullDim.height;
        refRect.setX((int) x);
        refRect.setY((int) y);
        refRect.setWidth((int) w);
        refRect.setHeight((int) h);
        return refRect;
    }

    public void setTarRect(Rectangle tarRect, Dimension fullDim){
        tarXMultiplier = (tarRect.getX() / fullDim.width - refX) / refW;
        tarYMultiplier = (tarRect.getY() / fullDim.height - refY) / refH;
        tarWMultiplier = (tarRect.getWidth() / fullDim.width) / refW;
        tarHMultiplier = (tarRect.getHeight() / fullDim.height) / refH;
    }

    public Rectangle getTargetRect(Rectangle refRect, Dimension fullDim){
        Rectangle tarRect = new Rectangle();
        double x = refRect.getX() / fullDim.width;
        double y = refRect.getY() / fullDim.height;
        double w = refRect.getWidth() / fullDim.width;
        double h = refRect.getHeight() / fullDim.height;
        tarRect.setX( (int) ( (w * tarXMultiplier + x) * fullDim.width));
        tarRect.setY( (int) ( (h * tarYMultiplier + y) * fullDim.height));
        tarRect.setWidth( (int) ( (w * tarWMultiplier) * fullDim.width));
        tarRect.setHeight( (int) ( (h * tarHMultiplier) * fullDim.height));
        return tarRect;
    }

    public double compareDist(ReferenceTargetPair compPair){
        double x = this.refX;
        double y = this.refY;
        double u = compPair.refX;
        double v = compPair.refY;
        return Math.sqrt( Math.pow(x - u, 2) + Math.pow(y - v, 2));
    }

}
