package com.example.sequax40.model.player;

import com.example.sequax40.model.board.Tile;

import java.util.List;
import java.util.Random;

public class BotPlayer {

    private final Random random = new Random();

    /**
     * Picks a random tile from available empty tiles.
     * Bot does NOT validate moves — GameManager handles rules.
     */
    public Tile chooseTile(List<Tile> availableTiles) {

        if (availableTiles == null || availableTiles.isEmpty()) {
            return null;
        }

        return availableTiles.get(random.nextInt(availableTiles.size()));
    }
}