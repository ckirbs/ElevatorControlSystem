package elevatorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import resources.Directions;
import static resources.Constants.*;

/**
 * ElevatorReceiver Responsible for receiving, interpreting, and assigning
 * request sent via the scheduler. Generates X elevators for a given subsystem
 * 
 * @author Callum Kirby
 * @version 1.0
 *
 */
public class ElevatorReciever {

	private static final int NUMBER_OF_ELEVATORS = 1;
	private List<Elevator> elevators;
	private DatagramSocket schedulerSocket;
	int messagePort;

	public ElevatorReciever() {
		elevators = new ArrayList<Elevator>();
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			elevators.add(new Elevator(i, this));
		}

		try {
			schedulerSocket = new DatagramSocket(ELEVATOR_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void recieverCommunicationLoop() {
		byte[] buffer;
		DatagramPacket packet;
		while (true) {
			buffer = new byte[MESSAGE_LENGTH];
			packet = new DatagramPacket(buffer, buffer.length);
			try {
				schedulerSocket.receive(packet);
				messagePort = packet.getPort();
				processSchedulerMsg(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * processSchedulerMsg() interpret messages received from scheduler and pass to
	 * the elevator indicated in the body
	 * 
	 * @param packet containing requestPort and messageInfo (Type, request, floor,
	 *               and elevator)
	 */
	public void processSchedulerMsg(DatagramPacket packet) {

		byte[] msg = packet.getData();

		int scenerio = (int) msg[0];
		int reqType = (int) msg[1];
		int floorReq = (int) msg[2];
		int elvNum = (int) msg[3];
		int dirReq = (int) msg[4];

		if (scenerio == NEW_ELEVATOR_DESTINATION) {
			// New floor request
			if (reqType == VOLUNTARY) {
				// Voluntary Dest
				if (elevators.get(elvNum).canServiceCall(floorReq)) {
					sendResponse(elevators.get(elvNum).generateAcceptMsg(floorReq), packet.getPort());
					addFloorToService(elvNum, floorReq, Directions.getDirByInt(dirReq));
				} else {
					sendResponse(elevators.get(elvNum).generateDeclineMsg(floorReq), packet.getPort());
				}
			} else if (reqType == MANDATORY) {
				// Mandatory
				sendResponse(elevators.get(elvNum).generateAcceptMsg(floorReq), packet.getPort());
				// Only works if Mandatory means inside elevator request
				addFloorToService(elvNum, floorReq); 
				elevators.get(elvNum).addToPassengerButtons(floorReq);
			}
		} else if (reqType == ELEVATOR_INFO_REQUEST) {
			sendResponse(elevators.get(elvNum).generateSatusMsg(), packet.getPort());
		}
	}

	/**
	 * addFloorToService() adds request received via message, if current floor ==
	 * startingFloor treaded as internalElevatorPanel request (button glows) else
	 * treated as floor request and added to service route
	 * 
	 * @param floor containing calling floor and
	 */
	public synchronized void addFloorToService(Integer elevatorNumber, Integer floor, Directions direction) {
		elevators.get(elevatorNumber).addToServiceList(floor, direction);
		elevators.get(elevatorNumber).updateFloorToService();
	}
	
	/**
	 * addFloorToService() adds request to the serviceList, calls select floor
	 * for adding the floor to the correct serviceList
	 * 
	 * @param  elevatorNumber: the elevator that is servicing the request
	 * @param  floor: the floor the user wants to go to
	 */
	public synchronized void addFloorToService(Integer elevatorNumber, Integer floor) {
		elevators.get(elevatorNumber).addToServiceList(floor);
		elevators.get(elevatorNumber).updateFloorToService();
	}

	/**
	 * sendMessage() sends a new message to send to the scheduler (async on floor
	 * arrival)
	 * 
	 * @param msg contains doorOpen/doorClose signal
	 */
	public void sendMessage(byte[] msg) {
		DatagramPacket packet;

		try {
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(Constants.SCHED_IP_ADDRESS), messagePort);
			schedulerSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * sendResponse() send a response to a message received from the scheduler
	 * 
	 * @param msg  containing confirm/deny/acknowledgment of request
	 * @param port address that the originating message was received on
	 */
	private synchronized void sendResponse(byte[] msg, int port) {
		DatagramPacket packet;
		try {
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(Constants.SCHED_IP_ADDRESS), port);
			schedulerSocket.send(packet);
		} catch (Exception e) {
			// Failed generating response
			e.printStackTrace();
		}
	}

	public List<Elevator> getElevators() {
		return elevators;
	}

	public static void main(String[] args) {
		ElevatorReciever elvReciever = new ElevatorReciever();
		elvReciever.recieverCommunicationLoop();
	}
	
	public void closeSocket() {
		schedulerSocket.close();
	}
}
