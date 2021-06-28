package app.gui.preferences;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import app.gui.database.GuiGraphDB;
import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.main.MainSplitPanel;
import app.gui.query.ComponentCreator;
import app.gui.query.GuiQuery;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Preferences {

	public final static Logger logger = Logger.getLogger(Preferences.class);
	public static GuiGraphDB processingApp; // handle action

	public static MainSplitPanel parent;
	private Scene scene;

	private static final String styleClassSubTitle = "subTitle";
	private static final String styleClassItem = "item";

	// TITLED PANE
	private TitledPane tpGraphView = new TitledPane();
	private TitledPane tpEmbeddings = new TitledPane();
	private TitledPane tpKelps = new TitledPane();
	private TitledPane tpSumgra = new TitledPane();

	private final Accordion accordion = new Accordion();

	public Preferences() {

		Group root = new Group();
		scene = new Scene(root, Constants.PREF_WIDTH, Constants.PREF_HEIGHT, Color.WHITE);
		scene.getStylesheets().add(Constants.JAVAFX_STYLE_FILE);

		/**
		 * 
		 * 
		 * 
		 * 
		 * GRAPH VIEW
		 *
		 * 
		 * 
		 */
		Slider slNodeRadius = ComponentCreator.makeSlider(1, 50, Constants.GRAPH_DB_NODE_RADIUS, 10, 1, 1, true, true);
		slNodeRadius.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setVerticesRadius(new_val.doubleValue());
			}
		});
		//
//		Slider slNodeOpacity = ComponentCreator.makeSlider(5, 100, 30, 50, 5,
//				1, true, true);
//		slNodeOpacity.valueProperty().addListener(new ChangeListener<Number>() {
//			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
//				processingApp.setAppBlocked(false);
//				processingApp.getGraph().setVerticesOpacity(new_val.doubleValue());
//			}
//		});
		//
