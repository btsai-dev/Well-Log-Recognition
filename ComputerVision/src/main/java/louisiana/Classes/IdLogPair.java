package louisiana.Classes;

import javafx.scene.shape.Rectangle;

import java.awt.*;

public class IdLogPair {
    private double refXid;
    private double refYid;
    private double refWid;
    private double refHid;
    private double refXlog;
    private double refYlog;
    private double refWlog;
    private double refHlog;

    public IdLogPair(Rectangle id, Rectangle log, Dimension dim){
        refXid = id.getX() / dim.width;
        refYid = id.getY() / dim.height;
        refWid = id.getWidth() / dim.width;
        refHid = id.getHeight() / dim.height;
        refXlog = log.getX() / dim.width;
        refYlog = log.getY() / dim.height;
        refWlog = log.getWidth() / dim.width;
        refHlog = log.getHeight() / dim.height;
    }

    public Rectangle getIdRect(Dimension dim){
        return new Rectangle(
                refXid * dim.width,
                refYid * dim.height,
                refWid * dim.width,
                refHid * dim.height
        );
    }

    public Rectangle getLogRect(Dimension dim){
        return new Rectangle(
                refXlog * dim.width,
                refYlog * dim.height,
                refWlog * dim.width,
                refHlog * dim.height
        );
    }

}
