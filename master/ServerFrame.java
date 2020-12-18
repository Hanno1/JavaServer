package master;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

public class ServerFrame extends JFrame implements ActionListener {
	JPanel rightSide, statusAndInformation, logPanel, statusPanel, informationPanel, taskPanel;
	JLabel logTitle;
	
	JTextArea outputPanel;
	
	JPanel finalLog, users;
	JTabbedPane tabs;
	
	public ServerFrame() {
		this.setTitle("Server!!!");
		this.setSize(1000, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// final Panel
		finalLog = new JPanel(new BorderLayout(20, 20));
		finalLog.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
		users = new JPanel();
		
		createLogPanel();
		
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.add("Server", finalLog);
		tabs.add("Chats", users);
		this.add(tabs);
		this.setVisible(true);
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
		
		createInformationsPanel();
		createStatusPanel();
		createTaskPanel();
		createServerLogPanel();
		
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
		outputPanel = new JTextArea(14, 20);
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
		JTextField taskText = new JTextField();
		taskText.setColumns(20);
		JButton taskButton = new JButton("OK");
		String comboBoxListe [] = {"change Servername" };
		JComboBox<String> chooseTask = new JComboBox<String>(comboBoxListe);

		northPanel.add(taskLabel, BorderLayout.WEST);
		northPanel.add(chooseTask, BorderLayout.EAST);
		southPanel.add(taskButton, BorderLayout.EAST);
		southPanel.add(taskText, BorderLayout.WEST);
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
	
	public static void main(String args[]) {
		ServerFrame frame = new ServerFrame();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void writeLog(String newLog) {
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String t = time.format(formatter) + ": ";
		outputPanel.append(t + newLog + "\n");
	}
}
