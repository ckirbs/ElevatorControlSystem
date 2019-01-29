package elevatorSubsystem;

import Resources.Directions;

public class ElevatorMotor implements Runnable {
	
	private Elevator elv;
	
	public ElevatorMotor(Elevator elevator) {
		this.elv = elevator;
	}

	@Override
	public void run() {
		while(true) {
			if(elv.getCurrFloorPosition() == elv.getFloorDestionation()) {
				serviceFloor();
				elv.updateFloorToService();
			} else {
				move();
			}
		}
		
	}
	
	private void  move() {
		System.out.println("Elevator is Moving " + elv.getStatus());
		if (elv.getCurrFloorPosition() == elv.MAX_FLOOR || elv.getCurrFloorPosition() == elv.MIN_FLOOR) {
			new Exception("Reached End of Track");
		}

		if (elv.getStatus() == Directions.UP) {
			elv.moveUp();
		} else if (elv.getStatus() == Directions.DOWN) {
			elv.moveDown();
		}

		try {
			Thread.sleep(Elevator.FLOOR_TRAVEL_SPEED_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private synchronized void serviceFloor() {
		System.out.print("Opening Doors on Floor " + elv.getCurrFloorPosition());

		//Clear floor from queue(s)
		elv.removeFromPassengerButtons(elv.pollServiceQueue());

		System.out.print("Opening Closing on Floor " + elv.getCurrFloorPosition());

		System.out.print("Current Floors Pressed: " + elv.getElevatorPassengerButtons());
	}
}
