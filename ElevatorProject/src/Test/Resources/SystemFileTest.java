package Test.Resources;

import static org.junit.Assert.*;

import org.junit.Test;

import Resources.Directions;
import Resources.SystemFile;

public class SystemFileTest {

    public static final String TESTFILE1 = "TestFile1.txt"; // TestFile1.txt found in Resources folder
    
    @Test
    public void testValidateLineNoInput() {
	SystemFile input = new SystemFile("Filename.txt");
	String empty = "";
	assertFalse("No Input!", input.testValidateLine(empty));
    }
    
    @Test
    public void testValidateLine5Inputs() {
	SystemFile input = new SystemFile("Filename.txt");
	String toManyInputs = "1 2 3 4 5";
	assertFalse("To Many Inputs!", input.testValidateLine(toManyInputs));
    }
    
    @Test
    public void testValidateLineInvalidTime() {
	SystemFile input = new SystemFile("Filename.txt");
	String invalidTime = "hello 2 Up 4";
	assertFalse("Invalid Time!", input.testValidateLine(invalidTime));
    }
    
    @Test
    public void testValidateLineInvalidDirection() {
	SystemFile input = new SystemFile("Filename.txt");
	String invalidDirection = "10:02:21 2 DIRECTION 4";
	assertFalse("Invalid Time!", input.testValidateLine(invalidDirection));
    }
    
    @Test
    public void testValidateLineInvalidStartFloor() {
	SystemFile input = new SystemFile("Filename.txt");
	String invalidDirection = "10:02:21 Floor1 DIRECTION 4";
	assertFalse("Invalid Time!", input.testValidateLine(invalidDirection));
    }
    
    @Test
    public void testValidateLineInvalidDestinationFloor() {
	SystemFile input = new SystemFile("Filename.txt");
	String invalidDirection = "10:02:21 1 DIRECTION Floor2";
	assertFalse("Invalid Time!", input.testValidateLine(invalidDirection));
    }

    @Test
    public void testValidateLineReadFile() {
	SystemFile input = new SystemFile(TESTFILE1);
	String returnInfo = "14:05:15.22 2 Up 4";
	String output = input.testReadFile().get(0);
	assertTrue("Correct Read of TestFile1!", returnInfo.equals(output));
    }

}
