package com.example.sequax40.test.sprint1;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.sequax40.controller.BoardController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Feature2Test {
	

	class BoardControllerTest {

	    // 1. Initialize the JavaFX Toolkit before running any tests
	    @BeforeAll
	    static void initToolkit() {
	        try {
	            Platform.startup(() -> {});
	        } catch (IllegalStateException e) {
	            // Ignore: The toolkit is already initialized (happens if multiple test classes run)
	        }
	    }

	    @Test
	    void testInitializeSetsPropertiesAndBindings() {
	        // 2. Instantiate the controller
	        // (Replace 'YourControllerClass' with the actual name of your controller)
	        BoardController controller = new BoardController();

	        // 3. Manually inject the FXML fields that the FXMLLoader normally handles
	        controller.masterGroup = new Group();
	        controller.mainContainer = new StackPane(); 
	        
	        // Note: For this test, I'm assuming DESIGN_WIDTH is 1920 and DESIGN_HEIGHT is 1080.
	        // Update the assertions below to match your actual design constants.

	        // 4. Call the method being tested
	        controller.initialize();

	        // --- PHASE 1: Assert Static Properties ---
	        assertTrue(controller.masterGroup.isManaged(), "Group should be managed to consider bounds");
	        assertEquals(-100, controller.masterGroup.getTranslateY(), "TranslateY should remove padding");
	        assertEquals(Pos.TOP_CENTER, StackPane.getAlignment(controller.masterGroup), "Alignment should be TOP_CENTER");

	        // --- PHASE 2: Assert Dynamic Bindings (The Scaling Logic) ---
	        
	        // Scenario A: Container size is 0 (should return 1.0 based on your if-statement)
	        controller.mainContainer.resize(0, 0);
	        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);

	        // Scenario B: Container exactly matches design specs (Scale should be 1.0)
	        controller.mainContainer.resize(1920, 1080);
	        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001);
	        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);

	        // Scenario C: Container is exactly half the size (Scale should be 0.5)
	        controller.mainContainer.resize(960, 540);
	        assertEquals(0.5, controller.masterGroup.getScaleX(), 0.001);
	        assertEquals(0.5, controller.masterGroup.getScaleY(), 0.001);

	        // Scenario D: Aspect Ratio check (e.g., Ultra-wide monitor)
	        // Width is 2x larger, but height remains 1x. Math.min should cap it at 1.0 based on height.
	        controller.mainContainer.resize(3840, 1080);
	        assertEquals(1.0, controller.masterGroup.getScaleX(), 0.001); 
	        assertEquals(1.0, controller.masterGroup.getScaleY(), 0.001);
	    }
	}
}
