package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.shared.IElement;

import com.gamesbykevin.drmario.engine.Engine;
import java.awt.Rectangle;

import java.awt.event.KeyEvent;

/**
 * The player will contain the pill piece and the input 
 * @author GOD
 */
public final class Human extends Player implements IElement
{
    public Human(final Rectangle renderLocation) throws Exception
    {
        super(renderLocation);
        
        //we are human
        super.setHuman(true);
    }
    
    /**
     * Update the Human Player with the given keyboard input
     * @param engine 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        super.update(engine);
        
        //if we won or lost no need to check for keyboard input
        if (hasWin() || hasLose())
            return;
        
        //if we can't interact with the board due to a virus/pill match or pill drop etc..
        if (!getBoard().canInteract())
            return;
        
        //if the Pill does not exist we don't need to worry about keyboard input
        if (getPill() == null)
            return;
        
        //if time has passed the blocks need to drop or if the user is forcing the piece to drop
        if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_DOWN))
        {
            //remove key released from List
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_DOWN);

            //apply gravity to pill
            applyGravity();
        }
        
        //the user wants to rotate the pieces
        if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_UP))
        {
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_UP);
            
            //rotate the pill
            getPill().rotate();
            
            //check for collision
            if (getBoard().hasCollision(getPill()))
            {
                //reset the location because of collision
                getPill().rewind();
            }
        }
        
        //move piece to the left
        if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_LEFT))
        {
            //move the pill left
            getPill().decreaseCol();

            //now that the blocked moved check for collision
            if (getBoard().hasCollision(getPill()))
            {
                //move the pill back
                getPill().increaseCol();
            }
            
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_LEFT);
        }

        //move the piece to the right
        if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            //move the blocks right
            getPill().increaseCol();

            //now that the blocked moved check for collision
            if (getBoard().hasCollision(getPill()))
            {
                getPill().decreaseCol();
                //move the blocks back;
            }
            
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_RIGHT);
        }
        
        //set the correct x,y Location for the current Pill
        updateLocation();
    }
}