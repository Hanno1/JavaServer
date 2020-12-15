package master;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerClientThread extends Thread implements Runnable {
	/*
	 * This is the heart of the server: manages a client
	 */
	private boolean exit = false;
	
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
	
	public Chatroom getRoom() { return this.chatroom; }
	
	public void setRoom(Chatroom room) { this.chatroom = room; }
	
	public String getUsername() { return this.name;	}
	
	public String getNickname() { return this.nickname;	}
	
	public String getPassword() { return this.password; }
	
	public String getUserpassword() { return this.password;	}
	
	public void sendMessage(String message) { out.println(message); }
	
	public String login() {
		/*
		 * login methode - gets both client lists from the server and change them accordingly
		 * is a thread since it may take a while
		 */
		HashMap<String, String> allClients = server.getAllUsers();
		ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
		String result = "Welcome " + name;
		boolean exist = false;
		// go through every existing client
		for (Map.Entry<String,String> client : allClients.entrySet()) {
			if (client.getKey().contentEquals(name)) {
				if (client.getValue().contentEquals(password)) {
					// if name and password match we write welcome back
					result = "Welcome back " + name;
					exist = true;
					// if there is already a user with same name and password online
					// we dont want this to work
					for (ServerClientThread activeClient : activeClients) {
						if (activeClient.getUsername().contentEquals(name)) { return "!exist"; }
					}
					break;
				}
				// password wrong or name does exist
				else { return null;	}
			}
		}
		// new client
		if (exist == false) {
			allClients.put(this.name, this.password);
			ReadAndWriteCsv.writeCsv(this.name,  this.password);
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
		return result;
	}
	
	public void createRoom(String name) {
		/*
		 * creates a new chatroom and sends a message to every user that 
		 * the room is now joinable
		 */
		// only if the room (name) doesnt already exist
		boolean exist = false;
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(name)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			// add room to the server
			// basically just creates a new chatroom and add it to the list
			server.addRoom(name);
		}
	}
	
	public void joinRoom(String roomname) {
		/*
		 * add the client to the list of clients of the room with name = name
		 */
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(roomname)) {
				room.sendAll(name + " just joined!");
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
	
	public void removeRoom(Chatroom room) {
		/*
		 * remove a client from a room
		 */
		// we need the assignement formerRoom to avoid null pointer exception
		Chatroom formerRoom = chatroom;
		// remove client from the list and set room attribute
		room.removeClient(this);
		// send leaving message and updated online list
		formerRoom.sendAll(name + " just left");
		sendOnline("r", formerRoom, null);
	}
	
	private void changeRoom(String newRoom) {
		/*
		 * just changes the room to the roomname name = newRoom
		 * Basically uses the functions remove and join Room
		 */
		removeRoom(chatroom);		
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
	
	public void removeUser() {
		/*
		 * remove a user from everything important: remove from chatroom, active users
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// remove from active client list:
	        	ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
	        	System.out.println(activeClients);
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
	      		removeRoom(chatroom);
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
	        }
	    });
	    t.start();
	}
	
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
	
	public void sendToAllR(String message, Chatroom room) {
		/*
		 * send a message to every client in the room or if room is null
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
				ArrayList<ServerClientThread> activeClients = chatroom.getClients();
				for (ServerClientThread user : activeClients) {
					user.sendMessage(nickname + ": " + message);
				}
			}
		});
		t.start();
	}
	
	private void checkName() throws IOException {
		/*
		 * calls login function and checks the given name
		 * 
		 */
		String result = login();
		try {
			// we need a bit of time before we can work with the result
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (result == null || result.contentEquals("!exist")) {
			// if password wrong or name does exist or user is online
			exit = true;
			return;
		}
		// welcome message
		else {
			out.println(result);
		}
	}
	
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
			checkName();
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
				else {
					if (line.length() > 9 && line.substring(0, 9).contentEquals("!nickname")) {
						// change nickname
						sendOnline("c", null, line);
					}
					else { 
						if (line.length() > 6 && line.substring(0, 6).contentEquals("!rooma")) {
							// create new room
							String newRoom = line.substring(6);
							System.out.println(newRoom);
							createRoom(newRoom);
							sendRooms("a", newRoom);
						}
						else {
							if (line.length() > 6 && line.substring(0, 6).contentEquals("!roomc")) {
								// change room
								changeRoom(line.substring(6));
							}
							else {
								// send to everybody in the room
								this.sendToAll(line);
							}
						}
						
					}
				}
			}
			this.close(false); 
		}
		catch (IOException e) {
			System.out.println(this.name + " was violently closed.");
			try {
				this.close(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void close(boolean joined) throws IOException {
		/*
		 * closes input stream and client and remove client instance
		 */
		if (joined) {
			removeUser();
		}
		exit = true;
		in.close();
		client.close();
		return;
	}
}

