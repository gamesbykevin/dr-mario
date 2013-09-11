package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.block.Pill;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * The player will contain the pill piece and the input 
 * @author GOD
 */
public class Player 
{
    //pill piece
    private Pill pill;
    
    //the start location for the Pill
    private static final Cell START = new Cell(3, 0);
    
    //our timer object to determine when the pieces should drop
    private Timer timer;
    
    public Player()
    {
        //new timer with 1000 milisecond delay
        this.timer = new Timer(TimerCollection.toNanoSeconds(1000L));
    }
    
    /**
     * Create a new pill
     */
    private void createPill()
    {
        pill = new Pill();
        pill.setStart(START);
        pill.setDimensions(Board.WIDTH, Board.HEIGHT);
    }
    
    public void update(final Keyboard keyboard, final Board board, final long time)
    {
        //the entrance is blocked (Game Over)
        if (board.getBlock(START) != null)
            return;
        
        //if our pieces have not been created yet 
        if (pill == null)
            createPill();
        
        //update timer
        timer.update(time);
        
        //if time has passed the blocks need to drop or if the user is forcing the piece to drop
        if (timer.hasTimePassed() || keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
        {
            //remove key released from List
            keyboard.removeKeyPressed(KeyEvent.VK_DOWN);

            //reset the time
            timer.reset();
            
            //have we hit the bottom
            if (pill.getRow() == board.getRows() - 1 || pill.getExtra().getRow() == board.getRows() - 1)
            {
                //place piece and create new one
                placePill(board);
            }
            else
            {
                //move the pill down 1 row
                pill.increaseRow();
                
                //now that the pill moved check for collision
                if (board.hasCollision(pill))
                {
                    //move the pill back
                    pill.decreaseRow();
                    
                    //place piece and create new one
                    placePill(board);
                }
            }
        }
        
        //the user wants to rotate the pieces
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_UP);
            
            //rotate the pill
            pill.rotate();
            
            //check for collision
            if (board.hasCollision(pill))
            {
                //reset the location because of collision
                pill.reset();
            }
        }
        
        //move piece to the left
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            //move the pill left
            pill.decreaseCol();

            //now that the blocked moved check for collision
            if (board.hasCollision(pill))
            {
                //move the pill back
                pill.increaseCol();
            }
            
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
        }

        //move the piece to the right
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            //move the blocks right
            pill.increaseCol();

            //now that the blocked moved check for collision
            if (board.hasCollision(pill))
            {
                //move the blocks back
                pill.decreaseCol();
            }
            
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
        }
        
        //set the correct x,y coordinates for the piece
        board.setLocation(pill);
    }
    
    /**
     * Place the piece on the board. 
     * Then after the piece is created create a new Piece
     * @param board The board we will be adding the piece to
     */
    private void placePill(final Board board)
    {
        //set the x,y coordinates for the piece
        board.setLocation(pill);
        
        //add the Pill to the board
        board.addPill(pill);
        
        //now that pieces have been placed create new piece at the top
        createPill();
    }
    
    public void render(final Graphics graphics)
    {
        pill.render(graphics);
    }
}