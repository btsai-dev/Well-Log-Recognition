package louisiana.Analysis;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class MsAzureInstanceTest extends ApplicationTest {
    /**
     * Will be called with {@code @Before} semantics, i.e., before each test method.
     * @param stage - Will be injected by test runner
    */
    @Start
    private void onStart(Stage stage) throws IOException {
        System.out.println("Inside onStart");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/MsAzureInstanceTest.fxml")
        );
        stage.setScene(new Scene(loader.load()));
        stage.show();
        stage.toFront();
    }


    @Test
    void analyzeGeneralTest() throws IOException {
        System.out.println("Running test");
    }
}