package floorSubsystem;

public class ElevatorForFloor {
	private int index;
	private boolean isDoorOpen; 
	private boolean isUpDirectionLampOn, isDownDirectionLampOn;
	
	public ElevatorForFloor(int index) {
		this.index = index;
		isDoorOpen = false;
		isUpDirectionLampOn = false;
		isDownDirectionLampOn = false;
	}

	public boolean isDoorOpen() {
		return isDoorOpen;
	}

	public void setDoorOpen(boolean isDoorOpen) {
		this.isDoorOpen = isDoorOpen;
		if (!isDoorOpen) {
			isUpDirectionLampOn = false;
			isDownDirectionLampOn = false;
		}
	}

	public boolean isUpDirectionLampOn() {
		return isUpDirectionLampOn;
	}

	public void setUpDirectionLampOn(boolean isUpDirectionLampOn) {
		this.isUpDirectionLampOn = isUpDirectionLampOn;
	}

	public boolean isDownDirectionLampOn() {
		return isDownDirectionLampOn;
	}

	public void setDownDirectionLampOn(boolean isDownDirectionLampOn) {
		this.isDownDirectionLampOn = isDownDirectionLampOn;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
