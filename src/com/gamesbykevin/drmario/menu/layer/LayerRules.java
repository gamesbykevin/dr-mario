package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.drmario.engine.Engine;

public interface LayerRules 
{
    /**
     * Setup Layer including options if they exist
     * 
     * @param engine 
     */
    public void setup(final Engine engine) throws Exception;
}