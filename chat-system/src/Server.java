import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
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
		        
				worker =  Executors.newSingleThreadScheduledExecutor();
	}

	public void acceptClients() {
		AcceptServer as = new AcceptServer();
		this.as = new Thread(as);
		this.as.start();

	}

	public void receiveMessages() {
		ReceiveServer rs = new ReceiveServer();
		this.rs = new Thread(rs);
		worker.schedule(this.rs, 2, TimeUnit.SECONDS);
	}

	public void sendMessages() {
		SendServer ss = new SendServer();
		this.ss = new Thread(ss);
		worker.schedule(this.ss, 2, TimeUnit.SECONDS);
	}
	
	public void closeConnections() {
		Consuela cleans = new Consuela();
		cs = new Thread(cleans);
		worker.schedule(this.cs, 3, TimeUnit.SECONDS);
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
		s.closeConnections();
		System.out.println("[Server]Consuela started cleaning.. Nono");

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
	int randomkey;
	private static Map<Integer, Connection> clients;

	public static Map<Integer, Connection> getClients() {
		return clients;
	}

	public void setClients(Map<Integer, Connection> clients) {
		this.clients = clients;
	}

	@Override
	public void run() {
        int clientsSize = 0;
		try {
			if (listeningSocket != null) {
				listeningSocket.close();
			}

			listeningSocket = new ServerSocket(listeningPort);
			clients = new HashMap<Integer, Connection>();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ready to accept connections on \nHost: "
				+ getMyIp() + "\nPort: " + listeningPort);

		while (acceptClient) {
			try {
				//listen to 4001 until a client knocks
				Socket clientSocket = listeningSocket.accept();
				Connection con = new Connection(clientSocket);
				String user = con.createBufferedReader().readLine();

				// Handshake here with code word
				if (user != null && user.contains("MarcoG")) {
					user = user.replaceAll("MarcoG", "");
					// Generate random free port
					ServerSocket server = new ServerSocket(0);
					int port = server.getLocalPort();
					
					// Send new port and listen to it.
					con.createPrintWriter().println(port);
					clientSocket = server.accept();
					Connection con2 = new Connection(clientSocket);
					clientsSize++;
					clients.put(clientsSize, con2);

				
					System.out.println("Accepted a new connection.");
					System.out.println("User is on port: " + port);
					System.out.println("Username is: " + user);
					
					Iterator<?> it = AcceptServer.getClients().entrySet()
							.iterator();
					Map.Entry<Integer, Connection> pair;				
					
					while (it.hasNext()) {
						
						 pair = (Map.Entry) it.next();
						 System.out.println("[ClientList]Client "+clientsSize+" on port "+pair.getValue().getNewConnection().getPort());
					
					}

				}

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
	private static Map<Timestamp, String> messages;
	private static Map<Timestamp, String> history;

	public static Map<Timestamp, String> getMessages() {
		return messages;
	}

	public static void setMessages(Map<Timestamp, String> messages) {
		ReceiveServer.messages = messages;
	}

	public static Map<Timestamp, String> getHistory() {
		return history;
	}

	public static void setHistory(Map<Timestamp, String> history) {
		ReceiveServer.history = history;
	}

	@Override
	public void run() {
		messages = new HashMap<Timestamp, String>();
		history = new HashMap<Timestamp, String>();
		while (true) {

			if (AcceptServer.getClients() != null) {
				Iterator<?> it = AcceptServer.getClients().entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, Connection> pair = (Map.Entry) it.next();
					String message = null;
					try {
						message = pair.getValue().createBufferedReader()
								.readLine();
					} catch (IOException e) {
						System.err.println(message
								+ " was read from socket "
								+ pair.getValue().getNewConnection()
										.getInetAddress() + " is invalid");
						e.printStackTrace();
					}
					// If a message is present save it
					if (message != null) {
						Date d = new Date();
						Timestamp t = new Timestamp(d.getTime());
						messages.put(t, message);
						history.put(t, message);
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
				Iterator it = ReceiveServer.getMessages().entrySet().iterator();
				Iterator clients = AcceptServer.getClients().entrySet()
						.iterator();
				while (it != null && it.hasNext()) {
					Map.Entry<Timestamp, String> message = (Map.Entry) it
							.next();
					System.out.println("[SendServer]Message sent: \n"
							+ message.getValue() + "\n");

					while (clients.hasNext()) {
						Map.Entry<Integer, Connection> client = (Map.Entry) clients
								.next();
						client.getValue().createPrintWriter()
								.println(message.getValue());
					}
					ReceiveServer.getMessages().remove(message.getKey());

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

				Iterator clients = AcceptServer.getClients().entrySet()
						.iterator();
				while (clients.hasNext()) {
					Map.Entry<InetAddress, Connection> client = (Map.Entry) clients
							.next();
					if(client.getValue().getNewConnection().isClosed()){
						AcceptServer.getClients().remove(client.getKey());
						System.out.println("[Server]Consuela closed:" +client.getKey()+"on port: " +client.getValue().getNewConnection().getLocalPort()+"\n");
					}

				}

			}
		}
	}

}
