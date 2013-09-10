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
    //the color of the block
    private final Color color;
    
    public Block(final Color color)
    {
        this.color = color;
    }
    
    public void render(Graphics graphics)
    {
        //for now each block will be an oval drawn
        graphics.setColor(color);
        graphics.fillOval((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
        
        //draw the block
        //super.draw(graphics);
    }
}