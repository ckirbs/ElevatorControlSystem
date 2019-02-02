package elevatorSubsystem;

import resources.Constants;
import resources.Directions;

public class ElevatorMotor extends Thread {
	
	private Elevator elv;
	private ElevatorState elvState;
	
	/*
	 * ElevatorMotor - Elevator motor constructor
	 * 
	 * @param Elevator: elevator
	 */
	public ElevatorMotor(Elevator elevator) {
		this.elv = elevator;
		this.elvState = ElevatorState.STANDBY; // standby state
	}

	@Override
	/*
	 * run() - elevator state machine
	 */
	public void run() {
		while(true) {
		    switch(elvState){
		    	case STANDBY:
		    	    System.out.println("elevator on StandBy");
		    	    if (!elv.isPriorityQueueEmpty()){ // Check if there is a floor to service
		    	    	elvState = ElevatorState.DOOR_CLOSE;
		    	    }
		    	    break;
		    	case MOVE:
		    	    System.out.println("elevator Moving");
    		    	if(elv.getCurrFloorPosition() == elv.getFloorDestionation()) { // Arrived at destination floor
    		    		elvState = ElevatorState.STOP;
    		    	} else {
    		    		move(); // Keep moving
    		    	}
		    	    break;
		    	case DOOR_CLOSE:
		    	    System.out.println("elevator close door");
		    	    elv.getElevatorReciever().sendMessage(elv.generateDoorCloseMsg());
		    	    elv.closeDoor();
		    	    elvState = ElevatorState.MOVE;
		    	    break;
		    	case DOOR_OPEN:
		    		System.out.println("elevator open door");
		    		elv.getElevatorReciever().sendMessage(elv.generateDoorOpenMsg());
				    elv.openDoor();
				    serviceFloor();
		    		if (elv.updateFloorToService()){ // If there is an item to service close door
		    			elvState = ElevatorState.DOOR_CLOSE;
		    		}
		    		else { // Else nothing to services so sit in standby at the current position with doors open
		    			elvState = ElevatorState.STANDBY;
		    		}
		    	    break;
		    	case STOP:
		    	    System.out.println("elevator stopped");
		    	    elvState = ElevatorState.DOOR_OPEN;
		    	    break;
		    	default:
		    		System.out.println("uh oh");
		    		break;
		    }
		}
		
	}
	
	/*
	 * move() - Move up or down a floor and sleep to simulate moving
	 */
	private void  move() {
		System.out.println("Elevator is Moving " + elv.getStatus());

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
	
	/*
	 * serviceFloor() - Arrive at floor, passenger gets off and remove current floor from service queue 
	 */
	private synchronized void serviceFloor() {
		elv.removeFromPassengerButtons(elv.pollServiceQueue());
	}
}
