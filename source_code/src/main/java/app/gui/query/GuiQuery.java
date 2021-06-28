package app.gui.query;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.controlsfx.control.StatusBar;

import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.EdgeType;
import app.graph.structure.NodeType;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Multiedge;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.database.GuiGraphDB;
import app.gui.ghost.GhostSliceItem;
import app.gui.ghost.PieGhost;
import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.main.SumgraBuffer;
import app.lib.ogdf.Layer;
import app.utils.DiversParser;
import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import sumgra.application.MainSumgra;
import sumgra.data.GraphDatabase;

@SuppressWarnings("restriction")
public class GuiQuery extends Node {

	public final static Logger logger = Logger.getLogger(GuiQuery.class);
	public static GuiGraphDB processingApp; // handle action
	public static LayoutGraphQuery parent; // handle action

	// TOOL BARS
	private ToolBar topToolBar = new ToolBar();
	private ToolBar leftToolBar = new ToolBar();
	private StatusBar bottomStatusBar = new StatusBar();

	// TOP TOOL MENU
	public static ToggleButton tbSearch;

	// LEFT TOOL MENU
	private ComboBox<NodeType> cmbAddNode = new ComboBox<NodeType>();
	private ComboBox<EdgeType> cmbAddEdge = new ComboBox<EdgeType>();
	private ToggleGroup toggleLeftGroup = new ToggleGroup();

	private ToggleButton tbDelete;
	private Button btnFm3;
	public static ToggleButton tbGhost;

	// BOTTOM STATUS BAR
	private Label captionStatusBar = new Label("Ready");

	// COMBOBOX for Pinned Labels in Nodes
	public static ComboBox<String> cmbLabelNodes;
	private static ObservableList<String> observableLabelsNodes = FXCollections.observableArrayList();

	private Canvas canvas;
	private GraphicsContext gc;

	// VISUAL GRAPH QUERY
	public static Graph query;
	private Vertex nodeQuerySource, nodeQueryTarget;
	private boolean isAddingEdge;
	private boolean isAddingNode;
	private EdgeType currEdgeType;
	private NodeType currNodeType;

	// SUMGRA
	private SumgraBuffer sumgraBuffer;
	public static int sumgraThresholdBufferBacktraking = Constants.SUMGRA_THRESHOLD_BUFFER_BACKTRAKING;
	public static int sumgraSleepBacktraking = Constants.SUMGRA_SLEEP_TIME_BACKTRAKING;

	// EDGE UNDER MOUSE
	private Edge edgeUnderMouse;

	private boolean displaying = true;

	public GuiQuery() {

		query = new Graph();
		canvas = new Canvas(Constants.VISUAL_CANVAS_WIDTH, Constants.VISUAL_CANVAS_HEIGHT);
		gc = canvas.getGraphicsContext2D();

		createTopToolBar();
		createLeftToolBar();
		createBottomStatusBar();

		// ---------------------- CANVAS ACTION EVENTS------------------

		canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {

				nodeQuerySource = query.getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY(), query.getListNode());
				edgeUnderMouse = query.getEdgeUnderMouse(mouseCursor.getX(), mouseCursor.getY());

				// Removing the combobox of label nodes
				if (cmbLabelNodes.isVisible()) {
					parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);
				}

				if (mouseCursor.getButton() == MouseButton.PRIMARY) {

					if (mouseCursor.getClickCount() == 1) {

						// Add a node
						if (isAddingNode && nodeQuerySource == null) {
							PositionShape addNodePosition = new PositionShape(mouseCursor.getX(), mouseCursor.getY());

							addNode(addNodePosition,currNodeType);
						}
						// Delete a node | Delete an edge
						else if (tbDelete.isSelected()) {
							// Delete a node
							if (nodeQuerySource != null) {

								query.deleteMultidgesByNode(nodeQuerySource.getId());
								query.deleteNode(nodeQuerySource);

								clearStopAllInProcessing();
							}
							// Delete an edge
							else if (edgeUnderMouse != null) {
								clearStopAllInProcessing();
								query.deleteEdge(edgeUnderMouse);
							}
						}
						// Change state of a Ghost edge
						else if (tbGhost.isSelected()) {

							logger.info(edgeUnderMouse);

							if (edgeUnderMouse != null) {
								logger.info("changin ghost...");
								// Change state 'Is a Ghost' to 'Was a Ghost'
								if (edgeUnderMouse.getStateGhost() == Constants.GRAPH_EDGE_IS_GHOST) {
									edgeUnderMouse.setStateGhost(Constants.GRAPH_EDGE_WAS_GHOST);
									clearStopAllInProcessing();
								} else if (edgeUnderMouse.getStateGhost() == Constants.GRAPH_EDGE_WAS_GHOST) {
									// Change state 'Was a Ghost' to 'Is a
									// Ghost'
									edgeUnderMouse.setStateGhost(Constants.GRAPH_EDGE_IS_GHOST);
									clearStopAllInProcessing();
								}
							}
						}
					} else if (mouseCursor.getClickCount() == 2 && !isAddingNode) {
						toggleLeftGroup.selectToggle(null);
						cmbAddEdgeUnselect();
						cmbAddNodeUnselect();			
					}

				}

