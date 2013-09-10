package com.gamesbykevin.drmario.board;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.block.Block;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The board where the viruses and pills will be contained
 * @author GOD
 */
public class Board extends Sprite
{
    //this List will contain all of the blocks on the board
    private List<Block> blocks;
    
    //the 2 blocks that represent the pill in the game
    private Block block1, block2;
    
    //the dimensions of each block
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    
    private final int cols, rows;
    
    //our timer object to determine when the pieces should drop
    private Timer timer;
    
    /**
     * Create a new empty board of the specified columns and rows and to be rendered within the screen.
     * 
     * @param container The container the board resides in
     * @param cols The total number of columns
     * @param rows The total number of rows
     */
    public Board(final Rectangle container, final int cols, final int rows)
    {
        //set the limits of the board
        this.cols = cols;
        this.rows = rows;
        
        //set the location and dimensions of the entire board
        super.setLocation(container.x, container.y);
        super.setDimensions(container.width, container.height);
        
        //new timer with 500 milisecond delay
        this.timer = new Timer(TimerCollection.toNanoSeconds(250L));
        
        //the blocks on the board
        blocks = new ArrayList<>();
        
        block1 = new Block(Color.RED);
        block1.setCol(5);
        block1.setRow(0);
        
        block2 = new Block(Color.BLUE);
        block2.setCol(4);
        block2.setRow(0);
    }
    
    private void startNewPiece()
    {
        block1 = new Block(Color.RED);
        block1.setCol(5);
        block1.setRow(0);
        
        block2 = new Block(Color.BLUE);
        block2.setCol(4);
        block2.setRow(0);
    }
    
    public void update(final long time, final Keyboard keyboard)
    {
        timer.update(time);
        
        //if time has passed the blocks need to drop
        if (timer.hasTimePassed() || keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            //remove key released from List
            keyboard.removeKeyReleased(KeyEvent.VK_DOWN);

            //reset the time
            timer.reset();
            
            //reset the location so when render is called the new position is calculated
            resetLocation(block1);
            resetLocation(block2);
            
            //have we hit the bottom
            if (block1.getRow() == rows - 1 || block2.getRow() == rows - 1)
            {
                //add block pieces to List
                addBlocks();
                
                //start new piece at top
                startNewPiece();
            }
            else
            {
                //move the blocks down 1 row
                block1.increaseRow();
                block2.increaseRow();
                
                //now that the blocked moved check for collision
                if (hasCollision(block1) || hasCollision(block2))
                {
                    //move the blocks back
                    block1.decreaseRow();
                    block2.decreaseRow();
                    
                    //add block pieces to List
                    addBlocks();

                    //start new piece at top
                    startNewPiece();
                }
            }
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            //move the blocks left
            block1.decreaseCol();
            block2.decreaseCol();

            //now that the blocked moved check for collision
            if (hasCollision(block1) || hasCollision(block2))
            {
                //move the blocks back
                block1.increaseCol();
                block2.increaseCol();
            }
            else
            {
                //reset the location so when render is called the new position is calculated
                resetLocation(block1);
                resetLocation(block2);
            }
            
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
        }

        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            //move the blocks right
            block1.increaseCol();
            block2.increaseCol();

            //now that the blocked moved check for collision
            if (hasCollision(block1) || hasCollision(block2))
            {
                //move the blocks back
                block1.decreaseCol();
                block2.decreaseCol();
            }
            else
            {
                //reset the location so when render is called the new position is calculated
                resetLocation(block1);
                resetLocation(block2);
            }
            
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
    }
    
    /**
     * Call this method when you need to update the position of the Block.
     * 
     * @param block The block we want to set the new location
     */
    private void resetLocation(final Block block)
    {
        block.setDimensions(0, 0);
    }
    
    /**
     * Are we supposed to calculate new coordinates for this Block
     * @param block The Block we are checking if we need to reset
     * @return boolean
     */
    private boolean hasRecalculateLocation(final Block block)
    {
        return (block.getWidth() == 0 || block.getHeight() == 0);
    }
    
    private boolean hasCollision(final Block tmp)
    {
        for (Block block : blocks)
        {
            if (block.getCol() == tmp.getCol() && block.getRow() == tmp.getRow())
                return true;
        }
        
        if (tmp.getCol() < 0 || tmp.getCol() > cols - 1)
            return true;
        if (tmp.getRow() < 0 || tmp.getRow() > rows - 1)
            return true;
        
        return false;
    }
    
    private void addBlocks()
    {
        blocks.add(block1);
        blocks.add(block2);
    }
    
    public void render(Graphics graphics)
    {
        //draw outline of board for now
        graphics.setColor(Color.WHITE);
        graphics.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        //draw all of the blocks already on the board
        for (Block block : blocks)
        {
            if (block != null)
            {
                setLocation(block);
                block.render(graphics);
            }
        }
        
        setLocation(block1);
        block1.render(graphics);
        
        setLocation(block2);
        block2.render(graphics);
    }
    
    /**
     * Set the proper block location/dimensions
     * @param block The block we want to set
     */
    private void setLocation(final Block block)
    {
        //if we aren't supposed to recalculate don't bother
        if (!hasRecalculateLocation(block))
            return;
        
        final int x = (int)(super.getX() + (block.getCol() * WIDTH));
        final int y = (int)(super.getY() + (block.getRow() * HEIGHT));

        //set the appropriate location and dimensions
        block.setLocation(x, y);
        block.setDimensions(WIDTH, HEIGHT);        
    }
}