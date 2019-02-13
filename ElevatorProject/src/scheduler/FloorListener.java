package scheduler;

import static resources.Constants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashSet;

/**
 * An object to listen to the floor system and deal with any received messages
 * 
 * @author Darren
 *
 */
public class FloorListener extends Communicator implements Runnable {
	private DatagramPacket packet;
	
	public FloorListener() {
		super();
		try {
			Communicator.floorSocket = new DatagramSocket(FLOOR_PORT);
		} catch (SocketException e) {
			System.out.println("Error creating floor socket.");
			e.printStackTrace();
			System.exit(1);
		}
		
		for (int e = 0; e < NUMBER_OF_ELEVATORS; e++) {
			for(int i = 0; i < NUMBER_OF_FLOORS; i++) {
				destinations.get(e).add(new HashSet<Integer>());
			}
		}
	}

	/**
	 * Check for incoming messages, address them if any
	 */
	private void checkForMessages() {
		try {
			byte[] message = new byte[MESSAGE_LENGTH];
			this.packet = new DatagramPacket(message, MESSAGE_LENGTH);
			Communicator.floorSocket.receive(packet);
			
			Communicator.floorPort = packet.getPort();

			this.handleNewMessage(message);
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
			super.retryDeniedReqs();
		}
	}

}
