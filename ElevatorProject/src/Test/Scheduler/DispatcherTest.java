package test.scheduler;

import org.junit.Before;
import org.junit.Test;

import resources.Directions;
import scheduler.Dispatcher;
import scheduler.Dispatcher.TempElevator;
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
		TempElevator elevator = this.dispatcher.new TempElevator(Directions.STANDBY, 1);
		dispatcher.addElevator(elevator);
		
		assertEquals("Incorrect Elevator", elevator, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevators() {
		TempElevator elevator1 = this.dispatcher.new TempElevator(Directions.STANDBY, 1);
		dispatcher.addElevator(elevator1);
		TempElevator elevator2 = this.dispatcher.new TempElevator(Directions.STANDBY, 2);
		dispatcher.addElevator(elevator2);
		
		// Should give the elevator closer to/at the given floor
		assertEquals("Incorrect Elevator", elevator2, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirections() {
		TempElevator elevator1 = this.dispatcher.new TempElevator(Directions.STANDBY, 4);
		dispatcher.addElevator(elevator1);
		TempElevator elevator2 = this.dispatcher.new TempElevator(Directions.UP, 7);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.UP, 2));
	}
	
	@Test
	public void testTwoElevatorsWithDirectionsAtSameFloor() {
		TempElevator elevator1 = this.dispatcher.new TempElevator(Directions.STANDBY, 4);
		dispatcher.addElevator(elevator1);
		TempElevator elevator2 = this.dispatcher.new TempElevator(Directions.UP, 4);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneDownTooFar() {
		TempElevator elevator1 = this.dispatcher.new TempElevator(Directions.DOWN, 7);
		dispatcher.addElevator(elevator1);
		TempElevator elevator2 = this.dispatcher.new TempElevator(Directions.DOWN, 3);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator1, dispatcher.getNearestElevator(Directions.DOWN, 4));
	}
	
	@Test
	public void testTwoElevatorsCloserOneUpTooFar() {
		TempElevator elevator1 = this.dispatcher.new TempElevator(Directions.UP, 7);
		dispatcher.addElevator(elevator1);
		TempElevator elevator2 = this.dispatcher.new TempElevator(Directions.UP, 3);
		dispatcher.addElevator(elevator2);
		
		// Should give the closest elevator that is going in the correct direction/not moving
		assertEquals("Incorrect Elevator", elevator2, dispatcher.getNearestElevator(Directions.UP, 6));
	}
}
