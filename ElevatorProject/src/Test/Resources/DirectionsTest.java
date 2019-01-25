package Test.Resources;

import Resources.Directions;
import org.junit.Test;
import static org.junit.Assert.*;

public class DirectionsTest {
	
	@Test
	public void testOppositeUpDown() {
		assertTrue("Incorrect Elevator", Directions.isOpposite(Directions.UP, Directions.DOWN));
		assertTrue("Incorrect Elevator", Directions.isOpposite(Directions.DOWN, Directions.UP));
	}
	
	@Test
	public void testOppositeUpUp() {
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.UP, Directions.UP));
	}
	
	@Test
	public void testOppositeDownDown() {
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.DOWN, Directions.DOWN));
	}
	
	@Test
	public void testOppositeDownSB() {
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.STANDBY, Directions.DOWN));
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.DOWN, Directions.STANDBY));
	}
	
	@Test
	public void testOppositeUpSB() {
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.STANDBY, Directions.UP));
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.UP, Directions.STANDBY));
	}
	
	@Test
	public void testOppositeSBSB() {
		assertFalse("Incorrect Elevator", Directions.isOpposite(Directions.STANDBY, Directions.STANDBY));
	}
}
