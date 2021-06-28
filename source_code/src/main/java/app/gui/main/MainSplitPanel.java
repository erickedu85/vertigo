package app.gui.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import app.gui.database.GraphDBToolBar;
import app.gui.database.GuiGraphDB;
import app.gui.embedding.LayoutEmbeddings;
import app.gui.histogram.Histogram;
import app.gui.preferences.Preferences;
import app.gui.query.GuiQuery;
import app.gui.query.LayoutGraphQuery;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sumgra.data.GraphDatabase;

public class MainSplitPanel extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private static GuiGraphDB processingApp;
//	private static TestMouseProcessing processingApp;
//	private static GraphDatabase graphDatabase;

	private JPanel panelGraphDBView;

	private static Stage startPreferences;

	public MainSplitPanel() throws MalformedURLException, IOException {

		// ------- MAIN's Panels -------
		//
		JPanel panelQueryView = new JPanel(new BorderLayout());
		JSplitPane splitPanelCentral = new JSplitPane();
		panelGraphDBView = new JPanel(new BorderLayout());
		JPanel panelEmbeddings = new JPanel(new BorderLayout());

		// ------- QUERY VIEW -------
		//
		final JFXPanel queryView = new JFXPanel();
		panelQueryView.add(queryView);

		// ------- GRAPH DB VIEW -------
		//
		processingApp = new GuiGraphDB(); //TestMouseProcessing();
		GuiQuery.processingApp = processingApp;
		Histogram.processingApp = processingApp;
		Preferences.processingApp = processingApp;
		GraphDBToolBar.processingApp = processingApp;
		GraphDBToolBar.parent = this;
		LayoutEmbeddings.processingApp = processingApp;
		LayoutGraphQuery.processingApp = processingApp;

		final JFXPanel toolBarGraphDBView = new JFXPanel();
		panelGraphDBView.add(processingApp, BorderLayout.CENTER);
		panelGraphDBView.add(toolBarGraphDBView, BorderLayout.PAGE_START);

		//PARA CARGAR AL INICIO DE LA APP, FUNCIONA CON POCOS DATOS
		// CON PANAMA PAPER OVER MEMORY
//		String sumgraGraphPath = Constants.PATH_DATA.concat(Constants.SUMGRA_GRAPH_FILE);
//		graphDatabase = new GraphDatabase(sumgraGraphPath);
		
		// ------- EMBEDDINGS VIEW -------
		//
		final JFXPanel embeddingsView = new JFXPanel();
		panelEmbeddings.add(embeddingsView, BorderLayout.CENTER);

		//
		splitPanelCentral.setOneTouchExpandable(false); // Split buttons
		splitPanelCentral.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPanelCentral.setContinuousLayout(false);
		splitPanelCentral.setRightComponent(panelEmbeddings);
		splitPanelCentral.setLeftComponent(panelGraphDBView);

		final Dimension dimensionPanelGraphDB = new Dimension((int)
				(Application.SCREEN_WIDTH - Application.LAYOUT_QUERY_VIEW_WIDTH - Application.LAYOUT_EMBEDDINGS_VIEW_WIDTH),
				(int)Application.SCREEN_HEIGHT);
		splitPanelCentral.getLeftComponent().setMaximumSize(dimensionPanelGraphDB);
		splitPanelCentral.getLeftComponent().setMinimumSize(dimensionPanelGraphDB);
		splitPanelCentral.getLeftComponent().setPreferredSize(dimensionPanelGraphDB);

		// final Dimension dimensionPanelEmbeddings = new
		// Dimension(Constants.LAYOUT_EMBEDDINGS_VIEW_WIDTH,
		// Application.SCREEN_HEIGHT);
		// splitPanelCentral.getRightComponent().setMaximumSize(dimensionPanelEmbeddings);
		// splitPanelCentral.getRightComponent().setMinimumSize(dimensionPanelEmbeddings);
		// splitPanelCentral.getRightComponent().setPreferredSize(dimensionPanelEmbeddings);

		//
		//
		//
		setOneTouchExpandable(false); // Split buttons
		setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		setContinuousLayout(false);
		setLeftComponent(panelQueryView);
		setRightComponent(splitPanelCentral);

		final Dimension sizePanelLeft = new Dimension((int)Application.LAYOUT_QUERY_VIEW_WIDTH, (int)Application.SCREEN_HEIGHT);
		panelQueryView.setMinimumSize(sizePanelLeft);
		panelQueryView.setMaximumSize(sizePanelLeft);
		panelQueryView.setPreferredSize(sizePanelLeft);

		// PropertyChangeListener propertyChangeListener = new
		// PropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent changeEvent) {
		// JSplitPane sourceSplitPane = (JSplitPane) changeEvent.getSource();
		// String propertyName = changeEvent.getPropertyName();
		// if (propertyName.equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
		// //
		// int current = sourceSplitPane.getDividerLocation();
		// Integer last = (Integer) changeEvent.getNewValue();
		// Integer priorLast = (Integer) changeEvent.getOldValue();
		// // System.out.println("Prior last: " + priorLast);
		//
		// if (last > Constants.LEFT_PANEL_WIDTH + 1) {
		// getLeftComponent().setSize(sizePanelLeft);
		// getRightComponent().setSize(sizePanelRight);
		// resetToPreferredSizes();
		// // setDividerLocation(0);
		// }
		// }
		// }
		// };
		// addPropertyChangeListener(propertyChangeListener);

		processingApp.init();

		Platform.runLater(new Runnable() {
			public void run() {
				initLayoutGraphQuery(queryView);
			}
		});

		Platform.runLater(new Runnable() {
			public void run() {
				initLayoutGraphEmbedding(embeddingsView);
			}
		});

		Platform.runLater(new Runnable() {
			public void run() {
				initGraphDBToolBar(toolBarGraphDBView);
			}
		});

		Platform.runLater(new Runnable() {
			public void run() {
				initPreferences(toolBarGraphDBView);
			}
		});

	}

	// GRAPH QUERY VIEW JAVAFX
	private static void initLayoutGraphQuery(JFXPanel fxPanel) {
		// This method is invoked on the JavaFX thread
		LayoutGraphQuery scene = new LayoutGraphQuery();
		fxPanel.setScene(scene.getScene());
		GuiGraphDB.fxLeftPanel = scene;
	}

	// GRAPH EMBEDDING VIEW JAVAFX
	private static void initLayoutGraphEmbedding(JFXPanel fxPanel) {
		// This method is invoked on the JavaFX thread
		LayoutEmbeddings scene = new LayoutEmbeddings();
		fxPanel.setScene(scene.getScene());
		GuiGraphDB.fxRightPanel = scene;
	}

	// TOOLBAR GRAPH DATABASE
	private static void initGraphDBToolBar(JFXPanel fxPanel) {
		GraphDBToolBar scene = new GraphDBToolBar();
		fxPanel.setScene(scene.getScene());
	}

	private static void initPreferences(JFXPanel fxPanel) {
		Preferences scene = new Preferences();
		startPreferences = new Stage();
		startPreferences.setX(0);
		startPreferences.setY(55);
//		startPreferences.setX(Application.SCREEN_WIDTH/2);
//		startPreferences.setY(Application.SCREEN_HEIGHT/2);
		startPreferences.initStyle(StageStyle.UNDECORATED);
		startPreferences.setScene(scene.getScene());
		startPreferences.hide();
	}

	public JPanel getPanelGraphDBView() {
		return panelGraphDBView;
	}

	public void setPanelGraphDBView(JPanel panelGraphDBView) {
		this.panelGraphDBView = panelGraphDBView;
	}

	public Stage getStartPage() {
		return startPreferences;
	}

	public static void setStartPage(Stage startPage) {
		MainSplitPanel.startPreferences = startPage;
	}

}
