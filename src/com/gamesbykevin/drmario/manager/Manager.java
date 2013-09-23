package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.player.Agent;
import com.gamesbykevin.drmario.player.Human;
import com.gamesbykevin.drmario.player.Player;
import com.gamesbykevin.drmario.player.PlayerInformation.SpeedKey;
import com.gamesbykevin.drmario.resource.Resources.GameImage;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements Disposable
{
    //our human object
    private Human player;
    
    //our cpu opponent
    private List<Agent> agents;
    
    private int virusCount = 80;
    
    public Manager(final Engine engine) throws Exception
    {
        final Image image = engine.getResources().getGameImage(GameImage.Spritesheet);
        
        //player = new Human();
        
        //basic setup
        //initialize(player, SpeedKey.Medium, new Rectangle(0, 0, 256, 224), image);
        
        //our List of AI opponents
        agents = new ArrayList<>();
        
        final int opponentCount = 1;
        
        for (int i=0; i < opponentCount; i++)
        {
            //create new agent
            final Agent agent = new Agent();
            
            //basic setup
            initialize(agent, SpeedKey.Medium, new Rectangle(256, 0, 256, 224), image);
            
            //add to list
            agents.add(agent);
        }
        
        createBoards();
    }
    
    /**
     * All of the setup required for each Player
     * @param player Object that needs to be setup
     * @param speed Difficulty
     * @param container Game area
     * @param image Sprite Sheet
     * @throws Exception 
     */
    private void initialize(final Player player, final SpeedKey speed, final Rectangle container, final Image image) throws Exception
    {
        //set sprite sheet image
        player.setImage(image);
        
        //set difficulty
        player.setSpeed(speed);
        
        //setup all locations for the board etc..
        player.setInformationLocations(container);
        
        //set x,y, width,height
        player.setLocation(container.x, container.y);
        player.setDimensions(container.width, container.height);
        
        //create new board
        player.createBoard(virusCount);
        
        //create next pill
        player.createNextPill();
    }
    
    /**
     * Create all of the boards
     */
    private void createBoards() throws Exception
    {
        if (player != null)
        {
            player.createBoard(virusCount);
            player.createNextPill();
        }
        
        if (agents != null && !agents.isEmpty())
        {
            for (Agent agent : agents)
            {
                agent.createBoard(virusCount);
                agent.createNextPill();
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
    public void render(final Graphics graphics)
    {
        if (player != null)
        {
            //draw the player's screen
            player.render(graphics);
        }
        
        if (agents != null)
        {
            for (Agent agent : agents)
            {
                //draw the AI's screen
                agent.render(graphics);
            }
        }
    }
}