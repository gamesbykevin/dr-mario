package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Pill;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.resource.Resources.GameImage;
import com.gamesbykevin.drmario.shared.IElement;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Random;

/**
 * Each player will have a game board and Pill
 * @author GOD
 */
public class Player extends Sprite implements IElement
{
    //the board where the game play will occur
    private Board board;
    
    //pill piece
    private Pill pill, next;
    
    //the start location for the Pill
    protected static final Cell START = new Cell(3, 0);
    
    //our timer object to determine when the pieces should drop
    private Timer gravityTimer;
    
    //has the player lost
    private boolean lose = false;
    
    //has the player won
    private boolean win = false;
    
    //the final location of the Pill
    private Cell goal;
    
    //the final rotation as well
    private Rotation rotation;
    
    //the seed used to generate random numbers
    private final long SEED = System.nanoTime();
    
    //random number generator
    private final Random random;
    
    //locations of the 3 backgrounds
    private static final Rectangle BACKGROUND_1 = new Rectangle(5,   373, 256, 224);
    private static final Rectangle BACKGROUND_2 = new Rectangle(266, 373, 256, 224);
    private static final Rectangle BACKGROUND_3 = new Rectangle(527, 373, 256, 224);
    
    //the pill jar where gameplay will take place
    private static final Rectangle PILL_CONTAINER_1 = new Rectangle(13,  187, 80, 176);
    private static final Rectangle PILL_CONTAINER_2 = new Rectangle(98,  187, 80, 176);
    private static final Rectangle PILL_CONTAINER_3 = new Rectangle(183, 187, 80, 176);
    
    //the magnifying glass showing the animated viruses
    private static final Rectangle VIRUS_CONTAINER = new Rectangle(186, 39, 80, 88);
    
    //screen that says stage clear
    private static final Rectangle WIN_NOTIFICATION = new Rectangle(279, 217, 60, 68);
    
    //screen that says game over
    private static final Rectangle LOSE_NOTIFICATION = new Rectangle(279, 289, 60, 52);
    
    //container for dr. mario animation
    private static final Rectangle MARIO_CONTAINER = new Rectangle(125, 57, 56, 56);
    
    //animation for mario when throwing a pill
    private static final Rectangle MARIO_PILL_THROW_1 = new Rectangle(47, 48, 25, 40);
    private static final Rectangle MARIO_PILL_THROW_2 = new Rectangle(76, 49, 32, 39);
    private static final Rectangle MARIO_PILL_THROW_3 = new Rectangle(15, 48, 24, 40);
    
    //non-repeating animation for mario when game over
    private static final Rectangle MARIO_GAME_OVER_1 = new Rectangle(13, 89, 25, 38);
    private static final Rectangle MARIO_GAME_OVER_2 = new Rectangle(48, 89, 25, 38);
    private static final Rectangle MARIO_GAME_OVER_3 = new Rectangle(77, 89, 38, 38);
    
    //the dr. mario logo
    private static final Rectangle LOGO = new Rectangle(21, 17, 88, 22);
    
    //backgrounds where score and other info are located
    private static final Rectangle INFORMATION_CONTAINER_1 = new Rectangle(351, 231, 72, 78);
    private static final Rectangle INFORMATION_CONTAINER_2 = new Rectangle(427, 231, 64, 102);
    
    //Level Text
    private static final Rectangle TEXT_LEVEL = new Rectangle(690, 245, 38, 8);
    
    //Speed Text
    private static final Rectangle TEXT_SPEED = new Rectangle(689, 255, 39, 7);
    
    //Virus Text
    private static final Rectangle TEXT_VIRUS = new Rectangle(689, 264, 39, 7);
    
    //Score Text
    private static final Rectangle TEXT_SCORE = new Rectangle(689, 282, 39, 7);
    
