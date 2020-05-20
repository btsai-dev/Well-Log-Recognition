package louisiana.Classes;

import javafx.scene.shape.Rectangle;

public class ReferenceTargetPair {
    private double refX;
    private double refY;
    private double refW;
    private double refH;
    private double tarXMultiplier;
    private double tarYMultiplier;
    private double tarWMultiplier;
    private double tarHMultiplier;

    public ReferenceTargetPair(Rectangle refRect, int[] fullDim){
        refX = refRect.getX() / fullDim[0];
        refY = refRect.getY() / fullDim[1];
        refW = refRect.getWidth() / fullDim[0];
        refH = refRect.getHeight() / fullDim[1];
    }

    public ReferenceTargetPair(Rectangle refRect, Rectangle tarRect, int[] fullDim){
        refX = refRect.getX() / fullDim[0];
        refY = refRect.getY() / fullDim[1];
        refW = refRect.getWidth() / fullDim[0];
        refH = refRect.getHeight() / fullDim[1];
        tarXMultiplier = (tarRect.getX() / fullDim[0] - refX) / refW;
        tarYMultiplier = (tarRect.getY() / fullDim[1] - refY) / refH;
        tarWMultiplier = (tarRect.getWidth() / fullDim[0]) / refW;
        tarHMultiplier = (tarRect.getHeight() / fullDim[1]) / refH;
    }

    public void setTarRect(Rectangle tarRect, int[] fullDim){
        tarXMultiplier = (tarRect.getX() / fullDim[0] - refX) / refW;
        tarYMultiplier = (tarRect.getY() / fullDim[1] - refY) / refH;
        tarWMultiplier = (tarRect.getWidth() / fullDim[0]) / refW;
        tarHMultiplier = (tarRect.getHeight() / fullDim[1]) / refH;
    }

    public Rectangle getRect(Rectangle refRect, int[] fullDim){
        Rectangle tarRect = new Rectangle();
        double x = refRect.getX() / fullDim[0];
        double y = refRect.getY() / fullDim[1];
        double w = refRect.getWidth() / fullDim[0];
        double h = refRect.getHeight() / fullDim[1];
        tarRect.setX(w * tarXMultiplier + x);
        tarRect.setY(h * tarYMultiplier + y);
        tarRect.setWidth(w * tarWMultiplier);
        tarRect.setHeight(h * tarHMultiplier);
        return tarRect;
    }

    public double compareTo(ReferenceTargetPair compPair){
        double x = this.refX;
        double y = this.refY;
        double u = compPair.refX;
        double v = compPair.refY;
        return Math.sqrt( Math.pow(x - u, 2) + Math.pow(y - v, 2));
    }

}
