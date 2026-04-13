package com.example.sequax40.model.player;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;

import java.util.*;

public class BotPlayer {

    // Cached strategy so Show Strategy and actual move are always in sync
    private StrategyResult cachedStrategy = null;

    // Strategy that was actually executed in the last bot move
    private StrategyResult lastExecutedStrategy = null;

    /**
     * Uses cached strategy if available, otherwise computes fresh.
     * Guarantees the move matches what Show Strategy displayed.
     */
    public Tile chooseTile(Map<String, Tile> tileMap,
                           PlayerEnum botColor,
                           int moveCount) {
        if (cachedStrategy != null && cachedStrategy.chosenTile != null) {
            return cachedStrategy.chosenTile;
        }
        // No cache — compute fresh
        return computeStrategy(tileMap, botColor, moveCount).chosenTile;
    }


    /**
     * Computes the bot's strategy for this turn and caches it.
     * Called from triggerBotIfNeeded so both Show Strategy
     * and makeBotMove use the exact same decision.
     *
     * Coordinate system (letter = col A-K, number = row 1-11):
     *   BLACK connects row 11 (top) → row 1 (bottom)
     *   WHITE connects col A (left) → col K (right)
     */
    public StrategyResult computeStrategy(Map<String, Tile> tileMap,
                                          PlayerEnum botColor,
                                          int moveCount) {

        PlayerEnum opponent = (botColor == PlayerEnum.BLACK)
                ? PlayerEnum.WHITE
                : PlayerEnum.BLACK;

        List<Tile> botPath = findShortestPath(tileMap, botColor);
        List<Tile> opponentPath = findShortestPath(tileMap, opponent);

        int botCost = countEmptyTiles(botPath);
        int opponentCost = countEmptyTiles(opponentPath);

        boolean blocking = opponentCost < botCost;

        List<Tile> chosenPath;
        Tile chosenTile;

        if (moveCount == 0) {
            Tile centre = tileMap.get("F6");
            if (centre != null && centre.isEmpty()) {
                cachedStrategy = new StrategyResult(List.of(centre), false, centre);
                return cachedStrategy;
            }
        }

        if (blocking) {
            chosenTile = bestTileFromPath(opponentPath, tileMap, botColor);
            chosenPath = (chosenTile != null) ? opponentPath : botPath;
        } else {
            chosenTile = bestTileFromPath(botPath, tileMap, botColor);
            chosenPath = botPath;
        }

        // 🔥 FIX: ensure chosen tile is ALWAYS legal rhombus
        if (chosenTile != null && chosenTile.getShape() == ShapeEnum.RHOMBUS) {
            if (!isRhombusValidForPlayer(chosenTile, tileMap, botColor)) {
                chosenTile = bestTileFromPath(botPath, tileMap, botColor);
                chosenPath = botPath;
                blocking = false;
            }
        }

        cachedStrategy = new StrategyResult(chosenPath, blocking, chosenTile);
        return cachedStrategy;
    }


    /** Returns the cached strategy — must call computeStrategy first */
    public StrategyResult getCachedStrategy() {
        return cachedStrategy;
    }


    /** Clears the cache after the bot has played */
    public void clearCache() {
        cachedStrategy = null;
    }


