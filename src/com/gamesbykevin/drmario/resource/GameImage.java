package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.ImageManager;

/**
 * All images for game
 * @author GOD
 */
public class GameImage extends ImageManager
{
    //location of resources
    private static final String DIRECTORY = "images/game/{0}.png";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Image Resources";
    
    public enum Keys
    {
        Spritesheet
    }
    
    public GameImage() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}