package Scheduler;

import static Resources.Constants.ELEVATOR_PORT;
import static Resources.Constants.FLOOR_PORT;
import static Resources.Constants.MESSAGE_LENGTH;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class FloorListener extends Communicator implements Runnable {
	private DatagramSocket floorSocket;
	private DatagramPacket packet;
	
	public FloorListener() {
		super();
		try {
			this.floorSocket = new DatagramSocket(FLOOR_PORT);
			this.floorSocket.setSoTimeout(TIMEOUT_TIME);
		} catch (SocketException e) {
			System.out.println("Error creating floor socket.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void checkForMessages() {
		try {
			this.packet = new DatagramPacket(new byte[MESSAGE_LENGTH], MESSAGE_LENGTH);
			this.floorSocket.receive(packet);
			
//			decode message
//			act accordingly
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		while (true) {
			checkForMessages();
		}
	}

}
