package master;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerAcceptThread extends Thread implements Runnable {
	private final static int port = 1244;
	
	private boolean exit = false;
	private ServerSocket serverMain;
	
	private ArrayList<ServerClientThread> allClients = new ArrayList<>();
	private ArrayList<ServerClientThread> activeClientList = new ArrayList<>();
	
	public ServerAcceptThread(int port) throws IOException{
		this.serverMain = new ServerSocket(port);
	}
	
	public String login(ServerClientThread t) {
		String name = t.getUsername();
		String password = t.getUserpassword();
		String result = "Welcome " + name;
		boolean exist = false;
		for (ServerClientThread clientThread : allClients) {
			if (clientThread.getUsername().contentEquals(name)) {
				if (clientThread.getUserpassword() == password) {
					result = "Welcome back " + name;
					exist = true;
					activeClientList.add(t);
					break;
				}
				else {
					result = null;
					exist = true;
					break;
				}
			}
		}
		if (exist == false) {
			allClients.add(t);
			activeClientList.add(t);
		}
		sendUsers();
		return result;
	}
	
	public void removeUser(String name) {
		int index = 0;
		for (ServerClientThread user : activeClientList) {
			if (user.getUsername().contentEquals(name)) {
				activeClientList.remove(index);
				break;
			}
			index++;
		}
		sendUsers();
	}
	
	public void sendUsers() {
		String line = "!online";
		for (ServerClientThread user : activeClientList) {
			line = line + "," + user.getUsername() + " (" + user.getNickname() + ")";
		}
		for (ServerClientThread user : activeClientList) {
			user.sendMessage(line);
		}
	}
	
	public void sendToAll(String name, String message) {
		for (ServerClientThread user : activeClientList) {
			user.sendMessage(name + ": " + message);
		}
	}
	
	public void sendToAllLogin(String name) {
		for (ServerClientThread user : activeClientList) {
			user.sendMessage(name + " just logged in.");
		}
	}
	
	@Override
	public void run() {
		try {
			while (!exit) {
				System.out.println("connecting...");
				Socket client = serverMain.accept();
				System.out.println(client + " is now connected!");

				Thread userThread = new Thread(new ServerClientThread(this, client));
				userThread.start();
			}
		} catch (IOException e) { System.out.println("Fehler in run, ServerAccept Thread"); }
	}

	public static void main(String[] args) throws IOException {
		ServerAcceptThread acceptingServer = new ServerAcceptThread(port);
		Thread accept = new Thread(acceptingServer);
		accept.start();
	}
}
