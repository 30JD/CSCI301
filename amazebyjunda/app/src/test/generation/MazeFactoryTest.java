package generation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import generation.StubOrder;
import generation.Order.Builder;
import generation.Cells;

public class MazeFactoryTest {
	private MazeFactory factory;
	private StubOrder stub;
	private MazeFactory constructortest;
	private MazeFactory constructorTrue;
	private MazeFactory constructorFalse;
	private MazeFactory factoryDFS;
	private StubOrder stubDFS;
	private MazeFactory factoryPrim;
	private StubOrder stubPrim;
	private MazeFactory factoryEller;
	private StubOrder stubEller;
	
	private MazeConfiguration config;
	private Cells cells;
	private Distance distance;
	
	//@Before
	public void setup() {
		factoryDFS = new MazeFactory();
		stubDFS = new StubOrder(Builder.DFS, 1, true);
		factoryPrim = new MazeFactory();
		stubPrim = new StubOrder(Builder.Prim, 1, true);
		factoryEller = new MazeFactory();
		stubEller = new StubOrder(Builder.Eller, 1, true);
		
		factory = new MazeFactory();
		stub = new StubOrder(Builder.DFS, 1, true);
		assertNotNull(factory);
		assertNotNull(stub);
	}
	
	
	/** Test constructor of MazeFactory 
	 */
	@Test
	public void testMazeFactory() {
		constructortest = new MazeFactory();
		constructorTrue = new MazeFactory(true);
		constructorFalse = new MazeFactory(false);
		
		assertNotNull(constructortest);
		assertNotNull(constructorTrue);
		assertNotNull(constructorFalse);
	}
	
	/** Test order method in MazeFactory
	 */
	@Test
	public void testOrder() {
		setup();
		factoryDFS = new MazeFactory();
		stubDFS = new StubOrder(Builder.DFS, 1, true);
		factoryPrim = new MazeFactory();
		stubPrim = new StubOrder(Builder.Prim, 1, true);
		factoryEller = new MazeFactory();
		stubEller = new StubOrder(Builder.Eller, 1, true);
		assertTrue(factoryDFS.order(stubDFS));
		assertTrue(factoryPrim.order(stubPrim));
		assertTrue(factoryEller.order(stubEller));
		
		factoryDFS.order(stubDFS);
		assertFalse(factoryPrim.order(stubPrim));
	}
	
	/** Test cancel method in MazeFactory
	*/
	@Test
	public void testCancel() {
		setup();
		factoryDFS = new MazeFactory();
		stubDFS = new StubOrder(Builder.DFS, 1, true);
		factoryPrim = new MazeFactory();
		stubPrim = new StubOrder(Builder.Prim, 1, true);
		factoryDFS.order(stubDFS);
		factoryDFS.cancel();
		assertTrue(factoryPrim.order(stubPrim));
	}
	
	/** Test testwaitTillDelivered in MazeFactory
	 */
	@Test
	public void testwaitTillDelivered() {
		setup();
		factoryDFS = new MazeFactory();
		stubDFS = new StubOrder(Builder.DFS, 1, true);
		factoryPrim = new MazeFactory();
		stubPrim = new StubOrder(Builder.Prim, 1, true);
		factoryDFS.order(stubDFS);
		factoryDFS.waitTillDelivered();
		assertTrue(factoryPrim.order(stubPrim));
	}
	
	/** Test there is only one exit
	 */
	@Test
	public void testOneExit() {
		setup();
		factory = new MazeFactory();
		stub = new StubOrder(Builder.Eller, 1, true);
		
		assertNotNull(factory);
		assertNotNull(stub);
		
		factory.order(stub);
		factory.waitTillDelivered();
		config = ((StubOrder)stub).getConfiguration();
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
		stub = new StubOrder(Builder.Eller, 1, true);
		factory.order(stub);
		factory.waitTillDelivered();
		config = ((StubOrder)stub).getConfiguration();
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
	
	void test() {
		fail("Not yet implemented");
	}

}
