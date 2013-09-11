package com.gamesbykevin.drmario.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.drmario.block.*;
import com.gamesbykevin.drmario.block.Block.*;

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
    private Block[][] blocks;
    
    //the dimensions of each block
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    
    //the total number of columns and rows in our Board
    private final int cols, rows;
    
    //minimum amount of pieces needed for a match
    private static final int MATCH_MINIMUM = 4;
    
    //the row to start the virus spawn to give the player some room
    private static final int SPAWN_START_ROW = 3;
    
    //the virus count, and progress count
    private final int count;
    
    //where are we compared to the actual total
    private int countProgress = 0;
    
    //the spawn locations for the viruses
    private List<Cell> locations;
    
    //if set to true there are dead pieces and we need to wait for the animation to finish
    private boolean dead = false;
    
    //the timer for determining when the death animation is finished
    private Timer timer;
    
    //the time to show the dead blocks animation
    private static final long DEATH_TIME = TimerCollection.toNanoSeconds(750L);
    
    /**
     * Create a new empty board of the specified columns and rows and to be rendered within the screen.
     * 
     * @param container The container the board resides in
     * @param cols The total number of columns
     * @param rows The total number of rows
     * @param count The total number of viruses
     */
    public Board(final Rectangle container, final int cols, final int rows, final int count)
    {
        //set the limits of the board
        this.cols = cols;
        this.rows = rows;
        
        //create new timer with death time set
        this.timer = new Timer(DEATH_TIME);
        
        //set the virus count
        this.count = count;
        
        //set the location and dimensions of the entire board
        super.setLocation(container.x, container.y);
        super.setDimensions(container.width, container.height);
        
        //the blocks on the board
        blocks = new Block[rows][cols];
        
        //create a new list for the spawn locations
        locations = new ArrayList<>();
        
        //add all spawn locations so we can choose at random
        for (int row=SPAWN_START_ROW; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                locations.add(new Cell(col, row));
            }
        }
    }
    
    /**
     * Are any of the blocks currently dead
     * @return boolean
     */
    public boolean hasDead()
    {
        return this.dead;
    }
    
    /**
     * Have all the viruses been created
     * @return boolean
     */
    public boolean hasSpawnGoal()
    {
        return countProgress >= count;
    }
    
    /**
     * Here we will check for a match and drop the pieces if necessary
     */
    public void update(final long time)
    {
        //if we haven't reached our goal and there are still spawn locations
        if (!hasSpawnGoal() && !locations.isEmpty())
        {
            //add virus to board
            spawnVirus();
        }
        else
        {
            //if blocks are dead wait until animation has finished
            if (hasDead())
            {
                //update timer
                timer.update(time);
                
                //if the time has passed
                if (timer.hasTimePassed())
                {
                    //reset timer
                    timer.reset();
                    
                    //pieces are no longer dead
                    this.dead = false;
                    
                    //remove any existing dead pieces
                    removeDead();
                }
            }
            else
            {
                //check the pieces on the board for a match
                checkMatch();
            }
        }
    }
    
    private void removeDead()
    {
        for (int row=0; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                if (blocks[row][col] != null && blocks[row][col].isDead())
                {
                    blocks[row][col] = null;
                }
            }
        }
    }
    
    private void spawnVirus()
    {
        //pick a random index
        final int index = (int)(Math.random() * locations.size());

        //get the location using the index
        Cell tmp = locations.get(index);

        //create new virus
        Virus virus = new Virus();

        //set the correct Column, Row
        virus.setCol(tmp);
        virus.setRow(tmp);

        //update the virus to be at the given location
        setLocation(virus);

        //set the virus on the board
        blocks[tmp.getRow()][tmp.getCol()] = virus;

        //remove location from list so we don't pick it again
        locations.remove(index);

        //increase the progress
        countProgress++;
    }
    
    private void checkMatch()
    {
        for (int row=0; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                Block block = getBlock(col, row);

                //block not found, or if block is dead we don't need to check it
                if (block == null || block.isDead())
                    continue;

                //get the block type
                final Type tmpType = block.getType();

                //start at the current position and head east checking for all matching
                checkMatchEast(col, row, tmpType);
                
                //start at the current position and head south checking for all matching
                checkMatchSouth(col, row, tmpType);
            }
        }
    }
    
    /**
     * Starting at the given col, row go south and check if the board blocks match of tmpType.
     * If a match is found the group will be marked as dead
     * 
     * @param col
     * @param row
     * @param tmpType 
     */
    private void checkMatchSouth(final int col, final int row, final Type tmpType)
    {
        //the number of matching blocks, start at 1 for the current location
        int matchCount = 1;
        
        //temporary block object
        Block tmpBlock;
        
        //make sure we aren't soo close that there is no way we will make a match
        if (row <= rows - MATCH_MINIMUM + 1)
        {
            //from the next position check the rest for match going south
            for (int start=row+1; start < rows; start++)
            {
                tmpBlock = getBlock(col, start);

                boolean match = (tmpBlock != null && tmpBlock.hasMatch(tmpType));

                if (match)
                    matchCount++;

                //if the block does not exist or the type does not match or the last row
                if (tmpBlock == null || !match || start == rows - 1)
                {
                    //make sure we made the minimum requirements
                    if (matchCount >= MATCH_MINIMUM)
                    {
                        //remove all of the blocks
                        if (start == rows - 1 && match)
                        {
                            markDead(row, start, col, false);
                        }
                        else
                        {
                            markDead(row, start - 1, col, false);
                        }
                    }

                    //exit the loop and check the next column
                    break;
                }
            }
        }
    }
    
    /**
     * Starting at the given col, row go east and check if the board blocks match of tmpType.
     * If a match is found the group will be marked as dead
     * 
     * @param col
     * @param row
     * @param tmpType 
     */
    private void checkMatchEast(final int col, final int row, final Type tmpType)
    {
        //the number of matching blocks, start at 1 for the current location
        int matchCount = 1;
        
        //temporary block object
        Block tmpBlock;
        
        //make sure we aren't soo close that there is no way we will make a match
        if (col <= cols - MATCH_MINIMUM + 1)
        {
            //from the next position check the rest for match going east
            for (int start=col+1; start < cols; start++)
            {
                tmpBlock = getBlock(start, row);

                boolean match = (tmpBlock != null && tmpBlock.hasMatch(tmpType));

                if (match)
                    matchCount++;

                //if the block does not exist or the type does not match or the last column
                if (tmpBlock == null || !match || start == cols - 1)
                {
                    //make sure we made the minimum requirements
                    if (matchCount >= MATCH_MINIMUM)
                    {
                        //remove all of the blocks
                        if (start == cols - 1 && match)
                        {
                            markDead(col, start, row, true);
                        }
                        else
                        {
                            markDead(col, start - 1, row, true);
                        }
                    }

                    //exit the loop and check the next column
                    break;
                }
            }
        }
    }
    
    /**
     * Mark a number of blocks dead with the given parameters and in the given direction
     * @param start The start position
     * @param end The end position
     * @param dimension Either the row or column that won't change
     * @param east Are we heading east, if false then we are heading south
     */
    private void markDead(final int start, final int end, final int dimension, final boolean east)
    {
        boolean hasPill = false;
        
        //in order for the viruses to be removed at least one of the blocks has to be a pill
        for (int current = start; current <= end; current++)
        {
            hasPill = (east) ? (blocks[dimension][current] != null && Pill.isPill(blocks[dimension][current].getType())) : (blocks[current][dimension] != null && Pill.isPill(blocks[current][dimension].getType()));
            
            if (hasPill)
                break;
        }
        
        //of all the matching blocks at least 1 has to be a pill
        if (hasPill)
        {
            //dead will be true since we are marking blocks dead
            this.dead = true;
            
            for (int current = start; current <= end; current++)
            {
                //if we are heading east the column will be what is changed
                if (east)
                {
                    blocks[dimension][current].setDead(true);
                }
                else
                {
                    //we are heading sound and the row will be the variable
                    blocks[current][dimension].setDead(true);
                }
            }
        }
    }
    
    /**
     * Get the block of the specified column, row
     * @param col Column
     * @param row Row
     * @return Block
     */
    public Block getBlock(final int col, final int row)
    {
        return blocks[row][col];
    }
    
    public Block getBlock(final Cell cell)
    {
        return getBlock(cell.getCol(), cell.getRow());
    }
    
    /**
     * Get the total number of Columns on the board
     * @return int
     */
    public int getCols()
    {
        return this.cols;
    }
    
    /**
     * Get the total number of Rows on the board
     * @return int
     */
    public int getRows()
    {
        return this.rows;
    }
    
    public void setLocation(final Pill pill)
    {
        setLocation((Block)pill);
        setLocation(pill.getExtra());
    }
    
    /**
     * Set the proper block location/dimensions
     * @param block The block we want to set
     */
    public void setLocation(final Block block)
    {
        final int x = (int)(super.getX() + (block.getCol() * WIDTH));
        final int y = (int)(super.getY() + (block.getRow() * HEIGHT));

        //set the appropriate location and dimensions
        block.setLocation(x, y);
        block.setDimensions(WIDTH, HEIGHT);
    }
    
    public boolean hasCollision(final Pill pill)
    {
        return (hasCollision((Block)pill) || hasCollision(pill.getExtra()));
    }
    
    /**
     * Check if the given Block collides with any of the Blocks on our board.
     * Will return true if an existing block is at the same spot or if the given block is out of bounds.
     * 
     * @param block The Block we want to check for collision
     * @return boolean Return true if the Block euqals any of the existing Block(s) on the board
     */
    private boolean hasCollision(final Block block)
    {
        //there will be a collision if the user goes out of bounds
        if (block.getCol() < 0 || block.getCol() > cols - 1)
            return true;
        if (block.getRow() < 0 || block.getRow() > rows - 1)
            return true;
        
        //if an object exists then there is a collision
        if (blocks[block.getRow()][block.getCol()] != null)
            return true;
        
        return false;
    }
    
    /**
     * Add the Pill to the board
     * @param pill The pill we want to add
     */
    public void addPill(final Pill pill)
    {
        blocks[pill.getRow()][pill.getCol()] = new Block(pill);
        blocks[pill.getExtra().getRow()][pill.getExtra().getCol()] = new Block(pill.getExtra());
    }
    
    public void render(Graphics graphics)
    {
        //draw outline of board for now
        graphics.setColor(java.awt.Color.WHITE);
        graphics.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        for (int row=0; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                //draw the blocks that exist
                if (blocks[row][col] != null)
                    blocks[row][col].render(graphics);
            }
        }
    }
}