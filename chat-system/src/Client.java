import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection con;
	private String name;
	private final int port = 4001;
	private boolean clientAccepted;
	private InetAddress host;
	private ArrayList<String> history;
	final JTextArea textArea = new JTextArea(25, 80);
	final JTextField userInputField = new JTextField(53);

	public Client() throws UnknownHostException, IOException {
		super();
		this.con = null;
		this.name = null;
		clientAccepted = false;
		initialize();
		run();
	}

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException {

		new Client();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	private void initialize() throws UnknownHostException, IOException {
		history = new ArrayList<String>();

		JTextPane hostname = new JTextPane();
		hostname.setText("localhost");
		JTextPane username = new JTextPane();
		username.setText("Username..");
		hostname.setPreferredSize(new Dimension(150, 20));
		username.setPreferredSize(new Dimension(150, 20));
		JPanel inputPanel = new JPanel();
		inputPanel.add(username);
		inputPanel.add(Box.createHorizontalStrut(20));
		inputPanel.add(hostname);
		inputPanel.setPreferredSize(new Dimension(350, 40));

		// At initialization, ask the user for username and host.

		int answ = JOptionPane.showConfirmDialog(this, inputPanel,
				"Enter credentials", JOptionPane.YES_NO_OPTION);

		if (answ == JOptionPane.NO_OPTION) {
			System.exit(1);
		}

		else {
			this.setName(username.getText());

			if (hostname.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, hostname.getText()
						+ " is not a valid IP address.");
				System.exit(1);
			} else {
				try {
					host = InetAddress.getByName(hostname.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, host.toString()
							+ " is not correct");
					System.err
							.println("An error occurred while looking up for the Host.");
					e.printStackTrace();
					System.exit(1);

				}
				if (!host.isReachable(20)) {
					JOptionPane.showMessageDialog(null, host.toString()
							+ " is not reachable");
					System.exit(1);
				}

			}

			// Handshake with Server
			if (host != null) {
				try {
					con = new Connection(new Socket(host, port));
					this.setCon(con);
					clientAccepted = true;
					textArea.append("Welcome "
							+ this.getName()
							+ " you are connected to \nHostname: "
							+ con.getNewConnection().getInetAddress()
									.getHostName() + "\nPort: "
							+ con.getNewConnection().getPort() + "\n");
				} catch (IOException e) {
					System.err
							.println("An error occurred while creating the I/O streams: the socket is closed or it is not connected.");
					e.printStackTrace();
				}
			}
		}
		System.out.println("Connection established.");

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(585, 150));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		System.out.println("GUI instantiated");

		this.setLayout(new FlowLayout());
		this.getContentPane().add(userInputField, SwingConstants.CENTER);
		this.getContentPane().add(scrollPane, SwingConstants.CENTER);

		userInputField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String fromUser = userInputField.getText();

				if (fromUser != null) {
					Message s = new Message(fromUser, name, host, port);
					// textArea.append(s.toString());
					synchronized (history) {
						try {
							con.createPrintWriter().writeUTF(s.toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						textArea.setCaretPosition(textArea.getDocument().getLength());
						userInputField.setText("");
						history.add(s.toString());
					}
				}
			}

		});

		this.setVisible(true);
		this.setSize(600, 225);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

	}

	/**
	 * Receive messages
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException {
		DataInputStream in = con.createBufferedReader();
		while (true) {
			String fromServer = in.readUTF();
			if (fromServer != null) {

				System.out.println("\n\n");
				synchronized (history) {
					if (history.contains(fromServer)) {
						textArea.append("         Me: ");
						textArea.append(fromServer.split("\\[")[2].split("\\]")[0] + "\n");
						//textArea.append(fromServer + "\n");
						textArea.setCaretPosition(textArea.getDocument().getLength());
						userInputField.setText("");
					} else {
						textArea.append(fromServer.split("\\[")[1].split("\\]")[0] + ": " + fromServer.split("\\[")[2].split("\\]")[0] + "\n");
						//textArea.append(fromServer.toString() + "\n");
						textArea.setCaretPosition(textArea.getDocument().getLength());
						userInputField.setText("");
					}
					System.out.println("Tot. messages sent:" + history.size());
					System.out.println("Last Message:"
							+ history.get(history.size() - 1));

				}
			}
		}
	}

	// Getters and Setters
	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isClientAccepted() {
		return clientAccepted;
	}

	public void setClientAccepted(boolean clientAccepted) {
		this.clientAccepted = clientAccepted;
	}

}
