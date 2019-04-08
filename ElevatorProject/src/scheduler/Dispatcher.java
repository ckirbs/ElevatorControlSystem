package scheduler;

import java.util.ArrayList;
import resources.*;
import static resources.Constants.*;

/**
 * Dispatcher class to take care of requests from floors and choose the correct elevator
 * 
 * @author Darren
 *
 */
public class Dispatcher {
	private static ArrayList<Elevator> elevators;
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
		Dispatcher.elevators = new ArrayList<Elevator>();
		for (int i = 0; i < elevNum; i++) {
			Elevator elv = new Elevator(i, Directions.STANDBY, 0);
			Dispatcher.elevators.add(elv);
		}
	}
	
	/**
	 * Determines if an elevator exists or is able to receive requests
	 * 
	 * @param elvNum	The elevator number to check
	 * @return 			True if the elevator can be called, otherwise false
	 */
	public Boolean elevatorCallable(int elvNum) {
        if (elvNum < 0 || elvNum >= Dispatcher.elevators.size()) return false;
		
        return Dispatcher.elevators.get(elvNum).getDir() != Directions.ERROR_HARD;
	}
	
	public synchronized boolean updateElevatorInfo(int id, Directions dir, int floor) {
		if (id > Dispatcher.elevators.size() || id < 0) return false;
		
		Dispatcher.elevators.get(id).setFloor(floor);
		Dispatcher.elevators.get(id).setDir(dir);
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
		for (Elevator elevator: Dispatcher.elevators) {
			// Only check elevators that are on standby or going the right direction
			// Elevators that are in error do not pass here
			if (!Directions.isOpposite(dir, elevator.getDir()) && 
					!Directions.isInError(dir) && !Directions.isInError(elevator.getDir())) {
				
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
	
	public static Directions getElevatorDirectionByElevatorNumber(int elvNum){
		return elevators.get(elvNum).getDir();		
	}
	
	public ArrayList<Elevator> getElevators() {
		return elevators;
	}
}
