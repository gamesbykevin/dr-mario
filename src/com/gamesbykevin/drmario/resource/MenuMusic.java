package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.AudioManager;

/**
 * All music for menu
 * @author GOD
 */
public class MenuMusic extends AudioManager
{
    //location of resources
    private static final String DIRECTORY = "audio/menu/music/{0}.mp3";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Menu Music Resources";
    
    public enum Keys
    {
        Title, Options
    }
    
    public MenuMusic() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}