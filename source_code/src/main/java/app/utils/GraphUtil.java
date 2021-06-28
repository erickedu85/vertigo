package app.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;

import app.graph.structure.Edge;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Multiedge;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.mst.EdgeMst;
import app.mst.EdgeWeightedGraph;
import app.mst.LazyPrimMST;
import app.overlap.VerticesOverlap;
import processing.core.PApplet;
import processing.core.PVector;

public class GraphUtil {

	public static Logger logger = Logger.getLogger(GraphUtil.class);

	/**
	 * Method to translate position of a point according to a factor (zoom)
	 * 
	 * @param adjacent
	 * @param opposite
	 * @param zoomFactor factor
	 * @return PositionShape object
	 */
	public static PositionShape translatePosition(double adjacent, double opposite, double zoomFactor) {
		double newHypotenuse = (MathUtil.calculateHypotenuse(adjacent, opposite)) * zoomFactor;
		double angleRadians = MathUtil.calculateTangent(opposite, adjacent);
		double coordinateX = Math.cos(angleRadians) * newHypotenuse;
		double coordinateY = Math.sin(angleRadians) * newHypotenuse;

		return new PositionShape(coordinateX, coordinateY);
	}

	/**
	 * Method to get the distance between 2 points is smaller than tolerance
	 * 
	 * @param xCenter1  X point 1
	 * @param yCenter1  Y point 1
	 * @param xCenter2  X point 2
	 * @param yCenter2  Y point 2
	 * @param tolerance
	 * @return true or false
	 */
	public static boolean isTwoPointDistSmallTolerance(double xCenter1, double yCenter1, double xCenter2,
			double yCenter2, double tolerance) {
		// .DIST calculate the distance between 2 points
		return PApplet.dist((float) xCenter1, (float) yCenter1, (float) xCenter2, (float) yCenter2) < tolerance;
	}

	/**
	 * PVector thePoint we will check if it is close to our line.
	 *
	 * PVector theLineEndPoint1 one end of the line.
	 *
	 * PVector theLineEndPoint2 the second end of the line.
	 *
	 * int theTolerance how close thePoint must be to our line to be recogized.
	 */
	public static boolean isOnePointDistSmallTolerToLine(PVector thePoint, PVector theLineEndPoint1,
			PVector theLineEndPoint2, double theTolerance) {

		PVector dir = new PVector(theLineEndPoint2.x, theLineEndPoint2.y, theLineEndPoint2.z);
		dir.sub(theLineEndPoint1);
		PVector diff = new PVector(thePoint.x, thePoint.y, 0);
		diff.sub(theLineEndPoint1);

		// inside distance determines the weighting
		// between linePoint1 and linePoint2
		float insideDistance = diff.dot(dir) / dir.dot(dir);

		if (insideDistance > 0 && insideDistance < 1) {
			PVector closest = new PVector(theLineEndPoint1.x, theLineEndPoint1.y, theLineEndPoint1.z);
			dir.mult(insideDistance);
			closest.add(dir);
			PVector d = new PVector(thePoint.x, thePoint.y, 0);
			d.sub(closest);
			float distsqr = d.dot(d);

			// check the distance of thePoint to the line against our tolerance.
			return (distsqr < Math.pow(theTolerance, 2));
		}
		return false;
	}

	/**
	 * Method to generate the key for the source, target and type of an edge
	 * 
	 * @param source
	 * @param target
	 * @param type
	 * @return
	 */
//	public static int edgeIdFunction(int source, int target, int type) {
//		return (int) (Math.pow(source, 2) + Math.pow(target, 2) + Math.pow(type, 2));
//	}

	/**
	 * Method to generate a key for the source and target. Use the pairingFunction.
	 * Internally, Source always < Target to get always the result, because 1 2 != 2
	 * 1
	 * 
	 * @param source
	 * @param target
	 * @return pairing number of source and target
	 */
	public static long pairingFunction(int source, int target) {
		int trueSource = Math.min(source, target);
		int trueTarget = Math.max(source, target);

		BigInteger num1 = new BigInteger(String.valueOf(trueSource));
		BigInteger num2 = new BigInteger(String.valueOf(trueTarget));

		BigInteger clave = pairingFunction(num1, num2);

		return clave.longValue();
	}

