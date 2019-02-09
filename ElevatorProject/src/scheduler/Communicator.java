package scheduler;

import static resources.Constants.CONFIRM_VOL_DESTINATION;
import static resources.Constants.ELEVATOR_PORT;
import static resources.Constants.MANDATORY;
import static resources.Constants.MESSAGE_LENGTH;
import static resources.Constants.NEW_ELEVATOR_DESTINATION;
import static resources.Constants.NEW_REQUEST_FROM_FLOOR;
import static resources.Constants.NO;
import static resources.Constants.OPEN;
import static resources.Constants.OPEN_CLOSE_DOOR;
import static resources.Constants.STATUS_REPORT;
import static resources.Constants.VOLUNTARY;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import resources.Directions;

/**
 * A class to handle communications, and handling of messages that the Scheduler system receives
 * 
 * 
 * @author Darren, Logan
 *
 */
public class Communicator {
	protected static DatagramSocket floorSocket, elevatorSocket;
	protected static Dispatcher dispatcher = new Dispatcher();
	protected final int TIMEOUT_TIME = 50;
	protected static int elevatorReturnPort;
	protected static int floorPort;
	protected static ArrayList<Set<Integer>> destinations = new ArrayList<Set<Integer>>();
	protected static ArrayList<byte[]> deniedReqs = new ArrayList<byte[]>();
	
	public Communicator() {
		
	}
	
	/**
	 * Takes in a message and preforms the appropriate actions on it
	 * 
	 * @param message	The byte array of hte message
	 * @return			True if properly handled, false otherwise
	 */
	public boolean handleNewMessage(byte[] message) {
		byte messageType = message[0];
		byte flag = message[1];
		byte val1 = message[2];
		byte val2 = message[3]; 
		
		switch (messageType) {
		case (byte) NEW_REQUEST_FROM_FLOOR: return this.processNewRequest(flag, val1, val2);
		case (byte) OPEN_CLOSE_DOOR: return this.openCloseDoor(flag, val1, val2);
		case (byte) CONFIRM_VOL_DESTINATION: return this.processConfirmation(flag, val1, val2);
		case (byte) STATUS_REPORT: return this.processStatusReport(flag, val1, val2);
		default: return false;
		}
	}

	/**
	 * Handles a status report from an elvator, and updates dispatcher
	 * 
	 * @param dir			The elevator's direction
	 * @param floorNum		The elevator's current floor
	 * @param elevatorNum	The elvator's id number
	 * @return
	 */
	private boolean processStatusReport(byte dir, byte floorNum, byte elevatorNum) {
		return Communicator.dispatcher.updateElevatorInfo((int) elevatorNum, Directions.getDirByInt((int) dir) , (int) floorNum);
	}

	/** 
	 * If a voluntary request was denied, add it to a list to be retried later.
	 * 
	 * @param yesNoVal
	 * @param floorNum
	 * @param elevatorNum
	 * @return
	 */
	private synchronized boolean processConfirmation(byte yesNoVal, byte floorNum, byte elevatorNum) {
		if (yesNoVal == NO) {
			deniedReqs.add(new byte[] {NEW_ELEVATOR_DESTINATION, VOLUNTARY, floorNum, elevatorNum});
			return false;
		}
		
		return true;
	}

	/**
	 * Handles open and close door requests from the elevator
	 * 
	 * @param openClose		Flag for if the door is opening or closing
	 * @param floorNum		The floor that the elevator is on
	 * @param elevatorNum	The elevator id
	 * @return
	 */
	private boolean openCloseDoor(byte openClose, byte floorNum, byte elevatorNum) {
		try {
			byte[] msg = new byte[MESSAGE_LENGTH];
			msg[0] = OPEN_CLOSE_DOOR;
			msg[1] = openClose;
			msg[2] = floorNum;
			msg[3] = elevatorNum;
			
			System.out.println(((openClose == OPEN) ? "Opening " : "Closing ") + "doors on floor " + (int) floorNum + " for elevator " + (int) elevatorNum);
			
			DatagramPacket packet = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getLocalHost(), floorPort);
			floorSocket.send(packet);
			
			if (openClose == OPEN) {
				Set<Integer> tempSet = destinations.get((int) floorNum);
				destinations.set((int) floorNum, new HashSet<Integer>());
				
				System.out.println("Sending elevator " + (int) elevatorNum + " new destinations: " + tempSet.toString());
				
				DatagramPacket pckt;
				byte[] destMsg = new byte[MESSAGE_LENGTH];
				destMsg[0] = NEW_ELEVATOR_DESTINATION;
				destMsg[1] = MANDATORY;
				destMsg[3] = (byte) 0;
				
				for (int i: tempSet) {
					destMsg[2] = (byte) i;
					pckt = new DatagramPacket(destMsg, MESSAGE_LENGTH, InetAddress.getLocalHost(), ELEVATOR_PORT);
					elevatorSocket.send(pckt);
				}
			}
			
		} catch (IOException e) {
			System.out.println("Error creating socket or sending message.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * Method to handle new requests
	 * 	
	 * @param dir			The direction of travel
	 * @param origFloor		The origin floor (where the passenger is)
	 * @param destFloor		The destination floor (where the passenger wants to go)
	 * @return
	 */
	private boolean processNewRequest(byte dir, byte origFloor, byte destFloor) {
		
		destinations.get((int) origFloor).add((int) destFloor);
		
		byte[] message = new byte[MESSAGE_LENGTH];
		message[0] = NEW_ELEVATOR_DESTINATION;
		message[1] = VOLUNTARY;
		message[2] = origFloor;
		message[3] = (byte) 0; //elevator id
		
		System.out.println("Sending elevator new destination: " + (int) origFloor);
		
		try {
			DatagramPacket pckt = new DatagramPacket(message, MESSAGE_LENGTH, InetAddress.getLocalHost(), ELEVATOR_PORT);
			elevatorSocket.send(pckt);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Retry all of the voluntary requests that were previously denied
	 */
	protected static synchronized void retryDeniedReqs() {
		for (byte[] msg: deniedReqs) {
			DatagramPacket pckt;
			try {
				pckt = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getLocalHost(), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		deniedReqs.clear();
	}
}
