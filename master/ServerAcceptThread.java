package master;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerAcceptThread extends Thread implements Runnable {
	private final static int port = 1244;
	
	private boolean exit = false;
	private ServerSocket serverMain;
	
	private ArrayList<ServerClientThread> allClients;
	private ArrayList<ServerClientThread> activeClientList;
	
	public ServerAcceptThread(int port) throws IOException{
		this.serverMain = new ServerSocket(port);
		this.allClients = new ArrayList<ServerClientThread>();
		this.activeClientList = new ArrayList<ServerClientThread>();
	}
	
	public ArrayList<ServerClientThread> getAllUsers() {
		return allClients;
	}
	
	public ArrayList<ServerClientThread> getAllActiveUsers() {
		return activeClientList;
	}
	
	public void setAllUsers(ArrayList<ServerClientThread> allNewClients) {
		this.allClients = allNewClients;
	}
	
	public void setAllActiveUsers(ArrayList<ServerClientThread> activeClients) {
		this.activeClientList = activeClients;
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
