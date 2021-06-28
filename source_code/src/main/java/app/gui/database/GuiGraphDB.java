package app.gui.database;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Hyperedge;
import app.graph.structure.Hypergraph;
import app.graph.structure.Multiedge;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.embedding.EmbeddingItem;
import app.gui.embedding.LayoutEmbeddings;
import app.gui.histogram.HistogramItem;
import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.query.ComponentCreator;
import app.gui.query.GuiQuery;
import app.gui.query.LayoutGraphQuery;
import app.utils.GraphUtil;
import app.utils.In;
import app.utils.MathUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.event.MouseEvent;
import static javax.swing.JOptionPane.*;

/**
 * @author Erick Cuenca
 *
 */
// implements ActionListener, ChangeListener
public class GuiGraphDB extends PApplet {

	private static final long serialVersionUID = 1L;
	public final static Logger logger = Logger.getLogger(GuiGraphDB.class);

	// LAYOUTS of Query View and Embeddings View
	public static LayoutGraphQuery fxLeftPanel;
	public static LayoutEmbeddings fxRightPanel;

	// PROCESSING
	private boolean saveScreenshot;
	private String savePathScreenshot;
	private boolean appBlocked;
	private int labelAlgorithmTimerDelay = 100;
	private Timer labelAlgoritmhMovementTimer;

	// GRAPH DATABASE
	private Graph graph;
	private float startAngle = 0;
	private boolean showVerticesGraphDB = true;
	private boolean showEdgesGraphDB = false;
	private boolean showEmbeddingsGraphDB = true;
	private String msg_puntos_suspensivos = "...";
	public static Vertex nodeDisplayArc;

	/**
	 * SUMGRA
	 */
	public static int sumgraProcessState = Constants.SUMGRA_STATE_NEW;

	// HISTOGRAM
	private int histogramNumCategories = Constants.HISTOGRAM_NUMBER_CATEGORIES;

	// HEATMAP
	// to visualize the heatmap after click in search pattern button
	private int heatMapTransitionZoom = Constants.HEATMAP_INIT_TRANSITION_ZOOM;
	private int heatMapRadius = Constants.HEATMAP_INIT_RADIUS;

	// KELP DIAGRAMS
	public Hypergraph hypergraph;
	private List<EmbeddingItem> selectedEmbeddingItems = new ArrayList<EmbeddingItem>();
	private int kelpFacteurNode = Constants.KELP_NODE_FACTOR;
	private int kelpFacteurEdge = Constants.KELP_EDGE_FACTOR;
	private boolean isKelpDiagramShowing = false;
	private boolean showKelpLines = true;
	private boolean kelpOverlapLines = true;
	private int kelpTypeRelation = Constants.KELP_RELATION_TOPOLOGY;

	// ------------ GHOST -------------
	private boolean isFinishGhost;
	private List<IntObjectHashMap<Object>> ghostFinal;
	// Ghost internal
	private Graph ghostInternal;
	// Ghost external
	private Map<Integer, List<Edge>> ghostExternal = new HashMap<Integer, List<Edge>>();

	private PFont myFont;

	// public static PImage cursorImg;

	// public static void main(String[] args) {
	// PApplet.main(new String[] { "--present", "fr.um.general.ProcessingApp"
	// });
	// }

	public void settings() {
		// fullScreen();
		// size(500,500);
		// size(displayWidth, displayHeight);
		// Java 2D API Default works
		// FX2D processing.javafx.PSurfaceFX cannot be cast to
		// processing.awt.PSurfaceAWT
		// P2D processing.opengl.PSurfaceJOGL cannot be cast to
		// processing.awt.PSurfaceAWT
		// P3D processing.opengl.PSurfaceJOGL cannot be cast to
		// processing.awt.PSurfaceAWT
		// pixelDensity(displayDensity());
	}

	public void setup() {
		cursor(WAIT);

		// Hue-Saturation-Brightness-Alpha
		colorMode(HSB, 360, 100, 100, 100);

		// Init
		ColorShape.parent = this;
		Graph.parent = this;
		Hypergraph.parent = this;
		Edge.parent = this;
		Vertex.parent = this;
		hypergraph = new Hypergraph();

		ellipseMode(RADIUS);

		// load the graph db
		String pathGmlFile = Constants.PATH_DATA.concat(Constants.MAIN_GRAPH_FILE);
		String pathSumgraFile = Constants.PATH_DATA.concat(Constants.SUMGRA_GRAPH_FILE);
		loadGraphDatabase(pathGmlFile, pathSumgraFile);

		// Size of the processing application
		size((int) Application.SCREEN_WIDTH, (int) Application.SCREEN_HEIGHT);

		// cursorImg = loadImage("src/main/resources/img/screenshot.png");

		// myFont = createFont("Georgia", 32);
		// textFont(myFont);

		cursor(ARROW);
	}

	/**
	 * Method to load a graph database
	 * 
	 * @param pathGmlFile
	 * @param pathSumgraFile
	 */
	private void loadGraphDatabase(String pathGmlFile, String pathSumgraFile) {
		In in = new In(pathGmlFile);
		graph = new Graph(in);
		graph.setName(pathGmlFile);

		// Normalization of node positions according to the screen size
		double cornerPadding = Constants.GRAPH_QUERY_NODE_RADIUS;
		graph.normalizationNodePosition(getHeight(), getHeight(), cornerPadding, cornerPadding, cornerPadding,
				cornerPadding);

		graph.changeMultiedgePosition();
	}

