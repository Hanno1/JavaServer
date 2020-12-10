package master;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerStart extends Thread implements Runnable {
	/*
	 * starts the server with a given port. manage 2 list with clients
	 * and one with chatrooms
	 */
	// initialise port
	private final static int port = 1244;
	// set exit variable (if true we kill the server)
	private boolean exit = false;
	private ServerSocket serverMain;
	// 2 lists to manage the clients. one with all clients
	// and one with the active ones
	private ArrayList<ServerClientThread> allClients;
	private ArrayList<ServerClientThread> activeClientList;
	// chatrooms
	private ArrayList<Chatroom> chatrooms;
	
	public ServerStart(int port) throws IOException{
		/*
		 * constructor just creates the server socket using the port
		 * and initialise the lists
		 */
		this.serverMain = new ServerSocket(port);
		this.allClients = new ArrayList<ServerClientThread>();
		this.activeClientList = new ArrayList<ServerClientThread>();
	}
	
	public ArrayList<ServerClientThread> getAllUsers() { return allClients; }
	
	public ArrayList<ServerClientThread> getAllActiveUsers() { return activeClientList; }
	
	public ArrayList<Chatroom> getRooms(){ return chatrooms; }
	
	public void setAllUsers(ArrayList<ServerClientThread> allNewClients) {
		this.allClients = allNewClients;
	}
	
	public void setAllActiveUsers(ArrayList<ServerClientThread> activeClients) {
		this.activeClientList = activeClients;
	}
	
	public void addRoom(String name) {
		/*
		 *  create a new room
		 */
		Chatroom newRoom = new Chatroom(name);
		chatrooms.add(newRoom);
	}
	
	@Override
	public void run() {
		try {
			// create a room called main as the default room
			chatrooms = new ArrayList<Chatroom>();
			Chatroom main = new Chatroom("main");
			chatrooms.add(main);
			while (!exit) {
				// accepts clients the entire time
				System.out.println("connecting...");
				Socket client = serverMain.accept();
				System.out.println(client + " is now connected!");
				// for every new client start a thread
				Thread userThread = new Thread(new ServerClientThread(this, client));
				userThread.start();
			}
		} catch (IOException e) { System.out.println("Fehler in run, ServerAccept Thread"); }
	}

	public static void main(String[] args) throws IOException {
		/*
		 *  just the main methode, nothing surprising
		 *  (Server start is a thread since we want to create a frame later on)
		 */
		ServerStart acceptingServer = new ServerStart(port);
		Thread accept = new Thread(acceptingServer);
		accept.start();
	}
}
