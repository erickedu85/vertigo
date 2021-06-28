package app.graph.structure;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;

import app.utils.GraphUtil;

public class TestChange {

	public static Logger logger = Logger.getLogger(TestChange.class);

	public static void main(String[] args) {

		Graph graphA = new Graph();

		// add nodes
		graphA.addNode(
				new Vertex(0, new Etiqueta("A", true), new PositionShape(), new Stroke(), new Fill(), true, 66.d,NodeType.getNodeTypeById(0)));
		graphA.addNode(
				new Vertex(1, new Etiqueta("B", true), new PositionShape(), new Stroke(), new Fill(), true, 66.d,NodeType.getNodeTypeById(0)));
		graphA.addNode(
				new Vertex(2, new Etiqueta("C", true), new PositionShape(), new Stroke(), new Fill(), true, 66.d,NodeType.getNodeTypeById(0)));
		graphA.addNode(
				new Vertex(3, new Etiqueta("D", true), new PositionShape(), new Stroke(), new Fill(), true, 66.d,NodeType.getNodeTypeById(0)));

		// add edges
		graphA.addEdgeInMultiedge(createEdge(0, 2, 0));
		graphA.addEdgeInMultiedge(createEdge(0, 3, 0));
		graphA.addEdgeInMultiedge(createEdge(0, 3, 1));
		graphA.addEdgeInMultiedge(createEdge(2, 3, 1));
		graphA.addEdgeInMultiedge(createEdge(2, 3, 2));
		graphA.addEdgeInMultiedge(createEdge(2, 1, 2));
		
		// ---------------------
		
		IntIntHashMap parserIntInt = GraphUtil.createIntIntParserViejoANuevo(new int[] {4,5}, new int[] {3,2});
		Graph graphB = GraphUtil.updateNodeIdsOnAGraph(graphA, parserIntInt);
		
		
		logger.info(graphA);
		logger.info("-------------");
		logger.info("-------------");
		logger.info(graphB);

	}

	/**
	 * @param source
	 * @param target
	 * @param type
	 * @return
	 */
	public static Edge createEdge(int source, int target, int type) {
		EdgeType edgeType = EdgeType.getLayerTypeById(type);
		Edge e = new Edge((int) (Math.pow(source, 2) + Math.pow(target, 2) + Math.pow(type, 2)), new Etiqueta(),
				new PositionShape(), new Stroke(), new Fill(), true, edgeType, source, target);
		e.setWeight(1);
		return e;
	}

}
