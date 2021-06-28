package app.gui.main;

import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.Comparator;

import com.gs.collections.api.block.predicate.primitive.IntObjectPredicate;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Hyperedge;
import app.graph.structure.Multiedge;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

public  class Constants {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");
	public static final int FRAME_MINIMUM_SIZE_WIDTH = 900;
	public static final int FRAME_MINIMUM_SIZE_HEIGHT = 700;
	public static final String MAIN_TITLE = "VERTIGo";
	public static final int GRILLA = 5;

	// RUNNING PATHS
	// "data/others/miserables/"
	// "data/others/sierpinski/"
	// "data/biogrid/"
	// "data/dblp_vis_bd/"
	// "data/panama/"
	// "data/hoja/"	

	public static final String PATH_DATA = "data/dblp_vis_bd/"; //dblp=edge_types_dblp
	public static final String MAIN_GRAPH_FILE = "graph_fm3.gml";
	public static final String SUMGRA_GRAPH_FILE = "relationships.txt";
	public static final String QUERY_GRAPH_FILE = "query.txt";
	public static final String RESOURCES_IMAGE_PATH = "img/";
	public static final String RESOURCES_IMAGE_PATH_EDGE_TYPES = "img/edge_types_dblp/";
//	public static final String RESOURCES_IMAGE_PATH_EDGE_TYPES = "img/edge_types/";
	public static final String RESOURCES_IMAGE_PATH_NODE_TYPES = "img/node_types/";

	// ------- JAVAFX -------
	//
	public static final String JAVAFX_STYLE_FILE = "stylesheet.css";

	// ------- MAIN TITLE TOOLBAR -------
	public static final int MAIN_TOOLBAR_HEIGHT = GRILLA*6;

	// ------- GRAPH QUERY VIEW LAYOUT -------
	//
	public static final double LAYOUT_QUERY_VIEW_WIDTH = Application.LAYOUT_QUERY_VIEW_WIDTH;
	public static final double LAYOUT_EMBEDDINGS_VIEW_WIDTH =  Application.LAYOUT_EMBEDDINGS_VIEW_WIDTH;
	public static final String LAYOUT_GRAPH_DB_VIEW_TITLE = "GRAPH";
	public static final double LAYOUT_GRAPH_DB_VIEW_TITLE_WIDTH = GRILLA*13; //THE WIDH OF GRAPH WORD
	public static final String LAYOUT_GRAPH_QUERY_VIEW_TITLE = "QUERY";
	public static final String LAYOUT_EMBEDDINGS_VIEW_TITLE = "EMBEDDINGS";

	
	
	// ----- GRAPH QUERY ----
	//
	public static final double VISUAL_GRAPH_QUERY_BTN_WIDTH = GRILLA*6; 
	public static final double VISUAL_GRAPH_QUERY_BTN_HEIGHT = GRILLA*6;
	public static final double VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER = GRILLA*9; 
	public static final double VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH = GRILLA*26;
	public static final double VISUAL_GRAPH_QUERY_CANVAS_HEIGHT = Application.LAYOUT_QUERY_VIEW_HEIGHT; 
	public static final double VISUAL_GRAPH_QUERY_CANVAS_WIDTH = LAYOUT_QUERY_VIEW_WIDTH - VISUAL_GRAPH_QUERY_BTN_HEIGHT;
	public static final double VISUAL_CANVAS_WIDTH = VISUAL_GRAPH_QUERY_CANVAS_WIDTH;
	public static final double VISUAL_CANVAS_HEIGHT = VISUAL_GRAPH_QUERY_CANVAS_HEIGHT;

	public static final double VISUAL_GRAPH_QUERY_STATUS_HEIGHT =  GRILLA*4;
	public static final int VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH = 160; // 150
	public static final int VISUAL_GRAPH_QUERY_NUMBER_EDGE_TYPES = 18;

	// GRAPH QUERY VERTEX GHOST
	public static final int VISUAL_GRAPH_QUERY_PIE_GHOST_ANGLE_START = 0;

