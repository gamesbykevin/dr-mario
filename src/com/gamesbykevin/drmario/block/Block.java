package com.gamesbykevin.drmario.block;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Color;
import java.awt.Graphics;

/**
 * The block will represent one part of the pill used in the original game
 * @author GOD
 */
public class Block extends Sprite implements Disposable
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
    
    public enum AnimationKey
    {
        Alive
    }
    
    //the type of block
    private Type type;
    
    //is this block dead
    private boolean dead = false;
    
    //the group will be a way to tell if multiple Block(s) are part of one
    private long group = System.nanoTime();
    
    //the dimensions of each block
    protected static final int WIDTH = 20;
    protected static final int HEIGHT = 20;
    
    public Block(final Block block) throws Exception
    {
        //call parent constructor so all properties are copied from object
        super(block);
        
        //the Type of Block. Pill, Virus, etc..
        setType(block.getType());
        
        //remember the group as well so we know which Block(s) are connected
        setGroup(block.getGroup());
        
        //set the dimensions of the Block
        super.setDimensions(WIDTH, HEIGHT);
    }
    
    public Block()
    {
        
    }
    
    /**
     * Sets the correct x,y location based on the row/col width/height
     */
    public void setPosition(final double startX, final double startY)
    {
        final int x = (int)(startX + (getCol() * WIDTH));
        final int y = (int)(startY + (getRow() * HEIGHT));

        //set the appropriate location and dimensions
        setLocation(x, y);
        setDimensions(WIDTH, HEIGHT);
    }
    
    public void setType(final Type type) throws Exception
    {
        if (getType() != null)
            throw new Exception("The Block Type can only be set once");
        
        this.type = type;
    }
    
    /**
     * Get the group this Block is associated with
     * @return long
     */
    public long getGroup()
    {
        return this.group;
    }
    
    /**
     * Set the group so we know the other Block(s) that belong to this one
     * @param group 
     */
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
        if (!isDead())
        {
            //image will be drawn in Board.class
        }
        else
        {
            //set the correct color accordingly
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
            
            //empty circle will be drawn when dead
            graphics.drawOval((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        }
    }
}