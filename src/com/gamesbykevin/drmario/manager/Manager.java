package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.display.WindowHelper;

import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.menu.CustomMenu.LayerKey;
import com.gamesbykevin.drmario.menu.CustomMenu.OptionKey;
import com.gamesbykevin.drmario.player.Agent;
import com.gamesbykevin.drmario.player.Human;
import com.gamesbykevin.drmario.player.Player;
import com.gamesbykevin.drmario.player.PlayerInformation.SpeedKey;
import com.gamesbykevin.drmario.resource.Resources.GameImage;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements Disposable
{
    //our human object
    private Human human;
    
    //our cpu opponent
    private List<Agent> agents;
    
    //number of viruses
    private int virusCount = 0;
    
    //the current level
    private int level = 0;
    
    //a factor of four per level will determine the total virus count
    private final static int VIRUS_PER_LEVEL = 4;
    
    //window that will contain the cpu opponents
    private static final Rectangle WINDOW_CPU = new Rectangle(448, 0, 448, 392);
    
    //different game modes
    private static final int MODE_REGULAR = 0;
    private static final int MODE_TIMED = 1;
    private static final int MODE_REGULAR_VS = 2;
    private static final int MODE_ATTACK_VS = 3;
    
    private final int modeIndex;
    
    //the amount of time to add for each virus in Timed mode, currently 20 seconds
    private static final long TIMED_DELAY = TimerCollection.toNanoSeconds(20000L);
    
    public Manager(final Engine engine) throws Exception
    {
        //get the speed selected from the menu
        final SpeedKey speedKey = SpeedKey.values()[engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Speed)];
        
        //get the level selected
        this.level = engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Level);
        
        //the type of game being played
        this.modeIndex = engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Mode);
                
        //is this game single player
        final boolean singlePlayer = (modeIndex == MODE_REGULAR || modeIndex == MODE_TIMED);
        
        //number of opponents
        int opponentLimit = engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.OpponentTotal);
        
        //is this a single player game
        if (singlePlayer)
        {
            //no opponents when playing single player
            opponentLimit = 0;
        }
        else
        {
            //if multiplayer make sure there is at least 1 opponent
            if (opponentLimit < 1)
                opponentLimit++;
        }
        
        //sprite sheet for game
        final Image image = engine.getResources().getGameImage(GameImage.Spritesheet);
        
        //the location where the human will be drawn
        final Rectangle renderLocation;
        
        //if we are playing multiplayer
        if (!singlePlayer)
        {
            //there are opponents so place this on the left side
            renderLocation = new Rectangle(0, 0, 448, 392);
        }
        else
        {
            //there are no opponents so this can be placed in the middle
            renderLocation = new Rectangle(224, 0, 448, 392);
        }
        
        //create new human with the given dimensions
        human = new Human(renderLocation);
        
        //basic setup of human
        initialize(human, speedKey, image);
        
        //our List of AI opponents
        agents = new ArrayList<>();
        
        //if we are supposed to have opponents
        if (opponentLimit > 0)
        {
            final int cols, rows;

            //if only 1 opponent they will get the entire window
            if (opponentLimit == 1)
            {
                cols = 1;
                rows = 1;
            }
            else
            {
                //anything else 2 x 2
                cols = 2;
                rows = 2;
            }

            //get a number of windows all equal in size depending on the number of rows and columns
            Rectangle[][] windows = WindowHelper.getWindows(WINDOW_CPU, rows, cols);

            for (int row = 0; row < windows.length; row++)
            {
                for (int col = 0; col < windows[0].length; col++)
                {
                    //we will stop once we have reached our limit
                    if (agents.size() >= opponentLimit)
                        continue;

                    //create new agent
                    final Agent agent = new Agent(windows[row][col]);

                    //basic setup of agent
                    initialize(agent, speedKey, image);

                    //add to list
                    agents.add(agent);
                }
            }
        }
        
        //setup next level for all players
        setNextLevel();
    }
    
    /**
     * All of the setup required for each Player
     * @param player Object that needs to be setup
     * @param speed Difficulty
     * @param container Game area
     * @param image Sprite Sheet
     * @throws Exception 
     */
    private void initialize(final Player player, final SpeedKey speed, final Image image)
    {
        //set sprite sheet image
        player.setImage(image);
        
        //set difficulty
        player.setSpeed(speed);
        
        //setup all locations for the board etc..
        player.setInformationLocations();
    }
    
    /**
     * Set up all the information for the next level for the specified player
     * @param player 
     */
    private void setNextLevel(final Player player)
    {
        //get the virus count based on the current level
        this.virusCount = (level * VIRUS_PER_LEVEL);
        
        //time passed timer or time remaining depending on the number of viruses
        final long time = (modeIndex != MODE_TIMED) ? 0 : (virusCount * TIMED_DELAY);
        
        //create new board
        player.createBoard(virusCount);
        
        //create next pill
        player.createNextPill();
        
        //create timer
        player.createTimer(time);
        
        //set the level for proper display
        player.setLevel(level);
        
        //remove win or lose
        player.resetStatus();
    }
    
    /**
     * Sets the next level up for all players
     */
    private void setNextLevel()
    {
        //move to the next level
        this.level++;
        
        if (human != null)
        {
            setNextLevel(human);
        }
        
        if (!agents.isEmpty())
        {
            for (Agent agent : agents)
            {
                setNextLevel(agent);
            }
        }
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (human != null)
        {
            human.dispose();
            human = null;
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
        if (human != null)
        {
            //check for keyboard input etc..
            human.update(engine);
            
            //if the human won, check for input to go to next level
            if (human.hasWin())
            {
                //space bar was hit so go to next level
                if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_SPACE))
                {
                    engine.getKeyboard().removeKeyPressed(KeyEvent.VK_SPACE);
                    
                    //setup next level for all players
                    setNextLevel();
                    
                    //exit method
                    return;
                }
            }
        }

        if (agents != null)
        {
            for (Agent agent : agents)
            {
                //execute ai logic
                agent.update(engine);
            }
        }
        
        //check if one of the players won
        locateWinner();
    }
    
    /**
     * Check if one of the players won and if so mark every one else defeated.
     */
    private void locateWinner()
    {
        //if playing against opponents
        if (!agents.isEmpty())
        {
            //has one of the agents won
            boolean hasWin = false;
            
            for (Agent agent : agents)
            {
                if (agent.hasWin())
                {
                    hasWin = true;
                    break;
                }
            }
            
            //if agent won set others to lose as well as human
            if (hasWin)
            {
                for (Agent agent : agents)
                {
                    if (!agent.hasWin())
                    {
                        agent.setLose();
                    }
                }

                human.setLose();
            }
            else
            {
                //if human won
                if (human.hasWin())
                {
                    //set every agent to lose
                    for (Agent agent : agents)
                    {
                        agent.setLose();
                    }
                }

                //if human lost
                if (human.hasLose())
                {
                    //set every agent to win
                    for (Agent agent : agents)
                    {
                        agent.setWin();
                    }
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    public void render(final Graphics graphics)
    {
        if (human != null)
        {
            //draw the player's screen
            human.render(graphics);
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