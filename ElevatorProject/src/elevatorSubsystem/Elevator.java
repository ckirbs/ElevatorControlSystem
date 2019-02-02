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
		serviceScheduleQueue = new PriorityBlockingQueue<Integer>(MAX_SERVICE_QUEUE_CAPACITY, floorComparator);
		status = Directions.STANDBY;

		motor.start();
	}

	/**
	 * updateFloorToService() Update the current destination of the elevator
	 * 
	 * @return true if a new destionationFloor is set, false if there are no floors
	 *         in the serviceQueue
	 */
	public synchronized boolean updateFloorToService() {
		if (!serviceScheduleQueue.isEmpty()) {
			floorDestionation = serviceScheduleQueue.peek();
			System.out.println(floorDestionation);
			updateDirection();
			return true;
		} else {
			// No floors to service, wait to receive request
			status = Directions.STANDBY;
			return false;
		}
	}

	/**
	 * updateDirection() Update the direction of the elevator to match that of the
	 * current service.
	 */
	public synchronized void updateDirection() {
		PriorityBlockingQueue<Integer> temp;
		//System.out.println("Updating Direction currFloor: " + currFloorPosition + ", destFloor: " + floorDestionation);
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
	 * canServiceCall() Checks to see if the current elevator can accept a floor
	 * service request returns true if request is along current route, false if
	 * outside Standby = Initializes new priorityQueue with priority depending on
	 * floor requested
	 * 
	 * @param floorRequested the stop to check against the current service route
	 * @return true if the floor can be added ALONG the service route, false
	 *         otherwise
	 */
	public boolean canServiceCall(int floorRequested) {
		if (floorRequested > MAX_FLOOR || floorRequested < MIN_FLOOR) {
			// Not within the service bounds of the elevator (off of track!)
			// This should be handled by scheduler -- fail-safe
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

	public void moveUp() {
		currFloorPosition++;
	}

	public void moveDown() {
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
		int floorVal = serviceScheduleQueue.poll();
		if (floorVal == currFloorPosition) return floorVal;
		else {
			serviceScheduleQueue.add(floorVal);
			while (serviceScheduleQueue.contains(currFloorPosition)) {
				serviceScheduleQueue.remove(currFloorPosition);
			}
			return currFloorPosition;
		}
	}

	/**
	 * generateDoorOpenMsg() generates a doorOpenMessage
	 * 
	 * @return msg containing open, elevator, and floor number
	 */
	public byte[] generateDoorOpenMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.OPEN, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber };
	}

	/**
	 * generateDoorCloseMsg() generates a doorClsoeMessage
	 * 
	 * @return msg containing close, elevator, and floor number
	 */
	public byte[] generateDoorCloseMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.CLOSE, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber };
	}

	/**
	 * generateAcceptMsg() generates a acceptFloorRequest msg
	 * 
	 * @return msg signaling a new floor has been added to service queue
	 */
	public byte[] generateAcceptMsg(int floorDest) {
		return new byte[] { Constants.CONFIRM_VOL_DESTINATION, Constants.YES, (byte) floorDest,
				(byte) (int) elvNumber };
	}

	/**
	 * generateAcceptMsg() generates a declineFloorRequest msg
	 * 
	 * @return msg indicating a voluntary request has been declined by the elevator
	 */
	public byte[] generateDeclineMsg(int floorDest) {
		return new byte[] { Constants.CONFIRM_VOL_DESTINATION, Constants.NO, (byte) floorDest, (byte) (int) elvNumber };
	}

	/**
	 * generateStatusMsg() generates a msg summarizing the current state and
	 * position of a elevator
	 * 
	 * @return msg containing elevator status, number, and current position
	 */
	byte[] generateSatusMsg() {
		return new byte[] { Constants.STATUS_REPORT, (byte) Directions.getIntByDir(this.getStatus()),
				(byte) (int) currFloorPosition, (byte) (int) elvNumber };
	}

	/**
	 * generateOpenMsg() generates a msg indicating the elevator is opening it's doors
	 * 
	 * @return msg containing elevator number and position, and doorOpenRequest
	 */
	byte[] generateOpenMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.OPEN, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber };
	}

	/**
	 * generateOpenMsg() generates a msg indicating the elevator is closing it's doors
	 * 
	 * @return msg containing elevator number and position, and doorCloseRequest
	 */
	byte[] generateCloseMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.CLOSE, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber };
	}

	public Integer getElvNumber() {
		return elvNumber;
	}

	public ElevatorReciever getElevatorReciever() {
		return elvReceieve;
	}

	public Integer getCurrFloorPosition() {
		return currFloorPosition;
	}

	public Integer getFloorDestionation() {
		return floorDestionation;
	}

	public PriorityBlockingQueue<Integer> getServiceScheduleQueue() {
		return serviceScheduleQueue;
	}

	public Boolean isPriorityQueueEmpty() {
		return (getServiceScheduleQueue().isEmpty());
	}

	public Set<Integer> getElevatorPassengerButtons() {
		return elevatorPassengerButtons;
	}

	public Directions getStatus() {
		return status;
	}
}
