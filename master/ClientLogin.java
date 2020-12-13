package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;

public class ClientLogin extends JFrame implements ActionListener, KeyListener {
	/*
	 * login window for the client
	 */
	PrintWriter out;
	ClientFrame frame;
	JLabel user, pass;
	TextField name, word;
	JPanel mainPanel;
	JButton login;
	
	public ClientLogin(PrintWriter out, ClientFrame frame) {
		/*
		 * constructor: set mainpanel with a gridlaout
		 * and add login frame, key and acctionlistener
		 */
		this.out = out;
		this.frame = frame;
		// main panel with gridlayout
		mainPanel = new JPanel(new GridLayout(3, 0, 15, 15));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// call this.login which adds components to the main panel
		this.login();
		this.add(mainPanel);
		// set some more properties
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 300);
		this.setVisible(true);
		this.setResizable(false);
	}
	
	private void login() {
		/*
		 * creates label for login
		 * 2 labels, 2 textfields and one button
		 */
		// name panel with KeyListener
		JPanel username = new JPanel(new BorderLayout());
		user = new JLabel("Username: ");
		name = new TextField(20);
		name.addKeyListener(this);
		username.add(user, BorderLayout.WEST);
		username.add(name, BorderLayout.EAST);
		// password panel with KeyListener
		JPanel password = new JPanel(new BorderLayout());
		pass = new JLabel("Password: ");
		word = new TextField(20);
		word.setEchoChar('*');
		word.addKeyListener(this);
		password.add(pass, BorderLayout.WEST);
		password.add(word, BorderLayout.EAST);
		// login button with ActionListener
		login = new JButton("login");
		login.addActionListener(this);
		// add components to the main panel
		mainPanel.add(username, BorderLayout.NORTH);
		mainPanel.add(password, BorderLayout.AFTER_LAST_LINE);
		mainPanel.add(login, BorderLayout.SOUTH);
	}
	
	private void checkAndSend() {
		/*
		 * checks the name and password property
		 */
		// check if name empty
		if (name.getText().contentEquals("")) {
			JOptionPane.showMessageDialog(this,
					"'Name' can not be empty!",
					"WARNING!!!",
					JOptionPane.WARNING_MESSAGE);
		} 
		else {
			// check if password empty
			if (word.getText().contentEquals("")) {
				JOptionPane.showMessageDialog(this,
						"'Password' can not be empty!",
						"WARNING!!!",
						JOptionPane.WARNING_MESSAGE);
			}
			else {
				// print name and password to the server and kills this window
				out.println(name.getText());
				out.println(word.getText());
				this.dispose();
				frame.setVisible(true);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) { checkAndSend(); }

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) { checkAndSend(); }
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
