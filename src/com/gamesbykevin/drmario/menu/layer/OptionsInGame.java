package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.resource.*;
import com.gamesbykevin.drmario.menu.CustomMenu;
import com.gamesbykevin.drmario.menu.CustomMenu.Toggle;

public class OptionsInGame extends Layer implements LayerRules
{
    public OptionsInGame(final Engine engine) throws Exception
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setTitle("Options");
        setForce(false);
        setPause(true);
        setOptionContainerRatio(RATIO);
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        tmp.add("Resume", null);
        super.add(CustomMenu.OptionKey.Resume, tmp);
        
        tmp = new Option("Sound: ");
        for (Toggle toggle : Toggle.values())
        {
            tmp.add(toggle.toString(), engine.getResources().getMenuAudio(MenuAudio.Keys.OptionChange));
        }
        super.add(CustomMenu.OptionKey.Sound, tmp);
        
        tmp = new Option("FullScreen: ");
        for (Toggle toggle : Toggle.values())
        {
            tmp.add(toggle.toString(), engine.getResources().getMenuAudio(MenuAudio.Keys.OptionChange));
        }
        super.add(CustomMenu.OptionKey.FullScreen, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.NewGameConfirm);
        tmp.add("New Game", null);
        super.add(CustomMenu.OptionKey.NewGame, tmp);

        tmp = new Option(CustomMenu.LayerKey.ExitGameConfirm);
        tmp.add("Exit Game", null);
        super.add(CustomMenu.OptionKey.ExitGame, tmp);
    }
}