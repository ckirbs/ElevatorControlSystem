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
		
	test
		elevator - 
		floor - 
		resources -
			DirectionTest - 
			SystemFileTest - JUnit tests for testing the input validation
		scheduler -
	
Group Memebers and Responsibilities
	Darren Holden
		Programming the Scheduler system classes
		Writing documention
	Callum Kirby
		Programming the Elevator system classes
	Christopher Molnar
		Programming SystemFile, Message, and Elevator State Machine
		Created JUnit Tests
		Writing documentation
		Drawing state machine diagrams
	Logan MacGillivary
		Programming the Scheduler system classes
	Brandon Hartford
		Programming the Floor system classes
	
Set Up Instructions/Run
	1 - Unzip project
	2 - Import the Java project into Eclipse
		2.1 - Open Eclipse
		2.2 - Create a Java Project (file->new->Java Project)
		2.3 - Give Project a Name (ex: ElevatorProjectGroup5) and Finish
		2.4 - Import File (file->import) and then General - File System and click Next
		2.5 - From Directory: Select from the unzipped folder the java project "ElevatorProject"
		2.6 - Check the Elevatpr box to grab all of the files
		2.7 - Into Folder: Select the Java Project you created (ex: ElevatorProjectGroup5) and hit finish
	3 - Navigate in the Package Explorer to the Java Project you imported the files into -> src
		3.1 - Select from elevatorSubsystem -> ElevatorReciever.java
		3.2 - Select from scheduler -> SchedulerRunner.java
		3.3 - Select from floorSubsytem -> FloorSystem.java
	4 - To run our project run ElevatorReciever.java, SchedulerRunner.java and floorSubsystem.java
		4.1 - The elevatorReciever output will show the progress of the elevator (where its going and what state its in)
	5 - To acces JUnit Tests navigate to ElevatorProject -> src -> test
		5.1 - Included are JUnit Tests for all of the subsystems (elevator, floor, scheduler and resources)
		5.2 - To run a JUnit Test navigate to the desired JUnit file and select run
