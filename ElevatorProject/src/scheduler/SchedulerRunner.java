package scheduler;


public class SchedulerRunner {

	public static void main(String[] args) {
		ElevatorListener elvListener = new ElevatorListener();
		FloorListener floorListener = new FloorListener();
		Thread eList = new Thread(elvListener, "elevListener");
		Thread fList = new Thread(floorListener, "floorListener");
		Thread gui = new Thread(new ElevatorGUI(elvListener, floorListener), "gui");
		
		fList.start();
		eList.start();
		gui.start();
	}
}