    //Difficulty Text
    private static final Rectangle TEXT_LOW = new Rectangle(672, 306, 22, 7);
    private static final Rectangle TEXT_MED = new Rectangle(699, 306, 23, 7);
    private static final Rectangle TEXT_HI = new Rectangle(727, 306, 15, 7);
    
    //# 0 - 9 Text
    private static final Rectangle NUMBER_0 = new Rectangle(664, 294, 7, 7);
    private static final Rectangle NUMBER_1 = new Rectangle(673, 294, 6, 7);
    private static final Rectangle NUMBER_2 = new Rectangle(681, 294, 7, 7);
    private static final Rectangle NUMBER_3 = new Rectangle(690, 294, 7, 7);
    private static final Rectangle NUMBER_4 = new Rectangle(699, 294, 7, 7);
    private static final Rectangle NUMBER_5 = new Rectangle(708, 294, 7, 7);
    private static final Rectangle NUMBER_6 = new Rectangle(717, 294, 7, 7);
    private static final Rectangle NUMBER_7 = new Rectangle(725, 294, 7, 7);
    private static final Rectangle NUMBER_8 = new Rectangle(734, 294, 7, 7);
    private static final Rectangle NUMBER_9 = new Rectangle(743, 294, 7, 7);
    
    //animation for display virus alive
    private static final Rectangle DISPLAY_VIRUS_BLUE_ALIVE_1 = new Rectangle(301, 47, 24, 21);
    private static final Rectangle DISPLAY_VIRUS_BLUE_ALIVE_2 = new Rectangle(274, 44, 23, 24);
    private static final Rectangle DISPLAY_VIRUS_BLUE_ALIVE_3 = new Rectangle(301, 47, 24, 21);
    private static final Rectangle DISPLAY_VIRUS_BLUE_ALIVE_4 = new Rectangle(329, 44, 23, 24);
    
    //animation for display virus dead
    private static final Rectangle DISPLAY_VIRUS_BLUE_HURT_1 = new Rectangle(386, 47, 23, 21);
    private static final Rectangle DISPLAY_VIRUS_BLUE_HURT_2 = new Rectangle(416, 47, 23, 21);
    
    //animation for display virus alive
    private static final Rectangle DISPLAY_VIRUS_YELLOW_ALIVE_1 = new Rectangle(301, 74, 24, 23);
    private static final Rectangle DISPLAY_VIRUS_YELLOW_ALIVE_2 = new Rectangle(274, 75, 24, 22);
    private static final Rectangle DISPLAY_VIRUS_YELLOW_ALIVE_3 = new Rectangle(301, 74, 24, 23);
    private static final Rectangle DISPLAY_VIRUS_YELLOW_ALIVE_4 = new Rectangle(328, 75, 24, 22);
    
    //animation for display virus dead
    private static final Rectangle DISPLAY_VIRUS_YELLOW_HURT_1 = new Rectangle(386, 76, 23, 21);
    private static final Rectangle DISPLAY_VIRUS_YELLOW_HURT_2 = new Rectangle(416, 76, 23, 21);
    
    //animation for display virus alive
    private static final Rectangle DISPLAY_VIRUS_RED_ALIVE_1 = new Rectangle(301, 103, 24, 22);
    private static final Rectangle DISPLAY_VIRUS_RED_ALIVE_2 = new Rectangle(274, 102, 24, 24);
    private static final Rectangle DISPLAY_VIRUS_RED_ALIVE_3 = new Rectangle(301, 103, 24, 22);
    private static final Rectangle DISPLAY_VIRUS_RED_ALIVE_4 = new Rectangle(328, 102, 24, 24);
    
    //animation for display virus dead
    private static final Rectangle DISPLAY_VIRUS_RED_HURT_1 = new Rectangle(386, 105, 24, 20);
    private static final Rectangle DISPLAY_VIRUS_RED_HURT_2 = new Rectangle(416, 105, 24, 20);
    
    private enum AnimationKey
    {
        DisplayHurtRed,
        DisplayHurtBlue, 
        DisplayHurtYellow,
        
