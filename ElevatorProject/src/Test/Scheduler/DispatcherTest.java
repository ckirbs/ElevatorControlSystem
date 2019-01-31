package test.scheduler;

import org.junit.Before;
import org.junit.Test;

import resources.Directions;
import scheduler.Dispatcher;
import scheduler.Dispatcher.Elevator;
import junit.framework.*;
import static org.junit.Assert.*;

public class DispatcherTest {
	private Dispatcher dispatcher;
	
	@Before
	public void initialize() {
		this.dispatcher = new Dispatcher();
	}
	
	@Test
	public void testOneElevator() {
		Elevator elevator = this.dispatcher.new Elevator(0, Directions.STANDBY, 1);
		dispatcher.addElevator(elevator);
		
		assertEquals("Incorrect Elevator", 0, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevators() {
		Elevator elevator1 = this.dispatcher.new Elevator(Directions.STANDBY, 1);
		dispatcher.addElevator(elevator1);
		Elevator elevator2 = this.dispatcher.new Elevator(Directions.STANDBY, 2);
		dispatcher.addElevator(elevator2);
		
		// Should give the elevator closer to/at the given floor
		assertEquals("Incorrect Elevator", elevator2, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirections() {
		Elevator elevator1 = this.dispatcher.new Elevator(Directions.STANDBY, 4);
		dispatcher.addElevator(elevator1);
		Elevator elevator2 = this.dispatcher.new Elevator(Directions.UP, 7);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirectionsAtSameFloor() {
		Elevator elevator1 = this.dispatcher.new Elevator(Directions.STANDBY, 4);
		dispatcher.addElevator(elevator1);
		Elevator elevator2 = this.dispatcher.new Elevator(Directions.UP, 4);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneDownTooFar() {
		Elevator elevator1 = this.dispatcher.new Elevator(Directions.DOWN, 7);
		dispatcher.addElevator(elevator1);
		Elevator elevator2 = this.dispatcher.new Elevator(Directions.DOWN, 3);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneUpTooFar() {
		Elevator elevator1 = this.dispatcher.new Elevator(Directions.UP, 7);
		dispatcher.addElevator(elevator1);
		Elevator elevator2 = this.dispatcher.new Elevator(Directions.UP, 3);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator2, dispatcher.getNearestElevator(Directions.UP, 6));
	}
}