				if (mouseCursor.getButton() == MouseButton.PRIMARY) {

				} else if (mouseCursor.getButton() == MouseButton.SECONDARY) {

					// Filling the observableLabelsNodes
					if (observableLabelsNodes.size() == 0) {
						observableLabelsNodes.add("*");
						// loop the listNode (non order) to add the item to the
						// observable list
						for (Vertex vertex : processingApp.getGraph().getListNode()) {
							observableLabelsNodes.add(vertex.getEtiqueta().getText());
						}
											
						ComponentCreator.fillCmbLabel(cmbLabelNodes, observableLabelsNodes);
					}

					if (nodeQuerySource != null) {
						cmbLabelNodes.setVisible(true);
						cmbLabelNodes.setTranslateX(mouseCursor.getX());
						cmbLabelNodes.setTranslateY(mouseCursor.getY());

						parent.getPannableCanvas().getChildren().add(cmbLabelNodes);
					}

				}

				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(0, 0);

			}
		});

		canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {

				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(mouseCursor.getX(), mouseCursor.getY());
			}
		});

		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {
				// Getting the node under the mouse point if any
				nodeQuerySource = query.getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY(), query.getListNode());
			}
		});

		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Adding an Edge
				if (isAddingEdge && nodeQuerySource != null) {
					// Drawing a line

					double sourceX = nodeQuerySource.getPosition().getX1();
					double sourceY = nodeQuerySource.getPosition().getY1();
					double targetX = mouseCursor.getX();
					double targetY = mouseCursor.getY();

					gc.setStroke(currEdgeType.getColor());
					gc.setLineWidth(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT_DRAWING);
					gc.strokeLine(sourceX, sourceY, targetX, targetY);

					gc.setFill(Color.BLACK);
					gc.setTextAlign(TextAlignment.CENTER);
					gc.setTextBaseline(VPos.CENTER);
					gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
					PositionShape edgeMiddlePoint = GeoUtil.middlePointBetweenTwoPoints(sourceX, sourceY, targetX,
							targetY);
					gc.fillText(currEdgeType.getLabel(), edgeMiddlePoint.getX1(), edgeMiddlePoint.getY1());

				}
				// Moving a Node
				else if (nodeQuerySource != null && !tbDelete.isSelected()) {
					// VALIDATE CANVAS BORDERS
					int factore = 12;
					if (mouseCursor.getX() > factore && mouseCursor.getY() > factore
							&& mouseCursor.getX() < (canvas.getWidth() - factore)
							&& mouseCursor.getY() < (canvas.getHeight() - factore)) {
						PositionShape positionDrag = new PositionShape(mouseCursor.getX(), mouseCursor.getY());
						nodeQuerySource.setPosition(positionDrag);
					}
				}

				display(mouseCursor.getX(), mouseCursor.getY());
			}
		});

		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseCursor) {

				nodeQueryTarget = query.getNodeUnderMouse(mouseCursor.getX(), mouseCursor.getY(), query.getListNode());

				// Complete adding an edge final step
				if (isAddingEdge && nodeQueryTarget != null && nodeQuerySource != null
						&& nodeQueryTarget != nodeQuerySource) {

					addEdge(nodeQuerySource.getId(), nodeQueryTarget.getId(), currEdgeType,
							Constants.GRAPH_EDGE_NEVER_GHOST);
				}
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				display(mouseCursor.getX(), mouseCursor.getY());
				nodeQueryTarget = null;
				nodeQuerySource = null;
			}
		});

	}

	/**
	 * Method to execute the FM3 algorithm
	 */
	private void executeFm3Algorithm() {
		toggleLeftGroup.selectToggle(null);
		cmbAddEdgeUnselect();
		cmbAddNodeUnselect();

		Graph graphFM3 = Layer.executeFMMMLayout(query);
		DiversParser.saveGML(graphFM3, Constants.OGDF_PATH_TEMP_LAYOUT_FM3);

		query = DiversParser.loadGML(Constants.OGDF_PATH_TEMP_LAYOUT_FM3);
		initQueryGraph();
		Layer.deleteTempGraphFile();

		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);
	}

	/**
	 * Method to add a new Edge
	 * 
	 * @param idSource id the Node Source
	 * @param idTarget id the Node Target
	 * @param edgeType edge type
	 * @param isGhost
	 */
	private void addEdge(int idSource, int idTarget, EdgeType edgeType, int stateGhost) {
		int id = edgeType.getId();

		Vertex nodeSource = query.getListNode().get(idSource);
		Vertex nodeTarget = query.getListNode().get(idTarget);

		PositionShape position = new PositionShape(nodeSource.getPosition(), nodeTarget.getPosition());
		Etiqueta etiqueta = new Etiqueta(edgeType.getLabel(), true);
		// Lines do not have FILL
		Fill fill = new Fill();
		Stroke stroke = new Stroke(true, ColorShape.parserColorJavafxToProcessingHsb(edgeType.getColor()),
				Constants.GRAPH_QUERY_EDGE_STROKE_OPACITY, Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);
		boolean isVisible = true;

		Edge e = new Edge(id, etiqueta, position, stroke, fill, isVisible, edgeType, idSource, idTarget);
		e.setStateGhost(stateGhost);
		query.addEdgeInMultiedge(e);
	}

	/**
	 * Method to add a Node
	 * 
	 * @param idNode       Id to the new node
	 * @param positionNode Position of the new node
	 */
	private void addNode(PositionShape positionNode, NodeType type) {

		Integer id = query.getMaxIdListNode();
		String label = "*"; //String.valueOf(id);// "*"; 
		
		Fill fill = new Fill(true,ColorShape.getHSBGoogle_NodeColorCategory(type.getId()),100);
		Stroke stroke = new Stroke(Constants.GRAPH_QUERY_NODE_STROKE);
		boolean isVisible = true;
		
		Etiqueta etiqueta = new Etiqueta(label, true);
		etiqueta.setShowRectangle(true);
		etiqueta.setLabelled(false);//this show or hide label and rectangle
		
		Vertex v = new Vertex(id, etiqueta,
				new PositionShape(positionNode.getX1(), positionNode.getY1()), stroke, fill, isVisible,
				Constants.GRAPH_QUERY_NODE_RADIUS,type);
		
		v.setShowImgBackground(true);
		
		query.addNode(v);

		clearStopAllInProcessing();
	}

	private void cmbAddEdgeSelect() {
		toggleLeftGroup.selectToggle(null);
		isAddingEdge = true;
		currEdgeType = cmbAddEdge.getSelectionModel().getSelectedItem();
		cmbAddEdge.setId("cmbAddEdge-selected");
		cmbAddNodeUnselect();
	}

	/**
	 * Method to set the combobox of edges unselected
	 */
	private void cmbAddEdgeUnselect() {
		cmbAddEdge.setId("cmbAddEdge-unselected");
		isAddingEdge = false;
	}
	
	//--------------------
	
	private void cmbAddNodeSelect() {
		toggleLeftGroup.selectToggle(null);
		isAddingNode = true;
		currNodeType = cmbAddNode.getSelectionModel().getSelectedItem();
		cmbAddNode.setId("cmbAddNode-selected");
		cmbAddEdgeUnselect();
	}

	/**
	 * Method to set the combobox of edges unselected
	 */
	private void cmbAddNodeUnselect() {
		cmbAddNode.setId("cmbAddNode-unselected");
		isAddingNode = false;
	}
	

	// ------------ GHOST--------------
	/**
	 * Method to add a node and edge of a Ghost
	 * 
	 * @param sliceAngle
	 * @param idNodeSource
	 * @param edgeType
	 */
	public void addExternalGhostNodeEdge(double sliceAngle, int idNodeSource, EdgeType layerType) {

		int idNewNode = query.getMaxIdListNode();// graphQuery.getListNode().size();

		// Vertex node = GraphUtil.getNodeById(query.getListNode(),
		// idNodeSource);
		Vertex node = query.getListNode().get(idNodeSource);

		PositionShape positionAngle = GeoAnalytic.getCoordinatesByAngle(node.getPosition(), sliceAngle,
				Constants.GRAPH_QUERY_EDGE_GHOST_DISTANCE);

		addNode(positionAngle,NodeType.getNodeTypeById(0));
		addEdge(idNodeSource, idNewNode, layerType, Constants.GRAPH_EDGE_NEVER_GHOST);

		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);

		clearStopAllInProcessing();
	}

	/**
	 * Method to clear all the process to start a new graph querying
	 * 
	 */
	private void clearStopAllInProcessing() {
		processingApp.noLoop();
		processingApp.clearAllPatternsInvolvedInGraph();
		processingApp.loop();
	}

	/**
	 * Method to display Node and Edges of the Graph Query
	 * 
	 * @param mouseX
	 * @param mouseY
	 */
	public void display(double mouseX, double mouseY) {
		if (displaying) {
			captionStatusBar.setText("Ready");
			query.displayEdges(gc, mouseX, mouseY, true);
			query.displayNodes(gc, mouseX, mouseY, true);
		}
	}

	/**
	 * Method to remove External the Pie Ghost and the Internal Ghost line dashes
	 */
	private void removeExternalInternalGhost() {
		// Removing External PieGhost
		List<Node> listRemovePieGhost = new ArrayList<Node>();
		for (Node n : parent.getPannableCanvas().getChildren()) {
			if (n instanceof PieGhost) {
				listRemovePieGhost.add(n);
			}
		}
		parent.getPannableCanvas().getChildren().removeAll(listRemovePieGhost);
		// Removing Internal line dashes
		for (Multiedge multiedgeQuery : query.getListMultiedge()) {
			List<Integer> listRemoveInternal = new ArrayList<Integer>();
			for (Edge edgeQuery : multiedgeQuery.getListEdge()) {
				if (edgeQuery.getStateGhost() == Constants.GRAPH_EDGE_IS_GHOST) {
					listRemoveInternal.add(edgeQuery.getId());
//					multiedgeQuery.getListEdge().remove(edgeQuery.getId());
				}
			}

			for (Integer integer : listRemoveInternal) {
				multiedgeQuery.getListEdge().remove(integer);
			}

		}

		// Refresh canvas
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		display(0, 0);
	}

	/**
	 * Method to load a Graph Query
	 * 
	 * @param g Graph to load
	 */
	private void initQueryGraph() {
		//gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		double borderPadding = Constants.GRAPH_QUERY_NODE_RADIUS * 2;
//		query.normalizationNodePosition(canvas.getWidth(), canvas.getHeight(), borderPadding, borderPadding,
//				borderPadding, borderPadding);
		
		query.changeMultiedgePosition();
		
		for (Vertex node : query.getListNode()) {
			node.setRadius(Constants.GRAPH_QUERY_NODE_RADIUS);
			node.getFill().setFillOpacity(100);
			node.setStroke(new Stroke(Constants.GRAPH_QUERY_NODE_STROKE));
			
			node.getEtiqueta().setLabelled(false);
			node.getEtiqueta().setShowRectangle(true);
			
			if(node.getEtiqueta().getPinId()!=-1){
				node.getEtiqueta().setLabelled(true);
			}
			node.setShowImgBackground(true);
			
			
		}

		for (Multiedge multiedge : query.getListMultiedge()) {
			for (Edge edge : multiedge.getListEdge()) {
				edge.setVisible(true);
				edge.getStroke().setStroked(true);
				edge.getStroke().setStrokeWeight(Constants.GRAPH_QUERY_EDGE_STROKE_WEIGHT);
				edge.getEtiqueta().setLabelled(true);
				edge.setStateGhost(Constants.GRAPH_EDGE_NEVER_GHOST);
			}
		}
	}

	/**
	 * Method to save a graphQuery to a GML file
	 * 
	 * @param file
	 */
	private void saveQueryGraphToGMLFile(File file) {
		cmbAddEdgeUnselect();
		cmbAddNodeUnselect();
		DiversParser.saveGML(query, file.getAbsolutePath());
	}

	/**
	 * Method to save a graphQuery to a PNG file
	 * 
	 * @param file
	 */
	@SuppressWarnings("unused")
	private void savePngFile(File file) {
		WritableImage wim = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
		canvas.snapshot(null, wim);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
		} catch (Exception s) {
		}
	}

	/**
	 * Method to open a GML file
	 * 
	 * @param file
	 */
	private void openGMLFile(File file) {
		cmbAddEdgeUnselect();
		cmbAddNodeUnselect();
		query = DiversParser.loadGML(file.getAbsolutePath());
		initQueryGraph();
	}

	/**
	 * Method to create the Top ToolBar
	 */
	public void createTopToolBar() {
		// to translate all the top toolbar buttons by the width of a button
		double translateX = Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER + 2;

		Button btnOpen = ComponentCreator.makeButton("Load", "open.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Load query");

		btnOpen.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String[] allowedExtension = { "gml" };
				FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
				File file = fileChooser.showOpenDialog(null);
				if (file != null) {
					openGMLFile(file);
				}
				display(0, 0);
			};

		});

		Button btnNew = ComponentCreator.makeButton("New", "new.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "New query");

		btnNew.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (query.getListNode().size() != 0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(Constants.GUI_TITLE_MSG_DIALOG);
					alert.setHeaderText(Constants.GUI_MSG_CONFIRMATION_NEW);

					ButtonType buttonTypeYes = new ButtonType("Yes");
					ButtonType buttonTypeNo = new ButtonType("No");
					ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeYes) {
						// ... user chose OK
						// Show save file dialog
						String[] allowedExtension = { "gml" };
						FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
						// File file = fileChooser.showSaveDialog(primaryStage);
						File file = fileChooser.showSaveDialog(null); // there

						if (file != null) {
							gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
							toggleLeftGroup.selectToggle(null);
							cmbAddEdgeUnselect();
							cmbAddNodeUnselect();
							DiversParser.saveGML(query, file.getAbsolutePath());
							query = new Graph();
							// nodeRadius = Constants.GRAPH_QUERY_NODE_RADIUS;
							clearStopAllInProcessing();
							removeExternalInternalGhost();
							tbGhost.setDisable(true);
						}
					} else if (result.get() == buttonTypeNo) {
						query = new Graph();
						// nodeRadius = Constants.GRAPH_QUERY_NODE_RADIUS;
						gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
						toggleLeftGroup.selectToggle(null);
						cmbAddEdgeUnselect();
						cmbAddNodeUnselect();
						clearStopAllInProcessing();
						removeExternalInternalGhost();
						tbGhost.setDisable(true);
					} else {
						// ... user chose CANCEL or closed the dialog
					}
				}
				display(0, 0);
			};
		});

		Button btnSave = ComponentCreator.makeButton("Save", "save.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Save query");

		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (query.getListNode().size() != 0) {
					String[] allowedExtension = { "gml" };
					FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
					File file = fileChooser.showSaveDialog(null);
					if (file != null) {
						saveQueryGraphToGMLFile(file);
					}
				}
				display(0, 0);
			};
		});

		tbSearch = ComponentCreator.makeToggleButton("SEARCH", "search.png", null, ContentDisplay.LEFT,
				Constants.VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, translateX, 0,
				this.getClass(), "Search embeddings in the Graph");
		tbSearch.getStyleClass().add("btnToggleMenu");

		tbSearch.setOnAction(new EventHandler<ActionEvent>() {

			// Al hacer click, selected attribute changes
			public void handle(ActionEvent e) {

				if (query.isConnected()) {

					// Disable block app to see the heatmap update
					processingApp.setAppBlocked(false);

					toggleLeftGroup.selectToggle(null);

					tbGhost.setDisable(true);

					switch (GuiGraphDB.sumgraProcessState) {
					case Constants.SUMGRA_STATE_NEW:
//						displaying = false;
						// put in Active
						GuiGraphDB.sumgraProcessState = Constants.SUMGRA_STATE_ACTIVE;
						// set tbSearch to the next option able
						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("pause.png")))));
						tbSearch.setText("PAUSE");

						//
						String sumgraGraphPath = Constants.PATH_DATA.concat(Constants.SUMGRA_GRAPH_FILE);
						String queryGraphPath = Constants.PATH_DATA.concat(Constants.QUERY_GRAPH_FILE);

						removeExternalInternalGhost();
						prepareGraphQueryTopology();

						cmbAddEdgeUnselect();
						cmbAddNodeUnselect();


						IntIntHashMap parserIdsViejoANuevos = new IntIntHashMap();
						for (Multiedge multiedge : query.getListMultiedge()) {
							int source = multiedge.getIdSource();
							if (!parserIdsViejoANuevos.containsKey(source)) {
								parserIdsViejoANuevos.put(source, parserIdsViejoANuevos.size());
							}

							int target = multiedge.getIdTarget();
							if (!parserIdsViejoANuevos.containsKey(target)) {
								parserIdsViejoANuevos.put(target, parserIdsViejoANuevos.size());
							}
						}
						
						System.out.println("---------------------------");
						System.out.println("BEFORE:");
						System.out.println(query);
						System.out.println("---------------------------");
						
						System.out.println("PARSER:");
						System.out.println(parserIdsViejoANuevos);
						System.out.println("---------------------------");
						query = GraphUtil.updateNodeIdsOnAGraph(query, parserIdsViejoANuevos);
						initQueryGraph();// to init the properties of the Query graph
						File file = new File(queryGraphPath);
						System.out.println("AFTER");
						System.out.println(query);
						DiversParser.saveMultiEdgesTXT(query, file.getAbsolutePath());
						System.out.println("---------------------------");

//						
						sumgraBuffer = new SumgraBuffer(sumgraThresholdBufferBacktraking);
						SumgraBuffer.processingApp = processingApp;

						// Constraints
						int[] constraints = new int[query.getListNode().size()];
						for (int index = 0; index < query.getListNode().size(); index++) {
//							logger.info("constraint " + query.getListNode().get(index).getEtiqueta().getPinId());
							constraints[index] = query.getListNode().get(index).getEtiqueta().getPinId();
						}

						logger.info("Constraints " + constraints);

//						MainSumgra main = new MainSumgra(sumgraGraphPath, queryGraphPath, constraints,
//								sumgraSleepBacktraking);
						
						
						GraphDatabase.pathGraphDatabase = sumgraGraphPath;
						GraphDatabase.pathQueryGraph = queryGraphPath;
						GraphDatabase.constraintsVertices = constraints;
						GraphDatabase.sleepTimeBacktraking = sumgraSleepBacktraking;

						GraphDatabase.sumgraBuffer = sumgraBuffer;
						
						MainSumgra main = new MainSumgra();
						main.start();

						// loop sumgra process
						loopSumgraProcess();
						break;
					case Constants.SUMGRA_STATE_ACTIVE:
						// put in Pause 2
						// set tbSearch to the next option able
						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("search.png")))));
						tbSearch.setText("RESUME");
						// To recalcule the nodelistpatterns data
						processingApp.pausedSumgraProcess();
						GuiGraphDB.sumgraProcessState = Constants.SUMGRA_STATE_PAUSED;
						break;
					case Constants.SUMGRA_STATE_PAUSED:
						// put in Active again 1
						// set tbSearch to the next option able
						tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
								.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("pause.png")))));
						tbSearch.setText("Pause");
						GuiGraphDB.sumgraProcessState = Constants.SUMGRA_STATE_ACTIVE;
						loopSumgraProcess();
						break;
					case Constants.SUMGRA_STATE_FINISHED:
						// put in Finish 3
						// set tbSearch to the next option able
						tbSearch.setText("END");
						GuiGraphDB.sumgraProcessState = Constants.SUMGRA_STATE_FINISHED;
						break;
					default:
						break;
					}
				}
			}

		});

		// Spacer
		final Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		int spacerWidth = (int) (Constants.LAYOUT_QUERY_VIEW_WIDTH - (4 * Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH)
				- Constants.VISUAL_GRAPH_QUERY_BTN_SEARCH_WIDTH - 20);
		spacer.setMinSize(spacerWidth, 1);
		spacer.setMaxSize(spacerWidth, 1);

		topToolBar.getStyleClass().add("backgroundMenu");
		topToolBar.setPadding(new Insets(1, 0, 1, 0));
		topToolBar.setOrientation(Orientation.HORIZONTAL);
		topToolBar.getItems().addAll(btnOpen, btnNew, btnSave, spacer, tbSearch);

		for (Node n : topToolBar.getItems()) {
			if (n instanceof Button) {
				n.getStyleClass().add("btnMenu");
			}
		}

	}

	/**
	 * Method to loop the Sumgra process
	 */
	private void loopSumgraProcess() {
		new Thread(new Runnable() {
			public void run() {
				while (GuiGraphDB.sumgraProcessState == Constants.SUMGRA_STATE_ACTIVE) {
					sumgraBuffer.pickUp();
				}
			}
		}).start();
	}

	/**
	 * Method to prepare the query topology
	 */
	@SuppressWarnings("serial")
	private void prepareGraphQueryTopology() {

		// for (Multiedge multiedge : query.getListMultiedge()) {
		// MutableCollection<Edge> edgesWithoutGhost =
		// multiedge.getListEdge().select(new Predicate<Edge>() {
		// public boolean accept(Edge edge) {
		// return edge.getStateGhost() != Constants.GRAPH_EDGE_IS_GHOST;
		// }
		// });
		// }
		//
		// for (Multiedge multiedge : query.getListMultiedge()) {
		//
		// }
		//
		//
		//
		//
		// Map<Integer, Integer> keyValue = new HashMap<Integer, Integer>();
		//
		// // Removing edges with the "IS A Ghost" state
		// List<Edge> listTmpIsGhost = new ArrayList<Edge>();
		// for (Edge edge : query.getListEdge()) {
		// if (edge.getStateGhost() == Constants.GRAPH_EDGE_IS_GHOST) {
		// listTmpIsGhost.add(edge);
		// }
		// }
		// query.getListEdge().removeAll(listTmpIsGhost);
		// //
		//
		// // Setting the key - value
		// for (Edge edge : query.getListEdge()) {
		// if (!keyValue.containsValue(edge.getIdSource())) {
		// keyValue.put(keyValue.size(), edge.getIdSource());
		// }
		// if (!keyValue.containsValue(edge.getIdTarget())) {
		// keyValue.put(keyValue.size(), edge.getIdTarget());
		// }
		// }
		//
		// // Changing the idSource and idTarget in the listEdge
		// for (Edge edge : query.getListEdge()) {
		// int idSource = GraphUtil.getKeyOfValue(keyValue, edge.getIdSource());
		// int idTarget = GraphUtil.getKeyOfValue(keyValue, edge.getIdTarget());
		//
		// edge.setIdSource(idSource);
		// edge.setIdTarget(idTarget);
		//
		// // Setting the edge in a never ghost state
		// edge.setStateGhost(Constants.GRAPH_EDGE_NEVER_GHOST);
		// }
		//
		// // Changing the idNode in the listNode to make coherence
		// for (Vertex node : query.getListNode()) {
		// int idNode = GraphUtil.getKeyOfValue(keyValue, node.getId());
		// node.setId(idNode);
		// }
		//
		// // Sort ascending the listNode by id
		// // Because the ghost topology is order
		// Collections.sort(query.getListNode(),
		// (Constants.NODE_ID_COMPARATOR));

	}

	/**
	 * Method to create the Left ToolBar
	 */
	public void createLeftToolBar() {
		
		//ADD NODE
		cmbAddNode.getItems().add(NodeType.Person);
		cmbAddNode.getItems().add(NodeType.Address);
		cmbAddNode.getItems().add(NodeType.Company);
		cmbAddNode.getItems().add(NodeType.Client);
		cmbAddNode.setValue(NodeType.Person);

		cmbAddNode.setMinWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER);
		cmbAddNode.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER);
		cmbAddNode.setMinHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddNode.setMaxHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddNode.setPrefSize(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddNode.setTooltip(new Tooltip("Add a node in the query"));

		cmbAddNode.setCellFactory(new Callback<ListView<NodeType>, ListCell<NodeType>>() {
			public ListCell<NodeType> call(ListView<NodeType> p) {
				return new ListCell<NodeType>() {
					@Override
					protected void updateItem(NodeType item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
							setText(null);
						} else {
							ImageView imageView = new ImageView(
									new Image(this.getClass().getClassLoader().getResourceAsStream(
											Constants.RESOURCES_IMAGE_PATH_NODE_TYPES.concat(item.getRelativePath()))));
							setGraphic(imageView);
							setText(item.getLabel());
						}
					}
				};
			}
		});

		// SET THE VALUE NEXTSTEP TO THE BUTTONCELL
		cmbAddNode.setButtonCell(new ListCell<NodeType>() {
			@Override
			protected void updateItem(NodeType item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					setText(null);
				} else {
					setPadding(new Insets(0, 0, 0, 0));
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					ImageView imageView = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream(
							Constants.RESOURCES_IMAGE_PATH_NODE_TYPES.concat(item.getRelativePath()))));
					String label = item.getLabel();
					setGraphic(imageView);
					setText(label);
				}
			}
		});

		// necessary
		cmbAddNode.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				cmbAddNodeSelect();
			}
		});

		// necessary
		cmbAddNode.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				cmbAddNodeSelect();
			}
		});
		
		
		//ADD EDGE TYPE
