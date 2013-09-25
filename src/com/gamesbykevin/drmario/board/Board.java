package com.gamesbykevin.drmario.board;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.drmario.block.*;
import com.gamesbykevin.drmario.block.Block.*;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.resource.Resources.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The board where the viruses and pills will be contained
 * @author GOD
 */
public class Board extends Sprite
{
    //this List will contain all of the blocks on the board
    private Block[][] blocks;
    
    //if any blocks need to be marked as dead they will be contained in here
    private List<Block> deadBlocks;
    
    //minimum amount of pieces needed for a match
    public static final int MATCH_MINIMUM = 4;
    
    //the row to start the virus spawn to give the player some room
    private static final int SPAWN_START_ROW = 6;
    
    //the virus count, and progress count
    private int virusCount;
    
    //where are we compared to the actual total
    private int countProgress = 0;
    
    //the spawn locations for the viruses
    private List<Cell> locations;
    
    //once the dead pieces are removed we need to drop any existing pills accordingly
    private boolean drop = false;
    
    //are we done spawning viruses
    private boolean spawnComplete = false;
    
    //our timers that will track death duration, and drop pill durtation
    private TimerCollection timers;
    
    //the time to show the dead blocks animation
    private static final long DEATH_TIME = TimerCollection.toNanoSeconds(500L);
    
    //the time to wait between dropping the Block(s)
    private static final long DROP_TIME = TimerCollection.toNanoSeconds(125L);
    
    private enum Key
    {
        Death, Drop
    }
    
    //the dimensions for the board
    private static final int COLUMNS = 8;
    private static final int ROWS = 16;
    
    //our random number generator object
    private final Random random;
    
    //the blocks that we will add as the penalty
    private List<Block> penaltyBlocks;
    
    //when we check for a match where we previously dropping so we can determine chain(s)
    private boolean previousDrop = false;
    
    //we will track score here for the current level
    private int score = 0;
    
    //the score to add for a pill kill regardless of a chain or not
    private static final int SCORE_PILL_KILL = 10;
    
    //the score to add for a virus kill
    private static final int SCORE_VIRUS_KILL = 100;
    
    //the score to add for a chained virus kill
    private static final int SCORE_VIRUS_KILL_CHAIN = 200;
    
