package louisiana;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.io.File;

public class DisplayImageTesting {
    @FXML
    private ImageView DisplayedImageView;

    @FXML
    private Pane DisplayedPane;

    public void initData(File imgFile, Rectangle rect1, Rectangle rect2){
        DisplayedImageView.setPreserveRatio(true);
        DisplayedImageView.setImage(new Image(imgFile.toURI().toString()));
        if (rect1 != null)
            DisplayedPane.getChildren().add(rect1);
        if (rect2 != null)
            DisplayedPane.getChildren().add(rect2);
    }

}
