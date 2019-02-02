package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static resources.Constants.ELEVATOR_PORT;
import static resources.Constants.MESSAGE_LENGTH;

/**
 * 
 * @author Darren
 *
 */
public class ElevatorListener extends Communicator implements Runnable {
	private DatagramPacket packet;
	
	public ElevatorListener() {
		super();
		try {
			this.elevatorSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Error creating elevator socket.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void checkForMessages() {
		try {
			byte[] message = new byte[MESSAGE_LENGTH];
			this.packet = new DatagramPacket(message, MESSAGE_LENGTH);
			this.elevatorSocket.receive(packet);
			
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
