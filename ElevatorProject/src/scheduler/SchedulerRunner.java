package scheduler;


public class SchedulerRunner {

	public static void main(String[] args) {
		Thread eList = new Thread(new ElevatorListener(), "elevListener");
		Thread fList = new Thread(new FloorListener(), "floorListener");
		
		fList.start();
		eList.start();
	}
}
