package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import scheduler.Dispatcher;
import static resources.Constants.*;
import resources.Directions;

public class Communicator {
	protected static Dispatcher dispatcher = new Dispatcher();
	protected final int TIMEOUT_TIME = 50;
	protected static int elevatorReturnPort;
	protected static int[] floorReturnPorts = new int[NUMBER_OF_FLOORS];
	
	public Communicator() {
	}
	
	
	public boolean dealWithMessage(byte[] message) {
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

	private boolean processStatusReport(byte dir, byte floorNum, byte elevatorNum) {
		return Communicator.dispatcher.updateElevatorInfo((int) elevatorNum, Directions.getDirByInt((int) dir) , (int) floorNum);
	}

	private boolean processConfirmation(byte yesNoVal, byte floorNum, byte elevatorNum) {
		
		return false;
	}

	private boolean openCloseDoor(byte openClose, byte floorNum, byte elevatorNum) {
		try {
			DatagramSocket tempSendingSocket = new DatagramSocket();
			byte[] msg = new byte[MESSAGE_LENGTH];
			msg[0] = OPEN_CLOSE_DOOR;
			msg[1] = openClose;
			msg[1] = openClose;
			msg[1] = openClose;
			
			DatagramPacket packet = new DatagramPacket(msg, MESSAGE_LENGTH, InetAddress.getByName("127.0.0.1"), floorReturnPorts[(int) floorNum]);
			tempSendingSocket.send(packet);
			tempSendingSocket.close();
			
		} catch (IOException e) {
			System.out.println("Error creating socket or sending message.");
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}

	private boolean processNewRequest(byte dir, byte origFloor, byte destFloor) {
		
		return false;
	}
}

