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
	private HashMap<String, String> allClients;
	// private ArrayList<ServerClientThread> allClients;
	private ArrayList<ServerClientThread> activeClientList;
	// chatrooms
	private ArrayList<Chatroom> chatrooms;
	private ServerFrame serverFrame;
	
	public ServerStart(int port) throws IOException{
		/*
		 * constructor just creates the server socket using the port
		 * and initialise the lists
		 */
		this.serverMain = new ServerSocket(port);
		this.allClients = new HashMap<String, String>();
		this.activeClientList = new ArrayList<ServerClientThread>();
		
		this.serverFrame = new ServerFrame(this);
	}
	
	public HashMap<String,String> getAllUsers() { return allClients; }
	
	public ArrayList<ServerClientThread> getAllActiveUsers() { return activeClientList; }
	
	public ArrayList<Chatroom> getRooms(){ return chatrooms; }
	
	public void setAllUsers(HashMap<String, String> allNewClients) {
		this.allClients = allNewClients;
	}
	
	public void setAllActiveUsers(ArrayList<ServerClientThread> activeClients) {
		this.activeClientList = activeClients;
	}
	
	public ServerClientThread getUser(String name) {
		for (ServerClientThread client : activeClientList) {
			System.out.println(name);
			if (client.getUsername().contentEquals(name)) { return client; }
		}
		return null;
	}
	
	public void addRoom(String name) {
		/*
		 *  create a new room
		 */
		Chatroom newRoom = new Chatroom(name);
		chatrooms.add(newRoom);
	}
	
	public void writeLog(String message) { serverFrame.writeLog(message); }
	
	public void sendRoomClients(String roomName) {
		for (ServerClientThread client : activeClientList) { client.sendRooms("a", roomName); break; }
	}
	
	public void sendRoom(String roomName) { serverFrame.displayRoom(roomName); }
	
	public void newClient(ServerClientThread client, String room, boolean remove) {
		String message = client.getUsername() + " (" + client.getNickname() + ") "
						 + "[" + room + "]";
		if (remove) { serverFrame.removeClient(message); }
		else { serverFrame.addClient(message); }
	}
	
	public void changeRoom(ServerClientThread client, String oldRoom, String newRoom) {
		String message = client.getUsername() + " (" + client.getNickname() + ") ";
		String oldMessage = message + "[" + oldRoom + "]";
		String newMessage = message + "[" + newRoom + "]";
		serverFrame.changeClient(oldMessage, newMessage);
	}
	
	public void changeNickname(ServerClientThread client, String oldNickname, String newNickname) {
		String oldMessage= client.getUsername() + " (" + oldNickname + ") " + "[" + client.getRoom().getName() + "]";
		String newMessage= client.getUsername() + " (" + newNickname + ") " + "[" + client.getRoom().getName() + "]";
		serverFrame.changeClient(oldMessage, newMessage);
	}
	
	public void changeRoomname(String oldName, String newName) {
		for (Chatroom room : chatrooms) {
			if (room.getName().contentEquals(oldName)) {
				for (ServerClientThread client : activeClientList) {
					String oldRoom = client.getRoom().getName();
					System.out.println("oldName: " + oldName + " newName " + newName + " client " + oldRoom);
					if (oldRoom.contentEquals(oldName)) { changeRoom(client, oldName, newName); }
					client.sendMessage("!roomn" + oldName + "," + newName);
				}
				room.setName(newName);
				break;
			}
		}
	}
	
	public void deleteRoom(String roomName) {
		// room cant be main
		for (Chatroom room : chatrooms) {
			if (room.getName().contentEquals(roomName)) {
				// remove clients from room
				ArrayList<ServerClientThread> clients = room.getClients();
				for (int i = 0; i < clients.size(); i++) {
					ServerClientThread client = clients.get(i);
					client.joinRoom(chatrooms.get(0).getName());
					String message= client.getUsername() + " (" + client.getNickname() + ") ";
					String oldMessage = message + "[" + roomName + "]";
					String newMessage = message + "[" + chatrooms.get(0).getName() + "]";
					serverFrame.changeClient(oldMessage, newMessage);
				}
				chatrooms.remove(room);
				for (ServerClientThread client : activeClientList) {
					client.sendMessage("!roomr" + roomName);
				}
				break;
			}
		}
	}
	
	@Override
	public void run() {
		try {
			// read all existing users from the csv file
			allClients = ReadAndWriteCsv.readCsv();
			// create a room called main as the default room
			chatrooms = new ArrayList<Chatroom>();
			Chatroom main = new Chatroom("main");
			chatrooms.add(main);
			sendRoom("main");
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