	public void draw() {
		if (!appBlocked && graph != null) {
			// PImage cursorImg
			// =loadImage("src/main/resources/img/screenshot.png");
			// cursor(cursorImg,16,16);
			displayView();

			// To save a screenshot
			if (saveScreenshot) {
				save(savePathScreenshot);
				saveScreenshot = false;
			}
		}
	}

	private void displayView() {

		background(ColorShape.getHSB_White());
		pushMatrix();
		translate((float) (-graph.getxView() * graph.getZoom()), (float) (-graph.getyView() * graph.getZoom()));

		if (isShowVerticesGraphDB()) {
			if (isShowEdgesGraphDB()) {
				graph.displayEdges(mouseX, mouseY);
			}
			graph.displayNodes();
			if (nodeDisplayArc != null) {
				nodeDisplayArc.displayArc(startAngle);
				startAngle = (float) (startAngle + 0.2);
			}
		}

		if (graph.getListNodePattern().size() > 0) {
			graph.setHeatMapSide(graph.getZoom() <= heatMapTransitionZoom);
			if (graph.isHeatMapSide()) {
				graph.displayHeatmap(heatMapRadius);
			} else {
				if (showEmbeddingsGraphDB) {
					graph.displayNodePatterns();
				}
				if (hypergraph.getListHyperedge().size() > 0) {
					hypergraph.setZoom(graph.getZoom());
					hypergraph.setxView(graph.getxView());
					hypergraph.setyView(graph.getyView());
					hypergraph.display(showKelpLines, kelpOverlapLines, mouseX, mouseY);
				}
			}
		}

		popMatrix();

		// To show the gray rectangle when the backtracking is running...
		// out of the translate to not distort if we drag the mouse
		if (sumgraProcessState == Constants.SUMGRA_STATE_ACTIVE) {

			float msgCoordinateX = (float) (Application.SCREEN_WIDTH - Constants.LAYOUT_QUERY_VIEW_WIDTH
					- Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH) / 2;
			float msgCoordinateY = (float) Application.SCREEN_HEIGHT / 2;
			float spaceBetweenMsg = 80;

			fill(GRAY, 45);
			rect(0, 0, (float) Application.SCREEN_WIDTH, (float) Application.SCREEN_HEIGHT);


			// --MSG Updating...
			textSize(67);
			fill(ColorShape.getHSB_Black());
			textAlign(PConstants.CENTER, PConstants.CENTER);
			

//			if (msg_puntos_suspensivos.length() == 3) {
//				msg_puntos_suspensivos = "";
//			} else {
//				msg_puntos_suspensivos = msg_puntos_suspensivos.concat(".");
//			}
			
			text(Constants.SUMGRA_MSG_LINE_1 + msg_puntos_suspensivos, msgCoordinateX, msgCoordinateY - spaceBetweenMsg);		
			
			textSize(22);
//			fill(ColorShape.getHSBUpdate_message());
			fill(ColorShape.getHSB_Blue());
			textAlign(PConstants.CENTER, PConstants.CENTER);
			text("#Embeddings Found: "+graph.getListFastIntEmbeddings().size(), msgCoordinateX, msgCoordinateY);
			
//			textSize(24);
//			fill(ColorShape.getHSBGoogle_Cat2());
//			textAlign(PConstants.TOP, PConstants.CENTER);
//			text(graph.getListFastIntEmbeddings().size(), msgCoordinateX+120, msgCoordinateY);
			
	
			
			
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * MOUSE BEHAVIOR
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	public void doubleClicked() {
		logger.info("double click");

		if (sumgraProcessState != Constants.SUMGRA_STATE_ACTIVE && !graph.isHeatMapSide()) {
			fxRightPanel.clearListEmbeddings();
			hypergraph.getListHyperedge().clear();
			// Deselect
			for (Vertex node : graph.getListNodePatternSorted()) {
				node.setSelected(false);
			}
			graph.setListNodePatternLabelVisibility(false);
			setAppBlocked(false);
			clearKelpDiagrams();

		}
	}

	public void mouseClicked(MouseEvent evt) {

		nodeDisplayArc = null;

		if (evt.getCount() == 2)
			doubleClicked();

		if (sumgraProcessState != Constants.SUMGRA_STATE_ACTIVE && !graph.isHeatMapSide() && mouseButton == LEFT) {

			Vertex nodePatternHover = graph.getNodeUnderMouse(mouseX, mouseY, graph.getListNodePattern());

			if (nodePatternHover != null) {

				if (nodePatternHover.isSelected()) {
					// Deselect
					// nodePatternHover.setFill(Constants.GRAPH_DB_NODE_PATTERN_NO_SELECTED_FILL);
					// nodePatternHover.setStroke(Constants.GRAPH_DB_NODE_PATTERN_NO_SELECTED_STROKE);
				} else {
					// Select
					// nodePatternHover.setFill(Constants.GRAPH_DB_NODE_PATTERN_SELECTED_FILL);
					// nodePatternHover.setStroke(Constants.GRAPH_DB_NODE_PATTERN_SELECTED_STROKE);
				}
				nodePatternHover.setSelected(!nodePatternHover.isSelected());

				if (graph.getNodePatternSelected().size() > 0) {
					int[] ids = new int[graph.getNodePatternSelected().size()];
					String[] labels = new String[graph.getNodePatternSelected().size()];
					for (int i = 0; i < graph.getNodePatternSelected().size(); i++) {
						Vertex v = graph.getNodePatternSelected().get(i);

						ids[i] = v.getId();
						labels[i] = v.getEtiqueta().getText();
					}
					fxRightPanel.updateSelectedVertex(ids, labels);
				} else {
					fxRightPanel.clearListEmbeddings();
				}
				hypergraph.getListHyperedge().clear();
			}
		}
	}

	public void mouseDragged() {
		nodeDisplayArc = null;
		if (sumgraProcessState != Constants.SUMGRA_STATE_ACTIVE) {
			// ABS() to prevent shifting when open a file dialog box
			// if (!guiQueryPanel.isMouseOverQueryPanel() && abs(mouseX -
			// pmouseX) <
			// 100 && abs(mouseY - pmouseY) < 100) {
			graph.setxView(graph.getxView() - (mouseX - pmouseX) / graph.getZoom());
			graph.setyView(graph.getyView() - (mouseY - pmouseY) / graph.getZoom());
			// }
		}
	}

	// Called when the mouse button is pressed
	public void mousePressed() {
		nodeDisplayArc = null;
	}

	// Called when the mouse button is released
	public void mouseReleased() {
		nodeDisplayArc = null;
	}

	public void mouseDragged(MouseEvent event) {
		if (labelAlgoritmhMovementTimer != null && labelAlgoritmhMovementTimer.isRunning()) {
			labelAlgoritmhMovementTimer.stop();
		}
		labelAlgoritmhMovementTimer = new Timer(labelAlgorithmTimerDelay, new LabelAlgorithmTimerActionListener());
		labelAlgoritmhMovementTimer.setRepeats(false);
		labelAlgoritmhMovementTimer.start();

		super.mouseDragged(event);
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		if (labelAlgoritmhMovementTimer != null && labelAlgoritmhMovementTimer.isRunning()) {
			labelAlgoritmhMovementTimer.stop();
		}

		// graph.setCompleteBloqued(true);
		labelAlgoritmhMovementTimer = new Timer(labelAlgorithmTimerDelay, new LabelAlgorithmTimerActionListener());
		labelAlgoritmhMovementTimer.setRepeats(false);
		labelAlgoritmhMovementTimer.start();

		super.mouseWheelMoved(event);
	}

	private class LabelAlgorithmTimerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// graph.setCompleteBloqued(false);
			verifyLabelAlgorithm();
			// graph.setCompleteBloqued(false);
		}
	}

