package Scheduler;

import java.util.ArrayList;
import ElevatorSubsystem.*;
import FloorSubsystem.*;
import Resources.*;


public class Dispatcher {
	private ArrayList<TempElevator> elevators;
	public final static int MAX_DIFF = 100;
	
	public Dispatcher() {
		this.elevators = new ArrayList<TempElevator>();
	}

	public void addElevator(TempElevator elevator) {
		this.elevators.add(elevator);
	}
	
	public TempElevator getNearestElevator(Directions dir, int floor) {
		int currDif = MAX_DIFF;
		int newDif;
		TempElevator currElevator = this.elevators.get(0);
		
		for (TempElevator elevator: this.elevators) {
			if (!Directions.isOpposite(dir, elevator.getDir())){
				newDif = Math.abs(elevator.getFloor() - floor);
				if (newDif < currDif) {
					currDif = newDif;
					currElevator = elevator;
				}
			}
		}
		
		return currElevator;
	}
	
	
	
	public class TempElevator {
		private Directions dir;
		private int currFloor;
		
		public TempElevator (Directions dir, int floor) {
			this.dir = dir;
			this.currFloor = floor;
		}
		
		public int getFloor() {
			return this.currFloor;
		}
		
		public Directions getDir() {
			return this.dir;
		}
	}
}
