package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.engine.Engine;
import java.awt.event.KeyEvent;

/**
 * The player will contain the pill piece and the input 
 * @author GOD
 */
public final class Human extends Player implements IPlayer
{
    public Human(final long delay)
    {
        super(delay);
    }
    
    /**
     * Update the Human Player with the given input
     * @param engine 
     */
    @Override
    public void update(final Engine engine)
    {
        //if the player has lost no more updates required
        if (hasLose())
            return;
        
        super.update(engine);
        
        //if time has passed the blocks need to drop or if the user is forcing the piece to drop
        if (getTimer().hasTimePassed() || engine.getKeyboard().hasKeyPressed(KeyEvent.VK_DOWN))
        {
            //remove key released from List
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_DOWN);

            //reset the time
            getTimer().reset();
            
            //apply gravity to pill
            applyGravity(engine.getManager().getBoard());
        }
        
        //the user wants to rotate the pieces
        if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_UP))
        {
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_UP);
            
            //rotate the pill
            getPill().rotate();
            
            //check for collision
            if (engine.getManager().getBoard().hasCollision(getPill()))
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
            if (engine.getManager().getBoard().hasCollision(getPill()))
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
            if (engine.getManager().getBoard().hasCollision(getPill()))
            {
                //move the blocks back
                getPill().decreaseCol();
            }
            
            engine.getKeyboard().removeKeyPressed(KeyEvent.VK_RIGHT);
        }
        
        //set the correct x,y coordinates for the pill
        if (getPill() != null)
        {
            getPill().setPosition(engine.getManager().getBoard().getX(), engine.getManager().getBoard().getY());
        }
    }
}