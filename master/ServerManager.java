package master;

public class ServerManager {
	private ArrayList<ServerClientThread> allClients = new ArrayList<>();
	private ArrayList<ServerClientThread> activeClientList = new ArrayList<>();
	
	public ServerManager() {
	}
	
	public String login(ServerClientThread t) {
		String name = t.getUsername();
		String password = t.getUserpassword();
		String result = "Welcome " + name;
		boolean exist = false;
		for (ServerClientThread clientThread : allClients) {
			if (clientThread.getUsername().contentEquals(name)) {
				if (clientThread.getUserpassword() == password) {
					sendToAllLogin(name);
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
			sendToAllLogin(name);
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
			user.out.println(name + ": " + message);
//			user.sendMessage(name + ": " + message);
		}
	}
	
	public void sendToAllLogin(String name) {
		for (ServerClientThread user : activeClientList) {
			user.out.println(name + " just logged in");
//			user.sendMessage(name + " just logged in.");
		}
	}
}
