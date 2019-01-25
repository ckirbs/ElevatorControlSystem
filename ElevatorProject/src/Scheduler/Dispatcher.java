package Scheduler;

import java.util.ArrayList;
import Resources.*;


public class Dispatcher {
	private ArrayList<TempElevator> elevators;
	public final static int MAX_DIFF = SystemFile.HIGHESTFLOOR - SystemFile.LOWESTFLOOR + 1; // + 1 so that it's always bigger than the greatest possible difference
	
	public Dispatcher() {
		this.elevators = new ArrayList<TempElevator>();
	}

	public void addElevator(TempElevator elevator) {
		this.elevators.add(elevator);
	}
	
	// Determine the nearest, applicable elevator given a request consisting of a direction and originating floor
	// Currently iterates through a list of elevators but will probably change to ping each elevator
	public TempElevator getNearestElevator(Directions dir, int floor) {
		int currDif = MAX_DIFF;
		int newDif;
		TempElevator currElevator = null;
		
		// For each elevator
		for (TempElevator elevator: this.elevators) {
			// Only check elevators that are on standby or going the right direction
			if (!Directions.isOpposite(dir, elevator.getDir())) {
				
				// If elevator is going down, it can only hit floors below it
				// If elevator is going up, it can only hit floors above it
				// If elevator is on standby, it can go either direction so floor locations don't matter
				if (elevator.getDir() == Directions.STANDBY || (dir == Directions.DOWN && elevator.getFloor() > floor && dir == elevator.getDir()) || (dir == Directions.UP && elevator.getFloor() < floor && dir == elevator.getDir())) {
					newDif = Math.abs(elevator.getFloor() - floor);
					if (newDif < currDif) {
						currDif = newDif;
						currElevator = elevator;
					}
				}
			}
		}
		
		return currElevator;
	}
	
	
	// Temporary elevator object to rough functionality in
	public class TempElevator {
		private Directions dir;
		private int currFloor;
		
		public TempElevator (Directions dir, int floor) {
			this.dir = dir;
			this.currFloor = floor;
		}
		
		public int getFloor() {
			return this.currFloor;
		}
		
		public Directions getDir() {
			return this.dir;
		}
	}
}
