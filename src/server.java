import java.io.*;
import java.util.*;
import java.lang.Iterable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.util.Random;


public class server implements Runnable {
	public static String host;
	public static int port;
    public static Node node ;
  
	public server (Node n){
		this.host = n.host;
		this.port = n.port;
		this.node = n;
        // numOfMsgsRcvd = 0;
	}
	public server(String host, int port){
		this.host=host;
		this.port=port;
	}
	private boolean isStopped = false;
	
	private boolean isStopped(){
		return this.isStopped;
	}
	
	/**
	 * Implementation of the run method for Runnable class.
	 * Creating sctpServerSocket on all nodes connected.
	 */
	@Override
	
	public void run() {
		try {
			runAsServer(this.host, this.port);
		}catch (Exception e){
			System.out.println(e);
		}
		
	}
	/**
	 * Method to create an SCTPserverSocket for each machine connected from shell script.
	 * @Exception IOException
	 * @return null
	 */
	private void runAsServer(String host, int port) throws IOException, ClassNotFoundException {
        
		// System.out.println("Creating server on :"+host+"::"+port);
        if ((host != null) && (port != 0)) {
            try {
                InetSocketAddress serverSocket = new InetSocketAddress(host, port);
                SctpServerChannel sctpServerChannel = SctpServerChannel.open().bind(serverSocket);
                while (!isStopped()) {
                	MultiThreadedServer mts = new MultiThreadedServer(sctpServerChannel.accept(), node);
                    new Thread(mts).start();
                }
            } catch (IOException e) {
                throw new RuntimeException("Node: runAsServer: Cannot open port on " + this.port, e);
            }
        } else {
            System.out.println("Node: runAsServer: Error: Hostname and port are not set.");
            System.out.println("Node: runAsServer: Exiting program...");
            System.exit(1);
        }
    }

 //    /**
 //     * Method to deserialize the message from sctp channel.
 //     *
 //     * @param bytes   Incoming message stream in bytes.
 //     * @retrun Object Deserialized object.
 //     */
 //    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
 //        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
 //            try(ObjectInputStream o = new ObjectInputStream(b)){
 //                return o.readObject();
 //            }
 //        }
 //    }

	// /**
	//  * Method to process the sctpChannel connected to the port on the machine.
	//  * 
	//  * @return null
	//  */
	
	// private void processChannel (SctpChannel channel) throws IOException, ClassNotFoundException {
		
 //        try {
 //            ByteBuffer bf = ByteBuffer.allocate(640000);
 //            MessageInfo messageInfo = channel.receive(bf, null, null); 
 //            bf.flip();
 //            byte[] bytes = new byte[bf.limit()];
 //            bf.get(bytes, 0, bf.limit());
 //            bf.clear();
 //            Token t = (Token) deserialize(bytes);
 //            System.out.println("109:Message received from: " + t);
 //            parseMessage(t);
 //        } catch (IOException e) {
 //            System.out.println("MultiThreadedServer: run: IOException: " + e);
 //            System.exit(1);
 //        }
 //    }

 //    private void parseMessage(Token t){
 //        System.out.println("117:Message received to parse:"+t);
 //        if (t.getType().equals("buildTree")){
 //            if (!node.getIsInTree()){
 //                node.setTreeNeighbor(t.getSender(), t.getSenderPort());
 //                node.setIsInTree();
 //                sendOkToken(t.getSender(), t.getSenderPort());
 //                sendChildMessages();
 //            }else{
 //                sendDoneToken(t.getSender(), t.getSenderPort());
 //            }
 //        }else if (t.getType().equals("confirm")){
 //            System.out.println("rec Confirm msg from,"+t.getSender()+","+t.getSenderPort());
 //            this.numOfMsgsRcvd++;
 //            node.setTreeNeighbor(t.getSender(), t.getSenderPort());
 //            node.setIsInTree();
 //            System.out.println("122:" + node.treeNeighbor.size());

 //        }else if (t.getType().equals("InTree")){
 //            System.out.println("rec AlreadyInTree msg form,"+t.getSender()+","+t.getSenderPort());
 //            this.numOfMsgsRcvd++;
 //            System.out.println("buildTree completed\n");
 //            System.out.println("122:" + node.treeNeighbor.size());
            
 //        }else if (t.getType().equals("BroadCast")){
 //            System.out.println("133:rec BC msg from ,"+t);
 //            node.setSum(t.getValue());
 //            System.out.println("146:sum="+node.getSum());
 //            if(node.getTreeNeighbor().size()>1){
 //                System.out.println("144:Node has sub children >1");
 //                sendBroadCastToChildren(t);
 //            }else{
 //                System.out.println("147:Node has children <1");
 //                sendConvergence(t);
 //            }
 //        }else if (t.getType().equals("ConvergeCast")){
 //            System.out.println("151:rec CC msg from ,"+t);
 //            convergeCastCount++;
 //            if (boradCastCount==convergeCastCount){
 //                broadCastCompleted = true;
 //            }
 //        }

 //        if (numOfMsgsRcvd == node.NeighborNodes.size()) {
 //            this.startBroadCast++;
 //            printTree(); 
 //            broadCastService();
 //        }
 //    }

 //    public void sendBroadCastToChildren(Token t){
 //        System.out.println("165:Sending Boradcast message to children of "+node.host+":"+node.port+":with "+t);
 //        for (String s:node.getTreeNeighbor()){
 //            if (!s.equals(t.printNode())){
 //                System.out.println("170:Forwarding message to "+s);
 //                String forwardHost = s.split(",")[0];
 //                int forwardPort = Integer.parseInt(s.split(",")[1]);

