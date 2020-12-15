package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ClientFrame extends JFrame implements ActionListener, KeyListener {
	/*
	 * creates a frame for the client
	 * the frame consists of a left, center and right panel with diefferent containers
	 * we mainly use a BorderLayout since Gridlayout doesnt work that well with different sizes
	 */
	// frame (need it only for @Override functions, since 'this' doesnt work)
	ClientFrame frame = this;
	// given outputStream to the Server
	PrintWriter out;
	// define some Panel Variables
	JPanel finalPanel, leftPanel, rightPanel, roomPanel;
	JPanel rowPanel, nickname, chatRooms, onlinePanel;
	// Textfields (insert text and insert new nickname
	JTextField insert, insertNickname;
	// display output from the server
	JTextArea outputPanel;
	// various buttons :D
	JButton sendMessage, sendNickname, createRoom;
	
	JLabel room, changeNickname, online;
	
	// list for rooms and users with corresponding listmodels to add elements	
	JList<String> onlineUsers, rooms;
	DefaultListModel<String> listModelOnline, listModelRooms;
	
	JMenuBar menubar;
	JMenu languageMenu, colorTheme;
	JCheckBoxMenuItem english, german, spanish, light, dark;
	
		 
	Border blackline = BorderFactory.createLineBorder(Color.black, 1);
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	Border ligthline = BorderFactory.createLineBorder(new Color(200, 220, 40), 1);
	
	Font output = new Font("Arial", Font.BOLD + Font.ITALIC, 20);
	
	public ClientFrame(PrintWriter printerOut) {
		/* Constructor for the chat frame. 
		 * We use a BorderLayout 
		 */
		// output stream to the server
		this.out = printerOut;
		// set visibility to false unless user is logged in
		this.setVisible(false);
		
		// set the layout of the frame to a Border Layout and set title
		this.setLayout(new BorderLayout());
		this.setTitle("The ultimate Java Chat!");
		
		createMenuBar();
		
		// We got one finalpanle which contains a left, right panel and room panel
		// The panels are created via the differnet function (look there for documentation)
		finalPanel = new JPanel(new BorderLayout(10, 10));
		finalPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		leftPanel = new JPanel(new BorderLayout(20, 20));
		this.createLeftPanel();
		rightPanel = new JPanel(new BorderLayout(20, 20));
		this.createRightPanel();
		roomPanel = new JPanel(new BorderLayout(20, 20));
		this.createRoomPanel();
		// add components to the panel
		finalPanel.add(roomPanel, BorderLayout.WEST);
		finalPanel.add(leftPanel, BorderLayout.CENTER);
		finalPanel.add(rightPanel, BorderLayout.EAST);
		// add component to the frame
		this.add(finalPanel, BorderLayout.CENTER);
		// set Size and closing Operator
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1130, 500);
		this.setResizable(true);
	}
	
	private void createMenuBar() {
		/*
		 * creates and adds a menu bar to the frame
		 * the menu bar contains a language submenu and a color theme submenu
		 */
		menubar = new JMenuBar();
		menubar.setBorder(blackline);
		languageMenu = new JMenu("Language");
		english = new JCheckBoxMenuItem("English");
		english.setSelected(true);
		languageMenu.add(english);
		german = new JCheckBoxMenuItem("Deutsch");
		languageMenu.add(german);
		spanish = new JCheckBoxMenuItem("Espanol");
		languageMenu.add(spanish);
		
		colorTheme = new JMenu("Color Theme");
		light = new JCheckBoxMenuItem("Ligth Color Theme");
		light.setSelected(true);
		colorTheme.add(light);
		dark = new JCheckBoxMenuItem("Dark Color Theme");
		colorTheme.add(dark);
		
		english.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				languageMenu.setText("Language");
				colorTheme.setText("Color Theme");
				light.setText("Light Color Theme");
				dark.setText("Dark Color Theme");
				english.setSelected(true);
				german.setSelected(false);
				spanish.setSelected(false);
				
				sendMessage.setText("Send Message");
				sendNickname.setText("Send Nickname");
				createRoom.setText("Create Room");
				room.setText("avaiable Rooms");
			}
		});
		german.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				languageMenu.setText("Sprache");
				colorTheme.setText("Farb Thema");
				light.setText("Hell");
				dark.setText("Dunktel");
				german.setSelected(true);
				english.setSelected(false);
				spanish.setSelected(false);
				
				sendMessage.setText("Nachricht senden");
				sendNickname.setText("Nickname ändern");
				createRoom.setText("Raum erstellen");
				room.setText("Existierende Räume");
			}
		});
		spanish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				languageMenu.setText("Idioma");
				colorTheme.setText("Tema de Color");
				light.setText("brillante");
				dark.setText("oscuro");
				english.setSelected(false);
				german.setSelected(false);
				spanish.setSelected(true);
				
				sendMessage.setText("enviar mensaje");
				sendNickname.setText("cambiar nombre");
				createRoom.setText("crear otro foro");
				room.setText("foros existe");
			}
		});
		light.setSelected(true);
		light.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(true);
				dark.setSelected(false);
				Color background = new Color(255, 255, 255);
				Color foreground = new Color(0, 0, 0);
				setColor(background, background, background, foreground, foreground);
			}
		});
		dark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(false);
				dark.setSelected(true);
				Color background0 = new Color(40, 40, 40);
				Color background1 = new Color(50, 50, 50);
				Color background2 = new Color(70, 70, 70);
				Color foreground = new Color(60, 170, 50);
				Color foregroundList = new Color(200, 220, 40);
				setColor(background0, background1, background2, foreground, foregroundList);
			}
		});
		
		menubar.add(languageMenu);
		menubar.add(colorTheme);
		this.setJMenuBar(menubar);
	}
	
	private void createRightPanel() {
		/* creates an Panel for the user to change the nickname 
		 * and to display all online users in the chatroom
		 * consists of an Label, a Button to commit changes
		 * and a list for the users
		 */
		// create nickname Panel
		changeNickname = new JLabel("Change Nickname: ");
		nickname = new JPanel(new BorderLayout());
		// create insert Field
		insertNickname = new JTextField(10);
		insertNickname.addKeyListener(this);
		insertNickname.setBorder(raisedetched);
		// create Button
		sendNickname = new JButton("Change Name");
		// add listener
		sendNickname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (insertNickname.getText().contentEquals("")) {}
				else {
					// prints a given code to the server to change the nickname
					out.println("!nickname" + insertNickname.getText());
					// sets the text of this input frame to null
					insertNickname.setText(null);
				}
			}
		});
		// add Panels
		nickname.add(changeNickname, BorderLayout.NORTH);
		nickname.add(insertNickname, BorderLayout.WEST);
		nickname.add(sendNickname, BorderLayout.EAST);
		// add list for all users (using JList and default Listmodel)
		onlinePanel = new JPanel(new BorderLayout());
		online = new JLabel("Online Users:");
		listModelOnline = new DefaultListModel<String>();
		onlineUsers = new JList<String>(listModelOnline);		
		onlineUsers.setBorder(blackline);
		onlinePanel.add(online, BorderLayout.NORTH);
		onlinePanel.add(onlineUsers, BorderLayout.CENTER);
		// add nickname-Panel and list to the rightPanel
		rightPanel.add(nickname, BorderLayout.NORTH);
		rightPanel.add(onlinePanel, BorderLayout.CENTER);
	}
	
	private void createLeftPanel() {
		/*
		 * basically creates the left Panel: A Textarea for the output Text, 
		 * a Input and a Button to send Messages
		 */
		// create Output TextArea
		outputPanel = new JTextArea(14, 20);
		outputPanel.setFont(output);
		outputPanel.setLineWrap(true);
		// sets Editable and Visibility of the output Panel
		outputPanel.setEditable(false);
		outputPanel.setVisible(true);
		outputPanel.setBorder(blackline);
		// we need to put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(outputPanel);
		
		// The row Panel should contain the insert and the button to send messages
		rowPanel = new JPanel(new BorderLayout());
		// text Field insert
		insert = new JTextField(50);
		insert.setBorder(raisedetched);
		insert.addKeyListener(this);
		// Sending Button
		sendMessage = new JButton("Send Message");
		sendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// start a thread since we have to do other things as well
				Thread t = new Thread(new Runnable() {
					@Override
			        public void run() {
						// the text shouldnt be empty
						if (insert.getText().contentEquals("")) {}
						// the first char shouldnt be a '!'
						if (insert.getText().substring(0, 1).contentEquals("!")) {}
						else { out.println(insert.getText()); }
						insert.setText(null);
			        }
			    });
			    t.start();
			}
		});
		// add Components
		rowPanel.add(insert, BorderLayout.WEST);
		rowPanel.add(sendMessage, BorderLayout.EAST);
		
		rowPanel.setVisible(true);
		// add to Left Panel
		leftPanel.add(scroll, BorderLayout.CENTER);
		leftPanel.add(rowPanel, BorderLayout.SOUTH);
	}
	
	private void createRoomPanel() {
		/*
		 * consists of a list for all different rooms and a button to create a new room
		 */
		room = new JLabel("avaiable Chatrooms:");
		// basic panel
		chatRooms = new JPanel(new BorderLayout(5, 10));
		// create a list and a model
		listModelRooms = new DefaultListModel<String>();
		rooms = new JList<String>(listModelRooms);
		rooms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// if we click a different list entry we send a code to the server
                if (e.getValueIsAdjusting()) {
                	int index = rooms.getSelectedIndex();
                	out.println("!roomc" + listModelRooms.get(index));
                }
            }
        });
		// create new room
		createRoom = new JButton("create new Room!");
		createRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// shows pop up window with a textfield
				String name = JOptionPane.showInputDialog(frame, "New Chatroom name: ", "create Chatroom",
						JOptionPane.QUESTION_MESSAGE);
				if (name == null || name.contentEquals("")) {}
				else { out.println("!rooma" + name); }
			}
		});
		// add components to the frame
		chatRooms.add(room, BorderLayout.NORTH);
		chatRooms.add(rooms);
		chatRooms.add(createRoom, BorderLayout.SOUTH);
		chatRooms.setVisible(true);
		roomPanel.setBorder(blackline);
		roomPanel.add(chatRooms, BorderLayout.CENTER);
	}
	
	public void writeOut(String line) {
		/*
		 * checks server input and writes output accordingly
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (line.contentEquals("")) {}
				else {
					// if the code is !online we update the online list (look updateOnline for doc)
					if (line.length() > 9 && line.substring(0, 7).contentEquals("!online")) {
						updateOnline(line);
					}
					else {
						// if the code is !room we update the room list
						if (line.length() > 5 && line.substring(0, 5).contentEquals("!room")) {
							updateRooms(line);
						}
						// just output the rest
						else {
							outputPanel.append(line + "\n");
						}
					}
				}
			}
		});
		t.start();
	}
	
	private void updateOnline(String line) {
		/*
		 * updates the online list
		 * either adds a user, removes one or displays the entire list (if new to room)
		 */
		String user = line.substring(8);
		String action = line.substring(7,8);
		// we update the entire list
		if (action.contentEquals("e")) {
			// first we need to reset the list
			listModelOnline.removeAllElements();
			// we are new so we need to write out everything
			String[] names = user.split(",");
			for (String name : names) {
				listModelOnline.addElement(name);
			}
		}
		else {
			// remove a list entry
			if (action.contentEquals("r")) {
				// remove user but catch if the user doesnt exist
				try {
					listModelOnline.removeElement(user);
				}
				catch (NullPointerException e) {}
			}
			else {
				if (action.contentEquals("a")) {
					// add user
					listModelOnline.addElement(user);
				}
				else {
					// change nickname
					try {
						// code for changing nickname
						String[] newName = user.split("%");
						String newNickname = newName[0];
						// go through every name and change a specific one
						for (int i = 0; i < listModelOnline.size(); i++) {
							if (listModelOnline.getElementAt(i).contentEquals(newName[1])) {
								listModelOnline.setElementAt(newNickname, i);
								break;
							}
						}
					}
					catch (NullPointerException e) {}
				}
			}
		}
	}
	
	private void updateRooms(String line) {
		/*
		 * is called then '!room' was the input
		 * update the rooms list accordingly
		 * remove isnt implemented yet
		 */
		// add all rooms (at the beginning)	
		if (line.substring(5, 6).contentEquals("e")) {
			// first we need to reset the list
			listModelRooms.removeAllElements();
			String[] names = line.substring(7).split(",");
			for (String name : names) {
				listModelRooms.addElement(name);
			}
			// select first entry because we are new and want to join the default room
			rooms.setSelectedIndex(0);
		}
		else {
			// just add a room
			if (line.substring(5, 6).contentEquals("a")) {
				listModelRooms.addElement(line.substring(6));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * add key listeners to both textfields 
		 * (does the same as the action listener if enter is pressed)
		 */
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (e.getSource() == insert) {
				out.println(insert.getText());
				insert.setText(null);
			}
			if (e.getSource() == insertNickname) {
				out.println("!nickname" + insertNickname.getText());
				insertNickname.setText(null);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	public void close() {
		/*
		 * closes the client
		 */
		this.dispose();
	}
	
	private void setColor(Color background0, Color background1, Color background2, Color foreground, Color foregroundList) {
		/*
		 * sets color for almost every component and borders
		 */
		finalPanel.setBackground(background0);
		// menu
		menubar.setBackground(background0);
		languageMenu.setBackground(background0);
		languageMenu.setForeground(foreground);
		colorTheme.setBackground(background0);
		colorTheme.setForeground(foreground);
		english.setBackground(background0);
		english.setForeground(foreground);
		german.setBackground(background0);
		german.setForeground(foreground);
		spanish.setBackground(background0);
		spanish.setForeground(foreground);
		light.setBackground(background0);
		light.setForeground(foreground);
		dark.setBackground(background0);
		dark.setForeground(foreground);		
		// left panel
		leftPanel.setBackground(background0);
		rowPanel.setBackground(background0);
		insert.setBackground(background1);
		insert.setForeground(foreground);
		outputPanel.setBackground(background1);
		outputPanel.setForeground(foreground);
		// Buttons
		sendMessage.setBackground(background1);
		sendNickname.setBackground(background1);
		createRoom.setBackground(background1);
		sendMessage.setForeground(foreground);
		sendNickname.setForeground(foreground);
		createRoom.setForeground(foreground);
		// JLists
		onlinePanel.setBackground(background1);
		onlineUsers.setBackground(background2);
		rooms.setBackground(background1);
		onlineUsers.setForeground(foregroundList);
		rooms.setForeground(foregroundList);
		online.setBackground(background1);
		online.setForeground(foreground);
		// Roompanel and Label
		roomPanel.setBackground(background1);
		chatRooms.setBackground(background1);
		room.setBackground(background1);
		room.setForeground(foreground);
		// nickname panel
		rightPanel.setBackground(background0);
		insertNickname.setBackground(background1);
		insertNickname.setForeground(foreground);
		nickname.setBackground(background1);
		changeNickname.setForeground(foreground);
	}
}
