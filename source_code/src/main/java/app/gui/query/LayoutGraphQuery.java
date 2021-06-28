package app.gui.query;

import java.util.List;

import org.apache.log4j.Logger;

import app.graph.structure.Graph;
import app.gui.database.GuiGraphDB;
import app.gui.ghost.PieGhost;
import app.gui.histogram.Histogram;
import app.gui.main.Application;
import app.gui.main.Constants;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

//
public class LayoutGraphQuery {

	public final static Logger logger = Logger.getLogger(LayoutGraphQuery.class);
	public static GuiGraphDB processingApp; // handle action
	public static GuiQuery designQuery;// handle action
	private Scene scene;

	private PannableCanvas pannableCanvas;
	Spinner<Integer> numHistoCategories;

	// LAYOUTS
	private GridPane layoutGrid;
	private BorderPane layoutGraphQueryView;
	private BorderPane layoutHistogram;

	public LayoutGraphQuery() {

		Group root = new Group();
		scene = new Scene(root, 0, 0, Color.WHITE);

		// scene.setOnMouseClicked(mouseHandler);
		// scene.setOnMouseDragged(mouseHandler);
		scene.setOnMouseEntered(mouseHandler);
		scene.setOnMouseExited(mouseHandler);
		// scene.setOnMouseMoved(mouseHandler);
		// scene.setOnMousePressed(mouseHandler);
		// scene.setOnMouseReleased(mouseHandler);

		// Link to the style sheet file (stylesheet)
		scene.getStylesheets().add(Constants.JAVAFX_STYLE_FILE);

		// ------------ TAB GRAPH QUERYING AND HISTOGRAM ------------

		GuiQuery.parent = this;
		layoutGraphQueryView = new BorderPane();
		
		double heightQueryView = Constants.VISUAL_GRAPH_QUERY_CANVAS_HEIGHT + Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT + Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT;
		layoutGraphQueryView.setMinHeight(heightQueryView);
		layoutGraphQueryView.setMaxHeight(heightQueryView);
		layoutGraphQueryView.setPrefHeight(heightQueryView);

		layoutHistogram = new BorderPane();
		layoutHistogram.setId("histogramLayout");
		
		double heightLayoutHistogram = Application.LAYOUT_HISTOGRAM_HEIGHT;
		
//		double heightLayoutHistogram = Application.SCREEN_HEIGHT - (Constants.VISUAL_GRAPH_QUERY_CANVAS_HEIGHT
//				+ Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT + Constants.VISUAL_GRAPH_QUERY_STATUS_HEIGHT) - 100; // -60
																												// //
																												// 110
																												// normal
		layoutHistogram.setMinHeight(heightLayoutHistogram);
		layoutHistogram.setMaxHeight(heightLayoutHistogram);
		layoutHistogram.setPrefHeight(heightLayoutHistogram);

		numHistoCategories = new Spinner<Integer>(1, 30, Constants.HISTOGRAM_NUMBER_CATEGORIES, 1);
		numHistoCategories.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				processingApp.setAppBlocked(false);
				processingApp.setHistogramNumCategories(new_val.intValue());
			}
		});
		numHistoCategories.setVisible(false);
		numHistoCategories.setPrefWidth(80);
		layoutHistogram.setBottom(numHistoCategories);
		BorderPane.setAlignment(numHistoCategories, Pos.CENTER);

		layoutGrid = new GridPane();

		designQuery = new GuiQuery();
		PieGhost.designQuery = designQuery;

		// create pannablecanvas
		// PannableCanvas pannableCanvas = new PannableCanvas();
		pannableCanvas = new PannableCanvas();
		pannableCanvas.setTranslateX(0);
		pannableCanvas.setTranslateY(0);
		SceneGestures sceneGestures = new SceneGestures(pannableCanvas);
		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

		pannableCanvas.getChildren().addAll(designQuery.getCanvas());
		layoutGraphQueryView.setCenter(pannableCanvas);
		layoutGraphQueryView.setTop(designQuery.getTopToolBar());
		layoutGraphQueryView.setLeft(designQuery.getLeftToolBar());
		layoutGraphQueryView.setBottom(designQuery.getBottomStatusBar());

		//
		layoutGrid.add(layoutGraphQueryView, 0, 1);
		layoutGrid.add(addToolBar(), 0, 0);
		layoutGrid.add(layoutHistogram, 0, 2);
		//
		layoutGrid.prefHeightProperty().bind(scene.heightProperty());
		layoutGrid.prefWidthProperty().bind(scene.widthProperty());

		root.getChildren().add(layoutGrid);

	}

	private ToolBar addToolBar() {
		ToolBar toolBar = new ToolBar();

		toolBar.setMaxHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setMinHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setPrefHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.getStyleClass().add("toolBarLayout");

		Label lblTitle = new Label(Constants.LAYOUT_GRAPH_QUERY_VIEW_TITLE);
		lblTitle.getStyleClass().add("toolBarLayoutTitle");
		toolBar.getItems().addAll(lblTitle);
		return toolBar;
	}

	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
				// logger.info("PROCESSING APP BLOCKED");
				processingApp.setAppBlocked(true);
			} else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
				// logger.info("PROCESSING APP NOT BLOCKED");
				processingApp.setAppBlocked(false);
			}
		}
	};

	public void createTabHistogram(final List<Graph> listGraph, final int numberOfCategies) {
		Platform.runLater(new Runnable() {
			public void run() {
				Histogram graphHistogram = new Histogram(new CategoryAxis(), new NumberAxis(), listGraph,
						numberOfCategies);
				layoutHistogram.setCenter(null);
				layoutHistogram.setCenter(graphHistogram);
				numHistoCategories.setVisible(true);
			}
		});
	}

	public void clearTabHistogram() {
		Platform.runLater(new Runnable() {
			public void run() {
				layoutHistogram.setCenter(null);
				numHistoCategories.setVisible(false);
			}
		});
	}

	public void updateGhostButton() {
		Platform.runLater(new Runnable() {
			public void run() {
				GuiQuery.tbGhost.setDisable(false);
				GuiQuery.tbGhost.setSelected(false);
			}
		});
	}

	public void updateSearchBtn() {
		Platform.runLater(new Runnable() {
			public void run() {
				GuiQuery.tbSearch.setGraphic(new ImageView(new Image(this.getClass().getClassLoader()
						.getResourceAsStream(Constants.RESOURCES_IMAGE_PATH.concat("search.png")))));
				GuiQuery.tbSearch.setText("SEARCH");
				// GraphQuerying.tbSearch.setDisable(true);
				GuiQuery.tbSearch.setSelected(false);
			}
		});
	}

	public Scene getScene() {
		return scene;
	}

	public PannableCanvas getPannableCanvas() {
		return pannableCanvas;
	}

	public void setPannableCanvas(PannableCanvas pannableCanvas) {
		this.pannableCanvas = pannableCanvas;
	}

}