	// GRAPH QUERY VERTEX
	public static final double GRAPH_QUERY_NODE_RADIUS = GRILLA*4.5;
	public static final Stroke GRAPH_QUERY_NODE_STROKE = new Stroke(true, 0, 100, 1.5d); //1d is to short
	public static final double GRAPH_QUERY_NODE_STROKE_WEIGHT_HOVER = 5.0d;

	// GRAPH QUERY EDGE (do not have Fill)
	public static final double GRAPH_QUERY_EDGE_PARALLEL_DISTANCE = GRILLA*9;
	public static final Font GRAPH_QUERY_EDGE_LABEL_FONT = new Font("Arial", (GRILLA*3));
	public static final double GRAPH_QUERY_EDGE_STROKE_OPACITY = 100.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT = 4.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT_HOVER = 6.0d;
	public static final double GRAPH_QUERY_EDGE_STROKE_WEIGHT_DRAWING = 7.0d;
	public static final double GRAPH_QUERY_EDGE_ALPHA = 0.6; // 0-1

	// GRAPH QUERY EDGE GHOST (do not have Fill)
	public static final int GRAPH_QUERY_EDGE_GHOST_EXTERNAL_TOP_K = 5;
	public static final int GRAPH_QUERY_EDGE_GHOST_INTERNAL_TOP_K = 1;
	public static final double GRAPH_QUERY_EDGE_GHOST_DISTANCE = GRILLA*15;
	public static final double[] GRAPH_QUERY_EDGE_STROKE_GHOST_DASHES = new double[] { 5, 10 };
	public static final int GRAPH_EDGE_IS_GHOST = 0;
	public static final int GRAPH_EDGE_WAS_GHOST = 1;
	public static final int GRAPH_EDGE_NEVER_GHOST = 2;

	// ------------ HISTOGRAM ----------
	//
	public static final int HISTOGRAM_NUMBER_CATEGORIES = 5;
	public static final String HISTOGRAM_TITLE = "Distribution of Embedding's MBR";
	public static final String HISTOGRAM_X_LABEL = "From lower to higher MBR Area";
	public static final String HISTOGRAM_Y_LABEL = "Num. Embeddings";
	public static final String HISTOGRAM_ITEM_STYLE_SELECTED = "-fx-bar-fill: #238443;";
	public static final String HISTOGRAM_ITEM_STYLE_DESELECTED = "-fx-bar-fill: #d9f0a3;";

	// ------- MAIN GRAPH DATABASE-------
	//
	public static final int VISUAL_GRAPH_DB_BTN_WIDTH = GRILLA*4;
	public static final int VISUAL_GRAPH_DB_BTN_HEIGHT = GRILLA*4;
	public static final int VISUAL_GRAPH_DB_CMBLABELNODES_WIDTH = GRILLA*20;
	public static final int VISUAL_GRAPH_DB_CMBLABELNODES_HEIGHT = 25;
	public static final double VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MIN = GRILLA*2.3; //1.5
	public static final double VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MAX = GRILLA*3.5; //2.5
	public static final double VISUAL_GRAPH_DB_ZOOM_THRESHOLD_OUT = (float) 0.1;
	public static final double VISUAL_GRAPH_DB_ZOOM_THRESHOLD_IN = (float) 10000;
	// always > 1.0
	public static final double VISUAL_GRAPH_DB_ZOOM_FACTOR_IN = (float) 1.1;
	// always < 1.0
	public static final double VISUAL_GRAPH_DB_ZOOM_FACTOR_OUT = (float) 0.9;

	// MAIN GRAPH DATABASE VERTEX
	public static final double GRAPH_DB_NODE_RADIUS = 1.0d;

	public static final int GRAPH_DB_NODE_NO_SELECTED = 0;
	public static final int GRAPH_DB_NODE_NO_SELECTED_HOVER = 0;
	public static final int GRAPH_DB_NODE_SELECTED = 0;
	public static final int GRAPH_DB_NODE_SELECTED_HOVER = 0;
	public static final int GRAPH_DB_NODE_FIXED = 2;

