package master;

import java.io.*;
import java.net.Socket;

public class ClientStart extends Thread {
	/*
	 * Starting and Connecting client to the server
	 * constantly search for server input
	 */
	public static void main(String[] args) {
		// server port
		int port = 1244;
		try {
			// new Spcket Instance
			Socket client = new Socket("localhost", port);
			// Output to the Server
			PrintWriter printerOut = new PrintWriter(client.getOutputStream(), true);
			// Input to the Client / System from the server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			// creates an frame and sets visibility to false
			ClientFrame frame = new ClientFrame(printerOut);
			// shows login screen and after succesfully login show frame
			new ClientLogin(printerOut, frame);
			
			// checking the server Input and send it to the frame
			String serverInput;
			while ((serverInput = in.readLine()) != null) {
				if (serverInput.contentEquals("!close")) { frame.writeOut(serverInput); break; }
				frame.writeOut(serverInput);
			}
			// if serverInput is null the connection was killed
			System.out.println("The connection to the server was shut down!");
			// close client and destroy frame
			client.close();
			frame.dispose();
		}
		catch (IOException e) {
			// if we cant connect to the server
			System.out.println("Server is not online!");
			System.exit(0);
		}
	}
}