    /**
     * Create a new empty board of the specified columns and rows and to be rendered within the screen.
     * 
     * @param container The container the board resides in
     * @param cols The total number of columns
     * @param rows The total number of rows
     * @param count The total number of viruses
     */
    public Board(final int virusCount, final long seed)
    {
        //create our random number generator object
        this.random = new Random(seed);
        
        //set the virus count
        this.virusCount = virusCount;
        
        //the blocks on the board
        blocks = new Block[ROWS][COLUMNS];
        
        //create a new list for the spawn locations
        locations = new ArrayList<>();
        
        //add all spawn locations so we can choose at random
        for (int row = SPAWN_START_ROW; row < ROWS; row++)
        {
            for (int col=0; col < COLUMNS; col++)
            {
                locations.add(new Cell(col, row));
            }
        }
        
        //create an empty List that will contain our dead Block(s)
        this.deadBlocks = new ArrayList<>();
        
        //a list of blocks being added to the board as a penalty
        this.penaltyBlocks = new ArrayList<>();
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
    
        if (timers != null)
            timers.dispose();
        
        timers = null;
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public void addScore(final int bonus)
    {
        this.score += bonus;
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
    public boolean hasDead()
    {
        return (!deadBlocks.isEmpty());
    }
    
    /**
     * Count the number of dead viruses
     * @return int
     */
    public int getDeadVirusCount()
    {
        int count = 0;
        
        for (Block block : deadBlocks)
        {
            if (Virus.isVirus(block))
                count++;
        }
        
        return count;
    }
    
    /**
     * Are we in the middle of dropping any single existing blocks
     * @return 
     */
    public boolean hasDrop()
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
    public void update(final Engine engine) throws Exception
    {
        if (this.timers == null)
        {
            this.timers = new TimerCollection(engine.getMain().getTime());
            this.timers.add(Key.Death, DEATH_TIME);
            this.timers.add(Key.Drop, DROP_TIME);
        }
        
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
                timers.update();
                
                //if the time has passed
                if (timers.hasTimePassed(Key.Death))
                {
                    //reset timer
                    timers.reset();
                    
                    //remove any existing dead pieces
                    removeDead();
                    
                    //now that the dead pieces have been removed we need to drop the extra Block(s)
                    this.drop = true;
                }
            }
            else
            {
                if (hasDrop())
                {
                    //we are dropping so set flag
                    previousDrop = true;
                    
                    //update timer
                    timers.update();
                    
                    checkDrop();
                }
                else
                {
                    //create a List of Block(s) that match
                    List<Block> deadBlocksTmp = getMatches();

                    //were any dead Block(s) found
                    if (!deadBlocksTmp.isEmpty())
                    {
                        //reset timers again
                        this.timers.reset();

                        //the number of viruses destroyed
                        int countV = 0;
                        
                        //the number of pills destroyed
                        int countP  = 0;
                        
                        //mark all found as dead
                        for (Block tmpBlock : deadBlocksTmp)
                        {
                            if (Virus.isVirus(tmpBlock))
                            {
                                //count the total number of viruses
                                countV++;
                            }
                            else
                            {
                                //count the total number of pills
                                countP++;
                            }
                            
                            //mark the Block as dead
                            getBlock(tmpBlock).setDead(true);

                            //now add it to the official dead List
                            deadBlocks.add(getBlock(tmpBlock));
                        }
                        
                        //add pill score
                        addScore((countP * SCORE_PILL_KILL));
                        
                        //if we were previously dropping this is part of a chain
                        if (previousDrop)
                        {
                            //add chained virus kill score 
                            addScore((countV * SCORE_VIRUS_KILL_CHAIN));
                            
                            //play chain sound effect
                            engine.getResources().playGameAudio(GameAudio.Chain, false);
                        }
                        else
                        {
                            //add virus kill score
                            addScore((countV * SCORE_VIRUS_KILL));
                            
                            //play match sound effect
                            engine.getResources().playGameAudio(GameAudio.Match, false);
                        }
                    }
                    else
                    {
                        //no match was found so set flag back to false
                        previousDrop = false;
                    }
                }
            }
        }
        
        //update animations
        for (Block[] row : blocks)
        {
            for (Block block : row)
            {
                if (block != null && block.getSpriteSheet() != null)
                {
                    if (!block.getSpriteSheet().hasDelay())
                        block.getSpriteSheet().setDelay(engine.getMain().getTime());
                    
                    block.getSpriteSheet().update();
                }
            }
        }
    }
    
