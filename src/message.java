

public class message{
	 String sourceHost;
	 int sourcePort;
	 String childHost;
	 int childPort;
	 int msgContent;
	 boolean acknowledgement = false;
	 String type;

	 message(String shost, int sport, String chost,int cport, String type, int content){
	 	this.sourceHost = shost;
	 	this.sourcePort = sport;
	 	this.childHost = chost;
	 	this.childPort = cport;
	 	this.type=type;
	 	this.msgContent = content;
	 }
	 public String toString(){
	 	return "source,"+sourceHost+","+sourcePort+","+childHost+","+childPort+","+msgContent+","+type;
	 }

}