package Resources;

public enum Directions {
	UP,
	DOWN,
	STANDBY;
	
	public static boolean isOpposite(Directions dir1, Directions dir2) {
		
		if (dir1 == STANDBY || dir2 == STANDBY || dir1 == dir2) return false;			
		return true;
	}
}
