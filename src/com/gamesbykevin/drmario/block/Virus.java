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
    //animation for board virus
    private static final Rectangle BOARD_VIRUS_BLUE_1 = new Rectangle(84, 138, 9, 9);
    private static final Rectangle BOARD_VIRUS_BLUE_2 = new Rectangle(95, 139, 9, 8);
    
    //animation for board virus
    private static final Rectangle BOARD_VIRUS_YELLOW_1 = new Rectangle(84, 149, 9, 9);
    private static final Rectangle BOARD_VIRUS_YELLOW_2 = new Rectangle(95, 149, 9, 9);
    
    //animation for board virus
    private static final Rectangle BOARD_VIRUS_RED_1 = new Rectangle(84, 160, 9, 9);
    private static final Rectangle BOARD_VIRUS_RED_2 = new Rectangle(95, 160, 9, 9);
    
    public Virus(final Random random) throws Exception
    {
        super();

        //assign random Type
        super.setType(getRandom(random));
        
        //create sprite sheet
        super.createSpriteSheet();
        
        //object we will use for our sprite sheet animation
        SpriteSheetAnimation animation = new SpriteSheetAnimation();
        
        switch(super.getType())
        {
            case BlueVirus:
                animation.add(BOARD_VIRUS_BLUE_1, TimerCollection.toNanoSeconds(250L));
                animation.add(BOARD_VIRUS_BLUE_2, TimerCollection.toNanoSeconds(250L));
                break;
                
            case YellowVirus:
                animation.add(BOARD_VIRUS_YELLOW_1, TimerCollection.toNanoSeconds(250L));
                animation.add(BOARD_VIRUS_YELLOW_2, TimerCollection.toNanoSeconds(250L));
                break;
                
            case RedVirus:
                animation.add(BOARD_VIRUS_RED_1, TimerCollection.toNanoSeconds(250L));
                animation.add(BOARD_VIRUS_RED_2, TimerCollection.toNanoSeconds(250L));
                break;
        }
        
        //all will loop
        animation.setLoop(true);
        super.getSpriteSheet().add(animation, AnimationKey.Alive);
        super.getSpriteSheet().setCurrent(AnimationKey.Alive);
    }
    
    /**
     * Get a random Type of Virus.
     * Blue Virus, Yellow Virus, Red Virus
     * 
     * @param rand The random object used to generate a random index
     * 
     * @return Type
     */
    @Override
    public Type getRandom(final Random rand)
    {
        List<Type> types = new ArrayList<>();
        
        for (Type tmp : Type.values())
        {
            if (isVirus(tmp))
                types.add(tmp);
        }
        
        return types.get(rand.nextInt(types.size()));
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