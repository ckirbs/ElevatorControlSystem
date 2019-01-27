package Test.Resources;

import Resources.Directions;
import org.junit.Test;
import static org.junit.Assert.*;

public class DirectionsTest {
	
	@Test
	public void testOppositeUpDown() {
		assertTrue("Wrong Opposite Value", Directions.isOpposite(Directions.UP, Directions.DOWN));
		assertTrue("Wrong Opposite Value", Directions.isOpposite(Directions.DOWN, Directions.UP));
	}
	
	@Test
	public void testOppositeUpUp() {
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.UP, Directions.UP));
	}
	
	@Test
	public void testOppositeDownDown() {
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.DOWN, Directions.DOWN));
	}
	
	@Test
	public void testOppositeDownSB() {
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.STANDBY, Directions.DOWN));
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.DOWN, Directions.STANDBY));
	}
	
	@Test
	public void testOppositeUpSB() {
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.STANDBY, Directions.UP));
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.UP, Directions.STANDBY));
	}
	
	@Test
	public void testOppositeSBSB() {
		assertFalse("Wrong Opposite Value", Directions.isOpposite(Directions.STANDBY, Directions.STANDBY));
	}
}
