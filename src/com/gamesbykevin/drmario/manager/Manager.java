package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.player.Player;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    private Board board;
    
    private Player player;
    
    public Manager(final Engine engine) throws Exception
    {
        board = new Board(new Rectangle(100,100, 200, 400), 10, 20);
        
        player = new Player();
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        
    }
    
    /**
     * Update all application elements
     * 
     * @param engine Our main game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        board.update(engine.getMain().getTimeDeductionPerUpdate(), engine.getKeyboard());
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        board.render(graphics);
    }
}