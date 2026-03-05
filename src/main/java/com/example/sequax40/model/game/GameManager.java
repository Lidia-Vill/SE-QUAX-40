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

	    private PlayerEnum currentTurn = PlayerEnum.BLACK;

	    public GameManager(Board board, Map<String, Tile> tileMap) {
	        this.board = board;
	        this.tileMap = tileMap;
	    }

	    public PlayerEnum getCurrentTurn() {
	        return currentTurn;
	    }

	    public boolean makeMove(Tile tile) {

	        if (tile == null || !tile.isEmpty()) {
	            return false;
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

	    private void switchTurn() {
	        currentTurn = (currentTurn == PlayerEnum.BLACK)
	                ? PlayerEnum.WHITE
	                : PlayerEnum.BLACK;
	    }

	    private boolean isRhombusValid(Tile rhombusTile) {

	        String id = rhombusTile.getCoord();

	        char letter1 = id.charAt(0);
	        char letter2 = id.charAt(1);

	        int firstUnderscore = id.indexOf("_");
	        int secondUnderscore = id.indexOf("_", firstUnderscore + 1);

	        String num1 = id.substring(firstUnderscore + 1, secondUnderscore);
	        String num2 = id.substring(secondUnderscore + 1);

	        Tile t1 = tileMap.get(letter1 + num1);
	        Tile t2 = tileMap.get(letter1 + num2);
	        Tile t3 = tileMap.get(letter2 + num2);
	        Tile t4 = tileMap.get(letter2 + num1);

	        boolean diag1 = (t1 != null && t3 != null)
	                && !t1.isEmpty()
	                && t1.getOwner() == currentTurn
	                && t1.getOwner() == t3.getOwner();

	        boolean diag2 = (t2 != null && t4 != null)
	                && !t2.isEmpty()
	                && t2.getOwner() == currentTurn
	                && t2.getOwner() == t4.getOwner();

	        return diag1 || diag2;
	    }

	    public void resetGame() {
	        board.reset();
	        currentTurn = PlayerEnum.BLACK;
	    }
	}
