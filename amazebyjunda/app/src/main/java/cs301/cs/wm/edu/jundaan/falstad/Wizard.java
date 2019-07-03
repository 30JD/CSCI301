package cs301.cs.wm.edu.jundaan.falstad;

import cs301.cs.wm.edu.jundaan.generation.Distance;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Direction;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Turn;
import cs301.cs.wm.edu.jundaan.generation.CardinalDirection;

public class Wizard implements RobotDriver{

	private BasicRobot robot;
	private int width;
	private int height;
	private Distance distance;

	public Wizard() {
		robot = null;
		width = 0;
		height = 0;
		distance = null;
	}

	public BasicRobot getRobot() {
		return robot;
	}

	/**
	 * provide the robot driver with information on maze width and height
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
		//check if the robot has stopped
		while(!robot.isAtExit()) {
			checkStopped();
			//get current position and direction
			int[] currentPosition = robot.getCurrentPosition();
			CardinalDirection dir = robot.getCurrentDirection();

			//get the position of the neighbor that is closer to the exit
			int[] neighborPosition = robot.getStatePlaying().getMazeConfiguration().getNeighborCloserToExit(currentPosition[0], currentPosition[1]);
			CardinalDirection relativeDirection = CardinalDirection.getDirection(neighborPosition[0] - currentPosition[0], neighborPosition[1] - currentPosition[1]);


			//move towards the neighbor position
			switch(dir) {
				case North:
					switch(relativeDirection) {
						case North:
							robot.move(1, false);
							checkStopped();
							break;
						case South:
							turnAroundAndMove();
							checkStopped();
							break;
						case East:
							turnLeftAndMove();
							checkStopped();
							break;
						case West:
							turnRightAndMove();
							checkStopped();
							break;
					}
					break;

				case South:
					switch(relativeDirection) {
						case South:
							robot.move(1, false);
							checkStopped();
							break;
						case North:
							turnAroundAndMove();
							checkStopped();
							break;
						case West:
							turnLeftAndMove();
							checkStopped();
							break;
						case East:
							turnRightAndMove();
							checkStopped();
							break;
					}
					break;

				case East:
					switch(relativeDirection) {
						case East:
							robot.move(1, false);
							checkStopped();
							break;
						case West:
							turnAroundAndMove();
							checkStopped();
							break;
						case South:
							turnLeftAndMove();
							checkStopped();
							break;
						case North:
							turnRightAndMove();
							checkStopped();
							break;
					}
					break;

				case West:
					switch(relativeDirection) {
						case West:
							robot.move(1, false);
							checkStopped();
							break;
						case East:
							turnAroundAndMove();
							checkStopped();
							break;
						case South:
							turnRightAndMove();
							checkStopped();
							break;
						case North:
							turnLeftAndMove();
							checkStopped();
							break;
					}
					break;
			}

		}

		//the robot is at the exit position

		if(robot.hasStopped()) {
			return false;
		}

		if(robot.isAtExit()) {
			if(robot.canSeeExit(Direction.LEFT)) {
				turnLeftAndMove();
			}

			else if(robot.canSeeExit(Direction.RIGHT)) {
				turnRightAndMove();
			}

			else {
				robot.move(1, false);
				checkStopped();
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

	private void turnAroundAndMove() throws Exception{
		robot.rotate(Turn.AROUND);
		checkStopped();
		robot.move(1, false);
	}

	private void checkStopped() throws Exception{
		if(robot.hasStopped()) {
			throw new Exception();
		}
	}


}
