package newElevatorSystem;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class Elevator implements Runnable{
	
	private int currentFloor;
	public static final int MAX_SERVICE_QUEUE_CAPACITY = 10;
	private int id;
	private Comparator<Integer> floorComparator = (Integer a, Integer b) -> a.compareTo(b);
	private PriorityBlockingQueue<Integer> serviceScheduleQueue;
	public static final int elevatorSpeed = 1000;
	public static final int doorOpeningInterval = 3000;
	private boolean isDoorOpen;
	private ElevatorSystem elevatorSystem;
	
	public Elevator(int id, ElevatorSystem elevatorSystem) {
		this.id = id;
		this.elevatorSystem = elevatorSystem;
		currentFloor = 0;
		isDoorOpen = false;
		serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_QUEUE_CAPACITY, floorComparator);
	}
	
	public synchronized void addFloortoServiceQueue(int floor) {
		serviceScheduleQueue.add(floor);
		notifyAll();
	}
	
	public void run() {
		while (true) {
			elevatorAction();
		}
	}
	
	public synchronized void elevatorAction() {
		while (serviceScheduleQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
		int floorDestination = serviceScheduleQueue.poll();
		if (currentFloor > floorDestination) {
			while (currentFloor != floorDestination) {
				currentFloor--;
				try {
					Thread.sleep(elevatorSpeed);
				} catch (InterruptedException e) {
				}
				System.out.println("Elevator " + id + " is at floor " + currentFloor);
			}
		} else {
			while (currentFloor != floorDestination) {
				currentFloor++;
				try {
					Thread.sleep(elevatorSpeed);
				} catch (InterruptedException e) {
				}
				System.out.println("Elevator " + id + " is at floor " + currentFloor);
			}
		}
		isDoorOpen = true;
		System.out.println("Opening elevator doors");
		System.out.println("Sending packet to scheduler to tell floor to open the door\n");
		elevatorSystem.sendOpenDoorMsg(1, id, currentFloor);
		try {
			Thread.sleep(doorOpeningInterval);
		} catch (InterruptedException e) {
		}
		System.out.println("Closing elevator doors");
		System.out.println("Sending packet to scheduler to tell floor to close the door\n");
		isDoorOpen = false;
	}
	
}
