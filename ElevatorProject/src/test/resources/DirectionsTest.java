package test.resources;

import org.junit.Test;

import resources.Directions;
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
	
	@Test
	public void testZeroReturnsDown() {
		assertEquals("Wrong Direction Value", Directions.DOWN, Directions.getDirByInt(0));
	}
	
	@Test
	public void testOneReturnsUp() {
		assertEquals("Wrong Direction Value", Directions.UP, Directions.getDirByInt(1));
	}
	
	@Test
	public void testTwoReturnsSB() {
		assertEquals("Wrong Direction Value", Directions.STANDBY, Directions.getDirByInt(2));
	}
	
	@Test
	public void testDownReturnsZero() {
		assertEquals("Wrong Integer Value", 0, Directions.getIntByDir(Directions.DOWN));
	}
	
	@Test
	public void testUpReturnsOne() {
		assertEquals("Wrong Integer Value", 1, Directions.getIntByDir(Directions.UP));
	}
	
	@Test
	public void testSBReturnsTwo() {
		assertEquals("Wrong Integer Value", 2, Directions.getIntByDir(Directions.STANDBY));
	}
	
	
	@Test
	public void testOtherValsReturnNegOne() {
		assertEquals("Wrong Integer Value", -1, Directions.getIntByDir(null));
	}
	
}
