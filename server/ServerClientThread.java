package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerClientThread extends Thread implements Runnable {
	/*
	 * This is the heart of the server: manages a client
	 */
	private boolean exit = false;
	private boolean loged = false;
	
	private ServerStart server;
	private Socket client;
	private String name;
	private String password;
	private String nickname;
	private PrintWriter out;
	private BufferedReader in;
	private Chatroom chatroom;
	
	public ServerClientThread(ServerStart server, Socket client) throws IOException {
		/*
		 * constructor initialise variable and the input and output stream
		 */
		this.server = server;
		this.client = client;
		this.name = "";
		this.nickname = "";
		this.password = "";
		this.out = new PrintWriter(client.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		this.chatroom = null;
	}
	
	// 1. getter and setter
	public Chatroom getRoom() { return this.chatroom; }
	
	public void setRoom(Chatroom room) { this.chatroom = room; }
	
	public String getUsername() { return this.name;	}
	
	public String getNickname() { return this.nickname;	}
	
	public String getPassword() { return this.password; }
	
	public String getUserpassword() { return this.password;	}
		
	public void setExit(boolean e) { exit = e; }
		
	// 2. Login Functionality
	private String checkName() throws IOException {
		/*
		 * calls login function and checks the given name using the login function
		 */
		String result = null;
		if (this.name != null && this.password != null) { result = login(); }
		try {
			// we need a bit of time before we can work with the result
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (result == null || result.contentEquals("!exist")) {
			// if password wrong or name does exist or user is online
			exit = true;
			// b for bad :D
			return "b";
		}
		// welcome message
		else {
			// prints welcome message, adds a online to the frame and changes title according to server
			out.println(result);
			server.setOnline("n");
			sendMessage(server.getTitle());
			// a fo accepted
			return "a";
		}
	}
	
	public String login() {
		/*
		 * login methode - gets both client lists from the server and change them accordingly
		 */
		HashMap<String, String> allClients = server.getAllUsers();
		ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
		String result = "Welcome " + name;
		boolean exist = false;
		// check if user is banned
		HashMap<String, String> banned = ReadAndWriteCsv.readCsv(1);
		for (Map.Entry<String,String> ban : banned.entrySet()) {
			if (ban.getKey().contentEquals(name)) { 
				server.writeLog("Banned User " + name + " just tried to login.");
				return null;
			}
		}
		// go through every existing client
		for (Map.Entry<String,String> client : allClients.entrySet()) {
			if (client.getKey().contentEquals(name)) {
				if (client.getValue().contentEquals(password)) {
					// if name and password match we write welcome back
					result = "Welcome back " + name;
					exist = true;
					server.writeLog(name + " just logged in again.");
					// if there is already a user with same name and password online
					// we dont want this to work
					for (ServerClientThread activeClient : activeClients) {
						if (activeClient.getUsername().contentEquals(name)) { return "!exist"; }
					}
					break;
				}
				// password wrong or name does exist
				else { 
					server.writeLog(name + " just entered wrong password.");
					return null;
				}
			}
		}
		// new client
		if (exist == false) {
			server.writeLog(name + " just logged in for the first time!");
			allClients.put(this.name, this.password);
			ReadAndWriteCsv.writeCsv(this.name,  this.password, 0);
		}
		// join room with name main (does exit since its the first room created)
		joinRoom("main");
		// send me all users in the room
		sendRooms("all", null);
		// sleep shortly to not overwork the output at the client side
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// add to active clients
		activeClients.add(this);
		// sets the lists in the server according to the changes
		server.setAllUsers(allClients);
		server.setAllActiveUsers(activeClients);
		server.newClient(this, this.chatroom.getName(), false);
		return result;
	}
	
	// 3. Logout 
	public void removeUser() {
		/*
		 * remove a user from everything important: remove from chatroom, active users
		 */
		// remove from active client list:
		ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
		int index = 0;
		for (ServerClientThread user : activeClients) {
			if (user.getUsername().contentEquals(name)) {
				activeClients.remove(index);
				break;
			}
			index++;
		}
		server.setAllActiveUsers(activeClients);
		// remove User from chatroom
		Chatroom formerRoom = chatroom;
		removeRoom(formerRoom, true);
		// send logout message and updated online list only after client is removed
		// so we wont send it to the client
		sendOnline("r", formerRoom, null);
		// update every online list
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// send logout message
		sendToAllR(name + " just logged Out", formerRoom);
		server.writeLog(name + " just logged Out.");
		server.setOnline("r");
	}
	
	// 4. RoomFunctionality
	public void createRoom(String roomName) {
		/*
		 * creates a new chatroom and sends a message to every user that 
		 * the room is now joinable
		 */
		// only if the room (name) doesnt already exist
		String roomPassword = null;
		if (roomName.length() > 2 && roomName.substring(0, 2).contentEquals("p!")) {
			for (int i = 3; i < roomName.length(); i++) {
				if (roomName.substring(i, i+1).contentEquals("!")) {
					roomPassword = roomName.substring(2, i);
					roomName = "[password] " + roomName.substring(i+1);
					break;
				}
			}
		}
		boolean exist = false;
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(roomName)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			if (roomPassword == null) {
				// add room to the server
				// basically just creates a new chatroom and add it to the list
				server.addRoom(roomName);
				server.writeLog(name + " just created a new Room named " + roomName + ".");
				server.sendRoomClients(roomName);
			}
			else {
				server.addPasswordRoom(roomName, roomPassword);
				server.writeLog(name + " just created a new Room named " + roomName + 
						" with password " + roomPassword);
				server.sendRoomClients(roomName);
			}
		}
	}
	
	public void joinRoom(String roomName) {
		/*
		 * add the client to the list of clients of the room with name = name
		 */
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(roomName)) {
				room.sendAll(name + " just joined!");
				// send to ther server as well if its in the same room
				boolean frameRoom = server.sameRoom(roomName);
				if (frameRoom) { server.sendToFrame(name + " just joined."); }
				try { Thread.sleep(300); }
				catch (InterruptedException e) { e.printStackTrace(); }
				// add the client to the list and set the attribute room
				room.joinClient(this);
				// sends all users in the room a updated online list
				sendOnline("a", null, null);
				// send all users who are online in the room
				sendOnline("all", null, null);
				break;
			}
		}
	}
	
	public void removeRoom(Chatroom room, boolean exit) {
		/*
		 * remove a client from a room
		 */
		if (exit) { server.newClient(this, this.chatroom.getName(), true); }
		// we need the assignement formerRoom to avoid null pointer exception
		Chatroom formerRoom = chatroom;
		// remove client from the list and set room attribute
		room.removeClient(this);
		// send leaving message and updated online list
		formerRoom.sendAll(name + " just left.");
		// send to the server as well if its in the same room
		boolean frameRoom = server.sameRoom(formerRoom.getName());
		if (frameRoom) { server.sendToFrame(name + " just left."); }
		sendOnline("r", formerRoom, null);
	}
	
	public void changeRoom(String newRoom) {
		/*
		 * just changes the room to the roomname name = newRoom
		 * Basically uses the functions remove and join Room
		 */
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(newRoom)) {
				if (room.privateRoom) {
					// ask for password
					this.sendMessage("!p");
					String password = null;
					try { password = in.readLine(); } 
					catch (IOException e) { e.printStackTrace(); }
					if (room == null || !room.getPassword().contentEquals(password)) {
						int index = rooms.indexOf(chatroom);
						this.sendMessage("!p" + index);
						changeRoom(chatroom.getName());
						return;
					}
				}
			}
		}
		if (!chatroom.getName().contentEquals(newRoom)) {
			// if we realy change room
			server.writeLog(name + " just changed from " + chatroom.getName() + " to " + newRoom + ".");
			sendMessage("--------");
		}
		server.changeRoom(this, chatroom.getName(), newRoom);
		removeRoom(chatroom, false);
		joinRoom(newRoom);
	}
	
	public void sendRooms(String action, String newRoom) {
		/*
		 * Send updated room to clients
		 * actions: all - send all rooms
		 * 			a	- add a new room
		 */
		if (action.contentEquals("all")) {
			// send all rooms (important then loged in)
			String line = "!roome";
			ArrayList<Chatroom> rooms = server.getRooms();
			for (Chatroom room : rooms) {
				line = line + "," + room.getName();
			}
			out.println(line);
		}
		else {
			// send a new Room for every Client
			if (action.contentEquals("a")) {
				String line = "!rooma" + newRoom;
				// get every client who is online right now
				ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
				for (ServerClientThread client : activeClients) {
					client.out.println(line);
				}
			}
		}
	}
	
	//private Rooms
	public void handlePrivateRoom(String message) {
		/*
		 * gets a message wich had ![p] at the beginning 
		 */
		ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
		if (message.length() > 7 && message.substring(0, 7).contentEquals("create!")) {
			// create and add new Frame
			String otherName = message.substring(7);
			for (ServerClientThread client : activeClients) {
				if (client.getUsername().contentEquals(otherName)) {
					client.sendMessage("![p]create!" + name);
					break;
				}
			}
			server.addPrivateRoom(this.name, otherName);
			server.writeLog(name + " just created a new private room with " + otherName);
		}
		else {
			String receiveName = "";
			int index = 0;
			for (int i = 0; i < message.length(); i++) {
				if (message.substring(i, i+1).contentEquals("!")) { index = i; break; }
				else { receiveName = receiveName + message.substring(i, i+1); }
			}
			for (ServerClientThread client : activeClients) {
				String newMessage = "![p]" + name + "!" + message.substring(index+1);
				if (client.getUsername().contentEquals(receiveName)) {
					client.sendMessage(newMessage);
					break;
				}
			}
			if (message.substring(index+1).contentEquals(this.name + " just closed the window.")) {
				server.writeLog(name + " just closed the private connection to " + receiveName);
				server.removePrivateRoom(this.name, receiveName);
			}
		}
	}
	
	// 5. Sending Functions
	public void sendMessage(String message) { out.println(message); }

	public void sendToAllR(String message, Chatroom room) {
		/*
		 * send a message to every client in the room or if room is null (if logout)
		 * we send it to all in the current chatroom
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (room != null) {
					// send message to all clients in room = room
					ArrayList<ServerClientThread> activeClients = room.getClients();
					for (ServerClientThread user : activeClients) {
						user.sendMessage(message);
					}
				}
				else {
					// sends message to all in the current room
					ArrayList<ServerClientThread> activeClients = chatroom.getClients();
					for (ServerClientThread user : activeClients) {
						user.sendMessage(message);
					}
				}
			}
		});
		t.start();
	}
	
	public void sendToAll(String message) {
		/*
		 * send normal chatmessage in the form nickname: message
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean frameRoom = server.sameRoom(chatroom.getName());
				if (frameRoom) { server.sendToFrame(nickname + ": " + message); }
				ArrayList<ServerClientThread> activeClients = chatroom.getClients();
				for (ServerClientThread user : activeClients) {
					user.sendMessage(nickname + ": " + message);
				}
			}
		});
		t.start();
	}
	
	// 6. Online List
	public void sendOnline(String action, Chatroom room, String line) {
		/*
		 * sends a code to update the online list of clients
		 * actions: r - removes client from the online list
		 * 			all - send every client
		 * 			a - add client to list
		 * 			c - change nickname
		 * 
		 * for remove we need the chatroom to avoid a nullPointerException
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// remove client
				if (action.contentEquals("r")) {
					ArrayList<ServerClientThread> activeClients = room.getClients();
					String newLine = "!onliner" + name + " (" + nickname + ")";
					// send to all clients in the formerRoom
					for (ServerClientThread user : activeClients) {
						user.sendMessage(newLine);
					}
				}
				else {
					ArrayList<ServerClientThread> activeClients = chatroom.getClients();
					// send a complete online list (needed at join room and new login)
					if (action.contentEquals("all")) {
						String newLine = "!onlinee";
						for (ServerClientThread client : activeClients) {
							newLine = newLine + "," + client.getUsername() + " (" + client.getNickname() + ")";
						}
						// only sent to this client
						sendMessage(newLine);
					}
					else {
						// displays a new user for every client in the room
						if (action.contentEquals("a")) {
							String newLine = "!onlinea" + name + " (" + nickname + ")";
							// dont send message to myself or i get double names
							// since we allready called action all to get all online names
							for (ServerClientThread user : activeClients) {
								if (!user.getUsername().contentEquals(name)) {
									user.sendMessage(newLine);
								}
							}
						}
						else {
							// write code to change nickname (a Bit complicated maybe)
							String newLine = "!onlinec" + name + " (" + line.substring(9) + ")" +
												"%" + name + " (" + nickname + ")";
							nickname = line.substring(9);
							for (ServerClientThread user : activeClients) {
								user.sendMessage(newLine);
							}
						}
					}
				}
			}
		});
		t.start();
	}
	
	// 7. Run and close Method
	@Override
	public void run() {
		/*
		 * run methode - reads name and password and checks it
		 * then it reads the user input and acts accordingly
		 */
		try {
			this.name = in.readLine();
			this.nickname = name;
			String pass = in.readLine();
			this.password = pass;
			// check name and password (is a thread)
			String res = checkName();
			// since im now loged in we have to be removed from a room as well
			if (res.contentEquals("a")) { loged = true; }
			try {
				// we need a bit of time before we can work with the result
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String line;
			while (!exit) {
				// checks user input and does something
				line = in.readLine();
				if (line == null) { removeUser(); break; }
				if (line.substring(0, 1).contentEquals("!")) {
					if (line.length() > 9 && line.substring(0, 9).contentEquals("!nickname")) {
						// change nickname
						server.changeNickname(this, this.nickname, line.substring(9));
						try { Thread.sleep(100); }
						catch (InterruptedException e) { e.printStackTrace(); }
						sendOnline("c", null, line);
					} else {
						if (line.length() > 6 && line.substring(0, 6).contentEquals("!rooma")) {
							// create new room
							String newRoom = line.substring(6);
							createRoom(newRoom);
						} else {
							if (line.length() > 6 && line.substring(0, 6).contentEquals("!roomc")) {
								// change room
								changeRoom(line.substring(6));
							} else {
								if (line.length() > 4 && line.substring(0, 4).contentEquals("![p]")) {
									handlePrivateRoom(line.substring(4));
								}
								else { System.out.println("unknown sequence " + line); }
							}
						}
					}
				}
				else {
					// send to everybody in the room
					this.sendToAll(line);
				}
			}
			this.closeClient(loged);
		}
		catch (IOException e) {
			try { this.closeClient(loged); }
			catch (IOException e1) { e1.printStackTrace(); }
		}
	}
	
	public void closeClient(boolean joined) throws IOException {
		/*
		 * closes input stream and client and remove client instance
		 */
		if (joined) { removeUser(); }
		this.sendMessage("!close");
		in.close();
		client.close();
		return;
	}
}