        DisplayAliveRed,
        DisplayAliveBlue,
        DisplayAliveYellow,
        
        MarioPillThrow,
        MarioGameOver
    }
    
    private HashMap<AnimationKey, SpriteSheetAnimation> animations;
    
    /**
     * Create a new timer with the specified delay that will determine the duration
     * between Pill drops.
     * @param container the overall dimensions of this game
     * @param delay The time delay in milliseconds
     */
    public Player(final Rectangle container, final long delay) throws Exception
    {
        //new timer with the specified milisecond delay
        this.gravityTimer = new Timer(TimerCollection.toNanoSeconds(delay));
        
        //create new random number generator
        this.random = new Random(SEED);
        
        //set the overall boundary of the players board
        super.setLocation(container.x, container.y);
        super.setDimensions(container.width, container.height);
        
        //map the animation
        animations = new HashMap<>();

        //object we will use for our sprite sheet animations
        SpriteSheetAnimation ssa;

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_1, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_2, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_3, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_4, TimerCollection.toNanoSeconds(333L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveBlue, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_1, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_2, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_3, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_4, TimerCollection.toNanoSeconds(333L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveRed, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_1, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_2, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_3, TimerCollection.toNanoSeconds(333L));
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_4, TimerCollection.toNanoSeconds(333L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveYellow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_BLUE_HURT_1, TimerCollection.toNanoSeconds(111L));
        ssa.add(DISPLAY_VIRUS_BLUE_HURT_2, TimerCollection.toNanoSeconds(111L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtBlue, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_RED_HURT_1, TimerCollection.toNanoSeconds(111L));
        ssa.add(DISPLAY_VIRUS_RED_HURT_2, TimerCollection.toNanoSeconds(111L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtRed, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_YELLOW_HURT_1, TimerCollection.toNanoSeconds(111L));
        ssa.add(DISPLAY_VIRUS_YELLOW_HURT_2, TimerCollection.toNanoSeconds(111L));
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtYellow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(MARIO_PILL_THROW_1, TimerCollection.toNanoSeconds(300L));
        ssa.add(MARIO_PILL_THROW_2, TimerCollection.toNanoSeconds(300L));
        ssa.add(MARIO_PILL_THROW_3, TimerCollection.toNanoSeconds(300L));
        ssa.setLoop(false);
        animations.put(AnimationKey.MarioPillThrow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(MARIO_GAME_OVER_1, TimerCollection.toNanoSeconds(500L));
        ssa.add(MARIO_GAME_OVER_2, TimerCollection.toNanoSeconds(500L));
        ssa.add(MARIO_GAME_OVER_3, TimerCollection.toNanoSeconds(500L));
        ssa.setLoop(false);
        animations.put(AnimationKey.MarioGameOver, ssa);
        
        //create a Next Pill that will be the next Pill used
        createNextPill();
    }
    
    public void createBoard(final int virusCount) throws Exception
    {
        board = new Board(virusCount, SEED);
    }
    
    protected Board getBoard()
    {
        return this.board;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (board != null)
            board.dispose();
        
        if (pill != null)
            pill.dispose();
        
        if (next != null)
            next.dispose();
        
        board = null;
        pill = null;
        next = null;
        
        gravityTimer = null;
    
        goal = null;
    
        rotation = null;
    }
    
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if we have won or lost there is no need to update
        if (hasWin() || hasLose())
            return;
        
        //the entrance is blocked (Game Over)
        if (getBoard().getBlock(START) != null)
        {
            this.lose = true;
            return;
        }
        
        //set image if not already
        if (super.getImage() == null)
            super.setImage(engine.getResources().getGameImage(GameImage.Spritesheet));
        
        //update all animations
        for (AnimationKey key : AnimationKey.values())
        {
            animations.get(key).update(engine.getMain().getTime());
        }
        
        //have we removed all of the viruses
        if (getBoard().getVirusCount() <= 0)
        {
            this.win = true;
            return;
        }
        
        //if our pill has not been created yet 
        if (getPill() == null)
        {
            //take the next Pill and assign as current
            createPill();
            
            //create a new Pill for the future
            createNextPill();
        }
        
        //update timer
        getTimer().update(engine.getMain().getTime());
        
        //has time passed
        if (getTimer().hasTimePassed())
        {
            //apply gravity to the Pill and check the Board for collision
            applyGravity();
        }
    }
    
    /**
     * Let the Artificial Intelligence know what the destination and rotation is
     * @param goal The col/row of where we want the Pill to be placed.
     * @param rotation The final rotation if the Pill
     */
    protected void setGoals(final Cell goal, final Rotation rotation)
    {
        this.goal = goal;
        this.rotation = rotation;
    }
    
    /**
     * Get the Goal destination set by the AI 
     * @return Cell containing the col/row of our destination
     */
    protected Cell getGoal()
    {
        return this.goal;
    }
    
    /**
     * Get the goal Rotation set by the AI
     * @return Rotation (North, South, East, or West)
     */
    protected Rotation getRotation()
    {
        return this.rotation;
    }
    
    /**
     * Apply gravity to the current Pill.
     * If the Pill hits the bottom or has a collision we will
     * add the Pill to the board and then remove and reset the goal.
     * In addition since gravity is applied we reset the gravity timer as well
     * 
     * @param board The board we are using to check for collision
     */
    protected void applyGravity() throws Exception
    {
        //move the pill down 1 row
        getPill().increaseRow();

        //now that the pill moved check for collision
        if (getBoard().hasCollision(getPill()))
        {
            //move the pill back
            getPill().decreaseRow();

            if (getPill().getRow() < 0 || getPill().getExtra().getRow() < 0)
            {
                this.lose = true;
                return;
            }
            else
            {
                //place Pill on board
                getBoard().addPill(getPill());

                //now that Pill has been placed remove it
                removePill();

                //reset the goal
                resetGoal();
            }
        }
        
        //reset the time until gravity has to be applied again
        getTimer().reset();
    }
    
    /**
     * Make sure the current Pill has the correct 
     * (x,y) coordinates based on their (column, row) location
     * @param board The game board that contains the (x, y) start position
     */
    protected void updateLocation()
    {
        if (getPill() != null)
        {
            //set the correct x,y coordinates for the pill
            getPill().setPosition(getBoard().getX(), getBoard().getY());
        }
    }
    
    public boolean hasLose()
    {
        return this.lose;
    }
    
    public boolean hasWin()
    {
        return this.win;
    }
    
    /**
     * Get the Timer for the player that determines when gravity is applied
     * @return Timer
     */
    private Timer getTimer()
    {
        return this.gravityTimer;
    }
    
    /**
     * Get the current Pill being used
     * @return Pill the current Pill we are controlling
     */
    protected Pill getPill()
    {
        return this.pill;
    }
    
    /**
     * Get the Pill that is destined to be the next Pill after
     * the current Pill is placed on the board.
     * 
     * @return Pill the next Pill after the current Pill has been placed
     */
    protected Pill getNext()
    {
        return this.next;
    }
    
    protected void createNextPill() throws Exception
    {
        this.next = new Pill(random);
        this.next.setStart(START);
    }
    
    /**
     * Take the next Pill and make that the current
     */
    protected void createPill()
    {
        //assign the next Pill to the current
        this.pill = next;
    }
    
    /**
     * Remove the current Pill
     */
    private void removePill()
    {
        this.pill = null;
    }
    
    /**
     * Remove the goal set
     */
    private void resetGoal()
    {
        this.goal = null;
        this.rotation = null;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        
        getBoard().render(graphics);
        //getBoard().canInteract();
        if (getPill() != null)
        {
            //getPill().render(graphics);
        }
    }
}