package test.scheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import resources.Directions;
import scheduler.Dispatcher;
import scheduler.Dispatcher.Elevator;
import junit.framework.*;
import static org.junit.Assert.*;

public class DispatcherTest {
	private Dispatcher dispatcher;
	
	@After
	public void tearDown() {
		dispatcher = null;
	}
	
	@Test
	public void testOneElevator() {
		dispatcher = new Dispatcher(1);
		dispatcher.updateElevatorInfo(0, Directions.STANDBY, 1);
		
		assertEquals("Incorrect Elevator", 0, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevators() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.STANDBY, 7);
		dispatcher.updateElevatorInfo(1, Directions.STANDBY, 3);
		
		// Should give the elevator closer to/at the given floor
		assertEquals("Incorrect Elevator", 1, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirections() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.UP, 7);
		dispatcher.updateElevatorInfo(1, Directions.STANDBY, 4);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", 1, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirectionsAtSameFloor() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.STANDBY, 4);
		dispatcher.updateElevatorInfo(1, Directions.UP, 4);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", 0, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneDownTooFar() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.DOWN, 7);
		dispatcher.updateElevatorInfo(1, Directions.DOWN, 3);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", 0, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneUpTooFar() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.UP, 7);
		dispatcher.updateElevatorInfo(1, Directions.UP, 3);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", 1, dispatcher.getNearestElevator(Directions.UP, 6));
	}
}
