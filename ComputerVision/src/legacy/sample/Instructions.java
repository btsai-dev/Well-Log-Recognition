package louisiana;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class Instructions implements Initializable {
    @FXML
    private TextArea areaForText;

    private StringBuilder instructions;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instructions.append(
                "There are two tabs that can be accessed. The Configure Analaysis tab will allow configuration of which " +
                        "keywords you want to add, which directory you want to analyze the images, and which directory " +
                        "which you want to place images that the program is submitting for human review.\n"
        );
        instructions.append(
                "The Execute Processing tab will allow you to begin the process of executing the program. The program " +
                        "will often hand and stop responding due to being a single-threaded program. This is normal, " +
                        "and there is no need to spam click the window.\n"
        );
        instructions.append(
                "During execution, the program will routinely request that you mark positions that the program should " +
                        "send to Microsoft Azure for Optical Character Recognition (OCR) analysis. The keywords which " +
                        "you entered should be unique (the program will only seek out the first instance detected) " +
                        "and should be relatively close to the data you want the program to read.\n"
        );
        instructions.append(
                "During target selection, you can select only one section per keyword. If you believe that the keyword" +
                        "detected is in error, you can select the red SUBMIT FOR REVIEW button to submit the image " +
                        "file for review. If this continuously happens, then it is likely that the keywords you " +
                        "have selected are not precise enough. If this happens, you should restart the program."
        );
    }
}
