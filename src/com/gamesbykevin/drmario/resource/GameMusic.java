package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.AudioManager;

/**
 * All music for game
 * @author GOD
 */
public class GameMusic extends AudioManager
{
    //location of resources
    private static final String DIRECTORY = "audio/game/music/{0}.mp3";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Music Resources";
    
    public enum Keys
    {
        Fever, Chill, Lose, Win
    }
    
    public GameMusic() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}