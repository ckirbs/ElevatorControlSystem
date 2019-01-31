package scheduler;

import static resources.Constants.FLOOR_PORT;
import static resources.Constants.MESSAGE_LENGTH;

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
		} catch (SocketException e) {
			System.out.println("Error creating floor socket.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void checkForMessages() {
		try {
			byte[] message = new byte[MESSAGE_LENGTH];
			this.packet = new DatagramPacket(message, MESSAGE_LENGTH);
			this.floorSocket.receive(packet);
			
			Communicator.floorReturnPorts[(int) message[2]] = packet.getPort();

			this.dealWithMessage(message);
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
