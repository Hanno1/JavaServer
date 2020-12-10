package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ClientFrame extends JFrame implements ActionListener, KeyListener {
	// given outputStream to the Server
	PrintWriter out;
	ClientFrame frame;
	// define some Panel Variables
	JPanel finalPanel, leftPanel, rightPanel, roomPanel;
	JPanel rowPanel, nickname;
	// Variables for login
	JPanel mainPanel;
	JPasswordField word;
	// Rest 
	JTextField insert, userText, insertNickname;
	JPasswordField passwordText;
	JTextArea outputPanel;

	JButton login, sendMessage, sendNickname, createRoom;
		
	JLabel userLabel, passwordLabel, changeName;
		
	JTextArea users = new JTextArea();
	
	JList<String> onlineUsers, rooms;
	DefaultListModel<String> listModelOnline, listModelRooms;
			
	public ClientFrame(PrintWriter printerOut) {
		/* Constructor for the chat frame. 
		 * The idea is to have a final Frame width a grid layout and inside are BorderLayouts
		 * So its a stacking of different Layouts to get the prefered Layout */
		this.out = printerOut;
		ClientFrame frame = this;
		
		this.setVisible(false);
		
		// set the layout of the frame to a Border Layout
		this.setLayout(new BorderLayout());
		this.setTitle("The ultimate Java Chat!");
		
		// We got one panle which contains a left and a right panel
		finalPanel = new JPanel(new BorderLayout());
		leftPanel = new JPanel(new BorderLayout());
		this.createLeftPanel();
		rightPanel = new JPanel(new BorderLayout());
		this.createRightPanel();
		roomPanel = new JPanel(new BorderLayout());
		this.createRoomPanel();
		
		finalPanel.add(roomPanel, BorderLayout.WEST);
		finalPanel.add(leftPanel, BorderLayout.CENTER);
		finalPanel.add(rightPanel, BorderLayout.EAST);

		this.setLayout(new BorderLayout());
		this.add(finalPanel, BorderLayout.CENTER);
		// set Size and closing Operator
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1130, 500);
		this.setResizable(false);
	}
	
	private void createRightPanel() {
		/* creates an Panel for the user to change the nickname
		 * consists of an Label, a Textfield and a Button to commit changes */
		// create Panel
		nickname = new JPanel(new BorderLayout());
		// create insert Field
		insertNickname = new JTextField(10);
		insertNickname.setBorder(BorderFactory.createTitledBorder("insertNickname"));
		insertNickname.addKeyListener(this);
		// create Button
		sendNickname = new JButton("Change Name");
		sendNickname.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (insertNickname.getText().contentEquals("")) {}
				else {
					out.println("!nickname" + insertNickname.getText());
					insertNickname.setText(null);
				}
			}
		});
		// add Panels
		nickname.add(insertNickname, BorderLayout.WEST);
		nickname.add(sendNickname, BorderLayout.EAST);
		nickname.setBorder(BorderFactory.createTitledBorder("Nickname panel"));
		// add textArea for all users -- will be a list later on
		listModelOnline = new DefaultListModel<String>();
		onlineUsers = new JList<String>(listModelOnline);
		onlineUsers.setBorder(BorderFactory.createTitledBorder("all Users online"));
		// add nickname-Panel to rightPanel
		rightPanel.add(nickname, BorderLayout.NORTH);
		rightPanel.add(onlineUsers, BorderLayout.CENTER);
	}
	
	private void createLeftPanel() {
		/*
		 * basically creates the left Panel: A Textarea for the output Text, 
		 * a Input and a Button to send Messages */
		// create Output TextArea
		outputPanel = new JTextArea(14, 10);
		outputPanel.setLineWrap(true);
		outputPanel.setBorder(BorderFactory.createTitledBorder("JTextArea"));
		// sets Editable and Visibility of the output Panel
		outputPanel.setEditable(false);
		outputPanel.setVisible(true);
		
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
				Thread t = new Thread(new Runnable() {
					@Override
			        public void run() {
						if (insert.getText().contentEquals("")) {}
						if (insert.getText().substring(0, 1).contentEquals("!")) {}
						else {
							out.println(insert.getText());
						}
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
		leftPanel.add(scroll, BorderLayout.NORTH);
		leftPanel.add(rowPanel, BorderLayout.SOUTH);
		
		leftPanel.setBorder(BorderFactory.createTitledBorder("left!"));
	}
	
	private void createRoomPanel() {
		JPanel chatRooms = new JPanel(new BorderLayout());
		listModelRooms = new DefaultListModel<String>();
		rooms = new JList<String>(listModelRooms);
		rooms.setSelectedIndex(0);
		rooms.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                	int index = rooms.getSelectedIndex();
                	out.println("!roomc" + listModelRooms.get(index));
                }
            }
        });
		createRoom = new JButton("create new Room!");
		createRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(frame, "New Chatroom name: ", "create Chatroom",
						JOptionPane.QUESTION_MESSAGE);
				if (name == null || name.contentEquals("")) {}
				else {
					out.println("!rooma" + name);
				}
			}
		});
		chatRooms.add(rooms);
		chatRooms.add(createRoom, BorderLayout.SOUTH);
		chatRooms.setVisible(true);
		roomPanel.add(chatRooms, BorderLayout.CENTER);
	}
	
	public void writeOut(String line) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("line:" + line);
				if (line.contentEquals("")) {}
				else {
					if (line.length() > 9 && line.substring(0, 7).contentEquals("!online")) {
						updateOnline(line);
					}
					else {
						System.out.println("0");
						if (line.length() > 5 && line.substring(0, 5).contentEquals("!room")) {
							System.out.println("1");
							updateRooms(line);
						}
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
		String user = line.substring(8);
		String action = line.substring(7,8);
		if (action.contentEquals("e")) {
			// first we need to reset
			listModelOnline.removeAllElements();
			// we are new so we need to write out everything
			String[] names = user.split(",");
			for (String name : names) {
				listModelOnline.addElement(name);
			}
		}
		else {
			if (action.contentEquals("r")) {
				// remove user
				try {
					listModelOnline.removeElement(user);
				}
				finally {}
			}
			else {
				if (action.contentEquals("a")) {
					// add user
					listModelOnline.addElement(user);
				}
				else {
					// change nickname
					try {
						String[] newName = user.split("%");
						String newNickname = newName[0];
						for (int i = 0; i < listModelOnline.size(); i++) {
							if (listModelOnline.getElementAt(i).contentEquals(newName[1])) {
								listModelOnline.setElementAt(newNickname, i);
								break;
							}
						}
					}
					finally {}
				}
			}
		}
	}
	
	private void updateRooms(String line) {
		boolean first = false;
		if (listModelRooms.size() == 0) {
			first = true;
		}
		if (line.substring(5, 6).contentEquals("e")) {
			// first we need to reset the list
			listModelRooms.removeAllElements();
			String[] names = line.substring(7).split(",");
			for (String name : names) {
				listModelRooms.addElement(name);
			}
			if (first) {
				rooms.setSelectedIndex(0);
			}
		}
		else {
			if (line.substring(5, 6).contentEquals("a")) {
				listModelRooms.addElement(line.substring(6));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
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
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
