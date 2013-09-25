package com.gamesbykevin.drmario.block;

import com.gamesbykevin.drmario.block.Block.Type;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.framework.util.TimerCollection;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * These are the enemies that need to be destroyed
 * @author GOD
 */
public class Virus extends Block implements IBlock
{
    public Virus()
    {
        super();
    }
    
    public void setup() throws Exception
    {
        setup(this);
    }
    
    /**
     * Set a random Type of Virus.
     * Blue Virus, Yellow Virus, Red Virus
     * 
     * @param rand The random object used to generate a random index
     */
    @Override
    public void setRandom(final Random random)
    {
        List<Type> types = new ArrayList<>();
        
        for (Type tmp : Type.values())
        {
            if (isVirus(tmp))
                types.add(tmp);
        }
        
        super.setType(types.get(random.nextInt(types.size())));
    }
    
    public static boolean isVirus(final Block block)
    {
        return (isVirus(block.getType()));
    }
    
    /**
     * Here lies the logic to determine if the Block is a Virus
     * @param type The type of Block
     * @return boolean Return true if type is one of the following (RedVirus, BlueVirus, YellowVirus)
     */
    public static boolean isVirus(final Type type)
    {
        switch(type)
        {
            case RedVirus:
            case BlueVirus:
            case YellowVirus:
                return true;
                
            default:
                return false;
        }
    }
}