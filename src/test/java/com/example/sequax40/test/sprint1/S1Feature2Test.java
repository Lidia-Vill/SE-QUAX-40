package com.example.sequax40.test.sprint1;

import com.example.sequax40.controller.BoardController;
import com.example.sequax40.test.helperMethods.*;

import javafx.application.Platform;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class S1Feature2Test {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

 
    @Test
    void testScalingZeroContainerReturnsOne() {
    	HelperMethods helper = new HelperMethods();
        BoardController controller = helper.createController();

        controller.mainContainer.resize(0, 0);
        controller.mainContainer.layout();

        assertEquals(1.0, controller.windowContainer.getScaleX(), 0.001);
    }

    @Test
    void testScalingAtDesignSize() {
    	HelperMethods helper = new HelperMethods();
        BoardController controller = helper.createController();

        controller.mainContainer.resize(900, 850);
        controller.mainContainer.layout();

        assertEquals(0.8181818, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(0.8181818, controller.windowContainer.getScaleY(), 0.001);
    }

    @Test
    void testScalingHalfSize() {
    	HelperMethods helper = new HelperMethods();
        BoardController controller = helper.createController();

        controller.mainContainer.resize(450, 425);
        controller.mainContainer.layout();

        assertEquals(0.4090909, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(0.4090909, controller.windowContainer.getScaleY(), 0.001);
    }

    @Test
    void testScalingExtraWideWindow() {
    	HelperMethods helper = new HelperMethods();
        BoardController controller = helper.createController();

        controller.mainContainer.resize(1800, 850);
        controller.mainContainer.layout();

        assertEquals(1.0059171, controller.windowContainer.getScaleX(), 0.001);
        assertEquals(1.0059171, controller.windowContainer.getScaleY(), 0.001);
    }
}
