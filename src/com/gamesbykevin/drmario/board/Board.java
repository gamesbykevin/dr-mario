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
        
    //the total number of columns and rows in our Board
    private final int cols, rows;
    
    //minimum amount of pieces needed for a match
    private static final int MATCH_MINIMUM = 4;
    
    //the row to start the virus spawn to give the player some room
    private static final int SPAWN_START_ROW = 4;
    
    //the virus count, and progress count
    private final int count;
    
    //where are we compared to the actual total
    private int countProgress = 0;
    
    //the spawn locations for the viruses
    private List<Cell> locations;
    
    //if set to true there are dead pieces and we need to wait for the animation to finish
    private boolean dead = false;
    
    //once the dead pieces are removed we need to drop any existing pills accordingly
    private boolean drop = false;
    
    //are we done spawning viruses
    private boolean spawnComplete = false;
    
    //the timer for determining when the death animation is finished
    private Timer timer;
    
    //the time to show the dead blocks animation
    private static final long DEATH_TIME = TimerCollection.toNanoSeconds(750L);
    
    //the time to wait between dropping the Block(s)
    private static final long DROP_TIME = TimerCollection.toNanoSeconds(250L);
    
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
     * Are we done spawning viruses
     * @return boolean
     */
    public boolean isSpawnComplete()
    {
        return this.spawnComplete;
    }
    
    /**
     * Are any of the blocks currently dead
     * @return boolean
     */
    private boolean hasDead()
    {
        return this.dead;
    }
    
    /**
     * Are we in the middle of dropping any single existing blocks
     * @return 
     */
    private boolean hasDrop()
    {
        return this.drop;
    }
    
    /**
     * Have all the viruses been created
     * @return boolean
     */
    private boolean hasSpawnGoal()
    {
        return countProgress >= count;
    }
    
    /**
     * Does the Player have the opportunity to interact with the board.
     * This will be true if the spawn is complete, there are no dead Block(s)
     * and no Block(s) are currently dropping.
     * 
     * @return boolean
     */
    public boolean canInteract()
    {
        //if the spawn has completed and no Block(s) are dead and none of the Block(s) are dropping
        return (isSpawnComplete() && !hasDead() && !hasDrop());
    }
    
    /**
     * Here we will check for a match and drop the pieces if necessary
     */
    public void update(final long time)
    {
        //if we haven't reached our goal and there are still spawn locations
        if (!isSpawnComplete())
        {
            //add virus to board
            spawnVirus();
            
            //if we have reached our limit or no more spawn locations the spawn is complete
            this.spawnComplete = (hasSpawnGoal() || locations.isEmpty());
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
                    
                    //now that the dead pieces have been removed we need to drop the extra Block(s)
                    this.drop = true;
                    
                    //set the timer for the drop time
                    timer.setReset(DROP_TIME);
                    timer.reset();
                }
            }
            else
            {
                
                if (hasDrop())
                {
                    //update timer
                    timer.update(time);
                    
                    checkDrop();
                }
                
                //check the pieces on the board for a match
                checkMatch();
            }
        }
    }
    
    /**
     * Apply gravity to any separate hanging Block(s)
     */
    private void checkDrop()
    {
        //time has passed to make the next drop
        if (timer.hasTimePassed())
        {
            timer.reset();
            
            //are we finished dropping Block(s)
            boolean finish = true;
            
            //start on the second to last row and go backwards
            for (int row=rows - 2; row >= 0; row--)
            {
                for (int col=0; col < cols; col++)
                {
                    final Block tmp = getBlock(col, row);
                    
                    //if the block does not exist no need to check or if it is already dead
                    if (tmp == null)
                        continue;
                    
                    //we are only dropping pills so skip if other
                    if (!Pill.isPill(tmp))
                        continue;
                    
                    //if the Block below is a virus we can't drop because the viruses don't drop
                    if (getBlock(col, row + 1) != null && Virus.isVirus(getBlock(col, row + 1)))
                        continue;
                    
                    // the block below the current does not exist so we will need to check if it needs to be dropped
                    if (getBlock(col, row + 1) == null)
                    {
                        //get the Block to the west and east
                        final Block west = getBlock(col - 1, row);
                        final Block east = getBlock(col + 1, row);

                        //if there is no connecting piece to the east or west it will be dropped
                        if (west == null && east == null)
                        {
                            //apply Gravity to specific Block
                            applyGravity(col, row, tmp);
                            
                            //we have altered a Block so we are not finished
                            finish = false;
                        }
                        else
                        {
                            boolean westMember = false;
                            boolean eastMember = false;
                            
                            //if a west Block exists
                            if (west != null)
                            {
                                //is the west Block part of the current Block
                                westMember = (west.getGroup() == tmp.getGroup());
                                
                                //is the west Block part of the current Block
                                if (westMember)
                                {
                                    //the west Block does not have anchor below it
                                    if (getBlock(west.getCol(), west.getRow() + 1) == null)
                                    {
                                        //apply Gravity to specific Block
                                        applyGravity(col, row, tmp);

                                        //we have altered a Block so we are not finished
                                        finish = false;
                                    }
                                }
                            }
                            
                            //if an east Block exists
                            if (east != null)
                            {
                                //is the east Block part of the current Block
                                eastMember = (east.getGroup() == tmp.getGroup());
                                
                                //is the east Block part of the current Block
                                if (eastMember)
                                {
                                    //the east Block does not have anchor below it
                                    if (getBlock(east.getCol(), east.getRow() + 1) == null)
                                    {
                                        //apply Gravity to specific Block
                                        applyGravity(col, row, tmp);

                                        //we have altered a Block so we are not finished
                                        finish = false;
                                    }
                                }
                            }
                            
                            //if the neighbors exist but are not part of the same group we can drop
                            if (!westMember && !eastMember)
                            {
                                //apply Gravity to specific Block
                                applyGravity(col, row, tmp);

                                //we have altered a Block so we are not finished
                                finish = false;
                            }
                        }
                    }
                }
            }
            
            //if no changes were made, stop checking the drop
            if (finish)
            {
                drop = false;
            }
        }
    }
    
    /**
     * Applies gravity to the specified block.
     * We will remove the block from the array at the specified col/row position
     * and then apply it to its new location specified by block.getCol(), block.getRow().
     * 
     * @param col The column we want to remove the current Block from
     * @param row The row we want to remove the current Block from
     * @param block The Block we are re-positioning that contains the new col/row values
     */
    private void applyGravity(final int col, final int row, final Block block)
    {
        //drop Block down 1 row
        block.increaseRow();

        //update x, y coordinates
        block.setPosition(getX(), getY());

        //set the block to be in the correct location in the array
        setBlock(block.getCell(), block);

        //remove the block from the current position in the array
        removeBlock(col, row);
    }
    
    private void removeDead()
    {
        for (int row=0; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                if (getBlock(col, row) != null && getBlock(col, row).isDead())
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
        virus.setPosition(getX(), getY());

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

                //make sure we aren't soo close that there is no way we will make a match
                if (col <= cols - MATCH_MINIMUM)
                {
                    //start at the current position and head east checking for all matching
                    checkConsecutiveMatch(col, cols, row, tmpType, true);
                }
                
                //make sure we aren't soo close that there is no way we will make a match
                if (row <= rows - MATCH_MINIMUM)
                {
                    //start at the current position and head south checking for all matching
                    checkConsecutiveMatch(row, rows, col, tmpType, false);
                }
            }
        }
    }
    
    private void checkConsecutiveMatch(final int start, final int finish, final int staticDimension, final Type tmpType, final boolean horizontal)
    {
        //the number of matching blocks, start at 1 for the current location
        int matchCount = 1;
        
        //temporary block object
        Block tmpBlock;
        
        //from the next position check the rest for match going south
        for (int begin=start+1; begin < finish; begin++)
        {
            //if moving horizontal
            if (horizontal)
            {
                tmpBlock = getBlock(begin, staticDimension);
            }
            else
            {
                tmpBlock = getBlock(staticDimension, begin);
            }

            boolean match = (tmpBlock != null && tmpBlock.hasMatch(tmpType));

            if (match)
                matchCount++;

            //if the block does not exist or the type does not match or the last row
            if (tmpBlock == null || !match || begin == rows - 1)
            {
                //make sure we made the minimum requirements
                if (matchCount >= MATCH_MINIMUM)
                {
                    //remove all of the blocks
                    if (begin == finish - 1 && match)
                    {
                        if (horizontal)
                        {
                            markDead(start, begin, staticDimension, true);
                        }
                        else
                        {
                            markDead(start, begin, staticDimension, false);
                        }
                    }
                    else
                    {
                        if (horizontal)
                        {
                            markDead(start, begin - 1, staticDimension, true);
                        }
                        else
                        {
                            markDead(start, begin - 1, staticDimension, false);
                        }
                    }
                }

                //exit the loop and check next
                break;
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
            
            //if there is at least 1 pill we can skip checking the rest
            if (hasPill)
                break;
        }
        
        //of all the matching blocks at least 1 has to be a pill
        if (hasPill)
        {
            //dead will be true since we are marking blocks dead
            this.dead = true;
            
            //set the time limit to the death time and reset the timer
            this.timer.setReset(DEATH_TIME);
            this.timer.reset();
            
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
     * If the col/row is out of bounds null will be returned
     * @param col Column
     * @param row Row
     * @return Block, null will be returned if the col/row is out of bounds
     */
    public Block getBlock(final int col, final int row)
    {
        if (col < 0 || col > blocks[0].length - 1)
            return null;
        if (row < 0 || row > blocks.length - 1)
            return null;
        
        return blocks[row][col];
    }
    
    public Block getBlock(final Cell cell)
    {
        return getBlock(cell.getCol(), cell.getRow());
    }
    
    private void removeBlock(final int col, final int row)
    {
        blocks[row][col] = null;
    }
    
    private void setBlock(final Cell cell, final Block block)
    {
        setBlock(cell.getCol(), cell.getRow(), block);
    }
    
    private void setBlock(final int col, final int row, final Block block)
    {
        blocks[row][col] = block;
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