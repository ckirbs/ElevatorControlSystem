package Resources;

/**
 * Constants class to hold various constants that my be used in multiple classes
 * 
 * @author Darren
 *
 */
public final class Constants {

	/**
	 * Private constructor, this class shoul never be instantiated
	 * Should just import the required constants
	 */
	private Constants() {}
	
	public static final int ELEVATOR_PORT = 12345;
	public static final int FLOOR_PORT = 12346;
	public static final int MESSAGE_LENGTH = 4;
	public static final int NUMBER_OF_ELEVATORS = 1;
	public static final int LOWEST_FLOOR = 0;
	public static final int HIGHEST_FLOOR = 10;
	public static final int NUMBER_OF_FLOORS = HIGHEST_FLOOR - LOWEST_FLOOR;
}
