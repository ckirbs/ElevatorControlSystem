package scheduler;

import java.util.ArrayList;
import java.time.LocalTime;

import resources.*;
import static resources.Constants.*;

/**
 * Dispatcher class to take care of requests from floors and choose the correct elevator
 * 
 * @author Darren
 *
 */
public class Dispatcher {
	private ArrayList<Elevator> elevators;
	public final static int MAX_DIFF = NUMBER_OF_FLOORS + 1; // + 1 so that it's always bigger than the greatest possible difference
	
	/**
	 * Default constructor that builds a Dispatcher with a default value
	 */
	public Dispatcher() {
		this(NUMBER_OF_ELEVATORS);
	}
	
	/** 
	 * Constructor class that builds a Dispatcher with suggested number of elevators
	 */
	public Dispatcher(int elevNum) {
		this.elevators = new ArrayList<Elevator>();
		for (int i = 0; i < elevNum; i++) this.elevators.add(new Elevator(i, Directions.STANDBY, 0));
	}
	
	public synchronized boolean updateElevatorInfo(int id, Directions dir, int floor) {
		if (id > this.elevators.size() || id < 0) return false;
		
		this.elevators.get(id).setFloor(floor);
		this.elevators.get(id).setDir(dir);
		return true;
	}
	
	/**
	 * Determine the nearest, applicable elevator given a request consisting of a direction and originating floor 
	 * Currently iterates through a list of elevators but will probably change to ping each elevator
	 * 
	 * @param dir		The direction of the request
	 * @param callingFloor		The request's originating floor
	 * @return			The elevator to handle the request
	 */
	public int getNearestElevator(Directions dir, int callingFloor) {
		int currDif = MAX_DIFF;
		int newDif;
		int currElevator = -1;
		
		// For each elevator
		for (Elevator elevator: this.elevators) {
			// Only check elevators that are on standby or going the right direction
			// Elevators that are in error do not pass here
			if (!Directions.isOpposite(dir, elevator.getDir())) {
				
				// If elevator is going down, it can only hit floors below it
				// If elevator is going up, it can only hit floors above it
				// If elevator is on standby, it can go either direction so floor locations don't matter
				if (elevator.getDir() == Directions.STANDBY || (dir == Directions.DOWN && elevator.getFloor() > callingFloor && dir == elevator.getDir()) || (dir == Directions.UP && elevator.getFloor() < callingFloor && dir == elevator.getDir())) {
					newDif = Math.abs(elevator.getFloor() - callingFloor);
					if (newDif < currDif) {
						currDif = newDif;
						currElevator = elevator.getId();
					}
				}
			}
		}
		
		return currElevator;
	}
	
	// Elevator object to hold elevator info
	public class Elevator {
		private Directions dir;
		private int currFloor;
		private int id;
		
		public Elevator (int id, Directions dir, int floor) {
			this.dir = dir;
			this.currFloor = floor;
			this.id = id;
		}
		
		public int getFloor() {
			return this.currFloor;
		}

		public void setFloor(int floor) {
			this.currFloor = floor;
		}
		
		public Directions getDir() {
			return this.dir;
		}

		public void setDir(Directions dir) {
			this.dir = dir;
		}
		
		public int getId() {
			return this.id;
		}
	}
}