	/**
	 * https://en.wikipedia.org/wiki/Pairing_function a process to uniquely encode
	 * two natural numbers into a single natural number.
	 * 
	 * @param num1
	 * @param num2
	 */
	private static BigInteger pairingFunction(BigInteger num1, BigInteger num2) {
		BigInteger uno = new BigInteger("1");
		BigInteger dos = new BigInteger("2");

		BigInteger a = num1.add(num2);
		BigInteger b = a.add(uno);
		BigInteger c = a.multiply(b);
		BigInteger d = c.divide(dos);
		BigInteger e = d.add(num2);

		return e;
	}

	/**
	 * Method to divide a Diameter by N parts
	 * 
	 * @param numberOfEdges N
	 * @param diameter      Diameter
	 * @return
	 */
	public static double[] divideDiameterByN(int numberOfEdges, double diameter) {

		double radius = diameter / 2;
		int numTimeDivDiameter = numberOfEdges + 1;
		double diameterDiv = diameter / numTimeDivDiameter;

		double[] result = new double[numberOfEdges];
		for (int i = 0; i < numberOfEdges; i++) {
			double newRadius = radius - (diameterDiv * (i + 1));
			// newRadius = 0 means that edge is in the center
			if (newRadius > 0) {
				result[i] = newRadius;
			}
		}
		return result;
	}

	/**
	 * @param g
	 * @return
	 */
	public static Graph getNewlyGraphParserOrderIds(Graph g) {
		if (g instanceof Graph) {

			// filling the parser
			HashMap<Integer, Integer> keyNewlyKey = new HashMap<Integer, Integer>();
			for (Multiedge multiedge : g.getListMultiedge()) {
				
				if (!keyNewlyKey.containsKey(multiedge.getIdSource())) {
					keyNewlyKey.put(multiedge.getIdSource(), keyNewlyKey.size());
				}
				
				if (!keyNewlyKey.containsKey(multiedge.getIdTarget())) {
					keyNewlyKey.put(multiedge.getIdTarget(), keyNewlyKey.size());
				}		
				
			}
			
			// filling the parser
//			HashMap<Integer, Integer> keyNewlyKey = new HashMap<Integer, Integer>();
//			for (Vertex node : g.getListNode()) {
//				if (!keyNewlyKey.containsKey(node.getId())) {
//					keyNewlyKey.put(node.getId(), keyNewlyKey.size());
//				}
//			}
			//
			
			Graph newlyGraph = new Graph();
			// Changing the nodes id's and edges source and target
			for (Vertex node : g.getListNode()) {
				Vertex newlyNode = new Vertex(node);
				newlyNode.setId(keyNewlyKey.get(node.getId()));
				newlyGraph.addNode(newlyNode);
			}
			for (Multiedge multiedge : g.getListMultiedge()) {
				
				int newIdSource = keyNewlyKey.get(multiedge.getIdSource());
				int newIdTarget = keyNewlyKey.get(multiedge.getIdTarget());
				
				multiedge.setIdSource(newIdSource);
				multiedge.setIdTarget(newIdTarget);
				multiedge.setId(GraphUtil.pairingFunction(newIdSource, newIdTarget));
				
				
				for (Edge edge : multiedge.getListEdge()) {
					edge.setIdSource(newIdSource);
					edge.setIdTarget(newIdTarget);
					newlyGraph.addEdgeInMultiedge(edge);
				}
			}
			return newlyGraph;
		}
		return null;
	}

	/**
	 * Method to get a Graph filtered by list nodes
	 * 
	 * @param g
	 * @return
	 */

