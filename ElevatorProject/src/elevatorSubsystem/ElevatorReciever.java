package elevatorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ElevatorReciever {

	private static final int NUMBER_OF_ELEVATORS = 1;
	private List<Elevator> elevators;
	DatagramSocket datagramSocket;

	public ElevatorReciever() {
		elevators = new ArrayList<Elevator>();
		for(int i=0; i< NUMBER_OF_ELEVATORS; i++) {
			elevators.add(new Elevator(i, this));
		}
		
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void recieverCommunicationLoop() {
		byte[] buffer;
		DatagramPacket packet;
		while (true) {
			buffer = new byte[4];
			packet = new DatagramPacket(buffer, buffer.length);
			try {
				datagramSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			processSchedulerMsg(buffer);
		}
	}
	
	// Receive msg from scheduler with floor number
	// TODO Update implementation when byte class is updated
	private void processSchedulerMsg(byte[] msg) {
		if (msg[0] == 4) {
			// New floor request
			if (msg[1] == 0) {
				// Voluntary Dest
				if (elevators.get(msg[2]).canServiceCall(msg[4])) {
					sendAcceptMsg();
					addFloorToService((int) msg[2], (int) msg[4]);
				} else {
					sendDeclineMsg();
				}
			} else if (msg[1] == 1) {
				// Mandatory
				sendAcceptMsg();
				addFloorToService((int) msg[2], (int) msg[4]);
				elevators.get(msg[2]).addToPassengerButtons(msg[4]);
			}
		} else if (msg[0] == 5) {
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
	private synchronized void addFloorToService(Integer elevatorNumber, Integer floor) {
		elevators.get(elevatorNumber).addToServiceQueue(floor);
		elevators.get(elevatorNumber).updateFloorToService();
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
	
	public static void main(String[] args) {
		ElevatorReciever elvReciever = new ElevatorReciever();
		elvReciever.recieverCommunicationLoop();
	}
}
