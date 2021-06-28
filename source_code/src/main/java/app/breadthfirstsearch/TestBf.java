package app.breadthfirstsearch;

import java.util.List;

import app.graph.structure.Graph;
import app.graph.structure.Multiedge;
import app.gui.main.Constants;
import app.utils.DiversParser;
import app.utils.GraphUtil;
import app.utils.In;

public class TestBf {

	public static void main(String[] args) {
		// "data/others/miserables/"
		// "data/others/sierpinski/"
		// "data/biogrid/"
		// "data/dblp_vis_bd/"
		// "data/panama/"
		// "data/dip/"
		// "data/hoja/"	
		
		String pathData = "data/panama/";
		System.out.println("loading the graph..");
		//the graph
		String pathGmlFile = pathData.concat("graph.gml");
		In in = new In(pathGmlFile);
		Graph graph = new Graph(in,true);
				
		System.out.println("Parsering the graph..");
		//the new graph to search BFS
		BfGraph bfGraph = new BfGraph(graph, false);
		//WE NEED TO KNOW THE NODE ID
		System.out.println("Breadth First Search algorithm..");
//		List<Integer> listBfs = bfGraph.breadthFirstSearch(new Node(3740, "3740"));//Arnaud
		List<Integer> listBfs = bfGraph.breadthFirstSearch(new Node(364636, "364636"));//Ilham aliye
		
		//the new connected subgraph
		System.out.println("Getting the connected subgraph..");
		Graph subGraphConnexe = GraphUtil.getNewlyGraphFromListNodes(graph, listBfs);
		
		//the connected subgraph with ordered ids
		System.out.println("Getting the connected subgraph with ordered node ids..");
		Graph subGraphConnexeOrderedIds = GraphUtil.getNewlyGraphParserOrderIds(subGraphConnexe);
		
		System.out.println("Saving the connected subgraph to GML..");
		//saving the connected subgraph to a GML format
		DiversParser.saveGML(subGraphConnexeOrderedIds, pathData.concat("graph_subgraph.gml"));
		
		System.out.println("Saving the list_nodes_names.txt file..");
		//saving the relationships.txt
		DiversParser.saveVertexNamesTXT(subGraphConnexeOrderedIds,  pathData.concat("list_nodes_names.txt"));
		
		System.out.println("DONE :)");

	}
}
