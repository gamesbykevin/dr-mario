package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.engine.Engine;

/**
 * The AI Agent we are competing against
 * @author GOD
 */
public final class Agent extends Player implements IPlayer
{
    public Agent(final long delay)
    {
        super(delay);
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (hasLose())
            return;
        
        super.update(engine);
        
        
    }
}