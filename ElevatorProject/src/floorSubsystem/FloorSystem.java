package floorSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
	
	public static int STARTING_HOUR = 14;
	public static int STARTING_MINUTE = 0;
	
	public FloorSystem() {
		floors = new ArrayList<>();
		floors.add(new Floor(1));
		systemFile = new SystemFile("elevatorInputFile.txt");
		que = Message.getMessageQueue();
	}
	
	public static void main(String args[]) {
		FloorSystem floorSystem = new FloorSystem();
		floorSystem.startFloorSchedule();
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
					DatagramSocket datagramSocket = new DatagramSocket();
					byte[] buffer = new byte[]{ (byte) message.getStartingFloor(), (byte) message.getDestinatinoFloor(), direction};
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), 23);
					datagramSocket.send(packet);
					return true;
				}
				
			}, delay, TimeUnit.MILLISECONDS);
			
		}
	}
	
}