	public static Graph getNewlyGraphFromListNodes(Graph g, final List<Integer> listIdNodes) {
		if(g instanceof Graph && listIdNodes.size()>0) {
			
			MutableCollection<Vertex> listOfVertex = g.getListNode().select(new Predicate<Vertex>() {
				public boolean accept(Vertex vertex) {
					if(listIdNodes.contains(vertex.getId()))
						return true;
					return false;
				}
			});
			
			MutableCollection<Multiedge> listOfMultiedges = g.getListMultiedge().select(new Predicate<Multiedge>() {
				public boolean accept(Multiedge multiedge) {
					if(listIdNodes.contains(multiedge.getIdSource()) 
							&& listIdNodes.contains(multiedge.getIdTarget()))
						return true;
					return false;
				}
			});
			
			Graph newlyGraphMST = new Graph(listOfVertex,listOfMultiedges);
			
			return newlyGraphMST;
		}
		return null;
	}

	/**
	 * Method to get a Graph with unique edges between nodes with the Initial
	 * topological Structure
	 * 
	 * @param g
	 * @return
	 */
	public static Graph getNewlyGraphTopology(Graph g) {
		if (g instanceof Graph) {
			Graph newlyGraphTopolgy = new Graph(g);
			// Merge multiedges
			newlyGraphTopolgy.mergeMultiedges();
			return newlyGraphTopolgy;
		}
		return null;
	}

	/**
	 * Method to get a Graph with unique edges between nodes with the Minimum
	 * Spanning Tree Structure
	 * 
	 * @return newly Graph MST
	 */
	public static Graph getNewlyGraphMST(Graph g) {

		if (g instanceof Graph) {

			Graph newlyGraphMST = new Graph(g);

			// Prim MST, EdgeWeightedGraph
			EdgeWeightedGraph edgeWeightedGraph = new EdgeWeightedGraph(newlyGraphMST.getListNode().size());

			Map<Integer, Integer> edgesParseados = new HashMap<Integer, Integer>();
			for (Multiedge multiedge : newlyGraphMST.getListMultiedge()) {
				// edges parser
				if (!edgesParseados.containsKey(multiedge.getIdSource())) {
					edgesParseados.put(multiedge.getIdSource(), edgesParseados.size());
				}
				if (!edgesParseados.containsKey(multiedge.getIdTarget())) {
					edgesParseados.put(multiedge.getIdTarget(), edgesParseados.size());
				}
				//
				int nodeSource = edgesParseados.get(multiedge.getIdSource());
				int nodeTarget = edgesParseados.get(multiedge.getIdTarget());
				edgeWeightedGraph
						.addEdge(new EdgeMst(nodeSource, nodeTarget, multiedge.getListEdge().getFirst().getDistance(),
								multiedge.getIdSource(), multiedge.getIdTarget()));
			}

			LazyPrimMST mst = new LazyPrimMST(edgeWeightedGraph);
			List<EdgeMst> mstList = new ArrayList<EdgeMst>();
			for (EdgeMst e : mst.edges()) {
				mstList.add(e);
			}

			// Loop the MST edges to match the origial multiedge
			LongObjectHashMap<Multiedge> listTmpMultiedgeMST = new LongObjectHashMap();
			for (EdgeMst edgeMst : mstList) {
				int idSource = edgeMst.getvTrue();
				int idTarget = edgeMst.getwTrue();
				Multiedge multiedge = newlyGraphMST.getMultiedgeBetween(idSource, idTarget);
				listTmpMultiedgeMST.put(multiedge.getId(), new Multiedge(multiedge));
			}
			// Clear
			newlyGraphMST.getListMultiedge().clear();
			// Fill multiedges with multiedges MST
			newlyGraphMST.getListMultiedge().putAll(listTmpMultiedgeMST);
			// Merge multiedges
			newlyGraphMST.mergeMultiedges();

			return newlyGraphMST;
		}
		return null;
	}

