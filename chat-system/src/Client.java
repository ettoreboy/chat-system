import java.io.BufferedReader;
import java.io.PrintWriter;


public class Client {
private Connection con;
private String name;
private int initialPort = 4001;
private BufferedReader read;
private PrintWriter write;


public Client(Connection con, String name) {
	super();
	this.con = con;
	this.name = name;
	this.read = con.createBufferedReader();
	this.write = con.createPrintWriter();
}


public Connection getCon() {
	return con;
}
public void setCon(Connection con) {
	this.con = con;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getInitialPort() {
	return initialPort;
}
public void setInitialPort(int initialPort) {
	this.initialPort = initialPort;
}
public BufferedReader getRead() {
	return read;
}
public void setRead(BufferedReader read) {
	this.read = read;
}
public PrintWriter getWrite() {
	return write;
}
public void setWrite(PrintWriter write) {
	this.write = write;
}


}

