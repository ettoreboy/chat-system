import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection {
private Socket socket;
private String hostName;



	public Connection(Socket newConnection) {
	super();
	this.socket = newConnection;
	hostName = newConnection.getInetAddress().getHostName();
}

	public  DataInputStream createBufferedReader () {
		DataInputStream in = null;
		try {				            
			in = new DataInputStream(socket.getInputStream());	        
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					hostName);
		}

		return in;
	}

	public  DataOutputStream createPrintWriter () {
		DataOutputStream out = null;
		try {			
				out = new DataOutputStream(socket.getOutputStream());				


		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					hostName);
			System.exit(1);
		}

		return out;
	}

	public Socket getNewConnection() {
		return socket;
	}

	public void setNewConnection(Socket newConnection) {
		this.socket = newConnection;
	}
	
	

}