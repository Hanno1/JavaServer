package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	JPanel rowPanel, nickname;
	// Textfields (insert text and insert new nickname
	JTextField insert, insertNickname;
	// display output from the server
	JTextArea outputPanel;
	// various buttons :D
	JButton sendMessage, sendNickname, createRoom;
	
	// list for rooms and users with corresponding listmodels to add elements	
	JList<String> onlineUsers, rooms;
	DefaultListModel<String> listModelOnline, listModelRooms;
			
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
		
		// We got one finalpanle which contains a left, right panel and room panel
		// The panels are created via the differnet function (look there for documentation)
		finalPanel = new JPanel(new BorderLayout());
		leftPanel = new JPanel(new BorderLayout());
		this.createLeftPanel();
		rightPanel = new JPanel(new BorderLayout());
		this.createRightPanel();
		roomPanel = new JPanel(new BorderLayout());
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
	
	private void createRightPanel() {
		/* creates an Panel for the user to change the nickname 
		 * and to display all online users in the chatroom
		 * consists of an Label, a Button to commit changes
		 * and a list for the users
		 */
		// create nickname Panel
		nickname = new JPanel(new BorderLayout());
		// create insert Field
		insertNickname = new JTextField(10);
		insertNickname.setBorder(BorderFactory.createTitledBorder("insertNickname"));
		insertNickname.addKeyListener(this);
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
		nickname.add(insertNickname, BorderLayout.WEST);
		nickname.add(sendNickname, BorderLayout.EAST);
		nickname.setBorder(BorderFactory.createTitledBorder("Nickname panel"));
		// add list for all users (using JList and default Listmodel)
		listModelOnline = new DefaultListModel<String>();
		onlineUsers = new JList<String>(listModelOnline);
		onlineUsers.setBorder(BorderFactory.createTitledBorder("all Users online"));
		// add nickname-Panel and list to the rightPanel
		rightPanel.add(nickname, BorderLayout.NORTH);
		rightPanel.add(onlineUsers, BorderLayout.CENTER);
	}
	
	private void createLeftPanel() {
		/*
		 * basically creates the left Panel: A Textarea for the output Text, 
		 * a Input and a Button to send Messages
		 */
		// create Output TextArea
		outputPanel = new JTextArea(14, 20);
		outputPanel.setLineWrap(true);
		outputPanel.setBorder(BorderFactory.createTitledBorder("JTextArea"));
		// sets Editable and Visibility of the output Panel
		outputPanel.setEditable(false);
		outputPanel.setVisible(true);
		// we need to put the outputPanel into an scroll panel
		JScrollPane scroll = new JScrollPane(outputPanel);
		
		// The row Panel should contain the insert and the button to send messages
		rowPanel = new JPanel(new BorderLayout());
		// text Field insert
		insert = new JTextField(19);
		insert.setBorder(BorderFactory.createTitledBorder("Insert"));
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
		
		leftPanel.setBorder(BorderFactory.createTitledBorder("left!"));
	}
	
	private void createRoomPanel() {
		/*
		 * consists of a list for all different rooms and a button to create a new room
		 */
		// basic panel
		JPanel chatRooms = new JPanel(new BorderLayout());
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
		chatRooms.add(rooms);
		chatRooms.add(createRoom, BorderLayout.SOUTH);
		chatRooms.setVisible(true);
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
							System.out.println("1");
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
	
}
