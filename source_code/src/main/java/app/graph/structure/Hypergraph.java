package app.graph.structure;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;

import app.gui.main.Constants;
import app.utils.GraphUtil;
import processing.core.PApplet;

public class Hypergraph {

	public static PApplet parent;
	public static Logger logger = Logger.getLogger(Hypergraph.class);

	private IntObjectHashMap<Vertex> listNode = new IntObjectHashMap();
	private LongObjectHashMap<Hyperedge> listHyperedge = new LongObjectHashMap();

	private double zoom = 1.0d;
	private double xView = 0.0d;
	private double yView = 0.0d;

	// Label parameters for the nodes
	private double textSize = Constants.KELP_TEXT_SIZE;
	private Fill backgroundText = Constants.KELP_TEXT_BACKGROUND_FILL;
	private double backgroundTextPadding = Constants.KELP_TEXT_BACKGROUND_PADDING;

	public Hypergraph() {
		// this.listNode = new CopyOnWriteArrayList<Vertex>();
		// this.listHyperedge = new CopyOnWriteArrayList<Hyperedge>();
	}

	/**
	 * Method to add a new node if it is not presents
	 * 
	 * @param node
	 */
	private void addSecureNode(Vertex node) {
		if (!listNode.containsKey(node.getId())) {
			listNode.put(node.getId(), new Vertex(node));
		}
	}

