package com.example.sequax40.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.game.GameManager;

public class S3Feature2 {
    //testing code to check the winner

    private Board board;
    private GameManager manager;

    //Board dump 11x11 grid [row][col]
    //0 = EMPTY
    //1 = BLACK
    //2 = WHITE

    // BLACK wins: full column F owned by BLACK (rows 1-11)
    private static final int[][] BLACK_WINS_STRAIGHT_COLUMN = {
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
    };

    // BLACK wins: column E rows 11 to 7, column F rows 7 to 1
    private static final int[][] BLACK_WINS_ZIGZAG = {
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
    };

    // BLACK does not win: column F missing row 1
    private static final int[][] BLACK_MISSING_BOTTOM = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
    };

    // BLACK does not win: column F missing row 11
    private static final int[][] BLACK_MISSING_TOP = {
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // BLACK does not win: two disconnected sequences of tiles
    private static final int[][] BLACK_DISCONNECTED = {
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
    };

    // BLACK does not win: column F blocked by WHITE at F6
    private static final int[][] BLACK_BLOCKED_BY_WHITE = {
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
    };

    // WHITE wins: full row 6 owned by WHITE
    private static final int[][] WHITE_WINS_STRAIGHT_ROW = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // WHITE wins: row 5 cols A to F, row 6 cols F to K
    private static final int[][] WHITE_WINS_ZIGZAG = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    //WHITE does not win: row 6 missing column K
    private static final int[][] WHITE_MISSING_RIGHT = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // WHITE does not win: row 6 missing column A
    private static final int[][] WHITE_MISSING_LEFT = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // WHITE does not win: two disconnected sequences in row 6
    private static final int[][] WHITE_DISCONNECTED = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // WHITE does not win: row 6 blocked by BLACK at F6
    private static final int[][] WHITE_BLOCKED_BY_BLACK = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };

    // Empty board.
    //No player can win of the board is empty.
    private static final int[][] EMPTY_BOARD = new int[11][11];


    @BeforeEach
    void setup() {
        board   = new Board(11, 11);
        Map<String, Tile> tileMap = board.getAllTiles();
        manager = new GameManager(board, tileMap);
    }

    //load dump into board
    private void loadDump(int[][] dump) {
        for (int row = 0; row < dump.length; row++) {
            for (int col = 0; col < dump[row].length; col++) {
                char colLetter = (char) ('A' + col);
                String coord = "" + colLetter + (row + 1);
                PlayerEnum owner = switch (dump[row][col]) {
                    case 1 -> PlayerEnum.BLACK;
                    case 2 -> PlayerEnum.WHITE;
                    default -> PlayerEnum.EMPTY;
                };
                Tile tile = new Tile(coord, ShapeEnum.OCTAGON);
                tile.setOwner(owner);
                board.addTile(tile);
            }
        }
    }


    // BLACK win tests

    @Test
    void blackWins_straightColumnPath() {
        loadDump(BLACK_WINS_STRAIGHT_COLUMN);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void blackWins_zigZagAcrossAdjacentColumns() {
        loadDump(BLACK_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void blackloses_missingBottom() {
        loadDump(BLACK_MISSING_BOTTOM);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void blackloses_missingTop() {
        loadDump(BLACK_MISSING_TOP);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void blackloses_disconnectedSequences() {
        loadDump(BLACK_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }

    @Test
    void blackloses_blockedByWhite() {
        loadDump(BLACK_BLOCKED_BY_WHITE);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }


    @Test
    void blackloses_emptyBoard() {
        loadDump(EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.BLACK));
    }


    // WHITE win tests

    @Test
    void whiteWins_straightRowPath() {
        loadDump(WHITE_WINS_STRAIGHT_ROW);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteWins_zigZagAcrossAdjacentRows() {
        loadDump(WHITE_WINS_ZIGZAG);
        assertTrue(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteloses_missingRight() {
        loadDump(WHITE_MISSING_RIGHT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteloses_missingLeft() {
        loadDump(WHITE_MISSING_LEFT);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteloses_disconnectedSequences() {
        loadDump(WHITE_DISCONNECTED);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteloses_blockedByBlack() {
        loadDump(WHITE_BLOCKED_BY_BLACK);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }

    @Test
    void whiteloses_emptyBoard() {
        loadDump(EMPTY_BOARD);
        assertFalse(manager.checkWin(PlayerEnum.WHITE));
    }


}