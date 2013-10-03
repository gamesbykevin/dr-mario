package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.*;

/**
 * All audio for menu
 * @author GOD
 */
public class MenuAudio extends AudioManager
{
    //location of resources
    private static final String DIRECTORY = "audio/menu/sound/{0}.wav";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Menu Audio Resources";
    
    public enum Keys
    {
        OptionChange
    }
    
    public MenuAudio() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}