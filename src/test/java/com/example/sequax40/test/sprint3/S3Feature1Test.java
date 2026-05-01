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

    private HelperMethods helper;

    @BeforeEach
    void setup() {
        helper = new HelperMethods();
    }

    /**
     * Rebuild board + manager EVERY TIME so state is clean
     */
    private GameManager initWithDump(int[][] dump) {

        Board board = new Board(11, 11);
        Map<String, Tile> tileMap = board.getAllTiles();

        helper.loadDump(board, tileMap, dump);

        return new GameManager(board, tileMap);
    }

    @Test
    void testBlackWinsStraightColumnPath() {
        GameManager manager = initWithDump(HelperMethods.BLACK_WINS_STRAIGHT_COLUMN);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackWinsZigZagAcrossAdjacentColumns() {
        GameManager manager = initWithDump(HelperMethods.BLACK_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLoseMissingBottom() {
        GameManager manager = initWithDump(HelperMethods.BLACK_MISSING_BOTTOM);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesMissingTop() {
        GameManager manager = initWithDump(HelperMethods.BLACK_MISSING_TOP);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesDisconnectedSequences() {
        GameManager manager = initWithDump(HelperMethods.BLACK_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesBlockedByWhite() {
        GameManager manager = initWithDump(HelperMethods.BLACK_BLOCKED_BY_WHITE);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testBlackLosesEmptyBoard() {
        GameManager manager = initWithDump(HelperMethods.EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void testWhiteWinsStraightRowPath() {
        GameManager manager = initWithDump(HelperMethods.WHITE_WINS_STRAIGHT_ROW);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteWinsZigZagAcrossAdjacentRows() {
        GameManager manager = initWithDump(HelperMethods.WHITE_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesMissingRight() {
        GameManager manager = initWithDump(HelperMethods.WHITE_MISSING_RIGHT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesMissingLeft() {
        GameManager manager = initWithDump(HelperMethods.WHITE_MISSING_LEFT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesDisconnectedSequences() {
        GameManager manager = initWithDump(HelperMethods.WHITE_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesBlockedByBlack() {
        GameManager manager = initWithDump(HelperMethods.WHITE_BLOCKED_BY_BLACK);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void testWhiteLosesEmptyBoard() {
        GameManager manager = initWithDump(HelperMethods.EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }
}