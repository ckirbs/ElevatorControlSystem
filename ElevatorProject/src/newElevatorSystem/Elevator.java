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
	
	public Elevator(int id) {
		this.id = id;
		currentFloor = 0;
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
		System.out.println("Sending packet to scheduler to open the door");
	}
	
}
