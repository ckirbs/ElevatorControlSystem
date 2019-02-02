File Names and Descriptions
	Elevator System
		Elevator
		ElevatorMotor - A class responsible for moving the elevator. Contains the state machine for the elevator
		ElevatorReceiver
		ElevatorState - An enumerated type denoting the different states for the elevator
		
	Scheduler
		Communicator - A class that handles reading messages and generating responses for them
		Dispatcher - A class responsible for choosing the most suitable elevator to handle a new request
		ElevatorListener - A subclass of Communicator that listens for messages from the elevator and deals with them (by responding or passing them to a floor)
		FloorListener - A subclass of Communicator that listens for messages from the floor and deals with them (by responding or passing them to an elevator)
		SchedulerRunner - A main class that starts the Elevator and Floor Listeners
		
	Floor System
		Floor
		FloorSystem
		Temp
		
	Resources
		Constants - A static class which only holds constant values to be used by other classes
		Directions - An enumerated type denoting directions that an elevator can move
		Message - A class responsible for creating and holding all of the users requests
		SystemFile - A class responsible for validating each line in the systems input file and delegating the creation of the 	messages to the Message class
	
Group Memebers and Responsibilities
	Darren Holden
		Programming the Scheduler system classes
		Writing documention
	Callum Kirby
		Programming the Elevator system classes
	Christopher Molnar
		Programming SystemFile, Message, and Elevator State Machine
		Created JUnit Tests
		Writing Documentation
		Drawing state machine diagrams
	Logan MacGillivary
		Programming the Scheduler system classes
	Brandon Hartford
		Programming the Floor system classes
	
Set Up Instructions
	TBD
