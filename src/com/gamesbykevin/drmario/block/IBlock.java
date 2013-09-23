package com.gamesbykevin.drmario.block;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Random;

public interface IBlock extends Disposable
{
    /**
     * Set a random Block Type.
     * Possibilities are 
     * (Red, Blue, Yellow) Pill
     * (Red, Blue, Yellow) Virus
     * 
     * @param random The random object used to generate a random index
     * 
     * @return Type
     */
    public void setRandom(final Random random);
    
    /**
     * Set the animations for the Block accordingly
     * @param block The block we want to add animation to
     * @throws Exception if the block type has not been set yet
     */
    public void setup(final Block block) throws Exception;
    
    /**
     * Draw our Block
     * @param graphics 
     * @param image
     */
    public void render(final Graphics graphics, final Image image);
}