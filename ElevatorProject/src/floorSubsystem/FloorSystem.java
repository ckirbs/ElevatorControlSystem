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

import resources.*;
import static resources.Constants.FLOOR_PORT;

import static resources.Constants.HIGHEST_FLOOR;

public class FloorSystem {
	
	private List<Floor> floors;
	private SystemFile systemFile;
	private Queue<Message> que;
	DatagramSocket datagramSocket;
	
	public static int STARTING_HOUR = 14;
	public static int STARTING_MINUTE = 0;
	
	public FloorSystem() {
		floors = new ArrayList<>();
		for (int i = 0; i < HIGHEST_FLOOR; i++) {
			floors.add(new Floor(i));
		}
		systemFile = new SystemFile(SystemFile.FILENAME1);
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
	}
	
	public void startFloorSchedule() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(HIGHEST_FLOOR);
		long delay;
		for (Message message : que) {
			delay = ChronoUnit.MILLIS.between(LocalTime.of(STARTING_HOUR, STARTING_MINUTE), message.getTime());
			scheduler.schedule(new Callable<Boolean>() {
				
				public Boolean call() throws Exception {
//					System.out.println("Someone on floor " + message.getStartingFloor() + " wants to go " + message.getDirection() + " to floor " + message.getDestinationFloor());
					byte direction;
					Floor floor = getFloorObjectByLevel(message.getStartingFloor());
					floor.addFloorButtonPressed(message.getDestinationFloor());
					if (message.getDirection() == Directions.UP) {
						floor.setUpButtonPressed(true);
						direction = 1;
					} else {
						floor.setDownButtonPressed(true);
						direction = 0;
					}
					printOutFloorInformation(floor, "A floor destination is chosen");
					byte[] buffer = new byte[]{(byte) 1, direction, (byte) message.getStartingFloor(), (byte) message.getDestinationFloor()};
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), FLOOR_PORT);
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
			try {
				datagramSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println(Arrays.toString(packet.getData()) + "\n");
			if (buffer[0] == (byte) 3) {
				for (Floor floor : floors) {
					if (floor.getLevel() == buffer[2]) {
						if (buffer[1] == (byte) 1) {
							floor.openDoor();
							printOutFloorInformation(floor, "door has opened");
//							System.out.println("Doors open on floor " + floor.getLevel());
						} else {
							floor.closeDoor();
							printOutFloorInformation(floor, "door has closed");
//							System.out.println("Doors close on floor " + floor.getLevel());
						}
						break;
					}
				}
			}
		}
	}
	
	public Floor getFloorObjectByLevel(int floorLevel) {
		for (Floor floor : floors) {
			if (floor.getLevel() == floorLevel) {
				return floor;
			}
		}
		return null;
	}
	
	public List<Floor> getFloors(){
		return floors;
	}
	
	public synchronized void printOutFloorInformation(Floor floor, String action) {
		System.out.print("\n" + action + ", ");
		System.out.print("On floor " + floor.getLevel() + ",");
		if (floor.isDoorOpen()) {
			System.out.print(" The door is open, ");
		} else {
			System.out.print(" The door is closed, ");
		}
		if (floor.isUpButtonPressed()) {
			System.out.print(" The up button pressed, ");
		}
		if (floor.isDownButtonPressed()) {
			System.out.print("The down button pressed, ");
		}
//		if (floor.isUpDirectionLampOn()) {
//			System.out.print("The up lamp is on, ");
//		}
//		if (floor.isDownDirectionLampOn()) {
//			System.out.print("The down lamp is on, ");
//		}
		if (!floor.getFloorDestinationButtonsPressed().isEmpty()) {
			System.out.print("and buttons ");
			for (int floorPressed : floor.getFloorDestinationButtonsPressed()) {
				System.out.print(floorPressed + " ");
			}
			System.out.print("are pressed");
		}
	}
	
}