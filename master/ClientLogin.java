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
	JPanel mainPanel, username, password;
	JButton login;
	// define colorscheme
	Color[] blueScheme = {new Color(0xbbc4f2), new Color(0xbdc6d4),
			new Color(0xdfe2ef), new Color(0xc8cff2), new Color(0x000000)};
	Color[] darkScheme = {new Color(0x102027), new Color(0x62727b), 
			new Color(0x37474f), new Color(0x00695c), new Color(0xffffff)};
	
	Font label = new Font("Arial", Font.BOLD + Font.ITALIC, 20);
	
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
		this.setColor(darkScheme[0], darkScheme[1], darkScheme[2], darkScheme[3], darkScheme[4]);
	}
	
	private void login() {
		/*
		 * creates label for login
		 * 2 labels, 2 textfields and one button
		 */
		// name panel with KeyListener
		username = new JPanel(new BorderLayout());
		user = new JLabel("Username: ");
		user.setFont(label);
		name = new TextField(30);
		name.setFont(label);
		name.addKeyListener(this);
		username.add(user, BorderLayout.WEST);
		username.add(name, BorderLayout.EAST);
		// password panel with KeyListener
		password = new JPanel(new BorderLayout());
		pass = new JLabel("Password: ");
		pass.setFont(label);
		word = new TextField(30);
		word.setEchoChar('*');
		word.setFont(label);
		word.addKeyListener(this);
		password.add(pass, BorderLayout.WEST);
		password.add(word, BorderLayout.EAST);
		// login button with ActionListener
		login = new JButton("login");
		login.setFont(label);
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
	
	public void setColor(Color background, Color label, Color textfield, Color button, Color text) {
		mainPanel.setBackground(background);
		
		username.setBackground(label);
		user.setForeground(text);
		password.setBackground(label);
		pass.setForeground(text);
		
		name.setBackground(textfield);
		name.setForeground(text);
		word.setBackground(textfield);
		word.setForeground(text);
		
		login.setBackground(button);
		login.setForeground(text);
	}
}
