package resources;

public enum Directions {
	UP,
	DOWN,
	STANDBY,
	ERROR_DOOR,
	ERROR_MOVE;
	
	public static boolean isOpposite(Directions dir1, Directions dir2) {
		return !(dir1 == STANDBY || dir2 == STANDBY || dir1 == dir2);
	}
	
	public static Directions getDirByInt(int val) {
		switch (val) {
		case 0: return DOWN;
		case 1: return UP;
		case 2: return STANDBY;
		case 3: return ERROR_DOOR;
		case 4: return ERROR_MOVE;
		default: return null;
		}
	}
	
	public static int getIntByDir(Directions dir) {
		if (dir == null) return -1;
		
		switch (dir) {
		case DOWN: return 0;
		case UP: return 1;
		case STANDBY: return 2;
		case ERROR_DOOR: return 3;
		case ERROR_MOVE: return 4;
		default: return -1;
		}
	}
}
