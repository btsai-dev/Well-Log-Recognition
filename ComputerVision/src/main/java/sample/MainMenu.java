package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {
    @FXML
    private TextField keywordAdditionField;

    @FXML
    private TextField targetAnalysisDirectoryField;

    @FXML
    private VBox keywordsDisplayVBox;

    @FXML
    private TextField reviewDirectoryField;

    /**
     * Executes file analysis through keywords
     * @param actionEvent
     */
    public void executeKeywords(ActionEvent actionEvent) throws IOException {
        AnalysisKeywords.execute();
    }

    /**
     * Resets entire list of keywords
     * @param actionEvent
     */
    public void resetKeywords(ActionEvent actionEvent){
        // Clear out the VBox and the keywords list
        keywordsDisplayVBox.getChildren().clear();
        Controller.clearKeywordList();
    }

    /**
     * Submits keyword
     * @param actionEvent
     */
    public void submitKeyword(ActionEvent actionEvent){
        String keywordSubmission = Utils.filter(keywordAdditionField.getText());
        // Make sure something valid exists in the submission field and has not been submitted
        if (keywordSubmission != null
                && keywordSubmission.length() > 0
                && keywordSubmission.length() <= Utils.MAX_KEYWORD_LENGTH
                && !Controller.isInKeywordList(keywordSubmission)){
            // Adds keyword to stack of keywords and scrolllist of keywords
            Controller.addKeyword(keywordSubmission);
            keywordsDisplayVBox.getChildren().add(new Text(keywordSubmission));
        }
    }

    /**
     * Selects the directory upon button press
     * @param actionEvent
     */
    public void chooseDirectoryForProcessing(ActionEvent actionEvent){
        // Sets the directory for analysis
        File userDirectory = new File(targetAnalysisDirectoryField.getText());
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        // Opens the directory chooser
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(userDirectory);
        dc.setTitle("Opening the location..");

        // Gets chosen file
        File chosen = dc.showDialog(Controller.mainStage);
        if(chosen != null) {
            chosen.mkdirs();
            Controller.setTargetAnalysisDirectory(chosen); // Alters global variable
            targetAnalysisDirectoryField.setText(chosen.getPath()); // Sets text in textfield
        }
    }

    /**
     * Selects the directory upon button press
     * @param actionEvent
     */
    public void chooseDirectoryForReview(ActionEvent actionEvent){
        // Sets the directory for analysis
        File userDirectory = new File(reviewDirectoryField.getText());
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        // Opens the directory chooser
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(userDirectory);
        dc.setTitle("Opening the location..");

        // Gets chosen file
        File chosen = dc.showDialog(Controller.mainStage);
        if(chosen != null) {
            chosen.mkdirs();
            Controller.setReviewSubmissionDirectory(chosen); // Alters global variable
            reviewDirectoryField.setText(chosen.getPath()); // Sets text in textfield
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Sets the path to the config file
        String path = System.getProperty("user.home") +
                File.separator+ "WellLogAnalysis" +
                File.separator+"config.properties";
        File config = new File(path); // TODO: STORE CONFIG DATA IN USER'S APPDATA FOLDER
        config.getParentFile().mkdirs();
        Controller.configPath = path;

        // Sets the default directory text in the CONFIGURE ANALYSIS tab
        File defaultDirectory = Utils.getUserDirectory();
        if (defaultDirectory != null){
            targetAnalysisDirectoryField.setText(defaultDirectory.getPath());
            reviewDirectoryField.setText(defaultDirectory.getPath());

        }
    }







/**

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
            Parent root = FXMLLoader.load(getClass().getResource("../../resources/ScanConfigurator.fxml"));
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

*/




}
