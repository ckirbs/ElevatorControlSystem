package test.elevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.invoke.ConstantCallSite;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorReciever;
import resources.Constants;
import resources.Directions;

public class ElevatorTest {

	static ElevatorReciever elvRec;
	// List of all elevators in system
	static Iterator<Elevator> elvList;

	@Before
	public void initElvSubSystem() {
		elvRec = new ElevatorReciever();
		elvList = elvRec.getElevators().iterator();
	}

	@Test
	public void testNewElevatorStatus() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			assertEquals("Elevator " + elv.getElvNumber() + " had unexpected status " + elv.getStatus(),
					Directions.STANDBY, elv.getStatus());
		}
	}

	@Test
	public void testCanServiceRequestTrue() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			assertEquals("Elevator failed to accept a valid request", true, elv.canServiceCall(5));
		}
	}

	@Test
	public void testCanServiceRequestFalse() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			assertEquals("Elevator failed to refuse a invalid service request", false, elv.canServiceCall(-1));
		}
	}

	@Test
	public void testAddToServiceQueue() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			elvRec.addFloorToService(elv.getElvNumber(), 10);
			assertEquals("Elevator " + elv.getElvNumber() + " should have destionation 10", 10,
					(int) elv.getFloorDestionation());
		}
	}

	@Test
	public void testProcessMessageVolValid() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			elvRec.processSchedulerMsg(new byte[] { Constants.NEW_ELEVATOR_DESTINATION, Constants.VOLUNTARY, 10,
					(byte) (int) elv.getElvNumber() });
			assertEquals("Elevator " + elv.getElvNumber() + " should have destionation 10", 10,
					(int) elv.getFloorDestionation());
		}
	}

	@Test
	public void testProcessMessageVolInvalid() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			elvRec.processSchedulerMsg(new byte[] { Constants.NEW_ELEVATOR_DESTINATION, Constants.VOLUNTARY, -1,
					(byte) (int) elv.getElvNumber() });
			assertEquals("Elevator " + elv.getElvNumber() + " should not have a destionation", null,
					elv.getFloorDestionation());
		}
	}

	@Test
	public void testElevatorMovement() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			elvRec.processSchedulerMsg(new byte[] {Constants.NEW_ELEVATOR_DESTINATION, Constants.VOLUNTARY, 10, (byte) (int) elv.getElvNumber()});
			
			try {
				Thread.sleep(Constants.ELEVATOR_TRAVEL_SPEED_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			assertNotEquals("Elevator " + elv.getElvNumber() + " has position 0 but should move towards floor 10", 0, (int) elv.getCurrFloorPosition());
		}
	}
}
