import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ServerMessage {
private String text;
private String user;
private Timestamp time;
private InetAddress sourceIp;
private int sourcePort;

public ServerMessage(String text, String user, InetAddress sourceIp,
		int sourcePort) {
	super();
	this.text = text;
	this.user = user;
	SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyy HH:ss");
	Date d = new Date();
	f.format(d);
	this.time = new Timestamp(d.getTime());
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
	return "User["+user+"]Message["+text+"]Datetime["+time+"]SourceIp["
			+sourceIp+"]SourcePort["+sourcePort+"]";
}



	
}
