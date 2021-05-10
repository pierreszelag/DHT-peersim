package dht;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

import java.util.*;

public class Initializer implements peersim.core.Control {
    
    private int dhtPid;
    private Dht firstNode;

    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
	this.dhtPid = Configuration.getPid(prefix + ".dhtProtocolPid");
	
	this.firstNode = (Dht) Network.get(0).getProtocol(this.dhtPid);
	this.firstNode.setTransportLayer(0);
	this.firstNode.state = "on";
	System.out.println("Fisrt Node in the ring	|UUID : " + this.firstNode.uuid);
	
	
	this.firstNode.leftNode = (Dht) Network.get(1).getProtocol(this.dhtPid);
	this.firstNode.leftNode.setTransportLayer(1);
	this.firstNode.leftNode.state = "on";
	
	this.firstNode.rightNode = this.firstNode.leftNode;
	this.firstNode.leftNode.leftNode = this.firstNode;
	this.firstNode.leftNode.rightNode = this.firstNode;
	
	System.out.println("Second Node in the ring	|UUID : " + this.firstNode.leftNode.uuid);
    }

    public boolean execute() {
		if (Network.size() < 1) {
		    System.err.println("Network size is not positive");
		    System.exit(1);
		}
		
		//creation de l'anneau en ajoutant 8 nouveaux noeuds
		this.createRing(8);
		
		System.out.println("Initialization completed");
		
		//Affichage de l'anneau
		this.displayRing();
		
		//Test de l'ajout de donnée à l'anneau
		Data datatest = new Data("example of content");
		datatest.findHomeNode(firstNode);
		
		return false;
    }
    
    public void addNode(Dht node) {
    	this.firstNode.addNeighbour(node);
    	
    	System.out.println("New node in the ring !");
    	System.out.println("Node UUID : " + node.uuid + "	|Left UUID : " + node.leftNode.uuid + "	|Right UUID : " + node.rightNode.uuid);
    }
    
    public void createRing(int nbOfNodes) {
    	
    	for (int i = 1; i < nbOfNodes + 1; i++) {
    		int id = this.randomNodeId();
    		Dht node = (Dht) Network.get(id).getProtocol(this.dhtPid);
    		node.setTransportLayer(id);
    		node.state = "on";
    		this.addNode(node);
    	}
    }
    
    public int randomNodeId() {
    	Random r = new Random();
    	int id = r.nextInt(Network.size());
    	
    	while(this.getNode(id).state != "off") {
    		id = r.nextInt(Network.size());
    	}
    	
    	return id;
    }
    
    public Dht getNode(int id) {
    	Dht node = (Dht) Network.get(id).getProtocol(this.dhtPid);
    	node.setTransportLayer(id);
    	return node;
    }
    
    public void displayRing() {
    	System.out.println("-------Start-------");
    	Dht node = this.firstNode;
    	do {
    		System.out.println(node.uuid);
    		node = node.rightNode;
    	} while(node != this.firstNode);
    	System.out.println("-------End-------");
    }
}