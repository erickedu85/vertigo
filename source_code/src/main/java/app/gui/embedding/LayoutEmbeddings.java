package app.gui.embedding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import app.graph.structure.Graph;
import app.gui.database.GuiGraphDB;
import app.gui.main.Constants;
import app.gui.query.ComponentCreator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import processing.core.PApplet;
import processing.core.PConstants;

public class LayoutEmbeddings {
	public final static Logger logger = Logger.getLogger(LayoutEmbeddings.class);
	public static GuiGraphDB processingApp; // handle action
	// private Insets prefInsets = new Insets(10, 5, 10, 5);
	private Scene scene;
	private Group root;

	private GridPane layoutGrid;
	private BorderPane layoutListEmbedding;

	private ListEmbedding tableEmbeddings;

	public LayoutEmbeddings() {

		root = new Group();
		scene = new Scene(root, 0, 0, Color.WHITE);

		// Link to the style sheet file (stylesheet)
		scene.getStylesheets().add(Constants.JAVAFX_STYLE_FILE);

		layoutGrid = new GridPane();

		layoutListEmbedding = new BorderPane();

		layoutGrid.add(addToolBar(), 0, 0);
		layoutGrid.add(layoutListEmbedding, 0, 1);

		// BIND to take available space
		// layoutGrid.setMinHeight(Application.SCREEN_HEIGHT);
		// layoutGrid.setMaxHeight(Application.SCREEN_HEIGHT);
		// layoutGrid.setPrefHeight(Application.SCREEN_HEIGHT);

		layoutGrid.prefHeightProperty().bind(scene.heightProperty());
		layoutGrid.prefWidthProperty().bind(scene.widthProperty());

		root.getChildren().add(layoutGrid);
	}

	private ToolBar addToolBar() {
		ToolBar toolBar = new ToolBar();
		toolBar.getStyleClass().add("toolBarLayout");

		toolBar.setMaxHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setMinHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setPrefHeight(Constants.MAIN_TOOLBAR_HEIGHT);

		Label lblTitle = new Label(Constants.LAYOUT_EMBEDDINGS_VIEW_TITLE);
		lblTitle.getStyleClass().add("toolBarLayoutTitle");
		toolBar.getItems().addAll(lblTitle);

		toolBar.prefHeightProperty().bind(scene.heightProperty());
		toolBar.prefWidthProperty().bind(scene.widthProperty());

		return toolBar;
	}

	private HBox addHBoxSelectedNodes(final int[] ids, final String[] labels) {
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(1, 5, 1, 5));
		hbox.getStyleClass().add("backgroundMenu");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		// grid.setGridLinesVisible(true);

		grid.getColumnConstraints().add(new ColumnConstraints(Constants.LIST_EMBEDDING_BTN_WIDTH_COLUMNA1));
		grid.getColumnConstraints().add(new ColumnConstraints(Constants.LIST_EMBEDDING_BTN_WIDTH_COLUMNA2));

		grid.getRowConstraints().add(new RowConstraints(Constants.LIST_EMBEDDING_BTN_HEIGHT_ROW));

		Button btnListEmbedding = ComponentCreator.makeButton("List Embeddings", "", ContentDisplay.TEXT_ONLY,
				Constants.LIST_EMBEDDING_BTN_WIDTH_COLUMNA2, Constants.LIST_EMBEDDING_BTN_HEIGHT, 0, 0, this.getClass(),
				"List embeddings of selected authors");
		btnListEmbedding.getStyleClass().add("btnMenu");
		btnListEmbedding.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				// HAY QUE BLOQUEAR PROCESSING APP
				processingApp.embeddingByNodes(ids, labels);
			}
		});

		ListView<String> labelOfAuthors = new ListView<String>();
		labelOfAuthors.getStyleClass().add("listLabelOfAuthors");
		// labelOfAuthors.setOrientation(Orientation.HORIZONTAL);
		for (String label : labels) {
			labelOfAuthors.getItems().add(label);
		}

		grid.add(labelOfAuthors, 0, 0);
		grid.add(btnListEmbedding, 1, 0);

		hbox.getChildren().add(grid);
		return hbox;
	}

	private Canvas createLeyendaListEmbedding() {
		int colorFromLerp = ColorShape.getHSB_FromLerp();
		int colorToLerp = ColorShape.getHSB_ToLerp();
		int lenghtR = (int)Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH - 30;
		int heightR = 40;
		int padding = 50;
		int diametreOval = 25;
		int numRanges = (lenghtR - (padding * 2)) / diametreOval;

		int xStart = padding;
		int yStart = heightR / 2;

		Canvas canvas = new Canvas(lenghtR, heightR);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		// Title
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFill(Color.BLACK);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Constants.LIST_EMBEDDING_LEGENDE_TITLE_LABEL_FONT);
		gc.fillText("Number of Occurrences", lenghtR / 2, 5);
		gc.translate(0, 7); // to move vers

		for (int i = 0; i < numRanges; i++) {
			float colorLerp = PApplet.map(i, 0, numRanges, 0, 1);
			int interA = processingApp.lerpColor(colorFromLerp, colorToLerp, colorLerp);
			gc.setFill(ColorShape.parserColorProcessingToJavafx(interA));
			gc.fillOval(xStart + i * diametreOval, yStart - (diametreOval / 2), diametreOval, diametreOval);
		}

		// Map<String, Double> thresholdSizeNode =
		// processingApp.graph.getThresholdRadiusNodePattern();
		//
		Map<String, Double> result = new HashMap<String, Double>();
		double min = (double) PConstants.MAX_FLOAT;
		double max = (double) PConstants.MIN_FLOAT;
		//TODO
		//eliminado getListEmbeddingsNodeNumAppears
