package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class ScanConfigurator implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private Pane paneView;

    private static Stack<Point2D> points;
    private static Stack<Rectangle> rectangles;

    private static Point2D initialPoint;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        points = new Stack<Point2D>();

        imageView.setImage(Controller.getScanImage());
        imageView.setOnMouseClicked(event -> {
            Point2D clickedPoint = new Point2D(event.getX(), event.getY());
            if (initialPoint == null){
                initialPoint = clickedPoint;
            } else{
                Rectangle selection = getRect(initialPoint, clickedPoint);
                selection.setFill(Color.rgb(200, 200, 200, 0.5));
                paneView.getChildren().add(selection);
                points.push(initialPoint);
                points.push(clickedPoint);
                rectangles.push(selection);
                initialPoint = null;
            }
        });
    }

    private Rectangle getRect(Point2D p1, Point2D p2) {
        return new Rectangle(
                Math.min(p1.getX(), p2.getX()),
                Math.min(p1.getY(), p2.getY()),
                Math.abs(p1.getX() - p2.getX()),
                Math.abs(p1.getY() - p2.getY())
        );
    }


    public void submitScan(ActionEvent actionEvent){

    }
    public void resetScan(ActionEvent actionEvent){
        while (!rectangles.isEmpty()){
            paneView.getChildren().remove(rectangles.pop());
        }
    }

}
