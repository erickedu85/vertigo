package app.overlap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import app.graph.structure.Edge;
import app.graph.structure.EdgeType;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.utils.GraphUtil;
import app.utils.JtsUtil;

public class VerticesOverlap {

	final static Logger logger = Logger.getLogger(VerticesOverlap.class);
	private Graph currentLayout;
	private Double sMaxDampingFactor; // mayor a 1
	private IntIntHashMap orderNodeCalculateLayout;
	private Double stressConfiguration;

	private boolean checkOverlapping;

	/**
	 * @param listNode
	 * @param maxDampingFactor
	 *            Greater than 1
	 */
	public VerticesOverlap(Graph graph, double maxDampingFactor, boolean checkOverlapping) {
		this.currentLayout = graph;
		this.sMaxDampingFactor = maxDampingFactor;
		this.checkOverlapping = checkOverlapping;
		
		commencer();
	}

	public void iteraction() {

		// 0. Clear listDTEdges at every interaction
		currentLayout.getListDTEdge().clear();

		// 1. Delaunay Triangulations DT is computed from the current layout
		Geometry delaunayTriangulations = JtsUtil.delaunayTriangulations(currentLayout.getGeometry(), 1);

		// 2. For every Delaunay Edge the overlap factor and desired distance is
		// computed
		List<Edge> edgesDTWithOverlap = new ArrayList<Edge>();
		for (int i = 0; i < delaunayTriangulations.getNumGeometries(); i++) {

			Geometry edgeDelaunay = delaunayTriangulations.getGeometryN(i);

			Coordinate sourceCoordinate = edgeDelaunay.getCoordinates()[(0)];
			Coordinate targetCoordinate = edgeDelaunay.getCoordinates()[(1)];

			Vertex source = currentLayout.getNodeByCoordinate(sourceCoordinate);
			Vertex target = currentLayout.getNodeByCoordinate(targetCoordinate);

			// Add a Edge Delaunay between source and target
			int id = 0;//does not matter
			String label = "Edge Delaunay";
			int type = 0;//does not matter
			PositionShape position = new PositionShape(source.getPosition(), target.getPosition());
			boolean isVisible = false;
			Edge edgeDT = new Edge(id, new Etiqueta(label, false), position, new Stroke(), new Fill(), isVisible,
					EdgeType.getLayerTypeById(type), source.getId(), target.getId());
			currentLayout.addDTEdge(edgeDT);

			double radiusDistanceRelation = (source.getRadius() + target.getRadius()) / (edgeDT.getDistance());
			edgeDT.setOverlapFactor(Math.max(radiusDistanceRelation, 1));

			// Calculate the desired distances if the overlap factor > 1
			if (edgeDT.getOverlapFactor() > 1) {
				double dampingFactor = Math.min(sMaxDampingFactor, edgeDT.getOverlapFactor());
				double desiredDistance = dampingFactor * edgeDT.getDistance();
				edgeDT.setDesiredDistance(desiredDistance);
				//
				edgesDTWithOverlap.add(edgeDT);
			}
		}

		// Setting the order to calculate the layout
		// any order is implemented, order is the list on the
		// edgesDTCONProblemasDeOverlap
		orderNodeCalculateLayout = new IntIntHashMap();
		for (Edge edgeDT : edgesDTWithOverlap) {
			int sourceId = edgeDT.getIdSource();
			int targetId = edgeDT.getIdTarget();
			if (!orderNodeCalculateLayout.containsValue(sourceId)) {
				orderNodeCalculateLayout.put(orderNodeCalculateLayout.size(), sourceId);
			}
			if (!orderNodeCalculateLayout.containsValue(targetId)) {
				orderNodeCalculateLayout.put(orderNodeCalculateLayout.size(), targetId);
			}
		}

		// Loop the order node calculate layouts
		MutableIntSet mutableInt = orderNodeCalculateLayout.keySet();
		MutableIntIterator itr = mutableInt.intIterator();
		while (itr.hasNext()) {

			int idNode = (int) orderNodeCalculateLayout.get(itr.next());
			Vertex node = currentLayout.getListNode().get(idNode);
			
			if (!node.isFixed()) {

				PositionShape nuevaPosicion = stressMinimization(node, edgesDTWithOverlap);
				node.setPosition(nuevaPosicion);

			}
		}
	}

