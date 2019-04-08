package scheduler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import resources.Directions;

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
	private static final String DOOR_CLOSED_IMAGE_PATH = "src/Resources/closedFloors.jpg";
	private static final String DOOR_OPEN_IMAGE_PATH = "src/Resources/openFloors.jpg";
	private static final String ELEVATOR_IMAGE_PATH = "src/Resources/insideElevator.png";
	private static final int HEIGHT_OF_ELEVATOR_IMAGE = 125;
	private static final int WIDTH_OF_ELEVATOR_IMAGE = 125;
	
	private JFrame frame;
	private int x = 0;
	
	private ElevatorListener elevatorListener;
	private FloorListener floorListener;
	private Dispatcher dispatcher;
	
	Image doorsClosed;
	Image doorsOpen;
	Image elevator;
	
	ArrayList<Set<Integer>> elevatorButtonsPressed = new ArrayList<Set<Integer>>();
	
	boolean upArrow = false;
	boolean downArrow = false;
	
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
		
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			elevatorButtonsPressed.add(new HashSet<Integer>());
		}
		
		try {
			doorsClosed = ImageIO.read(new File(DOOR_CLOSED_IMAGE_PATH));
			doorsOpen = ImageIO.read(new File(DOOR_OPEN_IMAGE_PATH));
			elevator = ImageIO.read(new File(ELEVATOR_IMAGE_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		HashMap<Integer, ArrayList<Integer>> floorWithOpenDoor = floorListener.getFloorsWithOpenDoor();
		
		ArrayList<ArrayList<Set<Integer>>> dest = floorListener.getDestinations();
		
		ArrayList<Elevator> elevators = dispatcher.getElevators();
		
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			for (int k = 0; k < HIGHEST_FLOOR; k++) {
				try {
					elevatorButtonsPressed.get(i).addAll(dest.get(i).get(k));
				} catch (Exception e) {
					
				}
			}
			try {
				elevatorButtonsPressed.get(i).remove(new Integer(elevators.get(i).getFloor()));
			} catch (Exception e) {
				
			}
		}
		
		g.fillRect(SIZE_X/2 - 200, 0, HEIGHT_OF_FLOOR_SEPERATOR, SIZE_Y);
		
		Set<Integer> buttonsPressed;

		for (int i = 0; i < HIGHEST_FLOOR; i++) {
			upArrow = false;
			downArrow = false;
			buttonsPressed = new HashSet<Integer>();
			g.fillRect(0, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_FLOOR, SIZE_X/2 - 200, HEIGHT_OF_FLOOR_SEPERATOR);
			g.drawString("Floor " + (HIGHEST_FLOOR - i), 20, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + 5);
			for (int k = 0; k < NUMBER_OF_ELEVATORS; k++) {
				try {
					buttonsPressed.addAll(dest.get(k).get(HIGHEST_FLOOR - i));
				} catch (Exception e) {
					
				}
			}
			g.drawString("Buttons Pressed: " + buttonsPressed, 350, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + 10);
			for (Integer p : buttonsPressed) {
				if (p > (HIGHEST_FLOOR - i)) {
					upArrow = true;
				} else if (p < (HIGHEST_FLOOR - i)) {
					downArrow = true;
				}
			}
			String arrows = "";
			if (upArrow) {
				arrows += "Up";
			}
			if (upArrow && downArrow) {
				arrows += ", Down";
			} else if (downArrow) {
				arrows += "Down";
			}
			g.drawString("Direction Arrows: " + arrows, 350, ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + 30);
			ArrayList<Integer> openElevators = floorWithOpenDoor.get(HIGHEST_FLOOR - i);
			for (int j = 0; j < NUMBER_OF_ELEVATORS; j++) {
				if (openElevators.contains(j)) {
					g.drawImage(doorsOpen, WIDTH_OF_BUFFER + (80 * j), ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_TEXT, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR, this);
				} else {
					g.drawImage(doorsClosed, WIDTH_OF_BUFFER + (80 * j), ((HEIGHT_OF_FLOOR + HEIGHT_OF_TEXT) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_TEXT, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR, this);
				}
			}
		}
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			g.drawString("Elevator: " + (i + 1), SIZE_X/2 - 150, HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			g.drawString("Current floor: " + elevators.get(i).getFloor(), SIZE_X/2, HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			if (elevators.get(i).getDir() == Directions.ERROR_HARD | elevators.get(i).getDir() == Directions.ERROR_SOFT) {
				g.setColor(Color.RED);
				g.drawString("Direction: " + elevators.get(i).getDir(), SIZE_X/2, 20 + HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
				g.setColor(Color.BLACK);
			} else {
				g.drawString("Direction: " + elevators.get(i).getDir(), SIZE_X/2, 20 + HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			}
			ArrayList<Integer> openElevators = floorWithOpenDoor.get(elevators.get(i).getFloor());
			g.drawString("Destinations: " + elevatorButtonsPressed.get(i), SIZE_X/2, 60 + HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			if (openElevators.contains(i)) {
				g.drawString("Door: open", SIZE_X/2, 40 + HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			} else {
				g.drawString("Door: closed", SIZE_X/2, 40 + HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 10);
			}	
			g.fillRect(SIZE_X/2 - 200, ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + HEIGHT_OF_BUFFER + HEIGHT_OF_FLOOR + 70, SIZE_X/2, HEIGHT_OF_FLOOR_SEPERATOR);
			g.drawImage(elevator, SIZE_X/2 + HEIGHT_OF_FLOOR_SEPERATOR + HEIGHT_OF_BUFFER - 200, HEIGHT_OF_BUFFER + ((HEIGHT_OF_ELEVATOR_IMAGE + HEIGHT_OF_BUFFER + 40) * i) + 20, WIDTH_OF_ELEVATOR_IMAGE, HEIGHT_OF_ELEVATOR_IMAGE, this);
		}
	}

	@Override
	public void run() {
		while(true) {
			this.repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
