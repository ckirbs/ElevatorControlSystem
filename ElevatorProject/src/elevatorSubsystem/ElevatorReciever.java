package elevatorSubsystem;

import Resources.Message;

public class ElevatorReciever implements Runnable {

	private Elevator elv;

	public ElevatorReciever(Elevator elevator) {
		this.elv = elevator;
	}

	@Override
	public void run() {
		while(true) {
			//receive message()
			Message msg = null;
			processSchedulerMsg(msg);
		}
	}

	// Receive msg from scheduler with floor number
	private void processSchedulerMsg(Message msg) {
		// TODO Update implementation when message class is updated
		if (msg.getByteOne() == 4) {
			// New floor request
			if (msg.getByteTwo() == 0) {
				// Voluntary Dest
				if (elv.canServiceCall(msg.getByteFour())) {
					sendAcceptMsg();
					addFloorToService(msg.getByteFour());
				} else {
					sendDeclineMsg();
				}
			} else if (msg.getByteTwo() == 1) {
				// Mandatory
				sendAcceptMsg();
				addFloorToService(msg.getByteFour());
				elv.addToPassengerButtons(msg.getByteFour());
			}
		} else if (msg.getByteOne() == 5) {
			sendSatusMsg();
		}
	}

	/**
	 * addFloorToService() adds request received via message, if current floor ==
	 * startingFloor treaded as internalElevatorPanel request (button glows) else
	 * treated as floor request and added to service route
	 * 
	 * @param floor containing calling floor and
	 */
	private synchronized void addFloorToService(Integer floor) {
		elv.addToServiceQueue(floor);
		elv.updateFloorToService();
	}

	private void sendAcceptMsg() {
		// Flag - 6
		// Y/N - 1
		// elevatorNumber - Byte 3
		// floorDest - Byte 4
	}

	private void sendDeclineMsg() {
		// Flag - 6
		// Y/N - 1
		// elevatorNumber - Byte 3
		// floorDest - Byte 4
	}

	private void sendSatusMsg() {
		// Flag - 7
		// Status - Byte 2
		// currFloor - Byte 3
		// elevatorNumber - Byte 4
	}

}
