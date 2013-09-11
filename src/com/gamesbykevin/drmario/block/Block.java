package com.gamesbykevin.drmario.block;

import com.gamesbykevin.framework.base.Sprite;
import java.awt.Color;

import java.awt.Graphics;

/**
 * The block will represent one part of the pill used in the original game
 * @author GOD
 */
public class Block extends Sprite
{
    /**
     * What type of Block is this
     */
    public enum Type
    {
        RedPill,
        RedVirus,
        BluePill,
        BlueVirus,
        YellowPill,
        YellowVirus
    }
    
    //the type of block
    private final Type type;
    
    //is this block dead
    private boolean dead = false;
    
    //the group will be a way to tell if multiple Block(s) are part of one
    private long group = System.nanoTime();
    
    public Block(final Block block)
    {
        super(block);
        
        this.type = block.getType();
    }
    
    public Block(final Type type)
    {
        this.type = type;
    }
    
    public long getGroup()
    {
        return this.group;
    }
    
    public void setGroup(final long group)
    {
        this.group = group;
    }
    
    public boolean isDead()
    {
        return this.dead;
    }
    
    public void setDead(final boolean dead)
    {
        this.dead = dead;
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    /**
     * Here we will take the current type and see if it matches parameter type.
     * @param type
     * @return True if there is a match
     */
    public boolean hasMatch(final Type type)
    {
        //if the two types are blue pill or blue virus or a combination we have a match
        if ((getType() == Type.BluePill || getType() == Type.BlueVirus) && (type == Type.BluePill || type == Type.BlueVirus))
            return true;
        
        //if the two types are yellow pill or yellow virus or a combination we have a match
        if ((getType() == Type.YellowPill || getType() == Type.YellowVirus) && (type == Type.YellowPill || type == Type.YellowVirus))
            return true;
        
        //if the two types are red pill or red virus or a combination we have a match
        if ((getType() == Type.RedPill || getType() == Type.RedVirus) && (type == Type.RedPill || type == Type.RedVirus))
            return true;
        
        return false;
    }
    
    public void render(Graphics graphics)
    {
        switch(type)
        {
            case RedPill:
            case RedVirus:
                graphics.setColor(Color.RED);
                break;
                
            case YellowPill:
            case YellowVirus:
                graphics.setColor(Color.YELLOW);
                break;
                
            case BluePill:
            case BlueVirus:
                graphics.setColor(Color.BLUE);
                break;
        }
        
        if (!isDead())
        {
            if (Pill.isPill(type))
                graphics.fillOval((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
            if (Virus.isVirus(type))
                graphics.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        }
        else
        {
            if (Pill.isPill(type))
                graphics.drawOval((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
            if (Virus.isVirus(type))
                graphics.drawRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        }
        
        //draw the block in the future as the appropriate image will be set
        //super.draw(graphics);
    }
}