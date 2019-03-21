package scheduler;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import static resources.Constants.HIGHEST_FLOOR;
import static resources.Constants.NUMBER_OF_ELEVATORS;

public class ElevatorGUI extends JPanel implements Runnable{
	
	private static final int SIZE_X = 2000;
	private static final int SIZE_Y = 1300;
	private static final int HEIGHT_OF_FLOOR_SEPERATOR = 5;
	private static final int HEIGHT_OF_FLOOR = 90;
	private static final int HEIGHT_OF_ELEVATOR = 75;
	private static final int WIDTH_OF_ELEVATOR = 75;
	private static final int HEIGHT_OF_TEXT = 10;
	private static final int HEIGHT_OF_BUFFER = 10;
	private static final int WIDTH_OF_BUFFER = 10;
	private static final String DOOR_CLOSED_PATH = "src/Resources/closedFloors.jpg";
	private static final String DOOR_OPEN_PATH = "src/Resources/openFloors.jpg";
	
	private JFrame frame;
	private int x = 0;
	
	private ElevatorListener elevatorListener;
	private FloorListener floorListener;
	private Dispatcher dispatcher;
	
	Image doorsClosed;
	Image doorsOpen;
	
	public ElevatorGUI(ElevatorListener elevatorListener, FloorListener floorListener) {
		super();
		this.elevatorListener = elevatorListener;
		this.floorListener = floorListener;
		dispatcher = this.elevatorListener.getDispatcher();
		frame = new JFrame("Elevator System");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setSize(SIZE_X,SIZE_Y);
		frame.setVisible(true);
		setDoubleBuffered(true);
		try {
			doorsClosed = ImageIO.read(new File(DOOR_CLOSED_PATH));
			doorsOpen = ImageIO.read(new File(DOOR_OPEN_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		HashMap<Integer, ArrayList<Integer>> floorWithOpenDoor = floorListener.getFloorsWithOpenDoor();
		for (int i = 0; i < HIGHEST_FLOOR; i++) {
			g.fillRect(0, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_FLOOR, SIZE_X/2, HEIGHT_OF_FLOOR_SEPERATOR);
			g.drawString("Floor " + (HIGHEST_FLOOR - i), 20, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + 5);
			
			ArrayList<Integer> openElevators = floorWithOpenDoor.get(HIGHEST_FLOOR - i);
			for (int j = 0; j < NUMBER_OF_ELEVATORS; j++) {
				if (openElevators.contains(j)) {
					g.drawImage(doorsOpen, WIDTH_OF_BUFFER + (80 * j), ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_TEXT, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR, this);
				} else {
					g.drawImage(doorsClosed, WIDTH_OF_BUFFER + (80 * j), ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_TEXT, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR, this);
				}
			}
		}
	}
	
//	public static void main (String args[]) {
//		ElevatorGUI gui = new ElevatorGUI();
//		while(true) {
//			gui.repaint();
//			try {
//				Thread.sleep(15);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	@Override
	public void run() {
		while(true) {
			this.repaint();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
