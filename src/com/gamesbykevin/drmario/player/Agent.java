package com.gamesbykevin.drmario.player;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.block.Block;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.block.Virus;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.shared.IElement;

import java.awt.Rectangle;
import java.util.List;

/**
 * The AI Agent we are competing against
 * @author GOD
 */
public final class Agent extends Player implements IElement
{
    //score to add for every virus kill, highest priority
    private static final int SCORE_VIRUS_KILL = 150;
    
    //score to add for every pill kill
    private static final int SCORE_PILL_KILL = 25;
    
    //score to add when adding pill on top of matching virus
    private static final int SCORE_VIRUS_MATCH = 50;
    
    //score to add when adding pill on top of matching pill
    private static final int SCORE_BLOCK_MATCH = 25;
    
    //score to deduct when placing pill on top of non-matching virus
    private static final int SCORE_VIRUS_NO_MATCH = -50;
    
    //score to deduct when placing pill on top of non-matching pill
    private static final int SCORE_BLOCK_NO_MATCH = -25;
    
    //if the block matches and in the same column there is a virus, we need this so the agent will work towards mining down to get to the virus
    private static final int SCORE_BLOCK_MATCH_VIRUS_COLUMN = 45;
    
    //if the block does not match and same has a virus, we need this so the agent will avoid these locations
    private static final int SCORE_BLOCK_NO_MATCH_VIRUS_COLUMN = -25;
    
    //the penalty for each row
    private static final int SCORE_HEIGHT = -5;
    
    //we are very close to the top and the block(s) don't match
    private static final int SCORE_DANGER_ZONE_NO_MATCH = -350;
    
    //the Timer that determines when the Agent can move
    private Timer movementTimer;
    
    public Agent(final Rectangle container, final long dropDelay, final long moveDelay) throws Exception
    {
        super(container, dropDelay);
        
        //create a new Timer that calculates the delay between each move
        this.movementTimer = new Timer(TimerCollection.toNanoSeconds(moveDelay));
        
        if (dropDelay <= moveDelay)
            throw new Exception("The drop delay has to be greater than the move delay.");
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if the player has lost or won no more updates are required
        if (hasLose() || hasWin())
            return;
        
        //check for matches on board etc...
        getBoard().update(engine);
        
        //if we can't interact with the board due to a virus/pill match or pill drop etc..
        if (!getBoard().canInteract())
            return;
        
        super.update(engine);
        
        //if the goal is not set we need to find one
        if (getGoal() == null)
        {
            //make sure the Pill exists so we can calculate the best position
            if (getPill() != null)
            {
                //store the original location
                final int pillCol = getPill().getCol();
                final int pillRow = getPill().getRow();
                
                //find the best place for the current Pill
                locateGoal();
                
                //reset the rotation
                getPill().reset();

                //set the pill back to the original location
                getPill().setCol(pillCol);
                getPill().setRow(pillRow);
            }
        }
        else
        {
            //update our timer
            movementTimer.update(engine.getMain().getTime());
            
            //the goal is set, now we need to move the Pill to the correct Location
            if (movementTimer.hasTimePassed())
            {
                //restart the timer
                movementTimer.reset();
                
                //if we aren't at the final rotation yet
                if (getPill().getRotation() != getRotation())
                {
                    //rotate Pill
                    getPill().rotate();
                }
                else
                {
                    //we are at the correct location so now we need to drop
                    if (getPill().getCol() == getGoal().getCol())
                    {
                        //drop Pill to next row
                        super.applyGravity();
                    }
                    else
                    {
                        //we are west of our goal so move east
                        if (getPill().getCol() < getGoal().getCol())
                        {
                            //move 1 column to the east
                            getPill().increaseCol();
                            
                            //if there was a collision move our Pill back
                            if (getBoard().hasCollision(getPill()) && getPill().getRow() > 0)
                                getPill().decreaseCol();
                        }

                        //we are east of our goal so move west
                        if (getPill().getCol() > getGoal().getCol())
                        {
                            //move 1 column to the west
                            getPill().decreaseCol();
                            
                            //if there was a collision move our Pill back
                            if (getBoard().hasCollision(getPill()) && getPill().getRow() > 0)
                                getPill().increaseCol();
                        }
                    }
                }
            }
        }
        
        //set the correct x,y Location for the current Pill
        updateLocation();
    }
    
    private void locateGoal() throws Exception
    {
        //start score
        int score = -100000;
        
        //check every column
        for (int col=0; col < getBoard().getCols(); col++)
        {
            //get the first block found in the current column so we know where to place the Pill
            final Block block = getBlockBelow(col);
            
            //check every rotation at the current position
            for (Rotation rotation : Rotation.values())
            {
                //set the current rotation
                getPill().setRotation(rotation);
            
                //set the appropriate column now that the rotation has taken place
                getPill().setCol(col);
                
                //we also need to get the block below the Pill Extra
                final Block extraBlock = getBlockBelow(getPill().getExtra().getCol());
                
                //we need to find the appropriate place height
                if (block != null && extraBlock != null)
                {
                    if (block.getRow() < extraBlock.getRow())
                    {
                        getPill().setRow(block.getRow());
                    }
                    else
                    {
                        getPill().setRow(extraBlock.getRow());
                    }
                    
                    getPill().decreaseRow();
                }
                else
                {
                    if (block == null && extraBlock == null)
                    {
                        //there were no blocks below so place the piece on the floor
                        getPill().setRow(getBoard().getRows() - 1);
                    }
                    else
                    {
                        if (block != null)
                            getPill().setRow(block.getRow());
                        
                        if (extraBlock != null)
                            getPill().setRow(extraBlock.getRow());
                        
                        getPill().decreaseRow();
                    }
                }
                
                //move up 1 more row if facing south
                if (rotation == Rotation.South)
                    getPill().decreaseRow();
                
                //if there's a collision this is an invalid location
                if (getBoard().hasCollision(getPill()))
                    continue;
                
                //calculate the score for this position
                final int tmpScore = calculateScore(block, extraBlock);
                
                //if the current score matches or beats the saved score set the new goal
                if (tmpScore > score)
                {
                    //set the location goal and rotation
                    super.setGoals(getPill().getCell(), getPill().getRotation());

                    //set this score as the one to beat
                    score = tmpScore;
                }
            }
        }
    }
    