	public static final int GRAPH_DB_NODE_PIN_ID = -1;

	public static final Stroke GRAPH_DB_NODE_STROKE = new Stroke(false, ColorShape.getHSB_Black(), 100.0d, 1.0d);
	public static final Stroke GRAPH_DB_NODE_ARC_STROKE = new Stroke(true, ColorShape.getHSB_Arc(), 100.0d, 15.0d);
	public static final double GRAPH_DB_NODE_TEXT_SIZE = 15.0d; //15.0d
	public static final double GRAPH_DB_EDGE_TEXT_SIZE = 15.0d;

	// MAIN GRAPH DATABASE VERTEX PATTERN
	public static final double GRAPH_DB_NODE_PATTERN_STROKE_WEIGHT = 1d;
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE = new Stroke(true,ColorShape.getHSB_Black(), 100.0d, GRAPH_DB_NODE_PATTERN_STROKE_WEIGHT);
	public static final double GRAPH_DB_NODE_PATTERN_STROKE_HOVER_WEIGHT = 3.0d;
	public static final Stroke GRAPH_DB_NODE_PATTERN_STROKE_SELECTED = new Stroke(true,ColorShape.getHSB_NodePatternSelectedStroke(), 
																					100.0d, GRAPH_DB_NODE_PATTERN_STROKE_HOVER_WEIGHT);

	// MAIN GRAPH DATABASE EDGES
	public static final int GRAPH_DB_EDGE_STROKE_OPACITY = 100;
	public static final double GRAPH_DB_EDGE_STROKE_WEIGHT = 3.0d;
	public static final double GRAPH_DB_EDGE_STROKE_WEIGHT_HOVER = 5.0d;
	public static final int GRAPH_DB_EDGE_PARALLEL_DISTANCE = 15;

	// ------- EMBEDDINGS VIEW LAYOUT -------
	//

	public static final Font LIST_EMBEDDING_LEGENDE_TITLE_LABEL_FONT = new Font("Arial", GRILLA*2);
	public static final Font LIST_EMBEDDING_LEGENDE_SUBTITLE_LABEL_FONT = new Font("Arial", GRILLA*2);

	public static final double LIST_EMBEDDING_COLUMN_MBR_WIDTH = LAYOUT_EMBEDDINGS_VIEW_WIDTH/8;
	public static final double LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH = LAYOUT_EMBEDDINGS_VIEW_WIDTH/8;
		
	public static final double LIST_EMBEDDING_COLUMN_GRAPH_WIDTH = LAYOUT_EMBEDDINGS_VIEW_WIDTH
			- LIST_EMBEDDING_COLUMN_MBR_WIDTH - LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH - GRILLA*6;
	public static final int LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT = GRILLA*40; 
	public static final double LIST_EMBEDDING_COLUMN_GRAPH_PADDING = GRILLA*10;
	
	public static final int LIST_EMBEDDING_BTN_HEIGHT = GRILLA*5;
	public static final int LIST_EMBEDDING_BTN_VISUALIZE_WIDTH = GRILLA*18;
	public static final int LIST_EMBEDDING_BTN_CLEAR_WIDTH = GRILLA*21;
	public static final int LIST_EMBEDDING_BTN_HEIGHT_ROW = GRILLA*10;
	public static final int LIST_EMBEDDING_BTN_WIDTH_COLUMNA2 = GRILLA*20;
	public static final double LIST_EMBEDDING_BTN_WIDTH_COLUMNA1 = LAYOUT_EMBEDDINGS_VIEW_WIDTH
			- LIST_EMBEDDING_BTN_WIDTH_COLUMNA2 - 45;

	public static final int LIST_EMBEDDING_COLUMN_GRAPH_BORDER_SELECTED = GRILLA;
	public static final int LIST_EMBEDDING_MAXIMUM_SELECTED_ITEMS = 5; // Max 7
	public static final String LIST_EMBEDDING_MSG_EMPTY = "Not embeddings for: ";

