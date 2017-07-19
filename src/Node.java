import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import java.io.Serializable;

public class Node implements Serializable {
	String host;
	int port;
	List<Node>NeighborNodes=new ArrayList<>();
	List<String> treeNeighbor= new ArrayList<>();
	boolean isInTree;
	int sum=0;
	boolean convergence= true;
	int numBroadCast;
	int expMean;
	boolean broadCast = false;
	HashSet<Integer> ValuesBroadCasted = new HashSet<>();
	int numMsgReceived=0 ;

	public void setNumMsgReceived(int x){
		this.numMsgReceived += x;
	}
	public int getNumMsgReceived(){
		return this.numMsgReceived;
	}


	public Node(String Host, int port){
		this.isInTree = false;
		this.host = Host;
		this.port = port;
	}
	public String toString(){
		return "Node:"+this.host+","+this.port;
	}

	public void addValuesBroadCasted(int v){
		this.ValuesBroadCasted.add(v);
	}
	public HashSet<Integer> getValuesBroadCasted(){
		return ValuesBroadCasted;
	}
	public boolean containsValue(int x){
		return ValuesBroadCasted.contains(x);
	}
	public void setBroadCast(boolean b){
		this.broadCast = b;
	}
	public boolean getBroadCast(){
		return this.broadCast;
	}
	public void buildNeighbors(String host,int port){
		Node negNode= new Node(host, port);
		setNeighborNodes(negNode);
	}
	public void setNeighborNodes(Node node){
		this.NeighborNodes.add(node);
	}
	public void printNeighborNodes(){
		System.out.println("# Neighbors::"+this.host+"::"+this.port+":"+NeighborNodes.size());
		for (Node n: NeighborNodes){
			System.out.println("\t nHost: "+n.host+" nPort: "+n.port);
		}
	}
	public List<Node> getNeighborNodes() {
		return NeighborNodes;
	}

	public List<String> getTreeNeighbor() {
		return treeNeighbor;
	}
	public void setTreeNeighbor(String host, int port) {
		if (!treeNeighbor.contains(host+","+Integer.toString(port))){
			treeNeighbor.add(host+","+Integer.toString(port));
		}
		System.out.println("Node added to Tree"+ host+":"+port);
	}	
	public void printTreeNeighbor(){
		for (String s : treeNeighbor) {
     	System.out.println("Element in Tree  of: "+this.host+" :: "+ s);
		}
	}

	public boolean getIsInTree(){
		return this.isInTree;
	}
	public void setIsInTree(){
		this.isInTree=true;
	}

	public int getSum(){
		return this.sum;
	}
	public void setSum(int value){
		System.out.println("Adding value to node :"+this.port+","+value);
		this.sum = this.sum+value;
	}

	public boolean getConvergence(){
		return this.convergence;
	}
	public void setConvergence( boolean b){
		this.convergence = b;
	}

	public int getNumBroadCast(){
		return this.numBroadCast;
	}
	public void setNumBroadCast(int num){
		this.numBroadCast = num;
	}
	public void decNumBroadCast(){
		this.numBroadCast--;
	}

	public int getExpMean(){
		return this.expMean;
	}
	public void setExpMean(int mean){
		this.expMean = mean;
	}
}
