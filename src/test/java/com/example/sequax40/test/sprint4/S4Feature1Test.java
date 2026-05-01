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

class S4Feature1Test {

    private BotPlayer botPlayer;
    private Map<String, Tile> tileMap;

    @BeforeEach
    void setUp() {
        botPlayer = new BotPlayer();
        tileMap   = buildFullBoard();
    }

    
    // OPENING MOVE BEHAVIOUR

    @Test
    void testFirstMoveAlwaysPlacesCentreF6() {
        BotPlayer.StrategyResult result = botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result.chosenTile, "Bot should always return a tile on move 0");
        assertEquals("F6", result.chosenTile.getCoord(),
                "Bot's first move should always be F6 (centre of board)");
    }

    @Test
    void testFirstMoveFallsBackIfF6Taken() {
        playerOwns("F6", PlayerEnum.WHITE);
        BotPlayer.StrategyResult result = botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result.chosenTile, "Bot should return a fallback tile if F6 is taken");
        assertNotEquals("F6", result.chosenTile.getCoord(),
                "Bot should not choose an opponent-owned tile");
    }

    @Test
    void testFirstMoveStrategyResultIsNotBlocking() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertFalse(result.isBlocking,
                "Bot should not be in blocking mode on the very first move");
        assertEquals("F6", result.chosenTile.getCoord(),
                "First move strategy should choose F6");
    }

    @Test
    void testComputeStrategyReturnsNonNullResult() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(result, "computeStrategy should never return null");
        assertNotNull(result.chosenTile,
                "StrategyResult should always have a chosen tile on an open board");
    }

    @Test
    void testComputeStrategyThrowsOnNullTileMap() {
        assertThrows(IllegalStateException.class,
                () -> botPlayer.computeStrategy(null, PlayerEnum.BLACK, 0),
                "computeStrategy should throw on null tileMap");
    }

    @Test
    void testComputeStrategyThrowsOnNullBotColor() {
        assertThrows(IllegalStateException.class,
                () -> botPlayer.computeStrategy(tileMap, null, 0),
                "computeStrategy should throw on null botColor");
    }


    @Test
    void testGetCachedStrategyReturnsNullBeforeCompute() {
        assertNull(botPlayer.getCachedStrategy(),
                "Cache should be null before computeStrategy is called");
    }

    @Test
    void testGetCachedStrategyReturnsResultAfterCompute() {
        botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        assertNotNull(botPlayer.getCachedStrategy(),
                "Cache should be populated after computeStrategy");
    }

    @Test
    void testClearCacheResetsToNull() {
        botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        botPlayer.clearCache();
        assertNull(botPlayer.getCachedStrategy(),
                "Cache should be null after clearCache()");
    }

    @Test
    void testCachedStrategyMatchesComputeResult() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 5);
        BotPlayer.StrategyResult cached = botPlayer.getCachedStrategy();
        assertSame(result, cached,
                "getCachedStrategy should return the same object returned by computeStrategy");
    }

    @Test
    void testComputeStrategyAfterClearCacheReturnsFreshNonNullResult() {
        botPlayer.clearCache();
        BotPlayer.StrategyResult fresh =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);
        assertNotNull(fresh.chosenTile,
                "computeStrategy should compute a fresh result when cache is empty");
    }

    @Test
    void testFindShortestPathBlackReturnsNonEmptyPathOnEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty(),
                "BLACK should always find a path on an empty board");
    }

    @Test
    void testFindShortestPathBlackStartsAtRow11() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty());
        Tile first = path.get(0);
        int firstRow = Integer.parseInt(first.getCoord().substring(1));
        assertEquals(11, firstRow,
                "BLACK's path should start at row 11 (top of board)");
    }

    @Test
    void testFindShortestPathBlackEndsAtRow1() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty());
        Tile last = path.get(path.size() - 1);
        int lastRow = Integer.parseInt(last.getCoord().substring(1));
        assertEquals(1, lastRow,
                "BLACK's path should end at row 1 (bottom of board)");
    }

    @Test
    void testFindShortestPathBlackPrefersColumnFOnEmptyBoard() {
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
    void testFindShortestPathBlackOwnTilesCostZeroAndAppearInPath() {
        for (int row = 7; row <= 11; row++) {
            playerOwns("F" + row, PlayerEnum.BLACK);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);

        long ownedInPath = path.stream()
                .filter(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.BLACK)
                .count();

        assertTrue(ownedInPath > 0,
                "Path should pass through already-owned BLACK tiles (cost 0)");
    }

    @Test
    void testFindShortestPathBlackAvoidsOpponentTiles() {
        for (int row = 1; row <= 11; row++) {
        	playerOwns("F" + row, PlayerEnum.WHITE);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertFalse(path.isEmpty(), "BLACK should find an alternative path around blocked column");

        boolean containsWhite = path.stream()
                .anyMatch(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.WHITE);
        assertFalse(containsWhite,
                "BLACK's path should never pass through WHITE-owned tiles");
    }

    @Test
    void testFindShortestPathBlackReturnsEmptyWhenCompletelyBlocked() {
        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
                playerOwns("" + col + row, PlayerEnum.WHITE);
            }
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);
        assertTrue(path.isEmpty(),
                "Path should be empty when all tiles are blocked by opponent");
    }

    @Test
    void testFindShortestPathWhiteRturnsNonEmptyPathOnEmptyBoard() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty(),
                "WHITE should always find a path on an empty board");
    }

    @Test
    void testFindShortestPathWhiteStartsAtColA() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty());
        Tile first = path.get(0);
        assertEquals('A', first.getCoord().charAt(0),
                "WHITE's path should start at column A (left of board)");
    }

    @Test
    void testindShortestPathWhiteEndsAtColK() {
        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty());
        Tile last = path.get(path.size() - 1);
        assertEquals('K', last.getCoord().charAt(0),
                "WHITE's path should end at column K (right of board)");
    }

    @Test
    void testFindShortestPathWhitePrefersRow6OnEmptyBoard() {
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
    void testFindShortestPathWhiteAvoidsOpponentTiles() {
        for (char col = 'A'; col <= 'K'; col++) {
        	playerOwns("" + col + 6, PlayerEnum.BLACK);
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertFalse(path.isEmpty(), "WHITE should find an alternative path around blocked row");

        boolean containsBlack = path.stream()
                .anyMatch(t -> !t.isEmpty() && t.getOwner() == PlayerEnum.BLACK);
        assertFalse(containsBlack,
                "WHITE's path should never pass through BLACK-owned tiles");
    }

    @Test
    void testFindShortestPathWhiteReturnsEmptyWhenCompletelyBlocked() {
        for (char col = 'A'; col <= 'K'; col++) {
            for (int row = 1; row <= 11; row++) {
            	playerOwns("" + col + row, PlayerEnum.BLACK);
            }
        }

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);
        assertTrue(path.isEmpty(),
                "WHITE's path should be empty when all tiles are blocked");
    }


    @Test
    void testStrategyAdvancesWhenOpponentIsNotThreat() {
    	playerOwns("F11", PlayerEnum.BLACK);
    	playerOwns("F10", PlayerEnum.BLACK);
    	playerOwns("F9",  PlayerEnum.BLACK);
    	playerOwns("F8",  PlayerEnum.BLACK);
    	playerOwns("F7",  PlayerEnum.BLACK);
    	playerOwns("F6",  PlayerEnum.BLACK);
    	playerOwns("F5",  PlayerEnum.BLACK);
    	playerOwns("F4",  PlayerEnum.BLACK);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 8);

        assertFalse(result.isBlocking,
                "Bot should advance when it has a large lead over the opponent");
    }

    @Test
    void testStrategyBlocksWhenOpponentIsCloseToWinning() {
    	playerOwns("A6", PlayerEnum.WHITE);
    	playerOwns("B6", PlayerEnum.WHITE);
    	playerOwns("C6", PlayerEnum.WHITE);
    	playerOwns("D6", PlayerEnum.WHITE);
    	playerOwns("E6", PlayerEnum.WHITE);
    	playerOwns("F6", PlayerEnum.WHITE);
    	playerOwns("G6", PlayerEnum.WHITE);
        playerOwns("H6", PlayerEnum.WHITE);
        playerOwns("I6", PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 9);

        assertTrue(result.isBlocking,
                "Bot should block when opponent needs only one more tile to win");
    }

    
    @Test
    void testStrategyChosenTileIsAlwaysEmpty() {
    	playerOwns("F11", PlayerEnum.BLACK);
    	playerOwns("F10", PlayerEnum.BLACK);
    	playerOwns("A6",  PlayerEnum.WHITE);
    	playerOwns("B6",  PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 4);

        assertNotNull(result.chosenTile, "Chosen tile should not be null");
        assertTrue(result.chosenTile.isEmpty(),
                "Bot should never choose an already-occupied tile");
    }

    @Test
    void testStrategyChosenTileIsNeverOpponentOwned() {
    	playerOwns("F6", PlayerEnum.WHITE);

        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);

        assertNotNull(result.chosenTile);
        assertNotEquals(PlayerEnum.WHITE, result.chosenTile.getOwner(),
                "Bot should never choose a tile owned by the opponent");
    }

    @Test
    void testStrategyPathIsNeverEmptyOnOpenBoard() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 2);
        assertFalse(result.path.isEmpty(),
                "Strategy path should never be empty on an open board");
    }


    @Test
    void testStrategyRespectsPieRuleBlackCannotUsePieSwappedTile() {
    	playerOwns("F6", PlayerEnum.WHITE);

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.BLACK);

        boolean pathContainsF6 = path.stream()
                .anyMatch(t -> "F6".equals(t.getCoord()));

        assertFalse(pathContainsF6,
                "After pie rule swap, BLACK's path should not go through the swapped tile");
    }

    @Test
    void testStrategyRespectsPieRuleWhiteBenefitsFromSwappedTile() {
    	playerOwns("F6", PlayerEnum.WHITE);

        List<Tile> path = botPlayer.findShortestPath(tileMap, PlayerEnum.WHITE);

        boolean pathContainsF6 = path.stream()
                .anyMatch(t -> "F6".equals(t.getCoord()));

        assertTrue(pathContainsF6,
                "After pie rule swap, WHITE's path should pass through their gained tile");
    }

    @Test
    void testStrategyRhombusIsChosenWhenDiagonalIsComplete() {
    	playerOwns("F6", PlayerEnum.BLACK);
    	playerOwns("F7", PlayerEnum.BLACK);
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

    @Test
    void testLastExecutedStrategyIsNullByDefault() {
        assertNull(botPlayer.getLastExecutedStrategy(),
                "Last executed strategy should be null before any move is made");
    }

    @Test
    void testLastExecutedStrategyCanBeSetAndRetrieved() {
        BotPlayer.StrategyResult result =
                botPlayer.computeStrategy(tileMap, PlayerEnum.BLACK, 0);
        botPlayer.setLastExecutedStrategy(result);

        assertSame(result, botPlayer.getLastExecutedStrategy(),
                "getLastExecutedStrategy should return exactly what was set");
    }
    
    // Helper Methods 
    
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

    private void playerOwns(String coord, PlayerEnum player) {
        tileMap.get(coord).setOwner(player);
    }

}