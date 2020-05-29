package louisiana.Classes;

import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceTargetPairTest {

    @Test
    void ReferenceTargetIncreaseWidth(){
        int dim1[] = {2000, 500};
        int dim2[] = {2000, 500};
        Rectangle ref = new Rectangle();
        Rectangle tar = new Rectangle();
        ref.setX(200);
        tar.setX(200);
        ref.setY(100);
        tar.setY(100);
        ref.setWidth(300);
        tar.setWidth(700);
        ref.setHeight(500);
        tar.setHeight(500);

        Rectangle newRef = new Rectangle();
        newRef.setX(500);
        newRef.setY(700);
        newRef.setWidth(150);
        newRef.setHeight(350);
    }

    @Test
    void setTarRectTest() {
    }

    @Test
    void getTargetRectTest() {
    }

    @Test
    void compareTest() {
    }
}