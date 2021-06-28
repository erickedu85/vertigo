package app.graph.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.google.common.primitives.Ints;
import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.gui.database.GraphDBToolBar;
import app.gui.database.GuiGraphDB;
import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.query.ComponentCreator;
import app.heatmap.HeatMapBuilder;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import app.utils.In;
import app.utils.MathUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import processing.core.PApplet;

/**
 * The graph class represent an abstraction of the a Graph G
 * 
 * @author Erick Cuenca
 *
 */
public class Graph {

	public static PApplet parent;
	public static Logger logger = Logger.getLogger(Graph.class);
	private Long id;
	private String name = "";
	private double zoom = 1.0d;
	private double xView = 0.0d;
	private double yView = 0.0d;
	private int numFusions = 1;
	private double mbr = 0.0d;

	// MULTIEDGES
	private LongObjectHashMap<Multiedge> listMultiedge = new LongObjectHashMap();
	// NODES
	private IntObjectHashMap<Vertex> listNode = new IntObjectHashMap();
	private boolean showMouseToolTip = false;

	// USING IN OVERLAPING
	private List<Edge> listDTEdge = new CopyOnWriteArrayList<Edge>();

	// ----------- EMBEDDINGS -----------
	private FastList<int[]> listFastIntEmbeddings = new FastList<int[]>();
	private IntObjectHashMap<Vertex> listNodePattern = new IntObjectHashMap();
	private List<Vertex> listNodePatternSorted = new ArrayList<Vertex>();

	// IMPROVE THE SPEED
	private IntObjectHashMap<List<Multiedge>> listMultiedgesByNode = new IntObjectHashMap();

	// HEATMAP
	private boolean isHeatMapSide;

	/**
	 * Initializes graph structure empty
	 */
	public Graph() {

	}

	/**
	 * Initializes a new graph from an input FM3 .gml file
	 * 
	 * @param in
	 */
	public Graph(In in) {
		String allString = in.readAll();
		this.listNode = in.getNodes(allString);
		this.listMultiedge = in.getMultiedges(allString);
			
//		ObservableList<String> items = FXCollections.observableArrayList();
//		for (Vertex node : getListNode()) {
//			items.add(node.getEtiqueta().getText());
//		}
//		ComponentCreator.fillCmbLabel(GraphDBToolBar.cmbLabelNodesPatterns, items);

	}
	
	/**
	 * Initializes a new graph from an input .gml file
	 * 
	 * @param in
	 */
	public Graph(In in, boolean isGML) {
		String allString = in.readAll();
		this.listNode = in.getNodesSinFm3(allString);
		this.listMultiedge = in.getMultiedgesSinFm3(allString);

	}
	
	

	/**
	 * Method to init a Graph from an other Graph g. Copies Vertex and Multiedges
	 * 
	 * @param g
	 */
	public Graph(Graph g) {
		for (Vertex node : g.getListNode()) {
			this.listNode.put(node.getId(), new Vertex(node));
		}
		for (Multiedge multiedge : g.getListMultiedge()) {
			this.listMultiedge.put(multiedge.getId(), new Multiedge(multiedge));
		}
	}
	
	/**
	 * Method to init a Graph from a list of Vertex and Multiedges
	 * 
	 * @param g
	 */
	public Graph(MutableCollection<Vertex> listNodes, MutableCollection<Multiedge> listMultiedges) {
		for (Vertex node : listNodes) {
			this.listNode.put(node.getId(), new Vertex(node));
		}
		for (Multiedge multiedge : listMultiedges) {
			this.listMultiedge.put(multiedge.getId(), new Multiedge(multiedge));
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * NODE
	 *
	 * 
	 * 
	 */

	/**
	 * Method to display nodes, used in JAVAFX
	 * 
	 * @param gc        GraphicsContext
	 * @param mouseX
	 * @param mouseY
	 * @param animation
	 */
	public void displayNodes(GraphicsContext gc, double mouseX, double mouseY, boolean animation) {
		for (Vertex node : listNode) {
			node.display(gc, mouseX, mouseY, animation, xView, yView, zoom);
		}
	}

	/**
	 * Method to display nodes
	 */
	public void displayNodes() {
		for (Vertex node : listNode) {
			if (node.isInsideScreen(xView, yView, zoom, parent.getWidth(), parent.getHeight()) && node.isVisible()) {
//				node.getEtiqueta().setLabelled(false);	
////				node.setShowImgBackground(false);
//				node.getEtiqueta().setShowRectangle(false);
				
//				if (showMouseToolTip && node.isAPointInside(parent.mouseX, parent.mouseY, xView, yView, zoom)) {
//					node.getEtiqueta().setLabelled(true);
//					node.getEtiqueta().setShowRectangle(true);
//					node.setShowImgBackground(true);
//				}else{
//					node.getEtiqueta().setLabelled(false);	
//					node.setShowImgBackground(false);
//					node.getEtiqueta().setShowRectangle(false);
//				}
				
				if (showMouseToolTip){
					
					if(node.isAPointInside(parent.mouseX, parent.mouseY, xView, yView, zoom)){
						node.getEtiqueta().setLabelled(true);
						node.getEtiqueta().setShowRectangle(true);
						node.setShowImgBackground(true);	
					}else{
						node.getEtiqueta().setLabelled(false);
						node.getEtiqueta().setShowRectangle(false);
						node.setShowImgBackground(false);
					}
					

				}
				node.display(zoom);
			}
		}
	}

	/**
	 * Return the node selected in the list of node patterns
	 * 
	 * @return
	 */
	public List<Vertex> getNodePatternSelected() {
		List<Vertex> result = new ArrayList<Vertex>();
		for (Vertex node : listNodePatternSorted) {
			if (node.isSelected()) {
				result.add(node);
			}
		}
		return result;
	}

	/**
	 * Method to display node patterns
	 * 
	 */
	public void displayNodePatterns() {
		for (Vertex node : listNodePatternSorted) {
			if (node.isInsideScreen(xView, yView, zoom, parent.getWidth(), parent.getHeight()) && node.isVisible()) {
				if (node.isAPointInside(parent.mouseX, parent.mouseY, xView, yView, zoom)) {
					node.getStroke().setStrokeWeight(Constants.GRAPH_DB_NODE_PATTERN_STROKE_HOVER_WEIGHT);
				}

				node.display(zoom);

				if (node.isSelected()) {
					node.setStroke(new Stroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE_SELECTED));
				} else {
					node.setStroke(new Stroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE));
				}
			}
		}

	}

