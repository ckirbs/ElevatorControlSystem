package floorSubsystem;

public class Floor {
	
	int level;
	boolean isDoorOpen;
	
	public Floor(int level) {
		this.level = level;
		isDoorOpen = false;
	}
	
	public void openDoor() {
		isDoorOpen = true;
	}
	
	public void closeDoor() {
		isDoorOpen = false;
	}

}
