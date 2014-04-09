import java.io.*;
import java.net.*;

/**
 * The class ServerThread represent a single Server Thread established for every client.
 * @author Ettore Ciprian
 * @author Marco Zanellati
 * @author Tobias Bernard
 *
 */
public class ServerThread extends Thread {

	private Server server;
	private Socket socket;

	/**
	 * Constructor ServerThread. Refers to server and a socket for the client.
	 * 
	 * @param server
	 * @param socket
	 */
	public ServerThread(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		server.sendHistory(this.socket);
		start();
	}

	/**
	 * Run instruction for a single server thread
	 */
	@Override
	public void run() {
		try {
			DataInputStream din = new DataInputStream(socket.getInputStream());
			while (true) {
				
				//Save messages on server history, if not duplicated.
					String message = din.readUTF();
					if(!Server.getHistory().contains(message)){
						server.saveMessage(message);
					}
					System.out.println("[SENT] " + message);
					server.sendToAll(message);
				}
			
		} catch (EOFException ie) {
		} catch (IOException ie) {
			ie.printStackTrace();
			//If ended, remove the client connection objct from the list
		} finally {
			server.removeConnection(socket);
		}
	}
}