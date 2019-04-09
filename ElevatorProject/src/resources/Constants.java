package resources;

import java.text.SimpleDateFormat;

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
	public static final int MESSAGE_LENGTH = 6;
	public static final int NUMBER_OF_ELEVATORS = 4;
	public static final int LOWEST_FLOOR = 0;
	public static final int HIGHEST_FLOOR = 22;
	public static final int NUMBER_OF_FLOORS = HIGHEST_FLOOR - LOWEST_FLOOR + 1;
	public static final int ELEVATOR_TRAVEL_SPEED_MS = 1500;
	public static final int ELEVATOR_STOP_TIME = 1750;
	
	// IP_ADDRESS
	public static final String SCHED_IP_ADDRESS = "127.0.0.1";
	public static final String FLOOR_SYS_IP_ADDRESS = "127.0.0.1";
	public static final String ELEVATOR_SYS_IP_ADDRESS = "127.0.0.1";
	
	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSSS");
	
	// Toggle GUI on (true) and off (false)
	public static final Boolean RUN_GUI = true;
	
	// Input files
	public final static String FILENAME1 = "elevatorInputFile.txt"; 
	public final static String FILENAME2 = "TestFile1.txt"; 
	public final static String FILENAME3 = "ErrorRunFile.txt"; 
	public final static String FILENAME4 = "PerformanceTimesInputFile.txt"; 
	public final static String FILENAME5 = "DuttonExampleFile.txt"; 
	// Select the file you want to run by putting it in FILE_TO_RUN
	public static final String FILE_TO_RUN = FILENAME5;

	
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
