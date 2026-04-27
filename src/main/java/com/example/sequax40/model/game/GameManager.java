package com.example.sequax40.model.game;

import java.util.*;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;

/*
 * Manages game rules and state: move validation, turn switching,
 * win detection via DFS and game reset
 * 
 * BLACK connects row 11 (top) to row 1 (bottom)
 * WHITE connects col A (left) to col K (right)
 */
public class GameManager {

	
    // - Edge Labels ------------------------------------------------------------------------------------------------

   	private static final String EDGE_TOP    = "TOP";
    private static final String EDGE_BOTTOM = "BOTTOM";
    private static final String EDGE_LEFT   = "LEFT";
    private static final String EDGE_RIGHT  = "RIGHT";
 
    private static final int BLACK_START_ROW = 11;
    private static final int BLACK_END_ROW   = 1;
    private static final char WHITE_START_COL = 'A';
    private static final char WHITE_END_COL   = 'K'; 
	
    
    // - Fields -----------------------------------------------------------------------------------------------------    
    
	private final Board board;
	private final Map<String, Tile> tileMap;

	private Tile firstMove; 
    private boolean gameOver = false;
    private PlayerEnum currentTurn = PlayerEnum.BLACK;
    private int moveCount = 0;


    // - Constructor ------------------------------------------------------------------------------------------------

	public GameManager(Board board, Map<String, Tile> tileMap) {
		this.board = board;
	    this.tileMap = tileMap;
	}
	

    // - Move Handling ----------------------------------------------------------------------------------------------

	public boolean makeMove(Tile tile) {
		if (gameOver || tile == null || !tile.isEmpty()) { return false; }
		if(firstMove == null) { firstMove = tile; }
        if (tile.getShape() == ShapeEnum.RHOMBUS && !isRhombusValid(tile, currentTurn)) { return false; }

	    tile.setOwner(currentTurn);
        tileMap.put(tile.getCoord(), tile);

        if (checkWin(currentTurn)) {
            System.out.println(currentTurn + " wins!");
            gameOver = true;
        } else {
            moveCount++;
            switchTurn();
        }
            return true;
	    }

	    public void switchTurn() {
	        currentTurn = (currentTurn == PlayerEnum.BLACK)
	                ? PlayerEnum.WHITE
	                : PlayerEnum.BLACK;
	    }


    private boolean isRhombusValid(Tile rhombusTile, PlayerEnum player) {

        Tile[] corners = getRhombusCorners(rhombusTile);

        return ownsBothOnDiagonal(corners[0], corners[2], player)
                || ownsBothOnDiagonal(corners[1], corners[3], player);
    }

    private boolean ownsBothOnDiagonal(Tile a, Tile b, PlayerEnum player) {
        return a != null && b != null
                && !a.isEmpty() && !b.isEmpty()
                && a.getOwner() == player
                && b.getOwner() == player;
    }


    // - Win Detection ----------------------------------------------------------------------------------------------
   
    public boolean checkWin(PlayerEnum player) {
        Set<String> visited = new HashSet<>();
        for (Tile tile : tileMap.values()) {
            if (tile == null || tile.isEmpty()) { continue; }
            if (tile.getOwner() == player && !visited.contains(tile.getCoord())) {
                if (dfs(new DfsState(visited), tile, player)) { return true; }
            }
        }
        return false;
    }

    private boolean dfs(DfsState state, Tile tile, PlayerEnum player) {

        if (tile == null || state.visited.contains(tile.getCoord())) { return false; }
        state.visited.add(tile.getCoord()); 
        markEdges(state, tile, player); 
        if (hasWon(state, player)) { return true; }
        for (Tile neighbor : getNeighbors(tile, player)) {
            if (neighbor != null
                    && neighbor.getOwner() == player
                    && !state.visited.contains(neighbor.getCoord())) {
                if (dfs(state, neighbor, player)) { return true; }
            }
        }
        return false;
    }

    private void markEdges(DfsState state, Tile tile, PlayerEnum player) {
    	if (tile.getShape() == ShapeEnum.RHOMBUS) { return; }
        int row = getRow(tile.getCoord());
        char col = getCol(tile.getCoord());

        if (player == PlayerEnum.BLACK) {
            if (row == BLACK_END_ROW) state.edgesReached.add(EDGE_TOP);
            if (row == BLACK_START_ROW) state.edgesReached.add(EDGE_BOTTOM);
        } else {
        	if (col == WHITE_START_COL) state.edgesReached.add(EDGE_LEFT);
            if (col == WHITE_END_COL)   state.edgesReached.add(EDGE_RIGHT);
        }
    }


