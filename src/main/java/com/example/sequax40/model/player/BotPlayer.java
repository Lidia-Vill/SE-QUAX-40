    package com.example.sequax40.model.player;
     
    import com.example.sequax40.enums.PlayerEnum;
    import com.example.sequax40.enums.ShapeEnum;
    import com.example.sequax40.model.board.Tile;
     
    import java.util.*;
    
    /*
     * Computes and caches the BOT's move strategy each turn
     * 	Uses Dijkstra's algorithm to find the optimal path across the board
     * 	switching to a blocking strategy when the opponent is closer to winning 
     * 
     * Coordinate System (letter = col A-K, number = row 1-11
     * 	BLACK connects row 11 (top) to row 1 (bottom)
     * 	WHITE connects col A (left) to col K (right)
     */
     
    public class BotPlayer {
        
    	// Constants
    	
    	private static final String CENTRE_TILE_COORD = "F6";
    	private static final int BLOCKING_THRESHOLD = 4;
    	
    	// State
    	
        private StrategyResult cachedStrategy = null;
        private StrategyResult lastExecutedStrategy = null;
     
        
        public StrategyResult computeStrategy(Map<String, Tile> tileMap, 
        										PlayerEnum botColour, 
        										int moveCount) {
        	if(tileMap == null || botColour == null) {
        		throw new IllegalStateException("Invalid Strategy Input");
        	}
        	StrategyContext context = new StrategyContext(tileMap, botColour);
        	if(isOpeningMove(moveCount)) {
        		return computeOpeningStrategy(context);
        	}
        	return computeNormalStrategy(context);
        }
        
        public StrategyResult getCachedStrategy() {
        	return cachedStrategy;
        }
        
        public void clearCache() {
        	cachedStrategy = null;
        }
        
              
        // - Strategy Selection  ----------------------------------------------------------------------------------------
        
        private boolean isOpeningMove(int moveCount) {
        	return moveCount == 0;
        }
        
        private StrategyResult computeOpeningStrategy(StrategyContext context) {
        	Tile centre = context.tileMap.get(CENTRE_TILE_COORD);
        	if(centre != null && centre.isEmpty()) {
        		return cacheAndReturn(new StrategyResult(List.of(centre), Collections.emptyList(), false, centre));
        	}
        	return computeNormalStrategy(context);
        }
        
        private StrategyResult computeNormalStrategy(StrategyContext context) {
        	List<Tile> botPath = findShortestPath(context.tileMap, context.botColour);
        	List<Tile> opponentPath = findShortestPath(context.tileMap, context.opponent);
        	boolean blocking = shouldBlock(botPath, opponentPath);
        	Tile chosenTile = selectTile(context, botPath, opponentPath, blocking);
        	
        	if(chosenTile == null) chosenTile = anyEmptyOctagon(context.tileMap);
        	if(chosenTile == null) {
        		return cacheAndReturn(new StrategyResult(Collections.emptyList(), Collections.emptyList(), false, null));
        	}
        	
        	List<Tile> displayPath = buildDisplayPath(botPath, chosenTile);
    		return cacheAndReturn(new StrategyResult(displayPath, opponentPath, blocking, chosenTile));

        }
        
        private boolean shouldBlock(List<Tile> botPath, List<Tile> opponentPath) {
        	return countEmptyTiles(opponentPath) <= countEmptyTiles(botPath) + BLOCKING_THRESHOLD;
        }
        
        private Tile selectTile(StrategyContext context, List<Tile> botPath, List<Tile> opponentPath, boolean blocking) {
        	if(!blocking || opponentPath.isEmpty()) {
        		return bestTileFromPath(context, botPath);
        	}
        	return selectBlockingTile(context, botPath, opponentPath);
        }
        
        private Tile selectBlockingTile(StrategyContext context, List<Tile> botPath, List<Tile> opponentPath) {
        	Tile chosenTile = bestTileFromPath(context, opponentPath);
        	if(chosenTile == null || isInvalidBlockingRhombus(context, chosenTile)) {
        		return bestTileFromPath(context, botPath);
        	}
        	return chosenTile;
        }
        
        private boolean isInvalidBlockingRhombus(StrategyContext context, Tile tile) {
        	return tile.getShape() == ShapeEnum.RHOMBUS && !isRhombusValidForPlayer(context, tile);
        }
        
        private List<Tile> buildDisplayPath(List<Tile> botPath, Tile chosenTile) {
        	List<Tile> displayPath = new ArrayList<>(botPath);
        	if(!displayPath.contains(chosenTile)) displayPath.add(chosenTile);
        	return displayPath;
        }
        
        private StrategyResult cacheAndReturn(StrategyResult result) {
        	cachedStrategy = result;
        	return result;
        }
        
        
        // - Tile Selection ---------------------------------------------------------------------------------------------
        
        private Tile bestTileFromPath(StrategyContext context, List<Tile> path) {
        	Tile rhombus = firstValidRhombus(context, path);
        	if(rhombus != null) return rhombus;
        	return closestEmptyOctagon(context.botColour, path);
        }
        
        private Tile firstValidRhombus(StrategyContext context, List<Tile> path) {
        	for(Tile tile : path) {
        		if(tile == null || !tile.isEmpty()) continue;
        		if(tile.getShape() == ShapeEnum.RHOMBUS && isRhombusValidForPlayer(context, tile)) {
        			return tile;
        		}
        	}
        	return null;
        }
        
        private Tile closestEmptyOctagon(PlayerEnum player, List<Tile> path) {
        	return path.stream()
        			.filter(t-> t != null && t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
        			.min(Comparator.comparingInt(t -> centrePenalty(t.getCoord(), player)))
        			.orElse(null);
        }
        
        private Tile anyEmptyOctagon(Map<String, Tile> tileMap) {
        	return tileMap.values().stream()
        			.filter(t -> t.isEmpty() && t.getShape() == ShapeEnum.OCTAGON)
        			.findFirst()
        			.orElse(null);
        }
        
        
        // - Dijkstra Path Finding --------------------------------------------------------------------------------------
        
        public List<Tile> findShortestPath(Map<String, Tile> tileMap, PlayerEnum player) {
        	Map<String, List<Tile>> octToRhombus = buildOctagonRhombusIndex(tileMap);
        	DijkstraState state = new DijkstraState(tileMap, player, octToRhombus);
        	seedStartEdge(state, player);
        	return runDijkstra(state, player, octToRhombus);
        }
        
        private void seedStartEdge(DijkstraState state, PlayerEnum player) {
        	for(Tile tile : state.tileMap.values()) {
        		if(!isStartEdge(tile, player)) continue;
        		if(isBlockedBy(tile, player)) continue;
        		state.tryRelax(tile.getCoord(), tileCost(tile, player), centrePenalty(tile.getCoord(), player), null);
        	}
        }
        
        
        private List<Tile> runDijkstra(DijkstraState state, PlayerEnum player, Map<String, List<Tile>> octToRhombus) {
        	while(!state.isEmpty()) {
        		CoordCost current = state.poll();
        		if(state.isStale(current)) continue;
        		Tile currentTile = state.tileMap.get(current.coord);
        		if(currentTile == null) continue;
        		if(isGoalEdge(currentTile, state.player)) {
        			return state.reconstructPath(current.coord);
        		}
        		relaxNeighbours(state, current, currentTile);
        	}
        	return Collections.emptyList();
        }
        
        private void relaxNeighbours(DijkstraState state, CoordCost current, Tile currentTile) {
        	for(Tile neighbour : getPathNeighbours(currentTile, state)) {
        		if(isBlockedBy(neighbour, state.player)) continue;
        		int newCost = current.cost + tileCost(neighbour, state.player);
        		state.tryRelax(neighbour.getCoord(), newCost, centrePenalty(neighbour.getCoord(), state.player), current.coord);
        	}
        }
        
        
        // - Graph Construction -----------------------------------------------------------------------------------------

        private Map<String, List<Tile>> buildOctagonRhombusIndex(Map<String, Tile> tileMap) {
        	Map<String, List<Tile>> index = new HashMap<>();
        	for(Tile rhombus : tileMap.values()) {
        		if(rhombus.getShape() != ShapeEnum.RHOMBUS) continue;
        		addRhombusToCornerIndex(index, rhombus, tileMap);
        	}
        	return index;
        }
        
        private void addRhombusToCornerIndex(Map<String, List<Tile>> index, Tile rhombus, Map<String, Tile> tileMap) {
        	for(Tile corner : getRhombusCorners(rhombus, tileMap)) {
        		if(corner != null) {
        			index.computeIfAbsent(corner.getCoord(), k -> new ArrayList<>()).add(rhombus);
        		}
        	}
        }
        
        private List<Tile> getPathNeighbours(Tile tile, DijkstraState state) {
        	if(isOctagonTile(tile)) {
        		return getOctagonNeighbours(tile, state);
        	}
        	return getRhombusNeighbours(tile, state.tileMap);
        }
        
        private List<Tile> getOctagonNeighbours(Tile tile, DijkstraState state) {
        	List<Tile> neighbours = new ArrayList<>();
        	char col = tile.getCoord().charAt(0);
        	int row = Integer.parseInt(tile.getCoord().substring(1));
        	
        	addTile(neighbours, state, "" + col + (row+1));
        	addTile(neighbours, state, "" + col + (row-1));
        	addTile(neighbours, state, "" + (char)(col-1) + row);
        	addTile(neighbours, state, "" + (char)(col+1) + row);

        	for(Tile rhombus : state.octToRhombus.getOrDefault(tile.getCoord(), Collections.emptyList())) {
        		if(!isBlockedBy(rhombus, state.player)) neighbours.add(rhombus);
        	}
        	return neighbours;
        }
        
        private List<Tile> getRhombusNeighbours(Tile tile, Map<String, Tile> tileMap) {
        	List<Tile> neighbours = new ArrayList<>();
        	for(Tile corner : getRhombusCorners(tile, tileMap)) {
        		if(corner != null) neighbours.add(corner);
        	}
        	return neighbours;
        }
        
        // - Tile Cost and Edge Logic -----------------------------------------------------------------------------------

        private int tileCost(Tile tile, PlayerEnum player) {
        	if(!tile.isEmpty() && tile.getOwner() == player) return 0;
        	if(tile.getShape() == ShapeEnum.RHOMBUS) return 1;
        	return 2;
        }
        
        private boolean isBlockedBy(Tile tile, PlayerEnum player) {
        	return !tile.isEmpty() && tile.getOwner() != player;
        }
        
        private boolean isStartEdge(Tile tile, PlayerEnum player) {
        	if(tile.getShape() == ShapeEnum.RHOMBUS) return false;
        	String coord = tile.getCoord();
        	return player == PlayerEnum.BLACK 
        			? Integer.parseInt(coord.substring(1)) == 11 
        			: coord.charAt(0) == 'A';
        }
        
        private boolean isGoalEdge(Tile tile, PlayerEnum player) {
        	if(tile.getShape() == ShapeEnum.RHOMBUS) return false;
        	String coord = tile.getCoord();
        	return player == PlayerEnum.BLACK 
        			? Integer.parseInt(coord.substring(1)) == 1
        			: coord.charAt(0) == 'K';
        }
        
        private int centrePenalty(String coord, PlayerEnum player) {
        	if(coord.contains("_")) return 0;
        	return player == PlayerEnum.BLACK
        			? Math.abs(coord.charAt(0) - 'F')
        			: Math.abs(Integer.parseInt(coord.substring(1)) - 6);
        }
        
        
        // - Rhombus Helpers --------------------------------------------------------------------------------------------
        
        private boolean isRhombusValidForPlayer(StrategyContext context, Tile rhombus) {
        	
			Tile[] corners = getRhombusCorners(rhombus, context.tileMap);
			return ownsBoth(corners[0], corners[2], context.botColour) 
					|| ownsBoth(corners[1], corners[3], context.botColour);
		}
        
        private boolean ownsBoth(Tile a, Tile b, PlayerEnum player) {
        	return a != null & b != null
        			&& !a.isEmpty() && !b.isEmpty()
        			&& a.getOwner() == player
        			&& b.getOwner() == player;
        }
        
        private Tile[] getRhombusCorners(Tile rhombus, Map<String, Tile> tileMap) {
        	String id = rhombus.getCoord();
        	char c1 = id.charAt(0);
        	char c2 = id.charAt(1);
        	int i1 = id.indexOf('_');
        	int i2 = id.indexOf('_', i1 + 1);
        	String r1 = id.substring(i1 + 1, i2);
        	String r2 = id.substring(i2 + 1);
        	
        	return new Tile[] {
        		tileMap.get("" + c1 + r1),
        		tileMap.get("" + c1 + r2),
        		tileMap.get("" + c2 + r2),
        		tileMap.get("" + c2 + r1)
        	};
        }
        

        // - Utility ----------------------------------------------------------------------------------------------------
        
        private static boolean isOctagonTile(Tile tile) {
        	return !tile.getCoord().contains("_");
        }
        
        private static void addTile(List<Tile> list, DijkstraState state, String coord) {
        	Tile t = state.tileMap.get(coord);
        	if(t != null) list.add(t);
        }
        
        private static int countEmptyTiles(List<Tile> path) {
        	return (int) path.stream().filter(Tile::isEmpty).count();
        }
        

        // - Getters / Setters ------------------------------------------------------------------------------------------

        public StrategyResult getLastExecutedStrategy() {
        	return lastExecutedStrategy;
        }
        
        public void setLastExecutedStrategy(StrategyResult s) {
        	this.lastExecutedStrategy = s;
        }
        

        // - Inner Types ------------------------------------------------------------------------------------------------

        private static class StrategyContext {
        	final Map<String, Tile> tileMap;
        	final PlayerEnum botColour;
        	final PlayerEnum opponent;
        	
        	StrategyContext(Map<String, Tile> tileMap, PlayerEnum botColour) {
        		this.tileMap = tileMap;
        		this.botColour = botColour;
        		this.opponent = botColour == PlayerEnum.BLACK ? PlayerEnum.WHITE : PlayerEnum.BLACK;
        	}
        }
        
        
        private static class DijkstraState {
        	final Map<String, Tile> tileMap;
        	final PlayerEnum player;
        	final Map<String, List<Tile>> octToRhombus;
        	private final Map<String, Integer> dist;
        	private final Map<String, String> prev;
        	private final PriorityQueue<CoordCost> pq;
        	
        	DijkstraState(Map<String, Tile> tileMap, PlayerEnum player, Map<String, List<Tile>> octToRhombus) {
        		this.tileMap = tileMap;
        		this.player = player;
        		this.octToRhombus = octToRhombus;
        		dist = new HashMap<>();
        		prev = new HashMap<>();
        		pq = new PriorityQueue<>(Comparator.comparingInt((CoordCost c) -> c.cost).thenComparingInt(c -> c.centrePenalty));
        	
        		for(String coord : tileMap.keySet()) {
        			dist.put(coord, Integer.MAX_VALUE);
        		}
        	}
        	
        	void tryRelax(String coord, int newCost, int penalty, String fromCoord) {
        		if(newCost < dist.getOrDefault(coord, Integer.MAX_VALUE)) {
        			dist.put(coord, newCost);
        			prev.put(coord, fromCoord);
        			pq.offer(new CoordCost(coord, newCost, penalty));
        		}
        	}
        	
        	boolean isStale(CoordCost entry) {
        		return entry.cost > dist.getOrDefault(entry.coord, Integer.MAX_VALUE);
        	}
        	
        	boolean isEmpty() {
        		return pq.isEmpty();
        	}
        	
        	CoordCost poll() {
        		return pq.poll();
        	}
        	
        	List<Tile> reconstructPath(String goalCoord) {
        		List<Tile> path = new ArrayList<>();
        		String current = goalCoord;
        		while(current != null) {
        			Tile t = tileMap.get(current);
        			if(t != null) path.add(t);
        			current = prev.get(current);
        		}
        		Collections.reverse(path);
        		return path;
        	}
        }
                
        
        public static class StrategyResult {
        	public final List<Tile> path;
        	public final List<Tile> opponentPath;
        	public final boolean isBlocking;
        	public final Tile chosenTile;
        	
        	public StrategyResult(List<Tile> path, List<Tile> opponentPath, boolean isBlocking, Tile chosenTile) {
        		this.path = path;
        		this.opponentPath = opponentPath;
        		this.isBlocking = isBlocking;
        		this.chosenTile = chosenTile;
        	}
        }
        
        
        private static class CoordCost { 
        	final String coord;
        	final int cost;
        	final int centrePenalty;
        	
        	CoordCost(String coord, int cost, int centrePenalty) {
        		this.coord = coord;
        		this.cost = cost;
        		this.centrePenalty = centrePenalty;
        	}
        }
        
        

    }