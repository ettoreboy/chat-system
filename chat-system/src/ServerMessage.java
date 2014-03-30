import java.net.InetAddress;
import java.util.Date;


public class ServerMessage {
private String text;
private String user;
private Date time;
private InetAddress sourceIp;
private int sourcePort;

public ServerMessage(String text, String user, Date time, InetAddress sourceIp,
		int sourcePort) {
	super();
	this.text = text;
	this.user = user;
	this.time = time;
	this.sourceIp = sourceIp;
	this.sourcePort = sourcePort;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public String getUser() {
	return user;
}

public void setUser(String user) {
	this.user = user;
}

public Date getTime() {
	return time;
}

public void setTime(Date time) {
	this.time = time;
}

public InetAddress getSourceIp() {
	return sourceIp;
}

public void setSourceIp(InetAddress sourceIp) {
	this.sourceIp = sourceIp;
}

public int getSourcePort() {
	return sourcePort;
}

public void setSourcePort(int sourcePort) {
	this.sourcePort = sourcePort;
}

@Override
public String toString() {
	return "ServerMessage [text=" + text + ", user=" + user + ", time=" + time
			+ ", sourceIp=" + sourceIp + ", sourcePort=" + sourcePort + "]";
}



	
}
