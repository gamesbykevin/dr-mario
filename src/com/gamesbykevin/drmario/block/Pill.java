package com.gamesbykevin.drmario.block;

import com.gamesbykevin.framework.base.Cell;

import com.gamesbykevin.drmario.block.Block.Type;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * The pills used in the game to cure the virus
 * @author GOD
 */
public class Pill extends Block 
{
    //a Pill consists of 2 Blocks
    private Block extra;
    
    //this will contain the previous location should we rotate Pill
    private Cell previous;
    
    /**
     * The different rotations of each Pill
     */
    public enum Rotation
    {
        East, South, West, North
    }
    
    //which rotation are we currently at
    private int rotationIndex;
    
    public Pill()
    {
        super(getRandom());
        
        //create new Block since a Pill constists of 2 Block(s)
        extra = new Block(getRandom());
        
        //make extra Block part of the same group so we know they are one
        extra.setGroup(super.getGroup());
        
        //we start with the Pill facing East
        this.rotationIndex = 0;
    }
    
    public Rotation getRotation()
    {
        return Rotation.values()[rotationIndex];
    }
    
    public static boolean isPill(final Block block)
    {
        return (isPill(block.getType()));
    }
    
    public Block getExtra()
    {
        return this.extra;
    }
    
    public void setStart(final Cell start)
    {
        super.setCol(start);
        super.setRow(start);
        
        extra.setCol(start.getCol() + 1);
        extra.setRow(start);
    }
    
    /**
     * Override method because we also need to make the adjustment to the extra Block
     */
    @Override
    public void decreaseRow()
    {
        super.decreaseRow();
        extra.decreaseRow();
    }
    
    /**
     * Override method because we also need to make the adjustment to the extra Block
     */
    @Override
    public void decreaseCol()
    {
        super.decreaseCol();
        extra.decreaseCol();
    }
    
    /**
     * Set the appropriate position for the Block(s) based on their col/row
     */
    @Override
    public void setPosition(final double startX, final double startY)
    {
        super.setPosition(startX, startY);
        extra.setPosition(startX, startY);
    }
    
    /**
     * Override method because we also need to make the adjustment to the extra Block
     */
    @Override
    public void increaseCol()
    {
        super.increaseCol();
        extra.increaseCol();
    }
    
    /**
     * Override method because we also need to make the adjustment to the extra Block
     */
    @Override
    public void increaseRow()
    {
        super.increaseRow();
        extra.increaseRow();
    }
    
    @Override
    public void setDimensions(final int width, final int height)
    {
        super.setDimensions(width, height);
        extra.setDimensions(width, height);
    }
    
    @Override
    public void setCol(final int col)
    {
        final int difference = super.getCol() - extra.getCol();
        
        super.setCol(col);
        extra.setCol(col - difference);
    }
    
    @Override
    public void setRow(final int row)
    {
        final int difference = super.getRow() - extra.getRow();
        
        super.setRow(row);
        extra.setRow(row - difference);
    }
    
    /**
     * Does the pill have a Block that has the same row as parameter row
     * @param row The row we want to see if it matches
     * @return boolean
     */
    public boolean hasRow(final int row)
    {
        return (getRow() == row || extra.getRow() == row);
    }
    
    public static Type getRandom()
    {
        List<Type> types = new ArrayList<>();
        
        for (Type tmp : Type.values())
        {
            if (isPill(tmp))
                types.add(tmp);
        }
        
        return types.get((int)(Math.random() * types.size()));
    }
    
    /**
     * Rotate the Pill
     */
    public void rotate()
    {
        //store the previous position
        this.previous = extra.getCell();
        
        //translate piece to the origin
        extra.setCol(extra.getCol() - getCol());
        extra.setRow(extra.getRow() - getRow());

        //store translated location
        final int row = extra.getRow();
        final int col = extra.getCol();

        //rotate piece
        extra.setCol(-row);
        extra.setRow(col);

        //translate piece back
        extra.setCol(getCol() + extra.getCol());
        extra.setRow(getRow() + extra.getRow());
        
        //move to the next rotation
        rotationIndex++;
        
        //keep the rotation in bounds
        if (rotationIndex >= Rotation.values().length)
            rotationIndex = 0;
    }
    
    /**
     * Set the position back to the previous rotation
     */
    public void rewind()
    {
        extra.setCol(previous);
        extra.setRow(previous);
        
        //move to the previous rotation
        rotationIndex--;
        
        //keep the rotation in bounds
        if (rotationIndex < 0)
            rotationIndex = Rotation.values().length - 1;
    }
    
    /**
     * Reset the Pill back to its original direction, which is facing east
     */
    public void reset()
    {
        //east is the original rotation
        setRotation(Rotation.East);
    }
    
    public void setRotation(final Rotation rotation)
    {
        //continue to rotate until we have reached our goal
        while (Rotation.values()[this.rotationIndex] != rotation)
        {
            rotate();
        }
    }
    
    /**
     * Here lies the logic to determine if the Block is a Pill
     * @param type The type of Block
     * @return boolean Return true if type is one of the following (RedPill, BluePill, YellowPill)
     */
    public static boolean isPill(final Type type)
    {
        switch(type)
        {
            case RedPill:
            case BluePill:
            case YellowPill:
                return true;
                
            default:
                return false;
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.render(graphics);
        extra.render(graphics);
    }
}