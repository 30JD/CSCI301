package generation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import generation.Order.Builder;

public class MazeBuilderEllerTest {
	
	private MazeFactory factory;
	private StubOrder stubOrder;
	private MazeConfiguration config;
	private Cells cells;
	//@Before
	public void setup() {
		factory = new MazeFactory();
		stubOrder = new StubOrder(Builder.Eller, 1, true);
		assertNotNull(factory);
		assertNotNull(stubOrder);
	}

	@Test
	public void testOneExit() {
		setup();
		factory = new MazeFactory();
		stubOrder = new StubOrder(Builder.Eller, 1, true);
		assertNotNull(factory);
		assertNotNull(stubOrder);
		factory.order(stubOrder);
		factory.waitTillDelivered();
		config = ((StubOrder)stubOrder).getConfiguration();
		cells = ((MazeContainer)config).getMazecells();
		
		int x;
		int y;
		int c = 0;
		for(x = 0; x < config.getWidth(); x++) {
			for(y = 0; y < config.getHeight(); y++) {
				if(cells.isExitPosition(x, y) == true) {
					if(x < 1 || x >= config.getWidth() - 1 || y < 1 || y >= config.getHeight() - 1) {
						c++;
					}
				}
			}
		}
		assertEquals(c, 1);
	}
	
	@Test
	public void testExitisReachable() {
		setup();
		factory = new MazeFactory();
		stubOrder = new StubOrder(Builder.Eller, 1, true);
		factory.order(stubOrder);
		factory.waitTillDelivered();
		config = ((StubOrder)stubOrder).getConfiguration();
		cells = ((MazeContainer)config).getMazecells();
		
		int x;
		int y;
		int c = 0;
		for(x = 0; x < config.getWidth(); x++) {
			for(y = 0; y < config.getHeight(); y++) {
				if(config.getDistanceToExit(x, y) == generation.Distance.INFINITY) {
					if(x < 1 || x >= config.getWidth() - 1 || y < 1 || y >= config.getHeight() - 1) {
						c++;
					}
				}
			}
		}
		assertEquals(c, 0);
	}
	
	@Test
	public void testRoom() {
		setup();
		factory = new MazeFactory();
		stubOrder = new StubOrder(Builder.Eller, 1, true);
		assertNotNull(factory);
		assertNotNull(stubOrder);
		factory.order(stubOrder);
		factory.waitTillDelivered();
		config = ((StubOrder)stubOrder).getConfiguration();
		cells = ((MazeContainer)config).getMazecells();
		Wall wall = new Wall(0, 0, CardinalDirection.South);
		for(int i = 1; i < config.getHeight() - 1; i++) {
			for(int j = 1; j < config.getWidth() - 1; j++) {
				wall.setWall(j, i, CardinalDirection.South);
				if(cells.hasWall(j, i, CardinalDirection.East) && cells.hasWall(j, i, CardinalDirection.North) && cells.hasWall(j, i, CardinalDirection.West) && cells.canGo(wall)) {
					assertTrue(cells.hasNoWall(j, i, CardinalDirection.South));
				}
			}
		}
	}
	
	void test() {
		fail("Not yet implemented");
	}

}
