package com.example.sequax40.test.helperMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.move.Move;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

public class HelperMethods {

	public MouseEvent mockClickEvent(Polygon polygon) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                MouseButton.PRIMARY,
                1, false, false, false, false,
                true, false, false, true, false, false, null
        ) {
            @Override
            public Object getSource() {
                return polygon;
            }
        };
    }

    public MouseEvent mockClickEvent(Tile tile) {
        Polygon dummy = new Polygon();
        dummy.setUserData(tile);

        return new MouseEvent(MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                javafx.scene.input.MouseButton.PRIMARY,
                1, false, false, false, false,
                true, false, false, true, false, false, null
        ) {
            @Override
            public Object getSource() {
                return dummy;
            }
        };
    }

    public void runOnFxThreadAndWait(Runnable action) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
	
	public Map<String, Tile> buildFullBoard() {
        Map<String, Tile> map = new HashMap<>();

        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
                String coord = "" + col + row;
                map.put(coord, new Tile(coord, ShapeEnum.OCTAGON));
            }
        }

        for (char col = 'A'; col < 'K'; col++) {
            char nextCol = (char)(col + 1);
            for (int row = 1; row < 11; row++) {
                String coord = "" + col + nextCol + "_" + row + "_" + (row + 1);
                map.put(coord, new Tile(coord, ShapeEnum.RHOMBUS));
            }
        }

        return map;
    }

	public Move moveFor(Tile tile) {
        return new Move(tile.getCoord(), tile.getShape());
    }
	
	public boolean boardsAreEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a[row].length; col++) {
                if (a[row][col] != b[row][col]) return false;
            }
        }
        return true;
    }

    public int[][] cloneBoardDump(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int row = 0; row < original.length; row++) {
            System.arraycopy(original[row], 0, copy[row], 0, original[row].length);
        }
        return copy;
    }
}
