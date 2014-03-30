import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

public class Server {

	private ArrayList<Connection> senderPool;
	private ArrayList<Connection> receiverPool;
	private ArrayList<ServerMessage> history;
	private ArrayList<Client> clientList;

	public Server() {

	}

	public void acceptClients() {
		AcceptServer as = new AcceptServer();
		Thread t = new Thread(as);
		if(t.isAlive()){
			t.interrupt();
		}
		t.start();
	}

	public static void main(String[] args) {
		Server s = new Server();
		s.acceptClients();

	}

}

class AcceptServer implements Runnable {
	private int listeningPort = 4001;
	private ServerSocket listeningSocket;
	private boolean acceptClient = true;

	@Override
	public void run() {
       
	
		try {
			
			listeningSocket = new ServerSocket(listeningPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ready to accept connections on \nHost: ."
				+ listeningSocket.getInetAddress().getHostName()
				+ "\nPort: " + listeningSocket.getLocalPort());

		while (acceptClient) {
			try {

				Socket clientSocket = listeningSocket.accept();
				// Generate random free port
				ServerSocket server = new ServerSocket(0);
				int port = server.getLocalPort();
				server.close();

				Connection con = new Connection(clientSocket, port);

				System.out.println("Accepted a new connection.");
				System.out.println("User is on port: " + port);

			} catch (IOException e) {
				System.err.println("An error occurred while creating the I/O streams: the socket is closed or it is not connected.");
				e.printStackTrace();
			}
		}

	}
}