    private int calculateScore(final Block block, final Block extraBlock) throws Exception
    {
        //current score we are calculating
        int tmpScore = 0;

        //place the Pill on the board so we can check for matches etc...
        getBoard().addPill(getPill());

        //get the list of blocks that matched so we can count the number of viruses destroyed
        List<Block> deadBlocks = getBoard().getMatches();

        //number of viruses destroyed
        int countVirus = 0;
        
        //number of pills destroyed
        int countPill = 0;
        
        //if there are dead Block(s) count how many to determine the score added
        if (deadBlocks.size() > 0)
        {
            for (Block tmp : deadBlocks)
            {
                //increase the count(s) accordingly
                if (Virus.isVirus(tmp.getType()))
                {
                    countVirus++;
                }
                else
                {
                    countPill++;
                }
            }

            //add up our score
            tmpScore += (countVirus * SCORE_VIRUS_KILL);
            tmpScore += (countPill  * SCORE_PILL_KILL);
        }
        
        //calculate the score for the Pill Block
        tmpScore += scoreBlock(getPill(), block);
        
        //calculate the score for the Pill extra Block
        tmpScore += scoreBlock(getPill().getExtra(), extraBlock);
        
        //remove the pill we placed on the getBoard()
        getBoard().removeBlock(getPill());
        
        //return the score calculated
        return tmpScore;
    }
    
    /**
     * Get the score for the specific Pill block and the specified block below
     * @param blockPill The Block we are checking
     * @param blockBelow The Block we are placing our blockPill on top of
     * @return int the score sum
     */
    private int scoreBlock(final Block blockPill, final Block blockBelow)
    {
        int tmpScore = 0;
        
        if (blockBelow == null)
            return tmpScore;
        
        //does the same column as the Pill contain a virus
        final boolean hasVirus = hasVirus(blockPill.getCol());

        //does the extra Block match the block below
        if (blockPill.hasMatch(blockBelow.getType()))
        {
            //more points if Block is a virus because we want to destroy the virus
            if (Virus.isVirus(blockBelow))
            {
                tmpScore += SCORE_VIRUS_MATCH;
            }
            else
            {
                //but still some points if the Block matches
                tmpScore += SCORE_BLOCK_MATCH;
            }

            //if Block matches and the column contains a virus, this is good because we are trying to get to the virus
            if (hasVirus)
            {
                tmpScore += SCORE_BLOCK_MATCH_VIRUS_COLUMN;
            }
            
            //block isn't directly below so a gap is created
            if (blockPill.getRow() + 1 != blockBelow.getRow())
            {
                //are we also in the danger zone
                if (blockPill.getRow() < Board.MATCH_MINIMUM)
                {
                    //add additional height penalty
                    tmpScore += ((getBoard().getRows() - blockPill.getRow()) * SCORE_HEIGHT);
                }
            }
        }
        else
        {
            //the non-matching block is a virus
            if (Virus.isVirus(blockBelow))
            {
                //no match so add penalty
                tmpScore += SCORE_VIRUS_NO_MATCH;
            }
            else
            {
                //no match so add penalty
                tmpScore += SCORE_BLOCK_NO_MATCH;
            }

            //if we aren't matching and column has a virus we penalize even more
            if (hasVirus)
            {
                tmpScore += SCORE_BLOCK_NO_MATCH_VIRUS_COLUMN;
            }
            
            //are we in the danger zone
            if (blockPill.getRow() < Board.MATCH_MINIMUM)
            {
                //add additional penalty
                tmpScore += SCORE_DANGER_ZONE_NO_MATCH;
            }
            
            //add extra penalty for height since the Block(s) don't match
            tmpScore += ((getBoard().getRows() - blockPill.getRow()) * SCORE_HEIGHT);
        }
        
        //also penaliza depending on the height based on height
        tmpScore += ((getBoard().getRows() - blockPill.getRow()) * SCORE_HEIGHT);
        
        return tmpScore;
    }
    
    /**
     * Start at the first row 0 and move south until we find a Block
     * If no Block is found null is returned.
     * 
     * @param col The column
     * @param board The board to check
     * @return Block if there is no Block below null will be returned
     */
    private Block getBlockBelow(final int col)
    {
        //start at the top and continue moving south
        for (int row = 0; row < getBoard().getRows(); row++)
        {
            //if the Block exists return it
            if (getBoard().getBlock(col, row) != null)
                return getBoard().getBlock(col, row);
        }
        
        //no Block was found so return null
        return null;
    }
    
    /**
     * Check the specified column to determine if a virus exists.
     * @param col The column we want to start at.
     * @param board The board where the viruses are located so we can check if they exist.
     * @return True if a virus is located in the column, false otherwise
     */
    private boolean hasVirus(final int col)
    {
        //start at the top and continue moving south
        for (int row = 0; row < getBoard().getRows(); row++)
        {
            final Block block = getBoard().getBlock(col, row);
            
            //if the Block exists and is a virus, then there is a virus below
            if (block != null && Virus.isVirus(block))
                return true;
        }
        
        //no virus was found so return false
        return false;
    }
}