	/**
	 * Method to fusion G1 and G2 in a new Graph Fusion.
	 * 
	 * @param g1 Graph 1
	 * @param g2 Graph 2
	 * @return a New Graph fusion
	 */
	public static Graph fusion(Graph g1, Graph g2) {
		if (g1 instanceof Graph && g2 instanceof Graph) {
			// init the fusionGraph with g1
			Graph fusionGraph = new Graph(g1);
			// iterate multiedges in G2
			for (Multiedge multiedgeG2 : g2.getListMultiedge()) {
				boolean existMultiedgeG2InFusion = fusionGraph.existeMultiedgeBetween(multiedgeG2.getIdSource(),
						multiedgeG2.getIdTarget());
				if (existMultiedgeG2InFusion) {
					// MultiedgeG2 exists in Fusion Graph
					// Fusion the edges
					fusionGraph.getListMultiedge().get(multiedgeG2.getId()).fusionAListEdges(multiedgeG2.getListEdge());
				} else {
					// MultiedgeG2 does not exist in Fusion Graph
					// Create a new Multiedge with the edges in the fusionGraph
					fusionGraph.getListMultiedge().put(multiedgeG2.getId(), new Multiedge(multiedgeG2));
				}
			}

			for (Vertex nodeG2 : g2.getListNode()) {
				if (!fusionGraph.getListNode().contains(nodeG2)) {
					fusionGraph.getListNode().put(nodeG2.getId(), new Vertex(nodeG2));
				}
			}
			return fusionGraph;
		}
		return null;
	}

	/**
	 * Method to subtract G2 from G1 in a newly graph
	 * 
	 * @param g1
	 * @param g2
	 * @return the newly graph result from the subtract of g2 from g1
	 */
	public static Graph subtraction(Graph g1, Graph g2) {
		if (g1 instanceof Graph && g2 instanceof Graph) {
			// init the subtractGraph always with g1
			Graph subtractGraph = new Graph(g1);
			// iterate multiedges in G2
			for (Multiedge multiedgeG2 : g2.getListMultiedge()) {
				boolean existMultiedgeG2InSubtract = subtractGraph.existeMultiedgeBetween(multiedgeG2.getIdSource(),
						multiedgeG2.getIdTarget());
				if (existMultiedgeG2InSubtract) {
					// MultiedgeG2 exists in subtractGraph
					// Subtract the edges
					subtractGraph.getListMultiedge().get(multiedgeG2.getId())
							.subtractAListEdges(multiedgeG2.getListEdge());
				}
			}
			return subtractGraph;
		}
		return null;
	}

	/**
	 * Used in Ghost extern Method to forcing the fusion on a listMultiedges,
	 * changing the source and target id's
	 * 
	 * @param listMultiedge list of the multiedges
	 * @return
	 */
	//
	public static Multiedge forcingFusionMultiedges(List<Multiedge> listMultiedge) {
		// Create a graph abstract
		int abstractIdSource = 555;
		int abstractIdTarget = 666;

		// BEFORE FUSSION
		// Changing the idSource, idTarget and update the Id of the multiedges
		for (Multiedge multiedge : listMultiedge) {
			multiedge.setIdSource(abstractIdSource);
			multiedge.setIdTarget(abstractIdTarget);
			for (Edge edge : multiedge.getListEdge()) {
				int idE = edge.getType().getId();
				edge.setIdSource(abstractIdSource);
				edge.setIdTarget(abstractIdTarget);
				edge.setId(idE);
			}
		}
		// fusion
		long keyAbstractMultiedge = GraphUtil.pairingFunction(abstractIdSource, abstractIdTarget);
		Multiedge abstractMultiedge = new Multiedge(keyAbstractMultiedge, abstractIdSource, abstractIdTarget);
		for (Multiedge multiedge : listMultiedge) {
			abstractMultiedge.fusionAListEdges(multiedge.getListEdge());
		}

		return abstractMultiedge;
	}

