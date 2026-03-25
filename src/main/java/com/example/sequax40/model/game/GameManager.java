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



        private final Map<String, List<Tile>> rhombusLookup = new HashMap<>();


	    public GameManager(Board board, Map<String, Tile> tileMap) {
	        this.board = board;
	        this.tileMap = tileMap;
	    }

	    public PlayerEnum getCurrentTurn() {
	        return currentTurn; //return the current players turn
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

            // Only check for win AFTER placing tile
            boolean win = checkWin(currentTurn);
            if (win) {
                System.out.println(currentTurn + " wins!");
                gameOver = true; // set flag
                // Optionally, notify controller / UI via callback
            } else {
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

	        String id = rhombusTile.getCoord(); // Get the rhombus coordinate, e.g., "AC_1_2"

            // Extract column letters
	        char letter1 = id.charAt(0);
	        char letter2 = id.charAt(1);

            // Find positions of underscores to split row numbers
	        int firstUnderscore = id.indexOf("_");
	        int secondUnderscore = id.indexOf("_", firstUnderscore + 1);

            // Extract the 2 row numbers from the coordinate string
	        String num1 = id.substring(firstUnderscore + 1, secondUnderscore);
	        String num2 = id.substring(secondUnderscore + 1);

            // Retrieve the four tiles forming the rhombus corners
	        Tile t1 = tileMap.get(letter1 + num1); // top-left corner
	        Tile t2 = tileMap.get(letter1 + num2); // top-left corner
	        Tile t3 = tileMap.get(letter2 + num2); // bottom-left corner
	        Tile t4 = tileMap.get(letter2 + num1); // bottom-right corner

            // Check first diagonal: t1-t3
	        boolean diag1 = (t1 != null && t3 != null)
	                && !t1.isEmpty()
	                && t1.getOwner() == currentTurn
	                && t1.getOwner() == t3.getOwner();

            // Check first diagonal: t2-t4
	        boolean diag2 = (t2 != null && t4 != null)
	                && !t2.isEmpty()
	                && t2.getOwner() == currentTurn
	                && t2.getOwner() == t4.getOwner();

	        return diag1 || diag2; // Return true if at least one diagonal is valid
	    }


	    public Tile getFirstMoveTile() {
	    	return firstMove;
	    }




    /*
     * Checks whether the given player has formed a continuous path
     * connecting their two required sides of the board.
     *
     * BLACK: top ↔ bottom (row 1 to row 11)
     * WHITE: left ↔ right (column A to column K)
     *
     * Uses DFS to explore connected components, including rhombus links.
     */
    public boolean checkWin(PlayerEnum player) {

        Set<String> visited = new HashSet<>();

        for (Tile tile : tileMap.values()) {

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
     * Depth-first search to explore all connected tiles belonging to the player.
     * Tracks which board edges are reached by this connected component.
     */
    private boolean dfs(Tile tile,
                        PlayerEnum player,
                        Set<String> visited,
                        Set<String> edgesReached) {

        if (visited.contains(tile.getCoord())) return false;

        visited.add(tile.getCoord());

        // mark which edges this tile touches
        markEdges(tile, player, edgesReached);

        // check if this component connects both required sides
        if (hasWon(player, edgesReached)) {
            return true;
        }

        for (Tile neighbor : getNeighbors(tile)) {
            if (neighbor != null && neighbor.getOwner() == player) {
                if (dfs(neighbor, player, visited, edgesReached)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Marks which edges of the board a tile touches.
     */
    private void markEdges(Tile tile, PlayerEnum player, Set<String> edges) {

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

    /*
     * Checks if the connected component satisfies the win condition.
     */
    private boolean hasWon(PlayerEnum player, Set<String> edges) {

        if (player == PlayerEnum.BLACK) {
            return edges.contains("TOP") && edges.contains("BOTTOM");
        } else {
            return edges.contains("LEFT") && edges.contains("RIGHT");
        }
    }

    // Extract row from "A10" → 10
    private int getRow(String coord) {
        return Integer.parseInt(coord.substring(1));
    }

    // Extract column from "A10" → 'A'
    private char getCol(String coord) {
        return coord.charAt(0);
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();

        String coord = tile.getCoord();

        if (!coord.contains("_")) {
            // Normal octagon tile
            int row = getRow(coord);
            char col = getCol(coord);

            neighbors.add(tileMap.get(col + "" + (row - 1))); // up
            neighbors.add(tileMap.get(col + "" + (row + 1))); // down
            neighbors.add(tileMap.get((char) (col - 1) + "" + row)); // left
            neighbors.add(tileMap.get((char) (col + 1) + "" + row)); // right

            // Also check for rhombuses that include this tile
            for (Tile rhombus : tileMap.values()) {
                if (rhombus.getShape() != ShapeEnum.RHOMBUS) continue;

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

                // If this octagon is a corner of the rhombus, connect to opposite corner
                if (tile.equals(t1) && t3 != null) neighbors.add(t3);
                if (tile.equals(t3) && t1 != null) neighbors.add(t1);
                if (tile.equals(t2) && t4 != null) neighbors.add(t4);
                if (tile.equals(t4) && t2 != null) neighbors.add(t2);
            }
        } else {
            // Rhombus tile: neighbors are its four corners
            char l1 = coord.charAt(0);
            char l2 = coord.charAt(1);
            int i1 = coord.indexOf("_");
            int i2 = coord.indexOf("_", i1 + 1);
            String n1 = coord.substring(i1 + 1, i2);
            String n2 = coord.substring(i2 + 1);

            Tile t1 = tileMap.get("" + l1 + n1);
            Tile t2 = tileMap.get("" + l1 + n2);
            Tile t3 = tileMap.get("" + l2 + n2);
            Tile t4 = tileMap.get("" + l2 + n1);

            if (t1 != null) neighbors.add(t1);
            if (t2 != null) neighbors.add(t2);
            if (t3 != null) neighbors.add(t3);
            if (t4 != null) neighbors.add(t4);
        }

        return neighbors;
    }

    public void resetGame() {
        board.reset();
        currentTurn = PlayerEnum.BLACK;
        firstMove = null;
        gameOver = false;  // reset the stop flag
    }

    public boolean isGameOver() {
        return gameOver;
    }

}
