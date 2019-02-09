package test.floor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import floorSubsystem.Floor;
import floorSubsystem.FloorSystem;

public class FloorSystemTest {
	
	FloorSystem floorSystem;
	Floor firstFloor;
	
	@Before
    public void before() throws Exception {
        floorSystem = new FloorSystem();
        firstFloor = floorSystem.getFloors().get(0);
    }
	
	@Test
	public void testAddingFloor() {
		assertFalse(floorSystem.getFloors().isEmpty());
	}
	@Test
	public void testGettingFloor() {
		int floorLevel = 5;
		assertTrue(floorSystem.getFloors().get(floorLevel).equals(floorSystem.getFloorObjectByLevel(floorLevel)));
	}
	
	@Test
	public void testReadingTextFile() {
		assertFalse(floorSystem.getQue().isEmpty());
	}
	
	@Test
	public void testAddingFloorPressed() {
		firstFloor.addFloorButtonPressed(5);
		ArrayList<Integer> temp = new ArrayList<>();
		temp.add(5);
		assertTrue(temp.equals(firstFloor.getFloorDestinationButtonsPressed()));
	}
	
	@Test
	public void testRemovingFloorPressed() {
		firstFloor.addFloorButtonPressed(5);
		firstFloor.removeFloorButtonPressed(5);
		assertTrue(new ArrayList<Integer>().equals(firstFloor.getFloorDestinationButtonsPressed()));
	}
	
	@Test
	public void testDoorOpen() {
		firstFloor.setDoorOpen(true);
		assertTrue(firstFloor.isDoorOpen());
	}
	
	@Test
	public void testUpButtonPressed() {
		firstFloor.setUpButtonPressed(true);
		assertTrue(firstFloor.isUpButtonPressed());
	}
	
	@Test
	public void testDownButtonPressed() {
		firstFloor.setDownButtonPressed(true);
		assertTrue(firstFloor.isDownButtonPressed());
	}
	
	@Test
	public void testSetLevel() {
		int floorLevel = 24;
		firstFloor.setLevel(floorLevel);
		assertTrue(floorLevel == firstFloor.getLevel());
	}

}

