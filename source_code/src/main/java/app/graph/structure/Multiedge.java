package app.graph.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;

import app.gui.main.Constants;
import app.utils.GeoAnalytic;
import app.utils.GraphUtil;

public class Multiedge {

	private Long id; // key
	private int idSource;
	private int idTarget;
	private LongObjectHashMap<Edge> listEdge = new LongObjectHashMap();

	/**
	 * @param idSource
	 * @param idTarget
	 */
	public Multiedge(Long id, int idSource, int idTarget) {
		super();
		this.id = id;// GraphUtil.pairingFunction(idSource, idTarget);
		this.idSource = idSource;
		this.idTarget = idTarget;
	}

	/**
	 * @param multiedge
	 */
	public Multiedge(Multiedge multiedge) {
		super();
		this.id = multiedge.id;
		this.idSource = multiedge.idSource;
		this.idTarget = multiedge.idTarget;
		for (Edge edge : multiedge.getListEdge()) {
			addEdgeSecure(edge);
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	//a ver
//	public void updateSourceOrTargets(int oldId, int newlyId){
//		if(idSource == oldId){
//			//source
//			idSource = newlyId;
//		}else{
//			//target
//			idTarget = newlyId;
//		}
//		
//		//update id Multiedge
//		setId(GraphUtil.pairingFunction(idSource, idTarget));
//		
//		// update edges
//		for (Edge edge : listEdge) {
//			edge.updateSourceOrTarget(oldId,newlyId);
//		}
//		
//	}
	
	
	/**
	 * Method to return a list of the edges order by weight
	 * 
	 * @param multiedge
	 */
	public MutableList<Edge> getEdgeDescendingByWeight() {
	
		return listEdge.toSortedList(EDGE_WEIGHT_COMPARATOR_BORRARLUEGO).toReversed();
	}
	
	public static final Comparator<Edge> EDGE_WEIGHT_COMPARATOR_BORRARLUEGO = new Comparator<Edge>() {
		public int compare(Edge o1, Edge o2) {
			Double weight1 = o1.getWeight();
			Double weight2 = o2.getWeight();
			return weight1.compareTo(weight2);
		}
	};

	
	//Fusionar lista de edge no importa el source y target
	//usado solamente en Ghost
	public void fusionAListEdgesQuery(LongObjectHashMap<Edge> newlyListEdges) {
		for (Edge newlyEdge : newlyListEdges) {
			if (this.listEdge.contains(newlyEdge)) {
				// update the weight of the edge
				double currentWeight = listEdge.get(newlyEdge.getId()).getWeight();
//				System.out.println("currentWeight" + currentWeight);
				listEdge.get(newlyEdge.getId()).setWeight(currentWeight + 1);
			} else {
				// create a new edge
				this.listEdge.put(newlyEdge.getId(), new Edge(newlyEdge));
			}
		}
	}
	
	
	/**
	 * Method to fusion the current list of edge to another list of edge. If
	 * edge does not exist, it is created. Otherwise, the weight is updated
	 * 
	 * @param newlyListEdges
	 *            List of edges to fusion with the existing one
	 */
	public void fusionAListEdges(LongObjectHashMap<Edge> newlyListEdges) {
		for (Edge newlyEdge : newlyListEdges) {
			if (this.listEdge.contains(newlyEdge)) {
				// update the weight of the edge
				double currentWeight = listEdge.get(newlyEdge.getId()).getWeight();
//				System.out.println("currentWeight" + currentWeight);
				listEdge.get(newlyEdge.getId()).setWeight(currentWeight + 1);
			} else {
				// create a new edge
				this.listEdge.put(newlyEdge.getId(), new Edge(newlyEdge));
			}
		}
	}

	/**
	 * Method to substrate the substracctionListEdges from the current list of
	 * edge
	 * 
	 * @param substractionListEdges
	 *            List of edges to subtract
	 */
	public void subtractAListEdges(LongObjectHashMap<Edge> substractionListEdges) {
		LongObjectHashMap<Edge> toDelete = new LongObjectHashMap<Edge>();
		for (Edge edge : this.listEdge) {
			if (substractionListEdges.contains(edge)) {
				toDelete.put(edge.getId(), edge);
			}
		}
		for (Edge edge : toDelete) {
			this.listEdge.remove(edge.getId());
		}

	}

	/**
	 * Method to merge the listEdge in the first one edge with labels
	 * concatenate.
	 */
	public void mergeListEdgeInOne() {
		Edge newlyEdge = new Edge(listEdge.getFirst());
		// setting the concatenate etiqueta
		String newlyFirstEtiquetaText = "";
		int i = 0;
		for (Edge edge : listEdge) {
			newlyFirstEtiquetaText = newlyFirstEtiquetaText.concat(edge.getEtiqueta().getText());
			if ((i + 1) < listEdge.size()) {
				newlyFirstEtiquetaText = newlyFirstEtiquetaText.concat(", ");
				i++;
			}
		}
		newlyEdge.getEtiqueta().setText(newlyFirstEtiquetaText);

		// clearly the edge list
		listEdge.clear();
		// seting the only one newlyedge
		listEdge.put(newlyEdge.getId(), newlyEdge);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Multiedge other = (Multiedge) obj;
		if (!this.containVertices(other.idSource, other.idTarget))
			return false;
		return true;
	}
	
	/**
	 * Method to know if the edge contains the BOTH idVertex1 and idVertex2 as
	 * source OR target
	 * 
	 * @param idVertex1
	 *            Vertex 1
	 * @param idVertex2
	 *            Vertex 2
	 * @return True if the edge contains the both V1 and V2 as source or target
	 */
	public boolean containVertices(int idVertex1, int idVertex2) {
		if (idVertex1 != idVertex2 && containOneVertex(idVertex1) && containOneVertex(idVertex2)) {
			return true;
		}
		return false;
	}

	/**
	 * Method to know if the edge contains AT LEAST Vertex1
	 * 
	 * @param idVertex
	 * @return True if the edge contains at least this vertex
	 */
	public boolean containOneVertex(int idVertex) {
		if ((idSource == idVertex) || (idTarget == idVertex)) {
			return true;
		}
		return false;
	}
	
	public boolean containAnyOf(int idVertex, int [] listIdNodes){
		for (int idNode : listIdNodes) {
			if(idVertex != idNode && containOneVertex(idNode)){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Method to add the edge only if the newly edge is not already in the list
	 * 
	 * @param edge
	 */
	public void addEdgeSecure(Edge edge) {
		if (!listEdge.containsKey(edge.getId())) {
			listEdge.put(edge.getId(), new Edge(edge));
		}
	}

	public void deleteEdge(Edge edge) {
		System.out.println("borrando edge de typo " + edge.getId());
		listEdge.remove(edge.getId());
	}

	/**
	 * @param nodeSourcePosition
	 * @param nodeTargetPosition
	 */
	public void updateEdgePositions(PositionShape nodeSourcePosition, PositionShape nodeTargetPosition, double nodeDiameter) {
//		double nodeDiameter = Constants.GRAPH_QUERY_EDGE_PARALLEL_DISTANCE;
		int numEdgesBetweenTwoNodes = listEdge.size();

		double[] divideDiameter = GraphUtil.divideDiameterByN(numEdgesBetweenTwoNodes, nodeDiameter);

		List<PositionShape> positionTangentEdges = new ArrayList<PositionShape>();
		for (double radio : divideDiameter) {
			// get outer tangents from two imaginary circles with radio
			double[][] outerTangents = GeoAnalytic.tangentPoints2Circles(nodeSourcePosition.getX1(),
					nodeSourcePosition.getY1(), radio, nodeTargetPosition.getX1(), nodeTargetPosition.getY1(), radio);
			// outer tangent
			positionTangentEdges.add(new PositionShape(outerTangents[0][0], outerTangents[0][1], outerTangents[0][2],
					outerTangents[0][3]));
			// outer tangent
			positionTangentEdges.add(new PositionShape(outerTangents[1][0], outerTangents[1][1], outerTangents[1][2],
					outerTangents[1][3]));
		}
		int i = 0;
		for (Edge edge : listEdge) {
			edge.setPosition(new PositionShape(positionTangentEdges.get(i)));
			i++;
		}

	}

	public int getIdSource() {
		return idSource;
	}

	public void setIdSource(int idSource) {
		this.idSource = idSource;
	}

	public int getIdTarget() {
		return idTarget;
	}

	public void setIdTarget(int idTarget) {
		this.idTarget = idTarget;
	}

	public Long getId() {
		return id;
	}

	public LongObjectHashMap<Edge> getListEdge() {
		return listEdge;
	}

	public void setListEdge(LongObjectHashMap<Edge> listEdge) {
		this.listEdge = listEdge;
	}

	@Override
	public String toString() {
//		return "Multiedge [id=" + getId() + ", idSource=" + idSource + ", idTarget=" + idTarget + ", listEdge="
//				+ listEdge + "]";
		
		String stringEdges = "";
		for (Edge edge : listEdge) {
			stringEdges = stringEdges.concat("\n").concat(edge.toString());
		}
		
		return "Multiedge [id= " +getId() + ", idSource=" + idSource + ", idTarget=" + idTarget + ", listEdge="
		+ stringEdges + "]\n";
	}

}
