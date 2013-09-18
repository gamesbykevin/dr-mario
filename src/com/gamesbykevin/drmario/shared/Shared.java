package com.gamesbykevin.drmario.shared;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * This Shared class will have shared objects
 * 
 * @author GOD
 */
public class Shared 
{
    //these dimensions is the size of the game window the user will see
    //public static final int INITIAL_WIDTH  = 260;
    public static final int INITIAL_WIDTH  = 1260;
    public static final int INITIAL_HEIGHT = 370;
    
    //the game is programmed in these dimensions
    //public static final int ORIGINAL_WIDTH  = 260;
    public static final int ORIGINAL_WIDTH  = 1260;
    public static final int ORIGINAL_HEIGHT = 370;
    
    //show UPS/FPS counters
    public static final boolean DEBUG = true;
    
    //how many updates per second, controls speed of game
    public static final int DEFAULT_UPS = 270;
    
    //how many frames per second, controls how many images are rendered
    public static final int DEFAULT_FPS = 60;
    
    //what is the name of our game
    public static final String GAME_NAME = "Dr. Mario";
    
    //blank cursor created here to hide the mouse cursor
    public static final Cursor CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
}