package oneComputerStart;

import elevatorSubsystem.ElevatorReciever;
import scheduler.SchedulerRunner;
import floorSubsystem.FloorSystem;;

public class Start {
	public static void main(String args[]) {
		ElevatorReciever.main(args);
		SchedulerRunner.main(args);
		FloorSystem.main(args);
	}
}
