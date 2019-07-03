package gui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Panel;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import generation.Distance;

class WizardTest {
	
	Wizard robotDriver;
	BasicRobot robot;
	Controller controller;
	Distance distance;
	State currentState;
	State[] states;
	MazePanel panel;
	@Before
	private void setUp() {
		
		controller = new Controller();
		controller.setFileName("test/data/input.xml");
		robot = new BasicRobot(controller);
		robotDriver = new Wizard();
		robotDriver.setRobot(robot);
		controller.setRobotAndDriver(robot, robotDriver);
		robot.setMaze(controller);
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
		assertEquals(true, robotDriver.drive2Exit());
	}

}
