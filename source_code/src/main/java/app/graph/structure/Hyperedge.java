   package app.graph.structure;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;

import app.gui.main.Constants;
import processing.core.PApplet;

public class Hyperedge {

	public static Logger logger = Logger.getLogger(Hyperedge.class);
	public static PApplet parent;
	private Long id;
	private IntObjectHashMap<Vertex> listNode = new IntObjectHashMap();
	private IntObjectHashMap<Edge> listEdge = new IntObjectHashMap();
	private int orderToDraw = 0;

	public Hyperedge() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Init a Hyperedge from a Graph with unique edges between nodes
	 * 
	 * @param graph
	 */
	public Hyperedge(Graph graphWithUniqueEdges) {
		for (Vertex node : graphWithUniqueEdges.getListNode()) {
			listNode.put(node.getId(), new Vertex(node));
		}
		for (Multiedge multiedge : graphWithUniqueEdges.getListMultiedge()) {
			// only one multiedge ce sur
			for (Edge edge : multiedge.getListEdge()) {
				//listEdge.put(edge.getId(), new Edge(edge));
				listEdge.put(listEdge.size(), new Edge(edge));
			}
		}
	}

	/**
	 * @param hyperedge
	 */
	public Hyperedge(Hyperedge hyperedge) {
		this.orderToDraw = hyperedge.getOrderToDraw();
		for (Vertex node : hyperedge.getListNode()) {
			listNode.put(node.getId(), new Vertex(node));
		}
		for (Edge edge : hyperedge.getListEdge()) {
//			logger.info(edge);
			listEdge.put(listEdge.size(), new Edge(edge));
//			listEdge.put(edge.getId(), new Edge(edge));
		}
	}

	/**
	 * Add a node if the list of nodes does not contains
	 * 
	 * @param node
	 */
	public void addSecureNode(Vertex node) {
		if (!listNode.containsKey(node.getId())) {
			listNode.put(node.getId(), new Vertex(node));
		}
	}

	/**
	 * Add an edge
	 * 
	 * @param edge
	 */
	public void addEdge(Edge edge) {
		listEdge.put(edge.getId(), new Edge(edge));
	}

	/**
	 * Method to display the edges
	 */
	public void displayEdges() {
		for (Edge edge : listEdge) {
			edge.display(1);
		}
	}

	/**
	 * Method to display the nodes
	 */
	public void displayNodes() {
		for (Vertex node : listNode) {
			node.display(1);
		}
	}

	public boolean isAPointInside(double mouseX, double mouseY, double xView, double yView, double zoom) {
		if (isAnyEdgeNearMouse(mouseX, mouseY, xView, yView, zoom)
				|| isAnyNodeNearMouse(mouseX, mouseY, xView, yView, zoom)) {
			return true;
		}
		return false;
	}

	/**
	 * @param mouseX
	 * @param mouseY
	 * @param xView
	 * @param yView
	 * @param zoom
	 * @return
	 */
	private boolean isAnyEdgeNearMouse(double mouseX, double mouseY, double xView, double yView, double zoom) {
		for (Edge edge : listEdge) {
			if (edge.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param mouseX
	 * @param mouseY
	 * @param xView
	 * @param yView
	 * @param zoom
	 * @return
	 */
	private boolean isAnyNodeNearMouse(double mouseX, double mouseY, double xView, double yView, double zoom) {
		for (Vertex node : listNode) {
			if (node.isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param strokeOpacity
	 */
	private void setEdgeStrokeOpacity(double strokeOpacity) {
		for (Edge edge : listEdge) {
			edge.getStroke().setStrokeOpacity(strokeOpacity);
		}
	}

	/**
	 * @param opacity
	 */
	private void setNodesOpacity(double opacity) {
		for (Vertex node : listNode) {
			node.getFill().setFillOpacity(opacity);
			node.getStroke().setStrokeOpacity(opacity);
		}
	}

	/**
	 * @param opacity
	 */
	public void setOpacity(double opacity) {
		setNodesOpacity(opacity);
		setEdgeStrokeOpacity(opacity);
	}

	public void setLabelled(boolean isLabelled) {
		setEdgesLabelled(isLabelled);
		setNodesLabelled(isLabelled);
	}

	public void setEdgesLabelled(boolean isLabelled) {
		for (Edge edge : listEdge) {
			edge.getEtiqueta().setLabelled(isLabelled);
		}
	}

	public void setNodesLabelled(boolean isLabelled) {
		for (Vertex node : listNode) {
			node.getEtiqueta().setLabelled(isLabelled);
		}
	}

	public int getOrderToDraw() {
		return orderToDraw;
	}

	public void setOrderToDraw(int orderToDraw) {
		this.orderToDraw = orderToDraw;
	}

	// public List<Edge> getListEdge() {
	// return listEdge;
	// }
	//
	// public void setListEdge(List<Edge> listEdge) {
	// this.listEdge = listEdge;
	// }

	public IntObjectHashMap<Vertex> getListNode() {
		return listNode;
	}

	public void setListNode(IntObjectHashMap<Vertex> listNode) {
		this.listNode = listNode;
	}

	public IntObjectHashMap<Edge> getListEdge() {
		return listEdge;
	}

	public void setListEdge(IntObjectHashMap<Edge> listEdge) {
		this.listEdge = listEdge;
	}

	public Long getId() {
		id = (long) 0;
		for (Vertex node : listNode) {
			id = (long) (id + Math.pow(node.getId(), 2));
		}
		return id;
	}

	@Override
	public String toString() {
		return "Hyperedge [id=" + getId() + ", listNode=" + listNode + ", listEdge=" + listEdge + ", orderToDraw="
				+ orderToDraw + "]";
	}

}
