package client;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class ClientFrame extends JFrame implements ActionListener, KeyListener, MouseListener {
	/*
	 * creates a frame for the client
	 * the frame consists of a left, center and right panel with diefferent containers
	 * we main 
	 */
	private Color[] colorScheme;
	private String myName;
	ArrayList<ClientPrivateFrame> privateRooms = new ArrayList<ClientPrivateFrame>();
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
	
	// Popup menu
	final JPopupMenu pop = new JPopupMenu();
	JMenuItem seeProfil, startChat;
	//menu
	JMenuBar menubar;
	JMenu languageMenu, colorTheme;
	JCheckBoxMenuItem english, german, spanish, light, dark, blue;
	
	String selectedUser;
	// color schemes. first is menu bar, second is final panel, third is textfields and list, 
	// fourth is buttons and fifth is writing color
	Color[] lightScheme = {new Color(0xffffff), new Color(0xcffffff), 
			new Color(0xffffff), new Color(0xc5e1a5), new Color(0x000000)};
	Color[] blueScheme = {new Color(0xbbc4f2), new Color(0xbdc6d4), 
			new Color(0xdfe2ef), new Color(0xc8cff2), new Color(0x000000)};
	Color[] darkScheme = {new Color(0x102027), new Color(0x62727b), 
			new Color(0x37474f), new Color(0x00695c), new Color(0xffffff)};
		 
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	
	Font output = new Font("Arial", Font.BOLD + Font.ITALIC, 20);
	
	// 1. Constructor
	public ClientFrame(PrintWriter printerOut) {
		/* Constructor for the chat frame. 
		 * We use a BorderLayout 
		 */
		this.myName = null;
		this.colorScheme = darkScheme;
		// output stream to the server
		this.selectedUser = null;
		this.out = printerOut;
		// set visibility to false unless user is logged in
		this.setVisible(false);
		
		// set the layout of the frame to a Border Layout and set title
		this.setLayout(new BorderLayout());
		this.setTitle("A Java Server");
		
		createMenuBar();
		createRoomPopupMenu();
		
		// We got one finalpanle which contains a left, right panel and room panel
		// The panels are created via the differnet function (look there for documentation)
		finalPanel = new JPanel(new BorderLayout(10, 10));
		finalPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		roomPanel = new JPanel(new BorderLayout(20, 20));
		this.createRoomPanel();
		leftPanel = new JPanel(new BorderLayout(20, 20));
		this.createLeftPanel();
		rightPanel = new JPanel(new BorderLayout(20, 20));
		this.createRightPanel();
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
		this.setColor(darkScheme);
	}
	
	public void setName(String name) { this.myName = name; }
		
	// 2. PopUp menu and Menu Bar
	private void createRoomPopupMenu() {
		/*
		 * if you click on a room you can choose to edit or remove the room
		 * see in the mouselisteners for more documentation
		 */
		seeProfil = new JMenuItem("See Profil");
		startChat = new JMenuItem("start private Chat");
        seeProfil.addMouseListener(this);
        startChat.addMouseListener(this);
        pop.add(seeProfil);
        pop.add(startChat);
	}
	
	
	private void createMenuBar() {
		/*
		 * creates and adds a menu bar to the frame
		 * the menu bar contains a language submenu and a color theme submenu
		 */
		menubar = new JMenuBar();
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
		blue = new JCheckBoxMenuItem("Blue Color Theme");
		colorTheme.add(blue);
		dark = new JCheckBoxMenuItem("Dark Color Theme");
		colorTheme.add(dark);
		
		english.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				languageMenu.setText("Language");
				colorTheme.setText("Color Theme");
				light.setText("Light Color Theme");
				blue.setText("Blue Color Theme");
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
				blue.setText("Blau");
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
				blue.setText("azule");
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
		dark.setSelected(true);
		light.setSelected(false);
		light.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(true);
				blue.setSelected(false);
				dark.setSelected(false);
				setColor(lightScheme);
				frame.colorScheme = lightScheme;
			}
		});
		blue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(false);
				blue.setSelected(true);
				dark.setSelected(false);
				setColor(blueScheme);
				frame.colorScheme = blueScheme;
			}
		});
		dark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				light.setSelected(false);
				blue.setSelected(false);
				dark.setSelected(true);
				setColor(darkScheme);
				frame.colorScheme = darkScheme;
			}
		});
		
		menubar.add(languageMenu);
		menubar.add(colorTheme);
		this.setJMenuBar(menubar);
	}
	
	// 3. create Panels from left to right
	private void createRoomPanel() {
		/*
		 * consists of a list for all different rooms and a button to create a new room
		 */
		room = new JLabel("avaiable Chatrooms:");
		// basic panel
		chatRooms = new JPanel(new BorderLayout(5, 5));
		// create a list and a model
		listModelRooms = new DefaultListModel<String>();
		listModelRooms.addElement("main");
		rooms = new JList<String>(listModelRooms);
		JScrollPane scrollRooms = new JScrollPane(rooms);
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
		createRoom = new JButton("  create new Room!  ");
		createRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// shows pop up window with a textfield
				JPanel panel = new JPanel(new GridLayout(5, 1));
				TextField inputName = new TextField(30);
				TextField inputPassword = new TextField(30);
				JCheckBox checkPrivate = new JCheckBox("create private Chatroom");
				inputPassword.setEnabled(false);
				checkPrivate.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (checkPrivate.isSelected()) { inputPassword.setEnabled(true); }
						else { inputPassword.setEnabled(false); }
					}
				});
				inputPassword.setEchoChar('*');
				panel.add(new JLabel("Enter Chatroomname: "));
				panel.add(inputName);
				panel.add(new JLabel("Enter Chatroompassword: "));
				panel.add(inputPassword);
				panel.add(checkPrivate);
				JOptionPane.showMessageDialog(frame, panel, "create Chatroom", JOptionPane.PLAIN_MESSAGE);
				String name = inputName.getText();
				String password = inputPassword.getText();
				if (password.contentEquals("") || password == null) {
					if (name == null || name.contentEquals("")) {}
					else { out.println("!rooma" + name); }
				}
				else {
					if (name == null || name.contentEquals("")) {}
					else { out.println("!roomap!" + password + "!" + name); }
				}
			}
		});
		chatRooms.add(room, BorderLayout.NORTH);
		chatRooms.add(scrollRooms, BorderLayout.CENTER);
		chatRooms.add(createRoom, BorderLayout.SOUTH);
		// add components to the frame
		roomPanel.add(chatRooms, BorderLayout.CENTER);
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
		// we need to put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(outputPanel);
		
		// The row Panel should contain the insert and the button to send messages
		rowPanel = new JPanel(new BorderLayout(30, 30));
		// text Field insert
		insert = new JTextField(50);
		insert.setBorder(raisedetched);
		insert.addKeyListener(this);
		// Sending Button
		sendMessage = new JButton("  Send Message  ");
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
		rowPanel.add(insert, BorderLayout.CENTER);
		rowPanel.add(sendMessage, BorderLayout.EAST);
		
		// add to Left Panel
		leftPanel.add(scroll, BorderLayout.CENTER);
		leftPanel.add(rowPanel, BorderLayout.SOUTH);
	}
	
	private void createRightPanel() {
		/* creates an Panel for the user to change the nickname 
		 * and to display all online users in the chatroom
		 * consists of an Label, a Button to commit changes
		 * and a list for the users
		 */
		// create nickname Panel
		changeNickname = new JLabel("Change Nickname: ");
		nickname = new JPanel(new BorderLayout(10, 10));
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
		onlineUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	// set selected item
            	selectedUser = onlineUsers.getSelectedValue();
            }
        });
		onlineUsers.addMouseListener(this);
		JScrollPane onlineScroll = new JScrollPane(onlineUsers);
		onlinePanel.add(online, BorderLayout.NORTH);
		onlinePanel.add(onlineScroll, BorderLayout.CENTER);
		// add nickname-Panel and list to the rightPanel
		rightPanel.add(nickname, BorderLayout.NORTH);
		rightPanel.add(onlinePanel, BorderLayout.CENTER);
	}
	
	// 4. Writing and Displaying Functions
	public void writeOut(String line) {
		/*
		 * checks server input and writes output accordingly
		 */
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (line.contentEquals("")) {}
				if (line.substring(0, 1).contentEquals("!")) {
					// if the code is !online we update the online list (look updateOnline for doc)
					if (line.length() > 9 && line.substring(0, 7).contentEquals("!online")) {
						updateOnline(line);
					} else {
						// if the code is !room we update the room list
						if (line.length() > 5 && line.substring(0, 5).contentEquals("!room")) {
							updateRooms(line);
						} else {
							if (line.length() == 8 && line.contentEquals("!warning")) {
								JOptionPane.showMessageDialog(frame, "Dont do this again", "you have been warned",
										JOptionPane.WARNING_MESSAGE);
							} else {
								if (line.length() == 6 && line.contentEquals("!close")) {
									close();
								} else {
									if (line.length() > 6 && line.substring(0, 6).contentEquals("!title")) {
										frame.setTitle(line.substring(6));
									}
									else {
										if (line.length() > 4 && line.substring(0, 4).contentEquals("![p]")) {
											privateRoom(line.substring(4));
										}
										else {
											if (line.length() == 2 && line.substring(0, 2).contentEquals("!p")) {
												JPanel panel = new JPanel();
												TextField inputPassword = new TextField(30);
												inputPassword.setEchoChar('*');
												panel.add(inputPassword);
												JOptionPane.showMessageDialog(frame, panel, 
														"enter Room Password", JOptionPane.PLAIN_MESSAGE);
												String password = inputPassword.getText();
												System.out.println(password);
												out.println(password);
											}
											else {
												if (line.length() > 2 && line.substring(0, 2).contentEquals("!p")) {
													int index = Integer.valueOf(line.substring(2));
													rooms.setSelectedIndex(index);
													outputPanel.append("You entered the wrong Password!\n");
												}
												else {
													System.out.println("Unknown Command " + line);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				 else {
						// just output the rest
						outputPanel.append(line + "\n");
					}
			}
		});
		t.start();
	}
	
	private void privateRoom(String message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (message.length() > 7 && message.substring(0, 7).contentEquals("create!")) {
					// create and add new Frame
					ClientPrivateFrame newPrivateChat = new ClientPrivateFrame(frame, 
							frame.colorScheme, message.substring(7), myName, out);
					privateRooms.add(newPrivateChat);
				}
				if (message.length() > 6 && message.substring(0, 6).contentEquals("close!")) {
					// close and remove private frame
					String otherName = message.substring(6);
					for (ClientPrivateFrame privateFrame : privateRooms) {
						if (privateFrame.getName().contentEquals(otherName)) {
							privateFrame.closeWindow();
							privateRooms.remove(privateFrame);
							break;
						}
					}
				}
				else {
					String name = "";
					int index = 0;
					for (int i = 0; i < message.length(); i++) {
						if (message.substring(i, i+1).contentEquals("!")) { index = i; break; }
						else { name = name + message.substring(i, i+1); }
					}
					for (ClientPrivateFrame privateframe : privateRooms) {
						if (privateframe.getName().contentEquals(name)) { 
							privateframe.writeInput(message.substring(index + 1));
						}
					}
				}
			}
		}); t.start();
	}
	
	public void closePrivateRoom(ClientPrivateFrame privateFrame) {
		/*
		 * remove room from private room array list
		 */
		privateRooms.remove(privateFrame);
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
								// we need to change the selected User as well
								selectedUser = listModelOnline.getElementAt(i);
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
			else {
				// change name 
				if (line.substring(5, 6).contentEquals("n")) {
					String[] names = line.substring(6).split(",");
					// names[0] is old name
					int index = listModelRooms.indexOf(names[0]);
					listModelRooms.setElementAt(names[1], index);
				}
				else {
					if (line.substring(5, 6).contentEquals("r")) {
						// remove room
						rooms.setSelectedIndex(0);
						listModelRooms.removeElement(line.substring(6));
					}
				}
			}
		}
	}
		
	// 5. Action, Key and Mouselisteners
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
		System.exit(1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// listener if online User selected. just show the popup menu
		if (e.getSource() == onlineUsers) {
			if (selectedUser == null) {}
			else { pop.show(onlineUsers, e.getX(), e.getY()); }
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// popup Menu options
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (e.getSource() == seeProfil) {
					String name = "", nickname = "";
					int index = 0;
					while (index < selectedUser.length()) {
						if (selectedUser.charAt(index) == '(') {
							nickname = selectedUser.substring(index + 1, selectedUser.length() - 1);
							break;
						}
						else { name = name + selectedUser.charAt(index); index++; }
					}
					JOptionPane.showMessageDialog(frame, "Name: " + name + "\nNickname: " + nickname);
				}
				if (e.getSource() == startChat) {
					String name = "";
					int index = 0;
					// get selected name
					while (index < selectedUser.length()) {
						if (selectedUser.charAt(index) == '(') { break; }
						else { name = name + selectedUser.charAt(index); index++; }
					}
					name = name.substring(0, name.length() - 1);
					// check if chat already exist
					boolean exist = false;
					for (ClientPrivateFrame clientFrame : privateRooms) {
						if (clientFrame.getName().contentEquals(name)) { exist = true; break; }
					}
					if (exist == false) {
						if (name.contentEquals(myName)) {}
						else {
							out.println("![p]create!" + name);
							ClientPrivateFrame newPrivateChat = new ClientPrivateFrame(frame, 
									frame.colorScheme, name, myName, out);
							privateRooms.add(newPrivateChat);
						}
					}
				}
			}
		});
		t.start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	// 6. set color
	private void setColor(Color[] scheme) {
		/*
		 * sets color for almost every component and borders
		 */
		Color menu = scheme[0];
		Color background = scheme[1];
		Color textfield = scheme[2];
		Color button = scheme[3];
		Color foreground = scheme[4];
		
		finalPanel.setBackground(background);
		leftPanel.setBackground(background);
		rowPanel.setBackground(background);
		rightPanel.setBackground(background);
		nickname.setBackground(background);
		onlinePanel.setBackground(background);
		roomPanel.setBackground(background);
		chatRooms.setBackground(background);
		// label
		online.setBackground(background);
		online.setForeground(foreground);
		room.setBackground(background);
		room.setForeground(foreground);
		changeNickname.setBackground(background);
		changeNickname.setForeground(foreground);
		// menu
		menubar.setBackground(menu);
		languageMenu.setBackground(menu);
		languageMenu.setForeground(foreground);
		colorTheme.setBackground(menu);
		colorTheme.setForeground(foreground);
		english.setBackground(menu);
		english.setForeground(foreground);
		german.setBackground(menu);
		german.setForeground(foreground);
		spanish.setBackground(menu);
		spanish.setForeground(foreground);
		light.setBackground(menu);
		light.setForeground(foreground);
		blue.setBackground(menu);
		blue.setForeground(foreground);
		dark.setBackground(menu);
		dark.setForeground(foreground);		
		// textfields and lists
		insert.setBackground(textfield);
		insert.setForeground(foreground);
		outputPanel.setBackground(textfield);
		outputPanel.setForeground(foreground);
		insertNickname.setBackground(textfield);
		insertNickname.setForeground(foreground);
		// lists
		onlineUsers.setBackground(textfield);
		rooms.setBackground(textfield);
		onlineUsers.setForeground(foreground);
		rooms.setForeground(foreground);
		// Buttons
		sendMessage.setBackground(button);
		sendNickname.setBackground(button);
		createRoom.setBackground(button);
		sendMessage.setForeground(foreground);
		sendNickname.setForeground(foreground);
		createRoom.setForeground(foreground);
	}
}
