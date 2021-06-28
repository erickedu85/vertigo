package app.kelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

import app.dijkstra.Dijkstra;
import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import processing.core.PApplet;

/**
 * @author Erick
 *
 */
public class Kelp {

//	public static Logger logger = Logger.getLogger(Kelp.class);
//	public static PApplet parent;
//	private double toleranceVoronoi;
//	private double elementRadius;
//	private double costDistance;
//	private double costAngular;
//	private double costIntersection;
//	private double benefitThreshold;
//	private double linkRadius; // radius link
//
//	private Geometry voronoiDiagram;
//	private Graph embeddedGraphGA = new Graph(); // Embedded Graph
//	private Graph tangentGraphGT = new Graph(); // Tangent Graph
//	private List<Graph> graphGL = new ArrayList<Graph>(); // Graphs GL
//
//	private List<Vertex> elements = new ArrayList<Vertex>(); // Vertex
//	private List<Hyp> collectionOfSets = new ArrayList<Hyp>(); // Hyperedges
//
//	/**
//	 * Kelp diagram
//	 * 
//	 * @param elements
//	 *            Element E
//	 * @param collectionOfSets
//	 *            Hyperedges S
//	 */
//	public Kelp(List<Vertex> elements, List<Hyp> collectionOfSets) {
//		this.elements = elements;
//		this.collectionOfSets = collectionOfSets;
//	}
//
//	/**
//	 * Method to run the algorithm
//	 */
//	public void runAlgo() {
//		deriveVoronoiDiagram(); // create the voronoi faces
//		deriveEmbeddedGraphGA(); // create the GraphGA
//		deriveTangentGraphGT(); // create the GraphGT
//
//		// dibujar
//		derivedGraphGL(this.collectionOfSets); // create a GL for every
//
//		// List<Vertex> link = new ArrayList<Vertex>();
//		// link.add(elements.get(0));
//		// link.add(elements.get(2));
//		// // update intersections in Tangent Graph GT
//		// for (Edge e : edgeIntersectionLink(tangentGraphGT, link)) {
//		// double numeroIntersection = e.getNumberIntersection() + 1;
//		// e.setNumberIntersection(numeroIntersection);
//		// }
//	}
//
//	public List<Edge> dibujar() {
//		// create a GL for every Hyperedge in a graph
//		return derivedGraphGL(this.collectionOfSets);
//	}
//
//	/**
//	 * 
//	 * Create a Voronoi Faces Diagram based in the Elements
//	 */
//	private void deriveVoronoiDiagram() {
//		// Getting coordinates for every Element in Hypergraph
//		List<Coordinate> coordinates = new ArrayList<Coordinate>();
//		for (Vertex v : elements) {
//			coordinates.add(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1()));
//		}
//		//
//		VoronoiDiagramBuilder voronoiBuilder = new VoronoiDiagramBuilder();
//		voronoiBuilder.setClipEnvelope(new Envelope(0, 1000, 0, 1000));
//		voronoiBuilder.setSites(coordinates);
//		voronoiBuilder.setTolerance(toleranceVoronoi);
//		this.voronoiDiagram = voronoiBuilder.getDiagram(new GeometryFactory());
//	}
//
//	/**
//	 * Derived embedded Graph GA
//	 * 
//	 * GA = (VA, EA), where VA = VE + VI (intersect points)
//	 * 
//	 * Include: Elements Vertices (VA) and the Vertices for the intersection
//	 * (VI) points between Voronoi faces and circles
//	 * 
//	 * There is not yet an Edges
//	 */
//	private void deriveEmbeddedGraphGA() {
//		embeddedGraphGA = new Graph();
//		// Vertex of the origin
//		embeddedGraphGA.getListNode().addAll(this.elements);
//		// Vertex result of the Voronoi-Inters and the Allocated space_(Buffer)
//		embeddedGraphGA.getListNode().addAll(getVertexIntersecVoronoi());
//	}
//
//	/**
//	 * Derived tangent Graph GT from Graph GA
//	 * 
//	 * GA ====> GT = (VT, ET), where VT = VA + VT (vertex tangent points)
//	 * 
//	 */
//	private void deriveTangentGraphGT() {
//		tangentGraphGT = new Graph();
//		// adding all vertex of the GraphGA
//		tangentGraphGT.getListNode().addAll(embeddedGraphGA.getListNode());
//		tangentGraphGT.getListNode().addAll(tangentEdgesBetweenAllocSpaces());
//		tangentGraphGT.getListNode().addAll(edgesFromElementsToAllocSpaces());
//		addEdgesBetweenElements(); // Add edges
//		addEdgesSliptCircle(); // Add edges
//	}
//
//	public void kk() {
//
//		for (Hyp hyperedge : collectionOfSets) {
//
//		}
//
//		Graph g = null;
//		Vertex p = null;
//		Vertex q = null;
//
//		while (benefit(g, p, q) > this.benefitThreshold) {
//
//			g = new Graph();
//			p = null;
//			q = null;
//		}
//	}
//
//	/**
//	 * @param collectionOfSets
//	 */
//	public List<Edge> derivedGraphGL(List<Hyp> collectionOfSets) {
//
//		List<Edge> result = new ArrayList<Edge>();
//		// Falta derivar un GL(S) solo con los edges de ese S
//		for (Hyp hyperedge : collectionOfSets) {
//			
////			for (Vertex vertexCurrently : hyperedge.getLink()) {
////				for (Vertex vertexWorking : hyperedge.getLink()) {
////					if (vertexCurrently != vertexWorking) {
////						
////						
////						List<Edge> listEdge = new ArrayList<Edge>();
////						List<Vertex> listVertex = new ArrayList<Vertex>();
////						listEdge = edgesListAvoidSourceTarget(tangentGraphGT, vertexCurrently, vertexWorking);
////						listVertex = tangentGraphGT.getListNode();
////
////						Graph graphUsedForDisjktrat = new Graph(listVertex, listEdge);
////						// ITERATIVE-PLACEMENT-OF-LINKS-TO-PLACE-ONE-LINK,-SHORTES-PATHS-ARE-COMPUTED-TO-
////						// ALL-NODES-IN-TANGENTGRAPH-FOR-EVERY-ELEMENT-AND-FOR-EVERY-SET
////						// SELECTION THE BEST TO PLACE LINK BETWEEN P And Q
////						if (benefit(graphUsedForDisjktrat, vertexCurrently, vertexWorking) > this.benefitThreshold) {
////							System.out.println("Adding...");
////						}else{
////							System.out.println("FINISH...");
////						}
////
////						// result.addAll(retornarLink(graphFilter,
////						// vertexCurrently, vertexWorking));
////						// ADD EDGES OF LINK TO GL
////
////						// while (benefit(graphFilter, vertexCurrently,
////						// vertexWorking) > this.benefitThreshold) {
////						// //ADD LINK - ADD EDGES of L in TangentGraph ===>
////						// subgraph GL(S)
////						// //DERIVE "all-pairs" shortest paths of GL(S)
////						// //UPDATE TangentGraph with intersections introduced
////						// by L
////						// //DERIVE "all-pairs" shortest paths of TangentGraph
////						// for all R £ S
////						// //DERIVE Next benefit()
////						// }
////					}
////				}
////			}
//			
//			//derivedGraphGL(this.collectionOfSets);
//			
//		}
//		System.out.println("result : " + result);
//		return result;
//	}
//
//	/**
//	 * 
//	 * @return A list the vertex that intersect with the Buffer of the E
//	 *         Elements and the voronoi face
//	 */
//	private List<Vertex> getVertexIntersecVoronoi() {
//		List<Vertex> result = new ArrayList<Vertex>();
//		for (Vertex v : this.elements) {
//			Geometry pointBuffer = new GeometryFactory()
//					.createPoint(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1())).buffer(getBuffer());
//			for (int i = 0; i < voronoiDiagram.getNumGeometries(); i++) {
//				// IF-the-point-intersect-with-a-Buffer-point,-Then-create-a-new-Vertex
//				if (voronoiDiagram.getGeometryN(i).getBoundary().intersects(pointBuffer.getBoundary())) {
//					Geometry pointGeometry = voronoiDiagram.getGeometryN(i).getBoundary()
//							.intersection(pointBuffer.getBoundary());
//					for (int j = 0; j < pointGeometry.getNumGeometries(); j++) {
//						Integer id = this.elements.size() + result.size();
//						PositionShape position = new PositionShape(pointGeometry.getGeometryN(j).getCoordinate().x,
//								pointGeometry.getGeometryN(j).getCoordinate().y);
//						String label = "Node " + id;
//						Integer type = 0;
//						Stroke stroke = new Stroke(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_STROKE_OPACITY,
//								Constants.NODE_DEFAULT_STROKE_WEIGHT);
//						Fill fill = new Fill(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_FILL_OPACITY);
//						double radius = 5.0d;
//						boolean isLabelling = false;
//
//						Vertex vertex = new Vertex(id, label, type, position, stroke, fill, isLabelling, radius);
//
//						if (!result.contains(vertex)) {
//							result.add(vertex);
//						}
//					}
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Method to get the GEOMETRY points of a Graph G
//	 * 
//	 * @param g
//	 *            Graph g
//	 * @return The GEOMETRY points of a Graph G
//	 */
//	public List<Geometry> getGeometryGraphPoints(Graph g) {
//		List<Geometry> result = new ArrayList<Geometry>();
//		for (Vertex v : g.getListNode()) {
//			Geometry point = new GeometryFactory()
//					.createPoint(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1()));
//			result.add(point);
//		}
//		return result;
//	}
//
//	/**
//	 * Method to get the GEOMETRY lines of a Graph
//	 *
//	 * @param g
//	 *            Graph g
//	 * @return The GEOMETRY Lines of a graph
//	 */
//	public List<Geometry> getGeometryGraphLines(Graph g) {
//		List<Geometry> result = new ArrayList<Geometry>();
//		for (Edge e : g.getListEdge()) {
//			Coordinate[] lineCoordinates = new Coordinate[] {
//					new Coordinate(e.getPosition().getX1(), e.getPosition().getY1()),
//					new Coordinate(e.getPosition().getX2(), e.getPosition().getY2()) };
//			Geometry line = new GeometryFactory().createLineString(lineCoordinates);
//			result.add(line);
//		}
//		return result;
//	}
//
//	/**
//	 * Return a GEOMETRY list of the edge in a Path calculed by Dijkstra
//	 * 
//	 * @param edgePath
//	 *            Path of edges
//	 * @return A Geometry List
//	 */
//	public List<Geometry> getGeometryGraphLines(Graph g, LinkedList<Edge> edgePath) {
//		List<Geometry> result = new ArrayList<Geometry>();
//		for (Edge e : g.getListEdge()) {
//			Coordinate[] lineCoordinates = new Coordinate[] {
//					new Coordinate(e.getPosition().getX1(), e.getPosition().getY1()),
//					new Coordinate(e.getPosition().getX2(), e.getPosition().getY2()) };
//			Geometry line = new GeometryFactory().createLineString(lineCoordinates);
//
//			if (edgePath.contains(e))
//				result.add(line);
//		}
//		return result;
//	}
//
//	/**
//	 * Remove Edges between Elements, except for edges vertexSource and
//	 * vertexTarget
//	 * 
//	 * @param vertexSource
//	 *            Vertex Source
//	 * @param vertexTarget
//	 *            Vertex Target
//	 * @return The new list of Edges, deleting the innecesaries
//	 */
//	public List<Edge> edgesListAvoidSourceTarget(Graph g, Vertex vertexSource, Vertex vertexTarget) {
//		List<Edge> result = new ArrayList<Edge>();
//		for (Edge e : g.getListEdge()) {
//			// quiere decir que en el edge hay vertex de Elements
//			if (revisarElements(e, this.elements)) {
//				if (e.containVertices(vertexSource.getId(), vertexTarget.getId())) {
//					result.add(e);
//				}
//			} else {
//				result.add(e);
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Method to get if vertex source and vertex target are the Edge e
//	 * 
//	 * @param e
//	 *            Edge
//	 * @param elements
//	 *            Elements List<Vertex>
//	 * @return true si algunos de los vertices estan en el Edge, falso sino
//	 */
//	private boolean revisarElements(Edge e, List<Vertex> elements) {
//		for (Vertex vertexCurrently : elements) {
//			for (Vertex vertexWorking : elements) {
//				if (vertexCurrently != vertexWorking) {
//					if (e.containVertices(vertexCurrently.getId(), vertexWorking.getId())) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Get tangent-points from Allocate spaces
//	 * 
//	 * Also add the EDGES
//	 * 
//	 * @param elements
//	 *            Vertex E
//	 * 
//	 * @return List of tangent points
//	 */
//	private List<Vertex> tangentEdgesBetweenAllocSpaces() {
//		List<Vertex> result = new ArrayList<Vertex>();
//		for (Vertex vCurrently : elements) {
//			for (Vertex vWorking : elements) {
//				if (vCurrently != vWorking) {
//					double[][] tangentList = GeoAnalytic.tangentPoints2Circles(vCurrently.getPosition().getX1(),
//							vCurrently.getPosition().getY1(), getBuffer(), vWorking.getPosition().getX1(),
//							vWorking.getPosition().getY1(), getBuffer());
//					for (int i = 0; i < tangentList.length; i++) {
//						Integer id = embeddedGraphGA.maxIdNode() + result.size();
//						PositionShape position1 = new PositionShape(tangentList[i][0], tangentList[i][1]);
//						PositionShape position2 = new PositionShape(tangentList[i][2], tangentList[i][3]);
//
//						Stroke stroke = new Stroke(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_STROKE_OPACITY,
//								Constants.NODE_DEFAULT_STROKE_WEIGHT);
//						Fill fill = new Fill(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_FILL_OPACITY);
//
//						String label1 = "Node " + id;
//						String label2 = "Node " + (id + 1);
//						Integer type = 0;
//						double radius = 5.0d;
//						boolean isLabelling = false;
//
//						Vertex v1 = new Vertex(id, label1, type, position1, stroke, fill, isLabelling, radius);
//						Vertex v2 = new Vertex((id + 1), label2, type, position2, stroke, fill, isLabelling, radius);
//						if (!crossesAllocatedSpace(position1, position2, vCurrently, vWorking)) {
//							if (!result.contains(v1) && !result.contains(v2)) {
//								result.add(v1);
//								result.add(v2);
//								addEdge(v1, v2, 66);
//							}
//						}
//					}
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Get tangent-points from an Element to an allocate space
//	 * 
//	 * Also add the EDGES
//	 * 
//	 * @param elements
//	 *            Vertex E
//	 * @return List of tangent points
//	 */
//	public List<Vertex> edgesFromElementsToAllocSpaces() {
//		List<Vertex> result = new ArrayList<Vertex>();
//		for (Vertex vCurrently : elements) {
//			for (Vertex vWorking : elements) {
//				if (vCurrently != vWorking) {
//					PositionShape[] p = GeoAnalytic.tangentPointToCircle(vCurrently.getPosition(),
//							vWorking.getPosition(), getBuffer());
//					for (int i = 0; i < p.length; i++) {
//						Integer id = tangentGraphGT.maxIdNode() + result.size();
//						PositionShape position1 = new PositionShape(p[i]);
//
//						Stroke stroke = new Stroke(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_STROKE_OPACITY,
//								Constants.NODE_DEFAULT_STROKE_WEIGHT);
//						Fill fill = new Fill(true, ColorShape.getHSB_Black(), Constants.NODE_DEFAULT_FILL_OPACITY);
//
//						String label = "Node " + id;
//						Integer type = 0;
//						double radius = 5.0d;
//						boolean isLabelling = false;
//
//						Vertex v1 = new Vertex(id, label, type, position1, stroke, fill, isLabelling, radius);
//						// Vertex v1 = new Vertex(id, position1, label, type,
//						// isVisibleFill, isVisibleStroke, radius);
//						if (!crossesAllocatedSpace(vCurrently.getPosition(), position1, vCurrently, vWorking)) {
//							if (!result.contains(v1)) {
//								result.add(v1);
//								addEdge(v1, vCurrently, 66);
//							}
//						}
//					}
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Method to add edge to graphGT
//	 * 
//	 * @param v1
//	 * @param v2
//	 * @param type
//	 */
//	private void addEdge(Vertex v1, Vertex v2, Integer type) {
//		if (!tangentGraphGT.isEdgesFromVerticesPair(v1.getId(), v2.getId())) {
//			int idEdge = tangentGraphGT.getListEdge().size();
//			String labelEdge = "Edge from node " + v1.getId() + " to node " + v2.getId();
//			// int typeEdge = 1; // +1-because-it-is-index
//			PositionShape positionEdge = new PositionShape(v1.getPosition(), v2.getPosition());
//			double distance = 0.0d;
//			tangentGraphGT.addEdge(new Edge(idEdge, labelEdge, type, positionEdge, null, null, false, v1.getId(),
//					v2.getId(), distance));
//		}
//	}
//
//	/**
//	 * Add the Edges between Elements E
//	 */
//	private void addEdgesBetweenElements() {
//		for (Vertex vCurrently : this.elements) {
//			for (Vertex vWorking : this.elements) {
//				if (vCurrently != vWorking) {
//					if (!crossesAllocatedSpace(vCurrently.getPosition(), vWorking.getPosition(), vCurrently,
//							vWorking)) {
//						addEdge(vWorking, vCurrently, 66);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Method to add the Edges that Split up the Allocated Space
//	 */
//	private void addEdgesSliptCircle() {
//		for (Vertex v : this.elements) {
//			Map<Double, Vertex> listVertexCircle = new HashMap<Double, Vertex>();
//			Geometry pointBuffer = new GeometryFactory()
//					.createPoint(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1()))
//					.buffer(linkRadius + elementRadius);
//			for (Vertex v2 : tangentGraphGT.getListNode()) {
//				if (v != v2) {
//					Geometry point = new GeometryFactory()
//							.createPoint(new Coordinate(v2.getPosition().getX1(), v2.getPosition().getY1()));
//					if (pointBuffer.getBoundary().intersects(point.buffer(5))) {
//						double angle = GeoUtil.angleOfPolarCoordinates(v2.getPosition().getX1(),
//								v2.getPosition().getY1(), v.getPosition().getX1(), v.getPosition().getY1());
//						// System.out.println("angle : " +
//						// GeoUtil.angleOfPolarCoordinates(v2.getPosition().getX1(),
//						// v2.getPosition().getY1(), v.getPosition().getX1(),
//						// v.getPosition().getY1()));
//						// System.out.println("*************************************************************");
//						listVertexCircle.put(angle, v2);
//					}
//				}
//			}
//
//			List<Vertex> lVertex = new ArrayList<Vertex>();
//			SortedSet<Double> keys = new TreeSet<Double>(listVertexCircle.keySet());
//			for (Double key : keys) {
//				Vertex value = listVertexCircle.get(key);
//				lVertex.add(value);
//				if (key.equals(keys.last())) {
//					lVertex.add(listVertexCircle.get(keys.first()));
//				}
//			}
//
//			for (int i = 0; i < lVertex.size() - 1; i++) {
//				// System.out.println(lVertex.get(i));
//				// System.out.println("adding : " + lVertex.get(i).getId() + " -
//				// " + lVertex.get(i + 1).getId());
//				addEdge(lVertex.get(i), lVertex.get(i + 1), 66);
//			}
//		}
//	}
//
//	/**
//	 * Method to verified if line crosses any circular allocated space of the
//	 * Elements e except the vCurrently and vWorking allocated space
//	 * 
//	 * @param pointBegin
//	 *            Begin point line (EDGE)
//	 * @param pointEnd
//	 *            End point line (EDGE)
//	 * @param vCurrently
//	 *            vertex
//	 * @param vWorking
//	 *            vertex
//	 * @return true if crosses, false else
//	 */
//	private boolean crossesAllocatedSpace(PositionShape pointBegin, PositionShape pointEnd, Vertex vCurrently,
//			Vertex vWorking) {
//		boolean result = false;
//		Coordinate[] lineCoordinates = new Coordinate[] { new Coordinate(pointBegin.getX1(), pointBegin.getY1()),
//				new Coordinate(pointEnd.getX1(), pointEnd.getY1()) };
//		Geometry line = new GeometryFactory().createLineString(lineCoordinates);
//		for (Vertex v : this.elements) {
//			if (v != vCurrently && v != vWorking) {
//				Geometry pointBuffer = new GeometryFactory()
//						.createPoint(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1()))
//						.buffer(getBuffer());
//				if (line.intersects(pointBuffer.getBoundary())) {
//					result = true;
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Method to get a List of Edges where a link intersects in a Graph g
//	 * 
//	 * @param g
//	 *            Graph
//	 * @param link
//	 *            List<Edge>
//	 * @return a list of edges where Link intersect in a Graph g
//	 */
//	private List<Edge> edgeIntersectionLink(Graph g, List<Vertex> link) {
//		// Creation of link
//		List<Coordinate> positions = new ArrayList<Coordinate>();
//		for (Vertex v : link) {
//			positions.add(new Coordinate(v.getPosition().getX1(), v.getPosition().getY1()));
//		}
//		Coordinate[] coordinates = positions.toArray(new Coordinate[positions.size()]);
//		Geometry lineLink = new GeometryFactory().createLineString(coordinates);
//		// ----------------------------
//		System.out.println("link: " + lineLink);
//
//		// Result intersection link
//		List<Edge> result = new ArrayList<Edge>();
//		for (Edge e : g.getListEdge()) {
//			Coordinate[] edgeCoordinates = new Coordinate[] {
//					new Coordinate(e.getPosition().getX1(), e.getPosition().getY1()),
//					new Coordinate(e.getPosition().getX2(), e.getPosition().getY2()) };
//			Geometry lineEdge = new GeometryFactory().createLineString(edgeCoordinates);
//
//			boolean isIntersected = lineLink.intersects(lineEdge);
//			System.out.println("Intersect? " + isIntersected);
//			if (isIntersected) {
//				Geometry intersection = lineLink.intersection(lineEdge);
//				System.out.println(
//						lineEdge + ", intersection in " + intersection.getNumGeometries() + " point: " + intersection);
//				result.add(e);
//			}
//		}
//		return result;
//	}
//
//	private LinkedList<Edge> retornarLink(Graph g, Vertex p, Vertex q) {
//
//		Dijkstra dijkstra = new Dijkstra(g);
//		LinkedList<Edge> link = dijkstra.getEdgePath(p, q);
//
//		return link;
//	}
//
//	/**
//	 * Method to get the benefit of place a link L between p and q
//	 * 
//	 * @param g
//	 *            Graph
//	 * @param p
//	 *            Vertex
//	 * @param q
//	 *            Vertex
//	 * @return The benefit between p and q Vertices in a Graph g
//	 */
//	private double benefit(Graph g, Vertex p, Vertex q) {
//
//		Dijkstra dijkstra = new Dijkstra(g);
//		LinkedList<Edge> link = dijkstra.getEdgePath(p, q);
//
//		double benefit = 0.0d;
//		double distance = minPathDistanceLink(link);
//		double cost = costOfLink(link);
//
//		benefit = distance / cost;
//		System.out.println(link);
//		System.out.println("p: " + p.getId() + ", q: " + q.getId() + ", benefit: " + benefit);
//
//		return benefit;
//	}
//
//	// Derive All-pairs Shortest Path
//	private void deriveAPSP(Graph g) {
//		Dijkstra dijkstra = new Dijkstra(g);
//		for (Vertex vCurrently : g.getListNode()) {
//			for (Vertex vWorking : g.getListNode()) {
//				if (vCurrently != vWorking) {
//
//				}
//			}
//		}
//	}
//
//	/**
//	 * Method to get the distance of a Link L
//	 * 
//	 * @param link
//	 *            a Path of edges
//	 * @return The distance of link
//	 */
//	private double minPathDistanceLink(LinkedList<Edge> link) {
//		double distance = 0.0d;
//		for (Edge edge : link) {
//			distance += edge.getDistance();
//		}
//		return distance;
//	}
//
//	/**
//	 * Method to get the cost of a link L
//	 * 
//	 * @param link
//	 *            a Path of edges
//	 * @return The cost of the Link
//	 */
//	private double costOfLink(LinkedList<Edge> link) {
//		double cost = 0.0d;
//		for (Edge edge : link) {
//			double costEdge = (this.costDistance * edge.getDistance() + this.costAngular * edge.getAngle()
//					+ this.costIntersection * edge.getNumberIntersection());
//			cost += costEdge;
//		}
//		return cost;
//	}
//
//	public Geometry getVoronoiDiagram() {
//		return voronoiDiagram;
//	}
//
//	public Graph getEmbeddedGraphGA() {
//		return embeddedGraphGA;
//	}
//
//	public Graph getTangentGraphGT() {
//		return tangentGraphGT;
//	}
//
//	public double getCostDistance() {
//		return costDistance;
//	}
//
//	public void setCostDistance(double costDistance) {
//		this.costDistance = costDistance;
//	}
//
//	public double getCostAngular() {
//		return costAngular;
//	}
//
//	public void setCostAngular(double costAngular) {
//		this.costAngular = costAngular;
//	}
//
//	public double getCostIntersection() {
//		return costIntersection;
//	}
//
//	public void setCostInterseccion(double costIntersection) {
//		this.costIntersection = costIntersection;
//	}
//
//	public double getToleranceVoronoi() {
//		return toleranceVoronoi;
//	}
//
//	public void setToleranceVoronoi(double toleranceVoronoi) {
//		this.toleranceVoronoi = toleranceVoronoi;
//	}
//
//	public double getBenefitThreshold() {
//		return benefitThreshold;
//	}
//
//	public void setBenefitThreshold(double benefitThreshold) {
//		this.benefitThreshold = benefitThreshold;
//	}
//
//	public double getElementRadius() {
//		return elementRadius;
//	}
//
//	public void setElementRadius(double elementRadius) {
//		this.elementRadius = elementRadius;
//	}
//
//	public double getLinkRadius() {
//		return linkRadius;
//	}
//
//	public void setLinkRadius(double linkRadius) {
//		this.linkRadius = linkRadius;
//	}
//
//	public List<Graph> getGraphGL() {
//		return graphGL;
//	}
//
//	public double getBuffer() {
//		return this.elementRadius + this.linkRadius;
//	}

}
