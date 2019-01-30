package scheduler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static resources.Constants.ELEVATOR_PORT;
import static resources.Constants.MESSAGE_LENGTH;

public class ElevatorCommunicator extends Communicator implements Runnable {
	private DatagramSocket elevatorSocket;
	private DatagramPacket packet;
	
	public ElevatorListener() {
		super();
		try {
			this.elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
			this.elevatorSocket.setSoTimeout(TIMEOUT_TIME);
		} catch (SocketException e) {
			System.out.println("Error creating elevator socket.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void checkForMessages() {
		try {
			this.packet = new DatagramPacket(new byte[MESSAGE_LENGTH], MESSAGE_LENGTH);
			this.elevatorSocket.receive(packet);
			
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