	private void borrarFictisios() {
		List<Vertex> aBorrar = new ArrayList<Vertex>();
		for (Vertex vertex : currentLayout.getListNode()) {
			if (vertex.isFixed()) {			
				aBorrar.add(vertex);
				//currentLayout.getListNode().remove(v.getId());
			}
		}
		for(Vertex v: aBorrar) {
			currentLayout.getListNode().remove(v.getId());			
		}
		
		
		//
//		List<Vertex> aBorrar = new ArrayList<Vertex>();
//		for (Vertex vertex : currentLayout.getListNode()) {
//			if (vertex.isFixed()) {
//				aBorrar.add(vertex);
//			}
//		}
//		// TODO esto de abajo ver
////		currentLayout.getListNode().removeAll(aBorrar);
		
	}

	private boolean hayAlgunFictisio() {
		for (Vertex vertex : currentLayout.getListNode()) {
			if (vertex.isFixed()) {
				return true;
			}
		}
		return false;
	}

	public void commencer() {
		// Until the configuration is stable and the stress cannot be reduced
		// further significantly

		for (int i = 0; i < 100; i++) {
			iteraction();
		}
		
		if (checkOverlapping) {
			// borrar fictisios
			if (hayAlgunFictisio()) {
				borrarFictisios();
			}
			currentLayout = GraphUtil.getGraphWithPointsIntersections(currentLayout);

			if (hayAlgunFictisio()) {
				commencer();
			}
		}

	}

	/**
	 * De-facto approach of computing the successive configuration with
	 * non-increasing stress
	 * 
	 * @param i
	 * @param currentEdgesConfiguration
	 * @return
	 */
	private PositionShape stressMinimization(Vertex i, List<Edge> currentEdgesConfiguration) {
		
		PositionShape iUpdatedCoordinate = new PositionShape();
		PositionShape summationNumerator = new PositionShape();
		
		double summationDeFactoWeighting = 0;

		// If Vertex i does not a fixed vertex
		if (!i.isFixed()) {

			// Loop the current overall configuration
			for (Edge edge : currentEdgesConfiguration) {

				// Verify that Vertex i is contained in the edge
				if (edge.containOneVertex(i.getId())) {

					Vertex j = null;
					if (edge.getIdSource() == i.getId()) {
						j = currentLayout.getListNode().get(edge.getIdTarget());
					} else {
						j = currentLayout.getListNode().get(edge.getIdSource());
					}

					// Sij DAMPING FACTOR
					double dampingFactor = Math.min(sMaxDampingFactor, edge.getOverlapFactor());

					// Wij DE-FACTO STANDAR WEIGHTING SCHEME
					double deFactoWeighting = 1 / Math.pow(edge.getDesiredDistance(), 2);

					// ---- STRESS FORMULA -----
					// Numerator
					PositionShape partial1 = PositionShape
							.multiply(PositionShape.rest(i.getPosition(), j.getPosition()), dampingFactor);
					PositionShape partial2 = PositionShape.sum(j.getPosition(), partial1);
					PositionShape numerator = PositionShape.multiply(partial2, deFactoWeighting);
					summationNumerator = PositionShape.sum(numerator, summationNumerator);

					// Denominator
					summationDeFactoWeighting = summationDeFactoWeighting + deFactoWeighting;
				}
			}

			iUpdatedCoordinate = PositionShape.divide(summationNumerator, summationDeFactoWeighting);

			return iUpdatedCoordinate;
		} else {
			return i.getPosition();
		}
	}


	public double getOverlapFactor() {
		return 0.d;
	}

	public Graph getGraph() {
		return currentLayout;
	}

	public void setGraph(Graph graph) {
		this.currentLayout = graph;
	}

}
