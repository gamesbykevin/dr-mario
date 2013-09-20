package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.player.Agent;
import com.gamesbykevin.drmario.player.Human;
import com.gamesbykevin.drmario.shared.IElement;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IElement
{
    //our human object
    private Human player;
    
    //our cpu opponent
    private List<Agent> agents;
    
    private int virusCount = 100;
    
    public Manager(final Engine engine) throws Exception
    {
        //1500 milliseconds per each Pill drop
        final long gravityDelay = 1500L;
        
        //the delay per each movement for the Artifical Intelligence
        final long movementDelay = 10L;//250L;
        
        //player = new Human(gravityDelay);
        
        //our List of AI opponents
        agents = new ArrayList<>();
        
        final int opponentCount = 7;
        
        for (int i=0; i < opponentCount; i++)
        {
            agents.add(new Agent(gravityDelay, movementDelay));
        }
        
        createBoards();
    }
    
    /**
     * Create all of the boards
     */
    private void createBoards() throws Exception
    {
        Rectangle container = new Rectangle(50, 25, 160, 320);
        
        if (player != null)
        {
            player.createBoard(container, virusCount);
            container.x += container.width;
        }
        
        if (agents != null && agents.size() > 0)
        {
            for (Agent agent : agents)
            {
                agent.createBoard(container, virusCount);
                container.x += container.width;
            }
        }
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (player != null)
        {
            player.dispose();
            player = null;
        }
        
        if (agents != null)
        {
            for (Agent agent : agents)
            {
                if (agent != null)
                    agent.dispose();
                
                agent = null;
            }
            
            agents.clear();
            agents = null;
        }
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
        if (player != null)
        {
            //check for keyboard input etc..
            player.update(engine);
        }

        if (agents != null)
        {
            for (Agent agent : agents)
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
        if (player != null)
        {
            //draw the player's current Pill and Board
            player.render(graphics);
        }
            
        if (agents != null)
        {
            for (Agent agent : agents)
            {
                //draw the AI's current Pill and Board
                agent.render(graphics);
            }
        }
    }
}