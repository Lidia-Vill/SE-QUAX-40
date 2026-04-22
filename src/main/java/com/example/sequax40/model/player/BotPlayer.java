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
     
        /*
         * Uses cached strategy if available, otherwise computes fresh.
         * Guarantees the move matches what Show Strategy displayed.
         */
        public Tile chooseTile(Map<String, Tile> tileMap, PlayerEnum botColor, int moveCount) {
            if (cachedStrategy != null && cachedStrategy.chosenTile != null) {
                return cachedStrategy.chosenTile;
            }
            return computeStrategy(tileMap, botColor, moveCount).chosenTile;
        }
     
     
        /*
         * Computes the bot's strategy for this turn and caches it.
         *
         * Coordinate system (letter = col A-K, number = row 1-11):
         *   BLACK connects row 11 (top) → row 1 (bottom)
         *   WHITE connects col A (left) → col K (right)
         */
        public StrategyResult computeStrategy(Map<String, Tile> tileMap, PlayerEnum botColor, int moveCount) {
    
            if (tileMap == null || botColor == null) {
                throw new IllegalStateException("Invalid strategy input");
            }
    
            PlayerEnum opponent = (botColor == PlayerEnum.BLACK)
                    ? PlayerEnum.WHITE : PlayerEnum.BLACK;
    
            // Opening move: take centre
            if (moveCount == 0) {
                Tile centre = tileMap.get("F6");
                if (centre != null && centre.isEmpty()) {
                    cachedStrategy = new StrategyResult(List.of(centre), Collections.emptyList(), false, centre);
                    return cachedStrategy;
                }
            }
            
            List<Tile> botPath = findShortestPath(tileMap, botColor);
            List<Tile> opponentPath = findShortestPath(tileMap, opponent);
            
            int botCost = countEmptyTiles(botPath);
            int opponentCost = countEmptyTiles(opponentPath);
            
            boolean blocking = opponentCost <= botCost + 4;
            
    
            Tile chosenTile;
            
            if (blocking && !opponentPath.isEmpty()) {
                chosenTile = bestTileFromPath(opponentPath, tileMap, botColor);
            
                // If the blocking tile is an invalid rhombus, fall back to own path
                if (chosenTile != null && chosenTile.getShape() == ShapeEnum.RHOMBUS
                        && !isRhombusValidForPlayer(chosenTile, tileMap, botColor)) {
                    chosenTile = bestTileFromPath(botPath, tileMap, botColor);
                    blocking   = false;
                }
                
                if (chosenTile == null) {
                    chosenTile = bestTileFromPath(botPath, tileMap, botColor);
                    blocking   = false;
                }
                
            } 
            else {
                chosenTile = bestTileFromPath(botPath, tileMap, botColor);
            }
            
            if (chosenTile == null) {
                chosenTile = tileMap.values().stream()
                .filter(t -> t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
                .findFirst()
                .orElse(null);
            }
            
            if (chosenTile == null) {
                cachedStrategy = new StrategyResult(Collections.emptyList(), Collections.emptyList(), false, null);   
                return cachedStrategy;
            }
        
            // HAS CAMBIADOO VUELVE SINO
    
            List<Tile> displayPath = new ArrayList<>(botPath);
            if (!displayPath.contains(chosenTile)) {
                displayPath.add(chosenTile);
            }


            cachedStrategy = new StrategyResult(displayPath, opponentPath, blocking, chosenTile);
            return cachedStrategy;
        }
     
     
        /* Returns the cached strategy — must call computeStrategy first */
        public StrategyResult getCachedStrategy() {
            return cachedStrategy;
        }
     
        /* Clears the cache after the bot has played */
        public void clearCache() {
            cachedStrategy = null;
        }
     
     
        /*
         * Picks the best tile to play from a path.
         *
         * Priority 1 – a valid EMPTY RHOMBUS (diagonal shortcut whose placement
         *              is legal, i.e. two of the player's own tiles already occupy
         *              one diagonal of that rhombus).
         * Priority 2 – the first empty OCTAGON closest to the board centre.
         */
        private Tile bestTileFromPath(List<Tile> path,
                                      Map<String, Tile> tileMap,
                                      PlayerEnum player) {

            // PASS 1: strict rhombus priority
            for (Tile tile : path) {
                if (tile == null || !tile.isEmpty()) continue;
                if (tile.getShape() == ShapeEnum.RHOMBUS
                        && isRhombusValidForPlayer(tile, tileMap, player)) {
                    return tile;
                }
            }

            // PASS 2: octagon fallback — prefer tiles near the centre
            return path.stream()
                    .filter(t -> t != null
                            && t.isEmpty()
                            && t.getShape() == ShapeEnum.OCTAGON)
                    .min(Comparator.comparingInt(
                            t -> centrePenalty(t.getCoord(), player)))
                    .orElse(null);
        }
     
     
        /*
         * Dijkstra's algorithm to find the shortest path for a player.
         *
         * Rhombuses are treated as FIRST-CLASS NODES in the graph so that:
         *   • they appear in the returned path (enabling correct UI highlighting)
         *   • their diagonal connectivity is properly accounted for
         *
         * Graph edges:
         *   Octagon   adjacent octagons (up/down/left/right)
         *   Octagon  → every rhombus for which this octagon is a corner
         *   Rhombus  → its 4 corner octagons
         *
         * Cost:
         *   Own tile already placed = 0   (free to traverse)
         *   Empty rhombus           = 1   (cheaper than octagon → preferred)
         *   Empty octagon           = 2
         *   Opponent tile           = ∞   (impassable / skipped)
         */
        public List<Tile> findShortestPath(Map<String, Tile> tileMap, PlayerEnum player) {
     
            // Pre-build octagon → adjacent rhombus list for performance
            Map<String, List<Tile>> octToRhombus = buildOctagonRhombusIndex(tileMap);
     
            Map<String, Integer> dist = new HashMap<>();
            Map<String, String>  prev = new HashMap<>();
     
            PriorityQueue<CoordCost> pq = new PriorityQueue<>(
                    Comparator.comparingInt((CoordCost c) -> c.cost)
                              .thenComparingInt(c -> c.centrePenalty)
            );
     
            for (String coord : tileMap.keySet()) {
                dist.put(coord, Integer.MAX_VALUE);
            }
     
            // Seed starting-edge tiles (only octagons are ever on the edge)
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
     
                if (isGoalEdge(currentTile, player)) {
                    return reconstructPath(prev, current.coord, tileMap);
                }
     
                for (Tile neighbor : getPathNeighbors(currentTile, tileMap, player, octToRhombus)) {
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
     
     
        /*
         * Pre-builds a map of octagon coord → list of rhombuses that share that octagon
         * as a corner.  Called once per findShortestPath invocation to avoid O(n²) lookups
         * inside the hot loop.
         */
        private Map<String, List<Tile>> buildOctagonRhombusIndex(Map<String, Tile> tileMap) {
            Map<String, List<Tile>> index = new HashMap<>();
     
            for (Tile rhombus : tileMap.values()) {
                if (rhombus.getShape() != ShapeEnum.RHOMBUS) continue;
     
                Tile[] corners = getRhombusCorners(rhombus, tileMap);
                for (Tile corner : corners) {
                    if (corner == null) continue;
                    index.computeIfAbsent(corner.getCoord(), k -> new ArrayList<>()).add(rhombus);
                }
            }
            return index;
        }
     
     
        /* Reconstructs the path by walking prev map from goal → start. */
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
     
     
        /*
         * Returns traversable neighbours for pathfinding.
         *
         * OCTAGONS connect to:
         *   • up / down / left / right adjacent octagons
         *   • every rhombus of which this octagon is a corner
         *     (the rhombus is an intermediate node — we do NOT jump straight to the
         *      diagonal octagon; that jump now goes octagon→rhombus→octagon, which
         *      causes rhombuses to appear in the reconstructed path and be highlighted)
         *
         * RHOMBUSES connect to their 4 corner octagons.
         */
        private List<Tile> getPathNeighbors(Tile tile,
                                            Map<String, Tile> tileMap,
                                            PlayerEnum player,
                                            Map<String, List<Tile>> octToRhombus) {
            List<Tile> neighbors = new ArrayList<>();
     
            if (isOctagonTile(tile)) {
                char col = tile.getCoord().charAt(0);
                int  row = Integer.parseInt(tile.getCoord().substring(1));
     
                // Cardinal neighbours
                addTile(neighbors, tileMap, "" + col + (row + 1)); // up
                addTile(neighbors, tileMap, "" + col + (row - 1)); // down
                addTile(neighbors, tileMap, "" + (char)(col - 1) + row); // left
                addTile(neighbors, tileMap, "" + (char)(col + 1) + row); // right
     
                // Rhombuses that share this octagon as a corner
                List<Tile> adjacentRhombuses = octToRhombus.getOrDefault(tile.getCoord(), Collections.emptyList());
                for (Tile rhombus : adjacentRhombuses) {
                    if (!isBlockedBy(rhombus, player)) {
                        neighbors.add(rhombus);
                    }
                }
     
            } else {
                // Rhombus → connect to all 4 corner octagons
                Tile[] corners = getRhombusCorners(tile, tileMap);
                for (Tile c : corners) {
                    if (c != null) neighbors.add(c);
                }
            }
     
            return neighbors;
        }
     
     
        /*
         * Cost to enter a tile.
         *
         *   Own tile    = 0  (already claimed, free)
         *   Empty rhombus = 1  (diagonal shortcut — cheaper than octagon)
         *   Empty octagon = 2
         */
        private int tileCost(Tile tile, PlayerEnum player) {
            if (!tile.isEmpty() && tile.getOwner() == player) return 0;
            if (tile.getShape() == ShapeEnum.RHOMBUS)         return 1;
            return 2; // empty octagon
        }
     
     
        /* True if the tile is owned by the opponent — impassable. */
        private boolean isBlockedBy(Tile tile, PlayerEnum player) {
            return !tile.isEmpty() && tile.getOwner() != player;
        }
     
     
        /*
         * Start edge for Dijkstra seeding.
         * BLACK: row 11 (top).  WHITE: col A (left).
         * Rhombuses are never on the board edge.
         */
        private boolean isStartEdge(Tile tile, PlayerEnum player) {
            if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
            String coord = tile.getCoord();
            if (player == PlayerEnum.BLACK) {
                return Integer.parseInt(coord.substring(1)) == 11;
            } else {
                return coord.charAt(0) == 'A';
            }
        }
     
     
        /*
         * Goal edge for Dijkstra termination.
         * BLACK: row 1 (bottom).  WHITE: col K (right).
         * Rhombuses are never on the board edge.
         */
        private boolean isGoalEdge(Tile tile, PlayerEnum player) {
            if (tile.getShape() == ShapeEnum.RHOMBUS) return false;
            String coord = tile.getCoord();
            if (player == PlayerEnum.BLACK) {
                return Integer.parseInt(coord.substring(1)) == 1;
            } else {
                return coord.charAt(0) == 'K';
            }
        }
     
     
        /*
         * Centre-column / centre-row tiebreaker.
         * BLACK → prefer column F.  WHITE → prefer row 6.
         * Rhombuses (coord contains '_') get penalty 0 — never penalise shortcuts.
         */
        private int centrePenalty(String coord, PlayerEnum player) {
            if (coord.contains("_")) return 0;
     
            if (player == PlayerEnum.BLACK) {
                return Math.abs(coord.charAt(0) - 'F');
            } else {
                return Math.abs(Integer.parseInt(coord.substring(1)) - 6);
            }
        }
     
     
        /** True if the tile is an octagon (coord has no underscore). */
        private boolean isOctagonTile(Tile tile) {
            return !tile.getCoord().contains("_");
        }
     
        /** Adds a tile to the list if it exists in the tileMap. */
        private void addTile(List<Tile> list, Map<String, Tile> tileMap, String coord) {
            Tile t = tileMap.get(coord);
            if (t != null) list.add(t);
        }
     
     
        /*
         * Extracts the 4 corner octagons of a rhombus.
         *
         * Rhombus coord format: "AB_1_2"
         *   c1='A', c2='B' (columns),  r1="1", r2="2" (rows)
         * Corners: [c1r1, c1r2, c2r2, c2r1]
         * Diagonals: (t1,t3) and (t2,t4).
         */
        private Tile[] getRhombusCorners(Tile rhombus, Map<String, Tile> tileMap) {
            String id = rhombus.getCoord();
            char c1 = id.charAt(0);
            char c2 = id.charAt(1);
            int  i1 = id.indexOf('_');
            int  i2 = id.indexOf('_', i1 + 1);
            String r1 = id.substring(i1 + 1, i2);
            String r2 = id.substring(i2 + 1);
     
            return new Tile[]{
                    tileMap.get("" + c1 + r1),
                    tileMap.get("" + c1 + r2),
                    tileMap.get("" + c2 + r2),
                    tileMap.get("" + c2 + r1)
            };
        }
     
     
        /*
         * Counts empty octagons in a path = number of moves still needed.
         * Rhombuses are counted separately since they too require a move.
         */
        private int countEmptyTiles(List<Tile> path) {
            return (int) path.stream()
                    .filter(t -> t.isEmpty())
                    .count();
        }
     
     
        public StrategyResult getLastExecutedStrategy() { return lastExecutedStrategy; }
        public void setLastExecutedStrategy(StrategyResult strategy) { this.lastExecutedStrategy = strategy; }
        public void cacheStrategy(StrategyResult strategy) { this.cachedStrategy = strategy; }
     
     
        /*
         * Validates whether placing on this rhombus is legal for the given player.
         * Legal iff the player already owns BOTH octagons on one diagonal of the rhombus.
         */
        private boolean isRhombusValidForPlayer(Tile rhombus,
                                                Map<String, Tile> tileMap,
                                                PlayerEnum player) {
            Tile[] corners = getRhombusCorners(rhombus, tileMap);
            Tile t1 = corners[0], t2 = corners[1], t3 = corners[2], t4 = corners[3];
     
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
     
     
        /*
         * Strategy result wrapper.
         * path       = full path to highlight in Show Strategy
         * isBlocking = true if bot is intercepting opponent, false if advancing
         * chosenTile = exact tile the bot will play (shown in yellow)
         */
        public static class StrategyResult {
            public final List<Tile> path;
            public final List<Tile> opponentPath;
            public final boolean isBlocking;
            public final Tile chosenTile;

            public StrategyResult(List<Tile> path, List<Tile> opponentPath,
                                  boolean isBlocking, Tile chosenTile) {
                this.path         = path;
                this.opponentPath = opponentPath;
                this.isBlocking   = isBlocking;
                this.chosenTile   = chosenTile;
            }
        }
     
        /* Priority queue entry for Dijkstra. */
        private static class CoordCost {
            final String coord;
            final int cost;
            final int centrePenalty;
     
            CoordCost(String coord, int cost, int centrePenalty) {
                this.coord         = coord;
                this.cost          = cost;
                this.centrePenalty = centrePenalty;
            }
        }




    }