import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

public class Server implements ActionListener {
	// The ServerSocket we'll use for accepting new connections
	private static ServerSocket ss;
	private final int port = 4001;
	private static Hashtable<Socket, DataOutputStream> outClients = new Hashtable();
	private JPanel clientList;
	private JToolBar tools;
	private static JFrame main;
	private JButton restart;

	private static boolean listenFlag;
	private static Server server;
	private static ArrayList<String> history;

	/**
	 * Constructor. It launches the listen() method on port 4001.
	 * 
	 * @throws IOException
	 */
	protected Server() throws IOException {

		listen();
	}

	/**
	 * Singleton instantiation for one Server object at time.
	 * 
	 * @throws IOException
	 */
	public static void instance() throws IOException {
		server = new Server();
	}

	/**
	 * Infinite loop that listens ad accept new connections.
	 * 
	 * @throws IOException
	 */
	private void listen() throws IOException {
		ss = new ServerSocket(port);
		listenFlag = true;
		System.out.println("Server launched\n" + "Host: " + getMyIp() + "\n"
				+ "Listening on " + ss);
		setupGui();
		while (true) {
			while (listenFlag) {
				Socket s = ss.accept();

				System.out.println("New user connected from "
						+ s.getInetAddress());
				DataOutputStream dout = new DataOutputStream(
						s.getOutputStream());
				outClients.put(s, dout);
				getClientList();

				new ServerThread(this, s);
			}
		}
	}

	/**
	 * Construct and show the server gui.
	 */
	public void setupGui() {
		main = new JFrame("Consuela Server " + getMyIp());
		main.setLayout(new BorderLayout());
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getClientList();
		setupTools();
		main.getContentPane().add(tools, BorderLayout.NORTH);
		main.setSize(600, 300);
		main.setVisible(true);
		main.setResizable(false);

	}

	/**
	 * Get my ip for external connections
	 * 
	 * @return String
	 */
	public String getMyIp() {
		String ip = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return ip;
	}

	/**
	 * Get the output streams of all connected clients in a Enumeration
	 * 
	 * @return Enumeration
	 */
	Enumeration getOutputStreams() {
		return outClients.elements();
	}

	/**
	 * Send messages to all ServerThread
	 * 
	 * @param message
	 */
	public void sendToAll(String message) {
		synchronized (outClients) {
			for (Enumeration e = getOutputStreams(); e.hasMoreElements();) {

				DataOutputStream dout = (DataOutputStream) e.nextElement();
				try {
					dout.writeUTF(message);
				} catch (IOException ie) {
					System.out.println(ie);
				}
			}
		}
	}

	/**
	 * Remove a client connection from the hashMap of clients
	 * 
	 * @param Socket
	 *            s
	 */
	void removeConnection(Socket s) {
		synchronized (outClients) {
			System.out.println("Removing connection to " + s);
			outClients.remove(s);
			getClientList();
			try {
				s.close();
			} catch (IOException ie) {
				System.out.println("Error closing " + s);
				ie.printStackTrace();
			}
		}
	}
	
	/**
	 * Save messages in the Server for late client.
	 * @param s
	 */
	public void saveMessage(String s){
		if(history == null){
			history = new ArrayList<String>();
		}
		history.add(s);
	}
	
	/**
	 * Send all the history to one socket
	 * @param s
	 */
	public void sendHistory(Socket s){
		if (history != null && !history.isEmpty()){
		try {
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			for (String old : history) {
				System.out.println("Sending " + old);
				dout.writeUTF(old);
			}
		} catch (EOFException ie) {
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			
		}
		}
		
	}

	/**
	 * Graphical user interface for the server client list
	 */
	public void getClientList() {

		clientList = new JPanel();
		synchronized (outClients) {
			System.out.println("Total users: " + outClients.size());
			String[] clients = new String[outClients.size()];
			int pointer = 0;
			for (Enumeration e = outClients.keys(); e.hasMoreElements();) {
				Socket sock = (Socket) e.nextElement();
				clients[pointer] = "Client " + pointer + 1 + " " + sock;
				System.out.println(clients[pointer]);
				pointer++;

			}
			JList list = new JList(clients); // data has type Object[]
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			list.setVisibleRowCount(-1);
			JScrollPane listScroller = new JScrollPane(list);
			listScroller.setPreferredSize(new Dimension(585, 218));
			clientList.add(listScroller);
		}
		main.getContentPane().add(clientList, BorderLayout.CENTER);
		main.repaint();
		main.revalidate();

	}

	/**
	 * Set up the JToolBar for the Consuela server with some options
	 */
	public void setupTools() {
		tools = new JToolBar();
		restart = new JButton("Restart");
		restart.addActionListener(this);
		tools.add(restart);

	}

	/**
	 * Maps the actions for the JToolBar of the server
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		String type = source.getClass().getName();

		if (source.equals(restart)) { // first button clicked
			System.out.println("Restarting server...");
			synchronized (outClients) {
				for (Enumeration e1 = outClients.keys(); e1.hasMoreElements();) {

					Socket connection = (Socket) e1.nextElement();
					removeConnection(connection);

				}
				getClientList();
			}

		}

	}
	

	public static ArrayList<String> getHistory() {
		return history;
	}

	public static void setHistory(ArrayList<String> history) {
		Server.history = history;
	}

	/**
	 * Launch Consuela Server
	 * 
	 * @param args
	 * @throws Exception
	 */
	static public void main(String args[]) throws Exception {
		instance();
	}
}