	// LIST EMBEDDING VERTEX
//	public static final double LIST_EMBEDDING_NODE_RADIUS = GRILLA*4;
	public static final Stroke LIST_EMBEDDING_NODE_STROKE =  new Stroke(true, 0, 100, 2d);

	// LIST EMBEDDING EDGE (do not have Fill)
	public static final double LIST_EMBEDDING_EDGE_STROKE_OPACITY = 1;
	public static final double LIST_EMBEDDING_EDGE_STROKE_WEIGHT = 4;

	// ------- KELP LIKE DIAGRAMS -------
	//
	public static final double KELP_DECREASE_SATURATION_FACTOR = 1.8d;
	public static final boolean KELP_EDGE_VISIBLE = true;
	public static final boolean KELP_CHECK_OVERLAP_EDGE_NODE = true;
	public static final double KELP_OVERLAP_MAX_DAMPING_FACTOR = 1.1; // > 1
	public static final int KELP_EDGE_FACTOR = GRILLA*3;
	public static final int KELP_NODE_FACTOR = GRILLA*4;
	public static final int KELP_TEXT_SIZE = GRILLA*2;
	public static final Fill KELP_TEXT_BACKGROUND_FILL = new Fill(true, ColorShape.getHSB_KelpTextBackground(), 60.0d);
	public static final Fill KELP_TEXT_FILL = new Fill(true, ColorShape.getHSB_KelpText(), 100.0d);
	public static final double KELP_TEXT_BACKGROUND_PADDING = 1.0d;
	public static final int KELP_RELATION_MST = 0;
	public static final int KELP_RELATION_TOPOLOGY = 1;
	public static final double KELP_OPACITY_HOVER = 100;
	public static final double KELP_OPACITY_NO_HOVER = 20;

	// ------- HEATMAPS -------
	//
	public static final int HEATMAP_INIT_TRANSITION_ZOOM = 6;
	public static final int HEATMAP_INIT_RADIUS = 10;

	// ------------ SUMGRA ------------
	//
	public static final String SUMGRA_MSG_LINE_1 = "Updating";
	public static final double SUMGRA_RECTANGLE_OPACITY = 30;
	// 1=on voit update; 1000 non
	public static final int SUMGRA_THRESHOLD_BUFFER_BACKTRAKING = 1000; //10 ; //1000; //1
	// higher time we saw better
	public static final int SUMGRA_SLEEP_TIME_BACKTRAKING = 0; // 100; // 0 //10
	public static final int SUMGRA_STATE_NEW = 0;
	public static final int SUMGRA_STATE_ACTIVE = 1;
	public static final int SUMGRA_STATE_PAUSED = 2;
	public static final int SUMGRA_STATE_FINISHED = 3;

	// ------------ OGDF ------------
	//
	public static final String OGDF_PATH_TEMP_FM3 = "temp.gml";
	public static final String OGDF_PATH_TEMP_LAYOUT_FM3 = "temp-layout.gml";

	// ------------ GUI Messages ------------
	//
	public static final String GUI_TITLE_MSG_DIALOG = "Graphes";
	public static final String GUI_MSG_PATH_FILE = "Path: ";
	public static final String GUI_MSG_ORDER_GRAPH = "Order of the graph: ";
	public static final String GUI_MSG_NUM_EDGES = "Number of edges: ";
	public static final String GUI_MSG_SAVE = "File saved to: ";
	public static final String GUI_MSG_CONFIRMATION_PLAY = "Executed Fast Multipole Multilevel Method (FM3) Force Directed Layout in Query Graph";
	public static final String GUI_MSG_CONFIRMATION_NEW = "Do you want to save the Query Graph?";
	public static final String GUI_LIST_EMBEDDING_EMPTY = "No embeddings found";

