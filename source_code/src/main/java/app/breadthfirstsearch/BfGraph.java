package app.breadthfirstsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.gs.collections.api.collection.MutableCollection;

import app.graph.structure.ColorShape;
import app.graph.structure.Graph;
import app.graph.structure.Multiedge;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.utils.DiversParser;
import app.utils.GraphUtil;
import app.utils.In;

public class BfGraph {

    // Each node maps to a list of all his neighbors
    private HashMap<Node, LinkedList<Node>> adjacencyMap;
    private boolean directed;

    public BfGraph(boolean directed) {
        this.directed = directed;
        adjacencyMap = new HashMap<Node, LinkedList<Node>>();
    }
    
    public BfGraph(Graph graph, boolean directed) {
        this.directed = directed;
        adjacencyMap = new HashMap<Node, LinkedList<Node>>();
        
        for (Multiedge multiedge : graph.getListMultiedge()) {
        	int source = multiedge.getIdSource();
        	int target = multiedge.getIdTarget();
        	
        	Node nodeSource = new Node(source, ""+source+"");
        	Node nodeTarget = new Node(target, ""+target+"");
        	        	
        	addEdge(nodeSource, nodeTarget);
        }


    }


    public void addEdgeHelper(Node a, Node b) {
        LinkedList<Node> tmp = adjacencyMap.get(a);

        if (tmp != null) {
            tmp.remove(b);
        }
        else tmp = new LinkedList<Node>();
        
        
        
        tmp.add(b);
        adjacencyMap.put(a,tmp);
    }

    public void addEdge(Node source, Node destination) {

        // We make sure that every used node shows up in our .keySet()
        if (!adjacencyMap.keySet().contains(source))
            adjacencyMap.put(source, null);

        if (!adjacencyMap.keySet().contains(destination))
            adjacencyMap.put(destination, null);

        addEdgeHelper(source, destination);

        // If a graph is undirected, we want to add an edge from destination to source as well
        if (!directed) {
            addEdgeHelper(destination, source);
        }
    }
    
    public void printEdges() {
        for (Node node : adjacencyMap.keySet()) {
            int tamano = adjacencyMap.get(node).size();
            if(tamano>1){
            	System.out.print("The " + node.name + " size: "+tamano+" has an edge towards: ");
            	for (Node neighbor : adjacencyMap.get(node)) {
            		System.out.print(neighbor.name + " ");
            	}
            }
            System.out.println();
        }
    }

//    public boolean hasEdge(Node source, Node destination) {
//        return adjacencyMap.containsKey(source) && adjacencyMap.get(source).contains(destination);
//    }
    
    public List<Integer> breadthFirstSearch(Node node) {
    	
    	//empty list to save the node ids
    	List<Integer> listIdNodes = new ArrayList<Integer>();

        // Just so we handle receiving an uninitialized Node, otherwise an
        // exception will be thrown when we try to add it to queue
        if (node == null)
            return null;;

        // Creating the queue, and adding the first node (step 1)
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Node currentFirst = queue.removeFirst();

            // In some cases we might have added a particular node more than once before
            // actually visiting that node, so we make sure to check and skip that node if we have
            // encountered it before
            if (currentFirst.isVisited())
                continue;

            // Mark the node as visited
            currentFirst.visit();
            listIdNodes.add(currentFirst.n);
//            System.out.print(currentFirst.name + " ");

            LinkedList<Node> allNeighbors = adjacencyMap.get(currentFirst);

            // We have to check whether the list of neighbors is null before proceeding, otherwise
            // the for-each loop will throw an exception
            if (allNeighbors == null)
                continue;

            for (Node neighbor : allNeighbors) {
                // We only add unvisited neighbors
                if (!neighbor.isVisited()) {
                    queue.add(neighbor);
                }
            }
        }
        return listIdNodes; 
//        System.out.println();
    }
    
   
}
