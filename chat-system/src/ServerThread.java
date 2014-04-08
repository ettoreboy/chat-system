import java.io.*;
import java.net.*;

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
				
					String message = din.readUTF();
					if(!Server.getHistory().contains(message)){
						server.saveMessage(message);
					}
					System.out.println("Sending " + message);
					server.sendToAll(message);
				}
			
		} catch (EOFException ie) {
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			server.removeConnection(socket);
		}
	}
}