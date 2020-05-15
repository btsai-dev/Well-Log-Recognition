package sample;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import java.util.Stack;

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
        System.out.println(directoryPath);
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
            Controller.defaultPositions = new Stack<DefaultScan>();
            Stage configScanStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("ScanConfigurator.fxml"));
            configScanStage.setTitle("Configure Scan Locations");
            configScanStage.setScene(new Scene(root, 800, 600));
            Controller.mainStage = configScanStage;
            configScanStage.initModality(Modality.APPLICATION_MODAL);
            configScanStage.showAndWait();
        }
    }

    public void executeCropping(ActionEvent actionEvent) throws IOException{
        System.out.println("Beginning Crop actions!");
        if (directoryPath != null && !Controller.defaultPositions.isEmpty()){
            // Load the directory
            File directory = new File(directoryPath);
            File[] directoryListing = directory.listFiles();

            // Load every image
            if (directoryListing != null){
                // Create a large directory to store all the cropped files
                File cropDirectory = new File(directoryPath + "\\Cropped");
                cropDirectory.mkdirs();
                // Delete anything in the directory
                purgeDirectory(cropDirectory);

                for (File file : directoryListing){


                    // If the file is an image
                    if (ImageUtils.confirmType(file, Controller.getImageFilter())){
                        String fname = file.getName();
                        int pos = fname.lastIndexOf(".");
                        if (pos > 0) {
                            fname = fname.substring(0, pos);
                        }

                        System.out.println("Loading file " + fname);

                        String saveFolderPath = directoryPath + "/Cropped/" + fname;
                        File saveDirectory = new File(saveFolderPath);
                        saveDirectory.mkdirs();

                        BufferedImage src = ImageIO.read(file);

                        // Loop through each section
                        int counter = 0;
                        for (DefaultScan section : Controller.defaultPositions) {
                            System.out.println("Going through a section!");
                            Point2D[] points = section.returnCornersImageReference();
                            Rectangle rect = getRect(points[0], points[1]);
                            BufferedImage cropped = src.getSubimage((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                            System.out.println(cropped.getWidth());
                            System.out.println("Writing to path");
                            ImageIO.write(cropped, "PNG", new File(saveFolderPath + "/" + counter + ".png"));
                            counter++;
                        }

                    }
                }
            }

            System.out.println("Finished Crop actions!");

        }
    }

    private Rectangle getRect(Point2D p1, Point2D p2) {
        return new Rectangle(
                Math.min(p1.getX(), p2.getX()),
                Math.min(p1.getY(), p2.getY()),
                Math.abs(p1.getX() - p2.getX()),
                Math.abs(p1.getY() - p2.getY())
        );
    }

    private void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }


}
