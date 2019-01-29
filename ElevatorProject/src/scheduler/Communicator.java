package scheduler;

import scheduler.Dispatcher;
import static resources.Constants.NUMBER_OF_ELEVATORS;
import static resources.Constants.NUMBER_OF_FLOORS;

public class Communicator {
	protected Dispatcher dispatcher;
	protected final int TIMEOUT_TIME = 50;
	protected int elevatorPort;
	protected static int[] floorPorts = new int[NUMBER_OF_FLOORS];
	
	public Communicator() {
		this.dispatcher = new Dispatcher();
	}
}

