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
	
	public ServerClientThread(ServerAcceptThread server, Socket client) throws IOException {
		this.server = server;
		this.client = client;
		this.name = "";
		this.nickname = "";
		this.password = "";
		this.out = new PrintWriter(client.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	}
	
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
		sendToAll(name + " just logged in");
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		activeClients.add(this);
		
		server.setAllUsers(allClients);
		server.setAllActiveUsers(activeClients);
		
		sendOnline();
		
		return result;
	}
	
	public void removeUser(String name) {
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
	      		sendOnline();
	      		server.setAllActiveUsers(activeClients);
	      		try {
	    			Thread.sleep(300);
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}	      		
	      		sendToAll(name + " just logged Out");
	        }
	    });
	    t.start();
	}
	
	public void sendOnline() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
				String line = "!online";
				for (ServerClientThread user : activeClients) {
					line = line + "," + user.getUsername() + " (" + user.getNickname() + ")";
				}
				for (ServerClientThread user : activeClients) {
					user.sendMessage(line);
				}
			}
		});
		t.start();
	}
	
	public void sendToAll(String message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
				for (ServerClientThread user : activeClients) {
					user.sendMessage(message);
				}
			}
		});
		t.start();
	}
	
	public void sendToAll(String name, String message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
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
				if (line == null) { removeUser(this.name); break; }
				else {
					if (line.length() > 9 && line.substring(0, 9).contentEquals("!nickname")) {
						nickname = line.substring(9);
						sendOnline();
					}
					else { this.sendToAll(nickname, line); }
				}
			}
			this.close(); 
		}
		catch (IOException e) {
			System.out.println(this.name + " was violently closed.");
			try {
				removeUser(this.name);
				this.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void close() throws IOException {
		exit = true;
		in.close();
		client.close();
		return;
	}
}

