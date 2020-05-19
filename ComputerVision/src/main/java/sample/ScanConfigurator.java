package sample;


import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


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
    private Image display;

    private static Point2D initialPoint;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**

        rectangles = new Stack<Rectangle>();
        imageView.setImage(Controller.getScanImageForCropping);
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
                Controller.defaultPositions.push(new DefaultScan(initialPoint, clickedPoint, imageView));
                initialPoint = null;
            }
        });
        imageView.setOnMouseEntered(new EventHandler() {
            @Override
            public void handle(Event me) {
                paneView.getScene().setCursor(Cursor.CROSSHAIR); //Change cursor to hand
            }
        });

        imageView.setOnMouseExited(new EventHandler() {
            @Override
            public void handle(Event me) {
                paneView.getScene().setCursor(Cursor.DEFAULT); //Change cursor to crosshair
            }
        });
         */
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
        Stage stage = (Stage) paneView.getScene().getWindow();
        stage.close();
    }

    public void resetScan(ActionEvent actionEvent){
        while (!rectangles.isEmpty()){
            paneView.getChildren().remove(rectangles.pop());
        }
        Controller.defaultPositions = new Stack<DefaultScan>();
    }

}
