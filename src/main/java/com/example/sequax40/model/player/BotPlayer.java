package com.example.sequax40.model.player;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Tile;

import java.util.*;

public class BotPlayer {

    /**
     * Hybrid strategy:
     * 1. First move always plays centre (F6)
     * 2. Find bot's shortest path and opponent's shortest path
     * 3. If opponent is within 2 moves of matching bot's progress → block
     * 4. Otherwise → advance along bot's own path
     * 5. Prefers rhombus placements when valid
     *
     * Coordinate system (confirmed from screenshot):
     *   Letter (A-K) = COLUMN, left to right
     *   Number (1-11) = ROW, top to bottom
     *
     * BLACK connects row 1 (top) → row 11 (bottom)  — number changes
     * WHITE connects col A (left) → col K (right)    — letter changes
     */
    public Tile chooseTile(Map<String, Tile> tileMap, PlayerEnum botColor, int moveCount) {

        // First move ever — always play centre (F6 = col F, row 6)
        if (moveCount == 0) {
            Tile centre = tileMap.get("F6");
            if (centre != null && centre.isEmpty()) return centre;
        }

        PlayerEnum opponent = (botColor == PlayerEnum.BLACK)
                ? PlayerEnum.WHITE
                : PlayerEnum.BLACK;

        List<Tile> botPath      = findShortestPath(tileMap, botColor);
        List<Tile> opponentPath = findShortestPath(tileMap, opponent);

        int botCost      = countEmptyTiles(botPath);
        int opponentCost = countEmptyTiles(opponentPath);

        // Block if opponent is within 2 moves of matching bot's progress
        boolean opponentIsThreat = opponentCost <= botCost + 2;

        if (opponentIsThreat && !opponentPath.isEmpty()) {
            Tile block = bestTileFromPath(opponentPath, tileMap, botColor);
            if (block != null) return block;
        }

        // Otherwise advance own path
        if (!botPath.isEmpty()) {
            Tile advance = bestTileFromPath(botPath, tileMap, botColor);
            if (advance != null) return advance;
        }

        // Fallback: any empty octagon
        return tileMap.values().stream()
                .filter(t -> t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
                .findFirst()
                .orElse(null);
    }


    /**
     * Returns the bot's own shortest path for the Show Strategy visualisation.
     */
    public List<Tile> getStrategyPath(Map<String, Tile> tileMap,
                                      PlayerEnum botColor,
                                      int moveCount) {
        // If this is the first move, strategy is simply F6
        if (moveCount == 0) {
            Tile centre = tileMap.get("F6");
            if (centre != null && centre.isEmpty()) {
                return List.of(centre);
            }
        }
        return findShortestPath(tileMap, botColor);
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
                .findFirst()
                .orElse(null);
    }


    /**
     * Mirrors GameManager's isRhombusValid check.
     * A rhombus is valid if at least one diagonal pair of corners
     * is fully owned by the given player.
     */
    private boolean isRhombusValidForPlayer(Tile rhombus,
                                            Map<String, Tile> tileMap,
                                            PlayerEnum player) {
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


    /**
     * Dijkstra's algorithm to find the shortest path for a player.
     *
     * BLACK: row 1 (top) → row 11 (bottom)   number = row
     * WHITE: col A (left) → col K (right)     letter = col
     *
     * Cost:
     *   Own tile already placed = 0  (free)
     *   Empty tile              = 1  (needs claiming)
     *   Opponent tile           = blocked, skipped entirely
     *
     * Tiebreaker: prefer tiles closer to the centre of the board
     * so the path runs through the middle rather than hugging an edge.
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
     * Reconstructs the path from the prev map, walking back from goal to start.
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
     *
     * Up/down = row number changes (±1)
     * Left/right = column letter changes (±1 in ASCII)
     */
    private List<Tile> getPathNeighbors(Tile tile,
                                        Map<String, Tile> tileMap,
                                        PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();

        if (isOctagonTile(tile)) {
            char col = tile.getCoord().charAt(0);        // letter = column
            int  row = Integer.parseInt(tile.getCoord().substring(1)); // number = row

            // Up and down: row number changes
            addTile(neighbors, tileMap, "" + col + (row - 1)); // up
            addTile(neighbors, tileMap, "" + col + (row + 1)); // down
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
     * Own placed tile = 0, everything else empty = 1.
     */
    private int tileCost(Tile tile, PlayerEnum player) {
        if (!tile.isEmpty() && tile.getOwner() == player) return 0;
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
     * Letter = col, Number = row
     *
     * BLACK starts at row 1  → number part == 1
     * WHITE starts at col A  → letter part == 'A'
     *
     * Rhombuses are never on the board edge.
     */
    private boolean isStartEdge(Tile tile, PlayerEnum player) {
        if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
        String coord = tile.getCoord();
        if (player == PlayerEnum.BLACK) {
            // BLACK: top edge = row 1
            int row = Integer.parseInt(coord.substring(1));
            return row == 1;
        } else {
            // WHITE: left edge = col A
            return coord.charAt(0) == 'A';
        }
    }


    /**
     * Goal edge for Dijkstra termination.
     *
     * BLACK goal is row 11 → number part == 11
     * WHITE goal is col K  → letter part == 'K'
     *
     * Rhombuses are never on the board edge.
     */
    private boolean isGoalEdge(Tile tile, PlayerEnum player) {
        if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
        String coord = tile.getCoord();
        if (player == PlayerEnum.BLACK) {
            // BLACK: bottom edge = row 11
            int row = Integer.parseInt(coord.substring(1));
            return row == 11;
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
     *   This keeps the vertical path running through the middle column.
     *
     * WHITE (left→right, col changes):
     *   Penalise distance from centre row 6.
     *   This keeps the horizontal path running through the middle row.
     *
     * Rhombuses get 0 penalty — never penalise shortcuts.
     */
    private int centrePenalty(String coord, PlayerEnum player) {
        if (coord.contains("_")) return 0;

        if (player == PlayerEnum.BLACK) {
            // BLACK goes top→bottom — prefer centre column F (ASCII 70)
            char col = coord.charAt(0);
            return Math.abs(col - 'F');
        } else {
            // WHITE goes left→right — prefer centre row 6
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
     *   c1 = 'A' (first col letter)
     *   c2 = 'B' (second col letter)
     *   r1 = "1" (first row number)
     *   r2 = "2" (second row number)
     *
     * Corners: [c1r1, c1r2, c2r2, c2r1]
     */
    private Tile[] getRhombusCorners(Tile rhombus, Map<String, Tile> tileMap) {
        String id = rhombus.getCoord(); // e.g. "AB_1_2"

        char c1 = id.charAt(0); // first col letter
        char c2 = id.charAt(1); // second col letter

        int i1 = id.indexOf('_');
        int i2 = id.indexOf('_', i1 + 1);

        String r1 = id.substring(i1 + 1, i2); // first row number
        String r2 = id.substring(i2 + 1);     // second row number

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


    /**
     * Priority queue entry.
     * cost          = Dijkstra cost so far.
     * centrePenalty = tiebreaker to prefer centre-running paths.
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
}