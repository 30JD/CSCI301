package cs301.cs.wm.edu.jundaan.falstad;
import cs301.cs.wm.edu.jundaan.generation.Distance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cs301.cs.wm.edu.jundaan.generation.CardinalDirection;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Direction;
import cs301.cs.wm.edu.jundaan.falstad.Robot.Turn;

public class Explorer implements RobotDriver{
	
	private BasicRobot robot;
	private int width;
	private int height;
	private Distance distance;
	
	//construct a default robot driver using explorer algorithm
	public Explorer() {
		robot = null;
		width = 0;
		height = 0;
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
		this.distance = distance;
		
	}
	
	/** raise exception if the robot stops
	 * if the robot successfully reaches the exit, return true. Otherwise, false.
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		//set a counter that counts how many times a cell has bean visited
		setDimensions(robot.getStatePlaying().getMazeConfiguration().getWidth(), getRobot().getStatePlaying().getMazeConfiguration().getHeight());
		int[][] visited = new int[width][height];
		visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]] ++;
		int[] curDoor;
		int[] minDoor = null;
		

		System.out.println("s");
		while(!robot.isAtExit()){
			checkStopped();
			
			int left = Integer.MAX_VALUE;
			int right = Integer.MAX_VALUE;
			int forward = Integer.MAX_VALUE;
			int backward = Integer.MAX_VALUE;
			
			//if the robot is outside of a room, 
			if(!robot.isInsideRoom()) {
				
				CardinalDirection dir = robot.getCurrentDirection();
	
				//checks the robot's options and picks a direction to the adjacent cell lease traveled
				if(robot.distanceToObstacle(Direction.LEFT) > 0) {
					switch(dir) {
					case North:
						left = visited[robot.getCurrentPosition()[0] + 1][robot.getCurrentPosition()[1]];
						break;
					case South:
						left = visited[robot.getCurrentPosition()[0] - 1][robot.getCurrentPosition()[1]];
						break;
					case West:
						left = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] - 1];
						break;
					case East:
						left = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] + 1];
						break;
					}
				}
				
				if(robot.distanceToObstacle(Direction.RIGHT) > 0) {
					switch(dir) {
					case North:
						right = visited[robot.getCurrentPosition()[0] - 1][robot.getCurrentPosition()[1]];
						break;
					case South:
						right = visited[robot.getCurrentPosition()[0] + 1][robot.getCurrentPosition()[1]];
						break;
					case West:
						right = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] + 1];
						break;
					case East:
						right = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] - 1];
						break;
					}
					System.out.println("r");
				}
				
				if(robot.distanceToObstacle(Direction.FORWARD) > 0) {
					switch(dir) {
					case North:
						forward = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] - 1];
						break;
					case South:
						forward = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] + 1];
						break;
					case West:
						forward = visited[robot.getCurrentPosition()[0] - 1][robot.getCurrentPosition()[1]];
						break;
					case East:
						forward = visited[robot.getCurrentPosition()[0] + 1][robot.getCurrentPosition()[1]];
						break;
					}
					System.out.println("f");
				}
				
				if(robot.distanceToObstacle(Direction.BACKWARD) > 0) {
					switch(dir) {
					case North:
						backward = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] + 1];
						break;
					case South:
						backward = visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1] - 1];
						break;
					case West:
						backward = visited[robot.getCurrentPosition()[0] + 1][robot.getCurrentPosition()[1]];
						break;
					case East:
						backward = visited[robot.getCurrentPosition()[0] - 1][robot.getCurrentPosition()[1]];
						break;
					}
					System.out.println("b");
				}
				
				int min = Math.min(Math.min(left, right), Math.min(forward, backward));
				System.out.println("min:" + min);
				
				ArrayList<Integer> numbers = new ArrayList<Integer>();
				
				if(left == min) {
					numbers.add(1);
				}
				
				if(right == min) {
					numbers.add(2);
				}
				
				if(forward == min) {
					numbers.add(3);
				}
				
				if(backward == min) {
					numbers.add(4);
				}
				
				Collections.shuffle(numbers);
				
				int n = numbers.get(0);
				
				System.out.println("n:" + n);
				
				//rotate if necessary and move one step toward that direction
				switch(n) {
				case 1:
					turnLeftAndMove();
					System.out.println("dd" + robot.getCurrentDirection());
					break;
				case 2:
					turnRightAndMove();
					checkStopped();
					System.out.println("dd" + robot.getCurrentDirection());
					break;
				case 3:
					robot.move(1, false);
					checkStopped();
					System.out.println("dd" + robot.getCurrentDirection());
					break;
				case 4:
					turnAroundAndMove();
					checkStopped();
					System.out.println("dd" + robot.getCurrentDirection());
					break;
				}
				System.out.println("dir" + robot.getCurrentDirection());
				visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]] ++;
			}
			
			//if the robot is inside a room
			if(robot.isInsideRoom()) {
				ArrayList<int[]> doors = new ArrayList<int[]>();
				
				int[] startPosition = robot.getCurrentPosition();
				
				
				if(robot.distanceToObstacle(Direction.FORWARD) == 0 && robot.isInsideRoom()) {
					turnRightAndMove();
					checkStopped();
				}
				
				if(!robot.isInsideRoom()) {
					turnAroundAndMove();
					checkStopped();
					robot.rotate(Turn.LEFT);
				}
				
				else {
					robot.move(1, false);
					checkStopped();
				}
				//make the robot move around the room and find possible doors
				while(robot.getCurrentPosition() != startPosition) {
					while(robot.distanceToObstacle(Direction.LEFT) == 0) {
						visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
						if(robot.distanceToObstacle(Direction.FORWARD) == 0 && robot.isInsideRoom()) {
							turnRightAndMove();
							checkStopped();
						}
						
						if(!robot.isInsideRoom()) {
							turnAroundAndMove();
							checkStopped();
							robot.rotate(Turn.LEFT);
						}
						
						else {
							robot.move(1, false);
							checkStopped();
						}
					}
					
					System.out.println("find a door!!!!!!!!!!!!!!!");
					visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
					curDoor = robot.getCurrentPosition();
					if(minDoor == null) {
						minDoor = curDoor;
						doors.add(curDoor);
						if(robot.distanceToObstacle(Direction.FORWARD) == 0) {
							robot.rotate(Turn.RIGHT);
							checkStopped();
						}
						robot.move(1, false);
						checkStopped();
						System.out.println("first door");
					}
					
					else if(visited[minDoor[0]][minDoor[1]] > visited[curDoor[0]][curDoor[1]]) {
						doors.clear();
						doors.add(curDoor);
						minDoor = curDoor;
						if(robot.distanceToObstacle(Direction.FORWARD) == 0) {
							robot.rotate(Turn.RIGHT);
							checkStopped();
						}
						robot.move(1, false);
						checkStopped();
						System.out.println("sb");
					}
					
					else if(visited[minDoor[0]][minDoor[1]] == visited[curDoor[0]][curDoor[1]]) {
						doors.add(curDoor);
						if(robot.distanceToObstacle(Direction.FORWARD) == 0) {
							robot.rotate(Turn.RIGHT);
							checkStopped();
						}
						robot.move(1, false);
						checkStopped();
						System.out.println("28");
					}
				}
				
				System.out.println("outoutout");
				Collections.shuffle(doors);
				int[] roomExit = doors.get(0);
				
				while(robot.getCurrentPosition() != roomExit) {
					if(robot.distanceToObstacle(Direction.FORWARD) == 0 && robot.isInsideRoom()) {
						turnRightAndMove();
						checkStopped();
					}
					
					if(!robot.isInsideRoom()) {
						turnAroundAndMove();
						checkStopped();
						robot.rotate(Turn.LEFT);
					}
					
					else {
						robot.move(1, false);
						checkStopped();
					}
				}
				System.out.println("left =" + robot.distanceToObstacle(Direction.LEFT));
				if(robot.distanceToObstacle(Direction.LEFT) != 0) {
					robot.rotate(Turn.LEFT);
					checkStopped();
				}
				
				robot.move(1, false);
				checkStopped();
				visited[robot.getCurrentPosition()[0]][robot.getCurrentPosition()[1]]++;
			}
		}
		
		//if the robot is at the exit
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