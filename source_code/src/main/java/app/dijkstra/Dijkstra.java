package app.dijkstra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;

import app.graph.structure.Edge;
import app.graph.structure.Graph;
import app.graph.structure.Vertex;
import app.utils.GraphUtil;

/**
 * Dijkstrat's Algorithm (by by Edsger Dijkstra)
 * 
 * It is a graph search algorithm that solve the shortest path problem with non
 * negative edge path costs.
 * 
 * For a given VERTEX in the graph, the algorithm finds the path with lowest
 * cost between THAT VERTEX and every other VERTEX
 */

/**
 * @author Erick Cuenca
 * 
 *         This implementation supports an undirected graph
 */
public class Dijkstra {

	private final IntObjectHashMap<Vertex> nodes = new IntObjectHashMap();
	private final IntObjectHashMap<Edge> edges = new IntObjectHashMap();

	private Set<Vertex> settledNodes; // Set not accept les doublons
	private Set<Vertex> unSettledNodes;
	private Map<Vertex, Vertex> predecessors;
	private Map<Vertex, Double> distance;

	/**
	 * To get a path of VERTEX
	 * 
	 * @param source
	 *            Vertex source
	 * @param target
	 *            Vertex target
	 * @return A path of Vertex
	 */
	public LinkedList<Vertex> getVertexPath(Vertex source, Vertex target) {
		execute(source);
		return getPath(target);
	}

	/**
	 * To get a path of EDGES
	 * 
	 * @param source
	 *            Vertex source
	 * @param target
	 *            Vertex target
	 * @return A path of Edges
	 */
	public LinkedList<Edge> getEdgePath(Vertex source, Vertex target) {
		execute(source);
		return getEdgePath(target);
	}

	private void execute(Vertex source) {
		settledNodes = new HashSet<Vertex>();
		unSettledNodes = new HashSet<Vertex>();
		distance = new HashMap<Vertex, Double>();
		predecessors = new HashMap<Vertex, Vertex>();
		distance.put(source, 0.0d);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Vertex node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);// llena en unsettle los vecinos
		}
	}

	private void findMinimalDistances(Vertex node) {
		List<Vertex> adjacentNodes = getNeighbors(node);
		adjacentNodes.addAll(getNeighborsAll(node)); // UNDIRECTED_GRAPH,_FOR_ALL_NODES
		for (Vertex target : adjacentNodes) {
			if (getShortestDistance(target) > (getShortestDistance(node) + getDistance(node, target))) {
				distance.put(target, (getShortestDistance(node) + getDistance(node, target)));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	/**
	 * 
	 * Get Weight of edges
	 * 
	 * @param node
	 * @param target
	 * @return
	 */
	private Double getDistance(Vertex node, Vertex target) {
		for (Edge edge : edges) {
			if (edge.getIdSource() == node.getId() && edge.getIdTarget() == target.getId()
					|| edge.getIdTarget() == node.getId() && edge.getIdSource() == target.getId()) {
				return edge.getDistance();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private List<Vertex> getNeighbors(Vertex node) {
		List<Vertex> neighbors = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			// new
			// Vertex nodeDestination = GraphUtil.getNodeById(nodes,
			// edge.getIdTarget());
			Vertex nodeDestination = nodes.get(edge.getIdTarget());
			// end-new

			if (edge.getIdSource() == node.getId() && !isSettled(nodeDestination)) {
				neighbors.add(nodeDestination);
			}
		}
		return neighbors;
	}

	private List<Vertex> getNeighborsAll(Vertex node) {
		List<Vertex> neighbors = new ArrayList<Vertex>();
		for (Edge edge : edges) {
			// Vertex nodeDestination = GraphUtil.getNodeById(nodes,
			// edge.getIdSource());
			Vertex nodeDestination = nodes.get(edge.getIdSource());
			if (edge.getIdTarget() == node.getId() && !isSettled(nodeDestination)) {
				neighbors.add(nodeDestination);
			}
		}
		return neighbors;
	}

	private Vertex getMinimum(Set<Vertex> vertexes) {
		Vertex minimum = null;
		for (Vertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Vertex vertex) {
		return settledNodes.contains(vertex);
	}

	private Double getShortestDistance(Vertex destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
			// return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	/**
	 * @param target
	 *            Vertex target
	 * @return A list<Vertex> that represent the path, NULL if no path exists
	 */
	private LinkedList<Vertex> getPath(Vertex target) {

		LinkedList<Vertex> path = new LinkedList<Vertex>();
		Vertex step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

	private LinkedList<Edge> getEdgePath(Vertex target) {
		LinkedList<Edge> edgePath = new LinkedList<Edge>();
		LinkedList<Vertex> path = getPath(target);

		for (int i = 0; i < path.size() - 1; i++) {
			Edge e = getEdgeFromNodes(path.get(i), path.get(i + 1));
			if (e != null) {
				edgePath.add(e);
			}
		}
		return edgePath;
	}

	private Edge getEdgeFromNodes(Vertex p, Vertex q) {
		for (Edge e : edges) {
			if (e.getIdSource() == p.getId() && e.getIdTarget() == q.getId()
					|| e.getIdTarget() == p.getId() && e.getIdSource() == q.getId()) {
				return e;
			}
		}
		return null;
	}

}
