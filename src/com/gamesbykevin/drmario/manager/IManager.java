package com.gamesbykevin.drmario.manager;

import com.gamesbykevin.drmario.engine.Engine;

import java.awt.Graphics;

public interface IManager 
{
    /**
     * This is for good housekeeping
     */
    public void dispose();
    
    /**
     * Update all application elements 
     * 
     * @param engine Our main game engine
     */
    public void update(final Engine engine) throws Exception;
    
    /**
     * Draw all of the application elements
     * @param graphics 
     */
    public void render(final Graphics graphics);
}
