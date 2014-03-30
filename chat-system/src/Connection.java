import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Connection {
private Socket newConnection;
private int portNumber;
private String hostName;



	public Connection(Socket newConnection, int portNumber) {
	super();
	this.newConnection = newConnection;
	this.portNumber = portNumber;
	hostName = newConnection.getInetAddress().getHostName();
}

	public  BufferedReader createBufferedReader () {
		BufferedReader in = null;
		try {				            
			in = new BufferedReader( new InputStreamReader(newConnection.getInputStream()));	        
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					hostName);
		}

		return in;
	}

	public  PrintWriter createPrintWriter () {
		PrintWriter out = null;
		try {			
				out = new PrintWriter(newConnection.getOutputStream(), true);				


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
		return newConnection;
	}

	public void setNewConnection(Socket newConnection) {
		this.newConnection = newConnection;
	}
	
	

}