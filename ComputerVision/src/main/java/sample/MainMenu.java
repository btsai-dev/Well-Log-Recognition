package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Stack;

public class MainMenu {
    private static String directoryPath;
    private static String filePathForImageCropping;
    private static String filePathForImageKeywords;

    @FXML
    private Text DirectoryNameForProcessing;

    @FXML
    private Text NameOfFileSelectedForCropping;

    @FXML
    private ImageView DisplayOfImageForCropping;

    @FXML
    private Text NameOfFileSelectedForKeywords;


    public void chooseDirectoryForProcessing(ActionEvent actionEvent){
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
            DirectoryNameForProcessing.setText(directoryPath);
        }
        System.out.println(directoryPath);
    }

    private File getUserDirectory(){
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        return userDirectory;
    }

    public void chooseImageFileForKeywords(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(getUserDirectory());
        fc.setTitle("Opening the location..");
        File chosen = fc.showOpenDialog(Controller.mainStage);
        if(chosen != null && ImageUtils.confirmType(chosen, Controller.getImageFilter() )) {
            filePathForImageKeywords = chosen.getPath();
            NameOfFileSelectedForKeywords.setText(filePathForImageKeywords);
            setImageForCropping(filePathForImageKeywords);
        }
    }

    public void AnalyzeSingleImageForKeywords(ActionEvent actionEvent) {
        AnalysisMicrosoftAzure imageAnalysis = new AnalysisMicrosoftAzure(filePathForImageKeywords);
        JSONObject results = imageAnalysis.analyze();
    }

    public void AnalyzeMultipleImageForKeywords(ActionEvent actionEvent) {

    }

    public void chooseImageFileForCropping(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(getUserDirectory());
        fc.setTitle("Opening the location..");
        File chosen = fc.showOpenDialog(Controller.mainStage);
        if(chosen != null && ImageUtils.confirmType(chosen, Controller.getImageFilter() )) {
            filePathForImageCropping = chosen.getPath();
            NameOfFileSelectedForCropping.setText(filePathForImageCropping);
            setImageForCropping(filePathForImageCropping);
        }
    }

    public void setImageForCropping(String filePath){
        if (filePath != null) {
            try {
                FileInputStream input = new FileInputStream(filePath);
                Image image = new Image(input);
                DisplayOfImageForCropping.setImage(image);
                Controller.setScanImageForCropping(image);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void configureCroppingLocations(ActionEvent actionEvent) throws IOException {
        if (Controller.getScanImageForCropping() != null) {
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
