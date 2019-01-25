package elevatorSubsystem;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import Resources.Directions;

public class Elevator {

	private static final int FLOOR_TRAVEL_SPEED_MS = 1000;
	private static final int MAX_SERVICE_CAPACITY = 10;

	private int currFloorPosition, floorDestionation;
	private Queue<Integer> serviceScheduleQueue;
	private Directions status;
	private Comparator<Integer> floorComparator = (Integer a, Integer b) -> a.compareTo(b);
	
	//TODO Two/Three threads are necessary for the function of the elevator
	// -- One Responsible for the travel of the elevator (traveling between destinations)
	// -- One Responsible for receiving and handling floor request from the scheduler
	// -- (Possibly) One responsible for listening to floor requests within the elevator

	public Elevator() {
		floorDestionation = -1;
		currFloorPosition = 0;
		status = Directions.STANDBY;
	}

	private void run() {
		//-------
		//THREAD 1 - MOTOR/MOVEMENT
		if(currFloorPosition == floorDestionation) {
			//open doors - remove floor from queue and grab next to service
			serviceScheduleQueue.poll();
			nextFloorToService();
		} else {
			//Continue traveling to destinationFloor
		}
		//-------

		//-------
		// THREAD 2 - SCHED LISTENER
		// Listen and Receive task from Scheduler
		replySchedulerRequest("MessageClass");
		//-------
	}

	// Receive msg from scheduler with floor number
	private void replySchedulerRequest(String msg) {
		// get floor Requested from msg
		Integer floorRequest = 0;

		if (canServiceCall(floorRequest)) {
			serviceScheduleQueue.add(floorRequest);
			// send reply accepting assignment
		}
		// send reply declining the assignment
	}

	private void nextFloorToService() {
		if (!serviceScheduleQueue.isEmpty()) {
			floorDestionation = serviceScheduleQueue.peek();
		} else {
			// No floors to service, wait to receive request
			status = Directions.STANDBY;
		}
	}

	// Checks to see if the current elevator can accept a floor service request
	// returns true if request is along current route, false if outside
	// Standby = Initializes new priorityQueue with priority depending on floor requested
	
	//TODO Logic assumes a user will never indicate the wrong elevator call on a floor request (Clicks 'UP' on six floor but intends to go 'DOWN' to fourth)
	private boolean canServiceCall(int floorRequested) {
		if (status.equals(Directions.DOWN)) {
			if (currFloorPosition >= floorRequested) {
				return true; // The elevator can accept service to this floor
			}
		} else if (status.equals(Directions.UP)) {
			if (currFloorPosition <= floorRequested) {
				return true; // The elevator can accept service to this floor
			}
		} else {
			if (currFloorPosition >= floorRequested) {
				serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_CAPACITY, floorComparator);
			} else {
				serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_CAPACITY, floorComparator.reversed());
			}
			return true; // The current elevator is not doing anything, will service request
		}
		return false; // The elevator can not service request during its current service route
	}
}
