package louisiana;

import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(ApplicationExtension.class)
class MainTest {

    /**
     * Will be called with {@code @Before} semantics, i.e., before each test method.
     * @param stage - Will be injected by test runner
     */
    @Start
    private void start(Stage stage){

    }
    @Test
    void main() {
    }
}