    /**
     * Picks the best tile to play from a path.
     * First pass  — prefers a valid empty rhombus (free diagonal shortcut).
     * Second pass — falls back to the first empty octagon.
     */
    private Tile bestTileFromPath(List<Tile> path,
                                  Map<String, Tile> tileMap,
                                  PlayerEnum player) {
        // First pass: valid rhombus on the path
        for (Tile tile : path) {
            if (!tile.isEmpty()) continue;
            if (tile.getShape() != ShapeEnum.RHOMBUS) continue;
            if (isRhombusValidForPlayer(tile, tileMap, player)) {
                return tile;
            }
        }

        // Second pass: first empty octagon
        return path.stream()
                .filter(t -> t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
                .min(Comparator.comparingInt(t -> centrePenalty(t.getCoord(), player)))
                .orElse(null);
    }




    /**
     * Dijkstra's algorithm to find the shortest path for a player.
     *
     * Coordinate system confirmed from screenshot:
     *   Letter (A-K) = COLUMN, left to right
     *   Number (1-11) = ROW,  11 = top,  1 = bottom
     *
     * BLACK: row 11 (top) → row 1 (bottom)   start=11, goal=1
     * WHITE: col A  (left) → col K (right)    start=A,  goal=K
     *
     * Cost:
     *   Own tile already placed = 0  (free)
     *   Empty tile              = 1  (needs claiming)
     *   Opponent tile           = blocked, skipped entirely
     */
    public List<Tile> findShortestPath(Map<String, Tile> tileMap, PlayerEnum player) {

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String>  prev = new HashMap<>();

        PriorityQueue<CoordCost> pq = new PriorityQueue<>(
                Comparator.comparingInt((CoordCost c) -> c.cost)
                        .thenComparingInt(c -> c.centrePenalty)
        );

        // Initialise all tiles to infinity
        for (String coord : tileMap.keySet()) {
            dist.put(coord, Integer.MAX_VALUE);
        }

        // Seed starting-edge tiles
        for (Tile tile : tileMap.values()) {
            if (!isStartEdge(tile, player)) continue;
            if (isBlockedBy(tile, player))  continue;

            int cost = tileCost(tile, player);
            if (cost < dist.get(tile.getCoord())) {
                dist.put(tile.getCoord(), cost);
                prev.put(tile.getCoord(), null);
                pq.offer(new CoordCost(
                        tile.getCoord(),
                        cost,
                        centrePenalty(tile.getCoord(), player)
                ));
            }
        }

        // Dijkstra main loop
        while (!pq.isEmpty()) {
            CoordCost current = pq.poll();

            if (current.cost > dist.getOrDefault(current.coord, Integer.MAX_VALUE)) continue;

            Tile currentTile = tileMap.get(current.coord);
            if (currentTile == null) continue;

            // Goal reached
            if (isGoalEdge(currentTile, player)) {
                return reconstructPath(prev, current.coord, tileMap);
            }

            // Explore neighbours
            for (Tile neighbor : getPathNeighbors(currentTile, tileMap, player)) {
                if (isBlockedBy(neighbor, player)) continue;

                int newCost = current.cost + tileCost(neighbor, player);
                if (newCost < dist.getOrDefault(neighbor.getCoord(), Integer.MAX_VALUE)) {
                    dist.put(neighbor.getCoord(), newCost);
                    prev.put(neighbor.getCoord(), current.coord);
                    pq.offer(new CoordCost(
                            neighbor.getCoord(),
                            newCost,
                            centrePenalty(neighbor.getCoord(), player)
                    ));
                }
            }
        }

        return Collections.emptyList();
    }


    /**
     * Reconstructs the path from prev map, walking back from goal to start.
     */
    private List<Tile> reconstructPath(Map<String, String> prev,
                                       String goalCoord,
                                       Map<String, Tile> tileMap) {
        List<Tile> path = new ArrayList<>();
        String current = goalCoord;

        while (current != null) {
            Tile t = tileMap.get(current);
            if (t != null) path.add(t);
            current = prev.get(current);
        }

        Collections.reverse(path);
        return path;
    }


    /**
     * Returns traversable neighbours for pathfinding.
     *
     * Letter = col (A-K), Number = row (1-11)
     * Up   = row number increases (toward 11)
     * Down = row number decreases (toward 1)
     */
    private List<Tile> getPathNeighbors(Tile tile,
                                        Map<String, Tile> tileMap,
                                        PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();

        if (isOctagonTile(tile)) {
            char col = tile.getCoord().charAt(0);
            int  row = Integer.parseInt(tile.getCoord().substring(1));

            // Up and down: row number changes
            addTile(neighbors, tileMap, "" + col + (row + 1)); // up   (higher number)
            addTile(neighbors, tileMap, "" + col + (row - 1)); // down (lower number)
            // Left and right: column letter changes
            addTile(neighbors, tileMap, "" + (char)(col - 1) + row); // left
            addTile(neighbors, tileMap, "" + (char)(col + 1) + row); // right

            // Rhombus diagonal shortcuts
            for (Tile rhombus : tileMap.values()) {
                if (rhombus.getShape() != ShapeEnum.RHOMBUS) continue;
                if (isBlockedBy(rhombus, player)) continue;

                Tile[] corners = getRhombusCorners(rhombus, tileMap);
                Tile t1 = corners[0], t2 = corners[1],
                        t3 = corners[2], t4 = corners[3];

                if (tile.equals(t1) && t3 != null) neighbors.add(t3);
                if (tile.equals(t3) && t1 != null) neighbors.add(t1);
                if (tile.equals(t2) && t4 != null) neighbors.add(t4);
                if (tile.equals(t4) && t2 != null) neighbors.add(t2);
            }

        } else {
            // Rhombus — neighbours are its 4 corner octagons
            Tile[] corners = getRhombusCorners(tile, tileMap);
            for (Tile c : corners) {
                if (c != null) neighbors.add(c);
            }
        }

        return neighbors;
    }


    /**
     * Cost to traverse a tile.
     * Own placed tile = 0 (free), everything else = 1.
     */
    private int tileCost(Tile tile, PlayerEnum player) {
        if (!tile.isEmpty() && tile.getOwner() == player) return 0;

        // Encourage rhombus usage
        if (tile.getShape() == ShapeEnum.RHOMBUS) return 0;

        return 1;
    }


    /**
     * True if the tile is owned by the opponent — impassable.
     * Reads ownership directly from tile so pie rule swaps are respected.
     */
    private boolean isBlockedBy(Tile tile, PlayerEnum player) {
        return !tile.isEmpty() && tile.getOwner() != player;
    }


    /**
     * Start edge for Dijkstra seeding.
     *
     * BLACK: starts at row 11 (top of screen)
     * WHITE: starts at col A (left of screen)
     *
     * Rhombuses are never on the board edge.
     */
    private boolean isStartEdge(Tile tile, PlayerEnum player) {
        if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
        String coord = tile.getCoord();
        if (player == PlayerEnum.BLACK) {
            // BLACK: top edge = row 11
            int row = Integer.parseInt(coord.substring(1));
            return row == 11;
        } else {
            // WHITE: left edge = col A
            return coord.charAt(0) == 'A';
        }
    }


    /**
     * Goal edge for Dijkstra termination.
     *
     * BLACK: goal is row 1 (bottom of screen)
     * WHITE: goal is col K (right of screen)
     *
     * Rhombuses are never on the board edge.
     */
    private boolean isGoalEdge(Tile tile, PlayerEnum player) {
        if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
        String coord = tile.getCoord();
        if (player == PlayerEnum.BLACK) {
            // BLACK: bottom edge = row 1
            int row = Integer.parseInt(coord.substring(1));
            return row == 1;
        } else {
            // WHITE: right edge = col K
            return coord.charAt(0) == 'K';
        }
    }


    /**
     * Centre penalty used as a Dijkstra tiebreaker.
     *
     * BLACK (top→bottom, row changes):
     *   Penalise distance from centre column F.
     *   Keeps vertical path through the middle column.
     *
     * WHITE (left→right, col changes):
     *   Penalise distance from centre row 6.
     *   Keeps horizontal path through the middle row.
     *
     * Rhombuses get 0 penalty — never penalise shortcuts.
     */
    private int centrePenalty(String coord, PlayerEnum player) {
        if (coord.contains("_")) return 0;

        if (player == PlayerEnum.BLACK) {
            // Prefer centre column F
            char col = coord.charAt(0);
            return Math.abs(col - 'F');
        } else {
            // Prefer centre row 6
            int row = Integer.parseInt(coord.substring(1));
            return Math.abs(row - 6);
        }
    }


    /** True if the tile is an octagon (coord has no underscore) */
    private boolean isOctagonTile(Tile tile) {
        return !tile.getCoord().contains("_");
    }


    /** Adds a tile to the list if it exists in the tileMap */
    private void addTile(List<Tile> list, Map<String, Tile> tileMap, String coord) {
        Tile t = tileMap.get(coord);
        if (t != null) list.add(t);
    }


    /**
     * Extracts the 4 corner octagons of a rhombus.
     *
     * Rhombus coord format: "AB_1_2"
     *   c1 = 'A', c2 = 'B' (column letters)
     *   r1 = "1", r2 = "2" (row numbers)
     *
     * Corners: [c1r1, c1r2, c2r2, c2r1]
     */
    private Tile[] getRhombusCorners(Tile rhombus, Map<String, Tile> tileMap) {
        String id = rhombus.getCoord();

        char c1 = id.charAt(0);
        char c2 = id.charAt(1);

        int i1 = id.indexOf('_');
        int i2 = id.indexOf('_', i1 + 1);

        String r1 = id.substring(i1 + 1, i2);
        String r2 = id.substring(i2 + 1);

        return new Tile[]{
                tileMap.get("" + c1 + r1),
                tileMap.get("" + c1 + r2),
                tileMap.get("" + c2 + r2),
                tileMap.get("" + c2 + r1)
        };
    }


    /**
     * Counts empty octagons in a path = number of moves still needed.
     */
    private int countEmptyTiles(List<Tile> path) {
        return (int) path.stream()
                .filter(t -> t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
                .count();
    }

    public StrategyResult getLastExecutedStrategy() {
        return lastExecutedStrategy;
    }


    /**
     * Strategy result wrapper.
     * path        = full path to highlight in Show Strategy
     * isBlocking  = true if bot is blocking opponent, false if advancing
     * chosenTile  = exact tile the bot will play (shown in yellow)
     */
    public static class StrategyResult {
        public final List<Tile> path;
        public final boolean isBlocking;
        public final Tile chosenTile;

        public StrategyResult(List<Tile> path, boolean isBlocking, Tile chosenTile) {
            this.path       = path;
            this.isBlocking = isBlocking;
            this.chosenTile = chosenTile;
        }
    }


    /**
     * Priority queue entry.
     * cost          = Dijkstra cost so far
     * centrePenalty = tiebreaker to prefer centre-running paths
     */
    private static class CoordCost {
        String coord;
        int cost;
        int centrePenalty;

        CoordCost(String coord, int cost, int centrePenalty) {
            this.coord         = coord;
            this.cost          = cost;
            this.centrePenalty = centrePenalty;
        }
    }

    private boolean isRhombusValidForPlayer(Tile rhombus, Map<String, Tile> tileMap, PlayerEnum player) {

        Tile[] corners = getRhombusCorners(rhombus, tileMap);

        Tile t1 = corners[0];
        Tile t2 = corners[1];
        Tile t3 = corners[2];
        Tile t4 = corners[3];

        boolean diag1 = t1 != null && t3 != null
                && !t1.isEmpty() && !t3.isEmpty()
                && t1.getOwner() == player
                && t3.getOwner() == player;

        boolean diag2 = t2 != null && t4 != null
                && !t2.isEmpty() && !t4.isEmpty()
                && t2.getOwner() == player
                && t4.getOwner() == player;

        return diag1 || diag2;
    }

    public void setLastExecutedStrategy(StrategyResult strategy) {
        this.lastExecutedStrategy = strategy;
    }

    public void cacheStrategy(StrategyResult strategy) {
        this.cachedStrategy = strategy;
    }

}