package com.example.sequax40.model.game;

import java.util.*;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.move.Move;

public class GameManager {


	    private final Board board;
	    private final Map<String, Tile> tileMap;

	    private Tile firstMove; //to track pie rule
        private boolean gameOver = false;
        private PlayerEnum currentTurn = PlayerEnum.BLACK; //set initial turn to black

        private int moveCount = 0;



        private final Map<String, List<Tile>> rhombusLookup = new HashMap<>();



	    public GameManager(Board board, Map<String, Tile> tileMap) {
	        this.board = board;
	        this.tileMap = tileMap;
	    }

	    public PlayerEnum getCurrentTurn() {
	        return currentTurn; //return the current players turn
	    }

        public int getMoveCount() {
            return moveCount;
        }

	    //checks whether a move is valid and sets the owner of the tile if it is
	    //calls switchTurn if a valid move is made
	    public boolean makeMove(Tile tile) {

	        if (gameOver || tile == null || !tile.isEmpty()) {
	            return false;
	        }

	        if(firstMove == null) {
	        	firstMove = tile;
	        }

            if (tile.getShape() == ShapeEnum.RHOMBUS){
                if(!isRhombusValid(tile)) {
                    return false;
                }
            }

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

	    //switches the turn to the opposite player
	    public void switchTurn() {
	        currentTurn = (currentTurn == PlayerEnum.BLACK)
	                ? PlayerEnum.WHITE
	                : PlayerEnum.BLACK;
	    }


    /*
     * Checks if a rhombus tile placement is valid based on the diagonal rule.
     * A rhombus is valid if at least one of its two diagonals is fully owned
     * by the current player.
     */

    private boolean isRhombusValid(Tile rhombusTile) {

        Tile[] corners = getRhombusCorners(rhombusTile);

        Tile t1 = corners[0];
        Tile t2 = corners[1];
        Tile t3 = corners[2];
        Tile t4 = corners[3];

        // Check diagonal 1 (t1 ↔ t3)
        boolean diag1 = t1 != null && t3 != null
                && !t1.isEmpty() && !t3.isEmpty()
                && t1.getOwner() == currentTurn
                && t3.getOwner() == currentTurn;

        // Check diagonal 2 (t2 ↔ t4)
        boolean diag2 = t2 != null && t4 != null
                && !t2.isEmpty() && !t4.isEmpty()
                && t2.getOwner() == currentTurn
                && t4.getOwner() == currentTurn;

        return diag1 || diag2;
    }


	    public Tile getFirstMoveTile() {
	    	return firstMove;
	    }




    /*
     * Checks whether the given player has formed a continuous path
     * connecting their two required sides of the board.
     *
     * BLACK: top to bottom (row 1 to row 11)
     * WHITE: left to right (column A to column K)
     *
     * Uses DFS to check connected components, including rhombus links.
     */
    public boolean checkWin(PlayerEnum player) {

        Set<String> visited = new HashSet<>();//to keep track of tiles we've checked to avoid re-checking the same ones and creating infinite loop

        for (Tile tile : tileMap.values()) {//iterates over every tile in board
            if (tile == null || tile.isEmpty()) {
                continue;
            }

            if (tile.getOwner() == player && !visited.contains(tile.getCoord())) {
                Set<String> edgesReached = new HashSet<>();

                if (dfs(tile, player, visited, edgesReached)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Depth-first search to explore all connected tiles belonging to the current player.
     * Tracks which board edges are reached by this connected component.
     */
    private boolean dfs(Tile tile, PlayerEnum player, Set<String> visited, Set<String> edgesReached) {

        if (tile == null || visited.contains(tile.getCoord())) {
            return false;
        }

        visited.add(tile.getCoord()); //mark current tile as visited so its not processed again

        markEdges(tile, player, edgesReached); //checks if this tile touches an edge of the board, if it does it records it.

        if (hasWon(player, edgesReached)) {
            return true;
        }

        for (Tile neighbor : getNeighbors(tile, player)) {
            if (neighbor != null
                    && neighbor.getOwner() == player
                    && !visited.contains(neighbor.getCoord())) {

                if (dfs(neighbor, player, visited, edgesReached)) { //recursively explores the neighbour
                    return true;
                }
            }
        }

        return false;
    }


    //If a tile is on the edge of the board, record it
    private void markEdges(Tile tile, PlayerEnum player, Set<String> edges) {

        // Rhombuses do not directly touch board edges so don't check them
        if (tile.getShape() == ShapeEnum.RHOMBUS) {
            return;
        }

        int row = getRow(tile.getCoord());
        char col = getCol(tile.getCoord());

        if (player == PlayerEnum.BLACK) {
            if (row == 1) edges.add("TOP");
            if (row == 11) edges.add("BOTTOM");
        } else {
            if (col == 'A') edges.add("LEFT");
            if (col == 'K') edges.add("RIGHT");
        }
    }


    //Checks if the connected component connects either the top to the bottom or the left to the right of the board
    private boolean hasWon(PlayerEnum player, Set<String> edges) {

        if (player == PlayerEnum.BLACK) {
            return edges.contains("TOP") && edges.contains("BOTTOM");
        } else {
            return edges.contains("LEFT") && edges.contains("RIGHT");
        }
    }

    // Extract row number e.g.  "A10" -> 10
    private int getRow(String coord) {
        return Integer.parseInt(coord.substring(1));
    }

    // Extract column letter e.g. "A10" -> 'A'
    private char getCol(String coord) {
        return coord.charAt(0);
    }



    //returns all connected tiles owned by the same player
    private List<Tile> getNeighbors(Tile tile, PlayerEnum player) {

        //check if it's an octagon or a rhombus and delegate accordingly
        if (isOctagon(tile)) {
            return getOctagonNeighbors(tile, player);
        } else {
            return getRhombusNeighbors(tile, player);
        }
    }


    //checks if the tile is an octagon (does not contain "_")
    private boolean isOctagon(Tile tile) {
        return !tile.getCoord().contains("_");
    }


    //handles neighbour logic for octagon tiles
    private List<Tile> getOctagonNeighbors(Tile tile, PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();

        int row = getRow(tile.getCoord());
        char col = getCol(tile.getCoord());

        //checks closest neighbours in all 4 directions (octagons only)
        addIfOwnedByPlayer(neighbors, tileMap.get(col + "" + (row - 1)), player); // up
        addIfOwnedByPlayer(neighbors, tileMap.get(col + "" + (row + 1)), player); // down
        addIfOwnedByPlayer(neighbors, tileMap.get((char) (col - 1) + "" + row), player); // left
        addIfOwnedByPlayer(neighbors, tileMap.get((char) (col + 1) + "" + row), player); // right

        //check rhombus connections linked to this octagon
        addRhombusConnections(tile, neighbors, player);

        return neighbors;
    }


    //loops through all placed rhombuses and connects valid ones
    private void addRhombusConnections(Tile tile, List<Tile> neighbors, PlayerEnum player) {
        for (Tile rhombus : tileMap.values()) { //loops through every tile on the board

            //skip if not a valid rhombus owned by the player
            if (!isOwnedRhombus(rhombus, player)) continue;

            //get the 4 corner tiles around the rhombus
            Tile[] corners = getRhombusCorners(rhombus);

            Tile t1 = corners[0];
            Tile t2 = corners[1];
            Tile t3 = corners[2];
            Tile t4 = corners[3];

            //add valid tiles to the neighbours array (diagonal connections)
            if (tile.equals(t1)) addIfOwnedByPlayer(neighbors, t3, player);
            if (tile.equals(t3)) addIfOwnedByPlayer(neighbors, t1, player);
            if (tile.equals(t2)) addIfOwnedByPlayer(neighbors, t4, player);
            if (tile.equals(t4)) addIfOwnedByPlayer(neighbors, t2, player);
        }
    }


    //checks if a tile is a placed rhombus owned by the current player
    private boolean isOwnedRhombus(Tile tile, PlayerEnum player) {
        return tile != null
                && tile.getShape() == ShapeEnum.RHOMBUS
                && !tile.isEmpty()
                && tile.getOwner() == player;
    }


    //extracts the 4 corner tiles (octagons) from a rhombus coordinate
    private Tile[] getRhombusCorners(Tile rhombus) {
        String id = rhombus.getCoord();

        //get column letters
        char l1 = id.charAt(0);
        char l2 = id.charAt(1);

        //find positions of underscores
        int i1 = id.indexOf("_");
        int i2 = id.indexOf("_", i1 + 1);

        //extract row numbers
        String n1 = id.substring(i1 + 1, i2);
        String n2 = id.substring(i2 + 1);

        //get the 4 tiles around the rhombus
        Tile t1 = tileMap.get("" + l1 + n1);
        Tile t2 = tileMap.get("" + l1 + n2);
        Tile t3 = tileMap.get("" + l2 + n2);
        Tile t4 = tileMap.get("" + l2 + n1);

        return new Tile[]{t1, t2, t3, t4};
    }


    //handles neighbour logic when the current tile is a rhombus
    private List<Tile> getRhombusNeighbors(Tile tile, PlayerEnum player) {
        List<Tile> neighbors = new ArrayList<>();

        //get the 4 corner tiles (octagons)
        Tile[] corners = getRhombusCorners(tile);

        //if these octagons are owned by the current player then add them to neighbours
        for (Tile t : corners) {
            addIfOwnedByPlayer(neighbors, t, player);
        }

        return neighbors;
    }


    //if the tile is not null, empty and is owned by the current player then add it to neighbours
    private void addIfOwnedByPlayer(List<Tile> neighbors, Tile tile, PlayerEnum player) {
        if (tile != null && !tile.isEmpty() && tile.getOwner() == player) {
            neighbors.add(tile);
        }
    }

    public void resetGame() {
        board.reset();
        moveCount = 0;
        currentTurn = PlayerEnum.BLACK;
        firstMove = null;
        gameOver = false;  // reset the stop flag
    }

    public boolean isGameOver() {
        return gameOver;
    }

	public void setMoveCount(int i) {
		moveCount = i;
	}

}