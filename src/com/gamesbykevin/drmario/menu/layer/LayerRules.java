package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.drmario.engine.Engine;

public interface LayerRules 
{
    //default ratio for all the option containers
    public static final float RATIO = .60F;
    
    /**
     * Setup Layer options (if they exist)
     * 
     * @param engine 
     */
    public void setup(final Engine engine) throws Exception;
}