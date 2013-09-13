package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.drmario.player.Agent;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.player.Human;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //the board the game play will occur
    private Board board;
    
    //the player and the Pill the Player will control
    private Human player;
    
    //our AI competitor
    private Agent agent;
    
    //the dimensions for the board
    private static final int COLUMNS = 8;
    private static final int ROWS = 16;
    
    private int virusCount = 45;
    
    public Manager(final Engine engine) throws Exception
    {
        board = new Board(new Rectangle(50,25, 160, 320), COLUMNS, ROWS, virusCount);
        
        //player = new Human(1500);
        agent = new Agent(500);
    }
    
    public Board getBoard()
    {
        return this.board;
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
        //check for matches on board etc...
        board.update(engine.getMain().getTime());
        
        //if we have the opportunity to interact with the board check Player input
        if (board.canInteract())
        {
            if (player != null)
            {
                //check for keyboard input etc..
                player.update(engine);
            }
            
            if (agent != null)
            {
                //execute ai logic
                agent.update(engine);
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        //draw the board and all of the pieces
        board.render(graphics);
        
        //if we have the opportunity to interact with the board draw the Player Pill
        if (board.canInteract())
        {
            if (player != null)
            {
                //draw the player's current Pill
                player.render(graphics);
            }
            
            if (agent != null)
            {
                //draw the AI's current Pill
                agent.render(graphics);
            }
        }
    }
}