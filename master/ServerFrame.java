package master;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ServerFrame extends JFrame implements ActionListener, MouseListener {
	JPanel rightSide, statusAndInformation, logPanel, statusPanel, informationPanel, taskPanel;
	JLabel logTitle;
	
	ServerFrame frame = this;
	
	JButton displayAllUsers;
	
	DefaultListModel<String> listModelRooms,listModelClients;
	JList<String> rooms, clients;
	
	JTextArea outputPanel;
	
	JMenuItem edit, delete, warning, ban, permaBan;
	
	JPanel finalLog, users;
	JTabbedPane tabs;
	
	final JPopupMenu pop = new JPopupMenu();
	final JPopupMenu popUser = new JPopupMenu();
	private ServerStart server;
	private String selectedRoom;
	private String selectedUser;
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int width = (int) screenSize.getWidth();
	int height = (int) screenSize.getHeight();
		
	public ServerFrame(ServerStart server) {
		this.selectedRoom = null;
		this.selectedUser = null;
		this.server = server;
		this.setTitle("Server!!!");
		this.setSize(1000, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		createRoomPopupMenu();
		createUserPopupMenu();
		// final Panel
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
	
	private void createRoomPopupMenu() {
		edit = new JMenuItem("Edit");
        delete = new JMenuItem("Remove");
        edit.addMouseListener(this);
        delete.addMouseListener(this);
        pop.add(edit);
        pop.add(delete);
	}
	
	private void createUserPopupMenu() {
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
	
	private void createLogPanel() {
		// left side
		logPanel = new JPanel(new BorderLayout());
		rightSide = new JPanel(new BorderLayout(10, 10));
		// the status and information panel will be low on the right side
		// it will contain the status and the informations
		statusAndInformation = new JPanel(new BorderLayout());
		statusPanel = new JPanel(new BorderLayout());
		informationPanel = new JPanel(new BorderLayout());
		// in the task panel we can change some settings
		taskPanel = new JPanel(new BorderLayout(10, 10));
		
		createServerLogPanel();
		createTaskPanel();
		createStatusPanel();
		createInformationsPanel();
		
		statusAndInformation.add(statusPanel, BorderLayout.NORTH);
		statusAndInformation.add(informationPanel, BorderLayout.SOUTH);
		rightSide.add(statusAndInformation, BorderLayout.SOUTH);
		rightSide.add(taskPanel, BorderLayout.NORTH);
		finalLog.add(rightSide, BorderLayout.EAST);
		finalLog.add(logPanel, BorderLayout.CENTER);
	}
	
	private void createServerLogPanel() {
		// title of the logs
		logTitle = new JLabel("Server Log: ");
		logTitle.setFont(logTitle.getFont().deriveFont(14f));
		// create output Panel
		outputPanel = new JTextArea();
		outputPanel.setLineWrap(true);
		outputPanel.setEditable(false);
		outputPanel.setVisible(true);
		outputPanel.setFont(outputPanel.getFont().deriveFont(14f));
		// we need to put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(outputPanel);
		
		logPanel.add(logTitle, BorderLayout.NORTH);
		logPanel.add(scroll, BorderLayout.CENTER);
		logPanel.setVisible(true);
	}
	
	private void createTaskPanel() {
		// set Title
		JLabel taskLabel = new JLabel();
		taskLabel.setText("Possibilities: ");
		JPanel northPanel = new JPanel(new BorderLayout(10, 10));
		JPanel southPanel = new JPanel(new BorderLayout(10, 10));
		JTextField taskText = new JTextField(20);
		JButton taskButton = new JButton("OK");
		String comboBoxListe [] = {"change Servername" };
		JComboBox<String> chooseTask = new JComboBox<String>(comboBoxListe);

		northPanel.add(taskLabel, BorderLayout.WEST);
		northPanel.add(chooseTask, BorderLayout.EAST);
		southPanel.add(taskText, BorderLayout.WEST);
		southPanel.add(taskButton, BorderLayout.EAST);
		taskPanel.add(northPanel, BorderLayout.NORTH);
		taskPanel.add(southPanel, BorderLayout.SOUTH);
	}
	
	private void createStatusPanel() {
		JLabel statusLabel = new JLabel("Serverstatus: ONLINE :)");
		statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
		statusPanel.add(statusLabel);
	}
	
	private void createInformationsPanel() {
		JPanel entireInfo = new JPanel(new BorderLayout());

		JLabel infoLabel = new JLabel("Informations: ");
		infoLabel.setFont(infoLabel.getFont().deriveFont(14f));
		
		JPanel leftInfo = new JPanel(new GridLayout(2, 1));
		JLabel idLabel = new JLabel("ID: ");
		JLabel setId = new JLabel();
		setId.setBorder(BorderFactory.createTitledBorder("setId"));
		JLabel onlineLabel = new JLabel("Online: ");
		JLabel setOnline = new JLabel();		
		
		JPanel rightInfo = new JPanel(new GridLayout(2, 1));
		JLabel nameLabel = new JLabel("Name: ");
		JLabel setName = new JLabel();
		JLabel editable = new JLabel("Editable: ");
		JLabel setEdit = new JLabel();
		
		leftInfo.add(idLabel);
		leftInfo.add(setId);
		leftInfo.add(onlineLabel);
		leftInfo.add(setOnline);
		
		rightInfo.add(nameLabel);
		rightInfo.add(setName);
		rightInfo.add(editable);
		rightInfo.add(setEdit);

		entireInfo.add(leftInfo, BorderLayout.WEST);
		entireInfo.add(rightInfo,BorderLayout.EAST);
		informationPanel.add(infoLabel, BorderLayout.NORTH);
		informationPanel.add(entireInfo, BorderLayout.CENTER);
	}
	
	private void createChatPanel() {
		JPanel leftSide = new JPanel(new BorderLayout(10, 10));
		JPanel rightSide = new JPanel(new BorderLayout(10, 10));
		
		JLabel leftSideLabel = new JLabel("Chat in the selected Room: ");
		JTextArea chatWindow = new JTextArea();
		chatWindow.setLineWrap(true);
		chatWindow.setEditable(false);
		chatWindow.setVisible(true);
		chatWindow.setFont(chatWindow.getFont().deriveFont(14f));
		// we need to put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(chatWindow);
		
		JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
		JTextField input = new JTextField();
		JButton send = new JButton("Send");
		
		rowPanel.add(input, BorderLayout.CENTER);
		rowPanel.add(send, BorderLayout.EAST);
		leftSide.add(leftSideLabel, BorderLayout.NORTH);
		leftSide.add(scroll, BorderLayout.CENTER);
		leftSide.add(rowPanel, BorderLayout.SOUTH);
		
		JLabel rightSideLabel = new JLabel("Rooms, Users and some Actions: ");
		
		listModelClients = new DefaultListModel<String>();
		clients = new JList<String>(listModelClients);
		clients.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// if we click a different list entry we send a code to the server
            	int index = clients.getSelectedIndex();
            	selectedUser = clients.getSelectedValue();
            	if (selectedUser == null) {}
            	else {
            		Point indexToLocation = clients.indexToLocation(index);
                    Rectangle cellBounds = clients.getCellBounds(index, index);
                    popUser.show(clients, indexToLocation.x, indexToLocation.y+cellBounds.height);
            	}
            }
        });
		
		JPanel roomPanel = new JPanel(new BorderLayout(5, 5));
		roomPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		listModelRooms = new DefaultListModel<String>();
		rooms = new JList<String>(listModelRooms);
		rooms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// if we click a different list entry we send a code to the server
            	int index = rooms.getSelectedIndex();
            	selectedRoom = rooms.getSelectedValue();
            	if (selectedRoom == null) {}
            	else {
            		Point indexToLocation = rooms.indexToLocation(index);
                    Rectangle cellBounds = rooms.getCellBounds(index, index);
                    pop.show(rooms, indexToLocation.x, indexToLocation.y+cellBounds.height);
            	}
            }
        });
		JButton roomButton = new JButton("create Room");
		roomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String roomName = JOptionPane.showInputDialog(frame, "New Chatroom name: ", "create Chatroom",
						JOptionPane.QUESTION_MESSAGE);
				if (roomName == null || roomName.contentEquals("")) {}
				else {
					boolean exist = false;
					ArrayList<Chatroom> rooms = server.getRooms();
					for (Chatroom room : rooms) {
						if (room.getName().contentEquals(roomName)) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						// add room to the server
						// basically just creates a new chatroom and add it to the list
						server.addRoom(roomName);
						server.writeLog("You just created a new Room named " + roomName + ".");
						server.sendRoom(roomName);
						server.sendRoomClients(roomName);
					}
				}
			}
		});
		
		roomPanel.add(rooms, BorderLayout.CENTER);
		roomPanel.add(roomButton, BorderLayout.SOUTH);
				
		JTabbedPane roomTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		roomTab.add("Rooms", roomPanel);
		roomTab.add("Users", clients);
		
		displayAllUsers = new JButton("Display All Users");
		displayAllUsers.addActionListener(this);
		
		rightSide.add(rightSideLabel, BorderLayout.NORTH);
		rightSide.add(roomTab, BorderLayout.CENTER);
		rightSide.add(displayAllUsers, BorderLayout.SOUTH);
		
		users.add(leftSide, BorderLayout.CENTER);
		users.add(rightSide, BorderLayout.EAST);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == displayAllUsers) {
			displayAllUsers();
		}
	}
	
	public void writeLog(String newLog) {
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String t = time.format(formatter);
		outputPanel.append("[" + t + "] - " + newLog + "\n");
	}
	
	public void displayRoom(String name) {
		listModelRooms.addElement(name);
	}
	
	public void addClient(String message) {
		// message allready contains name and roomname
		listModelClients.addElement(message);
	}
	
	public void removeClient(String message) {
		listModelClients.removeElement(message);
	}
	
	public void changeClient(String old, String message) {
		int index = listModelClients.indexOf(old);
		listModelClients.setElementAt(message, index);
	}
	
	private void displayAllUsers() {
		HashMap<String, String> list = ReadAndWriteCsv.readCsv();
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
        dialog.setSize(40 * listModelAllUsers.getSize(), 300);
        dialog.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
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
		    		try {
						client.close(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (e.getSource() == permaBan) {
					
				}
			}
		});
		t.start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
