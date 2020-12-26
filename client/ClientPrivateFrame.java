package client;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class ClientPrivateFrame extends JFrame implements ActionListener, KeyListener {
	private JPanel finalPanel, rowPanel;
	private JLabel otherPerson;
	private JTextArea outputPanel;
	private JTextField input;
	private JButton send;
	
	private String otherName;
	private String myName;
	private PrintWriter out;
	
	Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	Font output = new Font("Arial", Font.BOLD + Font.ITALIC, 20);
	
	public ClientPrivateFrame(ClientFrame clientFrame, Color[] scheme, String otherUser, String name, PrintWriter out) {
		ClientPrivateFrame frame = this;
		this.otherName = otherUser;
		this.myName = name;
		this.out = out;
		this.setTitle("Your private Chat with " + otherUser);
		finalPanel = new JPanel(new BorderLayout(5, 5));
		finalPanel.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		rowPanel = new JPanel(new BorderLayout(5, 5));
		
		otherPerson = new JLabel("Your private Chat with " + otherUser);
		outputPanel = new JTextArea();
		outputPanel.setFont(output);
		outputPanel.setEditable(false);
		outputPanel.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(outputPanel);
		
		input = new JTextField();
		input.addKeyListener(this);
		send = new JButton("  send  ");
		send.addActionListener(this);
		rowPanel.add(input, BorderLayout.CENTER);
		rowPanel.add(send, BorderLayout.EAST);
		
		finalPanel.add(otherPerson, BorderLayout.NORTH);
		finalPanel.add(scroll, BorderLayout.CENTER);
		finalPanel.add(rowPanel, BorderLayout.SOUTH);
		
		this.add(finalPanel);
		this.setSize(600, 500);
		this.setVisible(true);
		this.setColor(scheme);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(frame, 
		            "Are you sure you want to close this window?", "Close Window?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
		        		clientFrame.closePrivateRoom(frame);
		        		out.println("![p]" + otherName + "!" + myName + " just closed this window.");
		        		frame.dispose();
		        	}
		    }
		});
	}
	
	public String getName() { return this.otherName; }
	
	public void setName(String name) { this.otherName = name; }
	
	public void writeInput(String input) { outputPanel.append(input + "\n"); }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == send) {
			if (input.getText() == null || input.getText() == "") {}
			else {
				outputPanel.append(myName + ": " + input.getText() + "\n");
				out.println("![p]" + otherName + "!" + myName + ": " + input.getText());
				input.setText(null);
			}	
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (e.getSource() == input) {
				if (input.getText() == null || input.getText() == "") {}
				else {
					outputPanel.append(myName + ": " + input.getText() + "\n");
					out.println("![p]" + otherName + "!" + myName + ": " + input.getText());
					input.setText(null);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	public void closeWindow() {
		JOptionPane.showMessageDialog(this, "The connection was closed by the Server",
				"Inane warning", JOptionPane.WARNING_MESSAGE);
		this.dispose();
	}

	private void setColor(Color[] scheme) {
		Color background = scheme[1];
		Color textfield = scheme[2];
		Color button = scheme[3];
		Color foreground = scheme[4];
		
		finalPanel.setBackground(background);
		rowPanel.setBackground(background);
		otherPerson.setForeground(foreground);
		
		outputPanel.setBackground(textfield);
		outputPanel.setForeground(foreground);
		input.setBackground(textfield);
		input.setForeground(foreground);
		
		send.setBackground(button);
		send.setForeground(foreground);
	}
}
