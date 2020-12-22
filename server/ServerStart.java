package server;

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
	private HashMap<String, String> allClients;
	// private ArrayList<ServerClientThread> allClients;
	private ArrayList<ServerClientThread> activeClientList;
	// chatrooms
	private ArrayList<Chatroom> chatrooms;
	private ServerFrame serverFrame;
	private String frameRoom;
	private boolean online;
	
	// 1. Constructor
	public ServerStart(int port) throws IOException{
		/*
		 * constructor just creates the server socket using the port
		 * and initialise the lists
		 */
		this.online = true;
		this.serverMain = new ServerSocket(port);
		this.allClients = new HashMap<String, String>();
		this.activeClientList = new ArrayList<ServerClientThread>();
		this.serverFrame = new ServerFrame(this);
		// room of the frame
		this.frameRoom = null;
	}
	
	// 2. Getter and Setter 
	public HashMap<String,String> getAllUsers() { return allClients; }
	
	public ArrayList<ServerClientThread> getAllActiveUsers() { return activeClientList; }
	
	public ArrayList<Chatroom> getRooms(){ return chatrooms; }
	
	public void setFrameRoom(String room) { this.frameRoom = room; }
	
	public void setAllActiveUsers(ArrayList<ServerClientThread> activeClients) {
		this.activeClientList = activeClients;
	}
	
	public void setAllUsers(HashMap<String, String> allNewClients) {
		this.allClients = allNewClients;
	}
	
	// 3. Serverframe functions
	public void setOnline(String action) { serverFrame.changeOnline(action); }
	
	public String getTitle() { return serverFrame.returnTitle(); }
	
	public ServerClientThread getUser(String name) {
		/*
		 * returns - if existing - the user with username == name, else return null
		 */
		for (ServerClientThread client : activeClientList) {
			System.out.println(name);
			if (client.getUsername().contentEquals(name)) { return client; }
		}
		return null;
	}
	
	public boolean sameRoom(String room) {
		/*
		 * check if admin is in the chatroom with name room
		 */
		if (frameRoom == null) { return false; }
		if (room.contentEquals(frameRoom)) { return true; }
		return false;
	}
	
	public void sendToFrame(String message) {
		/*
		 * displays a given message in the frame
		 */
		serverFrame.displayMessage(message);
	}
	
	public void writeLog(String message) { 
		/*
		 * write log message
		 */
		serverFrame.writeLog(message);
	}
	
	public void newClient(ServerClientThread client, String room, boolean remove) {
		/*
		 * add or remove a new client to the serverframe (add then remove == false)
		 */
		String message = client.getUsername() + " (" + client.getNickname() + ") "
						 + "[" + room + "]";
		if (remove) { serverFrame.removeClient(message); }
		else { serverFrame.addClient(message); }
	}
	
	public void changeRoom(ServerClientThread client, String oldRoom, String newRoom) {
		/*
		 * if a client changed from oldRoom to newRoom we need to change its entry in the frame
		 */
		String message = client.getUsername() + " (" + client.getNickname() + ") ";
		String oldMessage = message + "[" + oldRoom + "]";
		String newMessage = message + "[" + newRoom + "]";
		serverFrame.changeClient(oldMessage, newMessage);
	}
	
	public void changeNickname(ServerClientThread client, String oldNickname, String newNickname) {
		/*
		 * if a client changes its nickname we need to apply changes in the frame
		 */
		String oldMessage= client.getUsername() + " (" + oldNickname + ") " + "[" + client.getRoom().getName() + "]";
		String newMessage= client.getUsername() + " (" + newNickname + ") " + "[" + client.getRoom().getName() + "]";
		serverFrame.changeClient(oldMessage, newMessage);
	}
	
	// 4. Room functions
	public void addRoom(String name) {
		/*
		 *  create a new room
		 */
		Chatroom newRoom = new Chatroom(name);
		chatrooms.add(newRoom);
	}
	
	public void sendRoomClients(String roomName) {
		/*
		 * for every client send the new room for display and send it to the server as well
		 */
		for (ServerClientThread client : activeClientList) { client.sendRooms("a", roomName); break; }
		serverFrame.displayRoom(roomName);
	}
	
	public void changeRoomname(String oldName, String newName) {
		/*
		 * change the roomname of oldname to newname
		 * we have to update every client as well as the serverframe
		 */
		for (Chatroom room : chatrooms) {
			if (room.getName().contentEquals(oldName)) {
				for (ServerClientThread client : activeClientList) {
					String oldRoom = client.getRoom().getName();
					// change display in serverframe
					if (oldRoom.contentEquals(oldName)) { changeRoom(client, oldName, newName); }
					// changes roomname for client
					client.sendMessage("!roomn" + oldName + "," + newName);
				}
				room.setName(newName);
				break;
			}
		}
	}
	
	public void deleteRoom(String roomName) {
		/*
		 * deletes a room in chatrooms with name roomName != main
		 * sends all users in the room to main
		 */
		for (Chatroom room : chatrooms) {
			if (room.getName().contentEquals(roomName)) {
				// remove clients from room
				ArrayList<ServerClientThread> clients = room.getClients();
				for (int i = 0; i < clients.size(); i++) {
					ServerClientThread client = clients.get(i);
					// join client into main
					client.joinRoom(chatrooms.get(0).getName());
					// change client in serverFrame
					String message= client.getUsername() + " (" + client.getNickname() + ") ";
					String oldMessage = message + "[" + roomName + "]";
					String newMessage = message + "[" + chatrooms.get(0).getName() + "]";
					serverFrame.changeClient(oldMessage, newMessage);
				}
				// removes room from arraylist
				chatrooms.remove(room);
				// send updated versions of rooms
				for (ServerClientThread client : activeClientList) {
					client.sendMessage("!roomr" + roomName);
				}
				break;
			}
		}
	}
	
	// 5 send to all users
	public void sendToAll(String message) {
		/*
		 * writes a message from the server frame to every client
		 */
		for (ServerClientThread client : activeClientList) { client.sendMessage(message); }
	}
	
	// 6. run and main Method
	@Override
	public void run() {
		try {
			// read all existing users from the csv file
			allClients = ReadAndWriteCsv.readCsv(0);
			// create a room called main as the default room
			chatrooms = new ArrayList<Chatroom>();
			Chatroom main = new Chatroom("main");
			chatrooms.add(main);
			serverFrame.displayRoom("main");
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
	
	public void waitUntil() {
		if (this.online) { this.online = false; }
		else { this.online = true; }
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
