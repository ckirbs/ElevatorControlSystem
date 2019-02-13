package floorSubsystem;

import java.util.ArrayList;
import java.util.List;
import static resources.Constants.NUMBER_OF_ELEVATORS;

public class Floor {
	
	private int level;
	//private boolean isDoorOpen; 
	private boolean isUpButtonPressed, isDownButtonPressed;
	//private boolean isUpDirectionLampOn, isDownDirectionLampOn;
	private List<Integer> floorDestinationButtonsPressed;
	private List<ElevatorForFloor> elevatorsAtFloor;
	/**
	 * creates a floor
	 * @param level the level of the floor
	 */
	public Floor(int level) {
		floorDestinationButtonsPressed = new ArrayList<>();
		elevatorsAtFloor = new ArrayList<>();
		this.level = level;
		isUpButtonPressed = false;
		isDownButtonPressed = false;
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			elevatorsAtFloor.add(new ElevatorForFloor(i));
		}
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
	public void openDoor(int elevatorIndex) {
		elevatorsAtFloor.get(elevatorIndex).setDoorOpen(true);
	}
	
	/**
	 * opens the door on the floor
	 */
	public void closeDoor(int elevatorIndex) {
		elevatorsAtFloor.get(elevatorIndex).setDoorOpen(false);
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
	public boolean isDoorOpen(int elevatorIndex) {
		return elevatorsAtFloor.get(elevatorIndex).isDoorOpen();
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
	public boolean isUpDirectionLampOn(int elevatorIndex) {
		return elevatorsAtFloor.get(elevatorIndex).isUpDirectionLampOn();
	}
	
	/**
	 * Sets the value of if the up direction lamp is on
	 * @param isUpDirectionLampOn
	 */
	public void setUpDirectionLampOn(boolean isUpDirectionLampOn, int elevatorIndex) {
		elevatorsAtFloor.get(elevatorIndex).setUpDirectionLampOn(isUpDirectionLampOn);
	}
	
	/**
	 * Returns true or false if the down direction lamp is on
	 * @return
	 */
	public boolean isDownDirectionLampOn(int elevatorIndex) {
		return elevatorsAtFloor.get(elevatorIndex).isDownDirectionLampOn();
	}
	
	/**
	 * Sets the value of if the down direction lamp is on
	 * @param isDownDirectionLampOn
	 */
	public void setDownDirectionLampOn(boolean isDownDirectionLampOn, int elevatorIndex) {
		elevatorsAtFloor.get(elevatorIndex).setDownDirectionLampOn(isDownDirectionLampOn);
	}
	
	/**
	 * Sets the level of the floor
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public List<ElevatorForFloor> getElevatorsAtFloor() {
		return elevatorsAtFloor;
	}

	public void setElevatorsAtFloor(List<ElevatorForFloor> elevatorsAtFloor) {
		this.elevatorsAtFloor = elevatorsAtFloor;
	}
	
	

	
}
