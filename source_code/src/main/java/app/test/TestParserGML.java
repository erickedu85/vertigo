package app.test;

import app.lib.ogdf.Layer;

/**
 * @author Erick Cuenca
 *
 */
public class TestParserGML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//others/miserables
		//data/dblp_vis_bd/
		//data/panama/ 
		String pathSource = "data/panama/graph.gml";
		String pathTarget = "data/panama/graph_fm3.gml";
		Double nodeRadius = 4.0d;
		Double unitEdgeLength = 1.0d;
		//unitEdgeLength: the bigger, the more separated
		//10.0d WORKS for dblp_vis_bd
		//In panama, WHEN PROBLEM (VM) CHANGE TO 1.0d

		Layer.createFM3Layout(pathSource, pathTarget, nodeRadius, unitEdgeLength);

	}
	
}
