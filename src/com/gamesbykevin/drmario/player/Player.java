package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Pill;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;

import com.gamesbykevin.drmario.shared.IElement;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 *
 * @author GOD
 */
public class Player implements IElement
{
    //the board where the game play will occur
    private Board board;
    
    //pill piece
    private Pill pill, next;
    
    //the start location for the Pill
    protected static final Cell START = new Cell(3, 0);
    
    //our timer object to determine when the pieces should drop
    private Timer gravityTimer;
    
    //has the player lost
    private boolean lose = false;
    
    //has the player won
    private boolean win = false;
    
    //the final location of the Pill
    private Cell goal;
    
    //the final rotation as well
    private Rotation rotation;
    
    //the seed used to generate random numbers
    private final long SEED = 21949303610819L;//System.nanoTime();//11407889599305L;
    
    private final Random random;
    
    /**
     * Create a new timer with the specified delay that will determine the duration
     * between Pill drops.
     * @param delay The time delay in milliseconds
     */
    public Player(final long delay) throws Exception
    {
        //new timer with the specified milisecond delay
        this.gravityTimer = new Timer(TimerCollection.toNanoSeconds(delay));
        
        this.random = new Random(SEED);
        
        System.out.println(SEED);
        
        //create a Next Pill that will be the next Pill used
        createNextPill();
    }
    
    public void createBoard(final Rectangle container, final int virusCount) throws Exception
    {
        board = new Board(container, virusCount, SEED);
    }
    
    protected Board getBoard()
    {
        return this.board;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (board != null)
            board.dispose();
        
        if (pill != null)
            pill.dispose();
        
        if (next != null)
            next.dispose();
        
        board = null;
        pill = null;
        next = null;
        
        gravityTimer = null;
    
        goal = null;
    
        rotation = null;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if we have won or lost there is no need to update
        if (hasWin() || hasLose())
            return;
        
        //the entrance is blocked (Game Over)
        if (getBoard().getBlock(START) != null)
        {
            this.lose = true;
            return;
        }
        
        //have we removed all of the viruses
        if (getBoard().getVirusCount() <= 0)
        {
            this.win = true;
            return;
        }
        
        //if our pill has not been created yet 
        if (getPill() == null)
        {
            //take the next Pill and assign as current
            createPill();
            
            //create a new Pill for the future
            createNextPill();
        }
        
        //update timer
        getTimer().update(engine.getMain().getTime());
        
        //has time passed
        if (getTimer().hasTimePassed())
        {
            //apply gravity to the Pill and check the Board for collision
            applyGravity();
        }
    }
    
    /**
     * Let the Artificial Intelligence know what the destination and rotation is
     * @param goal The col/row of where we want the Pill to be placed.
     * @param rotation The final rotation if the Pill
     */
    protected void setGoals(final Cell goal, final Rotation rotation)
    {
        this.goal = goal;
        this.rotation = rotation;
    }
    
    /**
     * Get the Goal destination set by the AI 
     * @return Cell containing the col/row of our destination
     */
    protected Cell getGoal()
    {
        return this.goal;
    }
    
    /**
     * Get the goal Rotation set by the AI
     * @return Rotation (North, South, East, or West)
     */
    protected Rotation getRotation()
    {
        return this.rotation;
    }
    
    /**
     * Apply gravity to the current Pill.
     * If the Pill hits the bottom or has a collision we will
     * add the Pill to the board and then remove and reset the goal.
     * In addition since gravity is applied we reset the gravity timer as well
     * 
     * @param board The board we are using to check for collision
     */
    protected void applyGravity() throws Exception
    {
        //move the pill down 1 row
        getPill().increaseRow();

        //now that the pill moved check for collision
        if (getBoard().hasCollision(getPill()))
        {
            //move the pill back
            getPill().decreaseRow();

            if (getPill().getRow() < 0 || getPill().getExtra().getRow() < 0)
            {
                this.lose = true;
                return;
            }
            else
            {
                //place Pill on board
                getBoard().addPill(getPill());

                //now that Pill has been placed remove it
                removePill();

                //reset the goal
                resetGoal();
            }
        }
        
        //reset the time until gravity has to be applied again
        getTimer().reset();
    }
    
    /**
     * Make sure the current Pill has the correct 
     * (x,y) coordinates based on their (column, row) location
     * @param board The game board that contains the (x, y) start position
     */
    protected void updateLocation()
    {
        if (getPill() != null)
        {
            //set the correct x,y coordinates for the pill
            getPill().setPosition(getBoard().getX(), getBoard().getY());
        }
    }
    
    public boolean hasLose()
    {
        return this.lose;
    }
    
    public boolean hasWin()
    {
        return this.win;
    }
    
    /**
     * Get the Timer for the player that determines when gravity is applied
     * @return Timer
     */
    private Timer getTimer()
    {
        return this.gravityTimer;
    }
    
    /**
     * Get the current Pill being used
     * @return Pill the current Pill we are controlling
     */
    protected Pill getPill()
    {
        return this.pill;
    }
    
    /**
     * Get the Pill that is destined to be the next Pill after
     * the current Pill is placed on the board.
     * 
     * @return Pill the next Pill after the current Pill has been placed
     */
    protected Pill getNext()
    {
        return this.next;
    }
    
    protected void createNextPill() throws Exception
    {
        this.next = new Pill(random);
        this.next.setStart(START);
    }
    
    /**
     * Take the next Pill and make that the current
     */
    protected void createPill()
    {
        //assign the next Pill to the current
        this.pill = next;
    }
    
    /**
     * Remove the current Pill
     */
    private void removePill()
    {
        this.pill = null;
    }
    
    /**
     * Remove the goal set
     */
    private void resetGoal()
    {
        this.goal = null;
        this.rotation = null;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        getBoard().render(graphics);
        //getBoard().canInteract();
        if (getPill() != null)
        {
            getPill().render(graphics);
        }
    }
}