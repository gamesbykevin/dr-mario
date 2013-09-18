package com.gamesbykevin.drmario.player;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.shared.IElement;

import com.gamesbykevin.drmario.block.Block;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.block.Virus;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;

import java.util.List;

/**
 * The AI Agent we are competing against
 * @author GOD
 */
public final class Agent extends Player implements IElement
{
    //score to add for every virus kill, highest priority
    private static final int SCORE_VIRUS_KILL = 300;
    
    //score to add for every pill kill
    private static final int SCORE_PILL_KILL = 10;
    
    //score to add when adding pill on top of matching virus
    private static final int SCORE_VIRUS_MATCH = 140;
    
    //score to add when adding pill on top of matching pill
    private static final int SCORE_BLOCK_MATCH = 10;
    
    //score to deduct when placing pill on top of non-matching virus
    private static final int SCORE_VIRUS_NO_MATCH = -50;
    
    //score to deduct when placing pill on top of non-matching pill
    private static final int SCORE_BLOCK_NO_MATCH = -10;
    
    //when both Block(s) in the Pill are in the same column and match
    private static final int SCORE_SAME_COLUMN_MATCH = 5;
    
    //when both Block(s) in the Pill are in the same column and they don't match
    private static final int SCORE_SAME_COLUMN_NO_MATCH = -15;
    
    //if the Block(s) don't match also penalize for the height because we don't want to stack non matching Block(s) high
    private static final int SCORE_NO_MATCH_HEIGHT_PENALTY = -10;
    
    //if placing a non matching block and too high that we can't match it to eliminate it
    private static final int SCORE_NO_MATCH_HEIGHT_SEVERE_PENALTY = -75;
    
    //if the block matches and in the same column there is a virus, we need this so the agent will work towards mining down to get to the virus
    private static final int SCORE_BLOCK_MATCH_VIRUS_COLUMN = 25;
    
    //if the block does not match and same has a virus, we need this so the agent will avoid these locations
    private static final int SCORE_BLOCK_NO_MATCH_VIRUS_COLUMN = -25;
    
    //the Timer that determines when the Agent can move
    private Timer movementTimer;
    
    public Agent(final long dropDelay, final long moveDelay) throws Exception
    {
        super(dropDelay);
        
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
                            //if (engine.getManager().getBoard().hasCollision(getPill()))
                            //    getPill().decreaseCol();
                        }

                        //we are east of our goal so move west
                        if (getPill().getCol() > getGoal().getCol())
                        {
                            //move 1 column to the west
                            getPill().decreaseCol();
                            
                            //if there was a collision move our Pill back
                            //if (engine.getManager().getBoard().hasCollision(getPill()))
                            //    getPill().increaseCol();
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
                
                //there was no block found below, so the pill will be placed at the bottom
                if (block == null)
                {
                    //set the Pill at the bottom for the current column
                    getPill().setCol(col);
                    getPill().setRow(getBoard().getRows() - 1);
                }
                else
                {
                    //set the Pill at the current location
                    getPill().setCol(block.getCol());
                    getPill().setRow(block.getRow());

                    //move up 1 row from Block to see if this position is available
                    getPill().decreaseRow();
                }
                
                //move up 1 more row if facing south
                if (rotation == Rotation.South)
                    getPill().decreaseRow();
                
                //if there's a collision this is an invalid location
                if (getBoard().hasCollision(getPill()))
                    continue;
                
                //if there is an existing Block at or above the row we are trying to place, this is an invalid location
                if (block != null && block.getRow() <= getPill().getRow())
                    continue;
                
                //get the Block in the same column as the extra Pill so we can tell if the position is valid
                final Block extraBlock = getBlockBelow(getPill().getExtra().getCol());
                
                //if there is an existing Block at or above the row we are trying to place, this is an invalid location
                if (extraBlock != null && extraBlock.getRow() <= getPill().getExtra().getRow())
                    continue;
                
                //calculate the score for this position
                final int tmpScore = calculateScore(block, extraBlock);
                
                //if the current score matches or beats the saved score set the new goal
                if (tmpScore >= score)
                {
                    //make sure the goal is never the start location
                    if (!getPill().getCell().equals(START) && !getPill().getExtra().getCell().equals(START))
                    {
                        //set the location goal and rotation
                        super.setGoals(getPill().getCell(), getPill().getRotation());

                        //set this score as the one to beat
                        score = tmpScore;
                    }
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

        //if there are dead Block(s) count how many to determine the score added
        if (deadBlocks.size() > 0)
        {
            //number of viruses destroyed
            int countVirus = 0;
            int countPill = 0;

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
        
        //if the columns are the same score accordingly
        if (getPill().getCol() == getPill().getExtra().getCol())
        {
            //if both pills are in the same column add a bonus if they match
            if (getPill().hasMatch(getPill().getExtra().getType()))
            {
                tmpScore += SCORE_SAME_COLUMN_MATCH;
            }
            else
            {
                //penalize otherwise
                tmpScore += SCORE_SAME_COLUMN_NO_MATCH;
            }
        }
        
        //does the same column as the Pill contain a virus
        boolean hasVirus = hasVirus(getPill().getCol());
        
        //calculate the score for the Pill Block
        tmpScore += scoreBlock(getPill(), block, getBoard().getRows(), hasVirus);

        //does the same column as the Pill extra contain a virus
        hasVirus = hasVirus(getPill().getExtra().getCol());
        
        //calculate the score for the Pill extra Block
        tmpScore += scoreBlock(getPill().getExtra(), extraBlock, getBoard().getRows(), hasVirus);
            
        //remove the pill we placed on the getBoard()
        getBoard().removeBlock(getPill());
        
        //return the score calculated
        return tmpScore;
    }
    
    /**
     * Get the score for the specific Pill block and the specified block below
     * @param blockPill The Block we are checking
     * @param blockBelow The Block we are placing our blockPill on top of
     * @param totalRows the Number of rows so we can penalize for height if Block(s) no match
     * @param hasVirus does the same column as blockPill contain a Virus
     * @return int the score sum
     */
    private int scoreBlock(final Block blockPill, final Block blockBelow, final int totalRows, final boolean hasVirus)
    {
        int tmpScore = 0;
        
        if (blockBelow != null)
        {
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
                    
                    //if Block matches and the column contains a virus, this is good becuase we are trying to get to the virus
                    if (hasVirus)
                    {
                        tmpScore += SCORE_BLOCK_MATCH_VIRUS_COLUMN;
                    }
                }
            }
            else
            {
                //the Block did not match
                
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

                //penalize for the height, because we don't want to stack non-matching Block(s) up high
                tmpScore += ((totalRows - blockPill.getRow()) * SCORE_NO_MATCH_HEIGHT_PENALTY);

                //also if we are very close to the top add severe penalty
                if (blockPill.getRow() <= Board.MATCH_MINIMUM)
                {
                    //use extra penalty score becuase we don't want to prevent destorying a pill/virus
                    tmpScore += ((totalRows - blockPill.getRow()) * SCORE_NO_MATCH_HEIGHT_SEVERE_PENALTY);
                }
            }
        }
        
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