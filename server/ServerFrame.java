package server;

import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class ServerFrame extends JFrame implements ActionListener, MouseListener, KeyListener {
	JTabbedPane tabs, roomTab;
	// log window
	JComboBox<String> chooseTask;
	JTextArea outputPanel;
	JTextField taskText;
	JPanel finalLog, rightSide, logPanel, statusPanel, informationPanel, entireInfo;
	// right panel things
	JPanel taskPanel, northPanel, southPanel, row, emptyPanel;
	JLabel logTitle, setName, setOnline, status, taskLabel, statusLabel, infoLabel;
	JLabel idLabel, setId, onlineLabel, nameLabel;
	JButton showLog, taskButton, change;
	// chat window
	JTextArea chatWindow;
	JTextField input;
	DefaultListModel<String> listModelRooms,listModelClients;
	JList<String> rooms, clients;
	JPanel users, leftSide, rowPanel, roomPanel, rightSideChat, display;
	JLabel leftSideLabel, rightSideLabel;
	JButton send, displayAllUsers, displayBannedUsers, roomButton;
	JList<String> privateRooms;
	DefaultListModel<String> privateRoomModel;
	
	// menuBar
	JMenuBar menubar;
	JMenu colorTheme;
	JCheckBoxMenuItem light, blue, dark;
		
	// popup menus
	final JPopupMenu pop = new JPopupMenu();
	final JPopupMenu popUser = new JPopupMenu();
	final JPopupMenu popPrivate = new JPopupMenu();
	JMenuItem join, edit, delete, warning, ban, permaBan, close;
	// server attributes
	ServerFrame frame = this;
	private ServerStart server;
	private String selectedRoom;
	private String selectedUser;
	private String selectedBan;
	private String selectedPrivate;
	private int online;
	// status == true -> online
	private boolean running;
	
	// colors for color scheme
	Color[] lightScheme = {new Color(0xffffff), new Color(0xcffffff), 
			new Color(0xffffff), new Color(0xc5e1a5), new Color(0x000000)};
	Color[] blueScheme = {new Color(0xbbc4f2), new Color(0xbdc6d4), 
			new Color(0xdfe2ef), new Color(0xc8cff2), new Color(0x000000)};
	Color[] darkScheme = {new Color(0x102027), new Color(0x62727b), 
			new Color(0x37474f), new Color(0x00695c), new Color(0xffffff)};
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	
	Font output = new Font("Arial", Font.BOLD + Font.ITALIC, 20);
		
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
		this.selectedPrivate = null;
		this.server = server;
		this.setTitle("A Java Server");
		this.setSize(1000, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// create the popup menus which show then clicked on list
		createMenuBar();
		createRoomPopupMenu();
		createUserPopupMenu();
		createPrivatePopupMenu();
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
		this.setColor(darkScheme);
	}
	
	// 1. PopUp menus
	private void createRoomPopupMenu() {
		/*
		 * if you click on a room you can choose to edit or remove the room
		 * see in the mouselisteners for more documentation
		 */
		join = new JMenuItem("Join");
		edit = new JMenuItem("Edit");
        delete = new JMenuItem("Remove");
        edit.addMouseListener(this);
        delete.addMouseListener(this);
        pop.add(join);
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
	
	private void createPrivatePopupMenu() {
		close = new JMenuItem("close");
		close.addMouseListener(this);
		popPrivate.add(close);
	}
	
	// 2. Menu Bar
	private void createMenuBar() {
		/*
		 * creates and adds a menu bar to the frame the menu bar contains a language
		 * submenu and a color theme submenu
		 */
		menubar = new JMenuBar();
		colorTheme = new JMenu("Color Theme");
		light = new JCheckBoxMenuItem("Ligth Color Theme");
		light.setSelected(true);
		colorTheme.add(light);
		blue = new JCheckBoxMenuItem("Blue Color Theme");
		colorTheme.add(blue);
		dark = new JCheckBoxMenuItem("Dark Color Theme");
		colorTheme.add(dark);

		dark.setSelected(true);
		light.setSelected(false);
		light.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(true);
				blue.setSelected(false);
				dark.setSelected(false);
				setColor(lightScheme);
			}
		});
		blue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(false);
				blue.setSelected(true);
				dark.setSelected(false);
				setColor(blueScheme);
			}
		});
		dark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(false);
				blue.setSelected(false);
				dark.setSelected(true);
				setColor(darkScheme);
			}
		});

		menubar.add(colorTheme);
		this.setJMenuBar(menubar);
	}
	
	// 3. Panels (log and chat)
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
		emptyPanel = new JPanel();
		rightSide.add(emptyPanel);
		rightSide.add(statusPanel);
		rightSide.add(emptyPanel);
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
		showLog.setBorder(raisedetched);
		showLog.addActionListener(this);
		
		logPanel.add(logTitle, BorderLayout.NORTH);
		logPanel.add(scroll, BorderLayout.CENTER);
		logPanel.add(showLog, BorderLayout.SOUTH);
	}
	
	private void createTaskPanel() {
		/*
		 * create task panel: dropdown menu, textfield and button
		 */
		northPanel = new JPanel(new BorderLayout(10, 10));
		southPanel = new JPanel(new BorderLayout(10, 10));
		// create dropdownMenu and title (north panel)
		taskLabel = new JLabel();
		taskLabel.setText("Possibilities: ");
		String comboBoxListe [] = {"change Servername", "clear Log", "clear Users", "clear Banned Users"};
		chooseTask = new JComboBox<String>(comboBoxListe);
		// create south panel with textfield and button
		taskText = new JTextField(20);
		taskText.addKeyListener(this);
		taskButton = new JButton("OK");
		taskButton.setBorder(raisedetched);
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
		row = new JPanel(new GridLayout(1, 3));
		statusLabel = new JLabel("Serverstatus: ");
		status = new JLabel("Online");
		status.setFont(status.getFont().deriveFont(14f));
		status.setHorizontalAlignment(JLabel.CENTER);
		change = new JButton("Change Status");
		change.setBorder(raisedetched);
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
		entireInfo = new JPanel(new GridLayout(4, 2));

		infoLabel = new JLabel("Informations: ");
		infoLabel.setFont(infoLabel.getFont().deriveFont(14f));
		// on the left side we display ip and numbers of online users
		idLabel = new JLabel("IP adress: ");
		setId = new JLabel(ip.getHostAddress().toString());
		onlineLabel = new JLabel("Online: ");
		setOnline = new JLabel(String.valueOf(this.online));		
		// on the right side we display the servername and if its editable
		nameLabel = new JLabel("Name: ");
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
		leftSide = new JPanel(new BorderLayout(10, 10));
		leftSideLabel = new JLabel("Chat in the selected Room: ");
		chatWindow = new JTextArea();
		chatWindow.setLineWrap(true);
		chatWindow.setEditable(false);
		chatWindow.setVisible(true);
		chatWindow.setFont(output);
		JScrollPane scroll = new JScrollPane(chatWindow);
		// input and text row
		rowPanel = new JPanel(new BorderLayout(10, 10));
		input = new JTextField();
		input.addKeyListener(this);
		send = new JButton("     Send     ");
		send.setBorder(raisedetched);
		send.addActionListener(this);
		// finish creating the left panel
		rowPanel.add(input, BorderLayout.CENTER);
		rowPanel.add(send, BorderLayout.EAST);
		leftSide.add(leftSideLabel, BorderLayout.NORTH);
		leftSide.add(scroll, BorderLayout.CENTER);
		leftSide.add(rowPanel, BorderLayout.SOUTH);
		// create right side
		rightSideChat = new JPanel(new BorderLayout(10, 10));
		rightSideLabel = new JLabel("Rooms, Users and some Actions: ");
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
		roomPanel = new JPanel(new BorderLayout(5, 5));
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
            	chatWindow.append("--------\n"); 
            }
        });
		rooms.addMouseListener(this);
		roomButton = new JButton("create Room");
		roomButton.setBorder(raisedetched);
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
		
		privateRoomModel = new DefaultListModel<String>();
		privateRooms = new JList<String>(privateRoomModel);
		privateRooms.addMouseListener(this);
		privateRooms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	selectedPrivate = privateRooms.getSelectedValue();
            }
        });
		JScrollPane scrollPrivateRooms = new JScrollPane(privateRooms);
			
		roomTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		roomTab.add("Rooms", roomPanel);
		roomTab.add("Users", scrollClients);
		roomTab.add("private Rooms", scrollPrivateRooms);
		// Panel with tabs and buttons to ban and show all users
		display = new JPanel(new BorderLayout(5, 5));
		displayAllUsers = new JButton("   All Users   ");
		displayAllUsers.setBorder(raisedetched);
		displayAllUsers.addActionListener(this);
		displayBannedUsers = new JButton("   Banned Users   ");
		displayBannedUsers.setBorder(raisedetched);
		displayBannedUsers.addActionListener(this);
		display.add(displayAllUsers, BorderLayout.WEST);
		display.add(displayBannedUsers, BorderLayout.EAST);
		
		rightSideChat.add(rightSideLabel, BorderLayout.NORTH);
		rightSideChat.add(roomTab, BorderLayout.CENTER);
		rightSideChat.add(display, BorderLayout.SOUTH);
		// set Size, so it doesnt resize after adding new Components
		rightSideChat.setPreferredSize(new Dimension(250, 100));
		
		users.add(leftSide, BorderLayout.CENTER);
		users.add(rightSideChat, BorderLayout.EAST);
	}
	
	// 4. minor Activities
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
	
	public void addPrivateRoom(String newRoom) {
		/*
		 * add a new private chat to the list
		 */
		privateRoomModel.addElement(newRoom);
	}
	
	public void removePrivateRoom(String oldRoom) {
		/*
		 * remove a private chat from the list
		 */
		privateRoomModel.removeElement(oldRoom);
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
	
	// 5. Dialogs
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
        okay.setBorder(raisedetched);
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
        unban.setBorder(raisedetched);
        unban.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// overwrite entry in csv file so the user is not banned anymore
				ReadAndWriteCsv.unbanUser(selectedBan);
				server.writeLog("You just unbaned " + selectedBan);
			}
        });
        listPanel.add(unban, BorderLayout.SOUTH);
        listPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        dialogPanel.add(listPanel, BorderLayout.CENTER);
        
        JButton okay = new JButton("Okay");
        okay.setBorder(raisedetched);
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
        okay.setBorder(raisedetched);
        okay.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) { dialog.dispose(); } });
        dialogPanel.add(okay, BorderLayout.SOUTH);
        
        dialog.add(dialogPanel);
        dialog.setSize(600, 500);
        dialog.setVisible(true);
	}
	
	// 6. Action Listeners
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
						if (line.contentEquals("")) {}
						else {
							// set servername
							setName.setText(line);
							frame.setTitle(line);
							server.writeLog("The Servername has been changed to " + line);
							server.sendToAll("!title" + line);
						}
					}
					if (index == 1) {
						//clear log
						ReadAndWriteCsv.clear("log");
						server.writeLog("The Serverlog was just cleared");
					}
					if (index == 2) {
						// clear user list
						ReadAndWriteCsv.clear("user");
						server.writeLog("The Server Userlist was just cleared");
					}
					if (index == 3) {
						// clear banned user list
						ReadAndWriteCsv.clear("banned");
						server.writeLog("The Serverbanned Userlist was just cleared");
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
						while (0 < clients.size()) {
							clients.get(0).sendMessage("!close");
							clients.remove(0);
							server.setAllActiveUsers(clients);
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
	
	// 7. Keylisteners
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
	
	// 8. MouseListeners
	@Override
	public void mousePressed(MouseEvent e) {
		/*
		 * works as a thread since a task could last longer
		 * basically handels clickevent on the popup menus
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (e.getSource() == join) {}
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
		    					
		    					selectedRoom = roomName;
		    					server.setFrameRoom(selectedRoom);
				            	listModelClients.setElementAt("You (Admin) " + "[" + selectedRoom + "]", 0);
		    				}
	    				}
	        		}
				}
				if (e.getSource() == delete) {
					if (selectedRoom.contentEquals("main")) {}
	        		else {
	        			server.deleteRoom(selectedRoom);
						listModelRooms.removeElement(selectedRoom);
		            	listModelClients.setElementAt("You (Admin) [main]", 0);
		            	rooms.setSelectedIndex(0);
		            	selectedRoom = listModelRooms.getElementAt(0);
		            	server.setFrameRoom(selectedRoom);
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
		    		client.sendMessage("!close");
				}
				if (e.getSource() == permaBan) {
					int index = selectedUser.indexOf("(");
		    		ServerClientThread client = server.getUser(selectedUser.substring(0, index - 1));
		    		server.writeLog(client.getUsername() + " has been permanently banned.");
		    		ReadAndWriteCsv.writeCsv(client.getUsername(), client.getPassword(), 1);
		    		client.sendMessage("!close");
				}
				if (e.getSource() == close) {
					// open new window with private conversation
					server.closePrivateConnection(selectedPrivate);
					privateRoomModel.removeElement(selectedPrivate);
					selectedPrivate = null;
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
			if (e.getSource() == privateRooms) {
				if (selectedPrivate == null) {}
            	else { popPrivate.show(privateRooms, e.getX(), e.getY()); }
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
	
	// 9. color schemes
	private void setColor(Color[] scheme) {
		Color menu = scheme[0];
		Color background = scheme[1];
		Color textfield = scheme[2];
		Color button = scheme[3];
		Color foreground = scheme[4];
		// menu
		menubar.setBackground(menu);
		colorTheme.setBackground(menu);
		colorTheme.setForeground(foreground);
		light.setBackground(menu);
		light.setForeground(foreground);
		blue.setBackground(menu);
		blue.setForeground(foreground);
		dark.setBackground(menu);
		dark.setForeground(foreground);		
		// tabs
		tabs.setBackground(background);
		roomTab.setBackground(background);
		// set Panel Color
		users.setBackground(background);
		finalLog.setBackground(background);
		rightSide.setBackground(background);
		logPanel.setBackground(background);
		statusPanel.setBackground(background);
		informationPanel.setBackground(background);
		taskPanel.setBackground(background);
		entireInfo.setBackground(background);
		northPanel.setBackground(background);
		southPanel.setBackground(background);
		row.setBackground(background);
		emptyPanel.setBackground(background);
		users.setBackground(background);
		leftSide.setBackground(background);
		rowPanel.setBackground(background);
		roomPanel.setBackground(background);
		rightSideChat.setBackground(background);
		display.setBackground(background);
		// set Textfield and lists
		outputPanel.setBackground(textfield);
		outputPanel.setForeground(foreground);
		taskText.setBackground(textfield);
		taskText.setForeground(foreground);
		chooseTask.setBackground(background);
		chooseTask.setForeground(foreground);
		chatWindow.setBackground(textfield);
		chatWindow.setForeground(foreground);
		input.setBackground(textfield);
		input.setForeground(foreground);
		privateRooms.setBackground(textfield);
		privateRooms.setForeground(foreground);
		clients.setBackground(textfield);
		clients.setForeground(foreground);
		rooms.setBackground(textfield);
		rooms.setForeground(foreground);
		
		// set Buttons
		showLog.setBackground(button);
		showLog.setForeground(foreground);
		taskButton.setBackground(button);
		taskButton.setForeground(foreground);
		change.setBackground(button);
		change.setForeground(foreground);
		send.setBackground(button);
		send.setForeground(foreground);
		roomButton.setBackground(button);
		roomButton.setForeground(foreground);
		displayAllUsers.setBackground(button);
		displayAllUsers.setForeground(foreground);
		displayBannedUsers.setBackground(button);
		displayBannedUsers.setForeground(foreground);
		
		// set Label
		setName.setForeground(foreground);
		setOnline.setForeground(foreground);
		status.setForeground(foreground);
		taskLabel.setForeground(foreground);
		statusLabel.setForeground(foreground);
		infoLabel.setForeground(foreground);
		idLabel.setForeground(foreground);
		setId.setForeground(foreground);
		onlineLabel.setForeground(foreground);
		nameLabel.setForeground(foreground);
		logTitle.setForeground(foreground);
		leftSideLabel.setForeground(foreground);
		rightSideLabel.setForeground(foreground);
	}
}
