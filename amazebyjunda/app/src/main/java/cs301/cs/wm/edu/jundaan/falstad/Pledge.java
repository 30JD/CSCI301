package cs301.cs.wm.edu.jundaan.falstad;
import cs301.cs.wm.edu.jundaan.generation.Distance;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Direction;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Turn;

public class Pledge implements RobotDriver{
	
	private BasicRobot robot;
	private int width;
	private int height;
	private Distance distance;
	
	//construct a default robot driver using pledge algorithm
	public Pledge() {
		robot = null;
		width = 0;
		height = 0;
		distance = null;
	}
	
	public BasicRobot getRobot() {
		return robot;
	}
	
	/**
	 * assign a robot platform to the driver.
	 */
	@Override
	public void setRobot(Robot r) {
		robot = (BasicRobot) r;
		
	}
	
	/**
	 * provide the robot driver with information on maze width and height
	 */
	@Override
	public void setDimensions(int w, int h) {
		width = w;
		height = h;
		
	}
	
	/**
	 * provide the robot driver with information on distance to the exit
	 */
	@Override
	public void setDistance(Distance distance) {
		distance = distance;
		
	}
	
	/* Apply pledge algorithm
	 * Every time the robot turns left, the counter decreases by 1. Every time the robot turns right, the counter increases by 1.
	 */
	private void pledgeal() throws Exception{
		//set counter to 0
		int counter = 0;
		//begin by turning left
		robot.rotate(Turn.LEFT);
		checkStopped();
		counter --;
		
		while(counter != 0 && !robot.isAtExit()) {
			//if there are walls on the right and in the front, make the robot turn left
			if(robot.distanceToObstacle(Direction.RIGHT) == 0 && robot.distanceToObstacle(Direction.FORWARD) == 0) {
				robot.rotate(Turn.LEFT);
				checkStopped();
				counter --;
			}
			
			//if there is no wall in the front, make the robot move one step forward
			else if(robot.distanceToObstacle(Direction.RIGHT) == 0 && robot.distanceToObstacle(Direction.FORWARD) != 0) {
				robot.move(1, false);
				checkStopped();
			}
			
			//if there is no wall on the right
			else {
				turnRightAndMove();
				checkStopped();
				counter ++;
			}
		}
	}
	
	/** raise exception if the robot stops
	 * if the robot successfully reaches the exit, return true. Otherwise, false.
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while(!robot.isAtExit()){
			checkStopped();
			
			//if there is a wall on the right
			if(robot.distanceToObstacle(Direction.RIGHT) == 0) {
				//if there is also a wall in the front
				if(robot.distanceToObstacle(Direction.FORWARD) == 0) {
					//enters the pledge
					pledgeal();
				}
				
				else {
					robot.move(1, false);
					checkStopped();
				}
			}
			
			else {
				turnRightAndMove();
				checkStopped();
			}
		}
		
		//the robot is now at the exit
		
		if(robot.canSeeExit(Direction.LEFT)) {
			turnLeftAndMove();
		}
		
		else if(robot.canSeeExit(Direction.RIGHT)) {
			turnRightAndMove();
		}
		
		else {
			robot.move(1, false);
		}
		
		return true;
	}
	
	/**
	 * Energy Consumption = initial battery level - current battery level
	 */
	@Override
	public float getEnergyConsumption() {
		float energyConsumption = 3000 - robot.getBatteryLevel();
		return energyConsumption;
	}
	
	/**
	 * return the total length of the journey in number of cells traversed.
	 */
	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}
	
	private void turnRightAndMove() throws Exception {
		robot.rotate(Turn.RIGHT);
		checkStopped();
		robot.move(1, false);
	}
	
	private void turnLeftAndMove() throws Exception {
		robot.rotate(Turn.LEFT);
		checkStopped();
		robot.move(1, false);
	}
	
	private void checkStopped() throws Exception{
		if(robot.hasStopped()) {
			throw new Exception();
		}
	}

}

