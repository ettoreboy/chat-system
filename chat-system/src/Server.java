import java.util.ArrayList;


public class Server {
	
	private int port;
	private ArrayList<Connection> senderPool;
    private ArrayList<Connection> receiverPool;
    private ArrayList<ServerMessage> history;

}
