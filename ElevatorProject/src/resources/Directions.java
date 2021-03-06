package resources;

public enum Directions {
	UP,
	DOWN,
	STANDBY,
	ERROR_DEFAULT,
	ERROR_SOFT,
	ERROR_HARD;
	
	public static boolean isOpposite(Directions dir1, Directions dir2) {
		return !((dir1 == STANDBY || dir2 == STANDBY || dir1 == dir2) || isInError(dir1) || isInError(dir2));
	}
	
	/**
	 * This function serves to prevent isOpposite from pairing two errored directions together
	 * 
	 * It is a separate function so that any call to it may be done from the dispatcher.
	 */
	public static boolean isInError(Directions dir) {
		return (Directions.getIntByDir(dir) >= Directions.getIntByDir(ERROR_DEFAULT));
	}
	
	public static Directions getDirByInt(int val) {
		switch (val) {
		case 0: return DOWN;
		case 1: return UP;
		case 2: return STANDBY;
		case 3: return ERROR_DEFAULT;
		case 4: return ERROR_SOFT;
		case 5: return ERROR_HARD;
		default: return null;
		}
	}
	
	public static int getIntByDir(Directions dir) {
		if (dir == null) return -1;
		
		switch (dir) {
		case DOWN: return 0;
		case UP: return 1;
		case STANDBY: return 2;
		case ERROR_DEFAULT: return 3;
		case ERROR_SOFT: return 4;
		case ERROR_HARD: return 5;
		default: return -1;
		}
	}
}
