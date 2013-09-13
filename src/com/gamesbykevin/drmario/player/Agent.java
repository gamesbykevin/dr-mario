package com.gamesbykevin.drmario.player;

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
    public Agent(final long delay)
    {
        super(delay);
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
                calculateGoal(engine.getManager().getBoard());
            }
        }
        else
        {
            //the goal is set, now we need to move the Pill to the correct Location
            
            //if we aren't at the final rotation yet
            if (getPill().getRotation() != getRotation())
            {
                //rotate Pill
                getPill().rotate();
            }
            else
            {
                //we are at the correct rotation so now we need to be in the correct location
                
                //we are west of our goal so move east
                if (getPill().getCol() < getGoal().getCol())
                    getPill().increaseCol();
                
                //we are east of our goal so move west
                if (getPill().getCol() > getGoal().getCol())
                    getPill().decreaseCol();
            }
        }
        
        if (getPill() != null)
        {
            //set the correct x,y coordinates for the pill
            getPill().setPosition(engine.getManager().getBoard().getX(), engine.getManager().getBoard().getY());
        }
    }
    
    private void calculateGoal(final Board board)
    {
        /*
         * High level view how we determine the best position for the Pill
         * 1. We want to eliminate viruses as the top priority (Note: If more viruses can be eliminated at once then the postion that eliminates the most will be the priority)
         * 2. If we can't eliminate viruses we at least want to match up the existing virus colors with the Pill
         * 3. If there aren't virus colors to match up check for any Pill colors that are blocking a virus and match those
         * 4. If there are any pill colors that match the current Pill match those
         * 5. If all else fails place the Pill at the lowest height possible
         */
        
        /*
         * Scoring algorithm (alpha)
         * 1. For each virus eliminated add 200 points
         * 2. Match the Pill color and for each Pill color that can be matched add 25 points 
         * 3. -----------If eliminating a virus leaves a Pill behind subtract 25 points
         * 4. If a Pill color will be placed on a non-matching color subtract 25 points for each one
         * 5. 
         * 6. 
         */
        
        //store the original location
        final int pillCol = getPill().getCol();
        final int pillRow = getPill().getRow();
        
        int score = 0;
        
        for (int col=0; col < board.getCols(); col++)
        {
            //get the first block found in the current column
            final Block block = getBlockBelow(col, 0, board);
            
            //there was no block found below skip to the next column
            if (block == null)
                continue;
            
            //check every rotation at the current position
            for (Rotation rotation : Rotation.values())
            {
                //set the Pill at the current location
                getPill().setCol(block.getCol());
                getPill().setRow(block.getRow());
                
                //move up 1 row from Block
                getPill().decreaseRow();
                
                //set the current rotation
                getPill().setRotation(rotation);

                //if we have a collision this is not a valid location so move to the next rotation
                if (board.hasCollision(getPill()))
                    continue;
                
                //current score we are calculating
                int tmpScore = 0;

                //1. place the Pill on the board so we can check for matches and calculate score
                placePill(board);
                
                //get the list of blocks that matched so we can count the number of viruses destoryed
                List<Block> deadBlocks = board.checkMatch();
                
                //number of viruses destroyed
                int count = 0;
                
                //if there are dead Block(s) count how many to determine the score added
                if (deadBlocks.size() > 0)
                {
                    for (Block tmp : deadBlocks)
                    {
                        //if the Block is a Virus add to the count
                        if (Virus.isVirus(tmp.getType()))
                            count++;
                    }
                }
                
                //remove the pill we placed
                board.removeBlock(getPill());
                
                //add virus kill score
                tmpScore += (count * 100);
                
                //2. check if the Block(s) below the Pill match and if so add 25 points for each
                if (block.hasMatch(getPill().getType()))
                {
                    tmpScore += 25;
                }
                else
                {
                    //no match so reduce score
                    tmpScore -= 25;
                }
                
                final Block tmpBlock = getBlockBelow(getPill().getExtra().getCol(), 0, board);
                
                //make sure Block exists and it isn't above the extra Block
                if (tmpBlock != null && tmpBlock.getRow() > getPill().getExtra().getRow())
                {
                    if (getPill().getExtra().hasMatch(tmpBlock.getType()))
                    {
                        tmpScore += 25;
                    }
                    else
                    {
                        tmpScore -= 25;
                    }
                }
                
                

                //if the current score beats the saved score set the new goal
                if (tmpScore > score)
                {
                    //set the location goal and rotation
                    super.setGoals(getPill().getCell(), rotation);
                }
            }
        }
        
        //set the pill back to the original location
        getPill().setCol(pillCol);
        getPill().setRow(pillRow);
        
        //also reset the rotation
        getPill().reset();
    }
    
    /**
     * Start at the parameter row and continue south until we find the Block below.
     * If no Block is found null is returned.
     * 
     * @param col The column
     * @param row The row start position
     * @param board The board to check
     * @return Block if there is no Block below null will be returned
     */
    private Block getBlockBelow(final int col, final int startRow, final Board board)
    {
        //start accordingly and continue moving south
        for (int row = startRow + 1; row < board.getRows(); row++)
        {
            //if the Block exists return it
            if (board.getBlock(col, row) != null)
                return board.getBlock(col, row);
        }
        
        //no Block was found so return null
        return null;
    }
}