package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;

public class ClientFrame extends JFrame implements ActionListener {
	// given outputStream to the Server
	PrintWriter out;
	// define some Panel Variables
	JPanel finalPanel, leftPanel, rightPanel;
	JPanel rowPanel, nickname;
	// Variables for login
	JPanel mainPanel;
	JPasswordField word;
	// Rest 
	JTextField insert, userText, insertNickname;
	JPasswordField passwordText;
	JTextArea outputPanel;

	JButton login, sendMessage, sendNickname;
		
	JLabel userLabel, passwordLabel, changeName;
		
	JTextArea users = new JTextArea();
			
	public ClientFrame(PrintWriter printerOut) {
		/* Constructor for the chat frame. 
		 * The idea is to have a final Frame width a grid layout and inside are BorderLayouts
		 * So its a stacking of different Layouts to get the prefered Layout */
		this.out = printerOut;
		
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

		finalPanel.add(leftPanel, BorderLayout.WEST);
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
		users.setLineWrap(true);
		users.setEditable(false);
		users.setBorder(BorderFactory.createTitledBorder("all Users online"));
		// add nickname-Panel to rightPanel
		rightPanel.add(nickname, BorderLayout.NORTH);
		rightPanel.add(users, BorderLayout.CENTER);
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
		// Sending Button
		sendMessage = new JButton("Send Message");
		sendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
			        public void run() {
						if (insert.getText().contentEquals("")) {}
						else {
							out.println(insert.getText());
							insert.setText(null);
						}
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
	
	public void writeOut(String line) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("0");
				if (line.contentEquals("")) {}
				else {
					if (line.length() > 7 && line.substring(0, 7).contentEquals("!online")) {
						System.out.println("1");
						String[] names = line.substring(8).split(",");
						users.setText(null);
						for (String name : names) {
							System.out.println(name);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							users.append(name + "\n");
						}
						System.out.println("3");
					}
					else {
						outputPanel.append(line + "\n");
					}
				}
			}
		});
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {}
}
