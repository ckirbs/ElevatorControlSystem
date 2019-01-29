package floorSubsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Resources.*;

public class FloorSystem {
	
	private List<Floor> floors;
	private SystemFile systemFile;
	private Queue<Message> que;
	DatagramSocket datagramSocket;
	
	public static int STARTING_HOUR = 14;
	public static int STARTING_MINUTE = 0;
	
	public FloorSystem() {
		floors = new ArrayList<>();
		for (int i = 0; i < SystemFile.HIGHESTFLOOR; i++) {
			floors.add(new Floor(i));
		}
		systemFile = new SystemFile("elevatorInputFile.txt");
		systemFile.readValidateAndCreateMessages();
		que = Message.getMessageQueue();
		try {
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		FloorSystem floorSystem = new FloorSystem();
		floorSystem.startFloorSchedule();
		floorSystem.schedulerCommunicationLoop();
		//DateFormat dateFormatter = new SimpleDateFormat("hh:mm:ss.mmm");
	}
	
	public void startFloorSchedule() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
		long delay;
		for (Message message : que) {
			delay = ChronoUnit.MILLIS.between(LocalTime.of(STARTING_HOUR, STARTING_MINUTE), message.getTime());
			scheduler.schedule(new Callable<Boolean>() {
				
				public Boolean call() throws Exception {
					System.out.println("Someone on floor " + message.getStartingFloor() + " wants to go " + message.getDirection() + " to floor " + message.getDestinatinoFloor());
					byte direction = 0;
					if (message.getDirection() == Directions.UP) {
						direction = 1;
					}
					byte[] buffer = new byte[]{(byte) 1, (byte) message.getStartingFloor(), (byte) message.getDestinatinoFloor(), direction};
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), 23);
					datagramSocket.send(packet);
					return true;
				}
				
			}, delay, TimeUnit.MILLISECONDS);
			
		}
	}
	
	public void schedulerCommunicationLoop() {
		byte[] buffer;
		DatagramPacket packet;
		while (true) {
			buffer = new byte[4];
			packet = new DatagramPacket(buffer, buffer.length);
			Floor floor = null;
			try {
				datagramSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(Arrays.toString(packet.getData()) + "\n");
			if (buffer[0] == (byte) 3) {
				for (Floor tempFloor : floors) {
					if (tempFloor.getLevel() == buffer[2]) {
						floor = tempFloor;
						break;
					}
				}
			}
			if (floor != null) {
				if (buffer[1] == (byte) 1) {
					floor.openDoor();
					System.out.println("Doors open on floor " + floor.getLevel());
				} else {
					floor.closeDoor();
					System.out.println("Doors close on floor " + floor.getLevel());
				}
			}
		}
	}
	
}

