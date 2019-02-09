package resources;

/**
 * Constants class to hold various constants that my be used in multiple classes
 * 
 * @author Darren
 *
 */
public final class Constants {

	/**
	 * Private constructor, this class should never be instantiated
	 * Should just import the required constants
	 */
	private Constants() {}
	
	public static final int ELEVATOR_PORT = 12345;
	public static final int FLOOR_PORT = 12346;
	public static final int MESSAGE_LENGTH = 5;
	public static final int NUMBER_OF_ELEVATORS = 1;
	public static final int LOWEST_FLOOR = 0;
	public static final int HIGHEST_FLOOR = 10;
	public static final int NUMBER_OF_FLOORS = HIGHEST_FLOOR - LOWEST_FLOOR + 1;
	public static final int ELEVATOR_TRAVEL_SPEED_MS = 1000;
	public static final int ELEVATOR_STOP_TIME = 2000;
	
	// Message Type Values
	public static final byte ERROR = 0;
	public static final byte NEW_REQUEST_FROM_FLOOR = 1;
	public static final byte REQUEST_RECEIVED = 2;
	public static final byte OPEN_CLOSE_DOOR = 3;
	public static final byte NEW_ELEVATOR_DESTINATION = 4;
	public static final byte ELEVATOR_INFO_REQUEST = 5;
	public static final byte CONFIRM_VOL_DESTINATION = 6;
	public static final byte STATUS_REPORT = 7;
	
	// Message flag values
	public static final byte YES = 1;
	public static final byte NO = 0;
	public static final byte OPEN = 1;
	public static final byte CLOSE = 0;
	public static final byte MANDATORY = 1;
	public static final byte VOLUNTARY = 0;
	
}
