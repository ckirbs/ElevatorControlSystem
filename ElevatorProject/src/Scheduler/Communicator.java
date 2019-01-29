package Scheduler;

import Scheduler.Dispatcher;
import static Resources.Constants.NUMBER_OF_ELEVATORS;
import static Resources.Constants.NUMBER_OF_FLOORS;

public class Communicator {
	protected Dispatcher dispatcher;
	protected final int TIMEOUT_TIME = 50;
	protected static int[] elevatorPorts = new int[NUMBER_OF_ELEVATORS];
	protected static int[] floorPorts = new int[NUMBER_OF_FLOORS];
	
	public Communicator() {
		this.dispatcher = new Dispatcher();
	}
}

