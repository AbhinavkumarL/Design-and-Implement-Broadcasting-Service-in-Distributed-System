import java.io.Serializable;
import java.util.*;

public class Token implements Serializable {

    private String sender;
    private int senderport;
    String type;
    int value=0;
    Stack<Node> msgStack = new Stack<>();


    public Token(String s, int sp, String t) {
        this.sender = s;
        this.senderport = sp;
        this.type = t;
    }
    public Token(String s, int sp, String t, int val) {
        this.sender = s;
        this.senderport = sp;
        this.type = t;
        this.value= val;
    }
    public Token(String s, int sp, String t, int val, Node node){
        this.sender = s;
        this.senderport = sp;
        this.type = t;
        this.value = val;
        addNode(node);
    }

    public void addNode(Node node){
        //System.out.println("Adding to Stack:"+node);
        this.msgStack.push(node);
    }
    public void removeNode(){
        //System.out.println("Removing from Stack:"+this.msgStack.peek());
        this.msgStack.pop();
    }
    public Node peekNode(){
        //System.out.println("top Node is :"+this.msgStack.peek()+ ", size:"+ this.msgStack.size());
        return this.msgStack.peek();
    }
    public int stackSize(){
        //System.out.println("get Stack Size:"+this.msgStack.size());
        return this.msgStack.size();
    }
    public Stack<Node> getStack(){
        return this.msgStack;
    } 
    public void setStack(Stack<Node> s) {
        this.msgStack = s;
    }
    public String getSender() {
        return this.sender;
    }
    public void setSender(String host){
        this.sender = host;
    }
    public int getSenderPort() {
        return this.senderport;
    }
    public void setSenderPort(int port){
        this.senderport = port;
    }
    public String toString() {
        return this.sender +","+this.senderport+","+this.value;
        
    }
    public String printNode(){
        return this.sender+","+this.senderport;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String t){
        this.type=t;
    }
    public int getValue(){
        return this.value;
    }
    public void setValue(int val){
        this.value = val;
    }

}
