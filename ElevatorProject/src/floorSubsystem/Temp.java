package floorSubsystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class Temp {
	public static void main(String args[]) {
		try {
			//Initialize variables
			byte[] bufFromClient;
			DatagramSocket datagramSocketClient = new DatagramSocket(23);
			DatagramPacket packetReceive;
			
			//Wait for another packet from the Client
			while (true) {
				//Re-initialize buffers and packet received from client
				bufFromClient = new byte[30];
				packetReceive = new DatagramPacket(bufFromClient, bufFromClient.length);
				datagramSocketClient.receive(packetReceive);
				
				//Print out packet from client
				System.out.println("Packet Received from Client: " + Arrays.toString(packetReceive.getData()) + "\n");
			}
		} catch (Exception e) {
			
		}
	}
}
