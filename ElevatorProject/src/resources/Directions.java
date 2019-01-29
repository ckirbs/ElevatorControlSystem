package resources;

public enum Directions {
	UP,
	DOWN,
	STANDBY;
	
	public static boolean isOpposite(Directions dir1, Directions dir2) {
		return !(dir1 == STANDBY || dir2 == STANDBY || dir1 == dir2);
	}
	
	public static Directions getDirByInt(int val) {
		switch (val) {
		case 0: return DOWN;
		case 1: return UP;
		case 2: return STANDBY;
		default: return null;
		}
	}
	
	public static int getIntByDir(Directions dir) {
		switch (dir) {
		case DOWN: return 0;
		case UP: return 1;
		case STANDBY: return 2;
		default: return -1;
		}
	}
}
