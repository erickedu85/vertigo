package app.gui.database;

import java.io.File;

import org.apache.log4j.Logger;

import app.gui.main.Application;
import app.gui.main.Constants;
import app.gui.main.MainSplitPanel;
import app.gui.query.ComponentCreator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class GraphDBToolBar {

	public final static Logger logger = Logger.getLogger(GraphDBToolBar.class);
	public static GuiGraphDB processingApp; // handle action
	public static MainSplitPanel parent; // handle action

	public static ComboBox<String> cmbLabelNodesPatterns = new ComboBox<String>();
	private Scene scene;

	public GraphDBToolBar() {

		Group root = new Group();
		scene = new Scene(root, 900, Constants.MAIN_TOOLBAR_HEIGHT, Color.WHITE);
		scene.getStylesheets().add(Constants.JAVAFX_STYLE_FILE);

		BorderPane borderPane = new BorderPane();
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());
		borderPane.setCenter(addToolBar());

		root.getChildren().add(borderPane);
	}

	private ToolBar addToolBar() {

		final ToolBar toolBar = new ToolBar();
		toolBar.setMaxHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setMinHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.setPrefHeight(Constants.MAIN_TOOLBAR_HEIGHT);
		toolBar.getStyleClass().add("toolBarLayout");

		int translateX = 0;
		int translateY = 0;

		Label lblTitle = new Label(Constants.LAYOUT_GRAPH_DB_VIEW_TITLE);
		lblTitle.getStyleClass().add("toolBarLayoutTitle");

		Button btnScreenshot = ComponentCreator.makeButton("Save screenshot", "screenshot.png",
				ContentDisplay.GRAPHIC_ONLY, Constants.VISUAL_GRAPH_DB_BTN_WIDTH, Constants.VISUAL_GRAPH_DB_BTN_HEIGHT,
				translateX, translateY, this.getClass(), "Save a screenshot");
		btnScreenshot.getStyleClass().add("btnMenuMain");

		btnScreenshot.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				String[] allowedExtension = { "png", "jpg", "tif", "tga" };
				FileChooser fileChooser = ComponentCreator.makeFileChooser(allowedExtension);
				File file = fileChooser.showSaveDialog(null);
				if (file != null) {
					processingApp.saveScreenshot(file);
				}
			}
		});

		Button btnPreferences = ComponentCreator.makeButton("Preferences", "preferences.png",
				ContentDisplay.GRAPHIC_ONLY, Constants.VISUAL_GRAPH_DB_BTN_WIDTH, Constants.VISUAL_GRAPH_DB_BTN_HEIGHT,
				translateX, translateY, this.getClass(), "Control panel");
		btnPreferences.getStyleClass().add("btnMenuMain");
		btnPreferences.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				parent.getStartPage().sizeToScene();
				if (parent.getStartPage().isShowing()) {
					parent.getStartPage().hide();
				} else {
					parent.getStartPage().show();
				}
			}
		});

//		cmbLabelNodesPatterns = new ComboBox<>();
		cmbLabelNodesPatterns.setEditable(true);
		cmbLabelNodesPatterns.setVisible(true);
		cmbLabelNodesPatterns.setPrefHeight(Constants.VISUAL_GRAPH_DB_CMBLABELNODES_HEIGHT);
		cmbLabelNodesPatterns.setMinWidth(Constants.VISUAL_GRAPH_DB_CMBLABELNODES_WIDTH);
		cmbLabelNodesPatterns.setPromptText("Search");

		cmbLabelNodesPatterns.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try {
					int index = cmbLabelNodesPatterns.getSelectionModel().getSelectedIndex();
					String value = cmbLabelNodesPatterns.getSelectionModel().getSelectedItem();
					
					processingApp.getGraph().searchNodePatternByLabel(value);
					processingApp.getGraph().calculeLabelOverlapping();
//					processingApp.verifyLabelAlgorithm();

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		final Pane spacer = new Pane();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		double space = Application.SCREEN_WIDTH - Constants.LAYOUT_QUERY_VIEW_WIDTH
				- Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH
				- Constants.VISUAL_GRAPH_DB_CMBLABELNODES_WIDTH
				- (2 * Constants.VISUAL_GRAPH_DB_BTN_WIDTH)-35;

		int spacerWidth = (int) (space-  Constants.LAYOUT_GRAPH_DB_VIEW_TITLE_WIDTH); 
		spacer.setMinSize(spacerWidth, 1);
		spacer.setMaxSize(spacerWidth, 1);

		toolBar.getItems().addAll(lblTitle, spacer, btnPreferences, btnScreenshot, new Separator(),
				cmbLabelNodesPatterns);

		return toolBar;
	}

	public Scene getScene() {
		return scene;
	}

	public ComboBox<String> getCmbLabelNodesPatterns() {
		return cmbLabelNodesPatterns;
	}
}
