import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;

public class Client {
	private Connection con;
	private String name;
	private final int initialPort = 4001;
	private BufferedReader read;
	private PrintWriter write;
	private boolean clientAccepted;
	private InetAddress host;

	private JFrame frame;

	public Client() throws UnknownHostException, IOException {
		super();
		this.con = null;
		this.name = null;
		this.read = null;
		this.write = null;
		clientAccepted = true;
		initialize();
	}

	public Client(Connection con, String name) {
		super();
		this.con = con;
		this.name = name;
		this.read = con.createBufferedReader();
		this.write = con.createPrintWriter();
		clientAccepted = true;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	private void initialize() throws UnknownHostException, IOException {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		final JTextArea textArea = new JTextArea(5, 30);
		JTextPane hostname = new JTextPane();
		hostname.setText("localhost");
		JTextPane username = new JTextPane();
		username.setText("Username..");
		JPanel inputPanel = new JPanel();
		inputPanel.add(username);
		inputPanel.add(Box.createHorizontalStrut(15));
		inputPanel.add(hostname);

		int answ = JOptionPane.showConfirmDialog(frame, inputPanel,
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
				if (!host.isReachable(5)) {
					JOptionPane.showMessageDialog(null, host.toString()
							+ " is not reachable");
					System.exit(1);
				}

			}

			Connection con = new Connection(new Socket(host, initialPort));
			this.setCon(con);
			this.setRead(con.createBufferedReader());
			this.setWrite(con.createPrintWriter());

			while (clientAccepted) {
				this.getWrite().println(this.getName());
				System.out.println(this.getName());

				if (this.getRead().readLine() != null) {

					String newport = this.getRead().readLine();
					System.out.println("newport");
					int port = Integer.valueOf(newport);
					try {
						con = new Connection(new Socket(host, port));
						this.setCon(con);
						this.setRead(con.createBufferedReader());
						this.setWrite(con.createPrintWriter());
						clientAccepted = false;
						textArea.append("Username: "
								+ this.getName()
								+ " Hostname: "
								+ con.getNewConnection().getInetAddress()
										.getHostName() + "Port: "
								+ con.getNewConnection().getPort());
					} catch (IOException e) {
						System.err
								.println("An error occurred while creating the I/O streams: the socket is closed or it is not connected.");
						e.printStackTrace();
					}
				}
			}
			System.out.println("Connection established.");

			// We put the TextArea object in a Scrollable Pane
			JScrollPane scrollPane = new JScrollPane(textArea);

			// In order to ensure the scroll Pane object appears in your window,
			// set a preferred size to it!
			scrollPane.setPreferredSize(new Dimension(380, 100));

			// Lines will be wrapped if they are too long to fit within the
			// allocated width
			textArea.setLineWrap(true);

			// Lines will be wrapped at word boundaries (whitespace) if they are
			// too long to fit within the allocated width
			textArea.setWrapStyleWord(true);

			// Assuming this is the chat client's window where we read text sent
			// out
			// and received, we don't want our Text Area to be editable!
			textArea.setEditable(false);

			// We also want a vertical scroll bar on our pane, as text is added
			// to it
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			// Now let's just add a Text Field for user input, and make sure our
			// text area stays on the last line as subsequent lines are
			// added and auto-scrolls
			final JTextField userInputField = new JTextField(30);
			userInputField.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					// We get the text from the textfield
					String fromUser = userInputField.getText();

					if (fromUser != null) {
						// We append the text from the user
						textArea.append("Asim: " + fromUser + "\n");

						// The pane auto-scrolls with each new response added
						textArea.setCaretPosition(textArea.getDocument()
								.getLength());
						// We reset our text field to "" each time the user
						// presses Enter
						userInputField.setText("");
					}
				}
				
			});
			frame.setLayout(new FlowLayout());
			// adds and centers the text field to the frame
			frame.add(userInputField, SwingConstants.CENTER);
			// adds and centers the scroll pane to the frame
			frame.add(scrollPane, SwingConstants.CENTER);
		}
	}

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

	public int getInitialPort() {
		return initialPort;
	}

	public BufferedReader getRead() {
		return read;
	}

	public void setRead(BufferedReader read) {
		this.read = read;
	}

	public PrintWriter getWrite() {
		return write;
	}

	public void setWrite(PrintWriter write) {
		this.write = write;
	}

	public boolean isClientAccepted() {
		return clientAccepted;
	}

	public void setClientAccepted(boolean clientAccepted) {
		this.clientAccepted = clientAccepted;
	}

}
