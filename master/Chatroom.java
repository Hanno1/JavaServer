package master;

import java.util.ArrayList;

public class Chatroom {
	private String name;
	private ServerAcceptThread server;
	
	private ArrayList<ServerClientThread> clients;
	
	public Chatroom(ServerAcceptThread server, String name) {
		this.name = name;
		this.server = server;
		this.clients = new ArrayList<>();
	}
	
	public String getName() { return name; }
	
	public ArrayList<ServerClientThread> getClients() { return clients; }
	
	public void sendAll(String message) {
		for (ServerClientThread client : clients) {
			client.sendMessage(message);
		}
	}
	
	public void joinClient(ServerClientThread client) {
		clients.add(client);
		client.setRoom(this);
	}
	
	public void removeClient(ServerClientThread client) {
		clients.remove(client);
		client.setRoom(null);
	}
}
