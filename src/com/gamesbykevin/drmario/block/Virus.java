package com.gamesbykevin.drmario.block;

import com.gamesbykevin.drmario.block.Block.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * These are the enemies that need to be destroyed
 * @author GOD
 */
public class Virus extends Block
{
    public Virus()
    {
        super(getRandomVirus());
    }
    
    public static Type getRandomVirus()
    {
        List<Type> types = new ArrayList<>();
        
        for (Type tmp : Type.values())
        {
            if (isVirus(tmp))
                types.add(tmp);
        }
        
        return types.get((int)(Math.random() * types.size()));
    }
    
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
