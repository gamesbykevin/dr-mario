package com.gamesbykevin.drmario.player;

import com.gamesbykevin.drmario.block.Block;
import com.gamesbykevin.drmario.block.Block.Type;
import com.gamesbykevin.drmario.block.Pill;
import com.gamesbykevin.drmario.block.Pill.Rotation;
import com.gamesbykevin.drmario.board.Board;
import com.gamesbykevin.drmario.engine.Engine;
import com.gamesbykevin.drmario.shared.IElement;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Each player will have a game board and Pill
 * @author GOD
 */
public class Player extends PlayerInformation implements IElement
{
    //the board where the game play will occur
    private Board board;
    
    //pill piece
    private Pill pill, next;
    
    //the start location for the Pill
    protected static final Cell START = new Cell(3, 0);
    
    //other starting location for the pill
    protected static final Cell START1 = new Cell(4, 0);
    
    //our timer object to determine when the pieces should drop
    private Timer gravityTimer, gameTimer;
    
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
    
    //time delay for the differet speed(s)
    private static final long SPEED_LOW = TimerCollection.toNanoSeconds(1500L);
    private static final long SPEED_MED = TimerCollection.toNanoSeconds(500L);
    private static final long SPEED_HI = TimerCollection.toNanoSeconds(250L);
    
    //we will write all player objects etc... to this single image
    private BufferedImage playerImage;
    
    //where the buffered image will be drawn and the dimensions as well
    private final Rectangle renderLocation;
    
    /**
     * Create a new timer with the specified delay that will determine the duration
     * between Pill drops.
     * @param container the overall dimensions of this game
     * @param delay The time delay in milliseconds
     */
    public Player(final Rectangle renderLocation)
    {
        super();
        
        //create new random number generator
        this.random = new Random(SEED);
        
        //set a random background
        super.setBackground(random);
        
        //create our buffered image object
        playerImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        
        //the location/dimension(s) of the buffered image
        this.renderLocation = renderLocation;
    }
    
    /**
     * Create game timer, if we are counting down the time
     * @param time The time to countdown
     */
    public void createTimer(final long time)
    {
        this.gameTimer = new Timer(time);
    }
    
    public void createBoard(final int virusCount)
    {
        board = new Board(virusCount, SEED);
        
        //set the location/dimension
        super.setBoardLocation(board, (int)getWidth());
        
        //set the display virus count
        super.setVirusCount(virusCount);
    }
    
    public Board getBoard()
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
        super.update(engine);
        
        //if the player has lost or won no more updates are required
        if (hasLose() || hasWin())
            return;
        
        //update Timer as long as game hasn't ended
        updateTimer(engine.getMain().getTime());
        
        //check for matches on board etc...
        getBoard().update(engine);
        
        //check here if any viruses hurt so can change the display virus in PlayerInformation
        if (getBoard().hasDeadType(Type.BlueVirus))
            super.setBlueHurt();
        if (getBoard().hasDeadType(Type.RedVirus))
            super.setRedHurt();
        if (getBoard().hasDeadType(Type.YellowVirus))
            super.setYellowHurt();
        
        //check here if any viruses should be displayed at all in PlayerInformation
        super.setRedDisplay((getBoard().getCount(Type.RedVirus) > 0));
        super.setBlueDisplay((getBoard().getCount(Type.BlueVirus) > 0));
        super.setYellowDisplay((getBoard().getCount(Type.YellowVirus) > 0));
        
        //if we can't interact with the board due to a virus/pill match or pill drop etc..
        if (!getBoard().canInteract())
            return;
        
        //set the correct virus count
        super.setVirusCount(getBoard().getVirusCount());
        
        //if the entrance is blocked or in timed mode and time ran out
        if (hasEntranceBlocked() || hasTimePassed())
        {
            setLose();
            return;
        }
        
        //have we removed all of the viruses
        if (getBoard().getVirusCount() <= 0)
        {
            setWin();
            return;
        }
        
