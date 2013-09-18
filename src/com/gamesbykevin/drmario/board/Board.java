package com.gamesbykevin.drmario.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.drmario.block.*;
import com.gamesbykevin.drmario.block.Block.*;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.shared.IElement;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board where the viruses and pills will be contained
 * @author GOD
 */
public class Board extends Sprite implements IElement
{
    //this List will contain all of the blocks on the board
    private Block[][] blocks;
    
    //if any blocks need to be marked as dead they will be contained in here
    private List<Block> deadBlocks;
    
    //the total number of columns and rows in our Board
    private final int cols, rows;
    
    //minimum amount of pieces needed for a match
    public static final int MATCH_MINIMUM = 4;
    
    //the row to start the virus spawn to give the player some room
    private static final int SPAWN_START_ROW = 5;
    
    //the virus count, and progress count
    private final int virusCount;
    
    //where are we compared to the actual total
    private int countProgress = 0;
    
    //the spawn locations for the viruses
    private List<Cell> locations;
    
    //once the dead pieces are removed we need to drop any existing pills accordingly
    private boolean drop = false;
    
    //are we done spawning viruses
    private boolean spawnComplete = false;
    
    //the timer for determining when the death animation is finished
    private Timer timer;
    
    //the time to show the dead blocks animation
    //private static final long DEATH_TIME = TimerCollection.toNanoSeconds(500L);
    private static final long DEATH_TIME = TimerCollection.toNanoSeconds(1L);
    
    //the time to wait between dropping the Block(s)
    //private static final long DROP_TIME = TimerCollection.toNanoSeconds(250L);
    private static final long DROP_TIME = TimerCollection.toNanoSeconds(1L);
    
    //the dimensions for the board
    private static final int COLUMNS = 8;
    private static final int ROWS = 16;
    
    //our random number generator object
    private final Random random;
    
