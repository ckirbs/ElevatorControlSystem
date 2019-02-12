package elevatorSubsystem;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import resources.Constants;
import resources.Directions;

public class Elevator {

	public static final int MAX_FLOOR = Constants.HIGHEST_FLOOR;
	public static final int MIN_FLOOR = Constants.LOWEST_FLOOR;
	public static final int MAX_SERVICE_QUEUE_CAPACITY = MAX_FLOOR;

	private final Integer elvNumber;
	private boolean isDoorOpen;
	private Integer currFloorPosition, floorDestination;
	private SortedSet<Integer> upList;
	private SortedSet<Integer> downList;
	private SortedSet<Integer> currentServiceList;
	private Set<Integer> elevatorPassengerButtons;
	private Directions status;

	private ElevatorMotor motor;
	private ElevatorReciever elvReceieve;

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
		floorDestination = null;
		currFloorPosition = 0;
		elevatorPassengerButtons = new HashSet<Integer>();
		upList = new TreeSet<Integer>();
		downList = new TreeSet<Integer>();
		currentServiceList = upList;
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
		if (!upList.isEmpty() || !downList.isEmpty()) {
			System.out.println("UPLIST: " + upList.toString());
			System.out.println("DOWNLIST: " + downList.toString());
			Directions serviceUpList = Directions.UP;
			if (status == Directions.UP) { // If direction is up
				if (!upList.subSet(currFloorPosition, MAX_FLOOR + 1).isEmpty()) { // There is stuff to service above the
																					// current floor keep going
					floorDestination = upList.subSet(currFloorPosition, MAX_FLOOR + 1).first();
				} else if (!downList.isEmpty()) { // If these isn't up requests above us check to see if there are down
													// requests
					floorDestination = downList.subSet(MIN_FLOOR, MAX_FLOOR + 1).last(); // Start at the top and work
																							// down
					serviceUpList = Directions.DOWN;
				} else { // Still must be some up requests from lower floors
					floorDestination = upList.subSet(MIN_FLOOR, MAX_FLOOR + 1).first(); // Start at the bottom and go up
				}
			} else if (status == Directions.DOWN) {
				if (!downList.subSet(MIN_FLOOR, currFloorPosition + 1).isEmpty()) {// There is down services below us
					floorDestination = downList.subSet(MIN_FLOOR, currFloorPosition + 1).last();
					serviceUpList = Directions.UP;
				} else if (!upList.isEmpty()) { // IF there aren't down requests below us check for up requests
					floorDestination = upList.subSet(MIN_FLOOR, MAX_FLOOR + 1).first(); // Start service at the lowest
																						// up floor
				} else { // still down floors to service above the currentFloorPosition
					floorDestination = downList.subSet(MIN_FLOOR, MAX_FLOOR + 1).last(); // Start at top and go down
					serviceUpList = Directions.UP;
				}
			} else { // status == Directions.STANDBY
				if (!upList.isEmpty()) {
					floorDestination = upList.subSet(MIN_FLOOR, MAX_FLOOR + 1).first();
				} else {
					floorDestination = downList.subSet(MIN_FLOOR, MAX_FLOOR + 1).last();
					serviceUpList = Directions.UP;
				}
			}
			setCurrentServiceList(serviceUpList);
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
		System.out.println("Updating Direction currFloor: " + currFloorPosition + ", destFloor: " + floorDestination);

		if (currFloorPosition < floorDestination) {
			if (status != Directions.UP) {
				status = Directions.UP;
			}
		} else if (currFloorPosition > floorDestination) {
			if (status != Directions.DOWN) {
				status = Directions.DOWN;
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

	public synchronized SortedSet<Integer> getServiceList() {
		return currentServiceList;
	}

	public void setCurrentServiceList(Directions direction) {
		if (direction == Directions.UP) {
			currentServiceList = upList;
		} else {
			currentServiceList = downList;
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

	/**
	 * addToServiceList - Adds floor to the serviceList based on the direction
	 * specified (if no direction is specified, add to list based on relation to
	 * current floor) * External request from floor
	 * 
	 * @param floor: floor requested
	 * @param direction: direction of request
	 */
	public synchronized void addToServiceList(int floor, Directions direction) {
		if (direction == Directions.UP) {
			upList.add(floor);
		} else if (direction == Directions.DOWN) {
			downList.add(floor);
		} else {
			if (currFloorPosition < floor) {
				upList.add(floor);
			} else {
				downList.add(floor);
			}
		}
	}

	/**
	 * addToServiceList - Add to list based on relation to current floor) *Pressed
	 * by user inside of the elevator
	 *
	 * @param floor: floor requested
	 */
	public synchronized void addToServiceList(int floor) {
		if (currFloorPosition < floor) {
			upList.add(floor);
		} else {
			downList.add(floor);
		}
	}

	/**
	 * serviceFloor - Services the floor, removing it from the queue
	 */
	public synchronized void serviceFloor() {
		upList.remove(currFloorPosition);
		downList.remove(currFloorPosition);
	}

	/**
	 * generateDoorOpenMsg() generates a doorOpenMessage
	 * 
	 * @return msg containing open, elevator, and floor number
	 */
	public byte[] generateDoorOpenMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.OPEN, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber, (byte) Directions.getIntByDir(getNextDirection())};
	}

	/**
	 * generateDoorCloseMsg() generates a doorClsoeMessage
	 * 
	 * @return msg containing close, elevator, and floor number
	 */
	public byte[] generateDoorCloseMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.CLOSE, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber, 0 };
	}

	/**
	 * generateAcceptMsg() generates a acceptFloorRequest msg
	 * 
	 * @return msg signaling a new floor has been added to service queue
	 */
	public byte[] generateAcceptMsg(int floorDest) {
		return new byte[] { Constants.CONFIRM_VOL_DESTINATION, Constants.YES, (byte) floorDest, (byte) (int) elvNumber,
				0 };
	}

	/**
	 * generateAcceptMsg() generates a declineFloorRequest msg
	 * 
	 * @return msg indicating a voluntary request has been declined by the elevator
	 */
	public byte[] generateDeclineMsg(int floorDest) {
		return new byte[] { Constants.CONFIRM_VOL_DESTINATION, Constants.NO, (byte) floorDest, (byte) (int) elvNumber,
				0 };
	}

	/**
	 * generateStatusMsg() generates a msg summarizing the current state and
	 * position of a elevator
	 * 
	 * @return msg containing elevator status, number, and current position
	 */
	byte[] generateSatusMsg() {
		return new byte[] { Constants.STATUS_REPORT, (byte) Directions.getIntByDir(this.getStatus()),
				(byte) (int) currFloorPosition, (byte) (int) elvNumber, 0 };
	}

	/**
	 * generateOpenMsg() generates a msg indicating the elevator is opening it's
	 * doors
	 * 
	 * @return msg containing elevator number and position, and doorOpenRequest
	 */
	byte[] generateOpenMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.OPEN, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber, 0 };
	}

	/**
	 * generateCloseMsg() generates a msg indicating the elevator is closing it's
	 * doors
	 * 
	 * @return msg containing elevator number and position, and doorCloseRequest
	 */
	byte[] generateCloseMsg() {
		return new byte[] { Constants.OPEN_CLOSE_DOOR, Constants.CLOSE, (byte) (int) currFloorPosition,
				(byte) (int) elvNumber, 0 };
	}
	
	private Directions getNextDirection() {
		Integer nextFloor = null;
		if (currentServiceList.size() > 1) {
			// There are currently more floors in the ServiceList, the elevator will
			// continue to travel in the given direction
			return status;
		} else {
			// The current floor is the only floor in the current service list
			if (currentServiceList == upList) {
				if (!downList.isEmpty()) { // If there aren't up requests above us check to see if there are any down
											// requests
					nextFloor = downList.subSet(MIN_FLOOR, currFloorPosition + 1).last();
					if (nextFloor > currFloorPosition) {
						return Directions.UP;
					} else {
						return Directions.DOWN;
					}

				} else if (currentServiceList == downList) {
					if (!upList.isEmpty()) { // If there aren't up requests above us check to see if there are any up
												// requests
						nextFloor = upList.subSet(MIN_FLOOR, currFloorPosition + 1).first();
						if (nextFloor < currFloorPosition) {
							return Directions.DOWN;
						} else {
							return Directions.UP;
						}
					}
				}
			}
		}
		//Both ServiceLists are empty, the elevator will remain stationary
		return Directions.STANDBY;
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
		return floorDestination;
	}

	public synchronized boolean isServiceListEmpty() {
		return (downList.isEmpty() && upList.isEmpty());
	}

	public Set<Integer> getElevatorPassengerButtons() {
		return elevatorPassengerButtons;
	}

	public Directions getStatus() {
		return status;
	}
}
