package server;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ServerFrame extends JFrame implements ActionListener, MouseListener, KeyListener {
	JTabbedPane tabs;
	// log window
	JComboBox<String> chooseTask;
	JTextArea outputPanel;
	JTextField taskText;
	JPanel finalLog, rightSide, logPanel, statusPanel, informationPanel, taskPanel;
	JLabel logTitle, setName, setOnline, status;
	JButton showLog, taskButton, change;
	// chat window
	JTextArea chatWindow;
	JTextField input;
	DefaultListModel<String> listModelRooms,listModelClients;
	JList<String> rooms, clients;
	JPanel users;
	JButton send, displayAllUsers, displayBannedUsers;
		
	// popup menus
	final JPopupMenu pop = new JPopupMenu();
	final JPopupMenu popUser = new JPopupMenu();
	JMenuItem edit, delete, warning, ban, permaBan;
	// server attributes
	ServerFrame frame = this;
	private ServerStart server;
	private String selectedRoom;
	private String selectedUser;
	private String selectedBan;
	private int online;
	// status == true -> online
	private boolean running;
		
	public ServerFrame(ServerStart server) {
		/*
		 * constructor. Sets tabcomponent and size and stuff
		 */
		// in lists selected elements
		this.running = true;
		this.online = 1;
		this.selectedRoom = null;
		this.selectedUser = null;
		this.selectedBan = null;
		this.server = server;
		this.setTitle("A Java Server");
		this.setSize(1000, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// create the popup menus which show then clicked on list
		createRoomPopupMenu();
		createUserPopupMenu();
		// final Panels
		finalLog = new JPanel(new BorderLayout(20, 20));
		finalLog.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		users = new JPanel(new BorderLayout(20, 20));
		users.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		createLogPanel();
		createChatPanel();
		
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.add("Server", finalLog);
		tabs.add("Chats", users);
		this.add(tabs);
		this.setVisible(true);
	}
	
	// 1. PopUp menus
	private void createRoomPopupMenu() {
		/*
		 * if you click on a room you can choose to edit or remove the room
		 * see in the mouselisteners for more documentation
		 */
		edit = new JMenuItem("Edit");
        delete = new JMenuItem("Remove");
        edit.addMouseListener(this);
        delete.addMouseListener(this);
        pop.add(edit);
        pop.add(delete);
	}
	
	private void createUserPopupMenu() {
		/*
		 * if you click on a user you can choose to warn, ban or permaban
		 * see in the mouselisteners for more documentation
		 */
		warning = new JMenuItem("warn");
        ban = new JMenuItem("ban");
        permaBan = new JMenuItem("perma Ban");
        warning.addMouseListener(this);
        ban.addMouseListener(this);
        permaBan.addMouseListener(this);
        popUser.add(warning);
        popUser.add(ban);
        popUser.add(permaBan);
	}
	
	// 2. Panels (log and chat)
	private void createLogPanel() {
		// left side (log panel with label)
		logPanel = new JPanel(new BorderLayout(10, 10));
		createServerLogPanel();
		// create the rightpanel (status and task panels
		rightSide = new JPanel(new GridLayout(5, 1));
		// the status and information panel will be low on the right side
		// it will contain the status and the informations
		statusPanel = new JPanel(new BorderLayout());
		createStatusPanel();
		informationPanel = new JPanel(new BorderLayout());
		createInformationsPanel();
		// in the task panel we can change some settings
		taskPanel = new JPanel(new BorderLayout(10, 10));
		createTaskPanel();
		
		rightSide.add(taskPanel);
		rightSide.add(new JPanel());
		rightSide.add(statusPanel);
		rightSide.add(new JPanel());
		rightSide.add(informationPanel);
		
		finalLog.add(rightSide, BorderLayout.EAST);
		finalLog.add(logPanel, BorderLayout.CENTER);
	}
	
	private void createServerLogPanel() {
		/*
		 * creating the log panel: outputPanel, Title and Button
		 */
		// title of the logs
		logTitle = new JLabel("Server Log: ");
		logTitle.setFont(logTitle.getFont().deriveFont(14f));
		// create output Panel
		outputPanel = new JTextArea();
		outputPanel.setLineWrap(true);
		outputPanel.setEditable(false);
		outputPanel.setVisible(true);
		outputPanel.setFont(outputPanel.getFont().deriveFont(14f));
		// put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(outputPanel);
		// show entire log saved as csv
		showLog = new JButton("Show Log");
		showLog.addActionListener(this);
		
		logPanel.add(logTitle, BorderLayout.NORTH);
		logPanel.add(scroll, BorderLayout.CENTER);
		logPanel.add(showLog, BorderLayout.SOUTH);
	}
	
	private void createTaskPanel() {
		/*
		 * create task panel: dropdown menu, textfield and button
		 */
		JPanel northPanel = new JPanel(new BorderLayout(10, 10));
		JPanel southPanel = new JPanel(new BorderLayout(10, 10));
		// create dropdownMenu and title (north panel)
		JLabel taskLabel = new JLabel();
		taskLabel.setText("Possibilities: ");
		String comboBoxListe [] = {"change Servername" };
		chooseTask = new JComboBox<String>(comboBoxListe);
		// create south panel with textfield and button
		taskText = new JTextField(20);
		taskText.addKeyListener(this);
		taskButton = new JButton("OK");
		taskButton.addActionListener(this);
		
		northPanel.add(taskLabel, BorderLayout.WEST);
		northPanel.add(chooseTask, BorderLayout.EAST);
		southPanel.add(taskText, BorderLayout.WEST);
		southPanel.add(taskButton, BorderLayout.EAST);
		taskPanel.add(northPanel, BorderLayout.NORTH);
		taskPanel.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void createStatusPanel() {
		/*
		 * set Status of the server
		 */
		JPanel row = new JPanel(new GridLayout(1, 3));
		JLabel statusLabel = new JLabel("Serverstatus: ");
		status = new JLabel("Online");
		status.setFont(status.getFont().deriveFont(14f));
		change = new JButton("Change Status");
		change.addActionListener(this);
		
		statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
		row.add(statusLabel);
		row.add(status);
		row.add(change);
		
		statusPanel.add(row, BorderLayout.SOUTH);
	}
	
	private void createInformationsPanel() {
		/*
		 * here we display basic informations like ip adress, server name 
		 * and how many are online
		 */
		// get ip 
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		JPanel entireInfo = new JPanel(new GridLayout(4, 2));

		JLabel infoLabel = new JLabel("Informations: ");
		infoLabel.setFont(infoLabel.getFont().deriveFont(14f));
		// on the left side we display ip and numbers of online users
		JLabel idLabel = new JLabel("IP adress: ");
		JLabel setId = new JLabel(ip.getHostAddress().toString());
		JLabel onlineLabel = new JLabel("Online: ");
		setOnline = new JLabel(String.valueOf(this.online));		
		// on the right side we display the servername and if its editable
		JLabel nameLabel = new JLabel("Name: ");
		setName = new JLabel("A Java Server");
		
		entireInfo.add(infoLabel);
		entireInfo.add(nameLabel);
		entireInfo.add(setName);
		entireInfo.add(idLabel);
		entireInfo.add(setId);
		entireInfo.add(onlineLabel);
		entireInfo.add(setOnline);
		
		informationPanel.add(infoLabel, BorderLayout.NORTH);
		informationPanel.add(entireInfo, BorderLayout.CENTER);
	}
	
	private void createChatPanel() {
		/*
		 * The chat panel contains a left and a right side
		 * the left side consists of a label, a output panel, input text and button
		 * the right side consists of a tabed panel with multiple buttons
		 */
		// create left side
		JPanel leftSide = new JPanel(new BorderLayout(10, 10));
		JLabel leftSideLabel = new JLabel("Chat in the selected Room: ");
		chatWindow = new JTextArea();
		chatWindow.setLineWrap(true);
		chatWindow.setEditable(false);
		chatWindow.setVisible(true);
		chatWindow.setFont(chatWindow.getFont().deriveFont(14f));
		JScrollPane scroll = new JScrollPane(chatWindow);
		// input and text row
		JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
		input = new JTextField();
		input.addKeyListener(this);
		send = new JButton("Send");
		send.addActionListener(this);
		// finish creating the left panel
		rowPanel.add(input, BorderLayout.CENTER);
		rowPanel.add(send, BorderLayout.EAST);
		leftSide.add(leftSideLabel, BorderLayout.NORTH);
		leftSide.add(scroll, BorderLayout.CENTER);
		leftSide.add(rowPanel, BorderLayout.SOUTH);
		
		// create right side
		JPanel rightSide = new JPanel(new BorderLayout(10, 10));
		JLabel rightSideLabel = new JLabel("Rooms, Users and some Actions: ");
		// the JList displays all users online atm
		listModelClients = new DefaultListModel<String>();
		clients = new JList<String>(listModelClients);
		listModelClients.addElement("You (Admin) [-]");
		JScrollPane scrollClients = new JScrollPane(clients);
		clients.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// set selected item
            	selectedUser = clients.getSelectedValue();
            }
        });
		clients.addMouseListener(this);
		JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
		roomPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		listModelRooms = new DefaultListModel<String>();
		rooms = new JList<String>(listModelRooms);
		JScrollPane scrollRooms = new JScrollPane(rooms);
		// sets selected Room
		rooms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// if we click a different list entry we send a code to the server
            	selectedRoom = rooms.getSelectedValue();
            	listModelClients.setElementAt("You (Admin) [" + selectedRoom + "]", 0);
            	// send the selected room to the server
            	// this is important to display messages in the server
            	server.setFrameRoom(selectedRoom);
            }
        });
		rooms.addMouseListener(this);
		JButton roomButton = new JButton("create Room");
		roomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// dialog for creating a new Room
				String roomName = JOptionPane.showInputDialog(frame, "New Chatroom name: ", "create Chatroom",
						JOptionPane.QUESTION_MESSAGE);
				if (roomName == null || roomName.contentEquals("")) {}
				else {
					// if roomname does already exist dont create a new one
					boolean exist = false;
					ArrayList<Chatroom> rooms = server.getRooms();
					for (Chatroom room : rooms) {
						if (room.getName().contentEquals(roomName)) {
							exist = true;
							break;
						}
					}
					// else create a new Room
					if (!exist) {
						// add room to the server
						// basically just creates a new chatroom and add it to the list
						server.addRoom(roomName);
						server.writeLog("You just created a new Room named " + roomName + ".");
						server.sendRoomClients(roomName);
					}
				}
			}
		});
		
		roomPanel.add(scrollRooms, BorderLayout.CENTER);
		roomPanel.add(roomButton, BorderLayout.SOUTH);
				
		JTabbedPane roomTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		roomTab.add("Rooms", roomPanel);
		roomTab.add("Users", scrollClients);
		// Panel with tabs and buttons to ban and show all users
		JPanel display = new JPanel(new BorderLayout(5, 5));
		displayAllUsers = new JButton("All Users");
		displayAllUsers.addActionListener(this);
		displayBannedUsers = new JButton("Banned Users");
		displayBannedUsers.addActionListener(this);
		display.add(displayAllUsers, BorderLayout.WEST);
		display.add(displayBannedUsers, BorderLayout.EAST);
		
		rightSide.add(rightSideLabel, BorderLayout.NORTH);
		rightSide.add(roomTab, BorderLayout.CENTER);
		rightSide.add(display, BorderLayout.SOUTH);
		
		users.add(leftSide, BorderLayout.CENTER);
		users.add(rightSide, BorderLayout.EAST);
	}
	
	// 3. minor Activities
	public void displayMessage(String message) {
		/*
		 * if we joined a room all activities will be written out into the chatWindow
		 */
		chatWindow.append(message + "\n");
	}

	public void writeLog(String newLog) {
		/*
		 * write the log and the time out. Saves the log in a csv file
		 */
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String t = time.format(formatter);
		String message = "[" + t + "] - " + newLog;
		outputPanel.append(message + "\n");
		ReadAndWriteCsv.writeLog(message);
	}
	
	public void displayRoom(String name) {
		/*
		 * add a new Room to the roomlist
		 */
		listModelRooms.addElement(name);
	}
	
	public void addClient(String message) {
		/*
		 * adds a new client to the client list
		 * message allready contains name and roomname
		 */
		listModelClients.addElement(message);
	}
	
	public void removeClient(String message) {
		/*
		 * removes a client
		 * message is containing entire string with room and stuff
		 */
		listModelClients.removeElement(message);
	}
	
	public void changeClient(String old, String message) {
		/*
		 * change old entry for a client to a new one (change room, nickname, ...)
		 */
		int index = listModelClients.indexOf(old);
		listModelClients.setElementAt(message, index);
	}
	
	public void changeOnline(String action) {
		/*
		 * changes the number of online users according to action
		 */
		if (action == "n") {
			// new user
			this.online += 1;
		}
		else {
			// remove user
			this.online -= 1;
		}
		setOnline.setText(String.valueOf(this.online));
	}
	
	public String returnTitle() {
		/*
		 * set name for new client frame
		 */ 
		return "!title" + frame.getTitle();
	}
	
	// 4. Dialogs
	private void displayAllUsers() {
		/*
		 * if a button is pressed we display all Users who loged in at a time
		 * We use a dialog with a list
		 */
		// read csv file with all users
		HashMap<String, String> list = ReadAndWriteCsv.readCsv(0);
		DefaultListModel<String> listModelAllUsers = new DefaultListModel<String>();
		JList<String> allUsers = new JList<String>(listModelAllUsers);
		for (Map.Entry<String,String> element : list.entrySet()) {
			listModelAllUsers.addElement(element.getKey());
		}
		// create a dialog Box 
        JDialog dialog = new JDialog(this, "All Names"); 
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        dialogPanel.add(allUsers, BorderLayout.CENTER);
        
        JButton okay = new JButton("Okay");
        okay.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		dialog.dispose();
        	}
        });
        dialogPanel.add(okay, BorderLayout.SOUTH);
        
        dialog.add(dialogPanel);
        dialog.setSize(40 * listModelAllUsers.getSize() + 300, 300);
        dialog.setVisible(true);
	}
	
	private void displayAllBannedUsers() {
		/*
		 * display csv file with all banned users.
		 * You can unban by clicking on the 'unban' button
		 */
		HashMap<String, String> list = ReadAndWriteCsv.readCsv(1);
		DefaultListModel<String> listModelAllUsers = new DefaultListModel<String>();
		JList<String> allUsers = new JList<String>(listModelAllUsers);
		for (Map.Entry<String,String> element : list.entrySet()) {
			listModelAllUsers.addElement(element.getKey());
		}
		allUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// if we click a different list entry we send a code to the server
            	selectedBan = allUsers.getSelectedValue();
            }
        });
		// create a dialog Box 
        JDialog dialog = new JDialog(this, "All Banned Names");
        JPanel listPanel = new JPanel(new BorderLayout());
        JPanel dialogPanel = new JPanel(new BorderLayout());
        listPanel.add(allUsers, BorderLayout.CENTER);
        JButton unban = new JButton("unban User");
        unban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// overwrite entry in csv file so the user is not banned anymore
				ReadAndWriteCsv.unbanUser(selectedBan);
			}
        });
        listPanel.add(unban, BorderLayout.SOUTH);
        listPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        dialogPanel.add(listPanel, BorderLayout.CENTER);
        
        JButton okay = new JButton("Okay");
        okay.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) { dialog.dispose(); } });
        dialogPanel.add(okay, BorderLayout.SOUTH);
        
        dialog.add(dialogPanel);
        dialog.setSize(40 * listModelAllUsers.getSize() + 300, 300);
        dialog.setVisible(true);
	}
	
	private void displayLog() {
		/*
		 * displays the csv log file
		 */
		ArrayList<String> log = ReadAndWriteCsv.readLog();
		JTextArea outputPanel = new JTextArea(14, 20);
		outputPanel.setLineWrap(true);
		outputPanel.setEditable(false);
		JScrollPane scroll = new JScrollPane(outputPanel);
		for (String l : log) { outputPanel.append(l + "\n"); }
		// create a dialog Box 
        JDialog dialog = new JDialog(this, "Complete Log");
        JPanel dialogPanel = new JPanel(new BorderLayout());
        
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        dialogPanel.add(scroll, BorderLayout.CENTER);
        
        JButton okay = new JButton("Okay");
        okay.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) { dialog.dispose(); } });
        dialogPanel.add(okay, BorderLayout.SOUTH);
        
        dialog.add(dialogPanel);
        dialog.setSize(600, 500);
        dialog.setVisible(true);
	}
	
	// 5. Action Listeners
	@Override
	public void actionPerformed(ActionEvent e) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (e.getSource() == displayAllUsers) {
					displayAllUsers();
				}
				if (e.getSource() == displayBannedUsers) {
					displayAllBannedUsers();
				}
				if (e.getSource() == showLog) {
					displayLog();
				}
				if (e.getSource() == taskButton) {
					// if we click on the task button the outcome depends on the selection
					int index = chooseTask.getSelectedIndex();
					String line = taskText.getText();
					taskText.setText("");
					if (index == 0) {
						setName.setText(line);
						frame.setTitle(line);
						server.writeLog("The Servername has been changed to " + line);
						server.sendToAll("!title" + line);
					}
				}
				if (e.getSource() == send) {
					// send message to room there we are
					if (selectedRoom != null) {
						String line = "Admin: " + input.getText();
						input.setText("");
						// get Room
						ArrayList<Chatroom> chatroomsServer = server.getRooms();
						for (Chatroom room : chatroomsServer) {
							if (room.getName().contentEquals(selectedRoom)) {
								ArrayList<ServerClientThread> activeClients = room.getClients();
								for (ServerClientThread user : activeClients) {
									user.sendMessage(line);
								}
								chatWindow.append(line + "\n");
								break;
							}
						}
					}
				}
				if (e.getSource() == change) {
					if (running) {
						running = false;
						ArrayList<ServerClientThread> clients = server.getAllActiveUsers();
						for (ServerClientThread client : clients) {
				    		try { client.close(true); } 
				    		catch (IOException e1) { e1.printStackTrace(); }
						}
			    		status.setText("OFFLINE");
						server.waitUntil();
					}
					else {
						running = true;
						server.waitUntil();
						status.setText("ONLINE");
					}
				}
			}
		}); t.start();
	}
	
	// 6. Keylisteners
	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * add key listeners to both textfields 
		 * (does the same as the action listener if enter is pressed)
		 */
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (e.getSource() == taskText) {
				int index = chooseTask.getSelectedIndex();
				String line = taskText.getText();
				taskText.setText("");
				if (index == 0) {
					setName.setText(line);
					this.setTitle(line);
					server.writeLog("The Servername has been changed to " + line);
				}
			}
			if (e.getSource() == input) {
				if (selectedRoom != null) {
					String line = "Admin: " + input.getText();
					input.setText("");
					// get Room
					ArrayList<Chatroom> chatroomsServer = server.getRooms();
					for (Chatroom room : chatroomsServer) {
						if (room.getName().contentEquals(selectedRoom)) {
							ArrayList<ServerClientThread> activeClients = room.getClients();
							for (ServerClientThread user : activeClients) {
								user.sendMessage(line);
							}
							chatWindow.append(line + "\n");
							break;
						}
					}
				}
			}
		}
	}
	
	// 7. MouseListeners
	@Override
	public void mousePressed(MouseEvent e) {
		/*
		 * works as a thread since a task could last longer
		 * basically handels clickevent on the popup menus
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (e.getSource() == edit) {
					if (selectedRoom.contentEquals("main")) {}
	        		else {
	        			String roomName = JOptionPane.showInputDialog(frame, "New Chatroom name: ", "change Name",
	    						JOptionPane.QUESTION_MESSAGE);
	    				if (roomName == null || roomName.contentEquals("")) {}
	    				else {
	    					boolean exists = false;
		    				// check if new name is already given
		    				for (int i = 0; i < listModelRooms.getSize(); i++) {
		    					if (listModelRooms.get(i).contentEquals(roomName)) {
		    						exists = true;
		    						break;
		    					}
		    				}
		    				if (!exists) {
		    					server.changeRoomname(selectedRoom, roomName);
		    					int index = listModelRooms.indexOf(selectedRoom);
		    					listModelRooms.setElementAt(roomName, index);
		    				}
	    				}
	        		}
				}
				if (e.getSource() == delete) {
					if (selectedRoom.contentEquals("main")) {}
	        		else {
	        			server.deleteRoom(selectedRoom);
						listModelRooms.removeElement(selectedRoom);
	        		}
				}
				if (e.getSource() == warning) {
					int index = selectedUser.indexOf("(");
	        		ServerClientThread client = server.getUser(selectedUser.substring(0, index - 1));
	        		if (client != null) { client.sendMessage("!warning"); }
				}
				if (e.getSource() == ban) {
					int index = selectedUser.indexOf("(");
		    		ServerClientThread client = server.getUser(selectedUser.substring(0, index - 1));
		    		try { client.close(true); } 
		    		catch (IOException e1) { e1.printStackTrace(); }
				}
				if (e.getSource() == permaBan) {
					int index = selectedUser.indexOf("(");
		    		ServerClientThread client = server.getUser(selectedUser.substring(0, index - 1));
		    		server.writeLog(client.getUsername() + " has been permanently banned.");
		    		ReadAndWriteCsv.writeCsv(client.getUsername(), client.getPassword(), 1);
		    		try { client.close(true); } 
		    		catch (IOException e1) { e1.printStackTrace(); }
				}
			}
		});
		t.start();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			if (e.getSource() == rooms) {
				if (selectedRoom == null || selectedRoom == listModelRooms.firstElement()) {}
		    	else { pop.show(rooms, e.getX(), e.getY()); }
			}
			if (e.getSource() == clients) {
            	if (selectedUser == null || selectedUser == listModelClients.firstElement()) {}
            	else { popUser.show(clients, e.getX(), e.getY()); }
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