        //if our pill has not been created yet 
        if (getPill() == null)
        {
            //take the next Pill and assign as current
            createPill();
            
            //create a new Pill for the future
            createNextPill();
            
            //reset pill throw animation
            resetPillThrow();
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
    
    @Override
    public void setLose()
    {
        this.lose = true;
        super.setLose();
        super.setDisplayGameOver();
    }
    
    @Override
    public void setWin()
    {
        this.win = true;
        super.setWin();
    }
    
    /**
     * Are the two Cells where the pill starts already occupied in the Board
     * @return boolean
     */
    private boolean hasEntranceBlocked()
    {
        return (getBoard().getBlock(START) != null || getBoard().getBlock(START1) != null);
    }
    
    /**
     * If playing timed mode and time has run out
     * @return boolean
     */
    private boolean hasTimePassed()
    {
        //if we are counting down and the time has passed
        if (hasCountdown() && gameTimer.hasTimePassed())
        {
            gameTimer.setRemaining(0);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private void updateTimer(final long time)
    {
        //update game timer as long as game hasn't ended
        gameTimer.update(time);

        //are we counting down
        if (hasCountdown())
        {
            //set the time remaining
            super.setTimeDesc(gameTimer.getDescRemaining("mm:ss"));
        }
        else
        {
            //set the time passed
            super.setTimeDesc(gameTimer.getDescPassed("mm:ss"));
        }
    }
    
    public void resetStatus()
    {
        this.lose = false;
        this.win = false;
        
        super.resetLose();
        super.resetWin();
        
        super.resetDisplayGameOver();
        
        //create a new random background
        super.setBackground(random);
    }
    
    /**
     * Are we in timed mode
     * @return 
     */
    private boolean hasCountdown()
    {
        return (gameTimer.getReset() != 0);
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
    
    @Override
    public void setSpeed(final SpeedKey speedKey)
    {
        switch(speedKey)
        {
            case Low:
                this.gravityTimer = new Timer(SPEED_LOW);
                break;
                
            case Medium:
                this.gravityTimer = new Timer(SPEED_MED);
                break;
                
            case High:
                this.gravityTimer = new Timer(SPEED_HI);
                break;
        }
        
        //set speed for display purposes
        super.setSpeed(speedKey);
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
    
    public void createNextPill()
    {
        this.next = new Pill();
        
        //set random type(s)
        this.next.setRandom(random);
        
        //setup animations
        this.next.setup();
        
        //start location
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
    
    public BufferedImage getBufferedImage()
    {
        return this.playerImage;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //create buffered image
        this.render();
        
        //draw our buffered image
        graphics.drawImage(playerImage, renderLocation.x, renderLocation.y, renderLocation.width, renderLocation.height, null);
    }
    
    public void render()
    {
        Graphics bufferedGraphics = playerImage.createGraphics();
        
        //draw all player info etc...
        super.render(bufferedGraphics);
        
        if (!hasLose() && !hasWin())
        {
            //draw board
            renderBoard(bufferedGraphics);
        }
        
        if (!hasWin() && !hasLose())
        {
            //now draw player pill if it exists and we can interact with the board
            if (getPill() != null && getBoard().canInteract())
                getPill().render(bufferedGraphics, getImage());

            //draw next pill in upper right corner
            if (getNext() != null && !hasDisplayPillThrow())
            {
                getNext().setPosition(getMarioLocation().x - (Block.WIDTH * 2), getMarioLocation().y - Block.HEIGHT);
                getNext().render(bufferedGraphics, getImage());
            }
        }
    }
    
    private void renderBoard(final Graphics graphics)
    {
        for (int row=0; row < getBoard().getRows(); row++)
        {
            for (int col=0; col < getBoard().getCols(); col++)
            {
                //draw the blocks that exist
                if (getBoard().getBlock(col, row) != null)
                {
                    getBoard().getBlock(col, row).render(graphics, getImage());
                }
            }
        }
    }
}