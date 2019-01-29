package Scheduler;

import Scheduler.Dispatcher;

public class Communicator {
	protected Dispatcher dispatcher;
	protected final int TIMEOUT_TIME = 50;
	
	public Communicator() {
		this.dispatcher = new Dispatcher();
	}
}

