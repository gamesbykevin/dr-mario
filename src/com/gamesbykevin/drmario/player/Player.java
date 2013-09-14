/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Pill;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;

/**
 *
 * @author GOD
 */
public class Player implements IPlayer
{
    //pill piece
    private Pill pill;
    
    //the start location for the Pill
    protected static final Cell START = new Cell(3, 0);
    
    //our timer object to determine when the pieces should drop
    private Timer timer;
    
    //has the player lost
    private boolean lose = false;
    
    //the final location of the Pill
    private Cell goal;
    
    //the final rotation as well
    private Rotation rotation;
    
    /**
     * Create a new timer with the specified delay that will determine the duration
     * between Pill drops.
     * @param delay The time delay in milliseconds
     */
    public Player(final long delay)
    {
        //new timer with the specified milisecond delay
        this.timer = new Timer(TimerCollection.toNanoSeconds(delay));
    }
    
    @Override
    public void update(final Engine engine)
    {
        //the entrance is blocked (Game Over)
        if (engine.getManager().getBoard().getBlock(START) != null)
        {
            this.lose = true;
            return;
        }
        
        //if our pill has not been created yet 
        if (getPill() == null)
            createPill();
        
        //update timer
        getTimer().update(engine.getMain().getTime());
        
        //has time passed
        if (getTimer().hasTimePassed())
        {
            //reset Timer
            getTimer().reset();
            
            //apply gravity to the board
            applyGravity(engine.getManager().getBoard());
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
    
    protected Cell getGoal()
    {
        return this.goal;
    }
    
    protected Rotation getRotation()
    {
        return this.rotation;
    }
    
    protected void applyGravity(final Board board)
    {
        //have we hit the bottom row
        if (getPill().hasRow(board.getRows() - 1))
        {
            //place Pill
            board.addPill(getPill());
            
            //now that pill has been placed remove it
            removePill();
            
            //remove the goal set
            resetGoal();
        }
        else
        {
            //move the pill down 1 row
            getPill().increaseRow();

            //now that the pill moved check for collision
            if (board.hasCollision(getPill()))
            {
                //move the pill back
                getPill().decreaseRow();

                //place Pill
                board.addPill(getPill());
                
                //now that Pill has been placed remove it
                removePill();
                
                //remove the goal set
                resetGoal();
            }
        }
    }
    
    public boolean hasLose()
    {
        return this.lose;
    }
    
    /**
     * Get the Timer for the player
     * @return Timer
     */
    protected Timer getTimer()
    {
        return this.timer;
    }
    
    /**
     * Get the current Pill being used
     * @return 
     */
    protected Pill getPill()
    {
        return this.pill;
    }
    
    /**
     * Create a new pill
     */
    protected void createPill()
    {
        pill = new Pill();
        pill.setStart(START);
    }
    
    protected void removePill()
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
        if (getPill() != null)
        {
            getPill().render(graphics);
        }
    }
}