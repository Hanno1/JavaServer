package master;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;

public class ClientLogin extends JFrame implements ActionListener, KeyListener {
	PrintWriter out;
	ClientFrame frame;
	JLabel user, pass;
	TextField name, word;
	JPanel mainPanel;
	JButton login;
	
	public ClientLogin(PrintWriter out, ClientFrame frame) {
		this.out = out;
		this.frame = frame;
		mainPanel = new JPanel(new GridLayout(3, 0));
		this.login();
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 300);
		this.setVisible(true);
		this.setResizable(false);
	}
	
	private void login() {
		/*
		 * creates label for login */
		JPanel username = new JPanel(new BorderLayout());
		user = new JLabel("Username: ");
		name = new TextField(20);
		username.add(user, BorderLayout.WEST);
		username.add(name, BorderLayout.EAST);
		
		JPanel password = new JPanel(new BorderLayout());
		pass = new JLabel("Password: ");
		word = new TextField(20);
		word.setEchoChar('*');
		password.add(pass, BorderLayout.WEST);
		password.add(word, BorderLayout.EAST);
		
		login = new JButton("login");
		login.addActionListener(this);
		name.addKeyListener(this);
		word.addKeyListener(this);
		
		mainPanel.add(username, BorderLayout.NORTH);
		mainPanel.add(password, BorderLayout.AFTER_LAST_LINE);
		mainPanel.add(login, BorderLayout.SOUTH);
	}
	
	private void checkAndSend() {
		if (name.getText().contentEquals("")) {
			JOptionPane.showMessageDialog(this,
					"'Name' can not be empty!",
					"WARNING!!!",
					JOptionPane.WARNING_MESSAGE);
		} 
		else {
			if (word.getText().contentEquals("")) {
				JOptionPane.showMessageDialog(this,
						"'Password' can not be empty!",
						"WARNING!!!",
						JOptionPane.WARNING_MESSAGE);
			}
			else {
				out.println(name.getText());
				out.println(word.getText());
				this.dispose();
				frame.setVisible(true);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		checkAndSend();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			checkAndSend();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
