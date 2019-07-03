package gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class ExplorerTest {
	
	Explorer robotDriver;
	BasicRobot robot;
	Controller controller;
	
	@Before
	private void setUp() {
		
		controller = new Controller();
		controller.setFileName("test/data/input.xml");
		robot = new BasicRobot(controller);
		robotDriver = new Explorer();
		robotDriver.setRobot(robot);
		controller.setRobotAndDriver(robot, robotDriver);
		robot.setMaze(controller);
		controller.start();
		int width = controller.getMazeConfiguration().getWidth();
		int height = controller.getMazeConfiguration().getHeight();
		robotDriver.setDimensions(width, height);

	}
	
	@Test
	void testSetRobot() {
		setUp();
		robot = new BasicRobot();
		robotDriver.setRobot(robot);
		assertEquals(robot, robotDriver.getRobot());
	}

	@Test
	void testDrive2Exit() throws Exception {
		setUp();
		assertEquals(true, controller.getDriver().drive2Exit());
	}

}