//		final ColorPicker cpNodeColor = new ColorPicker(
//				ColorShape.parserColorProcessingToJavafx(Constants.GRAPH_DB_NODE_FILL.getFillColor()));
//		cpNodeColor.valueProperty().addListener(new ChangeListener<Color>() {
//			@Override
//			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
//				processingApp.setAppBlocked(false);
//				processingApp.getGraph().setVerticesColor(ColorShape.parserColorJavafxToProcessingHsb(newValue));
//			}
//		});
		//
		final CheckBox chkNodeVisible = ComponentCreator.makeCheckBox("", true);
		chkNodeVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowVerticesGraphDB(chkNodeVisible.isSelected());
			};
		});
		//
		final CheckBox chkNodeImgBackgrounVisible = ComponentCreator.makeCheckBox("", false);
		chkNodeImgBackgrounVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setVerticesImgBackground(chkNodeImgBackgrounVisible.isSelected());
			};
		});
		
		final CheckBox chkNodeLabelVisible = ComponentCreator.makeCheckBox("", false);
		chkNodeLabelVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setVerticesLabelled(chkNodeLabelVisible.isSelected());
			};
		});
		
		Slider slNodeLabelFontSize = ComponentCreator.makeSlider(1, 50, Constants.GRAPH_DB_NODE_TEXT_SIZE, 10, 1, 1, true, true);
		slNodeLabelFontSize.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setVerticesFontSize(new_val.doubleValue());
			}
		});
		
		final CheckBox chkNodeLabelBackgroundVisible = ComponentCreator.makeCheckBox("", false);
		chkNodeLabelBackgroundVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setVerticesLabelledBackground(chkNodeLabelBackgroundVisible.isSelected());
			};
		});
		
		//
		//
		final CheckBox chkMouseTooltipVisible = ComponentCreator.makeCheckBox("", false);
		chkMouseTooltipVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setShowMouseToolTip(chkMouseTooltipVisible.isSelected());
			};
		});
		//
		
		final CheckBox chkEdgeVisible = ComponentCreator.makeCheckBox("", false);
		chkEdgeVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowEdgesGraphDB(chkEdgeVisible.isSelected());
			};
		});
		
		final CheckBox chkEdgeLabelVisible = ComponentCreator.makeCheckBox("", false);
		chkEdgeLabelVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setEdgessLabelled(chkEdgeLabelVisible.isSelected());
			};
		});
		
		
		Slider slEdgeLabelFontSize = ComponentCreator.makeSlider(1, 50, Constants.GRAPH_DB_EDGE_TEXT_SIZE, 10, 1, 1, true, true);
		slEdgeLabelFontSize.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setEdgesFontSize(new_val.doubleValue());
			}
		});
		
		
		//
		Slider slLevelTransitionHeatMap = ComponentCreator.makeSlider(1, 30, Constants.HEATMAP_INIT_TRANSITION_ZOOM, 5,
				1, 1, true, true);
		slLevelTransitionHeatMap.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setHeatMapTransitionZoom(new_val.intValue());
			}
		});
		//
		Slider slRadiusHeatMap = ComponentCreator.makeSlider(10, 50, Constants.HEATMAP_INIT_RADIUS, 10, 1, 1, true,
				true);
		slRadiusHeatMap.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setHeatMapRadius(new_val.intValue());
			}
		});

		//
		GridPane gridGraphDBView = new GridPane();
		gridGraphDBView.setGridLinesVisible(false);
		gridGraphDBView.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridGraphDBView.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridGraphDBView.setVgap(Constants.PREF_VERTICAL_GAP);
		gridGraphDBView.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridGraphDBView.setPadding(Constants.PREF_INSETS);
		
		gridGraphDBView.add(ComponentCreator.makeLabel("Tooltip Mouse: ", styleClassItem), 0, 0);
		gridGraphDBView.add(chkMouseTooltipVisible, 1, 0);
		
		
		gridGraphDBView.add(ComponentCreator.makeLabel("Nodes", styleClassSubTitle), 0, 1);
		gridGraphDBView.add(ComponentCreator.makeLabel("Visible: ", styleClassItem), 0, 2);
		gridGraphDBView.add(chkNodeVisible, 1, 2);
		gridGraphDBView.add(ComponentCreator.makeLabel("Radius: ", styleClassItem), 0, 3);
		gridGraphDBView.add(slNodeRadius, 1, 3);
		gridGraphDBView.add(ComponentCreator.makeLabel("Image Background: ", styleClassItem), 0, 4);
		gridGraphDBView.add(chkNodeImgBackgrounVisible, 1, 4);
		gridGraphDBView.add(ComponentCreator.makeLabel("Labelled: ", styleClassItem), 0, 5);
		gridGraphDBView.add(chkNodeLabelVisible, 1, 5);
		gridGraphDBView.add(ComponentCreator.makeLabel("Label font-size: ", styleClassItem), 0, 6);
		gridGraphDBView.add(slNodeLabelFontSize, 1, 6);
		gridGraphDBView.add(ComponentCreator.makeLabel("Label Background: ", styleClassItem), 0, 7);
		gridGraphDBView.add(chkNodeLabelBackgroundVisible, 1, 7);


		gridGraphDBView.add(ComponentCreator.makeLabel("Edges", styleClassSubTitle), 0, 8);
		gridGraphDBView.add(ComponentCreator.makeLabel("Visible: ", styleClassItem), 0, 9);
		gridGraphDBView.add(chkEdgeVisible, 1, 9);
		gridGraphDBView.add(ComponentCreator.makeLabel("Labelled: ", styleClassItem), 0, 10);
		gridGraphDBView.add(chkEdgeLabelVisible, 1, 10);	
		gridGraphDBView.add(ComponentCreator.makeLabel("Label font-size: ", styleClassItem), 0, 11);
		gridGraphDBView.add(slEdgeLabelFontSize, 1, 11);		
		
		gridGraphDBView.add(ComponentCreator.makeLabel("Heatmap", styleClassSubTitle), 0, 13, 2, 1);
		gridGraphDBView.add(ComponentCreator.makeLabel("Transition level: ", styleClassItem), 0, 14);
		gridGraphDBView.add(slLevelTransitionHeatMap, 1, 14);
		gridGraphDBView.add(ComponentCreator.makeLabel("Radius: ", styleClassItem), 0, 15);
		gridGraphDBView.add(slRadiusHeatMap, 1, 15);
		//
		tpGraphView = new TitledPane();
		tpGraphView.setText(Constants.PREF_PANE_GRAPH_DATABASE);
		gridGraphDBView.getStyleClass().add("preferences");
		tpGraphView.setContent(gridGraphDBView);

		/**
		 * 
		 * 
		 * 
		 * 
		 * EMBEDDINGS
		 *
		 * 
		 * 
		 */
