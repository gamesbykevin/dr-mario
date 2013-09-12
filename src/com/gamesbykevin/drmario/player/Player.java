/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Pill;
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
    
    private boolean lose = false;
    
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
    
    protected void applyGravity(final Board board)
    {
        //have we hit the bottom row
        if (getPill().hasRow(board.getRows() - 1))
        {
            //place piece and create new one
            placePill(board);
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

                //place piece and create new one
                placePill(board);
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
    
    /**
     * Place the piece on the board. 
     * Then after the piece is created create a new Piece
     * @param board The board we will be adding the piece to
     */
    protected void placePill(final Board board)
    {
        //set the x,y coordinates for the piece
        getPill().setPosition(board.getX(), board.getY());
        
        //add the Pill to the board
        board.addPill(getPill());
        
        //now that pieces have been placed create new piece at the top
        createPill();
    }
    
    public void render(final Graphics graphics)
    {
        getPill().render(graphics);
    }
}