//		cmbAddEdge.getItems().add(EdgeType.OfficerPanama);
//		cmbAddEdge.getItems().add(EdgeType.IntermediaryPanama);
//		cmbAddEdge.getItems().add(EdgeType.AdressePanama);
//		cmbAddEdge.getItems().add(EdgeType.OfficerOffshore);
//		cmbAddEdge.getItems().add(EdgeType.IntermediaryOffshore);
//		cmbAddEdge.getItems().add(EdgeType.AdresseOffshore);
//		cmbAddEdge.getItems().add(EdgeType.OfficerBahamas);
//		cmbAddEdge.getItems().add(EdgeType.IntermediaryBahamas);
//		cmbAddEdge.getItems().add(EdgeType.AdresseBahamas);
//		cmbAddEdge.getItems().add(EdgeType.OfficerParadise);
//		cmbAddEdge.getItems().add(EdgeType.IntermediaryParadise);
//		cmbAddEdge.getItems().add(EdgeType.AdresseParadise);
//		cmbAddEdge.setValue(EdgeType.OfficerPanama);

		cmbAddEdge.getItems().add(EdgeType.TVCG);
		cmbAddEdge.getItems().add(EdgeType.InfoVis);
		cmbAddEdge.getItems().add(EdgeType.CGF);
		cmbAddEdge.getItems().add(EdgeType.EuroVis);
		cmbAddEdge.getItems().add(EdgeType.PacificVis);
		cmbAddEdge.getItems().add(EdgeType.GraphDrawing);
		cmbAddEdge.getItems().add(EdgeType.CGA);
		cmbAddEdge.getItems().add(EdgeType.IV);
		cmbAddEdge.getItems().add(EdgeType.DASFAA);
		cmbAddEdge.getItems().add(EdgeType.EDBT);
		cmbAddEdge.getItems().add(EdgeType.ICDE);
		cmbAddEdge.getItems().add(EdgeType.ICDM);
		cmbAddEdge.getItems().add(EdgeType.ICDT);
		cmbAddEdge.getItems().add(EdgeType.KDD);
		cmbAddEdge.getItems().add(EdgeType.SDM);
		cmbAddEdge.getItems().add(EdgeType.SSDBM);
		cmbAddEdge.getItems().add(EdgeType.SIGMOD);
		cmbAddEdge.getItems().add(EdgeType.VLDB);
		cmbAddEdge.setValue(EdgeType.TVCG);

		cmbAddEdge.setMinWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER);
		cmbAddEdge.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER);
		cmbAddEdge.setMinHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setMaxHeight(Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setPrefSize(Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT);
		cmbAddEdge.setTooltip(new Tooltip("Add an edge in the query"));

		cmbAddEdge.setCellFactory(new Callback<ListView<EdgeType>, ListCell<EdgeType>>() {
			public ListCell<EdgeType> call(ListView<EdgeType> p) {
				return new ListCell<EdgeType>() {
					@Override
					protected void updateItem(EdgeType item, boolean empty) {
						super.updateItem(item, empty);
						if (item == null || empty) {
							setGraphic(null);
							setText(null);
						} else {
							// setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
							// int cmbAddEdgeWidth = 5;
							 setPrefWidth(200);
							 setMaxWidth(200);
							 setMinWidth(200);
							ImageView imageView = new ImageView(
									new Image(this.getClass().getClassLoader().getResourceAsStream(
											Constants.RESOURCES_IMAGE_PATH_EDGE_TYPES.concat(item.getRelativePath()))));
							setGraphic(imageView);
							setText(item.getLabel());
						}
					}
				};
			}
		});

		// SET THE VALUE NEXTSTEP TO THE BUTTONCELL
		cmbAddEdge.setButtonCell(new ListCell<EdgeType>() {
			@Override
			protected void updateItem(EdgeType item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
					setText(null);
				} else {
					setPadding(new Insets(0, 0, 0, 0));
					setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
					ImageView imageView = new ImageView(new Image(this.getClass().getClassLoader().getResourceAsStream(
							Constants.RESOURCES_IMAGE_PATH_EDGE_TYPES.concat(item.getRelativePath()))));
					String label = item.getLabel();
					setGraphic(imageView);
					setText(label);
				}
			}
		});

		// necessary
		cmbAddEdge.setOnMouseClicked(new EventHandler<Event>() {
			public void handle(Event event) {
				cmbAddEdgeSelect();
			}
		});

		// necesary
		cmbAddEdge.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				cmbAddEdgeSelect();
			}
		});
		
		
		//DELETE
		
		tbDelete = ComponentCreator.makeToggleButton("Delete", "delete.png", toggleLeftGroup,
				ContentDisplay.GRAPHIC_ONLY, Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER,
				Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(), "Delete a vertex/edge in the query");

		tbGhost = ComponentCreator.makeToggleButton("Ghost", "ghost.png", toggleLeftGroup, ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
				"Enable query suggestions");

		tbGhost.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {

				if (tbGhost.isSelected()) {

					// Refresh canvas
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

//					clearStopAllInProcessing();

					// ----------- Internal Ghost ----------
					for (Multiedge multiedge : processingApp.getGraphGhostInternal().getListMultiedge()) {
						int countInternalTopK = 0;
						for (Edge edge : multiedge.getEdgeDescendingByWeight()) {
							if (countInternalTopK < Constants.GRAPH_QUERY_EDGE_GHOST_INTERNAL_TOP_K) {
								addEdge(edge.getIdSource(), edge.getIdTarget(), edge.getType(),
										Constants.GRAPH_EDGE_IS_GHOST);
								countInternalTopK++;
							}
						}
					}

					// ----------- External Ghost ----------
//					double pivotTranslate = 0;
					for (Map.Entry<Integer, List<Edge>> entry : processingApp.getMapGhostExternal().entrySet()) {
						int idNodeQuery = entry.getKey();
						List<Edge> listEdgeFusioned = entry.getValue();
						if (listEdgeFusioned.size() > 0) {
							int countExternaltopK = 0;
							List<GhostSliceItem> edgeTypesOfNode = new ArrayList<GhostSliceItem>();
							for (Edge edge : listEdgeFusioned) {
								if (countExternaltopK < Constants.GRAPH_QUERY_EDGE_GHOST_EXTERNAL_TOP_K) {
									GhostSliceItem item = new GhostSliceItem(idNodeQuery,
											EdgeType.getLayerTypeById(edge.getType().getId()), (int) edge.getWeight());
									edgeTypesOfNode.add(item);
									countExternaltopK++;
								}
							}

							double positionXNodeQuery = query.getListNode().get(idNodeQuery).getPosition().getX1();
							double positionYNodeQuery = query.getListNode().get(idNodeQuery).getPosition().getY1();

							double pieGhostWidth = Constants.GRAPH_QUERY_NODE_RADIUS * 3;
//									+ Constants.VISUAL_GRAPH_QUERY_PIE_GHOST_WIDTH;
							double pieGhostHeight = Constants.GRAPH_QUERY_NODE_RADIUS * 3;
//									+ Constants.VISUAL_GRAPH_QUERY_PIE_GHOST_HEIGHT;

							//
							PieGhost pieGhost = new PieGhost(edgeTypesOfNode, captionStatusBar);
							pieGhost.setVisible(true);
							pieGhost.setPadding(new Insets(0, 0, 0, 0));
							pieGhost.setTranslateX(positionXNodeQuery - (pieGhostWidth / 2));
							pieGhost.setTranslateY(positionYNodeQuery - (pieGhostHeight / 2));
							pieGhost.setMinWidth(pieGhostWidth);
							pieGhost.setMaxWidth(pieGhostWidth);
							pieGhost.setMinHeight(pieGhostHeight);
							pieGhost.setMaxHeight(pieGhostHeight);
							// pivotTranslate = pivotTranslate + pieGhostWidth;

							parent.getPannableCanvas().getChildren().add(pieGhost);
						}
					}
					//
					// Refresh canvas
					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

				} else {
					removeExternalInternalGhost();
				}
			}

		});

		tbGhost.setDisable(true);

		btnFm3 = ComponentCreator.makeButton("Layout", "fm3.png", ContentDisplay.GRAPHIC_ONLY,
				Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH_LARGER, Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT, 0, 0, this.getClass(),
				"Execute a force-directed layout in the query");

		btnFm3.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				executeFm3Algorithm();
			}
		});

		cmbLabelNodes = new ComboBox<String>();
		cmbLabelNodes.setVisible(true);
		cmbLabelNodes.setEditable(true);
		cmbLabelNodes.setMinWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
		cmbLabelNodes.setMaxWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
		cmbLabelNodes.setPrefWidth(Constants.VISUAL_GRAPH_QUERY_CMBLABELNODES_WIDTH);
