package louisiana;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MenuController implements Initializable {
    @FXML
    private Label Instructions;

    @FXML
    private TextField scanDirectory;

    @FXML
    private TextField reviewDirectory;

    @FXML
    private TextField subscriptionKey;

    @FXML
    private TextField endpointURL;

    @FXML
    private Label Alert;

    public void submitSubscriptionKey(){
        Preferences prefs = Preferences.userNodeForPackage(louisiana.Controller.class);
        String text = subscriptionKey.getText();
        if (text == null || text.length() <= 0)
            return;
        prefs.put("AZURE_KEY", text);
        Controller.setAzureData(text, null);
    }

    public void submitEndpointURL(){
        Preferences prefs = Preferences.userNodeForPackage(louisiana.Controller.class);
        String text = endpointURL.getText();
        if (text == null || text.length() <= 0)
            return;
        prefs.put("AZURE_ENDPOINT", text);
        Controller.setAzureData(null, text);
    }

    public void selectReviewDirectory(){
        // Sets the directory for analysis
        File userDirectory = new File(reviewDirectory.getText());
        if(!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }

        // Opens the directory chooser
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(userDirectory);
        dc.setTitle("Select Directory to Store Images for Review");

        // Gets chosen file
        File chosen = dc.showDialog(Instructions.getScene().getWindow());
        if(chosen != null) {
            try {
                if (chosen.canRead()) {
                    chosen.mkdirs();
                    reviewDirectory.setText(chosen.getPath()); // Sets text in textfield
                    Controller.setReviewDir(chosen); // Alters global variable
                } else {
                    Alert.setText(Utils.CANNOT_READ);
                }
            } catch(Exception e){
                Alert.setText(Utils.CANNOT_READ);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Alert.setVisible(false);

        Preferences prefs = Preferences.userNodeForPackage(louisiana.Controller.class);
        String key = prefs.get("AZURE_KEY", null);
        String endpoint = prefs.get("AZURE_ENDPOINT", null);
        if (key != null){
            subscriptionKey.setText(key);
        }
        if (endpoint != null){
            endpointURL.setText(endpoint);
        }
        Controller.setAzureData(key, endpoint);

        File defaultDirectory = Utils.getUserDirectory();
        if (defaultDirectory != null){
            scanDirectory.setText(defaultDirectory.getPath());
            reviewDirectory.setText(defaultDirectory.getPath());
        }
    }
}