	private double getMaxRadiusInANode(Vertex node) {
		double maxRadius = -Float.MAX_VALUE;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex n : hyperedge.getListNode()) {
				if (n.equals(node)) {
					maxRadius = Math.max(maxRadius, n.getRadius());
				}
			}
		}
		return maxRadius;
	}

	private double getMaxStrokeInAEdge(Edge edge) {
		double maxStroke = -Float.MAX_VALUE;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge e : hyperedge.getListEdge()) {
				if (e.containVertices(edge.getIdSource(), edge.getIdTarget())) {
					maxStroke = Math.max(maxStroke, e.getStroke().getStrokeWeight());
				}
			}
		}
		return maxStroke;
	}

	/**
	 * Method to get a new graph topology from the Hypergraph. Nodes and Edges
	 * have the max radius and stroke
	 */
	public Graph getAGraphTopology() {
		Graph g = new Graph();

		for (Vertex node : listNode) {
			double maxRadius = getMaxRadiusInANode(node);
			Vertex n = new Vertex(node);
			n.setRadius(maxRadius);
			g.addNode(n);
		}
		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge edge : hyperedge.getListEdge()) {
				if (!g.existeMultiedgeBetween(edge.getIdSource(), edge.getIdTarget())) {
					double maxStroke = getMaxStrokeInAEdge(edge);
					Edge e = new Edge(edge);
					e.getStroke().setStrokeWeight(maxStroke);
					g.addEdgeInMultiedge(e);
				}
			}
		}
		return g;
	}

	public void display(boolean showKelpLines, boolean verifyOverlapLines, double mouseX, double mouseY) {
		// Get the graph topology from the Hypergraph
		// with the max radius in Nodes and max stroke in Edges
		Graph hypergraphToGraph = getAGraphTopology();

		// Nodes overlapping
		IntObjectHashMap<Vertex> nodesWithoutOverlap = GraphUtil.nodesWithoutOverlap(hypergraphToGraph,
				verifyOverlapLines);
		
		// Setting the position without overlap to the nodes and edges		
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex vertex : hyperedge.getListNode()) {
				vertex.setVisible(true);
				vertex.setShowImgBackground(false);
				vertex.getStroke().setStroked(false);
				vertex.getEtiqueta().setHeight(textSize);
				vertex.getEtiqueta().setLabelled(false);
				vertex.getEtiqueta().setShowRectangle(true);
				vertex.getEtiqueta().setFillRectangle(new Fill(backgroundText));
				vertex.getEtiqueta().setPaddingRectangle(backgroundTextPadding);
				vertex.setPosition(nodesWithoutOverlap.get(vertex.getId()).getPosition());
			}
			for (Edge edge : hyperedge.getListEdge()) {
				PositionShape source = nodesWithoutOverlap.get(edge.getIdSource()).getPosition();
				PositionShape target = nodesWithoutOverlap.get(edge.getIdTarget()).getPosition();
				edge.setVisible(true);
				edge.getStroke().setStroked(true);
				edge.getEtiqueta().setHeight(textSize);
				edge.getEtiqueta().setLabelled(false);
				edge.setPosition(new PositionShape(source, target));
			}
		}

		//
		setOpacity(Constants.KELP_OPACITY_HOVER);
		setEdgesLabelled(false);
		setNodesLabelled(false);
		Hyperedge hyperUnderMouse = getHyperedgeNearMouse(mouseX, mouseY);
		if (hyperUnderMouse != null) {
			setOpacity(Constants.KELP_OPACITY_NO_HOVER);
			setLabelled(false);
			hyperUnderMouse.setOpacity(Constants.KELP_OPACITY_HOVER);
			hyperUnderMouse.setEdgesLabelled(true);
		}
		//

		// Display every HyperEdge
		for (Hyperedge hyperedge : listHyperedge.toSortedList(Constants.HYPEREDGE_SORT_DRAW_COMPARATOR).reverseThis()) {
			if (showKelpLines) {
				hyperedge.displayEdges();
			}
			hyperedge.displayNodes();
		}

		// Display node and edges labels on top only WHEN SELECTED
		if (hyperUnderMouse != null) {
			for (Vertex vertex : hyperUnderMouse.getListNode()) {
				Vertex vertexUnder = new Vertex(vertex);
				vertexUnder.setVisible(false);
				vertexUnder.setRadius(Constants.KELP_NODE_FACTOR);
				vertexUnder.setShowImgBackground(true);
				vertexUnder.getEtiqueta().setLabelled(true);
				vertexUnder.getEtiqueta().setFillLabel(new Fill(Constants.KELP_TEXT_FILL));
				vertexUnder.display(zoom);
			}
			for (Edge edge : hyperUnderMouse.getListEdge()) {
				edge.setVisible(false);
				edge.getEtiqueta().setLabelled(true);
				edge.display(zoom);
			}
		}else {
			//TO SHOW LABES ONLY IN NODES OF THE HYPERGRAPH
			for (Vertex vertex : nodesWithoutOverlap) {
				Vertex vertexHypergraph = new Vertex(vertex);
				//
				vertexHypergraph.setRadius(Constants.KELP_NODE_FACTOR);
				vertexHypergraph.setVisible(false);
				vertexHypergraph.setShowImgBackground(true);
				vertexHypergraph.getEtiqueta().setHeight(textSize);
				vertexHypergraph.getEtiqueta().setLabelled(true);
				vertexHypergraph.getEtiqueta().setShowRectangle(true);
				vertexHypergraph.getEtiqueta().setFillLabel(new Fill(Constants.KELP_TEXT_FILL));
				vertexHypergraph.getEtiqueta().setFillRectangle(new Fill(backgroundText));
				vertexHypergraph.getEtiqueta().setPaddingRectangle(backgroundTextPadding);
				vertexHypergraph.display(1);
			}
		}
		
