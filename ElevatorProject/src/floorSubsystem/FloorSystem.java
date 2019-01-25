package floorSubsystem;

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
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(floors.size());
		long delay;
		for (Message message : que) {
			
			delay = 1000;//ChronoUnit.MILLIS.between(LocalTime.now(), LocalTime.of(13, 5, 45));
			
			scheduler.schedule(new Callable() {
				public Object call() throws Exception {
					System.out.println("holy heck");
					return "Called!";
				}
			}, delay, TimeUnit.MILLISECONDS);
			
		}
	}
	
}
