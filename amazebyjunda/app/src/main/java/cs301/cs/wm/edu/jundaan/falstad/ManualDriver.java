package cs301.cs.wm.edu.jundaan.falstad;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Turn;
import cs301.cs.wm.edu.jundaan.generation.Distance;
import cs301.cs.wm.edu.jundaan.falstad.Constants.UserInput;
/**
 * ManualDriver is a class that specifies a robot driver in which the user manually drives a robot.
 * @author Junda An
 */

public class ManualDriver implements RobotDriver{
	private BasicRobot robot;
	private int width;
	private int height;
	private Distance distance;
	
	//construct a default manual driver
	public ManualDriver() {
		robot = null;
	}
	
	public ManualDriver(BasicRobot r) {
		robot = r;
	}

	/**
	 * assign a robot platform to the driver.
	 */
	public void setRobot(Robot r) {
		robot = (BasicRobot) r;
	}
	
	public Robot getRobot() {
		return robot;
	}

	/**
	 * provide the robot driver with information on maze width and height
	 */
	public void setDimensions(int w, int h) {
		width = w;
		height = h;
		
	}

	/**
	 * provide the robot driver with information on distance to the exit
	 */
	public void setDistance(Distance dis) {
		distance = dis;
	}

	/** raise exception if the robot stops
	 * if the robot successfully reaches the exit, return true. Otherwise, false.
	 */
	public boolean drive2Exit() throws Exception {
		if(robot.hasStopped()) {
			throw new Exception();
		}
		
		if(robot.isAtExit()) {
			return true;
		}
		
		else {
			return false;
		}
	}

	/**
	 * Energy Consumption = initial battery level - current battery level
	 */
	public float getEnergyConsumption() {
		return 3000 - robot.getBatteryLevel();
	}

	/**
	 * return the total length of the journey in number of cells traversed.
	 */
	public int getPathLength() {
		return robot.getOdometerReading();
	}
	
	/**
	 * This method will help to manually drive the robot in the maze. It receives a keyboard input and affects the robot.
	 * @param key
	 * @return true
	 */
	public boolean keyDown(UserInput key, int value) {
		switch(key) {
		case Up:
			robot.move(1, true);
			break;
			
		case Left:
			robot.rotate(Turn.LEFT);
			break;
		
		case Right:
			robot.rotate(Turn.RIGHT);
			break;
			
		case Down:
			robot.rotate(Turn.AROUND);
			robot.move(1, true);
			break;
		}
		
		return true;
	}

}
