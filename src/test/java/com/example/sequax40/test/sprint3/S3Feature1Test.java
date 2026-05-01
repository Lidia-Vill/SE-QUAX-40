package com.example.sequax40.test.sprint3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;
import com.example.sequax40.test.helperMethods.HelperMethods;

public class S3Feature1Test {

	private HelperMethods helper = new HelperMethods();
    private Board board;
    private GameManager manager;

    @BeforeEach
    void setup() {
        board   = new Board(11, 11);
        Map<String, Tile> tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }

    
    @Test
    void testBlackWinsStraightColumnPath() {
    	helper.loadDump(HelperMethods.BLACK_WINS_STRAIGHT_COLUMN);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackWinsZigZagAcrossAdjacentColumns() {
    	helper.loadDump(HelperMethods.BLACK_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLoseMissingBottom() {
    	helper.loadDump(HelperMethods.BLACK_MISSING_BOTTOM);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesMissingTop() {
    	helper.loadDump(HelperMethods.BLACK_MISSING_TOP);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesDisconnectedSequences() {
    	helper.loadDump(HelperMethods.BLACK_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesBlockedByWhite() {
    	helper.loadDump(HelperMethods.BLACK_BLOCKED_BY_WHITE);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }


    @Test
    void testBlackLosesEmptyBoard() {
    	helper.loadDump(HelperMethods.EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    
    @Test
    void testWhiteWinsStraightRowPath() {
    	helper.loadDump(HelperMethods.WHITE_WINS_STRAIGHT_ROW);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteWinsZigZagAcrossAdjacentRows() {
    	helper.loadDump(HelperMethods.WHITE_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesMissingRight() {
    	helper.loadDump(HelperMethods.WHITE_MISSING_RIGHT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesMissingLeft() {
    	helper.loadDump(HelperMethods.WHITE_MISSING_LEFT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesDisconnectedSequences() {
    	helper.loadDump(HelperMethods.WHITE_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesBlockedByBlack() {
        helper.loadDump(HelperMethods.WHITE_BLOCKED_BY_BLACK);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesEmptyBoard() {
        helper.loadDump(HelperMethods.EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }


   
}
