package cs301.cs.wm.edu.jundaan.falstad;
import cs301.cs.wm.edu.jundaan.generation.Distance;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Direction;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Turn;

public class WallFollower implements RobotDriver{
	
	private BasicRobot robot;
	private int width;
	private int height;
	private Distance distance;
	
	//construct a default robot driver using wallFollower algorithm
	public WallFollower() {
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
	public void setDistance(Distance dis) {
		distance = dis;
		
	}
	
	/** raise exception if the robot stops
	 * if the robot successfully reaches the exit, return true. Otherwise, false.
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while(!robot.isAtExit()){
			checkStopped();
			
			//if there is no wall on the left, the robot moves in that direction
			if(robot.distanceToObstacle(Direction.LEFT) != 0) {
				robot.rotate(Turn.LEFT);
				robot.move(1, false);
				checkStopped();
			}
			
			else {
				//if there is a wall on the left and no wall in the front, the robot moves forward
				if(robot.distanceToObstacle(Direction.FORWARD) != 0) {
					robot.move(1, false);
					checkStopped();
				}
				
				//if there are walls on the left and in the front, the robot just turns right
				else {
					robot.rotate(Turn.RIGHT);
					checkStopped();
				}
			}
		}
		
		//the robot is now at the exit
		if(robot.isAtExit()) {
			if(robot.canSeeExit(Direction.LEFT)) {
				turnLeftAndMove();
			}
			
			else if(robot.canSeeExit(Direction.RIGHT)) {
				turnRightAndMove();
			}
			
			else {
				robot.move(1, false);
			}			
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