	public void verifyLabelAlgorithm() {
		if (graph.getListNodePattern().size() > 0 && showEmbeddingsGraphDB && !graph.isHeatMapSide()
				&& !isKelpDiagramShowing) {
			graph.calculeLabelOverlapping();

		}
	}

	public void mouseWheel(MouseEvent event) {
		nodeDisplayArc = null;
		//
		if (sumgraProcessState != Constants.SUMGRA_STATE_ACTIVE) {
			float e = -event.getCount();
			zoom(e);
		}
	}

	/**
	 * Method to change the zoom factor of the view
	 * 
	 * @param e
	 *            -1 Zoom-In; 1 Zoom-out
	 */
	private void zoom(float e) {
		double zoomFactor = (float) ((e == 1) ? Constants.VISUAL_GRAPH_DB_ZOOM_FACTOR_IN
				: Constants.VISUAL_GRAPH_DB_ZOOM_FACTOR_OUT);
		double z = graph.getZoom() * zoomFactor;
		z = constrain((float) z, (float) Constants.VISUAL_GRAPH_DB_ZOOM_THRESHOLD_OUT,
				(float) Constants.VISUAL_GRAPH_DB_ZOOM_THRESHOLD_IN);

		double wmX = getXMousePixel(mouseX);
		double wmY = getYMousePixel(mouseY);

		graph.setZoom(z);
		hypergraph.setZoom(z);

		graph.setxView(wmX - (mouseX / graph.getZoom()));
		graph.setyView(wmY - (mouseY / graph.getZoom()));

		//
		if (isShowVerticesGraphDB()) {
			graph.changeNodePosition(zoomFactor);
			if (isShowEdgesGraphDB()) {
				graph.changeMultiedgePosition();
			}
		}

		if (showEmbeddingsGraphDB) {
			graph.changeNodePositionPattern(zoomFactor);
		}

		// graph.updateNodePatternPositions();
		hypergraph.changeNodePosition(zoomFactor);
		// GraphUtil.changeNodePosition(hypergraph.getListNode(), zoomFactor);

	}

	/**
	 * @param mouseX
	 * @return
	 */
	private double getXMousePixel(double mouseX) {
		return graph.getxView() + (mouseX / graph.getZoom());
	}

	/**
	 * @param mouseY
	 * @return
	 */
	private double getYMousePixel(double mouseY) {
		return graph.getyView() + (mouseY / graph.getZoom());
	}

	/**
	 * 
	 * SUMGRA
	 * 
	 * 
	 */

	/**
	 * Method to finish the SumGRa process and start the Ghost calculate
	 */
	public void finishedSumgraProcess() {
		// nodesPatternSelectedInRed.clear();

		if (graph.getListFastIntEmbeddings().size() > 0) {

			fxLeftPanel.updateSearchBtn();
			calculateGhost();

		} else {
			clearAllPatternsInvolvedInGraph();
			showMessageDialog(null, "There are no results with the current graph query!!!", "Info",
					INFORMATION_MESSAGE);
			logger.info("ZERO RESULTS");
		}

	}