 //                boradCastCount++;
 //                String key = "BC msg to,"+forwardHost+","+forwardPort;
 //                Token fct = new Token (node.host, node.port, "BroadCast",t.getValue());
 //                message m = new message(node.host, node.port,"BroadCast", t);
 //                node.putMessageMap(key , m);

 //                client forwardChild = new client(forwardHost, forwardPort, fct);
 //                Thread forchild = new Thread(forwardChild);
 //                forchild.start();
 //                for (Map.Entry<String, message> entry : node.getMessageMap().entrySet()){
 //                    System.out.println("key:"+entry.getKey() +",Value:"+entry.getValue().toString());
 //                }
 //            }
 //        }
 //    }

 //    public void sendConvergence(Token t){
 //        System.out.println("185:Sending converge message form "+node.host+":"+node.port+":"+t);
 //        String convergeToHost=t.getSender();
 //        int convergeToPort=t.getSenderPort();

 //        String key = "CC msg to,"+convergeToHost+","+convergeToPort;
 //        Token convt = new Token(node.host, node.port, "ConvergeCast");
 //        message m = new message(node.host, node.port,"ConvergeCast", t);
 //        node.putMessageMap(key, m);

 //        client childConverge = new client(convergeToHost, convergeToPort, convt);
 //        Thread childconv = new Thread(childConverge);
 //        childconv.start();
 //        for (Map.Entry<String, message> entry : node.getMessageMap().entrySet()){
 //            System.out.println("key:"+entry.getKey() +",Value:"+entry.getValue().toString());
 //        }
 //    }

 //    public void printTree(){
 //        if (startBroadCast ==1){
 //            node.printTreeNeighbor();
 //        }
 //    }

 //    public void broadCastService(){
 //        if (startBroadCast ==1 ){
 //            BraodCastingService bcs = new BraodCastingService();
 //            Random rand = new Random(10000);
 //            System.out.println("total braodcasts left:"+node.getNumBroadCast());
 //            while(node.getNumBroadCast()>0){
 //                Thread bc = new Thread(bcs);
 //                try {
 //                    bc.sleep(5000);
 //                    bc.start();
 //                    // bc.sleep(getNext());
 //                    // bc.sleep(3000);
 //                }catch(Exception ie){
 //                    System.out.println(ie);
 //                }
 //                if (boradCastCount==convergeCastCount){
 //                    node.decNumBroadCast();
 //                }
 //            }
 //        } 
 //    }

 //    // public int getNext() {
 //    //     Random rand = new Random(6000);
 //    //     return  Math.log(1-rand.nextInt())/(-node.getExpMean());
 //    // }


 //    public void sendOkToken( String host, int port) {
 //        System.out.println("Send ok from " + node.port + " to " + port);
 //        Token t = new Token(node.host, node.port, "confirm");
 //        client c = new client(host, port, t);
 //        new Thread(c).start();
 //    }

 //    public void sendDoneToken(String host, int port){
 //    	Token t = new Token(node.host,node.port, "InTree");
 //        client c = new client(host, port, t);
 //        new Thread(c).start();
 //    }

 //    public void sendChildMessages(){
 //        for (Node n:node.getNeighborNodes()){
 //             if(!n.getIsInTree()){
 //                 Token childt = new Token(node.host,node.port,"buildTree");
 //                 client subclient = new client(n.host,n.port, childt);
 //                 Thread subchild = new Thread(subclient);
 //                 subchild.start();
 //             }
 //        }
 //    }

 //    class BraodCastingService extends Thread implements Runnable{
 //        Random rand = new Random();
 //        int value = rand.nextInt(100);

 //        public void run(){
 //                startBroadCasting();
 //        }

 //        public void startBroadCasting(){
 //        int mean = node.getExpMean();
 //        Token bct = new Token(node.host, node.port, "BroadCast", value);
 //        message m = new message(node.host, node.port,"BroadCast", bct);

 //            if (node.getConvergence() && broadCastCompleted && node.host.equals("localhost") && node.port ==3001){
 //                broadCastCompleted=false;
 //                node.setConvergence();
 //                // System.out.println("267:BroadCasting:"+value+" from :"+ node.host+":"+node.port);
 //                node.setSum(value);

 //                for (String s:node.getTreeNeighbor()){

                    
 //                    boradCastCount++;
 //                    // node.stackBroadCast(s);
 //                    String treechildHost = s.split(",")[0];
 //                    int treechildPort = Integer.parseInt(s.split(",")[1]);
 //                    String key = "MsgSentTo,"+treechildHost+","+treechildPort;

 //                    node.putMessageMap(key, m);
 //                    System.out.println("272:sending message to :"+treechildHost+":"+treechildPort+"::with "+bct);

 //                    client treeChildren = new client(treechildHost, treechildPort, bct);
 //                    Thread treechild = new Thread(treeChildren);
 //                    treechild.start();
 //                }
 //                System.out.println(" BC count:"+boradCastCount+"::CC count"+convergeCastCount);
 //                for (Map.Entry<String, message> entry : node.getMessageMap().entrySet()){
 //                    System.out.println("key:"+entry.getKey() +",Value:"+entry.getValue().toString());
 //                }
 //            }
 //        }
 //    }
    
}
