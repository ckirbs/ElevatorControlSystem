package test.scheduler;

import org.junit.After;
import org.junit.Test;

import resources.Directions;
import scheduler.Dispatcher;
import static org.junit.Assert.*;

public class DispatcherTest {
	private Dispatcher dispatcher;
	
	@After
	public void teardown() {
		this.dispatcher = null;
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
		dispatcher.updateElevatorInfo(0, Directions.STANDBY, 1);
		dispatcher.updateElevatorInfo(1, Directions.STANDBY, 2);
		
		// Should give the elevator closer to/at the given floor
		assertEquals("Incorrect Elevator", 1, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirections() {
		dispatcher = new Dispatcher(2);
		dispatcher.updateElevatorInfo(0, Directions.STANDBY, 4);
		dispatcher.updateElevatorInfo(1, Directions.UP, 7);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", 0, dispatcher.getNearestElevator(Directions.UP, 2));
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
	
	@Test
	public void testElevatorCallable() {
		dispatcher = new Dispatcher(5);
		dispatcher.updateElevatorInfo(0, Directions.UP, 7);
		dispatcher.updateElevatorInfo(1, Directions.DOWN, 3);
		dispatcher.updateElevatorInfo(2, Directions.STANDBY, 3);
		dispatcher.updateElevatorInfo(3, Directions.ERROR_SOFT, 3);
		dispatcher.updateElevatorInfo(4, Directions.ERROR_HARD, 3);
		
		// Nonexistent elevators and elevators in a hard error state should not be callable
		assertTrue("Incorrect Clallable Value", dispatcher.elevatorCallable(0));
		assertTrue("Incorrect Clallable Value", dispatcher.elevatorCallable(1));
		assertTrue("Incorrect Clallable Value", dispatcher.elevatorCallable(2));
		assertTrue("Incorrect Clallable Value", dispatcher.elevatorCallable(3));
		assertFalse("Incorrect Clallable Value", dispatcher.elevatorCallable(4));
		assertFalse("Incorrect Clallable Value", dispatcher.elevatorCallable(5));
		assertFalse("Incorrect Clallable Value", dispatcher.elevatorCallable(-1));

		dispatcher.updateElevatorInfo(0, Directions.ERROR_HARD, 7);
		assertFalse("Incorrect Clallable Value", dispatcher.elevatorCallable(0));
	}
	
	@Test
	public void testGetDirectionByElevNumb() {
		dispatcher = new Dispatcher(5);
		dispatcher.updateElevatorInfo(0, Directions.UP, 7);
		dispatcher.updateElevatorInfo(1, Directions.DOWN, 3);
		dispatcher.updateElevatorInfo(2, Directions.STANDBY, 3);
		dispatcher.updateElevatorInfo(3, Directions.ERROR_SOFT, 3);
		dispatcher.updateElevatorInfo(4, Directions.ERROR_HARD, 3);
		
		// Nonexistent elevators and elevators in a hard error state should not be callable
		assertEquals("Incorrect Clallable Value", Directions.UP, dispatcher.getElevatorDirectionByElevatorNumber(0));
		assertEquals("Incorrect Clallable Value", Directions.DOWN, dispatcher.getElevatorDirectionByElevatorNumber(1));
		assertEquals("Incorrect Clallable Value", Directions.STANDBY, dispatcher.getElevatorDirectionByElevatorNumber(2));
		assertEquals("Incorrect Clallable Value", Directions.ERROR_SOFT, dispatcher.getElevatorDirectionByElevatorNumber(3));
		assertEquals("Incorrect Clallable Value", Directions.ERROR_HARD, dispatcher.getElevatorDirectionByElevatorNumber(4));
		

		dispatcher.updateElevatorInfo(0, Directions.DOWN, 7);
		assertEquals("Incorrect Clallable Value", Directions.DOWN, dispatcher.getElevatorDirectionByElevatorNumber(0));
	}
}
