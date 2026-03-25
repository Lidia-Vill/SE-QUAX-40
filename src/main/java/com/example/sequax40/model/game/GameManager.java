package com.example.sequax40.model.game;

import java.util.Map;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.enums.ShapeEnum;
import com.example.sequax40.model.board.Board;
import com.example.sequax40.model.board.Tile;
import com.example.sequax40.model.move.Move;

public class GameManager {
	

	    private final Board board;
	    private final Map<String, Tile> tileMap;
	    
	    private Tile firstMove; //to track pie rule 

	    private PlayerEnum currentTurn = PlayerEnum.BLACK; //set initial turn to black

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

	        if (tile == null || !tile.isEmpty()) {
	            return false;
	        }
	        
	        if(firstMove == null) {
	        	firstMove = tile;
	        }

	        if (tile.getShape() == ShapeEnum.RHOMBUS) {
	            if (!isRhombusValid(tile)) {
	                return false;
	            }
	        }

	        tile.setOwner(currentTurn);
	        switchTurn();
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


        // Resets the game board and sets the current turn back to BLACK.
	    public void resetGame() {
	        board.reset();  // clear all tiles
	        currentTurn = PlayerEnum.BLACK; // reset turn to BLACK
	        firstMove = null;
	    }
	    
	    public Tile getFirstMoveTile() {
	    	return firstMove;
	    }
	   
	}
