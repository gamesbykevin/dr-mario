package com.gamesbykevin.drmario.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.player.PlayerInformation.SpeedKey;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.resource.Resources;
import com.gamesbykevin.drmario.menu.CustomMenu.*;

public class Options extends Layer implements LayerRules
{
    public Options(final Engine engine) throws Exception
    {
        super(Layer.Type.SCROLL_HORIZONTAL_WEST_REPEAT, engine.getMain().getScreen());
        
        super.setTitle("Options");
        super.setImage(engine.getResources().getMenuImage(Resources.MenuImage.TitleBackground));
        super.setTimer(new Timer(TimerCollection.toNanoSeconds(5000L)));
        super.setForce(false);
        super.setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        //there will be 20 levels
        tmp = new Option("Level: ");
        for (int i=1; i <= 20; i++)
        {
            tmp.add(Integer.toString(i), engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        }
        super.add(OptionKey.Level, tmp);
        
        //different speed(s)
        tmp = new Option("Speed: ");
        for (SpeedKey key : SpeedKey.values())
        {
            tmp.add(key.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        }
        super.add(OptionKey.Speed, tmp);
        
        
        //different mode(s)
        tmp = new Option("Mode: ");
        
        //original single player mode
        tmp.add("Regular (single)", engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        
        //single player mode where you are timed to finish each level
        tmp.add("Timed (single)",   engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        
        //first to clear the board wins
        tmp.add("Regular (vs)",     engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        
        //first to clear the board wins with penalties added for each virus kill
        tmp.add("Attack (vs)",      engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        super.add(OptionKey.Mode, tmp);
        
        
        //# of opponents facing
        tmp = new Option("# Opponents: ");
        for (int i=0; i < 5; i++)
        {
            tmp.add(Integer.toString(i), engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        }
        super.add(OptionKey.OpponentTotal, tmp);
        
        tmp = new Option("Sound: ");
        for (Toggle toggle : Toggle.values())
        {
            tmp.add(toggle.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        }
        super.add(OptionKey.Sound, tmp);
        
        tmp = new Option("FullScreen: ");
        for (Toggle toggle : Toggle.values())
        {
            tmp.add(toggle.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.OptionChange));
        }
        super.add(OptionKey.FullScreen, tmp);
        
        tmp = new Option(LayerKey.MainTitle);
        tmp.add("Go Back", null);
        super.add(OptionKey.GoBack, tmp);
    }
}