	/**
	 * Method to update the node pattern positions according to the zoom changes in
	 * the listNode
	 * 
	 */
	public void updateNodePatternPositions() {
		for (Vertex nodePattern : listNodePattern) {
			nodePattern.setPosition(new PositionShape(listNode.get(nodePattern.getId()).getPosition()));
		}
	}

	/**
	 * Collision detection, Method to know if ANY node of A LIST is under the mouse
	 * pointer
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param intObjectHashMap
	 * @return Node under the mouse pointer, otherwise null
	 */
	public Vertex getNodeUnderMouse(double mouseX, double mouseY, IntObjectHashMap<Vertex> intObjectHashMap) {
		for (Vertex node : intObjectHashMap) {
			if (node.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
				return node;
			}
		}
		return null;
	}

	public int getNumNodesPatternUnderScreen() {
		int count = 0;
		for (Vertex node : listNodePatternSorted) {
			if (node.isInsideScreen(xView, yView, zoom, parent.getWidth(), parent.getHeight()) && node.isVisible()) {
				count += 1;
			}
		}
		return count;
	}

	/**
	 * Method to calculate label algorithm
	 */
	public void calculeLabelOverlapping() {
		List<Vertex> nodePatternsInputLabelAlgo = new CopyOnWriteArrayList<Vertex>();
		for (Object vertex : listNodePatternSorted) {
			Vertex node = ((Vertex) vertex);
			node.getEtiqueta().setLabelled(false);
			if (node.isVisible() && node.isInsideScreen(xView, yView, zoom, parent.getWidth(), parent.getHeight())) {
				nodePatternsInputLabelAlgo.add(node);
			}
		}

		List<Vertex> listEmbeddingLabels = nodeLabelsWithoutOverlapping(nodePatternsInputLabelAlgo);
		for (Vertex vertex : listEmbeddingLabels) {
			vertex.getEtiqueta().setHeight(vertex.getRadius());
			vertex.getEtiqueta().setLabelled(true);
		}
	}

	/**
	 * Method to return a list of nodes without overlapping
	 * 
	 * @param listNodeWithOverlap
	 * @return Return a list of vertex without overlapping
	 */
	private List<Vertex> nodeLabelsWithoutOverlapping(List<Vertex> listNodeWithOverlap) {
		Collections.sort(listNodeWithOverlap, Collections.reverseOrder(Constants.NODE_WEIGHT_COMPARATOR));

		List<Vertex> results = new ArrayList<Vertex>();
		for (Vertex node : listNodeWithOverlap) {
			boolean overlaping = isLabelOverlapInList(node, results);
			if (!overlaping) {
				results.add(node);
			}
		}
		return results;
	}