    /**
     * If there are penalty blocks add them to the board and return true, otherwise return false
     * @return boolean
     */
    public boolean applyPenalty()
    {
        //if there are penalty blocks add them
        if (!penaltyBlocks.isEmpty())
        {
            //add the penalty blocks to the board
            for (Block block : penaltyBlocks)
            {
                //make sure block is in the first row
                block.setRow(0);

                //set the correct x, y position
                block.setPosition(getX(), getY());

                //add block to board
                setBlock(block.getCol(), block.getRow(), new Block(block));
            }

            //clear list
            penaltyBlocks.clear();

            //drop will remain true to drop new pieces
            drop = true;
            
            //penalty has been applied return true;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * When we penalize we add pills to columns that contain viruses.<br><br>
     * Not every virus will be covered
     */
    public void penalize()
    {
        //we don't want to stack up too many blocks too fast
        if (!penaltyBlocks.isEmpty())
            return;
        
        //the number of penalty blocks we will be adding
        final int limit = (getCols() / 2);
        
        List<Integer> columns = new ArrayList<>();
        
        for (int i=0; i < getCols(); i++)
        {
            columns.add(i);
        }
        
        //the total number of penalty blocks will be half the total number of columns
        while(penaltyBlocks.size() < limit && !columns.isEmpty())
        {
            //random index
            final int index = random.nextInt(columns.size());
            
            //get column
            final int col = columns.get(index);
            
            //penalty block
            Block tmp = new Block();

            //set the column to the random one chosen
            tmp.setCol(col);
            
            //get the first virus found in the specific column
            final Block block = getBlockBelow(col);
            
            //if the block exists then pick a non-matching color
            if (block != null)
            {
                if (block.hasMatch(Type.RedPill))
                    tmp.setType((random.nextInt(2) == 1) ? Type.BluePill : Type.YellowPill);
                if (block.hasMatch(Type.BluePill))
                    tmp.setType((random.nextInt(2) == 1) ? Type.YellowPill : Type.RedPill);
                if (block.hasMatch(Type.YellowPill))
                    tmp.setType((random.nextInt(2) == 1) ? Type.BluePill : Type.RedPill);
            }
            else
            {
                //set random type
                final int result = random.nextInt(3);
                
                switch(result)
                {
                    case 0:
                        tmp.setType(Type.RedPill);
                        break;
                        
                    case 1:
                        tmp.setType(Type.BluePill);
                        break;
                        
                    case 2:
                        tmp.setType(Type.YellowPill);
                        break;
                }
            }
            
            //remove selection from list so we don't choose it again
            columns.remove(index);
            
            //add block to penalty list
            this.penaltyBlocks.add(tmp);
        }
    }
    
    /**
     * Start at the first row 0 and move south until we find a Block
     * If no Block is found null is returned.
     * 
     * @param col The column
     * @param board The board to check
     * @return Block if there is no Block below null will be returned
     */
    public Block getBlockBelow(final int col)
    {
        //start at the top and continue moving south
        for (int row = 0; row < getRows(); row++)
        {
            //if the Block exists return it
            if (getBlock(col, row) != null)
                return getBlock(col, row);
        }
        
        //no Block was found so return null
        return null;
    }
    
    /**
     * Apply gravity to any separate hanging Block(s)
     */
    private void checkDrop()
    {
        //not enough time has passed yet for the next drop
        if (!timers.hasTimePassed(Key.Drop))
            return;
        
        //time has passed to make the next drop
        timers.reset();

        //are we finished dropping Block(s)
        boolean finish = true;

        //start on the second to last row and go backwards
        for (int row = getRows() - 2; row >= 0; row--)
        {
            for (int col=0; col < getCols(); col++)
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
    
    /**
     * Check if the Block Type is found in the List of dead blocks.<br><br>
     * If the List of dead blocks is empty then no matches have been found and false is returned.
     * @param type The type of Block we are looking for
     * @return boolean True if found.
     */
    public boolean hasDeadType(final Type type)
    {
        if (deadBlocks.isEmpty())
            return false;
        
        for (Block block : deadBlocks)
        {
            if (block.getType() == type)
                return true;
        }
        
        return false;
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
        Virus virus = new Virus();
        
        //set random type
        virus.setRandom(random);
        
        //setup animations
        virus.setup();
        
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
        
        for (int row=0; row < getRows(); row++)
        {
            for (int col=0; col < getCols(); col++)
            {
                Block block = getBlock(col, row);

                //block not found, or if block is dead we don't need to check it
                if (block == null || block.isDead())
                    continue;

                //get the block type
                final Type tmpType = block.getType();

                //make sure we aren't soo close that there is no way we will make a match
                if (col <= getCols() - MATCH_MINIMUM)
                {
                    //start at the current position and head east checking for all matching
                    checkConsecutiveMatch(col, getCols(), row, tmpType, true, deadBlocksTmp);
                }
                
                //make sure we aren't soo close that there is no way we will make a match
                if (row <= getRows() - MATCH_MINIMUM)
                {
                    //start at the current position and head south checking for all matching
                    checkConsecutiveMatch(row, getRows(), col, tmpType, false, deadBlocksTmp);
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
        return this.COLUMNS;
    }
    
    /**
     * Get the total number of Rows on the board
     * @return int
     */
    public int getRows()
    {
        return this.ROWS;
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
     * @return boolean Return true if the Block equals any of the existing Block(s) on the board or if out of bounds
     */
    private boolean hasCollision(final Block block)
    {
        //there will be a collision if the user goes out of bounds
        if (block.getCol() < 0 || block.getCol() > getCols() - 1)
            return true;
        if (block.getRow() < 0 || block.getRow() > getRows() - 1)
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
}