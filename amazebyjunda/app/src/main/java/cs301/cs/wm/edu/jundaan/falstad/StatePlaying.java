package cs301.cs.wm.edu.jundaan.falstad;

import cs301.cs.wm.edu.jundaan.falstad.Constants.UserInput;

import cs301.cs.wm.edu.jundaan.generation.CardinalDirection;
import cs301.cs.wm.edu.jundaan.generation.Cells;
import cs301.cs.wm.edu.jundaan.generation.MazeConfiguration;
import cs301.cs.wm.edu.jundaan.gui.PlayAnimationActivity;
import cs301.cs.wm.edu.jundaan.gui.PlayManuallyActivity;
import cs301.cs.wm.edu.jundaan.generation.MazeBuilder;

/**
 * Class handles the user interaction
 * while the game is in the third stage
 * where the user plays the game.
 * This class is part of a state pattern for the
 * Controller class.
 * 
 * It implements a state-dependent behavior that controls
 * the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt
 * with a key listener (SimpleKeyListener)
 * and then handed over to a Controller object
 * by way of the keyDown method.
 * 
 * Responsibilities:
 * Show the first person view and the map view,
 * Accept input for manual operation (left, right, up, down etc),  
 * Update the graphics, recognize termination.
 *
 * This code is refactored code from Maze.java by Paul Falstad, 
 * www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class StatePlaying extends DefaultState {
	FirstPersonDrawer firstPersonView;
	MapDrawer mapView;
    MazePanel panel;

    MazeConfiguration mazeConfig ;
    PlayManuallyActivity playManuallyActivity;
    PlayAnimationActivity playAnimationActivity;
    
    private boolean showMaze;           // toggle switch to show overall maze on screen
    private boolean showSolution;       // toggle switch to show solution in overall maze on screen
    private boolean mapMode; // true: display map of maze, false: do not display map of maze
    // mapMode is toggled by user keyboard input, causes a call to drawMap during play mode

    // current position and direction with regard to MazeConfiguration
    int px, py ; // current position on maze grid (x,y)
    int dx, dy;  // current direction
    
    int angle; // current viewing angle, east == 0 degrees
    int walkStep; // counter for intermediate steps within a single step forward or backward
    Cells seenCells; // a matrix with cells to memorize which cells are visible from the current point of view
    // the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map
    
    // debug stuff
    //private boolean deepdebug = false;
    //private boolean allVisible = false;
    //private boolean newGame = false;

    Robot robot;
    RobotDriver driver;

    public void setRobotAndDriver(Robot robot, RobotDriver robotdriver) {
        this.robot = robot;
        this.driver = robotdriver;

    }

    public Robot getRobot() {
        return robot;
    }
    /**
     * @return the driver, may be null
     */
    public RobotDriver getDriver() {
        return driver;
    }

    boolean started;
    
    public StatePlaying() {
        started = false;
        playManuallyActivity = null;
        playAnimationActivity = null;
    }

    public void setPlayManuallyActivity(PlayManuallyActivity manual){
        playManuallyActivity = manual;
        System.out.print("dsb007");
    }

    public void setPlayAnimationActivity(PlayAnimationActivity animation){
        playAnimationActivity = animation;
    }
    @Override
    public void setMazeConfiguration(MazeConfiguration config) {
        mazeConfig = config;
    }

    public void setMazePanel(MazePanel mazePanel){
        panel = mazePanel;
    }

    public MazePanel getPanel() {
        return panel;
    }

    /**
     * Start the actual game play by showing the playing screen.
     * If the panel is null, all drawing operations are skipped.
     * This mode of operation is useful for testing purposes, 
     * i.e., a dryrun of the game without the graphics part.
     * @param panel is part of the UI and visible on the screen, needed for drawing
     */
    public void start(MazePanel panel) {
        started = true;
        // keep the reference to the controller to be able to call method to switch the state
        //control = controller;
        // keep the reference to the panel for drawing
        this.panel = panel;

        /*
        if(this.getDriver() != RobotAlgo.ManualDriver) {
        	if(controller.getRobotAlgo() == RobotAlgo.Wizard) {
        		robotDriver = new Wizard();
        	}
        	
        	else if(controller.getRobotAlgo() == RobotAlgo.WallFollower) {
        		robotDriver = new WallFollower();
        	}
        	
        	else if(controller.getRobotAlgo() == RobotAlgo.Explorer) {
        		robotDriver = new Explorer();
        	}
        	
        	else if(controller.getRobotAlgo() == RobotAlgo.Pledge) {
        		robotDriver = new Pledge();
        	}
        	BasicRobot robot = new BasicRobot();
        	robot.setMaze(controller);
        	robotDriver.setRobot(robot);
        	controller.setRobotAndDriver(robot, robotDriver);
        }
        */
        //
        // adjust internal state of maze model
        // visibility settings
        showMaze = false ;
        showSolution = false ;
        mapMode = false;
        // init data structure for visible walls
        seenCells = new Cells(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
        // set the current position and direction consistently with the viewing direction
        setPositionDirectionViewingDirection();
        walkStep = 0; // counts incremental steps during move/rotate operation
    
        if (panel != null) {
        	startDrawer();
        }
        else {
        	// else: dry-run without graphics, most likely for testing purposes
        	printWarning();
        }
        /*
        if(control.getRobotAlgo() != RobotAlgo.ManualDriver) {
        	try {
				robotDriver.drive2Exit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } */
    }
    /**
     * Initializes the drawer for the first person view
     * and the map view and then draws the initial screen
     * for this state.
     */
	protected void startDrawer() {
		firstPersonView = new FirstPersonDrawer(Constants.VIEW_WIDTH,
				Constants.VIEW_HEIGHT, Constants.MAP_UNIT,
				Constants.STEP_SIZE, seenCells, mazeConfig.getRootnode()) ;
		mapView = new MapDrawer(seenCells, 60, mazeConfig) ;
		System.out.print("hh");
		// draw the initial screen for this state
		draw();
	}
    /**
     * Internal method to set the current position, the direction
     * and the viewing direction to values consistent with the 
     * given maze.
     */
	private void setPositionDirectionViewingDirection() {
		// obtain starting position
        int[] start = mazeConfig.getStartingPosition() ;

        setCurrentPosition(start[0],start[1]) ;
        // set current view direction and angle
        angle = 0; // angle matches with east direction, 
        // hidden consistency constraint!
        setDirectionToMatchCurrentAngle();
        // initial direction is east, check this for sanity:
        assert(dx == 1);
        assert(dy == 0);
	}
 

    /**
     * Method incorporates all reactions to keyboard input in original code, 
     * The simple key listener calls this method to communicate input.
     * Method requires {@link #start(MazePanel) start} to be
     * called before.
     * @param key provides the feature the user selected
     * @param value is not used, exists only for consistency across State classes
     * @return false if not started yet otherwise true
     */

    public boolean keyDown(UserInput key, int value) {
        if (!started)
            return false;

        // react to input for directions and interrupt signal (ESCAPE key)  
        // react to input for displaying a map of the current path or of the overall maze (on/off toggle switch)
        // react to input to display solution (on/off toggle switch)
        // react to input to increase/reduce map scale
        switch (key) {
        case Start: // misplaced, do nothing
            break;
        case Up: // move forward
            System.out.print("ZAH" + this.getCurrentPosition()[0] + this.getCurrentPosition()[1]);
            if(!robot.hasStopped()) {
                walk(1);
            }

            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
                System.out.print("gwz");
                if(playManuallyActivity != null){
                    toFinish(true, true);
                }

                else {
                    toFinish(true, false);
                }
            }
            else if(robot.hasStopped()){
                if(playManuallyActivity != null){
                    toFinish(false, true);
                }
                else {
                    toFinish(false, false);
                }
            }
            break;
        case Left: // turn left
            if(!robot.hasStopped()) {
                rotate(1);
            }
            if(this.robot.hasStopped()) {
                if(playManuallyActivity != null){
                    toFinish(false, true);
                }
                else {
                    toFinish(false, false);
                }
        	}
            break;
        case Right: // turn right
            if(!robot.hasStopped()) {
                rotate(-1);
            }
            if(this.robot.hasStopped()) {
                if(playManuallyActivity != null){
                    toFinish(false, true);
                }
                else {
                    toFinish(false, false);
                }
        	}
            break;
        case Down: // move backward
            if(!robot.hasStopped()) {
                walk(-1);
            }

            // check termination, did we leave the maze?
            if (isOutside(px,py)) {
                System.out.print("gbxx");
                if(playManuallyActivity != null){
                    toFinish(true, true);
                }
                else {
                    toFinish(true, false);
                }
            }

            else if(robot.hasStopped()){
                if(playManuallyActivity != null){
                    toFinish(false, true);
                }
                else {
                    toFinish(false, false);
                }
            }
            break;
        //case ReturnToTitle: // escape to title screen
            //this.switchToTitle();
            //break;
        case Jump: // make a step forward even through a wall
            // go to position if within maze
            if (mazeConfig.isValidPosition(px + dx, py + dy)) {
                setCurrentPosition(px + dx, py + dy) ;
                draw() ;
            }
            break;
        case ToggleLocalMap: // show local information: current position and visible walls
            // precondition for showMaze and showSolution to be effective
            // acts as a toggle switch
            mapMode = !mapMode;         
            draw() ; 
            break;
        case ToggleFullMap: // show the whole maze
            // acts as a toggle switch
            showMaze = !showMaze;       
            draw() ; 
            break;
        case ToggleSolution: // show the solution as a yellow line towards the exit
            // acts as a toggle switch
            showSolution = !showSolution;       
            draw() ;
            break;
        case ZoomIn: // zoom into map
        	adjustMapScale(true);
            draw() ;
            break ;
        case ZoomOut: // zoom out of map
            adjustMapScale(false);
            draw() ; 
            break ;
        } // end of internal switch statement for playing state
        return true;
    }

    protected void toFinish(boolean win, boolean manual){
        if(manual){
            if(win) {
                playManuallyActivity.go2Winning();
            }
            else{
                playManuallyActivity.go2Losing();
            }
        }

        else{
            System.out.print("gzd");
            if(win){
                playAnimationActivity.go2Winning();
            }
            else{
                playAnimationActivity.go2Losing();
            }
        }
    }
    /**
     * Draws the current content on panel to show it on screen.
     */
    protected void draw() {
    	if (panel == null) {
    		printWarning();
    		return;
    	}
    	// draw the first person view and the map view if wanted
    	firstPersonView.draw(panel, px, py, walkStep, angle) ;
        if (isInMapMode()) {
			mapView.draw(panel, px, py, angle, walkStep,
					isInShowMazeMode(),isInShowSolutionMode()) ;
		}
		// update the screen with the buffer graphics
        System.out.print("update");

        panel.update() ;
    }
    /**
     * Adjusts the internal map scale setting for the map view.
     * @param increment if true increase, otherwise decrease scale for map
     */
    private void adjustMapScale(boolean increment) {
    	if (increment) {
    		mapView.incrementMapScale() ;
    	}
    	else {
    		mapView.decrementMapScale() ;
    	}
    }
    /**
     * Prints the warning about a missing panel only once
     */
    boolean printedWarning = false;
    protected void printWarning() {
    	if (printedWarning)
    		return;
    	System.out.println("StatePlaying.start: warning: no panel, dry-run game without graphics!");
    	printedWarning = true;
    }
    ////////////////////////////// set methods ///////////////////////////////////////////////////////////////
    ////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
    protected void setCurrentPosition(int x, int y) {
        px = x ;
        py = y ;
    }
    private void setCurrentDirection(int x, int y) {
        dx = x ;
        dy = y ;
    }
    /**
     * Sets fields dx and dy to be consistent with
     * current setting of field angle.
     */
    private void setDirectionToMatchCurrentAngle() {
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
	}

    ////////////////////////////// get methods ///////////////////////////////////////////////////////////////
    protected int[] getCurrentPosition() {
        int[] result = new int[2];
        result[0] = px;
        result[1] = py;
        return result;
    }
    protected CardinalDirection getCurrentDirection() {
        return CardinalDirection.getDirection(dx, dy);
    }
    boolean isInMapMode() { 
        return mapMode ; 
    } 
    boolean isInShowMazeMode() { 
        return showMaze ; 
    } 
    boolean isInShowSolutionMode() { 
        return showSolution ; 
    } 
    public MazeConfiguration getMazeConfiguration() {
        return mazeConfig ;
    }
    //////////////////////// Methods for move and rotate operations ///////////////
    final double radify(int x) {
        return x*Math.PI/180;
    }
    /**
     * Helper method for walk()
     * @param dir
     * @return true if there is no wall in this direction
     */
    protected boolean checkMove(int dir) {
        CardinalDirection cd = null;
        switch (dir) {
        case 1: // forward
            cd = getCurrentDirection();
            break;
        case -1: // backward
            cd = getCurrentDirection().oppositeDirection();
            break;
        default:
            throw new RuntimeException("Unexpected direction value: " + dir);
        }
        return !mazeConfig.hasWall(px, py, cd);
    }
    /**
     * Draws and waits. Used to obtain a smooth appearance for rotate and move operations
     */
    private void slowedDownRedraw() {
        draw() ;
        try {
            Thread.sleep(25);
        } catch (Exception e) { 
        	// may happen if thread is interrupted
        	// no reason to do anything about it, ignore exception
        }
    }
 	
    /**
     * Performs a rotation with 4 intermediate views, 
     * updates the screen and the internal direction
     * @param dir for current direction, values are either 1 or -1
     */
    synchronized private void rotate(int dir) {
        final int originalAngle = angle;
        final int steps = 4;

        for (int i = 0; i != steps; i++) {
            // add 1/4 of 90 degrees per step 
            // if dir is -1 then subtract instead of addition
            angle = originalAngle + dir*(90*(i+1))/steps; 
            angle = (angle+1800) % 360;
            // draw method is called and uses angle field for direction
            // information.
			slowedDownRedraw();
        }
        // update maze direction only after intermediate steps are done
        // because choice of direction values are more limited.
        setDirectionToMatchCurrentAngle();
        //logPosition(); // debugging
    }
	
    /**
     * Moves in the given direction with 4 intermediate steps,
     * updates the screen and the internal position
     * @param dir, only possible values are 1 (forward) and -1 (backward)
     */
    synchronized private void walk(int dir) {
    	// check if there is a wall in the way
        if (!checkMove(dir))
            return;
        // walkStep is a parameter of FirstPersonDrawer.draw()
        // it is used there for scaling steps
        // so walkStep is implicitly used in slowedDownRedraw
        // which triggers the draw operation in 
        // FirstPersonDrawer and MapDrawer
        for (int step = 0; step != 4; step++) {
            walkStep += dir;
            slowedDownRedraw();
        }
        setCurrentPosition(px + dir*dx, py + dir*dy) ;
        walkStep = 0; // reset counter for next time
        //logPosition(); // debugging
    }

    /**
     * Checks if the given position is outside the maze
     * @param x coordinate of position
     * @param y coordinate of position
     * @return true if position is outside, false otherwise
     */
    private boolean isOutside(int x, int y) {
        return !mazeConfig.isValidPosition(x, y) ;
    }
    /////////////////////// Methods for debugging ////////////////////////////////
    /*
    private void dbg(String str) {
        //System.out.println(str);
    }
    
    private void logPosition() {
        if (!deepdebug)
            return;
        dbg("x="+viewx/Constants.MAP_UNIT+" ("+
                viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
                angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
    }
    */
}



