package test.scheduler;

import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

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
}
