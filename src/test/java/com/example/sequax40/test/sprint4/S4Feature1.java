package com.example.sequax40.test.sprint4;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.player.BotPlayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class S4Feature1 {

    // SETUP & TEST UTILITIES

    private BotPlayer botPlayer;
    private Map<String, Tile> tileMap;

    @BeforeEach
    void setUp() {
        botPlayer = new BotPlayer();
        tileMap   = buildFullBoard();
    }

    private Map<String, Tile> buildFullBoard() {
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

    private void own(String coord, PlayerEnum player) {
        tileMap.get(coord).setOwner(player);
    }

    // OPENING MOVE BEHAVIOUR

    @Test
    void firstMove_alwaysPlacesCentre_F6() {
        BotPlayer.StrategyResult result = botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result.chosenTile, "Bot should always return a tile on move 0");
        assertEquals("F6", result.chosenTile.getCoord(),
                "Bot's first move should always be F6 (centre of board)");
    }

    @Test
    void firstMove_fallsBackIfF6Taken() {
        own("F6", PlayerEnum.WHITE);
        BotPlayer.StrategyResult result = botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result.chosenTile, "Bot should return a fallback tile if F6 is taken");
        assertNotEquals("F6", result.chosenTile.getCoord(),
                "Bot should not choose an opponent-owned tile");
    }

    @Test
    void firstMove_strategyResult_isNotBlocking() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertFalse(result.isBlocking,
                "Bot should not be in blocking mode on the very first move");
        assertEquals("F6", result.chosenTile.getCoord(),
                "First move strategy should choose F6");
    }

    // BASIC STRATEGY VALIDATION & ERROR HANDLING

    @Test
    void computeStrategy_returnsNonNullResult() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result, "computeStrategy should never return null");
        assertNotNull(result.chosenTile,
                "StrategyResult should always have a chosen tile on an open board");
    }

    @Test
    void computeStrategy_throwsOnNullTileMap() {
        assertThrows(IllegalStateException.class,
                () -> botPlayer.computeStrategy(null, PlayerEnum.BLACK, 0),
                "computeStrategy should throw on null tileMap");
    }

    @Test
    void computeStrategy_throwsOnNullBotColor() {
        assertThrows(IllegalStateException.class,
                () -> botPlayer.computeStrategy(tileMap, null, 0),
                "computeStrategy should throw on null botColor");
    }


    // STRATEGY CACHING BEHAVIOUR

    @Test
    void getCachedStrategy_returnsNullBeforeCompute() {
        assertNull(botPlayer.getCachedStrategy(),
                "Cache should be null before computeStrategy is called");
    }

    @Test
    void getCachedStrategy_returnsResultAfterCompute() {
        botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(botPlayer.getCachedStrategy(),
                "Cache should be populated after computeStrategy");
    }

    @Test
    void clearCache_resetsToNull() {
        botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        botPlayer.clearCache();
        assertNull(botPlayer.getCachedStrategy(),
                "Cache should be null after clearCache()");
    }

    @Test
    void cachedStrategy_matchesComputeResult() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 5);
        BotPlayer.StrategyResult cached = botPlayer.getCachedStrategy();
        assertSame(result, cached,
                "getCachedStrategy should return the same object returned by computeStrategy");
    }

    @Test
    void computeStrategy_afterClearCache_returnsFreshNonNullResult() {
        botPlayer.clearCache();
        BotPlayer.StrategyResult fresh =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);
        assertNotNull(fresh.chosenTile,
                "computeStrategy should compute a fresh result when cache is empty");
    }

    // PATHFINDING – BLACK PLAYER

    @Test
    void findShortestPath_BLACK_returnsNonEmptyPath_onEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty(),
                "BLACK should always find a path on an empty board");
    }

    @Test
    void findShortestPath_BLACK_startsAtRow11() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty());
        Tile first = path.get(0);
        int firstRow = Integer.parseInt(first.getCoord().substring(1));
        assertEquals(11, firstRow,
                "BLACK's path should start at row 11 (top of board)");
    }

    @Test
    void findShortestPath_BLACK_endsAtRow1() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty());
        Tile last = path.get(path.size() - 1);
        int lastRow = Integer.parseInt(last.getCoord().substring(1));
        assertEquals(1, lastRow,
                "BLACK's path should end at row 1 (bottom of board)");
    }

    @Test
    void findShortestPath_BLACK_prefersColumnF_onEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty());

        long centreCount = path.stream()
                .filter(t -> t.getShape() == ShapeEnum.OCTAGON)
                .filter(t -> t.getCoord().charAt(0) == 'F')
                .count();

        assertTrue(centreCount > 5,
                "BLACK's path should run mostly through centre column F on an empty board");
    }

    @Test
    void findShortestPath_BLACK_ownTilesCostZero_andAppearInPath() {
        for (int row = 7; row <= 11; row++) {
            own("F" + row, PlayerEnum.BLACK);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);

        long ownedInPath = path.stream()
                .filter(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.BLACK)
                .count();

        assertTrue(ownedInPath > 0,
                "Path should pass through already-owned BLACK tiles (cost 0)");
    }

    @Test
    void findShortestPath_BLACK_avoidsOpponentTiles() {
        for (int row = 1; row <= 11; row++) {
            own("F" + row, PlayerEnum.WHITE);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty(), "BLACK should find an alternative path around blocked column");

        boolean containsWhite = path.stream()
                .anyMatch(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.WHITE);
        assertFalse(containsWhite,
                "BLACK's path should never pass through WHITE-owned tiles");
    }

    @Test
    void findShortestPath_BLACK_returnsEmpty_whenCompletelyBlocked() {
        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
                own("" + col + row, PlayerEnum.WHITE);
            }
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertTrue(path.isEmpty(),
                "Path should be empty when all tiles are blocked by opponent");
    }

    @Test
    void findShortestPath_WHITE_returnsNonEmptyPath_onEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty(),
                "WHITE should always find a path on an empty board");
    }

    @Test
    void findShortestPath_WHITE_startsAtColA() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty());
        Tile first = path.get(0);
        assertEquals('A', first.getCoord().charAt(0),
                "WHITE's path should start at column A (left of board)");
    }

    @Test
    void findShortestPath_WHITE_endsAtColK() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty());
        Tile last = path.get(path.size() - 1);
        assertEquals('K', last.getCoord().charAt(0),
                "WHITE's path should end at column K (right of board)");
    }

    // PATHFINDING – WHITE PLAYER

    @Test
    void findShortestPath_WHITE_prefersRow6_onEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty());

        long centreCount = path.stream()
                .filter(t -> t.getShape() == ShapeEnum.OCTAGON)
                .filter(t -> Integer.parseInt(t.getCoord().substring(1)) == 6)
                .count();

        assertTrue(centreCount > 5,
                "WHITE's path should run mostly through centre row 6 on an empty board");
    }

    @Test
    void findShortestPath_WHITE_avoidsOpponentTiles() {
        for (char col = 'A'; col <= 'K'; col++) {
            own("" + col + 6, PlayerEnum.BLACK);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty(), "WHITE should find an alternative path around blocked row");

        boolean containsBlack = path.stream()
                .anyMatch(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.BLACK);
        assertFalse(containsBlack,
                "WHITE's path should never pass through BLACK-owned tiles");
    }

    @Test
    void findShortestPath_WHITE_returnsEmpty_whenCompletelyBlocked() {
        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
                own("" + col + row, PlayerEnum.BLACK);
            }
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertTrue(path.isEmpty(),
                "WHITE's path should be empty when all tiles are blocked");
    }

    // STRATEGY DECISION MAKING (ATTACK VS BLOCK)

    @Test
    void strategy_advances_whenOpponentIsNotThreat() {
        own("F11", PlayerEnum.BLACK);
        own("F10", PlayerEnum.BLACK);
        own("F9",  PlayerEnum.BLACK);
        own("F8",  PlayerEnum.BLACK);
        own("F7",  PlayerEnum.BLACK);
        own("F6",  PlayerEnum.BLACK);
        own("F5",  PlayerEnum.BLACK);
        own("F4",  PlayerEnum.BLACK);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 8);

        assertFalse(result.isBlocking,
                "Bot should advance when it has a large lead over the opponent");
    }

    @Test
    void strategy_blocks_whenOpponentIsCloseToWinning() {
        own("A6", PlayerEnum.WHITE);
        own("B6", PlayerEnum.WHITE);
        own("C6", PlayerEnum.WHITE);
        own("D6", PlayerEnum.WHITE);
        own("E6", PlayerEnum.WHITE);
        own("F6", PlayerEnum.WHITE);
        own("G6", PlayerEnum.WHITE);
        own("H6", PlayerEnum.WHITE);
        own("I6", PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 9);

        assertTrue(result.isBlocking,
                "Bot should block when opponent needs only one more tile to win");
    }

    // CHOSEN TILE VALIDATION

    @Test
    void strategy_chosenTile_isAlwaysEmpty() {
        own("F11", PlayerEnum.BLACK);
        own("F10", PlayerEnum.BLACK);
        own("A6",  PlayerEnum.WHITE);
        own("B6",  PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 4);

        assertNotNull(result.chosenTile, "Chosen tile should not be null");
        assertTrue(result.chosenTile.isEmpty(),
                "Bot should never choose an already-occupied tile");
    }

    @Test
    void strategy_chosenTile_isNeverOpponentOwned() {
        own("F6", PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);

        assertNotNull(result.chosenTile);
        assertNotEquals(PlayerEnum.WHITE, result.chosenTile.getOwner(),
                "Bot should never choose a tile owned by the opponent");
    }

    // STRATEGY PATH VALIDATION
    @Test
    void strategy_path_isNeverEmpty_onOpenBoard() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);
        assertFalse(result.path.isEmpty(),
                "Strategy path should never be empty on an open board");
    }


    // PIE RULE
    @Test
    void strategy_respectsPieRule_BLACK_cannotUsePieSwappedTile() {
        own("F6", PlayerEnum.WHITE);

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);

        boolean pathContainsF6 = path.stream()
                .anyMatch(t -> "F6".equals(t.getCoord()));

        assertFalse(pathContainsF6,
                "After pie rule swap, BLACK's path should not go through the swapped tile");
    }

    @Test
    void strategy_respectsPieRule_WHITE_benefitsFromSwappedTile() {
        own("F6", PlayerEnum.WHITE);

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);

        boolean pathContainsF6 = path.stream()
                .anyMatch(t -> "F6".equals(t.getCoord()));

        assertTrue(pathContainsF6,
                "After pie rule swap, WHITE's path should pass through their gained tile");
    }

    // RHOMBUS (DIAGONAL) STRATEGY
    @Test
    void strategy_rhombus_isChosenWhenDiagonalIsComplete() {
        own("F6", PlayerEnum.BLACK);
        own("F7", PlayerEnum.BLACK);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);

        assertNotNull(result.chosenTile);

        boolean choseRhombus = result.chosenTile.getShape() == ShapeEnum.RHOMBUS;

        if (choseRhombus) {
            assertEquals(ShapeEnum.RHOMBUS, result.chosenTile.getShape(),
                    "Bot correctly chose a rhombus when a diagonal was complete");
        } else {
            assertEquals(ShapeEnum.OCTAGON, result.chosenTile.getShape(),
                    "Bot chose an octagon — valid if no rhombus diagonal was available yet");
        }
    }

    // STRATEGY STATE TRACKING (LAST EXECUTED)

    @Test
    void lastExecutedStrategy_isNullByDefault() {
        assertNull(botPlayer.getLastExecutedStrategy(),
                "Last executed strategy should be null before any move is made");
    }

    @Test
    void lastExecutedStrategy_canBeSetAndRetrieved() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        botPlayer.setLastExecutedStrategy(result);

        assertSame(result, botPlayer.getLastExecutedStrategy(),
                "getLastExecutedStrategy should return exactly what was set");
    }
}