//		for(Vertex vertex : listNode) {
//			vertex.setPosition(nodesWithoutOverlap.get(vertex.getId()).getPosition());
//		}
		
	}

	/**
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Hyperedge getHyperedgeNearMouse(double mouseX, double mouseY) {
		// Il commencer a verifier depuis haut
		for (Hyperedge hyperedge : listHyperedge.toSortedList(Constants.HYPEREDGE_SORT_DRAW_COMPARATOR)) {
			if (hyperedge.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
				return hyperedge;
			}
		}
		return null;
	}
	


	/**
	 * Method to set the opacity to all the hypergraph
	 * 
	 * @param opacty
	 */
	public void setOpacity(double opacty) {
		for (Hyperedge hyperedge : listHyperedge) {
			hyperedge.setOpacity(opacty);
		}
	}

	/**
	 * Method to set the labelled to all the hypergraph
	 * 
	 * @param isLabelled
	 */
	public void setLabelled(boolean isLabelled) {
		for (Hyperedge hyperedge : listHyperedge) {
			hyperedge.setLabelled(isLabelled);
		}
	}

	/**
	 * @param isLabelled
	 */
	public void setEdgesLabelled(boolean isLabelled) {
		for (Hyperedge hyperedge : listHyperedge) {
			hyperedge.setEdgesLabelled(isLabelled);
		}
	}

	/**
	 * @param isLabelled
	 */
	public void setNodesLabelled(boolean isLabelled) {
		for (Hyperedge hyperedge : listHyperedge) {
			hyperedge.setNodesLabelled(isLabelled);
		}
	}

	/**
	 * The number of time a Vertex appears in all the hyperedges of the
	 * hypergraph que han sido anadidos hasta ese momento
	 * 
	 * @param vertex
	 * @return the number the
	 */
	public int numTimesANodeIn(int nodeId) {
		int numAppears = 0;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Vertex node : hyperedge.getListNode()) {
				if (node.getId() == nodeId) {
					numAppears++;
				}
			}
		}
		return numAppears;
	}

	/**
	 * The number of time an edge appears in all the hyperedges of the
	 * hypergraph
	 * 
	 * @param edge
	 * @return
	 */
	public int numTimesAnEdgeIn(Edge edge) {
		int nodeSourceId = edge.getIdSource();
		int nodeTargetId = edge.getIdTarget();
		int numAppears = 0;
		for (Hyperedge hyperedge : listHyperedge) {
			for (Edge e : hyperedge.getListEdge()) {
				if (e.containVertices(nodeSourceId, nodeTargetId)) {
					numAppears++;
				}
			}
		}
		return numAppears;
	}

	/**
	 * Method to add a Hyperedge
	 * 
	 * @param hyperedge
	 * @param kelpFacteurNode
	 * @param kelpFacteurEdge
	 */
	public void addSecureHyperedge(Hyperedge hyperedge, int kelpFacteurNode, int kelpFacteurEdge) {
		// Update node's radius and add node
		for (Vertex node : hyperedge.getListNode()) {
			double newRadius = (numTimesANodeIn(node.getId()) + 1) * kelpFacteurNode;
			node.setRadius(newRadius);
			addSecureNode(node);
		}
		// Update edge's stroke
		for (Edge edge : hyperedge.getListEdge()) {
			double newStrokeWeight = (numTimesAnEdgeIn(edge) + 1) * kelpFacteurEdge;
			edge.getStroke().setStrokeWeight(newStrokeWeight);
		}
		listHyperedge.put(hyperedge.getId(), new Hyperedge(hyperedge));
	}
	
	public void changeNodePosition(double zoomFactor) {
		for (Vertex node : listNode) {
			PositionShape nodePosition = node.getPosition();
			PositionShape newCoordinates = GraphUtil.translatePosition(nodePosition.getX1(), nodePosition.getY1(), zoomFactor);
			node.setPosition(newCoordinates);
		}
	}


	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setTextSize(double textSize) {
		this.textSize = textSize;
	}

	public void setBackgroundTextPadding(double backgroundTextPadding) {
		this.backgroundTextPadding = backgroundTextPadding;
	}

	public void setBackgroundText(int fillColor) {
		Fill backgroundFill = new Fill(backgroundText.isFilled(), fillColor, backgroundText.getFillOpacity());
		this.backgroundText = backgroundFill;
	}

	public void setOpacityBackgroundTextPadding(double fillOpacity) {
		Fill backgroundFill = new Fill(backgroundText.isFilled(), backgroundText.getFillColor(), fillOpacity);
		this.backgroundText = backgroundFill;
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

	public LongObjectHashMap<Hyperedge> getListHyperedge() {
		return listHyperedge;
	}

	public IntObjectHashMap<Vertex> getListNode() {
		return listNode;
	}

}