//		Slider slEmbeddingOpacity = ComponentCreator.makeSlider(5, 100,
//				100, 50, 5, 1, true, true);
//		slEmbeddingOpacity.valueProperty().addListener(new ChangeListener<Number>() {
//			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
//				processingApp.setAppBlocked(false);
//				processingApp.getGraph().setVerticesEmbeddingsOpacity(new_val.doubleValue());
//			}
//		});
		//
//		final ColorPicker cpEmbeddingColor = new ColorPicker(ColorShape
//				.parserColorProcessingToJavafx(Constants.GRAPH_DB_NODE_PATTERN_NO_SELECTED_FILL.getFillColor()));
//		cpEmbeddingColor.valueProperty().addListener(new ChangeListener<Color>() {
//			@Override
//			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
//				processingApp.setAppBlocked(false);
//				processingApp.getGraph()
//						.setVerticesEmbeddingsColor(ColorShape.parserColorJavafxToProcessingHsb(newValue));
//			}
//		});
		//
		final CheckBox chkEmbeddingVisible = ComponentCreator.makeCheckBox("", true);
		chkEmbeddingVisible.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowEmbeddingsGraphDB(chkEmbeddingVisible.isSelected());
			};
		});
		//
		final CheckBox chkVisibleEmbeddLabel = ComponentCreator.makeCheckBox("", true);
		chkVisibleEmbeddLabel.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setListNodePatternLabelVisibility(chkVisibleEmbeddLabel.isSelected());
			};
		});
		//
		GridPane gridEmbeddings = new GridPane();
		gridEmbeddings.setGridLinesVisible(false);
		gridEmbeddings.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridEmbeddings.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridEmbeddings.setVgap(Constants.PREF_VERTICAL_GAP);
		gridEmbeddings.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridEmbeddings.setPadding(Constants.PREF_INSETS);

		//
		gridEmbeddings.add(ComponentCreator.makeLabel("Nodes", styleClassSubTitle), 0, 0);
		gridEmbeddings.add(ComponentCreator.makeLabel("Visible: ", styleClassItem), 0, 1);
		gridEmbeddings.add(chkEmbeddingVisible, 1, 1);
