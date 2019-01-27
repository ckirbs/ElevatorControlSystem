package Resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

/*
 * SystemFile - Responsible for validating each line in the systems input file.
 *  Creates Messages with the valid requests
 */
public class SystemFile {

    public final static int LOWESTFLOOR = 0; // Temporary variable for lowest
					     // floor
    public final static int HIGHESTFLOOR = 10; // Temporary variable for highest
					       // floor
    public final static String FILEPATH = "src/Resources/"; // File path for
							    // text files

    public final static String FILENAME1 = "elevatorInputFile.txt"; // Filename1

    private String filename;

    /*
     * SystemFile() - Creates a SystemFile object with the name of the file it
     * wants to input
     * 
     * @param: String - filename
     */
    public SystemFile(String filename) {
	this.filename = filename;
    }

    /*
     * validateFloor - Validates that the floor is within the upper and lower
     * boundary
     * 
     * @param: floor - The floor to be checked return:
     * 
     * @return: Boolean - True if the floor falls within the allowable range,
     * otherwise False
     */
    private Boolean validateFloorRange(int floor) {
	return (floor >= LOWESTFLOOR && floor <= HIGHESTFLOOR);
    }

    /*
     * readValidateAndCreateMessages - reads file using readFile() and the
     * validates and creates messages using validateLine()
     */
    private void readValidateAndCreateMessages() {
	ArrayList<String> requests = readFile();
	for (int position = 0; position < requests.size(); position++) {
	    validateLine(requests.get(position));
	}
    }

    /*
     * ValidateLine - Validate the string it gets passed. Valid if contains
     * time, start floor, direction and destination floor
     * 
     * @param: String lineInfo - The line it will be validating return: Boolean
     * 
     * @return- True if message created, otherwise False
     */
    private Boolean validateLine(String lineInfo) {
	String[] messageDetails = new String[Message.ELEMENTS_IN_MESSAGE];
	try {
	    messageDetails = lineInfo.split(" ");

	    if (messageDetails.length != Message.ELEMENTS_IN_MESSAGE) {
		System.out.println("Invalid Number of Elements");
		System.out.println("Size: " + messageDetails.length);
		System.out.println("Expected: " + Message.ELEMENTS_IN_MESSAGE + " Elements");
		System.out.println("at " + new Exception().getStackTrace()[0].toString());
		System.out.println("Line Format: hh:mm:ss:nnnn startFloor direction endFloor");
		System.out.println("Ex: 14:05:15.22 2 Up 4 \n");
		return false;
	    }

	    LocalTime time = LocalTime.parse(messageDetails[0]);

	    int startingFloor = Integer.parseInt(messageDetails[1]);

	    Directions direction;

	    if (messageDetails[2].equalsIgnoreCase("up")) {
		direction = Directions.UP;

	    } else if (messageDetails[2].equalsIgnoreCase("down")) {
		direction = Directions.DOWN;
	    } else // If not "up" or "down" it's an invalid direction
	    {
		System.out.println("Invalid Direction");
		System.out.println("Text: " + messageDetails[2]);
		System.out.println("at " + new Exception().getStackTrace()[0].toString());
		System.out.println("Line Format: hh:mm:ss:nnnn startFloor direction endFloor");
		System.out.println("Ex: 14:05:15.22 2 Up 4 \n");
		return false;
	    }

	    int destinationFloor = Integer.parseInt(messageDetails[3]);

	    if (validateFloorRange(startingFloor) && validateFloorRange(destinationFloor)) {
		Message message = new Message(time, startingFloor, direction, destinationFloor);
		return true;
	    }
	} catch (PatternSyntaxException pse) {
	    System.out.println("Invalid Message Format");
	    pse.printStackTrace();
	} catch (DateTimeParseException dtpe) {
	    System.out.println("Invalid time enetered");
	    dtpe.printStackTrace();
	} catch (NumberFormatException nfe) {
	    System.out.println("The Floor wasn't an Integer");
	    nfe.printStackTrace();
	} catch (ArrayIndexOutOfBoundsException obe) {
	    System.out.println("Invalid Message Entry");
	    obe.printStackTrace();
	}

	System.out.println("Line Format: hh:mm:ss:nnnn startFloor direction endFloor");
	System.out.println("Ex: 14:05:15.22 2 Up 4 \n");
	return false;
    }

    /*
     * readFile() - Reads the file stored in the filename attribute line by line
     * 
     * @return: ArrayList<String> - The parsed file line by line
     */
    private ArrayList<String> readFile() {
	Scanner scanner;
	ArrayList<String> requestsFromInputFile = new ArrayList<String>();
	try {
	    File n = new File(FILEPATH + filename);
	    scanner = new Scanner(n);
	    while (scanner.hasNextLine()) {
		requestsFromInputFile.add(scanner.nextLine());

	    }
	    scanner.close();
	    return requestsFromInputFile;
	} catch (FileNotFoundException e) {
	    System.out.println("File not found");
	    e.printStackTrace();
	}
	return requestsFromInputFile;
    }

    /*
     * testReadFile -public method for testing readFile()
     * 
     * @return: ArrayList<String>
     */
    public ArrayList<String> testReadFile() {
	return this.readFile();
    }

    /*
     * testValidateLine -public method for testing validateLine()
     * 
     * @param: String lineInfo - The line it will be validating return: Boolean
     * 
     * @return- True if message created, otherwise False
     */
    public Boolean testValidateLine(String lineInfo) {
	return this.validateLine(lineInfo);
    }
}
