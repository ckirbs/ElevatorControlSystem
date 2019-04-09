package test.scheduler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import resources.Directions;
import scheduler.Communicator;
import static resources.Constants.*;

import java.util.ArrayList;

public class CommunicatorTest {
	private Communicator comm;
	
	@Before
	public void setup() {
		comm = new Communicator();
	}
	
	@Test
	public void testInvalidMsgTypes() {
		ArrayList<Byte> validTypes = new ArrayList<Byte>();
		validTypes.add(ERROR);
		validTypes.add(NEW_REQUEST_FROM_FLOOR);
		validTypes.add(REQUEST_RECEIVED);
		validTypes.add(OPEN_CLOSE_DOOR);
		validTypes.add(NEW_ELEVATOR_DESTINATION);
		validTypes.add(ELEVATOR_INFO_REQUEST);
		validTypes.add(CONFIRM_VOL_DESTINATION);
		validTypes.add(STATUS_REPORT);
		
		byte[] msg = new byte[5];
		
		// For every possible byte value
		for (int b = -128; b < 128; b++) {
			// If the byte isn't a valid type value, send a message with it as the type to the communicator
			if (!validTypes.contains((byte) b)) {
				msg[0] = (byte) b;
				assertFalse("Invalid Message Type Processed. Type Value: " + b, comm.handleNewMessage(msg));
			}
		}
	}
	
	@Test
	public void testProcessStatusReport() {
		byte[] msg = new byte[] {STATUS_REPORT, 0, 0, 0, 0};
		
		for (int i = LOWEST_FLOOR - 1; i < HIGHEST_FLOOR + 2; i++) {
			for (int j = -1; j < NUMBER_OF_ELEVATORS + 2; j++) {
				for (Directions dir: Directions.values()) {
					msg[1] = (byte) Directions.getIntByDir(dir);
					msg[2] = (byte) i;
					msg[3] = (byte) j;
					
					if (i < LOWEST_FLOOR ||i > HIGHEST_FLOOR || j < 0 || j >= NUMBER_OF_ELEVATORS) {
						assertFalse("Not properly processed: Floor " + i + ", Elevator " + j + ", " + dir, comm.handleNewMessage(msg));
					} else {
						assertTrue("Not properly processed: Floor " + i + ", Elevator " + j + ", " + dir, comm.handleNewMessage(msg));
					}
				}
			}
		}
	}
	
	@Test
	public void testProcessErrorRequest() {
		byte[] msg = new byte[] {ERROR, 0, 0, 0, 0};
		
		for (int j = -1; j < NUMBER_OF_ELEVATORS + 2; j++) {
				msg[1] = (byte) Directions.getIntByDir(Directions.ERROR_SOFT);
				msg[2] = (byte) j;
				
				if (j < 0 || j >= NUMBER_OF_ELEVATORS) {
					assertFalse("Invalid elevator processed: " + j, comm.handleNewMessage(msg));
				} else {
					assertTrue("Error not properly processed: " + j, comm.handleNewMessage(msg));
					msg[1] = (byte) Directions.getIntByDir(Directions.ERROR_HARD);
					assertTrue("Error not properly processed: " + j, comm.handleNewMessage(msg));
				}
		}
	}
}
