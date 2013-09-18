package com.gamesbykevin.drmario.block;

import com.gamesbykevin.drmario.block.Block.Type;
import com.gamesbykevin.drmario.shared.IDisposable;

import java.awt.Graphics;
import java.util.Random;

public interface IBlock extends IDisposable
{
    /**
     * Get a random Block Type.
     * Possibilities are 
     * (Red, Blue, Yellow) Pill
     * (Red, Blue, Yellow) Virus
     * 
     * @param rand The random object used to generate a random index
     * 
     * @return Type
     */
    public Type getRandom(final Random rand);
    
    /**
     * Draw our Block
     * @param graphics 
     */
    public void render(final Graphics graphics);
}
