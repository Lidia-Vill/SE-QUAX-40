package com.example.sequax40.test.sprint2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.sequax40.enums.PlayerEnum;
import com.example.sequax40.model.board.Tile;

public class Feature2Test {
	
	@Test
    void testCannotPlaceOnOccupiedTile() {
    	Tile tile = board.getTile("A1");
    	
    	manager.makeMove(tile);
    	PlayerEnum turnFirstMove = manager.getCurrentTurn();
    	
    	boolean secondMove = manager.makeMove(tile);
    	    	
    	assertFalse(secondMove); //place fails 
    }
    
    @Test
    void testStaysPlayersTurnOnInvalidMove() {
    	Tile tile = board.getTile("A1");
    	
    	manager.makeMove(tile);
    	PlayerEnum turnFirstMove = manager.getCurrentTurn();
    	
    	boolean secondMove = manager.makeMove(tile);
    	
    	assertEquals(turnFirstMove, manager.getCurrentTurn());

    }
    
    @Test
    void testTurnAlternatesAfterValidMove() {
    	Tile tile = board.getTile("A1");
    	boolean moveMade = manager.makeMove(tile);
    	
    	assertTrue(moveMade);
    	assertEquals(PlayerEnum.WHITE, manager.getCurrentTurn());
    }
}
