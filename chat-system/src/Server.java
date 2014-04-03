import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Server class implements multi-thread modules
 * 
 * @author ettore
 * 
 */
public class Server {
	private final ScheduledExecutorService worker;
	private Thread as, rs, ss, cs;

	/**
	 * Server constructor. It handles the threads for the different modules.
	 */
	public Server() {

		worker = Executors.newSingleThreadScheduledExecutor();
	}

	public void acceptClients() {
		AcceptServer as = new AcceptServer();
		this.as = new Thread(as);
		this.as.start();

	}

	public void receiveMessages() {
		ReceiveServer rs = new ReceiveServer();
		this.rs = new Thread(rs);
		// worker.schedule(this.rs, 1, TimeUnit.SECONDS);
		this.rs.start();
	}

	public void sendMessages() {
		SendServer ss = new SendServer();
		this.ss = new Thread(ss);
		// worker.schedule(this.ss, 2, TimeUnit.SECONDS);
		this.ss.start();
	}

	public void closeConnections() {
		Consuela cleans = new Consuela();
		this.cs = new Thread(cleans);
		// worker.schedule(this.cs, 2, TimeUnit.SECONDS);
		this.cs.start();
	}

	// Launch Server
	public static void main(String[] args) {
		Server s = new Server();
		s.acceptClients();
		System.out.println("[Server]Accept Module initialized.");
		s.receiveMessages();
		System.out.println("[Server]Receive Module initialized.");
		s.sendMessages();
		System.out.println("[Server]Send Module initialized.");
		// s.closeConnections();
		// System.out.println("[Server]Consuela started cleaning.. Nono");

	}

}

// Classes to be run on a different thread.
/**
 * It accepts client connection and does the first handshake
 * 
 * @author ettore
 * 
 */
class AcceptServer implements Runnable {
	private int listeningPort = 4001;
	private static ServerSocket listeningSocket;
	private boolean acceptClient = true;

	private static List<Connection> clients;

	public static List<Connection> getClients() {
		return clients;
	}

	public static void setClients(ArrayList<Connection> clients) {
		AcceptServer.clients = clients;
	}

	@Override
	public void run() {
		try {
			if (listeningSocket != null) {
				listeningSocket.close();
			}

			listeningSocket = new ServerSocket(listeningPort);
			clients = new CopyOnWriteArrayList<Connection>();
			;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ready to accept connections on \nHost: "
				+ getMyIp() + "\nPort: " + listeningPort);

		while (acceptClient) {
			try {
				// listen to 4001 until a client knocks
				Socket clientSocket = listeningSocket.accept();
				Connection con = new Connection(clientSocket);

				clients.add(con);
				System.out.println("Total clients now: "
						+ AcceptServer.getClients().size());

			} catch (IOException e) {
				System.err
						.println("An error occurred while creating the I/O streams: the socket is closed or it is not connected.");
				e.printStackTrace();
			}
		}
		try {
			listeningSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get your current ip
	 * 
	 * @return
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
}

/**
 * Receive messages from all clients and store them in a HashMap as a Stack
 * 
 * @author ettore
 * 
 */
class ReceiveServer implements Runnable {
	private static List<String> messages;
	private static List<String> history;

	public static List<String> getMessages() {
		return messages;
	}

	public static void setMessages(ArrayList<String> messages) {
		ReceiveServer.messages = messages;
	}

	public static List<String> getHistory() {
		return history;
	}

	public static void setHistory(ArrayList<String> history) {
		ReceiveServer.history = history;
	}

	@Override
	public void run() {
		messages = new CopyOnWriteArrayList<String>();
		history = new CopyOnWriteArrayList<String>();
		while (true) {

			if (AcceptServer.getClients() != null
					&& !AcceptServer.getClients().isEmpty()) {
				List<Connection> clientList = AcceptServer.getClients();

				for (Connection client : clientList) {

					// If the connection is closed remove the client
					//System.out.println(client.getNewConnection()
							//.getInetAddress());
					//System.out.println(client.getNewConnection().isConnected());
					//System.out.println(client.getNewConnection().isBound());
					//System.out.println(client.getNewConnection().isInputShutdown());
					String message = null;
					try {
						if (client.getNewConnection() != null) {
							// Read message from object client Connection
							message = client.createBufferedReader().readUTF();
						} else {
							AcceptServer.getClients().remove(client);
						}
					} catch (IOException e) {
						System.err.println(message + " was read from socket "
								+ client.getNewConnection().getInetAddress()
								+ " is invalid");
						e.printStackTrace();

					}
					// If a message is present save it
					if (message != null) {
						messages.add(message);
						history.add(message);
						System.out
								.println("[ReceiveServer]Message received: \n"
										+ message + "\n");
					}
				}
			}

		}

	}
}

/**
 * Send messages to all clients
 * 
 * @author ettore
 * 
 */
class SendServer implements Runnable {

	@Override
	public void run() {

		while (true) {

			if (AcceptServer.getClients() != null) {
				Iterator<String> messages = ReceiveServer.getMessages()
						.iterator();
				Iterator<Connection> clients = AcceptServer.getClients()
						.iterator();
				while (messages.hasNext()) {
					String message = messages.next();
					System.out.println("[SendServer]Message sent: \n" + message
							+ "\n");

					while (clients.hasNext()) {
						Connection client = clients.next();
						try {
							client.createPrintWriter().writeUTF(
									message.toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					ReceiveServer.getMessages().remove(message);

				}

			}
		}

	}
}

/**
 * Close all unused connections/clients like a Cleaning Lady
 */
class Consuela implements Runnable {

	@Override
	public void run() {

		while (true) {

			if (AcceptServer.getClients() != null) {

				Iterator<Connection> clients = AcceptServer.getClients()
						.iterator();
				while (clients.hasNext()) {
					Connection client = clients.next();
					if (client.getNewConnection().isClosed()) {
						AcceptServer.getClients().remove(client);
						System.out.println("[Server]Consuela closed:"
								+ client.getNewConnection().getInetAddress()
										.getHostName());
					}

				}

			}
		}
	}

}
