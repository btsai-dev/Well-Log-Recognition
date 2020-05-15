package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class MainMenu {
    private static String directoryPath;
    private static String filePath;

    @FXML
    private Text ScanDirectory;

    @FXML
    private Text DefaultFile;

    @FXML
    private ImageView ScanImageDisplay;

    public void chooseDirectory(ActionEvent actionEvent){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(userDirectory);
        dc.setTitle("Opening the location..");
        File chosen = dc.showDialog(Controller.mainStage);
        if(chosen != null) {
            directoryPath = chosen.getPath();
            ScanDirectory.setText(directoryPath);
        }
    }

    public void chooseImageFile(ActionEvent actionEvent){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(userDirectory);
        fc.setTitle("Opening the location..");
        File chosen = fc.showOpenDialog(Controller.mainStage);
        if(chosen != null && ImageUtils.confirmType(chosen, Controller.getImageFilter() )) {
            filePath = chosen.getPath();
            DefaultFile.setText(filePath);
            setImage(filePath);
        }
    }

    public void setImage(String filePath){
        if (filePath != null) {
            try {
                FileInputStream input = new FileInputStream(filePath);
                Image image = new Image(input);
                ScanImageDisplay.setImage(image);
                Controller.setScanImage(image);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void configureScan(ActionEvent actionEvent) throws IOException {
        if (Controller.getScanImage() != null) {
            Stage configScanStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("ScanConfigurator.fxml"));
            configScanStage.setTitle("Configure Scan Locations");
            configScanStage.setScene(new Scene(root, 800, 600));
            Controller.mainStage = configScanStage;
            configScanStage.initModality(Modality.APPLICATION_MODAL);
            configScanStage.showAndWait();
        }
    }


}
