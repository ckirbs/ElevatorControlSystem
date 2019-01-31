package elevatorSubsystem;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import resources.Directions;
import resources.Message;
import resources.Constants;

public class Elevator {

	public static final int MAX_FLOOR = Constants.HIGHEST_FLOOR;
	public static final int MIN_FLOOR = Constants.LOWEST_FLOOR;
	public static final int FLOOR_TRAVEL_SPEED_MS = 1000;
	public static final int MAX_SERVICE_QUEUE_CAPACITY = MAX_FLOOR;

	private final Integer elvNumber;
	private boolean isDoorOpen;
	private Integer currFloorPosition, floorDestionation;
	private PriorityBlockingQueue<Integer> serviceScheduleQueue;
	private Set<Integer> elevatorPassengerButtons;
	private Directions status;

	private ElevatorMotor motor;
	private ElevatorReciever elvReceieve;

	private Comparator<Integer> floorComparator = (Integer a, Integer b) -> a.compareTo(b);

	// TODO Two/Three threads are necessary for the function of the elevator
	// -- One Responsible for the travel of the elevator (traveling between
	// destinations)
	// -- One Responsible for receiving and handling floor request from the
	// scheduler
	// -- (Possibly) One responsible for listening to floor requests within the
	// elevator
	public Elevator(Integer elvNumber, ElevatorReciever elvRecieve) {
		this.motor = new ElevatorMotor(this);
		this.elvReceieve = elvRecieve;
		this.elvNumber = elvNumber;
		
		isDoorOpen = false;
		floorDestionation = null;
		currFloorPosition = 0;
		elevatorPassengerButtons = new HashSet<Integer>();
		status = Directions.STANDBY;
		
		motor.start();
	}
	
	synchronized void updateFloorToService() {
		if (!serviceScheduleQueue.isEmpty()) {
			floorDestionation = serviceScheduleQueue.peek();
			updateDirection();
		} else {
			// No floors to service, wait to receive request
			status = Directions.STANDBY;
		}
	}

	/*
	 * updateDirection() updates the status of elevator to represent the direction
	 * of the currentDestionation
	 */
	synchronized void updateDirection() {
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

	// Checks to see if the current elevator can accept a floor service request
	// returns true if request is along current route, false if outside
	// Standby = Initializes new priorityQueue with priority depending on floor
	// requested
	boolean canServiceCall(int floorRequested) {
		if(floorRequested > MAX_FLOOR || floorRequested < MIN_FLOOR){
			//Not within the service bounds of the elevator (off of track!)
			//This should be handled by scheduler -- fail-safe
			return false;
		}
		
		if (status.equals(Directions.DOWN)) {
			// Check if elevator can accept service to this floor
			return (currFloorPosition >= floorRequested);
		} else if (status.equals(Directions.UP)) {
			// Check if elevator can accept service to this floor
			return (currFloorPosition <= floorRequested);
		} else {
			// The current elevator is not doing anything, will service request
			return true;
		}
	}
	
	public synchronized void moveUp() {
		currFloorPosition++;
	}

	public synchronized void moveDown() {
		currFloorPosition--;
	}
	
	public void openDoor() {
		isDoorOpen = true;
	}
	
	public void closeDoor() {
		isDoorOpen = false;
	}

	public void addToPassengerButtons(int floor) {
		elevatorPassengerButtons.add(floor);
	}

	public void removeFromPassengerButtons(int floor) {
		elevatorPassengerButtons.remove(floor);
	}

	public void addToServiceQueue(int floor) {
		serviceScheduleQueue.add(floor);
	}

	public int pollServiceQueue() {
		return serviceScheduleQueue.poll();
	}

	byte[] generateAcceptMsg(int floorDest) {
		return new byte[] { 6, 1, (byte) floorDest, (byte) (int) this.getElvNumber() };
	}

	byte[] generateDeclineMsg(int floorDest) {
		 return new byte[] { 6, 0, (byte) floorDest, (byte) (int) this.getElvNumber() };
	}

	byte[] generateSatusMsg() {
		 return new byte[] { 7, (byte) Directions.getIntByDir(this.getStatus()), (byte) (int) this.getCurrFloorPosition(), (byte) (int) this.getElvNumber() };
	}	
	
	byte[] generateOpenMsg() {
		 return new byte[] { 3, 1, (byte) (int) this.getCurrFloorPosition(), (byte) (int) this.getElvNumber() };
	}	
	
	byte[] generateCloseMsg() {
		 return new byte[] { 3, 1, (byte) (int) this.getCurrFloorPosition(), (byte) (int) this.getElvNumber() };
	}
	
	public Integer getElvNumber() {
		return elvNumber;
	}

	public Integer getCurrFloorPosition() {
		return currFloorPosition;
	}

	public void setCurrFloorPosition(Integer currFloorPosition) {
		this.currFloorPosition = currFloorPosition;
	}

	public Integer getFloorDestionation() {
		return floorDestionation;
	}

	public PriorityBlockingQueue<Integer> getServiceScheduleQueue() {
		return serviceScheduleQueue;
	}

	public Set<Integer> getElevatorPassengerButtons() {
		return elevatorPassengerButtons;
	}

	public Directions getStatus() {
		return status;
	}
}
