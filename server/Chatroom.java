package server;

import java.util.ArrayList;

public class Chatroom {
	/*
	 * class with chatroom
	 * a chatroom has different properties
	 * a list with all clients and a String 'name'
	 */
	private String name;
	private String password;
	boolean privateRoom = false;
	
	private ArrayList<ServerClientThread> clients;
	
	public Chatroom(String name) {
		/*
		 * Constructor for a Chatroom, sets name and initialise the client list
		 */
		this.name = name;
		this.clients = new ArrayList<>();
		this.password = null;
	}
	
	public Chatroom(String name, String password) {
		/*
		 * second constructor for chatrooms with a password needed
		 */
		this.name = name;
		this.clients = new ArrayList<>();
		this.password = password;
		this.privateRoom = true;
	}
	
	public String getName() { return name; }
	
	public void setName(String newName) { this.name = newName; }
	
	public ArrayList<ServerClientThread> getClients() { return clients; }
	
	public String getPassword() { return this.password; }
	
	public void sendAll(String message) {
		/*
		 *  send a message to every client in the room
		 */
		for (ServerClientThread client : clients) {
			client.sendMessage(message);
		}
	}
	
	public void joinClient(ServerClientThread client) {
		/*
		 * add client to the room and set the room attribute of client
		 */
		clients.add(client);
		client.setRoom(this);
	}
	
	public void removeClient(ServerClientThread client) {
		/*
		 * remove a client and set the room attribute to null
		 */
		clients.remove(client);
		client.setRoom(null);
	}
}
