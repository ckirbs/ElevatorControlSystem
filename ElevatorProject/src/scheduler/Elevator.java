package scheduler;

import resources.Directions;

public class Elevator {
	private Directions dir;
	private int currFloor;
	private int id;
	
	public Elevator (int id, Directions dir, int floor) {
		this.dir = dir;
		this.currFloor = floor;
		this.id = id;
	}
	
	public int getFloor() {
		return this.currFloor;
	}

	public void setFloor(int floor) {
		this.currFloor = floor;
	}
	
	public Directions getDir() {
		return this.dir;
	}

	public void setDir(Directions dir) {
		this.dir = dir;
	}
	
	public int getId() {
		return this.id;
	}
}