//		gridEmbeddings.add(ComponentCreator.makeLabel("Color: ", styleClassItem), 0, 2);
//		gridEmbeddings.add(cpEmbeddingColor, 1, 2);
//		gridEmbeddings.add(ComponentCreator.makeLabel("Opacity: ", styleClassItem), 0, 3);
//		gridEmbeddings.add(slEmbeddingOpacity, 1, 3);

		gridEmbeddings.add(ComponentCreator.makeLabel("Labels", styleClassSubTitle), 0, 5);
		gridEmbeddings.add(ComponentCreator.makeLabel("Visible: ", styleClassItem), 0, 6);
		gridEmbeddings.add(chkVisibleEmbeddLabel, 1, 6);

		tpEmbeddings = new TitledPane();
		tpEmbeddings.setText(Constants.PREF_PANE_GRAPH_EMBEDDINGS);
		gridEmbeddings.getStyleClass().add("preferences");
		tpEmbeddings.setContent(gridEmbeddings);

		/**
		 * 
		 * 
		 * 
		 * 
		 * KELPS
		 *
		 * 
		 * 
		 */
		GridPane gridKelps = new GridPane();
		gridKelps.setGridLinesVisible(false);
		gridKelps.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridKelps.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridKelps.setVgap(Constants.PREF_VERTICAL_GAP);
		gridKelps.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridKelps.setPadding(Constants.PREF_INSETS);

		//
		final ToggleGroup radioGroup = new ToggleGroup();
		RadioButton rbMst = ComponentCreator.makeToggleRadio("MST", Constants.KELP_RELATION_MST, radioGroup, 0, 0,
				"Minimum Spanning Tree", false);
		RadioButton rbTopology = ComponentCreator.makeToggleRadio("Topology", Constants.KELP_RELATION_TOPOLOGY,
				radioGroup, 0, 0, "Topology", true);
		radioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (radioGroup.getSelectedToggle() != null) {
					processingApp.setAppBlocked(false);
					processingApp.setKelpTypeRelation( (Integer) radioGroup.getSelectedToggle().getUserData());
				}
			}
		});

		//
		Slider slKelpFacNode = ComponentCreator.makeSlider(10, 80, Constants.KELP_NODE_FACTOR, 10, 1, 1, true, true);
		slKelpFacNode.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpFacteurNode(new_val.intValue());
			}
		});
		//
		Slider slKelpFacEdge = ComponentCreator.makeSlider(10, 50, Constants.KELP_EDGE_FACTOR, 10, 1, 1, true, true);
		slKelpFacEdge.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpFacteurEdge(new_val.intValue());
			}
		});

		//
		final CheckBox chkKelpShowEdge = ComponentCreator.makeCheckBox("", Constants.KELP_EDGE_VISIBLE);
		chkKelpShowEdge.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setShowKelpLines(chkKelpShowEdge.isSelected());
			};
		});

		//
		final CheckBox chkKelpOverlap = ComponentCreator.makeCheckBox("", Constants.KELP_CHECK_OVERLAP_EDGE_NODE);
		chkKelpOverlap.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				processingApp.setKelpOverlapLines(chkKelpOverlap.isSelected());
			};
		});
		//
		Slider slKelpTextSize = ComponentCreator.makeSlider(5, 100, Constants.KELP_TEXT_SIZE, 10, 5, 1, true, true);
		slKelpTextSize.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setTextSize(new_val.doubleValue());
			}
		});
		//
		Slider slKelpPadding = ComponentCreator.makeSlider(0, 20, Constants.KELP_TEXT_BACKGROUND_PADDING, 10, 5, 1,
				true, true);
		slKelpPadding.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setBackgroundTextPadding(new_val.doubleValue());
			}
		});

		//
		final ColorPicker cKelpLabelColor = new ColorPicker(
				ColorShape.parserColorProcessingToJavafx(Constants.KELP_TEXT_BACKGROUND_FILL.getFillColor()));
		cKelpLabelColor.valueProperty().addListener(new ChangeListener<Color>() {
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setBackgroundText(ColorShape.parserColorJavafxToProcessingHsb(newValue));
			}
		});
		//
		//
		Slider slKelpLabelOpacity = ComponentCreator.makeSlider(5, 100,
				Constants.KELP_TEXT_BACKGROUND_FILL.getFillOpacity(), 50, 5, 1, true, true);
		slKelpLabelOpacity.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});



		gridKelps.add(ComponentCreator.makeLabel("Kelp-Diagrams", styleClassSubTitle), 0, 0, 2, 1);
		gridKelps.add(ComponentCreator.makeLabel("Relation type: ", styleClassItem), 0, 1);
		gridKelps.add(rbMst, 1, 1);
		gridKelps.add(rbTopology, 1, 2);

		gridKelps.add(ComponentCreator.makeLabel("Node facteur: ", styleClassItem), 0, 3);
		gridKelps.add(slKelpFacNode, 1, 3);

		gridKelps.add(ComponentCreator.makeLabel("Edge facteur: ", styleClassItem), 0, 4);
		gridKelps.add(slKelpFacEdge, 1, 4);

		gridKelps.add(ComponentCreator.makeLabel("Show edge: ", styleClassItem), 0, 5);
		gridKelps.add(chkKelpShowEdge, 1, 5);

		gridKelps.add(ComponentCreator.makeLabel("Check overlap: ", styleClassItem), 0, 6);
		gridKelps.add(chkKelpOverlap, 1, 6);
		gridKelps.add(ComponentCreator.makeLabel("Label", styleClassSubTitle), 0, 8);
		gridKelps.add(ComponentCreator.makeLabel("Text size: ", styleClassItem), 0, 9);
		gridKelps.add(slKelpTextSize, 1, 9);
		gridKelps.add(ComponentCreator.makeLabel("Text padding: ", styleClassItem), 0, 10);
		gridKelps.add(slKelpPadding, 1, 10);
		gridKelps.add(ComponentCreator.makeLabel("Color: ", styleClassItem), 0, 11);
		gridKelps.add(cKelpLabelColor, 1, 11);
		gridKelps.add(ComponentCreator.makeLabel("Opacity: ", styleClassItem), 0, 12);
		gridKelps.add(slKelpLabelOpacity, 1, 12);

		tpKelps = new TitledPane();
		tpKelps.setText(Constants.PREF_PANE_KELP_DIAGRAMS);
		gridKelps.getStyleClass().add("preferences");
		tpKelps.setContent(gridKelps);

		/**
		 * 
		 * 
		 * 
		 * 
		 * SUMGRA
		 *
		 * 
		 * 
		 */
		Slider slSumgraBufferThreshold = ComponentCreator.makeSlider(1, 200,
				Constants.SUMGRA_THRESHOLD_BUFFER_BACKTRAKING, 200, 1, 1, true, true);
		slSumgraBufferThreshold.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				GuiQuery.sumgraThresholdBufferBacktraking = new_val.intValue();
				// processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});

		Slider slSumgraSleepThread = ComponentCreator.makeSlider(0, 10, Constants.SUMGRA_SLEEP_TIME_BACKTRAKING, 200, 1,
				1, true, true);
		slSumgraSleepThread.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				GuiQuery.sumgraSleepBacktraking = new_val.intValue();
				// processingApp.hypergraph.setOpacityBackgroundTextPadding(new_val.doubleValue());
			}
		});

		Label lbSumgraBufferSize = ComponentCreator.makeLabel("Buffer threshold:", styleClassItem);
		Label lbSumgraSleepThread = ComponentCreator.makeLabel("Sleep thread (ms): ", styleClassItem);

		GridPane gridSumgra = new GridPane();
		gridSumgra.setGridLinesVisible(false);
		gridSumgra.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA1));
		gridSumgra.getColumnConstraints().add(new ColumnConstraints(Constants.PREF_WIDTH_COLUMNA2));
		gridSumgra.setVgap(Constants.PREF_VERTICAL_GAP);
		gridSumgra.setHgap(Constants.PREF_HORIZONTAL_GAP);
		gridSumgra.setPadding(Constants.PREF_INSETS);
		gridSumgra.add(lbSumgraBufferSize, 0, 0);
		gridSumgra.add(slSumgraBufferThreshold, 1, 0);
		gridSumgra.add(lbSumgraSleepThread, 0, 1);
		gridSumgra.add(slSumgraSleepThread, 1, 1);

		//
		tpSumgra = new TitledPane();
		tpSumgra.setText(Constants.PREF_PANE_GRAPH_ENGINE);
		gridSumgra.getStyleClass().add("preferences");
		tpSumgra.setContent(gridSumgra);

		//
		//

		// ------- TITLEDPANE GRAPH QUERY -------
		//
		// tpGraphQuery = new TitledPane();
		// tpGraphQuery.setText(Constants.PREF_PANE_GRAPH_QUERY);
		// GridPane gridGraphQuery = new GridPane();
		// gridGraphQuery.setVgap(Constants.PREF_VERTICAL_GAP);
		// gridGraphQuery.setPadding(Constants.PREF_INSETS);
		// tpGraphQuery.setContent(gridGraphQuery);

		// ------- TITLEDPANE GRAPH QUERY ENGINE -------
		//
		// tpGraphQueryEngine = new TitledPane();
		// tpGraphQueryEngine.setText(Constants.PREF_PANE_GRAPH_QUERY_ENGINE);
		// GridPane gridGraphQueryEngine = new GridPane();
		// gridGraphQueryEngine.setVgap(Constants.PREF_VERTICAL_GAP);
		// gridGraphQueryEngine.setHgap(Constants.PREF_HORIZONTAL_GAP);
		// gridGraphQueryEngine.setPadding(Constants.PREF_INSETS);
		// tpGraphQueryEngine.setContent(gridGraphQueryEngine);

		// ADDING TO THE ACCORDION
		accordion.getPanes().addAll(tpGraphView, tpEmbeddings, tpKelps, tpSumgra);
		accordion.setExpandedPane(tpGraphView);

		root.getChildren().add(accordion);
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

}
