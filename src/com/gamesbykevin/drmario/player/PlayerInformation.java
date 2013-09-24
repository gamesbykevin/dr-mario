package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Block;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.base.SpriteSheetAnimation;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * This class will render all of the extra information on the board
 * @author GOD
 */
public class PlayerInformation extends Sprite
{
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
    private static final Rectangle MARIO_PILL_THROW_1 = new Rectangle(40, 48, 32, 40);
    private static final Rectangle MARIO_PILL_THROW_2 = new Rectangle(76, 48, 32, 40);
    private static final Rectangle MARIO_PILL_THROW_3 = new Rectangle(7, 48, 32, 40);
    
    //non-repeating animation for mario when game over
    private static final Rectangle MARIO_GAME_OVER_1 = new Rectangle(13, 89, 25, 38);
    private static final Rectangle MARIO_GAME_OVER_2 = new Rectangle(48, 89, 25, 38);
    
    //the dr. mario logo
    private static final Rectangle LOGO = new Rectangle(21, 17, 88, 22);
    
    //backgrounds where score and other info are located
    private static final Rectangle INFORMATION_CONTAINER_1 = new Rectangle(351, 231, 72, 78);
    private static final Rectangle INFORMATION_CONTAINER_2 = new Rectangle(427, 231, 64, 102);
    
    //Speed Text
    private static final Rectangle TEXT_LOW = new Rectangle(672, 306, 22, 7);
    private static final Rectangle TEXT_MED = new Rectangle(699, 306, 23, 7);
    private static final Rectangle TEXT_HI = new Rectangle(727, 306, 15, 7);
   
    //text to display for the type of player
    private static final Rectangle TEXT_HUMAN = new Rectangle(645, 266, 39, 7);
    private static final Rectangle TEXT_CPU = new Rectangle(645, 254, 23, 7);
    
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
    
    //area representing the symbol :
    private static final Rectangle COLON_TEXT = new Rectangle(751, 294, 6, 7);
    
    //area representing the symbol .
    private static final Rectangle PERIOD_TEXT = new Rectangle(757, 295, 6, 6);
    
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
    
    private static final int BOARD_WIDTH = 64;
    private static final int BOARD_HEIGHT = 128;
    
    //where the pill container will be drawn
    private Point pillContainerDestination;
    
    //magnifying glass containing animations of the viruses
    private Point magnifyingGlassContainer;
    
    //the location of the displayed viruses in the magnifying glass
    private Point displayVirusBlue;
    private Point displayVirusRed;
    private Point displayVirusYellow;

    //the container displaying score and time
    private Point informationContainer1;
    
    //the container displaying level, speed, and virus count
    private Point informationContainer2;
    
    //where dr. mario will be displayed
    private Point marioContainer;
    
    //draw dr. mario logo
    private Point gameLogo;
    
    //where mario himself will be drawn
    private Point drMario;
    
    //where to display win/lose notification
    private Point finishLocation;
    
    //do we show the hurt animation
    private boolean displayBlueVirusHurt = false;
    private boolean displayRedVirusHurt = false;
    private boolean displayYellowVirusHurt = false;
    
    //do we show any virus animation at all
    private boolean displayBlueVirus   = true;
    private boolean displayRedVirus    = true;
    private boolean displayYellowVirus = true;
    
    //which animation to show for mario
    private boolean displayPillThrow = false;
    private boolean displayGameover = false;
    
    //do we show win or lose screen(s)
    private boolean showGameover = false;
    private boolean showSuccess = false;
    
    private HashMap<AnimationKey, SpriteSheetAnimation> animations;
    
    //various animation/duration delays
    private static final long DURATION_DELAY_VIRUS_HURT = TimerCollection.toNanoSeconds(2000L);
    private static final long DURATION_DELAY_VIRUS_ALIVE= TimerCollection.toNanoSeconds(200L);
    private static final long DELAY_ANIMATION_VIRUS_HURT = TimerCollection.toNanoSeconds(25L);
    private static final long DELAY_PILL_THROW = TimerCollection.toNanoSeconds(75L);
    private static final long DELAY_GAME_OVER_DISPLAY = TimerCollection.toNanoSeconds(500L);
    
    //the timers to determine how long the display virus hurt animation shows
    private TimerCollection timers;
    
    public enum BackgroundKey
    {
        Background1, Background2, Background3
    }
    
    //the background used
    private BackgroundKey background;
    
    private enum AnimationKey
    {
        DisplayHurtRed,
        DisplayHurtBlue, 
        DisplayHurtYellow,
        
        DisplayAliveRed,
        DisplayAliveBlue,
        DisplayAliveYellow,
        
