package elevatorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ElevatorReciever {

	private static final int NUMBER_OF_ELEVATORS = 1;
	private List<Elevator> elevators;
	private DatagramSocket datagramSocket;

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
		int scenerio = (int) msg[0];
		int reqType = (int) msg[1];
		int floorReq = (int) msg[2];
		int elvNum = (int) msg[3];
		
		
		if (scenerio == 4) {
			// New floor request
			if (reqType == 0) {
				// Voluntary Dest
				if (elevators.get(elvNum).canServiceCall(floorReq)) {
					sendResponse(elevators.get(elvNum).generateAcceptMsg(floorReq));
					addFloorToService(elvNum, floorReq);
				} else {
					sendResponse(elevators.get(elvNum).generateDeclineMsg(floorReq));
				}
			} else if (msg[1] == 1) {
				// Mandatory
				sendResponse(elevators.get(elvNum).generateAcceptMsg(floorReq));
				addFloorToService(elvNum, floorReq);
				elevators.get(elvNum).addToPassengerButtons(floorReq);
			}
		} else if (msg[0] == 5) {
			sendResponse(elevators.get(elvNum).generateSatusMsg());
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
	
	private synchronized void sendResponse(byte[] msg) {
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("127.0.0.1"), 23);
			datagramSocket.send(packet);
		} catch (Exception e) {
			//Failed generating response
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ElevatorReciever elvReciever = new ElevatorReciever();
		elvReciever.recieverCommunicationLoop();
	}
}