	/**
	 * Method to perform the Step 1 for the GHOST process: -Fusion all the
	 * listEmbeddings in a gFusion -Update the Node id's of the gQuery to match with
	 * the gFusion -Finally, substrate the gQuery of the gFussion
	 * 
	 * @param gGlobal        graph global
	 * @param gQuery         graph query
	 * @param listEmbeddings list of the embeddings
	 * @return
	 */
	public static Graph gGhostStepOne(Graph gGlobal, Graph gQuery, int[] idsFijosTrabajar,
			FastList<int[]> listEmbeddings) {

		if (gGlobal instanceof Graph && gQuery instanceof Graph) {

			// POSITION DE LOS NODOS EN EMBEDDIGS ES MUY IMPORTANTE
			// PORQUE ESTAN EN LA MISMA POSICION QUE LOS NODOS EN EL GRAPH QUERY
			// EJEMPLO
			// EMBEDDING { 3 , 7 , 9}
			// QUERY { O , 1 , 2} NODOS ORDENADOS 01

			Graph gFusion = new Graph();
			for (int[] embedding : listEmbeddings) {
				
				
				// POR CADA EMBEDDING UN NUEVO GRAPH
				Graph gCurrEmbedding = new Graph();

				// POR CADA ID_EMBEDDING
				for (int id : embedding) {
					// SPEED
					for (Multiedge multiedgeGlobal : gGlobal.getListMultiedgesByNode().get(id)) {
						gCurrEmbedding.addNode(new Vertex(gGlobal.getListNode().get(multiedgeGlobal.getIdSource())));
						gCurrEmbedding.addNode(new Vertex(gGlobal.getListNode().get(multiedgeGlobal.getIdTarget())));
						for (Edge edgeGlobal : multiedgeGlobal.getListEdge()) {
							gCurrEmbedding.addEdgeInMultiedge(edgeGlobal);
						}
					}
				}

				//
				// CAMBIAR AQUI DE IDS EN EL GCURREMBEDDING PARA HACER MATCH LUEGO CON LA FUSION
				// { 3 , 7, 12 } //CURR EMBEDDINGS
				// = = =
				// { 21 , 22, 23 } //MATCH
				IntIntHashMap parserIdsViejoANuevos = GraphUtil.createIntIntParserViejoANuevo(embedding,
						idsFijosTrabajar);
				
				System.out.println("PARSER");
				System.out.println(parserIdsViejoANuevos);
				System.out.println("");
				Graph gCurrEmbeddUpdated = GraphUtil.updateNodeIdsOnAGraph(gCurrEmbedding, parserIdsViejoANuevos);
				gFusion = GraphUtil.fusion(gFusion, gCurrEmbeddUpdated);
			}

			
			System.out.println("GRAPH FUSIO EN GHOST LINE 499 GRAPH UTIL");
			System.out.println(gFusion);
			
			
			// PARA RESTAR EL GRAPHQUERY DEL GFUSION
			// DEBO CAMBIAR LOS IDS EN EL QUERY Y QUE HAGAN MATCH CON LA FUSION
			IntIntHashMap parserIdsGQuery = GraphUtil.createIntIntParserViejoANuevo(gQuery.getListOfNodesId(),
					idsFijosTrabajar);
			Graph gQueryChangedNodeIds = GraphUtil.updateNodeIdsOnAGraph(gQuery, parserIdsGQuery);

			// AHORA SI RESTO Y DEL gSubstract GRAPHO SE SACAN LOS GHOST INTERNE ET EXTERN
			Graph gSubstract = GraphUtil.subtraction(gFusion, gQueryChangedNodeIds);
			return gSubstract;
		}
		return null;
	}

	public static Graph getGraphBasedOnAnotherGraphTopology(Graph g1, Graph gBasedTopology) {

		if (g1 instanceof Graph && gBasedTopology instanceof Graph) {

			Graph gResult = new Graph();

			// los idSource and idTarget de la topologia son iguales a la del G1
			// nos basamos oslo en la topologia de gBasedTopology
			for (Multiedge multiedge : gBasedTopology.getListMultiedge()) {

				int idSource = multiedge.getIdSource();
				int idTarget = multiedge.getIdTarget();

				if (g1.existeMultiedgeBetween(idSource, idTarget)) {
					// add nodes
					gResult.addNode(new Vertex(g1.getListNode().get(idSource)));
					gResult.addNode(new Vertex(g1.getListNode().get(idTarget)));

					for (Edge g1Edge : g1.getMultiedgeBetween(idSource, idTarget).getListEdge()) {
						gResult.addEdgeInMultiedge(g1Edge);
					}
				}
			}
			return gResult;
		}
		return null;
	}

