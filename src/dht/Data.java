package dht;

import java.util.UUID;

import java.lang.Math;

public class Data {
	
	//uid de la donnée
    public long uuid;
	
    //contenue de la donnée
    public String content;
    
    public Data(String cont) {
    	this.uuid = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    	this.content = cont;
    }
    
    public void findHomeNode(Dht firstNode) {
    	Dht actualNode = firstNode;
    	Dht closestNode = firstNode;
    	
    	do{
    		actualNode = actualNode.rightNode;
    		
    		if(Math.abs(actualNode.uuid - this.uuid) < Math.abs(closestNode.uuid - this.uuid)) {
    			closestNode = actualNode;
    		}
    	} while(actualNode != firstNode);
    	
    	closestNode.data.add(this);
    	closestNode.leftNode.data.add(this);
    	closestNode.rightNode.data.add(this);
    	
    	System.out.println("Data with UUID : " + this.uuid + " as been added to the node " + closestNode.uuid + " and it's neighbours");
    	
    }
}
