package master;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerClientThread extends Thread implements Runnable {
	private boolean exit = false;
	
	private ServerAcceptThread server;
	private Socket client;
	private String name;
	private String password;
	private String nickname;
	private PrintWriter out;
	private BufferedReader in;
	private Chatroom chatroom;
	
	public ServerClientThread(ServerAcceptThread server, Socket client) throws IOException {
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
		ArrayList<ServerClientThread> allClients = server.getAllUsers();
		ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
		String result = "Welcome " + name;
		boolean exist = false;
				
		for (ServerClientThread clientThread : allClients) {
			if (clientThread.getUsername().contentEquals(name)) {
				if (clientThread.getUserpassword() == password) {
					result = "Welcome back " + name;
					exist = true;
					break;
				}
				else { return null;	}
			}
		}
		if (exist == false) {
			allClients.add(this);
		}
		
		sendToAllR(name + " just logged in", null);
		joinRoom("main");
		sendRooms("all", null);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		activeClients.add(this);
		
		server.setAllUsers(allClients);
		server.setAllActiveUsers(activeClients);		
		return result;
	}
	
	public void createRoom(String name) {
		boolean exist = false;
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(name)) {
				exist = true;
				break;
			}
		}
		if (!exist) {
			server.addRoom(name);
		}
	}
	
	public void joinRoom(String name) {
		ArrayList<Chatroom> rooms = server.getRooms();
		for (Chatroom room : rooms) {
			if (room.getName().contentEquals(name)) {
				room.joinClient(this);
				sendOnline("a", null, null);
				sendOnline("all", null, null);
				break;
			}
		}
	}
	
	public void removeRoom(Chatroom room) {
		Chatroom formerRoom = chatroom;
		room.removeClient(this);
		formerRoom.sendAll(name + " just left");
		sendOnline("r", formerRoom, null);
	}
	
	private void changeRoom(String newRoom) {
		removeRoom(chatroom);		
		joinRoom(newRoom);
	}
	
	public void sendRooms(String action, String newRoom) {
		if (action.contentEquals("all")) {
			// send all rooms
			String line = "!roome";
			ArrayList<Chatroom> rooms = server.getRooms();
			for (Chatroom room : rooms) {
				line = line + "," + room.getName();
			}
			out.println(line);
		}
		else {
			// send all Rooms
			if (action.contentEquals("a")) {
				String line = "!rooma" + newRoom;
				ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
				for (ServerClientThread client : activeClients) {
					client.out.println(line);
				}
			}
		}
	}
	
	public void removeUser() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
	        	ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
	        	int index = 0;
	      		for (ServerClientThread user : activeClients) {
	      			if (user.getUsername().contentEquals(name)) {
	      				activeClients.remove(index);
	      				break;
	      			}
	      			index++;
	      		}
	      		// remove User
	      		Chatroom formerRoom = chatroom;
	      		removeRoom(chatroom);
	      		sendOnline("r", formerRoom, null);
	      		// update every online list
	      		server.setAllActiveUsers(activeClients);
	      		try {
	    			Thread.sleep(300);
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}	      		
	      		sendToAllR(name + " just logged Out", formerRoom);
	        }
	    });
	    t.start();
	}
	
	public void sendOnline(String action, Chatroom room, String line) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (action.contentEquals("r")) {
					ArrayList<ServerClientThread> activeClients = room.getClients();
					String newLine = "!onliner" + name + " (" + nickname + ")";
					// dont send message to myself or i get double names
					for (ServerClientThread user : activeClients) {
						user.sendMessage(newLine);
					}
				}
				else {
					ArrayList<ServerClientThread> activeClients = chatroom.getClients();
					if (action.contentEquals("all")) {
						String newLine = "!onlinee";
						for (ServerClientThread client : activeClients) {
							newLine = newLine + "," + client.getUsername() + " (" + client.getNickname() + ")";
						}
						sendMessage(newLine);
					}
					else {
						if (action.contentEquals("a")) {
							String newLine = "!onlinea" + name + " (" + nickname + ")";
							// dont send message to myself or i get double names
							for (ServerClientThread user : activeClients) {
								if (!user.getUsername().contentEquals(name)) {
									user.sendMessage(newLine);
								}
							}
						}
						else {
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
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (room != null) {
					ArrayList<ServerClientThread> activeClients = room.getClients();
					for (ServerClientThread user : activeClients) {
						user.sendMessage(message);
					}
				}
				else {
					ArrayList<ServerClientThread> activeClients = chatroom.getClients();
					for (ServerClientThread user : activeClients) {
						user.sendMessage(message);
					}
				}
			}
		});
		t.start();
	}
	
	public void sendToAll(String name, String message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<ServerClientThread> activeClients = chatroom.getClients();
				for (ServerClientThread user : activeClients) {
					user.sendMessage(name + ": " + message);
				}
			}
		});
		t.start();
	}
	
	private void checkName() throws IOException {
		Thread t = new Thread(new Runnable() {
			@Override
	        public void run() {
				String result = login();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (result == null) {
					out.println("Username does allready exist or incorrect Password!");
					out.println("!close");
				}
				else { out.println(result); }
	        }
	    });
	    t.start();
	}
	
	@Override
	public void run() {
		try {
			this.name = in.readLine();
			this.nickname = name;
			String pass = in.readLine();
			this.password = pass;
			checkName();
			
			String line;
			while (!exit) {
				line = in.readLine();
				if (line == null) { removeUser(); break; }
				else {
					if (line.length() > 9 && line.substring(0, 9).contentEquals("!nickname")) {
						sendOnline("c", null, line);
					}
					else { 
						if (line.length() > 6 && line.substring(0, 6).contentEquals("!rooma")) {
							String newRoom = line.substring(6);
							System.out.println(newRoom);
							createRoom(newRoom);
							sendRooms("a", newRoom);
						}
						else {
							if (line.length() > 6 && line.substring(0, 6).contentEquals("!roomc")) {
								changeRoom(line.substring(6));
							}
							else {
								this.sendToAll(nickname, line);
							}
						}
						
					}
				}
			}
			this.close(); 
		}
		catch (IOException e) {
			System.out.println(this.name + " was violently closed.");
			try {
				this.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void close() throws IOException {
		removeUser();
		exit = true;
		in.close();
		client.close();
		return;
	}
}

