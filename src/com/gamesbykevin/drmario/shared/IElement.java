package com.gamesbykevin.drmario.shared;

import java.awt.Graphics;

import com.gamesbykevin.drmario.engine.Engine;

/**
 * Basic methods required for game elementsMethods needed for game elements
 * @author GOD
 */
public interface IElement extends IDisposable
{
    /**
     * Update our game element accordingly
     * @param engine The Engine containing resources if needed
     * @throws Exception 
     */
    public void update(final Engine engine) throws Exception;
    
    /**
     * Draw our game element accordingly
     * @param graphics Graphics object to write to
     */
    public void render(final Graphics graphics);
}