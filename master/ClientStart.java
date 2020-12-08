package master;

import java.io.*;
import java.net.Socket;

public class ClientStart extends Thread {
	public static void main(String[] args) {
		int port = 1244;
		try {
			Socket client = new Socket("localhost", port);

			PrintWriter printerOut = new PrintWriter(client.getOutputStream(), true); // Output to the Server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())); // Input to the Client / System
			
			ClientFrame frame = new ClientFrame(printerOut);
			new ClientLogin(printerOut, frame);
			
			String serverInput; // Here we check the userInput and send it to the server
			while ((serverInput = in.readLine()) != null) {
				if (serverInput.contentEquals("!close")) { frame.writeOut(serverInput); break; }
				frame.writeOut(serverInput);
			}
			System.out.println("The connection to the server was shut down!");
			client.close();
			frame.dispose();
		}
		catch (IOException e) {
			System.out.println("Server is not online!");
			System.exit(0);
		}
	}
}