//		cmbLabelNodes.setItems(observableLabelsNodes);
		cmbLabelNodes.setVisibleRowCount(5);
		
		
		// When select a node attribute
		cmbLabelNodes.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					// Removing the combobox
					parent.getPannableCanvas().getChildren().remove(cmbLabelNodes);

					// reduce -1 because we added "*" item to the combo label
					// index 0 = *
					// index is attache to search in the nodeList en el mismo
					// index orden
					int index = cmbLabelNodes.getSelectionModel().getSelectedIndex() - 1;
					String value = cmbLabelNodes.getSelectionModel().getSelectedItem();

					if (index >= -1) {
						nodeQuerySource.getEtiqueta().setPinId(index);
						nodeQuerySource.getEtiqueta().setText(value);
						nodeQuerySource.getEtiqueta().setLabelled(true);
						nodeQuerySource.getEtiqueta().setShowRectangle(false);
					}

					gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					display(0, 0);

					clearStopAllInProcessing();

				} catch (Exception e) {
					System.out.println(e);
					// TODO: handle exception
				}
			}
		});

		toggleLeftGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
				cmbAddEdgeUnselect();
				cmbAddNodeUnselect();
				if (!tbGhost.isSelected()) {
					removeExternalInternalGhost();
				}
			}
		});

		leftToolBar.getStyleClass().add("backgroundMenu");
		leftToolBar.setPadding(new Insets(0, 2, 0, 2));
		leftToolBar.setOrientation(Orientation.VERTICAL);

		//tbAddNode
		leftToolBar.getItems().addAll(cmbAddNode, cmbAddEdge, tbDelete, tbGhost, btnFm3);

		for (Node n : leftToolBar.getItems()) {
			if (n instanceof Button || n instanceof ComboBox<?>) {
				n.getStyleClass().add("btnMenu");
			} else if (n instanceof ToggleButton) {
				n.getStyleClass().add("btnToggleMenu");
			}
		}
	}

	/**
	 * Method to create a Bottom Status Bar
	 */
	public void createBottomStatusBar() {
		bottomStatusBar.setMinHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);
		bottomStatusBar.setMaxHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);
		bottomStatusBar.setPrefHeight(Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT);

		bottomStatusBar.setText("");
		bottomStatusBar.getLeftItems().add(captionStatusBar);
	}

	public Graph getGraphQuery() {
		return query;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public ToolBar getTopToolBar() {
		return topToolBar;
	}

	public ToolBar getLeftToolBar() {
		return leftToolBar;
	}

	public StatusBar getBottomStatusBar() {
		return bottomStatusBar;
	}

	@Override
	protected NGNode impl_createPeer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean impl_computeContains(double localX, double localY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	private void histoghost() {
		// -------------------------HISTOGHOST
		// ObservableList<PieChart.Data> pieChartData =
		// FXCollections.observableArrayList(
		// new PieChart.Data("Grapefruit", 43), new
		// PieChart.Data("Oranges", 25),
		// new PieChart.Data("Plums", 10), new
		// PieChart.Data("Pears", 42),
		// new PieChart.Data("Apples", 40));
		//
		// final PieChart chart = new PieChart(pieChartData);
		//
		// chart.setLabelsVisible(true);
		//
		// double positionX = node.getPosition().getX1();
		// double positionY = node.getPosition().getY1();
		// logger.info(positionX + ", " + positionY);
		//
		// double val = 70;
		// chart.setTranslateX(positionX + 15);
		// chart.setTranslateY(positionY - 405 + pivotTranslate
		// - 1);
		//
		// // subo
		// // resto la mitad de lo que subo
		//
		// chart.setMinWidth(val);
		// chart.setPrefWidth(val);
		// chart.setMaxWidth(val);
		//
		// chart.setMinHeight(val);
		// chart.setPrefHeight(val);
		// chart.setMaxHeight(val);
		//
		// toolLeftMenu.getItems().add(chart);
		// toolLeftMenu.setMinWidth(50);
		// toolLeftMenu.setPrefWidth(50);
		// toolLeftMenu.setMaxWidth(50);
		//
		// pivotTranslate = pivotTranslate - 70;

		// List<HistoghostItem> edgeTypesOfNode = new
		// ArrayList<HistoghostItem>();
		//
		// int idNode = node.getId();
		//
		// IntObjectHashMap<Object> currentNodeQuery =
		// processingApp.getGhostFinal().get(idNode);
		//
		// logger.info("For node id: " + idNode + "ghost: " +
		// currentNodeQuery);
		//
		// if (currentNodeQuery != null) {
		//
		// // logger.info("analysed node id: " + idNode);
		//
		// MutableIntSet mutableInt = currentNodeQuery.keySet();
		// MutableIntIterator itr = mutableInt.intIterator();
		//
		// // toma los K primeros, hay que ordenar
		// for (int i = 0; i < Constants.TOP_K_GHOST; i++) {
		// int edgeType = itr.next();
		// int numAppears = (int)
		// currentNodeQuery.get(edgeType);
		// // logger.info("edge type: " + edgeType + "
		// // numAppears: " + numAppears);
		// HistoghostItem item = new HistoghostItem(idNode,
		// edgeTypes.get(edgeType), numAppears);
		// edgeTypesOfNode.add(item);
		// }
		//
		// double widthHistoghost = 20;
		// double heightHistoghost = 10;
		// double gTranslateX = node.getPosition().getX1() - 60;
		// double gTranslateY = node.getPosition().getY1() - 370
		// + pivotTranslate;
		//
		// Histoghost histoGhost = new Histoghost(new
		// CategoryAxis(), new NumberAxis(),
		// edgeTypesOfNode);
		// histoGhost.setVisible(true);
		// histoGhost.setTranslateX(gTranslateX);
		// histoGhost.setTranslateY(gTranslateY);
		// histoGhost.setPrefSize(widthHistoghost,
		// heightHistoghost);
		// histoGhost.maxHeight(widthHistoghost);
		// histoGhost.maxWidth(heightHistoghost);
		//
		// toolLeftMenu.getItems().add(initIndexLeftPanel,
		// histoGhost);
		// initIndexLeftPanel++;
		//
		// pivotTranslate = pivotTranslate - 154;
		// }

	}

}
