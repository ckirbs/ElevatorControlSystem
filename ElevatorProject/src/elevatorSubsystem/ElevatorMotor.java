package elevatorSubsystem;


import java.util.Date;

import resources.Constants;
import resources.Directions;
import static resources.Constants.FORMATTER;

/**
 * Elevator Motor Controls movement of a single elevator and it's service route,
 * implements a state machine to the states of a real world elevator
 * 
 * @author Callum Kirby, Chris Molnar
 * @version 1.0
 *
 */
public class ElevatorMotor extends Thread {

	private Elevator elv;
	private ElevatorState currentElvState, previousElvState;

	/*
	 * ElevatorMotor - Elevator motor constructor
	 * 
	 * @param Elevator: elevator
	 */

	/**
	 * Associates a motor to a given elevator
	 * 
	 * @param elevator to associate
	 */
	public ElevatorMotor(Elevator elevator) {
		this.elv = elevator;
		this.currentElvState = ElevatorState.STANDBY; // start in standby state
		this.previousElvState = ElevatorState.DOOR_OPEN; // previous state is Door_OPEN
	}

	@Override
	/**
	 * run() State Machine controlling logic for each of the individual states of
	 * the elevator system
	 */
	public void run() {
		while (true) {
			switch (currentElvState) {
			case STANDBY:
				if (previousElvState != currentElvState){
					System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " is on StandBy on floor " + elv.getCurrFloorPosition());
					previousElvState = currentElvState;
				}
				if (!elv.isServiceListEmpty()) { // Check if there is a floor to service
					previousElvState = currentElvState;
					currentElvState = ElevatorState.DOOR_CLOSE;
				}
				break;
			case MOVE:
	    	    System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " Current Service List: " + elv.getServiceList().toString());
				if (elv.getCurrFloorPosition() == elv.getFloorDestionation()) { // Arrived at destination floor
					previousElvState = currentElvState;
					currentElvState = ElevatorState.STOP;
				} else if (Math.random()*10 <= 2){ // Random error occurs
					previousElvState = ElevatorState.MOVE;
					currentElvState = ElevatorState.ERROR;
				}
				else {
					move(); // Keep moving
				}
				break;
			case DOOR_CLOSE:
				System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " close door");
				elv.getElevatorReciever().sendMessage(elv.generateDoorCloseMsg());
				elv.closeDoor();
				previousElvState = currentElvState;
				currentElvState = ElevatorState.MOVE;
				break;
			case DOOR_OPEN:
				System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " open door on floor " + elv.getCurrFloorPosition());
				elv.getElevatorReciever().sendMessage(elv.generateDoorOpenMsg());
				elv.openDoor();
				serviceFloor();
				if (elv.updateFloorToService()) { // If there is an item to service close door
					previousElvState = currentElvState;
					currentElvState = ElevatorState.DOOR_CLOSE;
				} else { // Else nothing to services so sit in standby at the current position with doors
							// open
					previousElvState = currentElvState;
					currentElvState = ElevatorState.STANDBY;
				}
				break;
			case STOP:
				System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " stopped");
				previousElvState = currentElvState;
				currentElvState = ElevatorState.DOOR_OPEN;
				break;
			case ERROR:
				System.out.println("************************");
				System.out.println("An error has occured on elevator " + elv.getElvNumber());
				System.out.println("************************");
				
				// Fix the error
				fixElevator();
				
				// Return to previous state to complete the needed action
				System.out.println("Elevator Fixed! Returning to previous state");
				ElevatorState temp = previousElvState;
				previousElvState = currentElvState;
				currentElvState = temp;
				break;
			default:
				System.out.println(FORMATTER.format(new Date()) + ": uh oh");
				break;
			}
		}
	}
	
	/**
	 * fixElevator() Fix the elevator (it will take between 1 to 10 seconds)
	 */
	private void fixElevator() {
		// Generate random fix time
		long randomFixTime = (long) (Math.random()*9000 + 1000);
		try {
			Thread.sleep(randomFixTime); // sleep for 1 to 10000 seconds
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * move() Simulates movement OF the elevator subsystem with delay of X seconds
	 * to represent travel time
	 */
	private synchronized void move() {
		System.out.println(FORMATTER.format(new Date()) + ": Elevator " + elv.getElvNumber() + " is at floor " + elv.getCurrFloorPosition() + " Moving " + elv.getStatus());

		// The time it takes for the elevator to move up a floor
		try {
			Thread.sleep(Constants.ELEVATOR_TRAVEL_SPEED_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Move the elevator up a floor
		if (elv.getStatus() == Directions.UP) {
			elv.moveUp();
		} else if (elv.getStatus() == Directions.DOWN) {
			elv.moveDown();
		}
	}

	/**
	 * serviceFloor() Arrive at floor, passenger gets off and remove current floor
	 * from service queue
	 */
	private synchronized void serviceFloor() {
		elv.serviceFloor();
		elv.removeFromPassengerButtons(elv.getCurrFloorPosition());
		try {
			Thread.sleep(Constants.ELEVATOR_STOP_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
