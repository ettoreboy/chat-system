import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {

	private ArrayList<ServerMessage> history;
	private Map<String, Connection> clients;

	private static Thread t;

	public Server() {
	}

	public void acceptClients() {
		AcceptServer as = new AcceptServer();
		t = new Thread(as);
		t.start();
	}

	// For Testing
	public static void main(String[] args) {
		Server s = new Server();
		s.acceptClients();

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
	private static Map<String, Connection> clients;

	public Map<String, Connection> getClients() {
		return clients;
	}

	public void setClients(Map<String, Connection> clients) {
		this.clients = clients;
	}

	@Override
	public void run() {

		try {
			if (listeningSocket != null) {
				listeningSocket.close();
			}

			listeningSocket = new ServerSocket(listeningPort);
			clients = new HashMap();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ready to accept connections on \nHost: ."
				+ listeningSocket.getInetAddress().getCanonicalHostName()
				+ "\nPort: " + listeningSocket.getLocalPort());

		while (acceptClient) {
			try {
					
				Socket clientSocket = listeningSocket.accept();
				Connection con = new Connection(clientSocket);
				String user = con.createBufferedReader().readLine();
				// Handshake here
				if (user != null) {
					// Generate random free port
					ServerSocket server = new ServerSocket(0);
					int port = server.getLocalPort();
					server.close();

					con.createPrintWriter().println(port);
					
					clientSocket.close();
					
					clients.put(user, con);
					System.out.println("Accepted a new connection.");
					System.out.println("User is on port: " + port);
					System.out.println("Username is: "+user);

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
}