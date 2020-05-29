package louisiana.Analysis;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import louisiana.Controller;
import louisiana.Utils;

import java.io.File;
import java.io.IOException;

public class ApplyKeywordCorrections {
    @FXML
    private ImageView DisplayedImageView;

    @FXML
    private Pane DisplayedPane;

    @FXML
    private Button ResetButton;

    @FXML
    private Button SaveButton;

    @FXML
    private Text InfoText;

    private Point2D refInitial;
    private Point2D refFinal;
    private Point2D tarInitial;
    private Point2D tarFinal;
    private Rectangle refRectangle;
    private Rectangle tarRectangle;
    private int status;  // Stage 1: Waiting for refInitial. Stage 2: Waiting for refFinal, etc.

    private File fullImageFile;
    private boolean submittedForReview;

    public Rectangle getRefRectangle(){
        return refRectangle;
    }

    public Rectangle getTarRectangle(){
        return tarRectangle;
    }

    public void initData(File imgFile, String message){
        InfoText.setText(message);
        status = 1;
        fullImageFile = imgFile;
        submittedForReview = false;
        DisplayedImageView.setPreserveRatio(true);
        DisplayedImageView.setImage(new Image(imgFile.toURI().toString()));
        ResetButton.setDisable(true);
        SaveButton.setDisable(true);
        DisplayedImageView.setOnMouseClicked(event -> {
            Point2D clickedPoint = new Point2D(event.getX(), event.getY());
            switch(status){
                case 1:
                    refInitial = clickedPoint;
                    ResetButton.setDisable(false);
                    status ++;
                    break;
                case 2:
                    refFinal = clickedPoint;
                    refRectangle = Utils.getRect(refInitial, refFinal);
                    refRectangle.setFill(Color.rgb(216, 230, 173, 0.5));
                    DisplayedPane.getChildren().add(refRectangle);
                    status ++;
                    break;
                case 3:
                    tarInitial = clickedPoint;
                    status ++;
                    break;
                case 4:
                    tarFinal = clickedPoint;
                    tarRectangle = Utils.getRect(tarInitial, tarFinal);
                    tarRectangle.setFill(Color.rgb(106, 100, 103, 0.5));
                    DisplayedPane.getChildren().add(tarRectangle);
                    SaveButton.setDisable(false);
                    status ++;
                    break;
                case 5:
                    break;

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
        if (status != 5)
            return;

        Stage stage = (Stage) SaveButton.getScene().getWindow();
        stage.close();
    }
    public void ResetButtonPressed(ActionEvent actionEvent)  {
        reset();
    }

    private void reset(){
        refInitial = null;
        refFinal = null;

        tarInitial = null;
        tarFinal = null;

        try {
            DisplayedPane.getChildren().remove(refRectangle);
            DisplayedPane.getChildren().remove(tarRectangle);
        } catch(Exception e){
            e.printStackTrace();
        }
        refRectangle = null;
        tarRectangle = null;
        status = 1;
        SaveButton.setDisable(true);
        ResetButton.setDisable(true);
    }

    public boolean submittedForReview(){
        return submittedForReview;
    }

    public void SubmitForReview(ActionEvent actionEvent){
        Controller.submitFileForReview(fullImageFile, "User Request");
        submittedForReview = true;
        reset();
        Stage stage = (Stage) SaveButton.getScene().getWindow();
        stage.close();
    }

}
