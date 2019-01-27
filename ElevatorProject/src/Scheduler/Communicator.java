package Scheduler;

import static Resources.Constants.ELEVATOR_PORT;
import static Resources.Constants.FLOOR_PORT;
import static Resources.Constants.MAX_DIFF;

import Scheduler.Dispatcher;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import Resources.Directions;

import java.net.DatagramPacket;

public class Communicator {
	private static Dispatcher dispatcher;
	private static int timeoutTime =50;
	
	public Communicator() {
		Communicator.dispatcher = new Dispatcher();
	}

	public static void serviceFloorRequest(Directions dir, int floor) {
		pingElevators();
		
		Object elevator = dispatcher.getNearestElevator(dir, floor);
		
		if (elevator == null) {
			// Do Something
			// Probably store the request and try again
		} else {
			// send command to elevator
		}
	}
	
	public static void pingElevators() {
		int numberOfElevators = 27; // temp number to rough in function

		try {
			DatagramSocket socket1 = new DatagramSocket();
			DatagramPacket packet1;
			
			for (int i = 0; i < numberOfElevators; i++) {
				try {
					packet1 = new DatagramPacket(new byte[100], 100);
					socket1.setSoTimeout(timeoutTime);
					socket1.receive(packet1);
					
					// Decode packet and store info
					
											
				} catch (Exception e) {}
			}
			
			dispatcher.updateElevatorInfo(new Object());
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			DatagramSocket socket1 = new DatagramSocket();
			DatagramPacket packet1;
			
			while (true) {
				for (int i = 0; i < MAX_DIFF - 1; i++) {
					try {
						packet1 = new DatagramPacket(new byte[100], 100);
						socket1.setSoTimeout(timeoutTime);
						socket1.receive(packet1);
						
						// Decode packet
						serviceFloorRequest(null, -1);
												
					} catch (Exception e) {}
				}
			}
			
			
		} catch (IOException e) {
			System.out.println("Error occured regarding the sockets..");
			e.printStackTrace();
		}
	}
}

