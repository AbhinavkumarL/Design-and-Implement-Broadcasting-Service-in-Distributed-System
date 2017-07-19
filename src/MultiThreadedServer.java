import java.io.*;
import java.util.*;
import java.lang.Iterable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.util.Random;
// import com.sun.nio.sctp.*;

/**
 * Multi Threaded Server class handles threads created by the server for the
 * incoming sockets.
 */

public class MultiThreadedServer implements Runnable {

    private SctpChannel channel;
    // private Token serverToken;
    private boolean isStopped = false;
    private Thread runningThread = null;
    public static String host;
    public static int port;
    public static Node node ;
    // public static int numOfMsgsRcvd;
    public static int startBroadCast =0;
    public static int boradCastCount =0;
    public static int convergeCastCount =0;
    public static boolean broadCastCompleted= true;
    

    public MultiThreadedServer(SctpChannel channel, Node node){
        this.channel = channel;
        this.node = node;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.runningThread.interrupt();
            this.channel.close();
        } catch (IOException e) {
            throw new RuntimeException("MultiThreadedServer: stop: Error closing server", e);
        }
    }

    @Override
    public void run() {
        // synchronized(this){
        //     this.runningThread = Thread.currentThread();
        // }
        try{
            processChannel(this.channel);
        }catch( Exception e){
            System.out.println(e);
        }
       
    }
    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            }
        }
    }

    private void processChannel (SctpChannel channel) throws IOException, ClassNotFoundException {
        
        try {
            ByteBuffer bf = ByteBuffer.allocate(640000);
            MessageInfo messageInfo = channel.receive(bf, null, null); 
            bf.flip();
            byte[] bytes = new byte[bf.limit()];
            bf.get(bytes, 0, bf.limit());
            bf.clear();
            Token t = (Token) deserialize(bytes);
            // System.out.println("109:Message received from: " + t);
            // System.out.println("\n");
            // if (t.getType().equals("buildTree")|| t.getType().equals("confirm")||t.getType().equals("InTree")){
            //     parseMessage(t);
            // }else {
            //     parseBroadCastMessage(t);
            // }
             parseMessage(t);
        } catch (IOException e) {
            System.out.println("MultiThreadedServer: run: IOException: " + e);
            System.exit(1);
        }
    }

    private void parseMessage(Token t){
        // System.out.println("117:Message received to parse:"+t);
        if (t.getType().equals("buildTree")){
            System.out.println("94:Build tree message from "+ t);
            if (!node.getIsInTree()){
                node.setIsInTree();
                node.setTreeNeighbor(t.getSender(), t.getSenderPort());
                sendOkToken(t.getSender(), t.getSenderPort());
                sendChildMessages();
            }else{
                sendDoneToken(t.getSender(), t.getSenderPort());
            }
        }else if (t.getType().equals("confirm")){
            System.out.println("104:Confirm messgae from "+t);
            // numOfMsgsRcvd++;
            node.setNumMsgReceived(1);
            node.setIsInTree();
            node.setTreeNeighbor(t.getSender(), t.getSenderPort());
            
            System.out.println("122:" + node.treeNeighbor.size());

        }else if (t.getType().equals("InTree")){

            System.out.println("112: Intree message from "+t);
            // numOfMsgsRcvd++;
            node.setNumMsgReceived(1);
            System.out.println("122:" + node.treeNeighbor.size());
            
        } else if (t.getType().equals("BroadCast")){

            System.out.println("118:rec BC msg from ,"+t+", Stack Size:"+t.stackSize());
            node.setSum(t.getValue());
            if(node.getTreeNeighbor().size()>1){
                System.out.println("123:Node has children >1");
                sendBroadCastToChildren(t);
            }else{
                System.out.println("126:Node has children <1");
                sendConvergence(t);
            }

        }else if (t.getType().equals("ConvergeCast")){

            System.out.println("151:rec CC msg from ,"+t+" , Stack Size:"+t.stackSize());
            
            if (t.stackSize()>0){
                System.out.println("redirecting the CC message");
                Node n = t.peekNode();

                Token scc = new Token(n.host, n.port, "ConvergeCast",t.getValue());
                scc.setStack(t.getStack());
                System.out.println("139:new redirected token"+scc);
                sendConvergence(scc);
            }else{
                if (node.containsValue(t.getValue())){
                    convergeCastCount++;
                }
                System.out.println(" 146:BC count:"+boradCastCount+"::CC count"+convergeCastCount);
                System.out.println(" 147:BC completion on child tree"+ t.getSender()+","+t.getSenderPort());
                System.out.println("147: Total sum of BroadCast operation till now = "+node.getSum());
                if (convergeCastCount==boradCastCount){
                    // broadCastCompleted = true;
                    node.setConvergence(true);
                    node.decNumBroadCast();

                    if (node.getNumBroadCast()>0){
                         broadCastService();
                         node.setConvergence(false);
                    }
                   
                }
            }
        }

            // System.out.println("if " + t.getType() + " " + node.getNumMsgReceived());
        if (node.getNumMsgReceived() == node.NeighborNodes.size()) {
            node.setNumMsgReceived(100000);
            // System.out.println("values:"+numOfMsgsRcvd+","+node.NeighborNodes.size());
            node.printTreeNeighbor();
            node.setBroadCast(true);
            if (node.getBroadCast() && node.getConvergence() && node.getNumBroadCast()>0){
                System.out.println("Initiating BroadCast Service on this Node");
                broadCastService();
                node.setConvergence(false);
                
            }
        }
    }


    public void sendOkToken( String host, int port) {
        System.out.println("198:Send Confirm from " + node.port + " to " + port);
        Token t = new Token(node.host, node.port, "confirm");
        client c = new client(host, port, t);
        new Thread(c).start();
    }

    public void sendDoneToken(String host, int port){
        System.out.println("205:Send InTree from " + node.port + " to " + port);
        Token t = new Token(node.host,node.port, "InTree");
        client c = new client(host, port, t);
        new Thread(c).start();
    }

    public void sendChildMessages(){
        for (Node n:node.getNeighborNodes()){
             if(!n.getIsInTree()){
                System.out.println("214:Send BuildTree childMessages " + node.port + " to " + n.port);
                 Token childt = new Token(node.host,node.port,"buildTree");
                 client subclient = new client(n.host,n.port, childt);
                 Thread subchild = new Thread(subclient);
                 subchild.start();
             }
        }
    }


    // public void parseBroadCastMessage(Token t){
    //     if (t.getType().equals("BroadCast")){

    //         System.out.println("118:rec BC msg from ,"+t+", Stack Size:"+t.stackSize());
    //         node.setSum(t.getValue());
    //         if(node.getTreeNeighbor().size()>1){
    //             System.out.println("123:Node has children >1");
    //             sendBroadCastToChildren(t);
    //         }else{
    //             System.out.println("126:Node has children <1");
    //             sendConvergence(t);
    //         }

    //     }else if (t.getType().equals("ConvergeCast")){

    //         System.out.println("151:rec CC msg from ,"+t+" , Stack Size:"+t.stackSize());
            
    //         if (t.stackSize()>0){
    //             System.out.println("redirecting the CC message");
    //             Node n = t.peekNode();

    //             Token scc = new Token(n.host, n.port, "ConvergeCast",t.getValue());
    //             scc.setStack(t.getStack());
    //             System.out.println("139:new redirected token"+scc);
    //             sendConvergence(scc);
    //         }else{
    //             if (node.containValue(t.getValue())){
    //                 convergeCastCount++;
    //             }
    //             System.out.println(" 146:BC count:"+boradCastCount+"::CC count"+convergeCastCount);
    //             System.out.println(" 147:BC completion on child tree"+ t.getSender()+","+t.getSenderPort());
    //             System.out.println("147: Total sum of BroadCast operation till now = "+node.getSum());
    //             if (convergeCastCount==boradCastCount){
    //                 // broadCastCompleted = true;
    //                 node.setConvergence(true);
    //                 node.decNumBroadCast();
    //             }
    //         }
    //     }
    // }

    public void sendBroadCastToChildren(Token t){
        
        String forwardHost;
        int forwardPort;
        System.out.println("165:Sending BC to children of "+node.host+":"+node.port+":with "+t);
        // node.printTreeNeighbor();

        for (String s:node.getTreeNeighbor()){
            //  if (!s.equals(t.printNode())){
            //     System.out.println("FALSE:"+s+",Length:"+s.length()+"::"+t.printNode()+",Length:"+t.printNode().length());
            // }
            if (!s.equals(t.printNode())){
                System.out.println("170:Forwarding message to "+s);
                 forwardHost = s.split(",")[0];
                 forwardPort = Integer.parseInt(s.split(",")[1]);
                // t.setSender(node.host);
                // t.setSenderPort(node.port);
                // t.addNode(node);

                Token fbct = new Token(node.host, node.port, "BroadCast",t.getValue());
                fbct.setStack(t.getStack());
                fbct.addNode(node);

                // boradCastCount++;
                client forwardChild = new client(forwardHost, forwardPort, fbct);
                Thread forchild = new Thread(forwardChild);
                forchild.start(); 
            }   
        }
    }

    public void sendConvergence(Token t){
        System.out.println("185:Sending CC from "+node.host+","+node.port+" to "+t.getSender()+","+t.getSenderPort());
        System.out.println("186: Total sum of BroadCast operation till now = "+node.getSum());
        String convergeToHost=t.getSender();
        int convergeToPort=t.getSenderPort();

        t.setSender(node.host);
        t.setSenderPort(node.port);
        t.setType("ConvergeCast");
        // t.setValue(0);
        t.removeNode();

        client childConverge = new client(convergeToHost, convergeToPort, t);
        Thread childconv = new Thread(childConverge);
        childconv.start();
    }

    public int expMeanInternval(){
        int lambda = (1/node.getExpMean());
        Random r = new Random();
        double temp = ((Math.log(1 - r.nextDouble()) / (-lambda))) * 1000;
        return (int) temp;
    }


    public void broadCastService(){

            BroadCastingService bcs = new BroadCastingService();
            System.out.println("312: Total braodcasts left:"+node.getNumBroadCast());
            
            
                Thread bc = new Thread(bcs);
                try {
                    bc.sleep(5000);
                    bc.start();
                }catch(Exception ie){
                    System.out.println(ie);
                }
    }

    class BroadCastingService extends Thread implements Runnable{
        Random rand = new Random();
        int value = rand.nextInt(100);

        public void run(){
                startBroadCasting();
        }

        public void startBroadCasting(){
            int mean = node.getExpMean();
            // System.out.println("\n");
            // if (node.getConvergence() && broadCastCompleted && node.port ==3001){
            //     broadCastCompleted=false;
            //     node.setConvergence(false);
            // if (node.getNumBroadCast()>4){
            // if (node.port == 3003){
                 Token bct = new Token(node.host, node.port, "BroadCast", value, node);
                 node.setSum(value);
                 node.addValuesBroadCasted(value);

                for (String s:node.getTreeNeighbor()){
                     // System.out.println("271:BroadCast started, Message sent to :"+s);
                    
                    boradCastCount++;
                    String treechildHost = s.split(",")[0];
                    int treechildPort = Integer.parseInt(s.split(",")[1]);

                    System.out.println("272:sending BC:"+treechildHost+":"+treechildPort+"::with "+bct);
                    client treeChildren = new client(treechildHost, treechildPort, bct);
                    Thread treechild = new Thread(treeChildren);
                    treechild.start();
                }
                // System.out.println(" BC count:"+boradCastCount+"::CC count"+convergeCastCount);
            // }
                // System.out.println("end of methods");
        }
    }
    
}
