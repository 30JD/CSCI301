package cs301.cs.wm.edu.jundaan.falstad;

import cs301.cs.wm.edu.jundaan.generation.CardinalDirection;
import cs301.cs.wm.edu.jundaan.falstad.Constants.UserInput;

/**
 * 
 * @author Junda An
 *
 */
public class BasicRobot implements Robot{
	//fields used by BasicRobot
	private StatePlaying maze;
	private int battery; //amount of charge in the robot
	private int odometer; //distance travelled by the robot
	
	//the robot's sensor
	private boolean rightSensor;
	private boolean leftSensor;
	private boolean forwardSensor;
	private boolean backwardSensor;
	private boolean roomSensor;
	private boolean moving;
	
	//cost of robot's actions
	private static int costToSenseDist = 1;
	private static int costToRotate = 3;
	private static int costToMove = 5;
	
	
	//construct a robot on default
	public BasicRobot() {
		//initialize the battery level to 3000 and odometer to 0;
		setBatteryLevel(3000);
		resetOdometer();
		
		//Set all sensors to true
		rightSensor = true;
		leftSensor = true;
		forwardSensor = true;
		backwardSensor = true;
		roomSensor = true;
		moving = true;
		
	}
	
	//construct a robot that cooperates with a given controller
	public BasicRobot(StatePlaying maze) {
		//initialize the battery level to 3000 and odometer to 0;
		setBatteryLevel(3000);
		resetOdometer();
				
		//Set all sensors to true
		rightSensor = true;
		leftSensor = true;
		forwardSensor = true;
		backwardSensor = true;
		roomSensor = true;
		moving = true;
		
		//Set controller
		setMaze(maze);
	}
	
	/** if the robot does not have enough energy, it will stop.
	 * Otherwise, it will rotate according to the input.
	 */
	public void rotate(Turn turn) {
		//stop rotating if the robot does not have enough energy

		switch(turn) {
		case LEFT:
			if(battery >= costToRotate) {
				//System.out.println("Rotate(left) method is called")
				maze.keyDown(UserInput.Left, 1);
				setBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/4);
			}
			
			else {
				moving = false;
			}

			break;
		
		case RIGHT:
			if(battery >= costToRotate) {
				//System.out.println("Rotate(right) method is called")
				maze.keyDown(UserInput.Right, 1);
				setBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/4);
			}
			else {
				moving = false;
			}

			break;
			
		case AROUND:
			if(battery >= costToRotate*2) {
				System.out.println("Rotate(around) method is called");
				maze.keyDown(UserInput.Right, 1);
				maze.keyDown(UserInput.Right, 1);
				setBatteryLevel(getBatteryLevel() - getEnergyForFullRotation()/2);
			}
			
