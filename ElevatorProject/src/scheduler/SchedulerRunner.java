package scheduler;

import static resources.Constants.RUN_GUI;

public class SchedulerRunner {

	public static void main(String[] args) {
		ElevatorListener elvListener = new ElevatorListener();
		FloorListener floorListener = new FloorListener();
		Thread eList = new Thread(elvListener, "elevListener");
		Thread fList = new Thread(floorListener, "floorListener");
		
		fList.start();
		eList.start();
		
		if (RUN_GUI) {
			Thread gui = new Thread(new ElevatorGUI(elvListener, floorListener), "gui");
			gui.start();
		}
	}
}