    private boolean hasWon(DfsState state, PlayerEnum player) {
        if (player == PlayerEnum.BLACK) {
            return state.edgesReached.contains(EDGE_TOP) && state.edgesReached.contains(EDGE_BOTTOM);
        } 
        return state.edgesReached.contains(EDGE_LEFT) && state.edgesReached.contains(EDGE_RIGHT);
    }

    
    // - Neighbour Logic --------------------------------------------------------------------------------------------
    
    private List<Tile> getNeighbors(Tile tile, PlayerEnum player) {
        return isOctagon(tile) ? getOctagonNeighbours(tile, player) : getRhombusNeighbours(tile, player);
    }

    private List<Tile> getOctagonNeighbours(Tile tile, PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();

        int row = getRow(tile.getCoord());
        char col = getCol(tile.getCoord());

        addIfOwnedByPlayer(neighbors, tileMap.get(col + "" + (row - 1)), player);
        addIfOwnedByPlayer(neighbors, tileMap.get(col + "" + (row + 1)), player);
        addIfOwnedByPlayer(neighbors, tileMap.get((char) (col - 1) + "" + row), player);
        addIfOwnedByPlayer(neighbors, tileMap.get((char) (col + 1) + "" + row), player); 

        addRhombusConnections(tile, neighbors, player);

        return neighbors;
    }

    private void addRhombusConnections(Tile tile, List<Tile> neighbours, PlayerEnum player) {
        for (Tile rhombus : tileMap.values()) { 

            if (!isOwnedRhombus(rhombus, player)) continue;

            Tile[] corners = getRhombusCorners(rhombus);

            if (tile.equals(corners[0])) addIfOwnedByPlayer(neighbours, corners[2], player);
            if (tile.equals(corners[2])) addIfOwnedByPlayer(neighbours, corners[0], player);
            if (tile.equals(corners[1])) addIfOwnedByPlayer(neighbours, corners[3], player);
            if (tile.equals(corners[3])) addIfOwnedByPlayer(neighbours, corners[1], player);
                }
    }

    private List<Tile> getRhombusNeighbours(Tile tile, PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();
        for (Tile corner : getRhombusCorners(tile)) { addIfOwnedByPlayer(neighbors, corner, player);}
        return neighbors;
    }

    
    // - Rhombus Helpers --------------------------------------------------------------------------------------------

    private Tile[] getRhombusCorners(Tile rhombus) {
        String id = rhombus.getCoord();

        char l1 = id.charAt(0);
        char l2 = id.charAt(1);

        int i1 = id.indexOf("_");
        int i2 = id.indexOf("_", i1 + 1);

        String n1 = id.substring(i1 + 1, i2);
        String n2 = id.substring(i2 + 1);

        Tile t1 = tileMap.get("" + l1 + n1);
        Tile t2 = tileMap.get("" + l1 + n2);
        Tile t3 = tileMap.get("" + l2 + n2);
        Tile t4 = tileMap.get("" + l2 + n1);

        return new Tile[]{t1, t2, t3, t4};
    }

    private boolean isOwnedRhombus(Tile tile, PlayerEnum player) {
        return tile != null
                && tile.getShape() == ShapeEnum.RHOMBUS
                && !tile.isEmpty()
                && tile.getOwner() == player;
    }

    
    // - Utility ----------------------------------------------------------------------------------------------------

    private static boolean isOctagon(Tile tile) {
        return !tile.getCoord().contains("_");
    }

    private static int getRow(String coord) {
        return Integer.parseInt(coord.substring(1));
    }

    private static char getCol(String coord) {
        return coord.charAt(0);
    }

    private static void addIfOwnedByPlayer(List<Tile> neighbours, Tile tile, PlayerEnum player) {
        if (tile != null && !tile.isEmpty() && tile.getOwner() == player) neighbours.add(tile);
    }


    // - Reset ------------------------------------------------------------------------------------------------------
    
    public void resetGame() {
        board.reset();
        moveCount = 0;
        currentTurn = PlayerEnum.BLACK;
        firstMove = null;
        gameOver = false;  // reset the stop flag
    }

    
    // - Getters / Setters ------------------------------------------------------------------------------------------
    
    public PlayerEnum getCurrentTurn()   { return currentTurn; }
    public int        getMoveCount()     { return moveCount; }
    public boolean    isGameOver()       { return gameOver; }
    public Tile       getFirstMoveTile() { return firstMove; }
    public void       setMoveCount(int i){ this.moveCount = i; }
    

    // - Inner Types ------------------------------------------------------------------------------------------------

    /*
     * Groups the mutable state for a single DFS traversal so that
     * dfs() does not need more than 3 parameters.
     */
    private static class DfsState {
        final Set<String> visited;
        final Set<String> edgesReached;

        DfsState(Set<String> visited) {
            this.visited      = visited;
            this.edgesReached = new HashSet<>();
        }
    }
}