	/**
	 * Method to make a parse para NodeIdsViejos y NodeIdsNuevos
	 * 
	 * @param listNodesIdsViejos los que van hacer cambiados
	 * @param listNodesIdsNuevos los nuevos
	 * @return IntIntHashMap (viejos=>nuevo)
	 */
	public static IntIntHashMap createIntIntParserViejoANuevo(int[] listNodesIdsViejos, int[] listNodesIdsNuevos) {
		if (listNodesIdsNuevos.length == listNodesIdsViejos.length) {
			IntIntHashMap parserViejoNuevo = new IntIntHashMap();
			for (int i = 0; i < listNodesIdsViejos.length; i++) {
				parserViejoNuevo.put(listNodesIdsViejos[i], listNodesIdsNuevos[i]);
			}
			return parserViejoNuevo;
		} else {
			System.out.println("ERROR IN getIntIntParserViejoANuevo");
			return null;
		}
	}

	/**
	 * viejo=>nuevo
	 * 
	 * @param g1                Graph to change
	 * @param parserViejoANuevo a IntIntHashMap that contains oldId=>nuevo
	 * @return
	 */
	public static Graph updateNodeIdsOnAGraph(final Graph g1, IntIntHashMap parserViejoANuevo) {
//		System.out.println("-------------------------------------------");
//		System.out.println("parser viejo a nuevo");
//		System.out.println(parserViejoANuevo);
//		System.out.println();
//		System.out.println(g1);
		
		

		if (g1 instanceof Graph) {

			final Graph gResult = new Graph();

			for (Vertex node : g1.getListNode()) {
				int idNodeViejo = node.getId();

				try {
					int idNodeNuevo = parserViejoANuevo.getOrThrow(idNodeViejo);
					
					Vertex vertexViejo = g1.getListNode().get(idNodeViejo);

					Vertex vertexNuevo = new Vertex(vertexViejo);
					vertexNuevo.setId(idNodeNuevo);
					
					Etiqueta etiquetaNuevo = new Etiqueta(vertexViejo.getEtiqueta());
//					etiquetaNuevo.setText(String.valueOf(idNodeNuevo));
					vertexNuevo.setEtiqueta(etiquetaNuevo);	
					
					gResult.addNode(vertexNuevo);
					
				} catch (Exception e) {

					// TODO: handle exception
				
//					System.out.println("THERE IS NOT THIS IDNODEVIEJO: " + idNodeViejo);
				}
				
				
			}

			for (Multiedge multiedge : g1.getListMultiedge()) {

				int viejoSource = multiedge.getIdSource();
				int viejoTarget = multiedge.getIdTarget();
				
				
				int nuevoSource;
				try {
					nuevoSource = parserViejoANuevo.getOrThrow(viejoSource);
				} catch (Exception e) {
					nuevoSource = viejoSource;
				}
				
				int nuevoTarget;
				try {
					nuevoTarget = parserViejoANuevo.getOrThrow(viejoTarget);
				} catch (Exception e) {
					nuevoTarget = viejoTarget;
				}
				
				for (Edge edge : multiedge.getListEdge()) {
					Edge e = new Edge(edge);
					e.setIdSource(nuevoSource);
					e.setIdTarget(nuevoTarget);
					gResult.addEdgeInMultiedge(e);
				}
			}

			return gResult;
		}

		return null;
	}

	public static Graph getGraphWithPointsIntersections2(Graph graph) {

		for (Vertex node : graph.getListNode()) {
			System.out.println(node);
		}

		for (Multiedge multiedge : graph.getListMultiedge()) {
			System.out.println(multiedge);
		}

		return null;
	}