	/**
	 * Method called when: pause and finish the SumGRa process
	 */
	public void pausedSumgraProcess() {
	
		if (graph.getListFastIntEmbeddings().size() > 0) {
			
			graph.normaWeightToAreaNodePatterns(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MIN,
					Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MAX);
			
			verifyLabelAlgorithm();
			
			// Create the histogram from the nodeListPattern
			List<Graph> listGraphHistograms = getGraphsForHistogram();
			createHistogramChart(listGraphHistograms, histogramNumCategories);
			
			// Filling node labels list in the menu of the search BEFORE sort the
			// patterns by diameter
			// graph.sortListNodePattern(0, Constants.NODE_LABEL_COMPARATOR);
			ObservableList<String> items = FXCollections.observableArrayList();
			for (Vertex node : graph.getListNodePatternSorted()) {
				items.add(node.getEtiqueta().getText());
			}
			ComponentCreator.fillCmbLabel(GraphDBToolBar.cmbLabelNodesPatterns, items);
			// Sort the listNodePattern by diameter size
			// graph.sortListNodePattern(0, Constants.NODE_DIAMETER_COMPARATOR);
		}

	}

	/**
	 * @param listGraphHistograms
	 * @param numHistoCategories
	 */
	private void createHistogramChart(List<Graph> listGraphHistograms, int numHistoCategories) {
		if (listGraphHistograms != null && listGraphHistograms.size() > 0) {
			fxLeftPanel.createTabHistogram(listGraphHistograms, numHistoCategories);
		}
	}

	private boolean areNodesInEmbedding(int[] nodesIds, int[] embedding) {
		for (int node : nodesIds) {
			if (!isNodeInEmbedding(node, embedding)) {
				return false;
			}
		}
		return true;
	}

