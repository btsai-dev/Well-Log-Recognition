package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class Configurator {
    @FXML
    private Button buttonClose;

    @FXML
    private PasswordField azureSubscriptionKey;

    @FXML
    private TextField azureEndpoint;

    public void exitConfig(ActionEvent actionEvent){
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }

    public void saveKey(ActionEvent actionEvent) {
        String key = azureSubscriptionKey.getText();
        try{
            Properties prop = new Properties();

            InputStream input = new FileInputStream(Controller.getConfigPath());
            prop.load(input);
            input.close();

            OutputStream output = new FileOutputStream(Controller.getConfigPath());
            prop.setProperty("db.azureSubscriptionKey", key);
            prop.store(output, null);
            output.close();
            Controller.loadAzureData(key, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void saveEndpoint(ActionEvent actionEvent) {
        String endpoint = azureEndpoint.getText();
        try{
            Properties prop = new Properties();

            InputStream input = new FileInputStream(Controller.getConfigPath());
            prop.load(input);
            input.close();

            OutputStream output = new FileOutputStream(Controller.getConfigPath());
            prop.setProperty("db.azureEndpoint", endpoint);
            prop.store(output, null);
            output.close();
            Controller.loadAzureData(null, endpoint);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}