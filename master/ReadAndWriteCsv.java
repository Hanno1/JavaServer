package master;

import java.io.*;
import java.util.HashMap;

public class ReadAndWriteCsv {
	private static String filepath = "C:\\Users\\hanno\\OneDrive\\Desktop\\Informatik\\Semester VII"
			+ "\\fortgeschrittenes Programmierpraktikum\\git\\src\\master\\userList.csv";
	
	public static void writeCsv(String name, String password) {
		FileWriter filewriter = null;
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
	
	public static HashMap<String, String> readCsv() {
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
}
