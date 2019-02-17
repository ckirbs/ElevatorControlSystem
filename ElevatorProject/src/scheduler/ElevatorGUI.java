package scheduler;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import static resources.Constants.HIGHEST_FLOOR;
import static resources.Constants.NUMBER_OF_ELEVATORS;

public class ElevatorGUI extends JPanel{
	
	private static final int SIZE_X = 2000;
	private static final int SIZE_Y = 1300;
	private static final int HEIGHT_OF_FLOOR_SEPERATOR = 5;
	private static final int HEIGHT_OF_FLOOR = 115;
	private static final int HEIGHT_OF_ELEVATOR = 100;
	private static final int WIDTH_OF_ELEVATOR = 100;
	private static final String DOOR_CLOSED_PATH = "src/Resources/closedFloors.jpg";
	
	private JFrame frame;
	private int x = 0;
	
	
	Image doorsClosed;
	
	public ElevatorGUI() {
		super();
		frame = new JFrame("Elevator System");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(this);
		frame.setSize(SIZE_X,SIZE_Y);
		frame.setVisible(true);
		setDoubleBuffered(true);
		try {
			doorsClosed = ImageIO.read(new File(DOOR_CLOSED_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.fillRect(30, 5, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR);
		for (int i = 0; i < HIGHEST_FLOOR; i++) {
			g.fillRect(0, HEIGHT_OF_FLOOR + (HEIGHT_OF_FLOOR * i), SIZE_X, HEIGHT_OF_FLOOR_SEPERATOR);
			g.drawString("Floor " + (HIGHEST_FLOOR - i), SIZE_X - 100, (HEIGHT_OF_FLOOR * i) - 90);
			for (int j = 0; j < NUMBER_OF_ELEVATORS; j++) {
				g.drawImage(doorsClosed, ((SIZE_X/NUMBER_OF_ELEVATORS) * j) + (i % 2) * 200, (HEIGHT_OF_FLOOR * i) + 5, WIDTH_OF_ELEVATOR, HEIGHT_OF_ELEVATOR, this);
			}
		}
		for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
			
		}
		g.drawString("Floor 0", SIZE_X - 100, (HEIGHT_OF_FLOOR * 10) - 90);
	}
	
	public static void main (String args[]) {
		ElevatorGUI gui = new ElevatorGUI();
		while(true) {
			gui.addX();
			gui.repaint();
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addX() {
		x++;
	}
}
