package dht;

import peersim.edsim.*;

import peersim.core.*;
import peersim.config.*;

import java.util.UUID;

import java.util.ArrayList;

public class Dht implements EDProtocol {
    
    //identifiant de la couche transport
    private int transportPid;

    //objet couche transport
    private DhtTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //le numero de noeud
    private int nodeId;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;
    
    //uid du noeud
    public long uuid;
    
    //etat du noeud
    public String state = "off";
    
    //voisins du noeud
    public Dht leftNode;
    public Dht rightNode;
    
    //données du noeud
    public ArrayList<Data> data = new ArrayList<Data>();
    

    public Dht(String prefix) {
	this.prefix = prefix;
	//initialisation des identifiants a partir du fichier de configuration
	this.transportPid = Configuration.getPid(prefix + ".transport");
	this.mypid = Configuration.getPid(prefix + ".myself");
	this.transport = null;
	this.uuid = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE; 
    }

    //methode appelee lorsqu'un message est recu par le protocole HelloWorld du noeud
    public void processEvent( Node node, int pid, Object event ) {
	this.receive((Message)event);
    }
    
    //methode necessaire pour la creation du reseau (qui se fait par clonage d'un prototype)
    public Object clone() {

	Dht dolly = new Dht(this.prefix);

	return dolly;
    }

    //liaison entre un objet de la couche applicative et un 
    //objet de la couche transport situes sur le meme noeud
    public void setTransportLayer(int nodeId) {
	this.nodeId = nodeId;
	this.transport = (DhtTransport) Network.get(this.nodeId).getProtocol(this.transportPid);
    }

    //envoi d'un message (l'envoi se fait via la couche transport)
    public void send(Message msg, Node dest) {
	this.transport.send(getMyNode(), dest, msg, this.mypid);
    }

    //affichage a la reception
    private void receive(Message msg) {
	System.out.println(this + ": Received " + msg.getContent());
    }

    //retourne le noeud courant
    private Node getMyNode() {
	return Network.get(this.nodeId);
    }

    public String toString() {
	return "Node "+ this.nodeId;
    }
    
    public String getState() {
    	return this.state;
    }
    
    public boolean addNeighbour(Dht node) {
    	//cas ou la place du noeud est entre le noeud actuel et son voisin de droite
    	boolean c1 = ((this.uuid < node.uuid) && (node.uuid <= this.rightNode.uuid)) || ((this.uuid < node.uuid) && (this.uuid >= this.rightNode.uuid));
    	
    	//cas ou la place du noeud est entre le noeud actuel et son voisin de gauche
    	boolean c2 = ((this.uuid >= node.uuid) && (node.uuid > this.leftNode.uuid)) || ((this.uuid >= node.uuid) && (this.uuid <= this.leftNode.uuid));
    	
    	//cas ou la place du noeud est aprés le voisin de droite du noeud actuel
    	boolean c3 = ((this.uuid <= node.uuid) && (node.uuid >= this.rightNode.uuid));
    	
    	//cas ou la place du noeud est aprés le voisin de gauche du noeud actuel
    	boolean c4 = ((this.uuid >= node.uuid) && (node.uuid <= this.leftNode.uuid));
    	
    	if (c1) {
    		node.leftNode = this;
    		node.rightNode = this.rightNode;
    		
    		this.rightNode.leftNode = node;
    		this.rightNode = node;
    		
    		return true;
    	} else if (c2) {
    		node.leftNode = this.leftNode;
    		node.rightNode = this;
    		
    		this.leftNode.rightNode = node;
    		this.leftNode = node;
    		
    		return true;
    	} else if (c3) {
    		return this.rightNode.addNeighbour(node);
    	} else if (c4) {
    		return this.leftNode.addNeighbour(node);
    	} else {
    		return false;
    	}
    }
    
    public void leave() {
    	this.state = "off";
    	this.leftNode.rightNode = this.rightNode;
    	this.rightNode.leftNode = this.leftNode;
    }
    
}