//		for (int i = 0; i < processingApp.getGraph().getListEmbeddingsNodeNumAppears().size(); i++) {
//			int numeroValue = (int) processingApp.getGraph().getListEmbeddingsNodeNumAppears().get(i);
//			if (numeroValue > max)
//				max = numeroValue;
//			if (numeroValue < min)
//				min = numeroValue;
//		}
		//
		result.put("sizeMin", min);
		result.put("sizeMax", max);
		//

		// Minimum value color coding
		gc.setTextAlign(TextAlignment.LEFT);
		gc.setFill(Color.BLACK);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Constants.LIST_EMBEDDING_LEGENDE_SUBTITLE_LABEL_FONT);
		gc.fillText(Constants.DECIMAL_FORMAT.format(min), (padding / 2), heightR / 2);

		// Maximum value color coding
		gc.setTextAlign(TextAlignment.RIGHT);
		gc.setFill(Color.BLACK);
		gc.setTextBaseline(VPos.CENTER);
		gc.setFont(Constants.LIST_EMBEDDING_LEGENDE_SUBTITLE_LABEL_FONT);
		gc.fillText(Constants.DECIMAL_FORMAT.format(max), lenghtR - (padding / 2), heightR / 2);
		// gc.fillText(Constants.DECIMAL_FORMAT.format(thresholdSizeNode.get("sizeMax")),
		// lenghtR - (padding / 2),
		// heightR / 2);

		return canvas;
	}

	private HBox addBottomBox() {
		HBox hbox = new HBox();
		// hbox.setPadding(prefInsets);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(1, 5, 1, 5));
		hbox.setSpacing(10);
		hbox.getStyleClass().add("backgroundMenu");

		// Btn visualize kelps
		Button btnVisualizeKelps = ComponentCreator.makeButton("VISUALIZE", "", ContentDisplay.TEXT_ONLY,
				Constants.LIST_EMBEDDING_BTN_VISUALIZE_WIDTH, Constants.LIST_EMBEDDING_BTN_HEIGHT, 0, 0,
				this.getClass(), "Visualize Kelp-like Diagrams of selected embeddings");
		btnVisualizeKelps.getStyleClass().add("btnMenu");
		btnVisualizeKelps.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				processingApp.setAppBlocked(false);
				if (tableEmbeddings.getListSelectedEmbeddingsItem().size() > 0) {
					processingApp.createKelpDiagrams(tableEmbeddings.getListSelectedEmbeddingsItem());
				}
			}
		});

		// Btn clear kelps
		Button btnClearKelps = ComponentCreator.makeButton("Clear Selection", "", ContentDisplay.TEXT_ONLY,
				Constants.LIST_EMBEDDING_BTN_CLEAR_WIDTH, Constants.LIST_EMBEDDING_BTN_HEIGHT, 0, 0, this.getClass(),
				"Clear Kelp-like Diagrams");
		btnClearKelps.getStyleClass().add("btnMenu");
		btnClearKelps.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				tableEmbeddings.clearListSelectedEmbeddingItems();
				processingApp.setAppBlocked(false);
				processingApp.getGraph().setListNodePatternVisible(true);
				processingApp.clearKelpDiagrams();
			}
		});

		hbox.getChildren().addAll(btnClearKelps, btnVisualizeKelps);

		return hbox;
	}

	public void updateTabListEmbedding(final List<Graph> listFusionEmbeddings) {
		Platform.runLater(new Runnable() {
			public void run() {
				VBox vBox = new VBox();
				vBox.setPadding(new Insets(10, 5, 5, 5));
				vBox.setSpacing(10);
				vBox.setFillWidth(true);

				tableEmbeddings = new ListEmbedding(listFusionEmbeddings);
				VBox.setVgrow(tableEmbeddings, Priority.ALWAYS);

				//vBox.getChildren().addAll(createLeyendaListEmbedding(), tableEmbeddings);
				vBox.getChildren().addAll(tableEmbeddings);
				vBox.prefHeightProperty().bind(scene.heightProperty());
				vBox.prefWidthProperty().bind(scene.widthProperty());

				layoutListEmbedding.setCenter(vBox);
				layoutListEmbedding.setBottom(addBottomBox());
			}
		});
	}

	public void updateSelectedVertex(final int[] ids, final String[] labels) {
		Platform.runLater(new Runnable() {
			public void run() {
				HBox hbox = addHBoxSelectedNodes(ids, labels);
				layoutListEmbedding.setTop(hbox);
				layoutListEmbedding.setCenter(null);
				layoutListEmbedding.setBottom(null);
			}
		});
	}

	public void clearListEmbeddings() {
		Platform.runLater(new Runnable() {
			public void run() {
				layoutListEmbedding.setTop(null);
				layoutListEmbedding.setCenter(null);
				layoutListEmbedding.setBottom(null);
			}
		});
	}

	public Scene getScene() {
		return scene;
	}

}
