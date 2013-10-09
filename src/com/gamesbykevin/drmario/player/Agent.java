package com.gamesbykevin.drmario.player;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.block.Block;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.block.Virus;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.player.PlayerInformation.SpeedKey;
import com.gamesbykevin.drmario.resource.*;
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
    
    //movement delay for the differet speed(s)
    private static final long SPEED_LOW = TimerCollection.toNanoSeconds(750L);
    private static final long SPEED_MED = TimerCollection.toNanoSeconds(250L);
    private static final long SPEED_HI  = TimerCollection.toNanoSeconds(165L);
    
    public Agent(final Rectangle renderLocation)
    {
        super(renderLocation);
        
        //we are not human
        super.setHuman(false);
    }
    
    @Override
    public void setSpeed(final SpeedKey speedKey)
    {
        //create a new Timer that calculates the delay between each move
        switch(speedKey)
        {
            case Low:
                this.movementTimer = new Timer(SPEED_LOW);
                break;
                
            case Medium:
                this.movementTimer = new Timer(SPEED_MED);
                break;
                
            case High:
                this.movementTimer = new Timer(SPEED_HI);
                break;
        }
        
        //set speed for display purposes
        super.setSpeed(speedKey);
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update(engine);
        
        //if we won or lost no need to check for keyboard input
        if (hasWin() || hasLose())
            return;
        
        //if we can't interact with the board due to a virus/pill match or pill drop etc..
        if (!getBoard().canInteract())
            return;
        
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
                    
                    //play sound effect
                    engine.getResources().playGameAudio(GameAudio.Keys.Rotate, false);
                }
                else
                {
                    //we are at the correct location so now we need to drop
                    if (getPill().getCol() == getGoal().getCol())
                    {
                        //drop Pill to next row
                        final boolean result = applyGravity();
                        
                        //if block(s) were placed play sound effect
                        if (result)
                            engine.getResources().playGameAudio(GameAudio.Keys.Stack, false);
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
                            
                            //play sound effect
                            //engine.getResources().getGameAudio(GameAudio.Move).play();
                        }

                        //we are east of our goal so move west
                        if (getPill().getCol() > getGoal().getCol())
                        {
                            //move 1 column to the west
                            getPill().decreaseCol();
                            
                            //if there was a collision move our Pill back
                            if (getBoard().hasCollision(getPill()) && getPill().getRow() > 0)
                                getPill().increaseCol();
                            
                            //play sound effect
                            //engine.getResources().getGameAudio(GameAudio.Move).play();
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
            final Block block = getBoard().getBlockBelow(col);
            
            //check every rotation at the current position
            for (Rotation rotation : Rotation.values())
            {
                //set the current rotation
                getPill().setRotation(rotation);
            
                //set the appropriate column now that the rotation has taken place
                getPill().setCol(col);
                
                //we also need to get the block below the Pill Extra
                final Block extraBlock = getBoard().getBlockBelow(getPill().getExtra().getCol());
                
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
                    super.setGoals(new Cell(getPill().getCol(), getPill().getRow()), getPill().getRotation());

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
        final boolean hasVirus = (getVirus(blockPill.getCol()) != null);

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
     * Check the specified column to determine if a virus exists.
     * @param col The column we want to start at.
     * @return Block - The virus found and if none found null will be returned.
     */
    private Block getVirus(final int col)
    {
        //start at the top and continue moving south
        for (int row = 0; row < getBoard().getRows(); row++)
        {
            final Block block = getBoard().getBlock(col, row);
            
            //if the Block exists and is a virus, then there is a virus below
            if (block != null && Virus.isVirus(block))
                return block;
        }
        
        //no virus was found so return
        return null;
    }
}