package floorSubsystem;

import java.util.ArrayList;
import java.util.List;

public class Floor {
	
	private int level;
	private boolean isDoorOpen; 
	private boolean isUpButtonPressed, isDownButtonPressed;
	private boolean isUpDirectionLampOn, isDownDirectionLampOn;
	private List<Integer> floorDestinationButtonsPressed;
	
	public Floor(int level) {
		floorDestinationButtonsPressed = new ArrayList<>();
		this.level = level;
		isDoorOpen = false;
		isUpButtonPressed = false;
		isDownButtonPressed = false;
		isUpDirectionLampOn = false;
		isDownDirectionLampOn = false;
	}
	
	public void addFloorButtonPressed(int floor) {
		floorDestinationButtonsPressed.add(floor);
	}
	
	public void removeFloorButtonPressed(int floor) {
		floorDestinationButtonsPressed.remove(floor);
	}
	
	public List<Integer> getFloorDestinationButtonsPressed(){
		return floorDestinationButtonsPressed;
	}
	
	public void openDoor() {
		isDoorOpen = true;
	}
	
	public void closeDoor() {
		isDoorOpen = false;
		floorDestinationButtonsPressed.clear();
		isUpButtonPressed = false;
		isDownButtonPressed = false;
	}
	
	public int getLevel() {
		return level;
	}

	public boolean isDoorOpen() {
		return isDoorOpen;
	}

	public void setDoorOpen(boolean isDoorOpen) {
		this.isDoorOpen = isDoorOpen;
	}

	public boolean isUpButtonPressed() {
		return isUpButtonPressed;
	}

	public void setUpButtonPressed(boolean isUpButtonPressed) {
		this.isUpButtonPressed = isUpButtonPressed;
	}

	public boolean isDownButtonPressed() {
		return isDownButtonPressed;
	}

	public void setDownButtonPressed(boolean isDownButtonPressed) {
		this.isDownButtonPressed = isDownButtonPressed;
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

	public void setLevel(int level) {
		this.level = level;
	}

	
}
