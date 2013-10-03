package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.*;

/**
 * All menu images
 * @author GOD
 */
public class MenuImage extends ImageManager
{
    //location of resources
    private static final String DIRECTORY = "images/menu/{0}.gif";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Menu Image Resources";
    
    public enum Keys
    {
        TitleScreen, Credits, AppletFocus, TitleBackground, Mouse, MouseDrag, 
        Controls1,  
        Instructions1, Instructions2, Instructions3, Instructions4
    }
    
    public MenuImage() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}