	/**
	 * Method to get if a node label is overlap in a dynamic list
	 * 
	 * @param currentNode
	 * @param listNodePriority
	 * @return true if label is overlap, otherwise false
	 */
	private boolean isLabelOverlapInList(Vertex currentNode, List<Vertex> listNodePriority) {
		PositionShape currentRectEnds = currentNode.getRectangleEnds();
		for (Vertex node : listNodePriority) {
			PositionShape nodeRectEnds = node.getRectangleEnds();
			if (GeoUtil.isOverlappingTwoRectangles(currentRectEnds, nodeRectEnds)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Change the current the weight to the area of the nodes
	 * 
	 * @param lowerTargetRange
	 * @param upperTargetRange
	 */
	public void normaWeightToAreaNodePatterns(double lowerTargetRange, double upperTargetRange) {

		listNodePatternSorted = listNodePattern.toSortedList(Constants.NODE_WEIGHT_COMPARATOR);
		
//		System.out.println("tamano:"+listNodePatternSorted.size());
//		System.out.println("min:"+listNodePatternSorted.get(0));
//		System.out.println("max:"+listNodePatternSorted.get(listNodePatternSorted.size() - 1));
		
		// To get the lower and the upper from the sorted node pattern list
		double minNumAppearsInResults = (double) ((Vertex) listNodePatternSorted.get(0)).getWeight();
		double maxNumAppearsInResults = (double) ((Vertex) listNodePatternSorted.get(listNodePatternSorted.size() - 1)).getWeight();
		// always upper>lower
		if (maxNumAppearsInResults <= minNumAppearsInResults) {
			maxNumAppearsInResults++;
		}

		// Setting the new radius normalized
		for (Object vertex : listNodePatternSorted) {
			Vertex node = ((Vertex) vertex);
			double numNodeAppearsInResults = node.getWeight();
			double radiusNormalized = PApplet.map((float) Math.pow(numNodeAppearsInResults, 0.5),
					(float) Math.pow(minNumAppearsInResults, 0.5), (float) Math.pow(maxNumAppearsInResults, 0.5),
					(float) lowerTargetRange, (float) upperTargetRange);

			node.setRadius(radiusNormalized);
			node.getEtiqueta().setHeight(radiusNormalized);
		}
	}

	/**
	 * Change the node position of the listNode in a target range (width and height)
	 * 
	 * @param widthTarget  target width range
	 * @param heightTarget target height range
	 */
	public void normalizationNodePosition(double widthTarget, double heightTarget, double paddingTop,
			double paddingBottom, double paddingLeft, double paddingRight) {

		// Get the current range
		Map<String, Double> threshold = MathUtil.getPositionThresholds(listNode);
		double xLowerCurrentRange = threshold.get("xMin");
		double xUpperCurrentRange = threshold.get("xMax");
		double yLowerCurrentRange = threshold.get("yMin");
		double yUpperCurrentRange = threshold.get("yMax");

		double xLowerTargetRange = paddingLeft;
		double xUpperTargetRange = widthTarget - paddingRight;
		double yLowerTargetRange = paddingTop;
		double yUpperTargetRange = heightTarget - paddingBottom;

		for (Vertex node : listNode) {
			double xTargetPosition = PApplet.map((float) node.getPosition().getX1(), (float) xLowerCurrentRange,
					(float) xUpperCurrentRange, (float) xLowerTargetRange, (float) xUpperTargetRange);
			double yTargetPosition = PApplet.map((float) node.getPosition().getY1(), (float) yLowerCurrentRange,
					(float) yUpperCurrentRange, (float) yLowerTargetRange, (float) yUpperTargetRange);

			node.setPosition(new PositionShape(xTargetPosition, yTargetPosition));
		}
	}

	/**
	 * Adding a new node to listNode
	 * 
	 * @param node
	 */
	public void addNode(Vertex node) {
		listNode.put(node.getId(), node);
//		listNode.put(node.getId(), new Vertex(node));
	}

	/**
	 * Deleting a node from listNode
	 * 
	 * @param node
	 * 
	 */
	public void deleteNode(Vertex node) {
		listNode.remove(node.getId());
	}

	/**
	 * Method to verify if a list of node id's are in the listNodePattern
	 * 
	 * @param nodeIds List<Integer> of the node id's
	 * @return true if ALL node id's are in the listNodePattern
	 */
	public boolean isAllNodesInListPattern(int[] nodeIds) {
		for (Integer nodeId : nodeIds) {
			if (!listNodePattern.containsKey(nodeId)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * Method to get a node at specific coordinate
	 * 
	 * @param coordinate
	 * @return
	 */
	public Vertex getNodeByCoordinate(Coordinate coordinate) {
		double x = coordinate.x;
		double y = coordinate.y;

		for (Vertex node : listNode) {
			PositionShape position = node.getPosition();
			double xPosition = position.getX1();
			double yPosition = position.getY1();
			if (xPosition == x && yPosition == y) {
				return node;
			}
		}
		return null;
	}

	/**
	 * @param label
	 */
	public boolean searchNodePatternByLabel(String label) {

		// loop the list of the node patterns
		for (Vertex node : listNodePattern) {
			if (node.getEtiqueta().getText().toLowerCase().equals(label.toLowerCase())) {
				// Moving the GraphDB view

				GuiGraphDB.nodeDisplayArc = new Vertex(node);
				GuiGraphDB.nodeDisplayArc.setStroke(new Stroke(Constants.GRAPH_DB_NODE_ARC_STROKE));
				GuiGraphDB.nodeDisplayArc.setRadius(GuiGraphDB.nodeDisplayArc.getDiameter());
			
				
				int factorWidht = (int) ((Application.SCREEN_WIDTH - Constants.LAYOUT_QUERY_VIEW_WIDTH- Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH) / 2);
				int factorHeight = (int) (Application.SCREEN_HEIGHT / 2);
				
				double nuevoX = (node.getPosition().getX1() - factorWidht) / zoom;
				double nuevoY = (node.getPosition().getY1() - factorHeight) / zoom;		
				setxView(nuevoX);
				setyView(nuevoY);
				
				logger.info("coordinates: "+nuevoX+" , "+ nuevoY);

				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * 
	 * 
	 * 
	 * EDGE
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Collision detection, Method to know if ANY edge is under the mouse pointer
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Edge getEdgeUnderMouse(double mouseX, double mouseY) {
		for (Multiedge multiedge : listMultiedge.toList()) {
			for (Edge edge : multiedge.getListEdge()) {
				edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);
				if (getNodeUnderMouse(mouseX, mouseY, listNode) == null
						&& edge.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
					return edge;
				}
			}
		}
		return null;
	}

	/**
	 * Method to display multi edges in processing
	 */
	public void displayEdges(double mouseX, double mouseY) {
		for (Multiedge multiedge : listMultiedge) {
			
			for (Edge edge : multiedge.getListEdge()) {
//				edge.getStroke().setStrokeWeight(Constants.GRAPH_DB_EDGE_STROKE_WEIGHT);
//				edge.getEtiqueta().setLabelled(false);
				if (showMouseToolTip && getNodeUnderMouse(mouseX, mouseY, listNode) == null
						&& edge.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
					edge.getStroke().setStrokeWeight(Constants.GRAPH_DB_EDGE_STROKE_WEIGHT_HOVER);
					edge.getEtiqueta().setLabelled(true);
				}
				edge.display(zoom);
			}
		}
	}

	/**
	 * Method to display multi edges in GraphisContext JavaFX
	 * 
	 * @param gc        GraphicsContext
	 * @param mouseX    mouseX coordinate
	 * @param mouseY    mouseY coordinate
	 * @param animation is mouse animation
	 */
	public void displayEdges(GraphicsContext gc, double mouseX, double mouseY, boolean animation) {

		for (Multiedge multiedge : listMultiedge) {
			PositionShape nodeSourcePosition = listNode.get(multiedge.getIdSource()).getPosition();
			PositionShape nodeTargetPosition = listNode.get(multiedge.getIdTarget()).getPosition();

			// setting the new positions of the edges in the multiedge
			multiedge.updateEdgePositions(nodeSourcePosition, nodeTargetPosition, Constants.GRAPH_QUERY_EDGE_PARALLEL_DISTANCE);

			for (Edge edge : multiedge.getListEdge()) {
				edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);
				if (getNodeUnderMouse(mouseX, mouseY, listNode) == null
						&& edge.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
					edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT_HOVER);
				}
				edge.display(gc);
			}
		}
	}

	/**
	 * @param edge
	 */
	public void addDTEdge(Edge edge) {
		if (!listDTEdge.contains(edge)) {
			listDTEdge.add(edge);
		}
	}

	/**
	 * Method to add an edge to the multiedge. If the edge is newly, its create a
	 * new Multiedge. Otherwise, it update the newly edge in the multiedge
	 * 
	 * @param edge
	 */
	public void addEdgeInMultiedge(Edge edge) {
		long keyMultiedge = GraphUtil.pairingFunction(edge.getIdSource(), edge.getIdTarget());
		if (!listMultiedge.containsKey(keyMultiedge)) {
			// add a new multiedge with the edge
			Multiedge multiedge = new Multiedge(keyMultiedge, edge.getIdSource(), edge.getIdTarget());
			multiedge.addEdgeSecure(edge);
			listMultiedge.put(keyMultiedge, multiedge);
		} else {
			// add new edge
			listMultiedge.get(keyMultiedge).addEdgeSecure(edge);
		}
	}

	/**
	 * Method to delete all the multiedges where a node is present
	 * 
	 * @param nodeId the id of the node
	 * 
	 */
	public void deleteMultidgesByNode(final int idNode) {
		MutableCollection<Multiedge> resultado = listMultiedge.select(new Predicate<Multiedge>() {
			public boolean accept(Multiedge multiedge) {
				return multiedge.containOneVertex(idNode);
			}
		});
		for (Multiedge multiedge : resultado) {
			listMultiedge.remove(multiedge.getId());
		}
	}

	/**
	 * @param edge
	 */
	public void deleteEdge(Edge edge) {
		long keyMultiedge = GraphUtil.pairingFunction(edge.getIdSource(), edge.getIdTarget());
		listMultiedge.get(keyMultiedge).deleteEdge(edge);
	}

	/**
	 *
	 * 
	 * 
	 * 
	 * HEATMAP
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Method to build a Heat Map from a list of nodes
	 * 
	 * @param listOfNodes
	 * @param heatMapRadius
	 */
	public void displayHeatmap(int heatMapRadius) {
		int listNodePatternSize = listNodePattern.size();
		int[][] ptsHeatMap = new int[listNodePatternSize][2];
		int[] width = new int[listNodePatternSize];
		int[] height = new int[listNodePatternSize];

		int i = 0;
		for (Vertex node : listNodePattern) {
			double coordinateX = node.getPosition().getX1();
			double coordinateY = node.getPosition().getY1();

			ptsHeatMap[i][0] = (int) coordinateX;
			ptsHeatMap[i][1] = (int) coordinateY;

			// To get Min and Max
			width[i] = (int) coordinateX;
			height[i] = (int) coordinateY;
			i++;
		}
		int minWidth = Ints.min(width);
		int maxWidth = Ints.max(width);
		int minHeight = Ints.min(height);
		int maxHeight = Ints.max(height);

		// Building the heatmap
		// This is the expensive part of the function
		HeatMapBuilder heatBuilder = new HeatMapBuilder(minWidth - heatMapRadius, minHeight - heatMapRadius,
				maxWidth + heatMapRadius, maxHeight + heatMapRadius, ptsHeatMap, heatMapRadius, parent);

		heatBuilder.fillListRastered();
		for (Vertex node : heatBuilder.getListNodeRaster()) {
			node.display(zoom);
		}
	}

	/**
	 *
	 * 
	 * 
	 * 
	 * GRAPH
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * There is a path between every pair of vertices
	 * 
	 * @return True if the graph is connected, false otherwise
	 */
	public boolean isConnected() {
		if (listNode.size() == 0) {
			return false;
		}
		// for (Vertex node : listNode) {
		// if (getAdjacentEdgesOfNode(node.getId()).size() == 0) {
		// return false;
		// }
		// }
		return true;
	}

	/**
	 * Method to return distinct nodes of the listNode. Used in Hypergraph
	 * 
	 * @return a list of distinct nodes
	 */
	public List<Vertex> distinctNodes() {
		List<Vertex> result = new ArrayList<Vertex>();
		for (Vertex vertex : listNode) {
			if (!result.contains(vertex)) {
				result.add(vertex);
			}
		}
		return result;
	}

	private MutableCollection<Multiedge> getMultiedgeContainsNode(final int idNode) {
		MutableCollection<Multiedge> resultado = listMultiedge.select(new Predicate<Multiedge>() {
			public boolean accept(Multiedge multiedge) {
				return multiedge.containOneVertex(idNode);
			}
		});
		return resultado;
	}

	/**
	 * Method to return the adjacent multiedges of a node
	 * 
	 * @param idNode
	 * @return Adjacent edges
	 */
	public List<Multiedge> getAdjacentMultiedgesOfNode(final int idNode) {
		return getMultiedgeContainsNode(idNode).toList();
	}

	/**
	 * Method to return the adjacent multiedges of a node AND contains a list of Id
	 * nodes
	 * 
	 * @param idNode
	 * @param listIdInclude list of the nodes id that are presents
	 * @return Adjacent edges
	 */
	public List<Multiedge> getAdjacentMultiedgesOfNodeFromAList(final int idNode, int[] listIdInclude) {

		MutableCollection<Multiedge> listMultiedgesAdjacent = getMultiedgeContainsNode(idNode);

		List<Multiedge> resultado = new ArrayList<Multiedge>();
		for (final int idNodeInclude : listIdInclude) {
			MutableCollection<Multiedge> r = listMultiedgesAdjacent.select(new Predicate<Multiedge>() {
				public boolean accept(Multiedge multiedge) {
					return multiedge.containVertices(idNode, idNodeInclude);
				}
			});
			resultado.addAll(r);
		}
		return resultado;
	}

	/**
	 * Method to return the adjacent multiedges of a node AND NOT contains a a list
	 * of Id nodes
	 * 
	 * @param idNode
	 * @param listIdExclude list of the nodes id that are presents
	 * @return Adjacent edges
	 */
	public List<Multiedge> getAdjacentMultiedgesOfNodeNOTFromAList(final int idNode, final int[] listIdExclude) {

		MutableCollection<Multiedge> listMultiedgesAdjacent = getMultiedgeContainsNode(idNode);

		final List<Multiedge> resultado = new ArrayList<Multiedge>();
		listMultiedgesAdjacent.forEach(new Procedure<Multiedge>() {
			public void value(Multiedge multiedge) {
				// TODO Auto-generated method stub
				if (!multiedge.containAnyOf(idNode, listIdExclude)) {
					resultado.add(multiedge);
				}
			}
		});
		return resultado;
	}

//	public boolean existeMultiedgeWithQueryBetween(final int idSourceMatchQuery, final int idTargetMatchQuery) {
//		for (Multiedge multiedge : listMultiedge) {
//			if (multiedge.containVerticesQuery(idSourceMatchQuery, idTargetMatchQuery)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public Multiedge getMultiedgeBetweenMatchQuery(final int idSourceMatchQuery, final int idTargetMatchQuery) {
//		for (Multiedge multiedge : listMultiedge) {
//			if (multiedge.containVerticesQuery(idSourceMatchQuery, idTargetMatchQuery)) {
//				return multiedge;
//			}
//		}
//		return null;
//	}

	/**
	 * Method to know if there is at least one multiedge. A multiedge is equal if
	 * Source and Target are equals
	 * 
	 * @param multiedge
	 * @return
	 */
	public boolean existeMultiedgeBetween(final int idSource, final int idTarget) {
		for (Multiedge multiedge : listMultiedge) {
			if (multiedge.containVertices(idSource, idTarget)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get the Multiedge between two nodes
	 * 
	 * @param idSource
	 * @param idTarget
	 * @return
	 */
	public Multiedge getMultiedgeBetween(final int idSource, final int idTarget) {
		for (Multiedge multiedge : listMultiedge) {
			if (multiedge.containVertices(idSource, idTarget)) {
				return multiedge;
			}
		}
		return null;
	}

	/**
	 * Method to return the adjacent list of edges of a node, grouped by edge type.
	 * The structure is: key:edgeType, value:numberAppears
	 * 
	 * @param idNode
	 */
	@Deprecated
	public static IntIntHashMap groupAdjacentEdgesByType(List<Edge> adjacentEdgeList) {
		// Grouping the adjacent edge list by type
		IntIntHashMap relationEdgeTypeNumAppears = new IntIntHashMap();
		for (Edge edge : adjacentEdgeList) {
			int edgeType = edge.getType().getId();
			if (!relationEdgeTypeNumAppears.containsKey(edgeType)) {
				relationEdgeTypeNumAppears.put(edgeType, 1);
			} else {
				int numAppears = (int) relationEdgeTypeNumAppears.get(edgeType);
				relationEdgeTypeNumAppears.put(edgeType, numAppears + 1);
			}
		}
		return relationEdgeTypeNumAppears;
	}

	/**
	 * Method to return a list of edges For every pair of nodes los K mayores
	 * 
	 * @param edges
	 */
	@Deprecated
	public static List<Edge> devolverK(List<Edge> edges, int K) {
		List<Edge> result = new ArrayList<Edge>();
		for (Edge edge : edges) {
			if (edge.containOneVertex(edge.getIdSource())) {

			}
		}
		return result;
	}

	public void getTopKMultiEdge(int topK) {

		// debe tomar en cuenta el height

		// En cada multiedge ordenar en forma descendent los edges por su weight

		for (Multiedge multiedge : listMultiedge) {
			multiedge.getEdgeDescendingByWeight();
		}

	}

	/**
	 * @param topK
	 * @return
	 */
	@Deprecated
	public List<Edge> getTopKInternal(int topK) {
		// List<Edge> resultTopK = new ArrayList<Edge>();
		// List<Relationships> alreadySeenTmp = new ArrayList<Relationships>();
		//
		// for (Edge edge : listEdge) {
		//
		// int source = edge.getIdSource();
		// int target = edge.getIdTarget();
		//
		// // For each Node Pair get the K
		// Relationships relation = new Relationships(source, target);
		// if (!alreadySeenTmp.contains(relation)) {
		// alreadySeenTmp.add(relation);
		// // get all edges between source and target
		// List<Edge> edgesBetweenTwoNodes = getEdgesBetweenTwoNodes(source,
		// target);
		// // Descending sort by the weight edge
		// Collections.sort(edgesBetweenTwoNodes,
		// Collections.reverseOrder(Constants.EDGE_WEIGHT_COMPARATOR));
		//
		// // Get the top K
		// for (int k = 2; k < topK; k++) {
		// try {
		// // In case the list does not have enough items
		// Edge e = edgesBetweenTwoNodes.get(k);
		// resultTopK.add(e);
		// } catch (Exception e) {
		// }
		// }
		// }
		// }
		// return resultTopK;
		return null;
	}

	/**
	 * Method to merge each Multiedge in an unique edge between two nodes of the
	 * firstly type. Labels are concatenated
	 * 
	 */
	public void mergeMultiedges() {
		for (Multiedge multiedge : listMultiedge) {
			multiedge.mergeListEdgeInOne();
		}
	}

	/**
	 * Method to get the geometry Points of the listNode
	 * 
	 * @return a Geometry object
	 */
	public Geometry getGeometry() {
		Coordinate[] coordinates = new Coordinate[listNode.size()];
		int i = 0;
		for (Vertex node : listNode) {
			coordinates[i] = new Coordinate(node.getPosition().getX1(), node.getPosition().getY1());
			i++;
		}
		return new GeometryFactory().createMultiPoint(coordinates);
	}

	/**
	 * 
	 * Print geometry of nodes and edges
	 * 
	 */
	// public void printGeometryWithBuffer() {
	//
	// logger.info("--------------------------------------");
	// for (Vertex node : listNode) {
	// Geometry geometryNode = new GeometryFactory()
	// .createPoint(new Coordinate(node.getPosition().getX1(),
	// node.getPosition().getY1()))
	// .buffer(node.getRadius() / 2 - 1.5);
	// System.out.println(geometryNode);
	// // logger.info(geometryNode);
	// }
	//
	// for (Edge edge : listEdge) {
	// Coordinate[] coordinatesEdge = new Coordinate[] {
	// new Coordinate(edge.getPosition().getX1(), edge.getPosition().getY1()),
	// new Coordinate(edge.getPosition().getX2(), edge.getPosition().getY2()) };
	// Geometry geometryEdge = new
	// GeometryFactory().createLineString(coordinatesEdge)
	// .buffer(edge.getStroke().getStrokeWeight() / 2 - 2);
	// System.out.println(geometryEdge);
	// }
	// }

	// /**
	// * @return
	// */
	// public List<Relationships> getRelationsNodeWithEdge() {
	// List<Relationships> results = new ArrayList<Relationships>();
	//
	// for (Edge edge : listEdge) {
	// Relationships relation = new Relationships(edge.getIdSource(),
	// edge.getIdTarget());
	// if (!results.contains(relation)) {
	// results.add(relation);
	// }
	// }
	//
	// return results;
	// }

	/**
	 * Method to set a radius to all listNode
	 * 
	 * @param radius
	 */
	public void setVerticesRadius(double radius) {
		for (Vertex v : listNode) {
			v.setRadius(radius);
		}
		changeMultiedgePosition();
	}
	
	/**
	 * Method to set a text size to all listNode
	 * 
	 * @param text font size
	 */
	public void setVerticesFontSize(double fontsize) {
		for (Vertex v : listNode) {
			v.getEtiqueta().setHeight(fontsize);
		}
//		changeMultiedgePosition();
	}
	
	/**
	 * Method to set a text size to all edges
	 * 
	 * @param text font size
	 */
	public void setEdgesFontSize(double fontsize) {
		for (Multiedge multiedge : listMultiedge) {			
			for (Edge edge : multiedge.getListEdge()) {
				edge.getEtiqueta().setHeight(fontsize);
			}
		}
	}

	/**
	 * Method to show or hide a img background to all listNode
	 * 
	 * @param radius
	 */
	public void setVerticesImgBackground(boolean isShowingImgBackground) {
		for (Vertex v : listNode) {
			v.setShowImgBackground(isShowingImgBackground);
		}
	}
	
	public void setVerticesLabelled(boolean isLabelled){
		for (Vertex v : listNode) {
			v.getEtiqueta().setLabelled(isLabelled);
		}
	}
	
	public void setVerticesLabelledBackground(boolean showRectangle){
		for (Vertex v : listNode) {
			v.getEtiqueta().setShowRectangle(showRectangle);
		}
	}

	public void setEdgessLabelled(boolean isLabelled){
		for (Multiedge multiedge : listMultiedge) {			
			for (Edge edge : multiedge.getListEdge()) {
				edge.getEtiqueta().setLabelled(isLabelled);
			}
		}
	}
	
	/**
	 * Method to set a opacity value to all listNode
	 * 
	 * @param opacity Opacity value
	 */
	public void setVerticesOpacity(double opacity) {
		for (Vertex v : listNode) {
			v.getFill().setFillOpacity(opacity);
		}
	}

	/**
	 * Method to set a opacity value to all listNodePattern
	 * 
	 * @param opacity Opacity value
	 */
	public void setVerticesEmbeddingsOpacity(double opacity) {
		for (Vertex node : listNodePatternSorted) {
			node.getFill().setFillOpacity(opacity);
		}
	}

	/**
	 * Method to set a color value to all listNodePattern
	 * 
	 * @param color
	 */
	public void setVerticesEmbeddingsColor(int color) {
		for (Vertex node : listNodePatternSorted) {
			node.getFill().setFillColor(color);
		}
	}

	public int[] getListOfNodesId() {
		int[] listOfNodesId = new int[listNode.size()];
		for (int i = 0; i < listNode.size(); i++) {
			listOfNodesId[i] = listNode.get(i).getId();
		}

		return listOfNodesId;
	}

	/**
	 * Method to get the Max Id in the listNode
	 * 
	 * @return
	 */
	public int getMaxIdListNode() {
		if (listNode.size() == 0) {
			return 0;
		}
		int max = Integer.MIN_VALUE;
		for (Vertex node : listNode) {
			double id = node.getId();
			max = (int) Math.max(max, id);
		}
		return max + 1;
	}

	@Override
	public int hashCode() {
		double result = 0;
		for (Vertex v : listNode) {
			result = result + Math.pow(v.getId(), 2);
		}
		return (int) result;
	}

	/*
	 * Two graphs are equals if they have the same set of nodes (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Graph) {
			Graph graphCompare = (Graph) obj;
			if (listNode.toList().containsAll(graphCompare.getListNode().toList())) {
				return true;
			}
		}
		return false;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((listNode == null) ? 0 : listNode.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Graph other = (Graph) obj;
//		if (listNode == null) {
//			if (other.listNode != null)
//				return false;
//		} else if (!listNode.equals(other.listNode))
//			return false;
//		return true;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMbr() {
		return mbr;
	}

	public void setMbr(Double mbr) {
		this.mbr = mbr;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public double getxView() {
		return xView;
	}

	public void setxView(double xView) {
		this.xView = xView;
	}

	public double getyView() {
		return yView;
	}

	public void setyView(double yView) {
		this.yView = yView;
	}

	public int getNumFusions() {
		return numFusions;
	}

	public void setNumFusions(int numFusions) {
		this.numFusions = numFusions;
	}

	public FastList<int[]> getListFastIntEmbeddings() {
		return listFastIntEmbeddings;
	}

	public IntObjectHashMap<Vertex> getListNode() {
		return this.listNode;
	}

	public void setListNode(IntObjectHashMap<Vertex> listNode) {
		this.listNode = listNode;
	}

	
	/**
	 * Method to set the visible state to all the listNode
	 * 
	 * @param visible
	 */
	public void setListNodeVisible(boolean isVisible) {
		for (Vertex node : listNode) {
			node.setVisible(isVisible);
		}
	}

	/**
	 * Method to set the visible state to all the listNodePatterns
	 * 
	 * @param visible
	 */
	public void setListNodePatternVisible(boolean isVisible) {
		for (Vertex node : listNodePattern) {
			node.setVisible(isVisible);
		}
	}
	
	
	/**
	 * Method to set the labels in the list patterns a visible state
	 * 
	 * @param isLabelled
	 */
	public void setListNodePatternLabelVisibility(boolean isLabelled) {
		for (Vertex node : listNodePatternSorted) {
			node.getEtiqueta().setLabelled(isLabelled);
		}
	}

	public List<Edge> getListDTEdge() {
		return listDTEdge;
	}

	public void setListDTEdge(List<Edge> listDTEdge) {
		this.listDTEdge = listDTEdge;
	}

	public Long getId() {
		id = (long) 0;
		for (Vertex node : listNode) {
			// id = (int) (id + Math.pow(node.getId(), 2));
			id = (long) (id + Math.pow(node.getId(), 2));
		}
		return id;
	}

	public boolean isHeatMapSide() {
		return isHeatMapSide;
	}

	public void setHeatMapSide(boolean isHeatMapSide) {
		this.isHeatMapSide = isHeatMapSide;
	}

	public IntObjectHashMap<Vertex> getListNodePattern() {
		return listNodePattern;
	}
	
	
	public void setListNodePattern(IntObjectHashMap<Vertex> listNodePattern) {
		this.listNodePattern = listNodePattern;
	}

	public List<Vertex> getListNodePatternSorted() {
		return listNodePatternSorted;
	}

	public void setListNodePatternSorted(List<Vertex> listNodePatternSorted) {
		this.listNodePatternSorted = listNodePatternSorted;
	}

	public LongObjectHashMap<Multiedge> getListMultiedge() {
		return listMultiedge;
	}

//	public MutableList<Multiedge> getListMultiedgeWithSourceTargetQuery() {
//		MutableCollection<Multiedge> resultado = listMultiedge.select(new Predicate<Multiedge>() {
//			public boolean accept(Multiedge multiedge) {
//				return multiedge.getIdTargetMatchQuery()!=999 && multiedge.getIdSourceMatchQuery()!=999;
//			}
//		});
//		//(LongObjectHashMap<Multiedge>) 
//		return resultado.toList();
//	} 
	
	public void changeNodePosition(double zoomFactor) {
		for (Vertex node : listNode) {
			PositionShape nodePosition = node.getPosition();
			PositionShape newCoordinates = GraphUtil.translatePosition(nodePosition.getX1(), nodePosition.getY1(), zoomFactor);
			node.setPosition(newCoordinates);
		}
	}

	public void changeNodePositionPattern(double zoomFactor) {
		for (Vertex node : listNodePattern) {
			PositionShape nodePosition = node.getPosition();
			PositionShape newCoordinates = GraphUtil.translatePosition(nodePosition.getX1(), nodePosition.getY1(), zoomFactor);
			node.setPosition(newCoordinates);
		}
	}
	
	
	public void changeMultiedgePosition() {
		for (Multiedge multiedge : listMultiedge) {
			PositionShape nodeSourcePosition = listNode.get(multiedge.getIdSource()).getPosition();
			PositionShape nodeTargetPosition = listNode.get(multiedge.getIdTarget()).getPosition();

			// setting the new positions of the edges in the multiedge
			multiedge.updateEdgePositions(nodeSourcePosition, nodeTargetPosition, listNode.get(multiedge.getIdSource()).getDiameter());
		}
	}

	public void setListMultiedge(LongObjectHashMap<Multiedge> listMultiedge) {
		this.listMultiedge = listMultiedge;
	}

	public boolean isShowMouseToolTip() {
		return showMouseToolTip;
	}

	public void setShowMouseToolTip(boolean showMouseToolTip) {
		this.showMouseToolTip = showMouseToolTip;
	}

	@Override
	public String toString() {
		String stringMultiedges = "";
		for (Multiedge multiedge : listMultiedge.toSortedList(Constants.MULTIEDGE_ID_COMPARATOR)) {
			stringMultiedges = stringMultiedges.concat("\n").concat(multiedge.toString());
//			multiedge.getEdgeDescendingByWeight();
		}

//		return "Graph [id=" + getId() + ", listNode=" + listNode + ", listMultiedges=" + stringMultiedges + "]";
		return "\n Graph [\n id=" + getId() + ", \n listNode=" + listNode + ", \n listMultiedges=" + stringMultiedges + "] \n";
	}

	/**
	 * METHOD TO FILL THE NODERELATIONADJACENTMULTIEDGES TO IMPROVE THE SPEED
	 * 
	 * @param listOfListNodesIds
	 */
	public void fillNodeRelationAdjacentMultiedges(FastList<int[]> listOfListNodesIds) {
		for (int[] listOfNodesIds : listOfListNodesIds) {
			fillNodeRelationsAdjacent(listOfNodesIds);
		}
	}

	public void fillNodeRelationsAdjacent(int[] listIds) {
		for (int nodeId : listIds) {
			if (!listMultiedgesByNode.containsKey(nodeId)) {
				List<Multiedge> adjacentEdgesOfNode = getAdjacentMultiedgesOfNode(nodeId);
				listMultiedgesByNode.put(nodeId, adjacentEdgesOfNode);
			}
		}
	}

	public IntObjectHashMap<List<Multiedge>> getListMultiedgesByNode() {
		return listMultiedgesByNode;
	}

	public void setListMultiedgesByNode(IntObjectHashMap<List<Multiedge>> listMultiedgesByNode) {
		this.listMultiedgesByNode = listMultiedgesByNode;
	}

}