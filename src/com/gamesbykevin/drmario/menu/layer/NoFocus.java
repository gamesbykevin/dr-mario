package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.resource.*;
import com.gamesbykevin.drmario.menu.CustomMenu;

public class NoFocus extends Layer implements LayerRules
{
    public NoFocus(final Engine engine)
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setImage(engine.getResources().getMenuImage(MenuImage.Keys.AppletFocus));
        setForce(false);
        setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine)
    {
        //no options here to setup
    }
}