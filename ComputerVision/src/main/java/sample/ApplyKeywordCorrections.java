package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class ApplyKeywordCorrections {
    @FXML
    private ImageView DisplayedImageView;

    @FXML
    private Pane DisplayedPane;

    @FXML
    private Button ResetButton;

    @FXML
    private Button SaveButton;

    private Point2D initialPoint;
    private Point2D finalPoint;
    private Rectangle rectangle;
    private Circle initialCircle;
    private File fullImageFile;
    private boolean submittedForReview;

    public void initData(File imgFile, ScanProperties keywordScan, int[] fullDim){
        fullImageFile = imgFile;
        submittedForReview = false;
        DisplayedImageView.setPreserveRatio(true);

        DisplayedImageView.setImage(new Image(imgFile.toURI().toString()));
        ResetButton.setDisable(true);
        SaveButton.setDisable(true);
        Rectangle keywordRect = keywordScan.getRectangle(fullDim[0], fullDim[1]);
        keywordRect.setFill(Color.rgb(230, 173, 216, 0.5));
        DisplayedPane.getChildren().add(keywordRect);
        DisplayedImageView.setOnMouseClicked(event -> {
            Point2D clickedPoint = new Point2D(event.getX(), event.getY());
            if (finalPoint == null){            // If a complete pair has yet to be set
                if (initialPoint == null) {     // If no point has yet to be set
                    initialPoint = clickedPoint;
                    initialCircle = new Circle();
                    initialCircle.setFill(Color.rgb(230, 173, 216, 0.5));
                    initialCircle.setCenterX(initialPoint.getX());
                    initialCircle.setCenterY(initialPoint.getY());
                    initialCircle.setRadius(2);
                    DisplayedPane.getChildren().add(initialCircle);
                    ResetButton.setDisable(false);  // Enable the reset button
                }
                else{                       // If the first point has been set
                    finalPoint = clickedPoint;
                    Rectangle selection = Utils.getRect(initialPoint, finalPoint);
                    selection.setFill(Color.rgb(216, 230, 173, 0.5));
                    DisplayedPane.getChildren().add(selection);
                    DisplayedPane.getChildren().remove(initialCircle);
                    //points.push(initialPoint); Possible implementation of multiple points?
                    //points.push(clickedPoint);
                    rectangle = selection;
                    //Controller.defaultPositions.push(new DefaultScan(initialPoint, clickedPoint, DisplayedImageView));
                    SaveButton.setDisable(false);       // Enable the save button
                }
            }
        });
        DisplayedImageView.setOnMouseEntered(new EventHandler() {
            @Override
            public void handle(Event me) {
                DisplayedPane.getScene().setCursor(Cursor.CROSSHAIR); //Change cursor to hand
            }
        });

        DisplayedImageView.setOnMouseExited(new EventHandler() {
            @Override
            public void handle(Event me) {
                DisplayedPane.getScene().setCursor(Cursor.DEFAULT); //Change cursor to crosshair
            }
        });
    }

    public void SaveButtonPressed(ActionEvent actionEvent)  {
        Stage stage = (Stage) SaveButton.getScene().getWindow();
        stage.close();
    }
    public void ResetButtonPressed(ActionEvent actionEvent)  {
        reset();
    }

    private void reset(){
        initialPoint = null;
        finalPoint = null;
        DisplayedPane.getChildren().remove(rectangle);
        DisplayedPane.getChildren().remove(initialCircle);
        rectangle = null;
        initialCircle = null;
        SaveButton.setDisable(true);
        ResetButton.setDisable(true);
    }
    public boolean submittedForReview(){
        return submittedForReview;
    }

    public ScanProperties getLastScan() throws IOException{
        if (rectangle != null) {
            ScanProperties target = new ScanProperties(fullImageFile);
            target.addScan(rectangle);
            return target;
        } else
            return null;
    }

    public void SubmitForReview(ActionEvent actionEvent){
        Controller.submitFileForReview(fullImageFile);
        submittedForReview = true;
        reset();
        Stage stage = (Stage) SaveButton.getScene().getWindow();
        stage.close();
    }

}
