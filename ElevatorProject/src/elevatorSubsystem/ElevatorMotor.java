package elevatorSubsystem;

import resources.Constants;
import resources.Directions;

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
	private ElevatorState elvState;

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
		this.elvState = ElevatorState.STANDBY; // standby state
	}

	@Override
	/**
	 * run() State Machine controlling logic for each of the individual states of
	 * the elevator system
	 */
	public void run() {
		while (true) {
			switch (elvState) {
			case STANDBY:
				System.out.println("Elevator " + elv.getElvNumber() + " is on StandBy");
				if (!elv.isPriorityQueueEmpty()) { // Check if there is a floor to service
					elvState = ElevatorState.DOOR_CLOSE;
				}
				break;
			case MOVE:
				System.out.println("Elevator " + elv.getElvNumber() + " is Moving" );
				if (elv.getCurrFloorPosition() == elv.getFloorDestionation()) { // Arrived at destination floor
					elvState = ElevatorState.STOP;
				} else {
					move(); // Keep moving
				}
				break;
			case DOOR_CLOSE:
				System.out.println("Elevator " + elv.getElvNumber() + " close door");
				elv.getElevatorReciever().sendMessage(elv.generateDoorCloseMsg());
				elv.closeDoor();
				elvState = ElevatorState.MOVE;
				break;
			case DOOR_OPEN:
				System.out.println("Elevator " + elv.getElvNumber() + " open door");
				elv.getElevatorReciever().sendMessage(elv.generateDoorOpenMsg());
				elv.openDoor();
				serviceFloor();
				if (elv.updateFloorToService()) { // If there is an item to service close door
					elvState = ElevatorState.DOOR_CLOSE;
				} else { // Else nothing to services so sit in standby at the current position with doors
							// open
					elvState = ElevatorState.STANDBY;
				}
				break;
			case STOP:
				System.out.println("Elevator " + elv.getElvNumber() + " stopped");
				elvState = ElevatorState.DOOR_OPEN;
				break;
			default:
				System.out.println("uh oh");
				break;
			}
		}

	}

	/**
	 * move() Simulates movement OF the elevator subsystem with delay of X seconds
	 * to represent travel time
	 */
	private synchronized void move() {
		System.out.println("Elevator " + elv.getElvNumber() + " isis Moving " + elv.getStatus());

		if (elv.getStatus() == Directions.UP) {
			elv.moveUp();
		} else if (elv.getStatus() == Directions.DOWN) {
			elv.moveDown();
		}

		try {
			Thread.sleep(Constants.ELEVATOR_TRAVEL_SPEED_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * serviceFloor() Arrive at floor, passenger gets off and remove current floor
	 * from service queue
	 */
	private synchronized void serviceFloor() {
		elv.removeFromPassengerButtons(elv.pollServiceQueue());
	}
}