	/**
	 * Get the points from a graph where a node intersect with the edges
	 * 
	 * @param graph
	 */
	public static Graph getGraphWithPointsIntersections(Graph graph) {

		// Fill the edges coordinates for the source and target
//		for (Multiedge multiedge : graph.getListMultiedge()) {			
//			PositionShape sourcePosition = graph.getListNode().get(multiedge.getIdSource()).getPosition();
//			PositionShape targetPosition = graph.getListNode().get(multiedge.getIdTarget()).getPosition();
//			//el multiedge debe tener ya el origin y destination		
////			multiedge.setPosition(new PositionShape(sourcePosition, targetPosition));
//		}

		IntObjectHashMap listNodeFicticios = new IntObjectHashMap();

		// Take account the diameter of the nodes
		// Take account the stroke of the edges
		// ----------
		// Loop nodes
		for (Vertex node : graph.getListNode()) {
			
			for (Multiedge multiedge : graph.getListMultiedge()) {
				for (Edge edge : multiedge.getListEdge()) {
					
					if (!edge.containOneVertex(node.getId())) {
						
						Vertex ficticioNodeIntersection = JtsUtil.getPointIntersectionNodeToEdge(node, edge);
						if (ficticioNodeIntersection != null) {
							// Add the nodeIntersection to the graph
							// Change the idNode to make ascending ids
							ficticioNodeIntersection.setId(graph.getListNode().size() + listNodeFicticios.size());
//							listNodeFicticios.add(ficticioNodeIntersection);
							listNodeFicticios.put(ficticioNodeIntersection.getId(), ficticioNodeIntersection);
						}
						
					}
				}
			}
			

//			// Loop the edges
//			for (Edge edge : graph.getListEdge()) {
//				if (!edge.containOneVertex(node.getId())) {
//					// If edge does not contains the current analyzed node
//					
//					List<Vertex> nodeReference = graph.getAdjacentNodesOfNode(node.getId());
//					// As this node can intersect only in one point a this
//					// edge
//					Vertex ficticioNodeIntersection = JtsUtil.getPointIntersectionNodeToEdge(node, edge,
//							nodeReference.get(0));
//					if (ficticioNodeIntersection != null) {
//						// Add the nodeIntersection to the graph
//						// Change the idNode to make ascending ids
//						ficticioNodeIntersection.setId(graph.getListNode().size() + listNodeFicticios.size());
//						listNodeFicticios.add(ficticioNodeIntersection);
//					}
//				}
//			}
			
			
		}

		graph.getListNode().putAll(listNodeFicticios);

		return graph;
	}

	/**
	 * Method to get a list of nodes without overlap
	 * 
	 * 
	 * @param g
	 * @param showKelpLines
	 * @param checkOverlap
	 * @param nodes         Nodes with overlapping
	 * @return A list of Nodes without overlapping
	 */
	public static IntObjectHashMap<Vertex> nodesWithoutOverlap(Graph g, boolean checkOverlap) {
		VerticesOverlap overlap = new VerticesOverlap(g, Constants.KELP_OVERLAP_MAX_DAMPING_FACTOR, checkOverlap);
		IntObjectHashMap<Vertex> nodesWithoutOverlap = overlap.getGraph().getListNode();

		return nodesWithoutOverlap;
	}

	/**
	 * Method to change the node position according to zoom-in or zoom-out
	 * 
	 * @param zoomFactor > 1 = zoom-in; < 1 = zoom-out
	 * @return
	 */
//	public static void changeNodePosition(IntObjectHashMap<Vertex> intObjectHashMap, double zoomFactor) {
//		for (Vertex node : intObjectHashMap) {
//			PositionShape nodePosition = node.getPosition();
//			PositionShape newCoordinates = translatePosition(nodePosition.getX1(), nodePosition.getY1(), zoomFactor);
//			node.setPosition(newCoordinates);
//		}
//	}
	

//	public static void changeMultiedgePosition(LongObjectHashMap<Multiedge> intObjectHashMap,IntObjectHashMap<Vertex> intVertexHashMap, double zoomFactor) {
//		for (Multiedge multiedge : intObjectHashMap) {
//			PositionShape nodeSourcePosition = intVertexHashMap.get(multiedge.getIdSource()).getPosition();
//			PositionShape nodeTargetPosition = intVertexHashMap.get(multiedge.getIdTarget()).getPosition();
//
//			// setting the new positions of the edges in the multiedge
//			multiedge.updateEdgePositions(nodeSourcePosition, nodeTargetPosition,intVertexHashMap.get(multiedge.getIdSource()).getDiameter());
//		}		
//	}

	

}
