package sample;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.Utility.Utils;

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

    @FXML
    private MenuBar menuBar;

    @FXML
    private TabPane tabPane;

    @FXML
    private Button executeButton;

    @FXML
    private TextArea logOutput;

    @FXML
    private Tab configureTab;

    @FXML
    private Tab executeTab;

    /**
     * Executes file analysis through keywords
     * @param actionEvent
     */
    public void executeKeywords(ActionEvent actionEvent) throws IOException {
        menuBar.setDisable(true);
        configureTab.setDisable(true);
        executeButton.setDisable(true);
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
        dc.setTitle("Select Directory to Scan");

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
        dc.setTitle("Select Directory to Store Images for Review");

        // Gets chosen file
        File chosen = dc.showDialog(Controller.mainStage);
        if(chosen != null) {
            chosen.mkdirs();
            Controller.setReviewSubmissionDirectory(chosen); // Alters global variable
            reviewDirectoryField.setText(chosen.getPath()); // Sets text in textfield
        }
    }

    public void showInstructions(ActionEvent actionEvent){
        Stage instructionStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/Instructions.fxml"
                    )
            );
            instructionStage.setTitle("Instructions");
            instructionStage.setScene(
                    new Scene(
                            loader.load(),
                            800,
                            600
                    )
            );
            /**
             Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
             menuStage.setTitle("Main Menu");
             menuStage.setScene(new Scene(root));
             **/
            instructionStage.setMaximized(false);
            instructionStage.showAndWait();
        } catch(Exception e){
            e.printStackTrace();
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

        //Console console = new Console(logOutput);
        //PrintStream ps = new PrintStream(console, true);
        //System.setOut(ps);
        //System.setErr(ps);
    }

    public static class Console extends OutputStream {

        private TextArea output;

        Console(TextArea ta) {
            this.output = ta;
        }

        @Override
        public void write(int i) throws IOException {
            Platform.runLater(()->output.appendText(String.valueOf((char) i)));
        }

    }

}