	// ------- PREFERENCES -------
	//
	public static final double PREF_HEIGHT = (Application.SCREEN_HEIGHT*0.8);
	public static final double PREF_WIDTH = (Application.SCREEN_WIDTH*0.3);
	public static final int PREF_VERTICAL_GAP = 7;
	public static final int PREF_HORIZONTAL_GAP = 10;
	public static final Insets PREF_INSETS = new Insets(10, 10, 10, 10);
	public static final double PREF_WIDTH_COLUMNA1 = (PREF_WIDTH * 0.4);
	public static final double PREF_WIDTH_COLUMNA2 = PREF_WIDTH - PREF_WIDTH_COLUMNA1
			- 2 * PREF_VERTICAL_GAP - PREF_INSETS.getLeft() - PREF_INSETS.getRight();

	public static final String PREF_TAB_TITLE = "Preferences";
	public static final String PREF_PANE_GRAPH_DATABASE = "Graph";
	public static final String PREF_PANE_GRAPH_ENGINE = "Query Engine - SumGRa";
	public static final String PREF_PANE_GRAPH_EMBEDDINGS = "Embedding Results";
	public static final String PREF_PANE_GRAPH_QUERY = "Query";
	public static final String PREF_PANE_KELP_DIAGRAMS = "Set Relations - Kelp-like Diagrams";

	// --------- COMPARATORS -------------
	//
	public static final Comparator<Hyperedge> HYPEREDGE_SORT_DRAW_COMPARATOR = new Comparator<Hyperedge>() {
		public int compare(Hyperedge hyperedge1, Hyperedge hyperedge2) {
			Integer orderToDraw1 = hyperedge1.getOrderToDraw();
			Integer orderToDraw2 = hyperedge2.getOrderToDraw();
			return orderToDraw1.compareTo(orderToDraw2);
		}
	};

	public static final Comparator<Graph> GRAPH_MBR_COMPARATOR = new Comparator<Graph>() {
		public int compare(Graph g1, Graph g2) {
			Double mbr1 = g1.getMbr();
			Double mbr2 = g2.getMbr();
			return mbr1.compareTo(mbr2);
		}
	};

	public static final Comparator<Graph> GRAPH_NUM_EMBEDDING_COMPARATOR = new Comparator<Graph>() {
		public int compare(Graph g1, Graph g2) {
			Integer numEmbedding1 = g1.getNumFusions();
			Integer numEmbedding2 = g2.getNumFusions();
			return numEmbedding1.compareTo(numEmbedding2);
		}
	};

	public static final Comparator<Edge> EDGE_TYPE_COMPARATOR = new Comparator<Edge>() {
		public int compare(Edge o1, Edge o2) {
			Integer type1 = o1.getType().getId();
			Integer type2 = o2.getType().getId();
			return type1.compareTo(type2);
		}
	};

	public static final Comparator<Edge> EDGE_WEIGHT_COMPARATOR = new Comparator<Edge>() {
		public int compare(Edge o1, Edge o2) {
			Double weight1 = o1.getWeight();
			Double weight2 = o2.getWeight();
			return weight1.compareTo(weight2);
		}
	};
	
	public static final Comparator<Multiedge> MULTIEDGE_ID_COMPARATOR = new Comparator<Multiedge>() {
		public int compare(Multiedge v1, Multiedge v2) {
			Long idVertex1 = v1.getId();
			Long idVertex2 = v2.getId();
			return idVertex1.compareTo(idVertex2);
		}
	};


	public static final Comparator<Vertex> NODE_WEIGHT_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex o1, Vertex o2) {
			Double weight1 = o1.getWeight();
			Double weight2 = o2.getWeight();
			return weight1.compareTo(weight2);
		}
	};

	public static final Comparator<Vertex> NODE_ID_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex v1, Vertex v2) {
			Integer idVertex1 = v1.getId();
			Integer idVertex2 = v2.getId();
			return idVertex1.compareTo(idVertex2);
		}
	};
	
	public static final Comparator<Vertex> NODE_LABEL_COMPARATOR = new Comparator<Vertex>() {
		public int compare(Vertex v1, Vertex v2) {
			String labelVertex1 = v1.getEtiqueta().getText();
			String labelVertex2 = v2.getEtiqueta().getText();
			return labelVertex1.compareTo(labelVertex2);
		}
	};
	

}
