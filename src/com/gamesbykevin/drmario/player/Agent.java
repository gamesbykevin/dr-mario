package com.gamesbykevin.drmario.player;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

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
public final class Agent extends Player implements IPlayer
{
    //score to add for every virus kill, highest priority
    private static final int SCORE_VIRUS_KILL = 5000;
    
    //score to add for every pill kill
    private static final int SCORE_PILL_KILL = 450;
    
    //score to add when adding pill on top of matching virus
    private static final int SCORE_VIRUS_MATCH = 2500;
    
    //score to add when adding pill on top of matching pill
    private static final int SCORE_BLOCK_MATCH = 250;
    
    //score to deduct when placing pill on top of non-matching virus
    private static final int SCORE_VIRUS_NO_MATCH = -1500;
    
    //score to deduct when placing pill on top of non-matching pill
    private static final int SCORE_BLOCK_NO_MATCH = -750;
    
    //score if no match and no Block below
    private static final int SCORE_NO_MATCH_EMPTY = 0;
    
    //score if placing block in an unreachable position
    private static final int SCORE_NO_POSITION = -2000;
    
    //when both Block(s) in the Pill are in the same column and match
    private static final int SCORE_SAME_COLUMN_MATCH = 500;
    
    //when both Block(s) in the Pill are in the same column and they don't match
    private static final int SCORE_SAME_COLUMN_NO_MATCH = -500;
    
    //if the Block(s) don't match also penalize for the height because we don't want to stack non matching Block(s) high
    private static final int SCORE_HEIGHT_PENALTY = -250;
    
    //if placing a non matching block and too high that we can't match it to eliminate it
    private static final int SCORE_HEIGHT_SEVERE_PENALTY = -750;
    
    //the Timer that determines when the Agent can move
    private Timer timer;
    
    public Agent(final long dropDelay, final long moveDelay) throws Exception
    {
        super(dropDelay);
        
        //create a new Timer that calculates the delay between each move
        this.timer = new Timer(TimerCollection.toNanoSeconds(moveDelay));
        
        if (dropDelay < moveDelay)
            throw new Exception("The drop delay has to be greater than the move delay.");
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if the player has lost no more updates required
        if (hasLose())
            return;
        
        super.update(engine);
        
        //if the goal is not set we need to find one
        if (getGoal() == null)
        {
            if (getPill() != null)
            {
                //store the original location
                final int pillCol = getPill().getCol();
                final int pillRow = getPill().getRow();
                
                //find the best place for the current Pill
                locateGoal(engine.getManager().getBoard());

                if (getGoal() != null)
                    System.out.println("col=" + getGoal().getCol() + ", row=" + getGoal().getRow() + " " + this.getRotation().toString());
                
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
            timer.update(engine.getMain().getTime());
            
            //the goal is set, now we need to move the Pill to the correct Location
            if (timer.hasTimePassed())
            {
                //restart the timer
                timer.reset();
                
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
                        //drop to next row
                        super.applyGravity(engine.getManager().getBoard());
                        
                        //reset timer that determines the drop because we are doing that now
                        super.getTimer().reset();
                    }
                    else
                    {
                        //we are west of our goal so move east
                        if (getPill().getCol() < getGoal().getCol())
                            getPill().increaseCol();

                        //we are east of our goal so move west
                        if (getPill().getCol() > getGoal().getCol())
                            getPill().decreaseCol();
                    }
                }
            }
        }
        
        if (getPill() != null)
        {
            //set the correct x,y coordinates for the pill
            getPill().setPosition(engine.getManager().getBoard().getX(), engine.getManager().getBoard().getY());
        }
    }
    
    private void locateGoal(final Board board)
    {
        /**
         * Priority in this order
         * 1.  Can we eliminate a virus
         * 2.  Can we match the color of a virus
         * 3.  Can we match the color of a block
         * 4.  
         * 5.  
         * 6.  
         * 7.  
         * 8.  
         * 9.  
         * 10. 
         */
        
        /*
         * High level view how we determine the best position for the Pill
         * 1. We want to eliminate viruses as the top priority (Note: If more viruses can be eliminated at once then the postion that eliminates the most will be the priority)
         * 2. If we can't eliminate viruses we at least want to match up the existing virus colors with the Pill
         * 3. If there aren't virus colors to match up check for any Pill colors that are blocking a virus and match those
         * 4. If there are any pill colors that match the current Pill match those
         * 5. If all else fails place the Pill at the lowest height possible
         */
        
        //start score
        int score = -1000000000;
        
        //check every column
        for (int col=0; col < board.getCols(); col++)
        {
            //get the first block found in the current column so we know where to place the Pill
            final Block block = getBlockBelow(col, board);
            
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
                    getPill().setRow(board.getRows() - 1);
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
                
                //if we have a collision this is not a valid location
                if (board.hasCollision(getPill()))
                    continue;
                
                //calculate the score for this position
                final int tmpScore = calculateScore(block, board);
                
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
    
    private int calculateScore(Block block, final Board board)
    {
        //current score we are calculating
        int tmpScore = 0;

        //place the Pill on the board so we can check for matches etc...
        board.addPill(getPill());

        //get the list of blocks that matched so we can count the number of viruses destroyed
        List<Block> deadBlocks = board.checkMatch();

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
        
        //calculate the score for the Pill Block
        tmpScore += scoreBlock(getPill(), block, board.getRows());
        
        //get the block below for the Pill extra Block
        block = getBlockBelow(getPill().getExtra().getCol(), board);

        //calculate the score for the Pill extra Block
        tmpScore += scoreBlock(getPill().getExtra(), block, board.getRows());
            
        //remove the pill we placed on the board
        board.removeBlock(getPill());
        
        //return the score calculated
        return tmpScore;
    }
    
    /**
     * Get the score for the specific Pill block and the specified block below
     * @param blockPill
     * @param blockBelow
     * @param totalRows the Number of rows so we can penalize for height if Block(s) no match
     * @return int the score sum
     */
    private int scoreBlock(final Block blockPill, final Block blockBelow, final int totalRows)
    {
        int tmpScore = 0;
        
        if (blockBelow != null)
        {
            //make sure the block below actually is below
            if (blockBelow.getRow() > blockPill.getRow())
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
                    }
                }
                else
                {
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
                    
                    //penalize for the height, because we don't want to stack non-matching Block(s) up high
                    tmpScore += ((totalRows - blockPill.getRow()) * SCORE_HEIGHT_PENALTY);
                    
                    //also if we are very close to the top add severe penalty
                    if (blockPill.getRow() <= Board.MATCH_MINIMUM)
                    {
                        //use extra penalty score becuase we don't want to prevent destorying a pill/virus
                        tmpScore += ((totalRows - blockPill.getRow()) * SCORE_HEIGHT_SEVERE_PENALTY);
                    }
                }
            }
            else
            {
                //there is a Block, but it is above the pill Block which is very bad because we won't even be able to place in this position
                tmpScore += SCORE_NO_POSITION;
            }
        }
        else
        {
            //if Block does not exist it is still ok to place
            tmpScore += SCORE_NO_MATCH_EMPTY;
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
    private Block getBlockBelow(final int col, final Board board)
    {
        //start accordingly and continue moving south
        for (int row = 0; row < board.getRows(); row++)
        {
            //if the Block exists return it
            if (board.getBlock(col, row) != null)
                return board.getBlock(col, row);
        }
        
        //no Block was found so return null
        return null;
    }
}