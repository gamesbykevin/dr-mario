package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.drmario.engine.Engine;

public class StartGame extends Layer implements LayerRules
{
    public StartGame(final Engine engine)
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine)
    {
        //no options here to setup
    }    
}