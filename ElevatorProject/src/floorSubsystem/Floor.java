package floorSubsystem;

import java.util.ArrayList;
import java.util.List;

public class Floor {
	
	private int level;
	private boolean isDoorOpen; 
	private boolean isUpButtonPressed, isDownButtonPressed;
	private boolean isUpDirectionLampOn, isDownDirectionLampOn;
	private List<Integer> floorDestinationButtonsPressed;
	/**
	 * creates a floor
	 * @param level the level of the floor
	 */
	public Floor(int level) {
		floorDestinationButtonsPressed = new ArrayList<>();
		this.level = level;
		isDoorOpen = false;
		isUpButtonPressed = false;
		isDownButtonPressed = false;
		isUpDirectionLampOn = false;
		isDownDirectionLampOn = false;
	}
	
	/**
	 * add a floor to the list of floors that were pressed on this objects floor
	 * @param floor  the floor to be added to the list
	 */
	public void addFloorButtonPressed(int floor) {
		floorDestinationButtonsPressed.add(floor);
	}
	
	/**
	 * Remove a floor from the list of floors that were pressed
	 * @param floor  the floor to remove from the list of floors that were pressed
	 */
	public void removeFloorButtonPressed(int floor) {
		floorDestinationButtonsPressed.remove(Integer.valueOf(floor));
	}
	
	/**
	 * Get a list of all floors that were pressed
	 * @return  the list of pressed floors
	 */
	public List<Integer> getFloorDestinationButtonsPressed(){
		return floorDestinationButtonsPressed;
	}
	
	/**
	 * opens the door on the floor
	 */
	public void openDoor() {
		isDoorOpen = true;
	}
	
	/**
	 * opens the door on the floor
	 */
	public void closeDoor() {
		isDoorOpen = false;
		floorDestinationButtonsPressed.clear();
		isUpButtonPressed = false;
		isDownButtonPressed = false;
	}
	
	/**
	 * return the level of the floor
	 * @return
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * returns true or false if the door is open
	 * @return
	 */
	public boolean isDoorOpen() {
		return isDoorOpen;
	}
	
	/**
	 * Sets the value of if the door is open
	 * @param isDoorOpen
	 */
	public void setDoorOpen(boolean isDoorOpen) {
		this.isDoorOpen = isDoorOpen;
	}
	
	/**
	 * return true or false if the up button is pressed
	 * @return
	 */
	public boolean isUpButtonPressed() {
		return isUpButtonPressed;
	}
	
	/**
	 * Sets the value of if the up button is pressed
	 * @param isUpButtonPressed
	 */
	public void setUpButtonPressed(boolean isUpButtonPressed) {
		this.isUpButtonPressed = isUpButtonPressed;
	}
	
	/**
	 * return true or false if the down button is pressed
	 * @return
	 */
	public boolean isDownButtonPressed() {
		return isDownButtonPressed;
	}
	
	/**
	 * Sets the value of if the down button is pressed
	 * @param isDownButtonPressed
	 */
	public void setDownButtonPressed(boolean isDownButtonPressed) {
		this.isDownButtonPressed = isDownButtonPressed;
	}
	
	/**
	 * Returns true or false if the up direction lamp is on
	 * @return
	 */
	public boolean isUpDirectionLampOn() {
		return isUpDirectionLampOn;
	}
	
	/**
	 * Sets the value of if the up direction lamp is on
	 * @param isUpDirectionLampOn
	 */
	public void setUpDirectionLampOn(boolean isUpDirectionLampOn) {
		this.isUpDirectionLampOn = isUpDirectionLampOn;
	}
	
	/**
	 * Returns true or false if the down direction lamp is on
	 * @return
	 */
	public boolean isDownDirectionLampOn() {
		return isDownDirectionLampOn;
	}
	
	/**
	 * Sets the value of if the down direction lamp is on
	 * @param isDownDirectionLampOn
	 */
	public void setDownDirectionLampOn(boolean isDownDirectionLampOn) {
		this.isDownDirectionLampOn = isDownDirectionLampOn;
	}
	
	/**
	 * Sets the level of the floor
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	
}
