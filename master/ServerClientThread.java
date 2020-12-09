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
		
		sendOnline("all", null);
		sendOnline("a", null);
		
		return result;
	}
	
	public void removeUser() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
	        	ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
	        	int index = 0;
      			System.out.println(activeClients);

	      		for (ServerClientThread user : activeClients) {
	      			if (user.getUsername().contentEquals(name)) {
	      				activeClients.remove(index);
	      				break;
	      			}
	      			index++;
	      		}
	      		sendOnline("r", null);
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
	
	public void sendOnline(String action, String line) {
		/*
		 * if action == remove: send !onlineruser so we can just removee the user
		 * else send !onlineauser to add and !onlinecuser to change nickname
		 * if action == all, we need to send all users (for new user)
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (action.contentEquals("all")) {
					ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
					String newLine = "!onlinee";
					for (ServerClientThread client : activeClients) {
						newLine = newLine + "," + client.getUsername() + " (" + client.getNickname() + ")";
					}
					sendMessage(newLine);
				}
				else {
					if (action.contentEquals("a") || action.contentEquals("r")) {
						ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
						String newLine = "!online" + action + name + " (" + nickname + ")";
						// dont send message to myself or i get a problem
						for (ServerClientThread user : activeClients) {
							if (!user.getUsername().contentEquals(name)) {
								user.sendMessage(newLine);
							}
						}
					}
					else {
						ArrayList<ServerClientThread> activeClients = server.getAllActiveUsers();
						String newLine = "!onlinec" + name + " (" + line.substring(9) + ")" +
											"%" + name + " (" + nickname + ")";
						nickname = line.substring(9);
						for (ServerClientThread user : activeClients) {
							user.sendMessage(newLine);
						}
					}
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
				if (line == null) { removeUser(); break; }
				else {
					if (line.length() > 9 && line.substring(0, 9).contentEquals("!nickname")) {
						sendOnline("c", line);
					}
					else { this.sendToAll(nickname, line); }
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

