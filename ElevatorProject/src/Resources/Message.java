package Resources;

import java.time.LocalTime;
import java.util.Queue;
import java.util.LinkedList;

/*
 * Message - Responsible for creating all of the users requests
 */
public class Message {

    private LocalTime time;
    private int startingFloor;
    private Directions direction;
    private int destinationFloor;
    public static Queue<Message> messageQueue = new LinkedList<Message>();

    /*
     * Message - Creates a message object which stores the time the user made
     * the request, the floor they are currently on, the direction they want to
     * go and the floor they want to go to Adds created message to the
     * messageQueue
     * 
     * @param: LocalTime time, int startingFloor, Direction direction, int
     * destinationFloor
     */
    public Message(LocalTime time, int startingFloor, Directions direction, int destinationFloor) {
	this.time = time;
	this.startingFloor = startingFloor;
	this.direction = direction;
	this.destinationFloor = destinationFloor;
	messageQueue.add(this);
    }

    /*
     * getTime - Gets the time return:
     * 
     * @return: LocalTime - the time
     */
    public LocalTime getTime() {
	return time;
    }

    /*
     * getStartingFloor - Gets the starting floor
     * 
     * @return: int - The starting floor
     */
    public int getStartingFloor() {
	return startingFloor;
    }

    /*
     * getDirection - Gets the direction
     * 
     * @return: Directions - the direction
     */
    public Directions getDirection() {
	return direction;
    }

    /*
     * getDestinationFloor - Gets the destination floor
     * 
     * @return: int - the destination floor
     */
    public int getDestinatinoFloor() {
	return destinationFloor;
    }

    /*
     * getMessageQueue - Gets a Queue of all the Messages
     * 
     * @return: Queue<Message> - queue of Messages
     */
    public static Queue<Message> getMessageQueue() {
	return messageQueue;
    }

    /*
     * toString - Overrides toString method to return the time, start floor,
     * direction and destination floor
     * 
     * @return: String
     */
    public String toString() {
	return (this.getTime().toString() + " " + this.getStartingFloor() + " " + this.getDirection().toString() + " "
		+ this.getDestinatinoFloor());
    }
}
