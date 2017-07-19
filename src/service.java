import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class service {
	List<String> list = new ArrayList<String>();
	Node node;
	String Startnode;
	int Startport;
	int iterations;
	int mean;

	/**
	 * Method to load the configuration file for building neighbor nodes.
	 * 
	 * @Exception IOException
	 * @return null
	 */
	public void loadFile() throws IOException{
		
		for (String Line : Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/CS6378/Project1/config.txt"))){
			if (Line.startsWith("dc")){
				list.add(Line);
			}else if (Line.startsWith("#iter")){
				this.iterations = Integer.parseInt(Line.split(" ")[1]);
			}else if (Line.startsWith("#mean")){
				this.mean = Integer.parseInt(Line.split(" ")[1]);
			}
		}
		Startnode = list.get(0).split(" ")[0];
		Startport = Integer.parseInt(list.get(0).split(" ")[1]);
	}
	/**
	 * Method to build neighbors associated to a particular node.
	 * 
	 * @return null
	 */
	public void  buildNodes(String host, int port, String neighbors){
		node= new Node(host, port);
		node.setNumBroadCast(this.iterations);
		node.setExpMean(this.mean);

		for (int i=0;i<neighbors.toCharArray().length; i++){
			String s=list.get(Character.getNumericValue(neighbors.charAt(i))-1);
			String negHost=s.split(" ")[0];
			int negPort=Integer.parseInt(s.split(" ")[1]);
			node.buildNeighbors(negHost, negPort);
		}
		node.printNeighborNodes();
		server sctpServer = new server(node);
		Thread t =new Thread(sctpServer);
		try {

			t.start();
			t.sleep(15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendBuildTreeMessages();
	}
	
	public void sendBuildTreeMessages(){
		if (node.host.equals(Startnode) && (node.port == Startport)){
			System.out.println("Build tree Initiated at this Node");
			for(Node n: node.getNeighborNodes()){
				Token t = new Token(node.host, node.port,"buildTree");
				// node.setTreeNeighbor(n.host,n.port);
				// node.setIsInTree();
				System.out.println("Connecting to Node:"+n.host+"::"+n.port);
				client c = new client(n.host, n.port, t);
				Thread tclient = new Thread(c);
				tclient.start();
			}
			// node.printTreeNeighbor();
		}
	}
	
	public void toString(List<Node> nodeList){
		int i=0;
		for(Node n : nodeList){
			System.out.println("Node "+i+n.host+","+n.port+","+n.NeighborNodes.toString());
			i++;
		}
	}
	
	public static void main(String[] args) throws IOException{
		if (args.length>0){
			service sv = new service();
			sv.loadFile();
			sv.buildNodes(args[0],Integer.parseInt(args[1]),args[2].replaceAll(" ", ""));
			
		}else {
			service sv = new service();
			sv.loadFile();
			System.out.println("The loaded file:"+ sv.list.toString());
			sv.buildNodes("localhost",3332,"");
			System.out.println("Incorrect inputs...");
		}
	}
}
