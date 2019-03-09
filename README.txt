File Names and Descriptions

	elevatorSubsystem

		Elevator - A class responsiable for holding states, and information regarding the elevator subsystem

		ElevatorMotor - A class responsible for moving the elevator. Contains the state machine for the elevator

		ElevatorReceiver - Responsible for receiving, interpreting, and assigning floor request sent via the scheduler

		ElevatorState - An enumerated type denoting the different states for the elevator

	scheduler

		Communicator - A class that handles reading messages and generating responses for them

		Dispatcher - A class responsible for choosing the most suitable elevator to handle a new request (not used in this iteration)

		ElevatorListener - A subclass of Communicator that listens for messages from the elevator and deals with them (by responding or passing them to a floor)

		FloorListener - A subclass of Communicator that listens for messages from the floor and deals with them (by responding or passing them to an elevator)

		SchedulerRunner - A main class that starts the Elevator and Floor Listeners
	

	floorSubsystem

		Floor - A class that holds the information of each floor such as the doors and the lights

		FloorSystem - A class that schedules the events from the text file to be sent as packetsto the scheduler

			and receives packets from the scheulder to open/close doors and turn on lights
		
		ElevatorForFloor - A class to control each elevator's lights and floor doors 

	resources

		Constants - A static class which only holds constant values to be used by other classes

		Directions - An enumerated type denoting directions that an elevator can move and the possible elevator error states

		Message - A class responsible for creating and holding all of the users requests

		SystemFile - A class responsible for validating each line in the systems input file and delegating the creation of the 	messages to the Message class


	test

		elevator - 
			ElevatorTests - JUnit tests for testing the elevator functionality

		floor -
			FloorSystemTest - JUnit tests for testing the floor functionality

		resources -

			DirectionTest - JUnit tests for testing functionality
 
			SystemFileTest - JUnit tests for testing the input validation

		scheduler -

			DispatcherTests - JUnit tests for testing the dispatcher in the scheduler system

Group Memebers and Responsibilities
	Iteration 1
		Darren Holden
		

			Programming the Scheduler system classes
					Writing documention
			
			Writing JUnit test cases

			Performing end-to-end tests and debugging

		Callum Kirby

			Programming the Elevator, ElevatorMotor, and ElevatorReciever
			Writing JUnit Test Cases

			Documentation

		Christopher Molnar

			Programming SystemFile, Message, and Elevator State Machine

			Created JUnit Tests

			Writing documentation

			Drawing the elevator state machine diagram

		Logan MacGillivary

			Programming the Scheduler system classes

			Drawing UML Class diagram

		Brandon Hartford

			Programming the Floor system classes

			Writing JUnit Test Cases
				Documentation
	
	Iteration 2
		Darren Holden
		
			Updating the Scheduler subsystem

		Callum Kirby

			Updating the Elevator subsystem
			
		Christopher Molnar

			Updating the Elevator subsystem

		Logan MacGillivary

			Updating UML and README

		Brandon Hartford
	
			Updating Floor subsystem
	Iteration 3
		Darren Holden
		
			Debugging error modes
			
			Measuring performance times

		Callum Kirby

			Updates to Directions enum class
			
      Automated test updates

			UML Class Diagram Update			

		Christopher Molnar

			Implementing elevator error modes and error propogation through the system
			Update State Machine

		Logan MacGillivary

      Update UML Class diagram
      
			Dispatcher & Communicator logic update to accomodate errors

		Brandon Hartford
			
			Timing diagram
			
			Floor subsystem formatting cleanup

Set Up Instructions/Run
	
	1 - Unzip project

	2 - Import the Java project into Eclipse

		2.1 - Open Eclipse
		2.2 - Create a Java Project (file->new->Java Project)

		2.3 - Give Project a Name (ex: ElevatorProjectGroup5) and Finish

		2.4 - Import File (file->import) and then General - File System and click Next

		2.5 - From Directory: Browse for the java project "ElevatorProject" in the unzipped folder from step 1

		2.6 - Check the ElevatorProject box to grab all of the files

		2.7 - Into Folder: Select the Java Project you created (ex: ElevatorProjectGroup5) and hit finish

		2.8 - If overwrite '.classpath' in folder "your folder name (ElevatorGroupProject5)" click Yes to All
	3 - Navigate in the Package Explorer to the Java Project you imported the files into -> src

		3.1 - Select from elevatorSubsystem -> ElevatorReciever.java

		3.2 - Select from scheduler -> SchedulerRunner.java

		3.3 - Select from floorSubsytem -> FloorSystem.java

	4 - To run our project run ElevatorReciever.java, SchedulerRunner.java and floorSystem.java (in that order)

		4.1 - The elevatorReciever output will show the progress of the elevator (where its going and what state its in)

	5 - To acces JUnit Tests navigate to ElevatorProject -> src -> test

		5.1 - Included are JUnit Tests for all of the subsystems (elevator, floor, scheduler and resources)

		5.2 - To run a JUnit Test navigate to the desired JUnit file and select run

Errors List

	(1) Scheduler subsystem receives back a response message with an id that does not exist
	
	(2) Scheduler subsystem does not receive a response after sending voluntary requests
	
	(3) Scheduler, Elevator, and Floor subsystems receive invalid message type
	
	(4) Scheduler subsystem experiences request starvation when a denied request consistently gets denied
	
	(5) Scheduler, Elevator, and Floor subsystems all experience packet loss

Performance Notes
	Currently the Elevators travel at 1 floor per second with instantaneous acceleration
	The doors open and close instantaneously, with 1 second between open and close
	Times were measured without any elevator errors

	Service Times
		Service time was measured as the time it takes between a passenger pressing a button on a floor and an elevator going in the correct direction opening its doors on that floor
		As seen below, the system is much more efficient with 4 elevators as opposed to 1, increasing the number of elevators to 4 decreases the average service time by 86%	

		4 Elevators, 11 Floors: 
			Average: 3.8 seconds
			Minimum: 0 seconds
			Maximum: 9 seconds

		1 Elevator, 11 Floors:
			Average: 27.3 seconds
			Minimum: 4 seconds
			Maximum: 53 seconds

	Travel Times
		Travel time was measured as the time it takes between an elevator opening its doors to pick up the passenger and the elevator opening its doors at the destination floor
		As seen below, the average travel time does not significantly change with more elevators, however the range is much less volatile with more elvators, the maximum travel time being 27 seconds higher for 1 elevator

		4 Elevators, 11 Floors: 
			Average: 10.1 seconds
			Minimum: 3 seconds
			Maximum: 20 seconds

		1 Elevator, 11 Floors:
			Average: 12.4 seconds
			Minimum: 3 seconds
			Maximum: 47 seconds