			else {
				moving = false;
			}
			break;
		}
	}

	/** if the robot does not have enough energy, it will stop
	 * Otherwise, it will move.
	 */
	public void move(int distance, boolean manual) {
		int[] currentPosition = maze.getCurrentPosition();
		CardinalDirection currentDirection = maze.getCurrentDirection();
		System.out.println("The move method in BasicRobot is called");
				
		if(manual) {
			while(distance > 0) {
				//if the battery is not enough, the robot stops
				if(battery < getEnergyForStepForward() || hasStopped()) {
					moving = false;
					System.out.println("Ran out of energy");
					break;
				}
				
				//if there is a wall, the robot stops
				if(maze.getMazeConfiguration().getMazecells().hasWall(currentPosition[0], currentPosition[1], currentDirection)) {
					System.out.println("Wall encountered");
					break;
				}
				
				//if the robot can move
				else {
					maze.keyDown(UserInput.Up, 1);
					setBatteryLevel(getBatteryLevel() - getEnergyForStepForward());
					distance -= 1;
					odometer += 1;
				}
				
			}
		}
		
		else {
			while(distance > 0) {
				//if the battery is not enough, the robot stops
				if(battery < getEnergyForStepForward() || hasStopped()) {
					moving = false;
					System.out.println("Ran out of energy");
					break;
				}
				
				//if there is a wall, the robot stops
				if(maze.getMazeConfiguration().getMazecells().hasWall(currentPosition[0], currentPosition[1], currentDirection)) {
					moving = false;
					System.out.println("Wall encountered");
					break;
				}
				
				//if the robot can move
				else {
					maze.keyDown(UserInput.Up, 1);
					setBatteryLevel(getBatteryLevel() - getEnergyForStepForward());
					distance -= 1;
					odometer += 1;
				}
				
			}
		}
	}

	/** If the position is out of maze, throw an exception
	 * Otherwise, use controller.getCurrentPosition() to return the current position which is an array.
	 */
	public int[] getCurrentPosition() throws Exception {
		//Sanity check the position
		int x = maze.getCurrentPosition()[0];
		int y = maze.getCurrentPosition()[1];
		if(!maze.getMazeConfiguration().isValidPosition(x, y)) {
			throw new Exception();
		}
		
		return maze.getCurrentPosition();
	}

	/**
	 * Provides the robot with a reference to the controller to cooperate with.
	 */
	public void setMaze(StatePlaying maze) {
		if(maze != null) {
			this.maze = maze;
		}
	}

	/** tell the robot is at the exit
	 * if yes, return true. Otherwise, return false.
	 */
	public boolean isAtExit() {
		int x = maze.getCurrentPosition()[0];
		int y = maze.getCurrentPosition()[1];
	    //check if the robot is at exit
		if(maze.getMazeConfiguration().getMazecells().isExitPosition(x, y)) {
			return true;
		}
		
		else {
			return false;
		}
	}

	/**
	 * Tells if a sensor can identify the exit in given direction relative to 
	 * the robot's current forward direction from the current position.
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 */
	public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {
		if(!hasDistanceSensor(direction)) {
			throw new UnsupportedOperationException();
		}
		
		if(getBatteryLevel() < costToSenseDist) {
			moving = false;
		}
		setBatteryLevel(getBatteryLevel() - costToSenseDist);
		
	    return (distanceToObstacle(direction) == Integer.MAX_VALUE);
	}

	/** tell if the robot is in a room
	 * if yes, return true. Otherwise, return false
	 */
	
	public boolean isInsideRoom() throws UnsupportedOperationException {
		if(!roomSensor) {
			throw new UnsupportedOperationException();
		}
		
		int x = maze.getCurrentPosition()[0];
		int y = maze.getCurrentPosition()[1];
		if(maze.getMazeConfiguration().getMazecells().isInRoom(x, y)) {
			return true;
		}
		
		else {
			return false;
		}
	}

	/** tell whether the robot has a room sensor
	 * if yes, return true. Otherwise, return false
	 */
	public boolean hasRoomSensor() {
		return roomSensor;
	}

	/** tell the current direction of the robot, which is an array of size 2
	 * 
	 */
	public CardinalDirection getCurrentDirection() {
		return maze.getCurrentDirection();
	}

	/** tell the current battery level of the robot
	 * 
	 */
	public int getBatteryLevel() {
		return battery;
	}

	/** set the battery level of the robot
	 * 
	 */
	public void setBatteryLevel(int level) {
		battery = level;
	}

	/** tell the distance traveled by the robot
	 * 
	 */
	public int getOdometerReading() {
		return odometer;
	}

	/** reset the odometer to 0
	 * 
	 */
	public void resetOdometer() {
		odometer = 0;
	}

	/** tell the energy required for a full rotation
	 * 
	 */
	public int getEnergyForFullRotation() {
		// a full rotation involves 4 rotations
		return costToRotate*4;
	}

	/** tell the energy required to move forward
	 * 
	 */
	public int getEnergyForStepForward() {
		// costToMove
		return costToMove;
	}

	/** tell if the robot has stopped
	 * 
	 */
	public boolean hasStopped() {
		return !moving;
	}

	/** tell the distance to the obstacle the robot is now facing
	 * 
	 */
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		//store the current position of the robot
		int x = maze.getCurrentPosition()[0];
		int y = maze.getCurrentPosition()[1];
		
		//Store the distance to obstacle
		int distToObstacle = 0;
		
		//Throw an exception if the robot does not have a sensor in the given direction
		if(!hasDistanceSensor(direction)) {
			throw new UnsupportedOperationException();
		}
		
		//Check if there is enough battery to sense distance
		if(battery < costToSenseDist) {
			moving = false;
		}
		
		CardinalDirection dir = null;
		//get the direction of the robot after rotation
		switch(direction) {
		case LEFT:
			dir = maze.getCurrentDirection().rotateClockwise();
			break;
			
		case RIGHT:
			dir = maze.getCurrentDirection().oppositeDirection().rotateClockwise();
			break;
		
		case FORWARD:
			dir = maze.getCurrentDirection();
			break;
		
		case BACKWARD:
			dir = maze.getCurrentDirection().oppositeDirection();
			break;
		}
		
		while(maze.getMazeConfiguration().getMazecells().hasNoWall(x, y, dir)) {
			distToObstacle += 1;
			
			if(dir == CardinalDirection.North) {
				y --;
			}
			
			else if(dir == CardinalDirection.South) {
				y ++;
			}
			
			else if(dir == CardinalDirection.East) {
				x --;
			}
			
			else {
				x ++;
			}
			//if the robot is going outside of the maze
			if(x <= 0 || y <= 0 || x >= maze.getMazeConfiguration().getWidth() || y >= maze.getMazeConfiguration().getHeight()) {
				return Integer.MAX_VALUE;
			}
		}
		setBatteryLevel(getBatteryLevel()-costToSenseDist);
		return distToObstacle;
	}

	/** tell if the robot has a distance sensor for a given direction
	 * if yes, return true. Otherwise, return false
	 */
	public boolean hasDistanceSensor(Direction direction) {
		if(direction == Direction.FORWARD) {
			return forwardSensor;
		}
		
		else if(direction == Direction.BACKWARD) {
			return backwardSensor;
		}
		
		else if(direction == Direction.RIGHT) {
			return rightSensor;
		}
		
		else {
			return leftSensor;
		}
	}
	
	public StatePlaying getStatePlaying() {
		return maze;
	}
	
}
