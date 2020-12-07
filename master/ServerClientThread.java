package master;

import java.io.*;
import java.net.*;

public class ServerClientThread extends Thread implements Runnable {
	private boolean exit = false;
	
	private ServerAcceptThread server;
	public Socket client;
	private String name;
	private String password;
	private String nickname;
	public BufferedReader in;
	public PrintWriter out;
	
	public ServerClientThread(ServerAcceptThread server, Socket client) throws IOException {
		this.server = server;
		this.client = client;
		this.name = "";
		this.nickname = "";
		this.password = "";
		this.out = new PrintWriter(client.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
	}
	
	public String getUsername() {
		return this.name;
	}
	
	public String getNickname() {
		return this.nickname;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getUserpassword() {
		return this.password;
	}
	
	public void sendMessage(String message) {
		out.println(message);
	}
	
	private void checkName() throws IOException {
		String result = server.login(this);
		if (result == null) {
			out.println("Username does allready exist or incorrect Password!");
			this.close(); 
		}
		else { 
			out.println(result);
			server.sendToAllLogin(this.name);
		}
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
				if (line == null) { server.removeUser(this.name); break; }
				else { server.sendToAll(nickname, line); }
			}
			this.close(); 
		}
		catch (IOException e) {
			System.out.println(this.name + " was violently closed.");
			try {
				server.removeUser(this.name);
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