    /**
     * Create a new empty board of the specified columns and rows and to be rendered within the screen.
     * 
     * @param container The container the board resides in
     * @param cols The total number of columns
     * @param rows The total number of rows
     * @param count The total number of viruses
     */
    public Board(final Rectangle container, final int virusCount, final long seed)
    {
        //set the limits of the board
        this.cols = COLUMNS;
        this.rows = ROWS;
        
        //create our random number generator object
        this.random = new Random(seed);
        
        //create new timer with death time set
        this.timer = new Timer(DEATH_TIME);
        
        //set the virus count
        this.virusCount = virusCount;
        
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
        
        //create an empty List that will contain our dead Block(s)
        this.deadBlocks = new ArrayList<>();
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        super.dispose();
        
        for (int col=0; col < blocks[0].length; col++)
        {
            for (int row=0; row < blocks.length; row++)
            {
                if (blocks[row][col] != null)
                {
                    blocks[row][col].dispose();
                    blocks[row][col] = null;
                }
            }
        }
        
        blocks = null;
        
        deadBlocks.clear();
        deadBlocks = null;
    
        locations.clear();
        locations = null;
    
        timer = null;
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
        return (!deadBlocks.isEmpty());
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
        return countProgress >= virusCount;
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
     * Here we will check for match and drop the Block(s) if necessary
     * @param engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
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
                timer.update(engine.getMain().getTime());
                
                //if the time has passed
                if (timer.hasTimePassed())
                {
                    //reset timer
                    timer.reset();
                    
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
                    timer.update(engine.getMain().getTime());
                    
                    checkDrop();
                }
                else
                {
                    //create a List of Block(s) that match
                    List<Block> deadBlocksTmp = getMatches();

                    //were any dead Block(s) found
                    if (deadBlocksTmp.size() > 0)
                    {
                        //set the time limit to the death time and reset the timer
                        this.timer.setReset(DEATH_TIME);
                        this.timer.reset();

                        //mark all found as dead
                        for (Block tmpBlock : deadBlocksTmp)
                        {
                            //mark the Block as dead
                            getBlock(tmpBlock).setDead(true);

                            //now add it to the official dead List
                            deadBlocks.add(getBlock(tmpBlock));
                        }
                    }
                }
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
    
    /**
     * Remove any dead Block(s) in our List
     */
    private void removeDead()
    {
        for (Block block : deadBlocks)
        {
            removeBlock(block.getCol(), block.getRow());
        }
        
        //clear List
        deadBlocks.clear();
    }
    
    private void spawnVirus() throws Exception
    {
        //pick a random index
        int index = 0;
        
        if (locations.size() > 1)
            index = random.nextInt(locations.size() - 1);
        
        //get the location using the index
        Cell tmp = locations.get(index);

        //create new virus
        Virus virus = new Virus(random);

        //set the correct Column, Row
        virus.setCol(tmp);
        virus.setRow(tmp);

        //update the virus to be at the given location
        virus.setPosition(getX(), getY());

        //set the virus on the board
        setBlock(tmp.getCol(), tmp.getRow(), virus);

        //remove location from list so we don't pick it again
        locations.remove(index);

        //increase the progress
        countProgress++;
    }
    
    public List<Block> getMatches()
    {
        //list of matching blocks
        ArrayList<Block> deadBlocksTmp = new ArrayList<>();
        
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
                    checkConsecutiveMatch(col, cols, row, tmpType, true, deadBlocksTmp);
                }
                
                //make sure we aren't soo close that there is no way we will make a match
                if (row <= rows - MATCH_MINIMUM)
                {
                    //start at the current position and head south checking for all matching
                    checkConsecutiveMatch(row, rows, col, tmpType, false, deadBlocksTmp);
                }
            }
        }
        
        return deadBlocksTmp;
    }
    
    /**
     * Here we will check a number of Block(s) from a start to finish location 
     * going in a certain direction checking for a match of Block.Type 
     * 
     * @param start The start row or column
     * @param finish The end row or column
     * @param staticDimension Either the row or column that will not change while testing
     * @param tmpType The type of Block we are looking for a match.
     * @param horizontal If true we will test going east. If false we will test going south.
     */
    private void checkConsecutiveMatch(final int start, final int finish, final int staticDimension, final Type tmpType, final boolean horizontal, final ArrayList<Block> deadBlocksTmp)
    {
        //the number of matching blocks
        int matchCount = 0;
        
        //temporary block object
        Block tmpBlock;
        
        boolean hasPill = false;
        
        //from the next position check the rest for match going south
        for (int current=start; current < finish; current++)
        {
            //if moving horizontal
            if (horizontal)
            {
                tmpBlock = getBlock(current, staticDimension);
            }
            else
            {
                tmpBlock = getBlock(staticDimension, current);
            }

            boolean match = (tmpBlock != null && tmpBlock.hasMatch(tmpType));

            //if there is a match increase the count
            if (match)
            {
                //see if at least one of the matching Block(s) is a Pill
                if (Pill.isPill(tmpBlock))
                    hasPill = true;
                
                matchCount++;
            }

            //if the block does not exist or the type does not match or the last dimension
            if (tmpBlock == null || !match || current == finish - 1)
            {
                //make sure we made the minimum requirements and at least 1 of the matching Block(s) is a Pill
                if (matchCount >= MATCH_MINIMUM && hasPill)
                {
                    //the last dimension that is part of the match
                    final int matchFinish; 
                    
                    //if we have a match or at the finish line
                    if (current == finish - 1 && match)
                    {
                        matchFinish = current;
                    }
                    else
                    {
                        matchFinish = current - 1;
                    }
                    
                    for (int matchCurrent = start; matchCurrent <= matchFinish; matchCurrent++)
                    {
                        //if we are heading east the column will be the variable
                        if (horizontal)
                        {
                            deadBlocksTmp.add(getBlock(matchCurrent, staticDimension));
                        }
                        else
                        {
                            //we are heading south and the row will be the variable
                            deadBlocksTmp.add(getBlock(staticDimension, matchCurrent));
                        }
                    }
                    
                }

                //exit the loop and check next
                break;
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
    
    public Block getBlock(final Block block)
    {
        return getBlock(block.getCol(), block.getRow());
    }
    
    public Block getBlock(final Cell cell)
    {
        return getBlock(cell.getCol(), cell.getRow());
    }
    
    /**
     * Count the total number of viruses on the board.
     * @return int The total number of red, blue, and yellow viruses
     */
    public int getVirusCount()
    {
        int count = 0;
        
        for (Type type : Type.values())
        {
            if (Virus.isVirus(type))
            {
                count += getCount(type);
            }
        }
        
        return count;
    }
    
    /**
     * Get the count for a specific Type of Block
     * @param type The type of Block we want a count for
     * @return int The total number of Block type found in the board
     */
    public int getCount(final Type type)
    {
        //count of Block(s) of a specific Type
        int typeCount = 0;
        
        for (int col=0; col < getCols(); col++)
        {
            for (int row=0; row < getRows(); row++)
            {
                if (getBlock(col, row) != null && getBlock(col, row).getType() == type)
                    typeCount++;
            }
        }
        
        return typeCount;
    }
    
    public void removeBlock(final Pill pill)
    {
        removeBlock(pill.getCol(), pill.getRow());
        removeBlock(pill.getExtra().getCol(), pill.getExtra().getRow());
    }
    
    public void removeBlock(final int col, final int row)
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
        return (hasCollision((Block)pill) || hasCollision((Block)pill.getExtra()));
    }
    
    /**
     * Check if the given Block collides with any of the Blocks on our board.
     * Will return true if an existing block is at the same spot or if the 
     * parameter block is out of bounds.
     * 
     * @param block The Block we want to check for collision
     * @return boolean Return true if the Block euqals any of the existing Block(s) on the board or if out of bounds
     */
    private boolean hasCollision(final Block block)
    {
        //there will be a collision if the user goes out of bounds
        if (block.getCol() < 0 || block.getCol() > cols - 1)
            return true;
        if (block.getRow() < 0 || block.getRow() > rows - 1)
            return true;
        
        //if an object exists then there is a collision
        if (getBlock(block) != null)
            return true;
        
        return false;
    }
    
    /**
     * Add the Pill to the board
     * @param pill The pill we want to add
     */
    public void addPill(final Pill pill) throws Exception
    {
        //correct the x,y coordinates
        pill.setPosition(getX(), getY());
        
        //add the Block(s) to the board
        setBlock(pill.getCol(), pill.getRow(), new Block(pill));
        setBlock(pill.getExtra().getCol(), pill.getExtra().getRow(), new Block(pill.getExtra()));
    }
    
    @Override
    public void render(Graphics graphics)
    {
        //draw outline of board for now
        graphics.setColor(Color.WHITE);
        graphics.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        for (int row=0; row < rows; row++)
        {
            for (int col=0; col < cols; col++)
            {
                //draw the blocks that exist
                if (getBlock(col, row) != null)
                    getBlock(col, row).render(graphics);
            }
        }
        
        //draw virus counts
        graphics.setFont(graphics.getFont().deriveFont(12f));
        graphics.setColor(Color.WHITE);
        
        int r = getCount(Type.RedVirus);
        int b = getCount(Type.BlueVirus);
        int y = getCount(Type.YellowVirus);
        
        
        graphics.drawString("R = " + r, (int)getX() - 45, (int)getY());
        graphics.drawString("B = " + b, (int)getX() - 45, (int)getY() + 15);
        graphics.drawString("Y = " + y, (int)getX() - 45, (int)getY() + 30);
        
        graphics.drawString("All = " + (r+b+y), (int)getX() - 45, (int)getY() + 60);
    }
}