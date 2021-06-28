package app.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

import app.graph.structure.Edge;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;

public class JtsUtil {

	public static Logger logger = Logger.getLogger(JtsUtil.class);

	/**
	 * Build a delaunay triangulations based on all coordinates of the geometry
	 * 
	 * @param geometry
	 * @param opts
	 * @return opts =0 return the the Triangles, 1 return the Edges
	 */
	public static GeometryCollection delaunayTriangulations(Geometry geometry, int opts) {
		DelaunayTriangulationBuilder triangulationBuilder = new DelaunayTriangulationBuilder();
		triangulationBuilder.setSites(geometry);

		if (opts == 0) {
			return (GeometryCollection) triangulationBuilder.getTriangles(geometry.getFactory());
		} else if (opts == 1) {
			return (GeometryCollection) triangulationBuilder.getEdges(geometry.getFactory());
		} else {
			return null;
		}
	}

	/**
	 * @param geometry
	 * @return
	 */
	public static Geometry voronoiDiagram(Geometry geometry) {
		VoronoiDiagramBuilder voronoiBuilder = new VoronoiDiagramBuilder();
		voronoiBuilder.setClipEnvelope(new Envelope(0, 1000, 0, 1000));
		voronoiBuilder.setSites(geometry);
		return voronoiBuilder.getDiagram(geometry.getFactory());
	}

	/**
	 * Method to get just a virtual nodes if a node intersect an edge
	 * 
	 * @param node
	 * @param edge
	 * @return Node intersection, otherwise null
	 */
	public static Vertex getPointIntersectionNodeToEdge(Vertex node, Edge edge) { //, Edge edge

		// Get the coordinates of the node
		// Node buffer is the radius
		Geometry geometryNode = new GeometryFactory()
				.createPoint(new Coordinate(node.getPosition().getX1(), node.getPosition().getY1()))
//				.buffer(node.getRadius() / 2 - 1.5); // 1.5
				.buffer(node.getRadius()); // 1.5

		// Get the coordinates of the edge
		// Edge buffer is the stroke weight
		Coordinate[] coordinatesEdge = new Coordinate[] {
				new Coordinate(edge.getPosition().getX1(), edge.getPosition().getY1()),
				new Coordinate(edge.getPosition().getX2(), edge.getPosition().getY2()) };
		Geometry geometryEdge = new GeometryFactory().createLineString(coordinatesEdge)
				.buffer(edge.getStroke().getStrokeWeight() / 2 - 1.5); // 2

		
		if (geometryEdge.contains(geometryNode)) {
			// means the geometry of Node is inside the geometry edge
			// means centroid and center of circle are the same
//			logger.info("CONTIENE INSIDE");

			PositionShape nodePosition = new PositionShape(node.getPosition().getX1() + 5, node.getPosition().getY1());

			int id = 0;
			String label = "Ficticio";
			boolean isLabelled = false;
			boolean isVisible = false;
			Fill fill = new Fill();
			Stroke stroke = new Stroke();

			// Get the diameter
			double diameter = edge.getStroke().getStrokeWeight() - 2;
			Vertex vertexIntersection = new Vertex(id, new Etiqueta(label, false), nodePosition, stroke, fill,
					isVisible, diameter, true);

//			logger.info("++++++++++++++++++++++++++++++++++++++");

			return vertexIntersection;

		} else if (geometryEdge.intersects(geometryNode)) {
			Geometry pointsIntersection = geometryEdge.intersection(geometryNode);

			Point centroid = pointsIntersection.getCentroid();

			PositionShape nodePosition = new PositionShape(centroid.getCoordinate().x, centroid.getCoordinate().y);

			int id = 0;
			String label = "Ficticio";
			int state = Constants.GRAPH_DB_NODE_FIXED;
			boolean isLabelled = false;
			boolean isVisible = false;
			Fill fill = new Fill();
			Stroke stroke = new Stroke();

			// Get the diameter
			double minRadius = Float.MAX_VALUE;
			for (int i = 0; i < pointsIntersection.getNumPoints(); i++) {
				Point anExternalPoint = new GeometryFactory().createPoint(pointsIntersection.getCoordinates()[i]);
				double distance = anExternalPoint.distance(centroid);
				minRadius = Math.min(minRadius, distance);
			}
			double diameter = minRadius * 2;

			Vertex vertexIntersection = new Vertex(id, new Etiqueta(label, isLabelled), nodePosition, stroke, fill,
					isVisible, diameter, true);

			Geometry geometryNodeIntersection = new GeometryFactory().createPoint(
					new Coordinate(vertexIntersection.getPosition().getX1(), vertexIntersection.getPosition().getY1()))
//					.buffer(vertexIntersection.getRadius() / 2 - 1.5);
					.buffer(vertexIntersection.getRadius());

//			 logger.info("Node:" + node + " Intersect: " + edge);
//			 logger.info(geometryNode);
//			 logger.info(geometryEdge);
//			 logger.info("Polygon Intersection: " +
//			 pointsIntersection.getNumGeometries());
//			 logger.info("centroid: " + centroid);
//			 logger.info("nueva geo: " + geometryNodeIntersection);
//			 logger.info("---------------------------------------");

			return vertexIntersection;
		}

		return null;
	}

	/**
	 * @param listEdges
	 * @return
	 */
	@Deprecated
	public static Coordinate[] getEdgeCoordinates(List<Edge> listEdges) {
		Edge edgeInitial = listEdges.get(0);
		double x1Initial = edgeInitial.getPosition().getX1();
		double y1Initial = edgeInitial.getPosition().getY1();
		double x2Initial = edgeInitial.getPosition().getX2();
		double y2Initial = edgeInitial.getPosition().getY2();
		double bufferInitial = edgeInitial.getStroke().getStrokeWeight();
		Geometry unionFinal = new GeometryFactory()
				.createLineString(
						new Coordinate[] { new Coordinate(x1Initial, y1Initial), new Coordinate(x2Initial, y2Initial) })
				.buffer(bufferInitial);

		for (Edge edge : listEdges) {
			double x1 = edge.getPosition().getX1();
			double y1 = edge.getPosition().getY1();
			double x2 = edge.getPosition().getX2();
			double y2 = edge.getPosition().getY2();
			double buffer = edge.getStroke().getStrokeWeight();

			Geometry edgeGeometry = new GeometryFactory()
					.createLineString(new Coordinate[] { new Coordinate(x1, y1), new Coordinate(x2, y2) })
					.buffer(buffer);

			unionFinal = unionFinal.union(edgeGeometry).union();
		}
		return null;
	}

}
