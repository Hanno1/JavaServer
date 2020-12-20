package server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadAndWriteCsv {
	private static String filepathUserList = "C:\\Users\\hanno\\OneDrive\\Desktop\\Informatik\\Semester VII"
	+ "\\fortgeschrittenes Programmierpraktikum\\git\\src\\serverFiles\\userList.csv";
	
	private static String filepathBannedUser = "C:\\Users\\hanno\\OneDrive\\Desktop\\Informatik\\Semester VII"
	+ "\\fortgeschrittenes Programmierpraktikum\\git\\src\\serverFiles\\bannedUsers.csv";
	
	private static String filepathLog = "C:\\Users\\hanno\\OneDrive\\Desktop\\Informatik\\Semester VII"
			+ "\\fortgeschrittenes Programmierpraktikum\\git\\src\\serverFiles\\serverLog.csv";
	
	// 1. Writing
	public static void writeCsv(String name, String password, int action) {
		/*
		 * here we open and write in a file which is depending on action
		 */
		FileWriter filewriter = null;
		String filepath = filepathUserList;;
		if (action  == 1) { filepath = filepathBannedUser; }
		try {
			filewriter = new FileWriter(filepath, true);
			filewriter.append(name);
			filewriter.append(",");
			filewriter.append(password);
			filewriter.append("\n");
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try {
				filewriter.flush();
				filewriter.close();
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	public static void writeLog(String line) {
		/*
		 * write a new line to the log file
		 */
		FileWriter filewriter = null;
		String filepath = filepathLog;
		try {
			filewriter = new FileWriter(filepath, true);
			filewriter.append(line);
			filewriter.append("\n");
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try {
				filewriter.flush();
				filewriter.close();
			} 
			catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	// 2. Reading
	public static HashMap<String, String> readCsv(int action) {
		/*
		 * read a csv. If action is 1 we read the banned user csv. else the normal userlist
		 */
		String filepath = filepathUserList;
		if (action  == 1) { filepath = filepathBannedUser; }
		BufferedReader reader = null;
		HashMap<String,String> users = new HashMap<>();
		try {
			String line = "";
			reader = new BufferedReader(new FileReader(filepath));
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields.length > 0) {
					users.put(fields[0], fields[1]);
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try { reader.close(); }
			catch (Exception e) { e.printStackTrace(); }
		}
		return users;
	}
	
	public static ArrayList<String> readLog() {
		/*
		 * read the log and return it as a string
		 */
		String filepath = filepathLog;
		BufferedReader reader = null;
		ArrayList<String> log = new ArrayList<String>();
		try {
			String line = "";
			reader = new BufferedReader(new FileReader(filepath));
			while ((line = reader.readLine()) != null) { log.add(line); }
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try { reader.close(); }
			catch (Exception e) { e.printStackTrace(); }
		}
		return log;
	}
	
	// 3. interesting Functions
	public static void unbanUser(String name) {
		/*
		 * unbans the user. this empties the entire file and then writes it again skipping
		 * the given String name
		 */
		String filepath = filepathBannedUser;
		BufferedReader reader = null;
		HashMap<String,String> users = new HashMap<>();
		try {
			String line = "";
			reader = new BufferedReader(new FileReader(filepath));
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields[0].contentEquals(name)) {}
				else {
					if (fields.length > 0) { users.put(fields[0], fields[1]); }
				}
			}
			PrintWriter writer = new PrintWriter(filepath);
			writer.print("");
			writer.close();
			for (Map.Entry<String,String> user : users.entrySet()) { writeCsv(user.getKey(), user.getValue(), 1); }
		}
		catch (Exception e) { e.printStackTrace(); }
		finally {
			try { reader.close(); }
			catch (Exception e) { e.printStackTrace(); }
		}
	}
}
