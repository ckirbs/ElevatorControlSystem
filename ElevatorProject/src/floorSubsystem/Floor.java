package floorSubsystem;

public class Floor {
	
	private int level;
	private boolean isDoorOpen; 
	private boolean isUpButtonPressed, isDownButtonPressed;
	private boolean isUpDirectionLampOn, isDownDirectionLampOn;
	
	public Floor(int level) {
		this.level = level;
		isDoorOpen = false;
		isUpButtonPressed = false;
		isDownButtonPressed = false;
		isUpDirectionLampOn = false;
		isDownDirectionLampOn = false;
	}
	
	public void openDoor() {
		isDoorOpen = true;
	}
	
	public void closeDoor() {
		isDoorOpen = false;
	}
	
	public int getLevel() {
		return level;
	}

}
