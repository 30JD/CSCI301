package gui;

import static org.junit.jupiter.api.Assertions.*;
import generation.CardinalDirection;
import gui.Constants;
import gui.Robot.Direction;
import gui.Robot.Turn;
import gui.SimpleKeyListener;
import gui.Constants.UserInput;

import java.awt.event.KeyListener;
//import java.io.File;
import gui.SimpleKeyListener;
import gui.MazeApplication;

import javax.swing.JFrame;

import org.junit.Before;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Junda
 *
 */

class BasicRobotTest {
	ManualDriver robotDriver;
	BasicRobot robot;
	Controller controller;
	KeyListener kl;
	MazeApplication app ;
	
	@Before
	private void setUp() {
		
		controller = new Controller();
		controller.setFileName("test/data/input.xml");
		robot = new BasicRobot(controller);
		robot.setMaze(controller);
		robotDriver = new ManualDriver();
		controller.setRobotAndDriver(robot, robotDriver);
		robotDriver.setRobot(robot);
		robot.setMaze(controller);
		controller.start();

	}

	@Test
	public void testRotate() {
		setUp();
		
		assertEquals(0, robot.getOdometerReading());
		
		//test each rotating operation
		robot.rotate(Turn.LEFT);
		assertEquals(CardinalDirection.South, robot.getCurrentDirection());
		assertEquals(2997, robot.getBatteryLevel());
		robot.rotate(Turn.RIGHT);
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		assertEquals(2994, robot.getBatteryLevel());
		robot.rotate(Turn.AROUND);
		assertEquals(CardinalDirection.West, robot.getCurrentDirection());
		assertEquals(2988, robot.getBatteryLevel());
		
		robot.setBatteryLevel(3);
		robot.rotate(Turn.LEFT);
		assertEquals(0, robot.getBatteryLevel());
		assertEquals(CardinalDirection.North, robot.getCurrentDirection());
		robot.rotate(Turn.RIGHT);
		//CardinalDirection should stay the same since didn't actually rotate
		//because not enough battery
		assertEquals(CardinalDirection.North, robot.getCurrentDirection());
		
		//turn around takes 6 battery unit for it is a 180 degree rotation
		setUp();
		//if the battery level is 3, it should stay the same direction
		robot.setBatteryLevel(3);
		robot.rotate(Turn.AROUND);
		assertEquals(3, robot.getBatteryLevel());
		assertEquals(CardinalDirection.East, robot.getCurrentDirection());
		robot.setBatteryLevel(6);
		robot.rotate(Turn.AROUND);
		assertEquals(0, robot.getBatteryLevel());
		assertEquals(CardinalDirection.West, robot.getCurrentDirection());
		
		//rotate should not change odometer field and should match with the
		//number of step before rotations
		assertEquals(0, robot.getOdometerReading());
	}
	
	@Test
	public void testMove() throws Exception {
		setUp();
		int[] originalPosition = controller.getCurrentPosition();
		
		//move(0, true) does not cost any energy and the robot stays at the same position
		robot.move(0, true);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0]);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(3000, robot.getBatteryLevel());
		assertEquals(0, robot.getOdometerReading());
		
		//moving by 1 unit costs 5 battery level and move one step forward. Odometer should increase by 1
		robot.move(1, true);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0] + 1);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(2995, robot.getBatteryLevel());
		assertEquals(1, robot.getOdometerReading());
		
		//rotating around costs 6 battery level and the robot stays at the same position
		robot.rotate(Turn.AROUND);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0] + 1);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(2989, robot.getBatteryLevel());
		assertEquals(1, robot.getOdometerReading());
		
		//moving by 1 unit costs 5 battery level and move one step forward. Odometer should increase by 1
		robot.move(1, true);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0]);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(2984, robot.getBatteryLevel());
		assertEquals(2, robot.getOdometerReading());
		
		//turning left costs 3 battery level and does not change the position
		robot.rotate(Turn.LEFT);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0]);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(2981, robot.getBatteryLevel());
		assertEquals(2, robot.getOdometerReading());
		
		//When a robot encounters a wall, it does not move 
		robot.move(1, true);
		assertEquals(robot.getCurrentPosition()[0], originalPosition[0]);
		assertEquals(robot.getCurrentPosition()[1], originalPosition[1]);
		assertEquals(2981, robot.getBatteryLevel());
		assertEquals(2, robot.getOdometerReading());
	}
	
	@Test
	public void testSetMaze() {
		//There is no controller
		BasicRobot robot = new BasicRobot();
		assertNull(robot.getController());
		
		//set controller to be controller1
		Controller controller1 = new Controller();
		robot.setMaze(controller1);
		assertNotNull(robot.getController());
		
		//set controller to be controller2
		Controller controller2 = new Controller();
		robot.setMaze(controller2);
		assertNotEquals(robot.getController(), controller1);
	}
	
	@Test
	public void testIsAtExit() {
		setUp();
		//The starting position is not at exit
		assertFalse(robot.isAtExit());
		
		//Going to the exit
		robot.rotate(Turn.AROUND);
		controller.keyDown(UserInput.Jump, 0);
		robot.move(3, true);
		robot.rotate(Turn.RIGHT);
		robot.move(3, true);
		robot.rotate(Turn.LEFT);
		assertTrue(robot.isAtExit());
		
	}
	
	@Test
	public void testCanSeeExit() {
		setUp();
		//Cannot see the exit from the starting position
		assertFalse(robot.canSeeExit(Direction.FORWARD));
		setUp();
		//Going to the exit
		robot.rotate(Turn.AROUND);
		controller.keyDown(UserInput.Jump, 0);
		robot.move(3, true);
		robot.rotate(Turn.RIGHT);
		robot.move(3, true);
		robot.rotate(Turn.LEFT);
		assertTrue(robot.canSeeExit(Direction.FORWARD));
	}
	
	@Test
	public void testIsInsideRoom() {
		setUp();
		//The robot is not inside a room at the starting position
		assertFalse(robot.isInsideRoom());
		//Going to a room
		robot.rotate(Turn.AROUND);
		controller.keyDown(UserInput.Jump, 0);
		robot.rotate(Turn.RIGHT);
		controller.keyDown(UserInput.Jump, 0);
		assertTrue(robot.isInsideRoom());
	}
	
	
	@Test
	public void testDistanceToObstacle() {
		setUp();
		//The left side of the robot is wall
		assertEquals(robot.distanceToObstacle(Direction.LEFT), 0);

		//The robot has to move 11 steps forward until it encounters a wall
		assertEquals(robot.distanceToObstacle(Direction.FORWARD), 11);
	}
	
	
	void test() {
		fail("Not yet implemented");
	}

}