	private boolean isNodeInEmbedding(int nodeId, int[] embedding) {
		for (int nodeCurrent : embedding) {
			if (nodeCurrent == nodeId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get the embedding for the nodes ids patterns
	 * 
	 * @param nodesIds
	 *            [] ids of pattern nodes
	 * @param nodesLabels
	 *            labels
	 */
	public void embeddingByNodes(int[] nodesIds, String[] nodesLabels) {
		noLoop();

		List<Graph> listGraphEmbeddingsWithoutFussion = new ArrayList<Graph>();
		for (int[] embedding : graph.getListFastIntEmbeddings()) {

			if (areNodesInEmbedding(nodesIds, embedding)) {
				// To verify that all nodes ids in the line exist in the current
				// ListNodePattern-that because we can filter by histogram
				if (graph.isAllNodesInListPattern(embedding)) {
					Graph g = new Graph();
					// Adding the nodes of the embedding
					// Has the same order than the node ids in GraphQuery
					imprimir(embedding);
					logger.info("count embedding: " + embedding.length);
					for (int nodeId : embedding) {
						g.addNode(new Vertex((Vertex) graph.getListNodePattern().get(nodeId)));
					}
					logger.info("count graph " + g.getListNode().size());

					if (g.getListNode().size() == 2) {
						logger.info(g);
					}

					// g.setMbr(MathUtil.calculateMinimalBoundingRectangle(g.getListNode()));
					// Query Topology is already ordered
					// Adding edges to G from the Query
					// They have the same order than the node ids in GraphQuery
					for (Multiedge multiedgeQuery : GuiQuery.query.getListMultiedge()) {
						// In query.getListMultiedge() the Source and Target are
						// 0 1 2 3..
						int idSourceQuery = multiedgeQuery.getIdSource();
						int idTargetQuery = multiedgeQuery.getIdTarget();
						// We use them to get the index in the embedding array
						int idSourceInGraph = embedding[idSourceQuery];
						int idTargetInGraph = embedding[idTargetQuery];

						for (Edge edgeQuery : multiedgeQuery.getListEdge()) {
							Edge edgeInGraph = new Edge(edgeQuery);
							// update idsource and idtarget and ID, the rest of
							// attributs are the same
							edgeInGraph.setIdSource(idSourceInGraph);
							edgeInGraph.setIdTarget(idTargetInGraph);
							// edgeInGraph.setId((int)
							// (Math.pow(idSourceInGraph, 2) +
							// Math.pow(idTargetInGraph, 2)
							// + Math.pow(edgeQuery.getType().getId(), 2)));
							g.addEdgeInMultiedge(edgeInGraph);
						}
					}
					listGraphEmbeddingsWithoutFussion.add(g);
				}
			}
		}
		List<Graph> listFusionGraphs = new ArrayList<Graph>(createListFusionGraph(listGraphEmbeddingsWithoutFussion));

		// GET THE FUSSIONED OF THE EMBEDDING GRAPHS
		if (listFusionGraphs.size() > 0) {

			// logger.info("GET THE FUSSIONED OF THE EMBEDDING GRAPHS");
			// for (Graph graph : listFusionGraphs) {
			// logger.info("");
			// logger.info("");
			// logger.info(graph);
			// logger.info("");
			// logger.info("");
			// }

			fxRightPanel.updateTabListEmbedding(listFusionGraphs);
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle(Constants.GUI_TITLE_MSG_DIALOG);
			alert.setHeaderText(null);
			alert.setContentText(Constants.LIST_EMBEDDING_MSG_EMPTY + Arrays.asList(nodesLabels));
			alert.showAndWait();
		}
		loop();
	}

	/**
	 * Method to create an abstraction of a list of graph by topology
	 * 
	 * @param listGraphsToFusion
	 *            List of Graphs initial
	 * @return a new List of Graphs with the same vertices and edges merged
	 */
	private List<Graph> createListFusionGraph(List<Graph> listGraphsToFusion) {

		// We must fusion only equals graphs. i.e. graph with same vertices
		// topology
		LongObjectHashMap<Graph> listGraphFusioned = new LongObjectHashMap<Graph>();

		for (Graph graph : listGraphsToFusion) {
			long graphKey = graph.getId();
			if (!listGraphFusioned.containsKey(graphKey)) {
				// Create new graph
				listGraphFusioned.put(graphKey, graph);

				// logger.info("**************************************");
				// logger.info("");
				// logger.info("");
				// logger.info("PUT NUEVO GRAFO");
				// logger.info("id graph: " + graph.getId());
				// logger.info("count listnode: " +
				// graph.getListNode().toList().size());
				// logger.info(graph.getListNode().toList());
				// logger.info("**************************************");
				// logger.info("");
				// logger.info("");
			} else {
				// Fusion the graphs
				Graph g1 = listGraphFusioned.get(graphKey);
				// logger.info("**************************************");
				// logger.info("");
				// logger.info("");
				// logger.info("FUSION DE GRAFOS");
				// logger.info("");
				// logger.info("id graph: " + g1.getId());
				// logger.info("count listnode: " +
				// g1.getListNode().toList().size());
				// logger.info(g1.getListNode().toList());
				// logger.info("+");
				// logger.info("id graph: " + graph.getId());
				// logger.info("count listnode: " +
				// graph.getListNode().toList().size());
				// logger.info(graph.getListNode().toList());

				Graph graphFusion = GraphUtil.fusion(g1, graph);
				graphFusion.setNumFusions(g1.getNumFusions() + 1);
				listGraphFusioned.put(graphKey, graphFusion);
				// logger.info("=");
				// logger.info("id fusion: " + graphFusion.getId());
				// logger.info("count fusion: " +
				// graphFusion.getListNode().toList().size());
				// logger.info(graphFusion.getListNode().toList());
				// logger.info("**************************************");
				// logger.info("");
				// logger.info("");

			}
		}
		return listGraphFusioned.toList();
	}

	/**
	 * Method to create a list of graph for every embedding in the listEmbeddins
	 * (histogram)
	 * 
	 * @return
	 */
	public List<Graph> getGraphsForHistogram() {
		List<Graph> listGraphHistograms = new ArrayList<Graph>();

		for (int[] embedding : graph.getListFastIntEmbeddings()) {
			// Creating a graph from one embedding
			Graph graphEmbedding = new Graph();
			// Adding nodes to the graph
			for (int nodeId : embedding) {
				Vertex vertexEmbedding = new Vertex(graph.getListNode().get(nodeId));
				graphEmbedding.addNode(vertexEmbedding);
			}
			// Adding edge from graphQuery
			graphEmbedding.setMbr(MathUtil.calculateMinimalBoundingRectangle(graphEmbedding.getListNode()));
			listGraphHistograms.add(graphEmbedding);
		}
		return listGraphHistograms;
	}

	/**
	 * To ALL clean
	 */
	public void clearAllPatternsInvolvedInGraph() {

		// setting visible all the nodes in listNode
		graph.setListNodeVisible(true);

		//
		hypergraph.getListHyperedge().clear();
		graph.getListNodePattern().clear();
		graph.getListNodePatternSorted().clear();
		graph.getListFastIntEmbeddings().clear();
		// graph.getListEmbeddingsNodeNumAppears().clear();

		// clear heatmap
		// listHeatMap = new ArrayList<Vertex>();
		// isSearchingPattern = false;

		// clear histogram
		fxLeftPanel.clearTabHistogram();

		// clear list embedding
		fxRightPanel.clearListEmbeddings();

		// clear sumgra
		sumgraProcessState = Constants.SUMGRA_STATE_NEW;

		// clear ghost
		isFinishGhost = false;
		fxLeftPanel.updateSearchBtn();

		// Test
		setAppBlocked(false);
	}

	/**
	 * 
	 * 
	 * 
	 * KELP DIAGRAMS
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Method to create a Kelp diagrams from a list of embedding items
	 * 
	 * @param listEmbeddingItems
	 */
	public void createKelpDiagrams(List<EmbeddingItem> listEmbeddingItems) {
		noLoop();
		selectedEmbeddingItems = new ArrayList<EmbeddingItem>(listEmbeddingItems);
		drawKelpDiagrams(selectedEmbeddingItems);
		loop();
	}

	/**
	 * Method to update a kelp diagrams. Called when change the node side in the
	 * control panel
	 */
	private void updateKelpsDiagrams() {
		drawKelpDiagrams(selectedEmbeddingItems);
	}

	/**
	 * Method to draw kelp diagrams from a list of embedding items
	 * 
	 * @param listSelectedEmbeddings
	 */
	private void drawKelpDiagrams(List<EmbeddingItem> listSelectedEmbeddings) {

		// Setting LABEL visible false to all other nodes
		graph.setListNodePatternLabelVisibility(false);

		hypergraph = new Hypergraph();

		for (EmbeddingItem selectEmbeddingItem : listSelectedEmbeddings) {
			double hue = selectEmbeddingItem.getBorderColor().getHue(); // 0-1
			double saturation = selectEmbeddingItem.getBorderColor().getSaturation();// 0-1
			double brightness = selectEmbeddingItem.getBorderColor().getBrightness();// 0-1
			int colorKelp = color((float) hue, (float) saturation * 100, (float) brightness * 100, (float) 100);

			Graph graphWithUniqueEdges = new Graph();
			if (kelpTypeRelation == Constants.KELP_RELATION_MST) {
				graphWithUniqueEdges = GraphUtil.getNewlyGraphMST(selectEmbeddingItem.getGraph());
			} else if (kelpTypeRelation == Constants.KELP_RELATION_TOPOLOGY) {
				graphWithUniqueEdges = GraphUtil.getNewlyGraphTopology(selectEmbeddingItem.getGraph());
			}

			// logger.info("GET GRAPH WITH MULTIEDGEES");
			// logger.info(graphWithUniqueEdges);

			// update the position according to the current node position
			for (Vertex node : graphWithUniqueEdges.getListNode()) {
				node.setFill(new Fill(true, colorKelp, 100));
				node.setStroke(new Stroke(true, colorKelp, 100, 1));

				// node.setPosition(graph.getListNode().get(node.getId()).getPosition());
				// set the node to visible false
				Vertex nodePatternInGraph = graph.getListNodePattern().get(node.getId());

				nodePatternInGraph.setVisible(false);
				node.setPosition(nodePatternInGraph.getPosition());

			}
			for (Multiedge multiedge : graphWithUniqueEdges.getListMultiedge()) {
				for (Edge edge : multiedge.getListEdge()) {
					edge.setFill(new Fill(true, colorKelp, 100));
					edge.setStroke(new Stroke(true, colorKelp, 100, 1));
					edge.setPosition(new PositionShape(graph.getListNode().get(edge.getIdSource()).getPosition(),
							graph.getListNode().get(edge.getIdTarget()).getPosition()));
				}
			}

			// 2 Create an Hyperedge
			Hyperedge hyperedge = new Hyperedge(graphWithUniqueEdges);
			hyperedge.setOrderToDraw(hypergraph.getListHyperedge().size());

			// 3 Add the Hyperedge to the Hypergraph
			hypergraph.addSecureHyperedge(hyperedge, kelpFacteurNode, kelpFacteurEdge);
		}

		isKelpDiagramShowing = true;
	}

	public void clearKelpDiagrams() {
		hypergraph = new Hypergraph();
		isKelpDiagramShowing = false;
		graph.calculeLabelOverlapping();
		// verifyLabelAlgorithm();
	}

	/**
	 * Use the keys to make zoom in and zoom out
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void verifyKeyPressedZoom() {
		if (keyPressed && key == CODED) {
			if (keyCode == UP) {
			} else if (keyCode == DOWN) {
			} else if (keyCode == LEFT) {
			} else if (keyCode == RIGHT) {
			}
		}
	}

	public String imprimir(int[] embedding) {
		String result = "[";
		for (int i : embedding) {
			result = result.concat(String.valueOf(i)).concat(", ");
		}
		result = result.concat("]");
		return result;
	}

	@Deprecated
	private boolean uniqueElementsInEmbedding(int[] embedding) {
		int[] dd = new int[2];
		for (int id : embedding) {
		}
		return false;
	}

	private boolean validateEmbedding(int[] embedding) {

		// size must be equal to the query
		if (embedding.length != GuiQuery.query.getListOfNodesId().length)
			return false;

		// topology must be equal to the query (node id)
		for (Vertex vQuery : GuiQuery.query.getListNode()) {
			// El ID en el query es igual al index en el embedding
			int indexIdEmbedding = vQuery.getId();
			// Despues, comparar los tipos

			int idX = vQuery.getType().getId();
			int idA = graph.getListNode().get(embedding[indexIdEmbedding]).getType().getId();

			if (idX != idA) {
				return false;
			}
		}

		// Deberia comparar luego los edges?

		return true;
	}

	/**
	 * Method to fetch the embedding from SumGRa. Used for the patterns
	 * 
	 * @param listFetchedEmbedding
	 */
	public void fetchEmbeddings(FastList<int[]> listFetchedEmbeddingBeforeValidate) {

		FastList<int[]> listFetchedEmbedding = new FastList<int[]>();
		for (int[] embedding : listFetchedEmbeddingBeforeValidate) {
			if (validateEmbedding(embedding)) {
				listFetchedEmbedding.add(embedding);
			}
		}

		// Adding the fetched embedding to list historic of embedding
		graph.getListFastIntEmbeddings().addAll(listFetchedEmbedding);

		// Filling the NodeRelationAdjacentMultiedge list to improve the speed
		graph.fillNodeRelationAdjacentMultiedges(listFetchedEmbedding);
		// Loop the fetched list to count the number that appears a node in the
		// embedding
		for (int[] embedding : listFetchedEmbedding) {
//			System.out.println(imprimir(embedding));
//			if (embedding.length != GuiQuery.query.getListOfNodesId().length) {
//				System.out.println("ERROR IN THIS EMBEDDING FETCHED: " + imprimir(embedding));
//			}
			for (int idNodePattern : embedding) {
				addUpdateNodePatternWeight(idNodePattern);
			}
		}
		logger.info("Embedding fetched batch: " + listFetchedEmbedding.size());
		logger.info("Embedding fetched in total: 		" + graph.getListFastIntEmbeddings().size());
		
		
		
	}

	

	public void addUpdateNodePatternWeight(int idNodePattern) {
		if (!graph.getListNodePattern().containsKey(idNodePattern)) {
			Vertex nodeTmpPattern = new Vertex(graph.getListNode().get(idNodePattern));

			// Formatting the initial nodeTmpPattern
			nodeTmpPattern.setWeight(1);
			nodeTmpPattern.setFill(new Fill(true,
					ColorShape.getHSBGoogle_NodePatternColorCategory(nodeTmpPattern.getType().getId()), 100));

			graph.getListNodePattern().put(idNodePattern, nodeTmpPattern);

			// To do not draw double
			graph.getListNode().get(idNodePattern).setVisible(false);
		} else {
			// adding 1 to the weight
			Vertex nodeTmp = (Vertex) graph.getListNodePattern().get(idNodePattern);
			nodeTmp.setWeight(nodeTmp.getWeight() + 1);
		}
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * HISTOGRAM
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Method to update the listNodePattern
	 * 
	 * @param listHistogram
	 *            List of Histograms; HistogramItem is a ListOfGraphs
	 */
	public void filterEmbeddingsByHistogram(List<HistogramItem> listHistogram) {

		cursor(WAIT);
		noLoop();
		//
		clearKelpDiagrams();
		// Remove the listNodePattern list in order to fill after with the
		// selected histogramItems
		graph.getListNodePattern().clear();
		
		graph.getListNodePatternSorted().clear();
		
		// setting visible all the nodes in listNode
		graph.setListNodeVisible(true);

			
		// Loop the histogram to fill patterns
		for (HistogramItem histogramItem : listHistogram) {
			// Only if the histogram isSelected
			if (histogramItem.isSelected()) {
				for (Vertex nodeTmpPattern : histogramItem.getAllVertexOfListGraph()) {
					int idNodePattern = nodeTmpPattern.getId();
					addUpdateNodePatternWeight(idNodePattern);
				}
			}
		}

		// Normalization of the diameter of the list of node patterns
		graph.normaWeightToAreaNodePatterns(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MIN,
				Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MAX);

		// Sort the listNodePatterns by diameter after normalization
		// graph.sortListNodePattern(0, Constants.NODE_DIAMETER_COMPARATOR);

		// verify to run label Algorithm
		graph.updateNodePatternPositions();
		verifyLabelAlgorithm();

		appBlocked = false;
		loop();
		cursor(ARROW);

		// Clear the list of embeddings
		fxRightPanel.clearListEmbeddings();
	}

	/**
	 * 
	 * 
	 * 
	 * GHOST
	 * 
	 * 
	 * 
	 */
	private void getC(int[] embeddingsFastInt) {
		// # # #
		// create a new graph from the embeddingFastInt
		LongObjectHashMap<List<Multiedge>> listEmbedLinkedToMultiedges = new LongObjectHashMap();

		for (int i : embeddingsFastInt) {
			if (!listEmbedLinkedToMultiedges.containsKey(i)) {
				listEmbedLinkedToMultiedges.put(i, graph.getAdjacentMultiedgesOfNode(i));
			}
		}

		Graph g = new Graph();
		for (int i : embeddingsFastInt) {
			for (Multiedge multiedge : listEmbedLinkedToMultiedges.get(i)) {
				for (Edge edge : multiedge.getListEdge()) {
					g.addEdgeInMultiedge(edge);
				}
			}
		}

	}

	/**
	 * Method to calculate GHOST May in another thread
	 */
	private void calculateGhost() {
		if (!isFinishGhost && graph.getListFastIntEmbeddings().size() > 0) {

			logger.info(" ------ Init GHOST :) --------");

			int[] idsFijosTrabajar = new int[GuiQuery.query.getListOfNodesId().length];
			for (int i = 0; i < GuiQuery.query.getListOfNodesId().length; i++) {
				idsFijosTrabajar[i] = graph.getListNode().size() + i;
				if (graph.getListNode().get(idsFijosTrabajar[i]) != null) {
					System.out.println("error garrafal xq si hay un node con este id: "
							+ graph.getListNode().get(idsFijosTrabajar[i]));
				}
			}
			/////
			IntIntHashMap parserIdQueryToFijos = GraphUtil
					.createIntIntParserViejoANuevo(GuiQuery.query.getListOfNodesId(), idsFijosTrabajar);
			
			
			System.out.println("PARSER INIT in GRAPH QUERY:");
			System.out.println(parserIdQueryToFijos);
			System.out.println();
			
			Graph gTopologyQuery = GraphUtil.updateNodeIdsOnAGraph(GuiQuery.query, parserIdQueryToFijos);
			Graph gGhostGeneral = GraphUtil.gGhostStepOne(graph, GuiQuery.query, idsFijosTrabajar,
					graph.getListFastIntEmbeddings());

			Graph tmpGhostInternal = GraphUtil.getGraphBasedOnAnotherGraphTopology(gGhostGeneral, gTopologyQuery);
			IntIntHashMap parserFijosToQueryId = GraphUtil.createIntIntParserViejoANuevo(idsFijosTrabajar,
					GuiQuery.query.getListOfNodesId());
			ghostInternal = GraphUtil.updateNodeIdsOnAGraph(tmpGhostInternal, parserFijosToQueryId);

			// logger.info(ghostInternal);

			// Building ghost externe
			for (int i = 0; i < idsFijosTrabajar.length; i++) {
				int idFijo = idsFijosTrabajar[i];
				List<Multiedge> adjacentesMultiedges = gGhostGeneral.getAdjacentMultiedgesOfNode(idFijo);
				
				
				System.out.println("ID FIJO: " +idFijo);
				for (Multiedge multiedge : adjacentesMultiedges) {
					System.out.println(multiedge);
				}
				System.out.println("");
				
				
				
				List<Edge> l = GraphUtil.forcingFusionMultiedges(adjacentesMultiedges).getEdgeDescendingByWeight();
				ghostExternal.put(i, l);
			}

			System.out.println(ghostExternal);
			
			isFinishGhost = true;
			fxLeftPanel.updateGhostButton();
			logger.info(" ------ Terminate GHOST :) --------");
		}
	}

	/**
	 * 
	 * @param embedding
	 */
	private void externalGhost(int[] embedding) {
		//
		// // logger.info("embedding " + Arrays.toString(embedding));
		// for (int indexEmbedding = 0; indexEmbedding < embedding.length;
		// indexEmbedding++) {
		//
		// int focusNode = embedding[indexEmbedding];
		// // logger.info(" focus " + focusNode);
		//
		// // In GraphQuery
		// List<Vertex> internalToExcludeInEmbedding =
		// GuiQuery.query.getAdjacentNodesOfNode(indexEmbedding);
		// int[] nodesInternalInEmbedding = new
		// int[internalToExcludeInEmbedding.size()];
		// for (int j = 0; j < internalToExcludeInEmbedding.size(); j++) {
		// // Node Ids in nodeQuery are the indices in the embedding
		// nodesInternalInEmbedding[j] =
		// embedding[internalToExcludeInEmbedding.get(j).getId()];
		// }
		//
		// // logger.info(" interno " +
		// // Arrays.toString(nodesInternalInEmbedding));
		//
		// @SuppressWarnings("unchecked")
		// List<Edge> adjacentEdgeOfNodeFocus = new ArrayList<Edge>(
		// (List<Edge>) nodeRelationAdjacentEdges.get(focusNode));
		//
		// List<Edge> internalEdges = new ArrayList<Edge>();
		// for (int nodeInternal : nodesInternalInEmbedding) {
		// for (Edge edge : adjacentEdgeOfNodeFocus) {
		// if (edge.containOneVertex(nodeInternal)) {
		// internalEdges.add(edge);
		// }
		// }
		// }
		// adjacentEdgeOfNodeFocus.removeAll(internalEdges);
		//
		// // Futuro se podria ver el nodo al que mayor numero de edges van :)
		// // Aqui ir sumando los resultados al node con el index i en graph
		// // query
		// ghostExternal.put(indexEmbedding,
		// GraphUtil.mergeTwoListEdgeByType(ghostExternal.get(indexEmbedding),
		// adjacentEdgeOfNodeFocus));
		//
		// }
		//
	}

	public void saveScreenshot(File file) {
		this.saveScreenshot = true;
		this.savePathScreenshot = file.getAbsolutePath();
	}

	public boolean isAppBlocked() {
		return appBlocked;
	}

	public void setAppBlocked(boolean appBlocked) {
		this.appBlocked = appBlocked;
	}

	public void setShowKelpLines(boolean showKelpLines) {
		this.showKelpLines = showKelpLines;
	}

	public boolean isShowKelpLines() {
		return showKelpLines;
	}

	public List<IntObjectHashMap<Object>> getGhostFinal() {
		return ghostFinal;
	}

	public boolean isFinishGhost() {
		return isFinishGhost;
	}

	public void setFinishGhost(boolean isFinishGhost) {
		this.isFinishGhost = isFinishGhost;
	}

	public void setShowVerticesGraphDB(boolean showVerticesGraphDB) {
		this.showVerticesGraphDB = showVerticesGraphDB;
	}

	public boolean isShowVerticesGraphDB() {
		return showVerticesGraphDB;
	}

	public void setShowEdgesGraphDB(boolean showEdgesGraphDB) {
		this.showEdgesGraphDB = showEdgesGraphDB;
		graph.changeMultiedgePosition();
	}

	public boolean isShowEdgesGraphDB() {
		return showEdgesGraphDB;
	}

	public void setShowEmbeddingsGraphDB(boolean showEmbeddingsGraphDB) {
		this.showEmbeddingsGraphDB = showEmbeddingsGraphDB;
	}

	public Graph getGraphGhostInternal() {
		return ghostInternal;
	}

	public Map<Integer, List<Edge>> getMapGhostExternal() {
		return ghostExternal;
	}

	public void setHeatMapTransitionZoom(int heatMapTransitionZoom) {
		this.heatMapTransitionZoom = heatMapTransitionZoom;
	}

	public void setKelpFacteurNode(int kelpFacteurNode) {
		this.kelpFacteurNode = kelpFacteurNode;
		updateKelpsDiagrams();
	}

	public void setKelpFacteurEdge(int kelpFacteurEdge) {
		this.kelpFacteurEdge = kelpFacteurEdge;
		updateKelpsDiagrams();
	}

	public void setKelpTypeRelation(int kelpTypeRelation) {
		this.kelpTypeRelation = kelpTypeRelation;
		updateKelpsDiagrams();
	}

	public List<EmbeddingItem> getSelectedEmbeddingItems() {
		return selectedEmbeddingItems;
	}

	public void setSelectedEmbeddingItems(List<EmbeddingItem> selectedEmbeddingItems) {
		this.selectedEmbeddingItems = selectedEmbeddingItems;
	}

	public void setKelpOverlapLines(boolean kelpOverlapLines) {
		this.kelpOverlapLines = kelpOverlapLines;
	}

	public void setHeatMapRadius(int heatMapRadius) {
		this.heatMapRadius = heatMapRadius;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setHistogramNumCategories(int histogramNumCategories) {
		this.histogramNumCategories = histogramNumCategories;
		List<Graph> listGraphHistograms = getGraphsForHistogram();
		fxLeftPanel.createTabHistogram(listGraphHistograms, histogramNumCategories);
	}

}
