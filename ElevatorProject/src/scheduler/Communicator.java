package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;

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
	protected final int TIMEOUT_TIME = 1000;
	protected static int elevatorReturnPort;
	protected static int floorPort;
	protected static ArrayList<ArrayList<Set<Integer>>> destinations = new ArrayList<ArrayList<Set<Integer>>>();
	protected static ArrayList<byte[]> deniedReqs = new ArrayList<byte[]>();
	private static ArrayList<byte[]> tempDeniedHolder = new ArrayList<byte[]>();
	protected static ArrayList<byte[]> pendingReqs = new ArrayList<byte[]>();
	private static int currReqId;
	private static Set<Integer> pendingMandReqs = new HashSet<Integer>();
	
	public Communicator() {
		Communicator.currReqId = 1;
	}
	
	/**
	 * Takes in a message and performs the appropriate actions on it
	 * 
	 * @param message	The byte array of the message
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
		case (byte) ERROR: return this.processErrorRequest(flag, val1, val2);
		default: return false;
		}
	}

	/**
	 * Handles a status report from an elevator, and updates dispatcher
	 * 
	 * @param dir			The elevator's direction
	 * @param floorNum		The elevator's current floor
	 * @param elevatorNum	The elvator's id number
	 * @return
	 */
	private boolean processStatusReport(byte dir, byte floorNum, byte elevatorNum) {
		System.out.println(FORMATTER.format(new Date()) + ": Updating status of elev " + (int) elevatorNum);
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
		// Confirmation for mandatory requests - no action needed
		if (id == (byte) 0) return true;
		
		// Find the corresponding pending request and remove from the list
		byte[] req = null;
		synchronized (pendingReqs) {
			for (byte[] b: pendingReqs) {
				if (b[0] == id) {
					req = b;
					break;
				}
			}
			pendingReqs.remove(req);
		}
		
		// If the request was a mandatory destination
		if (req == null && pendingMandReqs.contains((int)id)) {
			pendingMandReqs.remove((int)id);
			return true;
		// If the request id is invalid
		} else if (req == null) {
			System.err.println(FORMATTER.format(new Date()) + ": Confirmation error for floor: " + floorNum + ", elevator: " + elevatorNum + ", request id: " + id);
			return false;
		}
		
		// If denied, add the request to the denied requests list
		if (yesNoVal == NO) {
			synchronized (Communicator.deniedReqs) {
				Communicator.deniedReqs.add(new byte[] {req[1], req[2], req[3]});
			}
			return false;
			
		// If accepted, add the new destinations to the elevators future destination set
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
			
			System.out.println(FORMATTER.format(new Date()) + ": " + ((openClose == OPEN) ? "Opening " : "Closing ") + "doors on floor " + (int) floorNum + " for elevator " + (int) elevatorNum);
			
			// Pass the message to the floor
			DatagramPacket packet = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getByName(FLOOR_SYS_IP_ADDRESS), floorPort);
			floorSocket.send(packet);
			
			// If it's an open request, check for any new destination (i.e. from passengers getting on)
			if (openClose == OPEN) {
				Set<Integer> tempSet;
				synchronized (destinations) {
					tempSet = destinations.get((int) elevatorNum).get((int) floorNum);
					destinations.get((int) elevatorNum).set((int) floorNum, new HashSet<Integer>());
				}
				
				System.out.println(FORMATTER.format(new Date()) + ": Sending elevator " + (int) elevatorNum + " new destinations: " + tempSet.toString());
				
				// Construct the generic message structure
				DatagramPacket pckt;
				byte[] destMsg = new byte[MESSAGE_LENGTH];
				destMsg[0] = NEW_ELEVATOR_DESTINATION;
				destMsg[1] = MANDATORY;
				destMsg[3] = elevatorNum;
				
				// For each new destination, send it to the elevator
				for (int i: tempSet) {
					destMsg[2] = (byte) i;
					// Give id to mandatory request and increment it
					destMsg[4] = (byte) currReqId;
					pendingMandReqs.add(currReqId);
					currReqId ++;
					
					// Send the packet
					pckt = new DatagramPacket(destMsg, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
					elevatorSocket.send(pckt);
				}
			}
			
		} catch (IOException e) {
			System.out.println(FORMATTER.format(new Date()) + ": Error creating socket or sending message.");
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
		
		// Create the request, giving it an id
		byte[] message = new byte[MESSAGE_LENGTH];
		message[0] = NEW_ELEVATOR_DESTINATION;
		message[1] = VOLUNTARY;
		message[2] = origFloor;
		message[4] = dir;
		message[5] = (byte) Communicator.currReqId;
		Communicator.currReqId++;
				
		// Update the dispatcher information
		Communicator.updateDispatcher();
		
		// Pick an elevator to send a request to
		int elevatorNumber = Communicator.dispatcher.getNearestElevator(Directions.getDirByInt((int) dir), (int) origFloor);
		
		/*if (Directions.getDirByInt((int) dir) == Directions.ERROR_MOVE || Directions.getDirByInt((int) dir) == Directions.ERROR_DOOR) {
			message[0] = ERROR;
		}*/
		
		// This method allows us to add more errors in the future rather than just the two 
		// we currently have
		if ((int) dir >= Directions.getIntByDir(Directions.ERROR_DEFAULT)) {
			message[0] = ERROR;
		}
		
		// If an elevator was chosen
		if (elevatorNumber != -1) {
			System.out.println(FORMATTER.format(new Date()) + ": Sending elevator " + elevatorNumber + " new destination: " + (int) origFloor + " Direction: " + Directions.getDirByInt((int) dir));
			
			// The request is now pending
			pendingReqs.add(new byte[] {message[5], dir, origFloor, destFloor, (byte) elevatorNumber});
			
			// Send the request to the elevator
			try {
				message[3] = (byte) elevatorNumber;
				DatagramPacket pckt = new DatagramPacket(message, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
			
		// If no elevator was chosen, consider the request denied
		} else {
			synchronized (Communicator.deniedReqs) {
				Communicator.deniedReqs.add(new byte[] {dir, origFloor, destFloor});
			}
			return false;
		}

	}
	
	/**
	 * Method to set elevators into "broken" states, where the doors are stuck open or the elevator 
	 * is stuck between floors.
	 * 
	 * 
	 * @param dir
	 * @param elevatorNumber
	 * @param timer
	 * @return
	 */
	private boolean processErrorRequest(byte dir, byte elevatorNumber, byte timer) {
		// Create the request, giving it an id
		byte[] message = new byte[MESSAGE_LENGTH];
		message[0] = ERROR;
		message[1] = MANDATORY;
		message[2] = elevatorNumber;
		message[4] = dir;
		message[5] = (byte) Communicator.currReqId;
		Communicator.currReqId++;
		
		// Update the dispatcher information
		Communicator.updateDispatcher();
		
		// If an elevator was chosen
		if (elevatorNumber != -1) {
			System.out.println(FORMATTER.format(new Date()) + ": Sending elevator " + elevatorNumber + " an error notice: " + Directions.getDirByInt((int) dir));
			
			// The request is now pending
			pendingReqs.add(new byte[] {message[5], dir, elevatorNumber, timer, (byte) elevatorNumber});
			
			// Send the request to the elevator
			try {
				message[3] = (byte) elevatorNumber;
				DatagramPacket pckt = new DatagramPacket(message, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
			
		// If no elevator was chosen, consider the request denied
		} else {
			synchronized (Communicator.deniedReqs) {
				Communicator.deniedReqs.add(new byte[] {dir, elevatorNumber, timer});
			}
			
			return false;
		}
	}
	
	/**
	 * Sends a status request to each elevator so that the dispatcher can update its data.
	 */
	private static synchronized void updateDispatcher() {
		DatagramPacket pckt;
		
		// For each elevator
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			// Generate and send a status report request
			byte[] msg = new byte[] {ELEVATOR_INFO_REQUEST, (byte) 0, (byte) 0, (byte) i, (byte) 0, (byte) 0};
			try {
				pckt = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getByName(ELEVATOR_SYS_IP_ADDRESS), ELEVATOR_PORT);
				elevatorSocket.send(pckt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Sleep for a brief period to give time for responses
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Retry all of the voluntary requests that were previously denied
	 */
	protected synchronized void retryDeniedReqs() {
		synchronized (Communicator.deniedReqs) {
			for (byte[] x: Communicator.deniedReqs) Communicator.tempDeniedHolder.add(x);
			
			Communicator.deniedReqs.clear();
			
			for (byte[] req: Communicator.tempDeniedHolder) {
				processNewRequest(req[0], req[1], req[2]);
			}
			
			Communicator.tempDeniedHolder.clear();
		}
	}
}
