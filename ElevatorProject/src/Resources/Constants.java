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
	public final static int MAX_DIFF = SystemFile.HIGHESTFLOOR - SystemFile.LOWESTFLOOR + 1; // + 1 so that it's always bigger than the greatest possible difference
}
