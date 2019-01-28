package elevatorSubsystem;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import Resources.Directions;
import Resources.Message;

public class Elevator {

	private static final int MAX_FLOOR = 22;
	private static final int MIN_FLOOR = 0;

	private static final int FLOOR_TRAVEL_SPEED_MS = 1000;
	private static final int MAX_SERVICE_QUEUE_CAPACITY = 22;

	private Integer currFloorPosition, floorDestionation;
	private PriorityBlockingQueue<Integer> serviceScheduleQueue;
	private Set<Integer> elevatorPassengerButtons;
	private Directions status;

	private Comparator<Integer> floorComparator = (Integer a, Integer b) -> a.compareTo(b);

	// TODO Two/Three threads are necessary for the function of the elevator
	// -- One Responsible for the travel of the elevator (traveling between
	// destinations)
	// -- One Responsible for receiving and handling floor request from the
	// scheduler
	// -- (Possibly) One responsible for listening to floor requests within the
	// elevator

	public Elevator() {
		floorDestionation = null;
		currFloorPosition = 0;
		elevatorPassengerButtons = new HashSet<Integer>();
		status = Directions.STANDBY;
	}

	private void run() {
		// -------
		// THREAD 1 - MOTOR/MOVEMENT
		if (currFloorPosition == floorDestionation) {
			serviceFloor(); // open doors - remove floor from queue and grab next to service
			nextFloorToService();
		} else {
			move();
		}
		// -------

		// -------
		// THREAD 2 - SCHED LISTENER
		// Listen and Receive task from Scheduler
		//replySchedulerRequest();
		// -------
	}

	// Receive msg from scheduler with floor number
	private void replySchedulerRequest(Message msg) {
		//TODO find cleaner way of determining this --> Set bit that differentiates requests
		if (currFloorPosition == msg.getStartingFloor()) {
			//User presses button inside elevator
			addFloorToService(msg.getDestinationFloor());
		}
		
		if (canServiceCall(msg.getStartingFloor())) {
			//User requesting elevator from floor
			addFloorToService(msg.getStartingFloor());
			// send reply accepting assignment
		}
		// send reply declining the assignment
	}

	private void move() {
		System.out.println("Elevator is Moving " + status);
		if (currFloorPosition == MAX_FLOOR || currFloorPosition == MIN_FLOOR) {
			new Exception("Reached End of Track");
		}

		if (status == Directions.UP) {
			currFloorPosition++;
		} else if (status == Directions.DOWN) {
			currFloorPosition--;
		}

		try {
			Thread.sleep(FLOOR_TRAVEL_SPEED_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void nextFloorToService() {
		if (!serviceScheduleQueue.isEmpty()) {
			floorDestionation = serviceScheduleQueue.peek();
			updateDirection();
		} else {
			// No floors to service, wait to receive request
			status = Directions.STANDBY;
		}
	}

	private void serviceFloor() {
		System.out.print("Opening Doors on Floor " + currFloorPosition);

		serviceScheduleQueue.poll();
		if (elevatorPassengerButtons.contains(currFloorPosition)) {
			elevatorPassengerButtons.remove(currFloorPosition);
		}

		System.out.print("Opening Closing on Floor " + currFloorPosition);

		System.out.print("Current Floors Pressed: " + elevatorPassengerButtons.toString());
	}

	/*
	 * updateDirection() updates the status of elevator to represent the direction
	 * of the currentDestionation
	 */
	private void updateDirection() {
		PriorityBlockingQueue<Integer> temp;
		if (currFloorPosition < floorDestionation) {
			if (status != Directions.UP) {
				temp = serviceScheduleQueue;
				status = Directions.UP;
				serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_QUEUE_CAPACITY, floorComparator);
				serviceScheduleQueue.addAll(temp);
			}
		} else if (currFloorPosition > floorDestionation) {
			if (status != Directions.DOWN) {
				temp = serviceScheduleQueue;
				status = Directions.DOWN;
				serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_QUEUE_CAPACITY,
						floorComparator.reversed());
				serviceScheduleQueue.addAll(temp);
			}
		}
	}

	/**
	 * addFloorToService() adds request received via message, if current floor ==
	 * startingFloor treaded as internalElevatorPanel request (button glows) else
	 * treated as floor request and added to service route
	 * 
	 * @param floor containing calling floor and 
	 */
	private void addFloorToService(Integer floor) {
		if (currFloorPosition == floor) {
			// On caller floor -- Receive destination floor
			serviceScheduleQueue.add(floor);
			elevatorPassengerButtons.add(floor);
		} else {
			// Receiving general floor request
			serviceScheduleQueue.add(floor);
		}
		nextFloorToService();
	}

	// Checks to see if the current elevator can accept a floor service request
	// returns true if request is along current route, false if outside
	// Standby = Initializes new priorityQueue with priority depending on floor
	// requested

	// TODO Logic assumes a user will never indicate the wrong elevator call on a
	// floor request (Clicks 'UP' on six floor but intends to go 'DOWN' to fourth)
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
			// The current elevator is not doing anything, will service request
			return true;
		}
		// The elevator can not service during its current service
		return false;
	}
}
