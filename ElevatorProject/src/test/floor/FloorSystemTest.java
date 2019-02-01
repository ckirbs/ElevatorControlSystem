package test.floor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import floorSubsystem.*;

public class FloorSystemTest {
	
	FloorSystem floorSystem;
	
	@Before
    public void before() throws Exception {
        floorSystem = new FloorSystem();    
    }
	
	@Test
	public void testAddingFloor() {
		assertFalse(floorSystem.getFloors().isEmpty());
	}
}

