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
import static resources.Constants.SCHED_IP_ADDRESS;

public class FloorSystem {
	
	private List<Floor> floors;
	private SystemFile systemFile;
	private Queue<Message> que;
	DatagramSocket datagramSocket;
	
	public static int STARTING_HOUR = 14;
	public static int STARTING_MINUTE = 0;
	
	public FloorSystem() {
		floors = new ArrayList<>();
		//creates a floor object for each floor
		for (int i = 0; i < HIGHEST_FLOOR; i++) {
			floors.add(new Floor(i));
		}
		//Reads in the file and puts all the messages into a queue
		systemFile = new SystemFile(SystemFile.FILENAME1);
		systemFile.readValidateAndCreateMessages();
		que = Message.getMessageQueue();
		try {
			//create the datagram socket to send packets to the scheduler
			datagramSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	/**
	 * start the reading in the floor events from the text file and send them to the scheduler
	 * @param args
	 */
	public static void main(String args[]) {
		FloorSystem floorSystem = new FloorSystem();
		floorSystem.startFloorSchedule();
		floorSystem.schedulerCommunicationLoop();
	}
	/**
	 * Reads in each floor event and schedules them to be sent to the scheduler when it is supposed to occur
	 */
	public void startFloorSchedule() {
		//create a thread for each floor
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(HIGHEST_FLOOR);
		long delay;
		for (Message message : que) {
			//calculates the delay to run the thread
			delay = ChronoUnit.MILLIS.between(LocalTime.of(STARTING_HOUR, STARTING_MINUTE), message.getTime());
			scheduler.schedule(new Callable<Boolean>() {
				
				public Boolean call() throws Exception {
					byte direction;
					// finds the floor depending on the starting floor
					Floor floor = getFloorObjectByLevel(message.getStartingFloor());
					//takes the data from the message object and puts it into a floor object
					floor.addFloorButtonPressed(message.getDestinationFloor());
					if (message.getDirection() == Directions.UP) {
						floor.setUpButtonPressed(true);
						direction = 1;
					} else {
						floor.setDownButtonPressed(true);
						direction = 0;
					}
					printOutFloorInformation(floor, "A floor destination is chosen");
					//puts the information of the floor event into a packet and sends it to the scheduler
					byte[] buffer = new byte[]{(byte) 1, direction, (byte) message.getStartingFloor(), (byte) message.getDestinationFloor()};
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SCHED_IP_ADDRESS), FLOOR_PORT);
					datagramSocket.send(packet);
					return true;
				}
				
			}, delay, TimeUnit.MILLISECONDS);
			
		}
	}
	/**
	 * gets a packet from the scheduler which can either be a door open or door close command
	 */
	public void schedulerCommunicationLoop() {
		byte[] buffer;
		DatagramPacket packet;
		while (true) {
			buffer = new byte[4];
			packet = new DatagramPacket(buffer, buffer.length);
			try {
				//wait to receive a packet from the scheduler
				datagramSocket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//if it is a door open or door close command
			if (buffer[0] == (byte) 3) {
				for (Floor floor : floors) {
					//if it is the right floor
					if (floor.getLevel() == buffer[2]) {
						//open the door on the floor
						if (buffer[1] == (byte) 1) {
							floor.openDoor();
							printOutFloorInformation(floor, "door has opened");
						} else {
							//close the door on the floor
							floor.closeDoor();
							printOutFloorInformation(floor, "door has closed");
						}
						break;
					}
				}
			}
		}
	}
	/**
	 *  Get the floor object from the level
	 * @param floorLevel  the floor level to compare
	 * @return
	 */
	public Floor getFloorObjectByLevel(int floorLevel) {
		for (Floor floor : floors) {
			//if the level matches the floor in the object then return that
			if (floor.getLevel() == floorLevel) {
				return floor;
			}
		}
		return null;
	}
	/**
	 * return the list of floors
	 * @return
	 */
	public List<Floor> getFloors(){
		return floors;
	}
	/**
	 * Prints out all information about a floor
	 * @param floor  the floor to print out
	 * @param action  the action causing the information to be printed out eg. a door opens
	 */
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
		if (!floor.getFloorDestinationButtonsPressed().isEmpty()) {
			System.out.print("and buttons ");
			for (int floorPressed : floor.getFloorDestinationButtonsPressed()) {
				System.out.print(floorPressed + " ");
			}
			System.out.print("are pressed");
		}
	}
	
	public Queue<Message> getQue(){
		return que;
	}
	
}