        MarioPillThrow,
        MarioGameOver,
    }
    
    //is the player human
    private boolean human = false;
    
    //different available speeds
    public enum SpeedKey
    {
        Low, Medium, High
    }
    
    //speed which pill falls
    private SpeedKey speed;
    
    //the current score
    private int score = 0;
    
    //virus count
    private int count = 0;
    
    //the level
    private int level = 0;
    
    //the locations of the information
    private Point statusLocation;
    private Point speedLocation;
    private Point scoreLocation;
    private Point virusCountLocation;
    private Point levelLocation;
    private Point timeLocation;
    
    //original size of the game
    protected static final int SCREEN_WIDTH = 256;
    protected static final int SCREEN_HEIGHT = 224;
    
    private String timeDesc = "00:00.0";
    
    public PlayerInformation()
    {
        //hard set these location/dimension(s)
        super.setLocation(0, 0);
        super.setDimensions(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        //map the animation
        animations = new HashMap<>();
        
        //object we will use for our sprite sheet animations
        SpriteSheetAnimation ssa;

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_1, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_2, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_3, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_BLUE_ALIVE_4, DURATION_DELAY_VIRUS_ALIVE);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveBlue, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_1, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_2, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_3, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_RED_ALIVE_4, DURATION_DELAY_VIRUS_ALIVE);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveRed, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_1, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_2, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_3, DURATION_DELAY_VIRUS_ALIVE);
        ssa.add(DISPLAY_VIRUS_YELLOW_ALIVE_4, DURATION_DELAY_VIRUS_ALIVE);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayAliveYellow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_BLUE_HURT_1, DELAY_ANIMATION_VIRUS_HURT);
        ssa.add(DISPLAY_VIRUS_BLUE_HURT_2, DELAY_ANIMATION_VIRUS_HURT);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtBlue, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_RED_HURT_1, DELAY_ANIMATION_VIRUS_HURT);
        ssa.add(DISPLAY_VIRUS_RED_HURT_2, DELAY_ANIMATION_VIRUS_HURT);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtRed, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(DISPLAY_VIRUS_YELLOW_HURT_1, DELAY_ANIMATION_VIRUS_HURT);
        ssa.add(DISPLAY_VIRUS_YELLOW_HURT_2, DELAY_ANIMATION_VIRUS_HURT);
        ssa.setLoop(true);
        animations.put(AnimationKey.DisplayHurtYellow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(MARIO_PILL_THROW_1, DELAY_PILL_THROW);
        ssa.add(MARIO_PILL_THROW_2, DELAY_PILL_THROW);
        ssa.add(MARIO_PILL_THROW_3, DELAY_PILL_THROW);
        ssa.setLoop(false);
        animations.put(AnimationKey.MarioPillThrow, ssa);

        ssa = new SpriteSheetAnimation();
        ssa.add(MARIO_GAME_OVER_1, DELAY_GAME_OVER_DISPLAY);
        ssa.add(MARIO_GAME_OVER_2, DELAY_GAME_OVER_DISPLAY);
        ssa.setLoop(true);
        animations.put(AnimationKey.MarioGameOver, ssa);
    }
    
    public void setInformationLocations()
    {
        pillContainerDestination = new Point();
        pillContainerDestination.x = (int)((getWidth() / 2) - (PILL_CONTAINER_1.width / 2));
        pillContainerDestination.y = (int)((getHeight() * .18));
        
        finishLocation = new Point();
        finishLocation.x = (int)((getWidth() / 2) - (WIN_NOTIFICATION.width / 2));
        finishLocation.y = (int)(getHeight() * .45);

        magnifyingGlassContainer = new Point();
        magnifyingGlassContainer.x = (int)(getWidth()  * .02);
        magnifyingGlassContainer.y = (int)(getHeight() * .55);
        
        displayVirusBlue = new Point();
        displayVirusBlue.x = (int)(magnifyingGlassContainer.x + (VIRUS_CONTAINER.width * .57) - (DISPLAY_VIRUS_BLUE_ALIVE_1.width / 2));
        displayVirusBlue.y = (int)(magnifyingGlassContainer.y + (VIRUS_CONTAINER.height * .11));
        
        displayVirusRed = new Point();
        displayVirusRed.x = (int)(magnifyingGlassContainer.x + (VIRUS_CONTAINER.width * .37) - (DISPLAY_VIRUS_BLUE_ALIVE_1.width / 2));
        displayVirusRed.y = (int)(magnifyingGlassContainer.y + (VIRUS_CONTAINER.height * .45));
        
        displayVirusYellow = new Point();
        displayVirusYellow.x = (int)(magnifyingGlassContainer.x + (VIRUS_CONTAINER.width * .74) - (DISPLAY_VIRUS_BLUE_ALIVE_1.width / 2));
        displayVirusYellow.y = (int)(magnifyingGlassContainer.y + (VIRUS_CONTAINER.height * .45));
        
        informationContainer1 = new Point();
        informationContainer1.x = (int)(getWidth()  * .05);
        informationContainer1.y = (int)(getHeight() * .15);
        
        informationContainer2 = new Point();
        informationContainer2.x = (int)(getWidth()  * .68);
        informationContainer2.y = (int)(getHeight() * .48);
        
        statusLocation = new Point();
        statusLocation.x = (int)(informationContainer1.x + (INFORMATION_CONTAINER_1.width / 2) - (TEXT_HUMAN.width / 2));
        statusLocation.y = (int)(informationContainer1.y + (INFORMATION_CONTAINER_1.height * .8));
        
        scoreLocation = new Point();
        scoreLocation.x = (int)(informationContainer1.x + (INFORMATION_CONTAINER_1.width * .21));
        scoreLocation.y = (int)(informationContainer1.y + (INFORMATION_CONTAINER_1.height * .3));
        
        speedLocation = new Point();
        speedLocation.x = (int)(informationContainer2.x + (INFORMATION_CONTAINER_2.width / 2) - (TEXT_MED.width / 2));
        speedLocation.y = (int)(informationContainer2.y + (INFORMATION_CONTAINER_2.height * .53));
        
        virusCountLocation = new Point();
        virusCountLocation.x = (int)(informationContainer2.x + (INFORMATION_CONTAINER_2.width / 2) - NUMBER_0.width);
        virusCountLocation.y = (int)(informationContainer2.y + (INFORMATION_CONTAINER_2.height * .76));
        
        levelLocation = new Point();
        levelLocation.x = (int)(informationContainer2.x + (INFORMATION_CONTAINER_2.width / 2) - (NUMBER_0.width/2));
        levelLocation.y = (int)(informationContainer2.y + (INFORMATION_CONTAINER_2.height * .25));
        
        timeLocation = new Point();
        timeLocation.x = (int)(informationContainer1.x + (INFORMATION_CONTAINER_1.width * .25));
        timeLocation.y = (int)(informationContainer1.y + (INFORMATION_CONTAINER_1.height * .6));
        
        marioContainer = new Point();
        marioContainer.x = (int)(getWidth()  * .7);
        marioContainer.y = (int)(getHeight() * .18);
        
        drMario = new Point();
        drMario.x = (int)(marioContainer.x + (MARIO_CONTAINER.width  * .15));
        drMario.y = (int)(marioContainer.y + (MARIO_CONTAINER.height * .21));
        
        gameLogo = new Point();
        gameLogo.x = (int)(getWidth()  * .35);
        gameLogo.y = (int)(getHeight() * .03);
    }
    
    /**
     * Set the time to be displayed
     * @param timeDesc 
     */
    protected void setTimeDesc(final String timeDesc)
    {
        this.timeDesc = timeDesc;
    }
    
    /**
     * Set a random background
     * @param random Random number generator object
     */
    protected void setBackground(final Random random)
    {
        this.background = BackgroundKey.values()[random.nextInt(BackgroundKey.values().length)];
    }

    /**
     * Sets the appropriate location/dimensions for the board
     * @param board 
     */
    protected void setBoardLocation(final Board board, final int width)
    {
        board.setLocation((width / 2) - (BOARD_WIDTH / 2), pillContainerDestination.y + ((Block.HEIGHT-1) * 5));
        board.setDimensions(BOARD_WIDTH, BOARD_HEIGHT);
    }
    
    protected void setBlueHurt()
    {
        timers.reset(AnimationKey.DisplayHurtBlue);
        this.displayBlueVirusHurt = true;
    }
    
    protected void setRedHurt()
    {
        timers.reset(AnimationKey.DisplayHurtRed);
        this.displayRedVirusHurt = true;
    }
    
    protected void setYellowHurt()
    {
        timers.reset(AnimationKey.DisplayHurtYellow);
        this.displayYellowVirusHurt = true;
    }
    
    protected void setBlueDisplay(final boolean result)
    {
        this.displayBlueVirus = result;
    }
    
    protected void setRedDisplay(final boolean result)
    {
        this.displayRedVirus = result;
    }
    
    protected void setYellowDisplay(final boolean result)
    {
        this.displayYellowVirus = result;
    }
    
    protected void resetPillThrow()
    {
        this.displayPillThrow = true;
    }
    
    protected boolean hasDisplayPillThrow()
    {
        return this.displayPillThrow;
    }
    
    protected Point getMarioLocation()
    {
        return this.drMario;
    }
    
    protected void setWin()
    {
        showSuccess = true;
    }
    
    protected void setLose()
    {
        showGameover = true;
    }
    
    protected void resetWin()
    {
        showSuccess = false;
    }
    
    protected void resetLose()
    {
        showGameover = false;
    }
    
    protected void setDisplayGameOver()
    {
        displayGameover = true;
    }
    
    protected void resetDisplayGameOver()
    {
        displayGameover = false;
    }
    
    public void setHuman(final boolean result)
    {
        human = result;
    }
    
    public void setSpeed(final SpeedKey speed)
    {
        this.speed = speed;
    }
    
    protected void setScore(final int score)
    {
        this.score = score;
    }
    
    protected void setVirusCount(final int count)
    {
        this.count = count;
    }
    
    public void setLevel(final int level)
    {
        this.level = level;
    }
    
    protected void update(final Engine engine) throws Exception
    {
        if (timers == null)
        {
            timers = new TimerCollection(engine.getMain().getTime());
            timers.add(AnimationKey.DisplayHurtRed,    DURATION_DELAY_VIRUS_HURT);
            timers.add(AnimationKey.DisplayHurtBlue,   DURATION_DELAY_VIRUS_HURT);
            timers.add(AnimationKey.DisplayHurtYellow, DURATION_DELAY_VIRUS_HURT);
        }
        else
        {
            timers.update();
            
            if (timers.hasTimePassed(AnimationKey.DisplayHurtRed) && displayRedVirusHurt)
                displayRedVirusHurt = !displayRedVirusHurt;
            if (timers.hasTimePassed(AnimationKey.DisplayHurtBlue) && displayBlueVirusHurt)
                displayBlueVirusHurt = !displayBlueVirusHurt;
            if (timers.hasTimePassed(AnimationKey.DisplayHurtYellow) && displayYellowVirusHurt)
                displayYellowVirusHurt = !displayYellowVirusHurt;
        }
     
        //update animations
        if (displayBlueVirus)
            animations.get(AnimationKey.DisplayAliveBlue).update(engine.getMain().getTime());
        if (displayRedVirus)
            animations.get(AnimationKey.DisplayAliveRed).update(engine.getMain().getTime());
        if (displayYellowVirus)
            animations.get(AnimationKey.DisplayAliveYellow).update(engine.getMain().getTime());
            
        if (displayBlueVirusHurt)
            animations.get(AnimationKey.DisplayHurtBlue).update(engine.getMain().getTime());
        if (displayRedVirusHurt)
            animations.get(AnimationKey.DisplayHurtRed).update(engine.getMain().getTime());
        if (displayYellowVirusHurt)
            animations.get(AnimationKey.DisplayHurtYellow).update(engine.getMain().getTime());
        
        if (displayPillThrow)
        {
            animations.get(AnimationKey.MarioPillThrow).update(engine.getMain().getTime());
            
            if (animations.get(AnimationKey.MarioPillThrow).hasFinished())
            {
                displayPillThrow = !displayPillThrow;
                animations.get(AnimationKey.MarioPillThrow).reset();
            }
        }
        
        if (displayGameover)
            animations.get(AnimationKey.MarioGameOver).update(engine.getMain().getTime());
    }
    
    protected void render(final Graphics graphics)
    {
        switch (background)
        {
            case Background1:
                //draw background
                drawImage(graphics, getImage(), BACKGROUND_1, new Point(0,0));

                //draw pill jar
                drawImage(graphics, getImage(), PILL_CONTAINER_1, pillContainerDestination);
                break;
                
            case Background2:
                //draw background
                drawImage(graphics, getImage(), BACKGROUND_2, new Point(0,0));

                //draw pill jar
                drawImage(graphics, getImage(), PILL_CONTAINER_2, pillContainerDestination);
                break;
                
            case Background3:
                //draw background
                drawImage(graphics, getImage(), BACKGROUND_3, new Point(0,0));

                //draw pill jar
                drawImage(graphics, getImage(), PILL_CONTAINER_3, pillContainerDestination);
                break;
        }
        
        //draw magnifying glass
        drawImage(graphics, getImage(), VIRUS_CONTAINER, magnifyingGlassContainer);
        
        if (displayBlueVirus)
        {
            if (displayBlueVirusHurt)
            {
                //draw blue display virus hurt
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayHurtBlue).getLocation(), displayVirusBlue);
            }
            else
            {
                //draw blue display virus alive
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayAliveBlue).getLocation(), displayVirusBlue);
            }
        }
        
        if (displayRedVirus)
        {
            if (displayRedVirusHurt)
            {
                //draw red display virus hurt
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayHurtRed).getLocation(), displayVirusRed);
            }
            else
            {
                //draw red display virus alive
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayAliveRed).getLocation(), displayVirusRed);
            }
        }
        
        if (displayYellowVirus)
        {
            if (displayYellowVirusHurt)
            {
                //draw yellow display virus hurt
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayHurtYellow).getLocation(), displayVirusYellow);
            }
            else
            {
                //draw yellow display virus alive
                drawImage(graphics, getImage(), animations.get(AnimationKey.DisplayAliveYellow).getLocation(), displayVirusYellow);
            }
        }
        
        //draw score and timer, and player status (human, cpu)
        drawImage(graphics, getImage(), INFORMATION_CONTAINER_1, informationContainer1);
        
        //draw level, speed and virus count
        drawImage(graphics, getImage(), INFORMATION_CONTAINER_2, informationContainer2);
        
        //draw container mario will be in
        drawImage(graphics, getImage(), MARIO_CONTAINER, marioContainer);
        
        //draw logo
        drawImage(graphics, getImage(), LOGO, gameLogo);
        
        if (!displayGameover)
        {
            //draw mario himself
            drawImage(graphics, getImage(), animations.get(AnimationKey.MarioPillThrow).getLocation(), drMario);
        }
        else
        {
            //draw mario himself
            drawImage(graphics, getImage(), animations.get(AnimationKey.MarioGameOver).getLocation(), drMario);
        }
        
        if (showGameover)
            drawImage(graphics, getImage(), LOSE_NOTIFICATION, finishLocation);
        
        if (showSuccess)
            drawImage(graphics, getImage(), WIN_NOTIFICATION, finishLocation);
        
        if (human)
        {
            drawImage(graphics, getImage(), TEXT_HUMAN, statusLocation);
        }
        else
        {
            drawImage(graphics, getImage(), TEXT_CPU, statusLocation);
        }
        
        switch(speed)
        {
            case Low:
                drawImage(graphics, getImage(), TEXT_LOW, speedLocation);
                break;
                
            case Medium:
                drawImage(graphics, getImage(), TEXT_MED, speedLocation);
                break;
                
            case High:
                drawImage(graphics, getImage(), TEXT_HI, speedLocation);
                break;
        }
        
        //draw score
        drawNumberDescription(graphics, getImage(), Integer.toString(score), scoreLocation);
        
        //draw virus count
        drawNumberDescription(graphics, getImage(), Integer.toString(count), virusCountLocation);
        
        //draw level number
        drawNumberDescription(graphics, getImage(), Integer.toString(level), levelLocation);
        
        //draw timer
        drawNumberDescription(graphics, getImage(), timeDesc, timeLocation);
    }
    
    private void drawNumberDescription(final Graphics graphics, final Image image, final String desc, Point start)
    {
        final Point drawLocation = new Point(start);
        
        for (int i=0; i < desc.trim().length(); i++)
        {
            Rectangle tmp = getTextLocation(desc.trim().substring(i, i + 1));
            
            //draw number
            drawImage(graphics, image, tmp, drawLocation);
            
            drawLocation.x += tmp.width + 1;
        }
    }
    
    private Rectangle getTextLocation(final String display)
    {
        if (display.trim().equals("0"))
            return NUMBER_0;
        if (display.trim().equals("1"))
            return NUMBER_1;
        if (display.trim().equals("2"))
            return NUMBER_2;
        if (display.trim().equals("3"))
            return NUMBER_3;
        if (display.trim().equals("4"))
            return NUMBER_4;
        if (display.trim().equals("5"))
            return NUMBER_5;
        if (display.trim().equals("6"))
            return NUMBER_6;
        if (display.trim().equals("7"))
            return NUMBER_7;
        if (display.trim().equals("8"))
            return NUMBER_8;
        if (display.trim().equals("9"))
            return NUMBER_9;
        if (display.trim().equals(":"))
            return COLON_TEXT;
        if (display.trim().equals("."))
            return PERIOD_TEXT;
        
        return null;
    }
    
    private void drawImage(final Graphics graphics, final Image image, final Rectangle source, final Point destination)
    {
        graphics.drawImage(image, 
            destination.x, 
            destination.y, 
            destination.x + source.width, 
            destination.y + source.height, 
            source.x, 
            source.y, 
            source.x + source.width, 
            source.y + source.height, 
            null);
    }
}