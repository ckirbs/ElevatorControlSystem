package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import scheduler.Dispatcher;
import static resources.Constants.*;
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
	protected static ArrayList<ArrayList<Set<Integer>>> destinations = new ArrayList<ArrayList<Set<Integer>>>();
	protected static ArrayList<byte[]> deniedReqs = new ArrayList<byte[]>();
	protected static ArrayList<byte[]> pendingReqs = new ArrayList<byte[]>();
	private static int currReqId;
	
	public Communicator() {
		Communicator.currReqId = 0;
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
		byte val3 = message[4]; 
		
		switch (messageType) {
		case (byte) NEW_REQUEST_FROM_FLOOR: return this.processNewRequest(flag, val1, val2);
		case (byte) OPEN_CLOSE_DOOR: return this.openCloseDoor(flag, val1, val2, val3);
		case (byte) CONFIRM_VOL_DESTINATION: return this.processConfirmation(flag, val1, val2, val3);
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
	private boolean processConfirmation(byte yesNoVal, byte floorNum, byte elevatorNum, byte id) {
		byte[] req = null;
		synchronized (pendingReqs) {
			for (byte[] b: pendingReqs) {
				if (b[0] == id) {
					req = b;
					pendingReqs.remove(b);
					break;
				}
			}
		}
		
		if (yesNoVal == NO) {
			synchronized (deniedReqs) {
				deniedReqs.add(new byte[] {req[1], req[2], req[3]});
			}
			return false;
		} else {
			synchronized (destinations) {
				destinations.get((int) elevatorNum).get((int) floorNum).add((int) req[3]);
			}
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
	private boolean openCloseDoor(byte openClose, byte floorNum, byte elevatorNum, byte dir) {
		try {
			byte[] msg = new byte[MESSAGE_LENGTH];
			msg[0] = OPEN_CLOSE_DOOR;
			msg[1] = openClose;
			msg[2] = floorNum;
			msg[3] = elevatorNum;
			msg[4] = dir;
			
			System.out.println(((openClose == OPEN) ? "Opening " : "Closing ") + "doors on floor " + (int) floorNum + " for elevator " + (int) elevatorNum);
			
			DatagramPacket packet = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getByName(FLOOR_SYS_IP_ADDRESS), floorPort);
			floorSocket.send(packet);
			
			if (openClose == OPEN) {
				Set<Integer> tempSet;
				synchronized (destinations) {
					tempSet = destinations.get((int) elevatorNum).get((int) floorNum);
					destinations.get((int) elevatorNum).set((int) floorNum, new HashSet<Integer>());
				}
				
				System.out.println("Sending elevator " + (int) elevatorNum + " new destinations: " + tempSet.toString());
				
				DatagramPacket pckt;
				byte[] destMsg = new byte[MESSAGE_LENGTH];
				destMsg[0] = NEW_ELEVATOR_DESTINATION;
				destMsg[1] = MANDATORY;
				destMsg[3] = elevatorNum; // elevator number
				destMsg[4] = (byte) 0; // Direction (not used atm)
				
				for (int i: tempSet) {
					destMsg[2] = (byte) i;
					pckt = new DatagramPacket(destMsg, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
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
		
		//destinations.get((int) origFloor).add((int) destFloor);
		
		byte[] message = new byte[MESSAGE_LENGTH];
		message[0] = NEW_ELEVATOR_DESTINATION;
		message[1] = VOLUNTARY;
		message[2] = origFloor;
		message[4] = dir;
		message[5] = (byte) Communicator.currReqId;
		Communicator.currReqId++;
		
		System.out.println("Sending elevator new destination: " + (int) origFloor + " Direction: " + Directions.getDirByInt((int) dir));
		
		Communicator.updateDispatcher();
		
		int elevatorNumber = Communicator.dispatcher.getNearestElevator(Directions.getDirByInt((int) dir), (int) origFloor);
		
		if (elevatorNumber != -1) {
			pendingReqs.add(new byte[] {message[5], dir, origFloor, destFloor, (byte) elevatorNumber});
			try {
				message[3] = (byte) elevatorNumber;
				DatagramPacket pckt = new DatagramPacket(message, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return false;
		}

	}
	
	private static synchronized void updateDispatcher() {
		DatagramPacket pckt;
		
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			byte[] msg = new byte[] {ELEVATOR_INFO_REQUEST, (byte) 0, (byte) 0, (byte) i, (byte) 0};
			try {
				pckt = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Retry all of the voluntary requests that were previously denied
	 */
	protected void retryDeniedReqs() {
		synchronized (deniedReqs) {
			for (byte[] req: deniedReqs) {
				processNewRequest(req[0], req[1], req[2]);
			}
			deniedReqs.clear();
		}
	}
}
