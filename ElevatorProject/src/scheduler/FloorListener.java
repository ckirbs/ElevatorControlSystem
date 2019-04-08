package scheduler;

import static resources.Constants.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
			Communicator.floorSocket.setSoTimeout(TIMEOUT_TIME);
		} catch (SocketException e) {
			System.out.println("Error creating floor socket.");
			e.printStackTrace();
			System.exit(1);
		}
		
		for (int e = 0; e < NUMBER_OF_ELEVATORS; e++) {
			ArrayList<Set<Integer>> tempList = new ArrayList<Set<Integer>>();
			for(int i = 0; i < NUMBER_OF_FLOORS; i++) {
				tempList.add(new HashSet<Integer>());
			}
			Communicator.destinations.add(tempList);
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
		} catch(SocketTimeoutException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		while (true) {
			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					Communicator.updateDispatcher();
				}
			}, 0, 500, TimeUnit.MILLISECONDS);
			checkForMessages();
			super.retryDeniedReqs();
		}
	}

}
