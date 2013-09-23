package com.gamesbykevin.drmario.block;

import com.gamesbykevin.drmario.block.Block.Type;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.framework.util.TimerCollection;
import java.awt.Graphics;
import java.awt.Image;
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
    
    //time delay for each frame in the animation
    private static final long BLUE_DELAY = TimerCollection.toNanoSeconds(333L);
    private static final long YELLOW_DELAY = TimerCollection.toNanoSeconds(150L);
    private static final long RED_DELAY = TimerCollection.toNanoSeconds(250L);
    
    public Virus()
    {
        super();
    }
    
    public void setup() throws Exception
    {
        setup(this);
    }
    
    @Override
    public void setup(final Block block) throws Exception
    {
        //create sprite sheet
        block.createSpriteSheet();
        
        //object we will use for our sprite sheet animation
        SpriteSheetAnimation animation = new SpriteSheetAnimation();
        
        switch(block.getType())
        {
            case BlueVirus:
                animation.add(BOARD_VIRUS_BLUE_1, BLUE_DELAY);
                animation.add(BOARD_VIRUS_BLUE_2, BLUE_DELAY);
                break;
                
            case YellowVirus:
                animation.add(BOARD_VIRUS_YELLOW_1, YELLOW_DELAY);
                animation.add(BOARD_VIRUS_YELLOW_2, YELLOW_DELAY);
                break;
                
            case RedVirus:
                animation.add(BOARD_VIRUS_RED_1, RED_DELAY);
                animation.add(BOARD_VIRUS_RED_2, RED_DELAY);
                break;
                
            default:
                throw new Exception("Block type has not been set yet.");
        }
        
        //all will loop
        animation.setLoop(true);
        block.getSpriteSheet().add(animation, AnimationKey.Alive);
        block.getSpriteSheet().setCurrent(AnimationKey.Alive);
        block.setDimensions();
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