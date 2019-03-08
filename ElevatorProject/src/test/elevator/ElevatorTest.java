package test.elevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.junit.After;
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

	@After
	public void teardown() {
		elvRec.closeSocket();
		elvRec = null;
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
			assertEquals("Elevator failed to refuse a invalid service request", false,
					elv.canServiceCall(Constants.LOWEST_FLOOR - 1));
		}
	}

	@Test
	public void testAddToServiceQueue() {
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			elvRec.addFloorToService(elv.getElvNumber(), 10, Directions.UP);
			assertEquals("Elevator " + elv.getElvNumber() + " should have destionation 10", 10,
					(int) elv.getFloorDestionation());
		}
	}

	@Test
	public void testProcessMessageVolValid() throws UnknownHostException {
		DatagramPacket packet;
		byte[] msg;
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			
			msg = new byte[] {Constants.NEW_ELEVATOR_DESTINATION, Constants.VOLUNTARY, 10, (byte) (int) elv.getElvNumber(), (byte) Directions.getIntByDir(Directions.UP), (byte) 1 };
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("127.0.0.1"), Constants.ELEVATOR_PORT);
			
			elvRec.processSchedulerMsg(packet);
		}
	}

	@Test
	public void testProcessMessageVolInvalid() throws UnknownHostException {
		
		DatagramPacket packet;
		byte[] msg;
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			msg = new byte[] {Constants.NEW_ELEVATOR_DESTINATION, Constants.VOLUNTARY, -1, (byte) (int) elv.getElvNumber(), (byte) Directions.getIntByDir(Directions.UP) };
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("127.0.0.1"), Constants.ELEVATOR_PORT);
			
			elvRec.processSchedulerMsg(packet);
			
			assertEquals("Elevator " + elv.getElvNumber() + " should not have a destionation", null,
					elv.getFloorDestionation());
		}
	}

	@Test
	public void testElevatorMovement() throws UnknownHostException {
		DatagramPacket packet;
		byte[] msg;
		while (elvList.hasNext()) {
			Elevator elv = elvList.next();
			
			msg = new byte[] {Constants.NEW_ELEVATOR_DESTINATION, Constants.MANDATORY, 10, (byte) (int) elv.getElvNumber(), (byte) Directions.getIntByDir(Directions.UP) };
			packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("127.0.0.1"), Constants.ELEVATOR_PORT);
			
			elvRec.processSchedulerMsg(packet);

			try {
				Thread.sleep(Constants.ELEVATOR_TRAVEL_SPEED_MS + 200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			assertNotEquals("Elevator " + elv.getElvNumber() + " has position 0 but should move towards floor 10", 0,
					(int) elv.getCurrFloorPosition());
		}
	}
}
