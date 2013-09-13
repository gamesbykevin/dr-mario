package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.engine.Engine;

import java.awt.Graphics;

/**
 * Interface for each Player that is created
 * @author GOD
 */
public interface IPlayer 
{
    /**
     * Update the Player accordingly
     * @param engine Engine containing all the objects we need
     */
    public void update(final Engine engine);
    
    /**
     * Draw the Player Pill and possibly other misc information
     * @param graphics 
     */
    public void render(final Graphics graphics);
}
