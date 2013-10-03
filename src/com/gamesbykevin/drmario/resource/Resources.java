package com.gamesbykevin.drmario.resource;

import com.gamesbykevin.framework.resources.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class will load all resources in the collection and provide a way to access them
 * @author GOD
 */
public class Resources implements IResources
{
    //root directory of all resources
    public static final String RESOURCE_DIR = "resources/"; 
    
    //are we done loading resources
    private boolean loading = true;
    
    private enum TypeAudio
    {
        GameAudio, MenuAudio, GameMusic, MenuMusic
    }
    
    private enum TypeImage
    {
        MenuImage, GameImage
    }
    
    private enum TypeFont
    {
        AllFonts
    }
    
    //object containing all audio objects
    private final HashMap<Object, AudioManager> audio;
    
    //object containing all image objects
    private final HashMap<Object, ImageManager> images;
    
    //object containing all font objects
    private final HashMap<Object, FontManager> fonts;
    
    public Resources() throws Exception
    {
        audio = new HashMap<>();
        audio.put(TypeAudio.GameAudio, new GameAudio());
        audio.put(TypeAudio.GameMusic, new GameMusic());
        audio.put(TypeAudio.MenuAudio, new MenuAudio());
        audio.put(TypeAudio.MenuMusic, new MenuMusic());
        
        images = new HashMap<>();
        images.put(TypeImage.GameImage, new GameImage());
        images.put(TypeImage.MenuImage, new MenuImage());
        
        fonts = new HashMap<>();
        fonts.put(TypeFont.AllFonts, new AllFonts());
        
        //make sure each constant has been added to hashmap
        for (TypeAudio key : TypeAudio.values())
        {
            if (audio.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap audio");
        }
        
        //make sure each constant has been added to hashmap
        for (TypeImage key : TypeImage.values())
        {
            if (images.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap images");
        }
        
        //make sure each constant has been added to hashmap
        for (TypeFont key : TypeFont.values())
        {
            if (fonts.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap fonts");
        }
    }
    
    @Override
    public boolean isLoading()
    {
        return loading;
    }
    
    /**
     * Stop all sound
     */
    public void stopAllSound()
    {
        for (Object key : audio.keySet())
        {
            audio.get(key).stopAll();
        }
    }
    
    /**
     * Here we will load the resources one by one and then marking the process finished once done
     * @param source Class in root directory of project so we have a relative location so we know how to access resources
     * @throws Exception 
     */
    @Override
    public void update(final Class source) throws Exception
    {
        if (fonts != null)
        {
            for (Object key : fonts.keySet())
            {
                if (fonts.get(key).isLoading())
                {
                    fonts.get(key).update(source);
                    return;
                }
            }
        }
        
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                if (audio.get(key).isLoading())
                {
                    audio.get(key).update(source);
                    return;
                }
            }
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                if (images.get(key).isLoading())
                {
                    images.get(key).update(source);
                    return;
                }
            }
        }
        
        //we are done loading the resources
        this.loading = false;
    }
    
    /**
     * Checks to see if audio is turned on
     * @return 
     */
    public boolean isAudioEnabled()
    {
        for (Object key : audio.keySet())
        {
            return audio.get(key).isEnabled();
        }
        
        return false;
    }
    
    /**
     * Set the audio enabled.
     * All existing audio collections here will have the audio enabled value set.
     * 
     * @param boolean Is the audio enabled 
     */
    public void setAudioEnabled(final boolean enabled)
    {
        for (Object key : audio.keySet())
        {
            audio.get(key).setEnabled(enabled);
        }
    }
    
    /**
     * Get the specified Image from the Menu list
     * @param key
     * @return Image
     */
    public Image getMenuImage(final Object key)
    {
        return images.get(TypeImage.MenuImage).get(key);
    }
    
    public Image getGameImage(final Object key)
    {
        return images.get(TypeImage.GameImage).get(key);
    }
    
    public Audio getMenuMusic(final Object key)
    {
        return audio.get(TypeAudio.MenuMusic).get(key);
    }
    
    public Audio getMenuAudio(final Object key)
    {
        return audio.get(TypeAudio.MenuAudio).get(key);
    }
    
    public void playGameAudio(final Object key, final boolean loop)
    {
        audio.get(TypeAudio.GameAudio).play(key, loop);
    }
    
    public void playGameMusic(final Object key, final boolean loop)
    {
        audio.get(TypeAudio.GameMusic).play(key, loop);
    }
    
    public Font getFont(final Object key)
    {
        return fonts.get(TypeFont.AllFonts).get(key);
    }
    
    @Override
    public void dispose()
    {
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                audio.get(key).dispose();
                audio.put(key, null);
            }
            
            audio.clear();
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                images.get(key).dispose();
                images.put(key, null);
            }
            
            images.clear();
        }
        
        if (fonts != null)
        {
            for (Object key : fonts.keySet())
            {
                fonts.get(key).dispose();
                fonts.put(key, null);
            }
            
            fonts.clear();
        }
    }
    
    @Override
    public void render(final Graphics graphics, final Rectangle screen)
    {
        if (!isLoading())
            return;
        
        if (fonts != null)
        {
            for (Object key : fonts.keySet())
            {
                if (fonts.get(key).isLoading())
                {
                    fonts.get(key).render(graphics, screen);
                    return;
                }
            }
        }
        
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                if (audio.get(key).isLoading())
                {
                    audio.get(key).render(graphics, screen);
                    return;
                }
            }
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                if (images.get(key).isLoading())
                {
                    images.get(key).render(graphics, screen);
                    return;
                }
            }
        }
    }
}