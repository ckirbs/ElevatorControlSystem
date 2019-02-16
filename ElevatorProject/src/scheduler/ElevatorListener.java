package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static resources.Constants.MESSAGE_LENGTH;

/**
 * An object to listen to the elevator system and deal with any received messages
 * 
 * @author Darren
 *
 */
public class ElevatorListener extends Communicator implements Runnable {
	private DatagramPacket packet;
	
	public ElevatorListener() {
		super();
		try {
			Communicator.elevatorSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Error creating elevator socket.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Check for incoming messages, address them if any
	 */
	private void checkForMessages() {
		try {
			byte[] message = new byte[MESSAGE_LENGTH];
			this.packet = new DatagramPacket(message, MESSAGE_LENGTH);
			Communicator.elevatorSocket.receive(packet);
			
			Communicator.elevatorReturnPort = packet.getPort();
			
			this.handleNewMessage(message);
			
		} catch (IOException e) {
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
