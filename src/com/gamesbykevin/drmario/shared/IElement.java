package com.gamesbykevin.drmario.shared;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.drmario.engine.Engine;

import java.awt.Graphics;

/**
 * Basic methods required for game elementsMethods needed for game elements
 * @author GOD
 */
public interface IElement extends Disposable
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