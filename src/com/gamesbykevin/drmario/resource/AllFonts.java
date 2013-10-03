package com.gamesbykevin.drmario.resource;

import static com.gamesbykevin.drmario.resource.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.*;

/**
 * All fonts
 * @author GOD
 */
public class AllFonts extends FontManager
{
    //location of resources
    private static final String DIRECTORY = "font/{0}.ttf";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Font Resources";
    
    public enum Keys
    {
        Menu, Game
    }
    
    public AllFonts() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}