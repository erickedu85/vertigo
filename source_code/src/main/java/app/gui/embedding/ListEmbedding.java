package app.gui.embedding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import app.graph.structure.Graph;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import app.utils.MathUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import processing.core.PApplet;

public class ListEmbedding extends TableView<EmbeddingItem> {

	public final static Logger logger = Logger.getLogger(ListEmbedding.class);
	private Map<Color, Boolean> listBorderColors;
	private List<EmbeddingItem> listSelectedEmbeddingsItem;

	/**
	 * @param listFusionEmbeddings
	 */
	public ListEmbedding(List<Graph> listFusionEmbeddings) {
		if (listFusionEmbeddings == null) {
			return;
		}

		setId("listEmbedding");

		listBorderColors = new LinkedHashMap<Color, Boolean>();
		listBorderColors.put(Color.web("#4285F4"), false); //blue google 
		listBorderColors.put(Color.web("#DB4437"), false); //red google
		listBorderColors.put(Color.web("#F4B400"), false); //orange google
		listBorderColors.put(Color.web("#0F9D58"), false); //green google
		listBorderColors.put(Color.web("#990099"), false); //purple
		listBorderColors.put(Color.web("#0099c6"), false);
		listBorderColors.put(Color.web("#dd4477"), false);

		listSelectedEmbeddingsItem = new ArrayList<EmbeddingItem>();
		ObservableList<EmbeddingItem> listEmbeddingsItem = FXCollections.observableArrayList();
		for (Graph graph : listFusionEmbeddings) {
			graph.setMbr(MathUtil.calculateMinimalBoundingRectangle(graph.getListNode()));
			double mbr = graph.getMbr();
			int numEmbedding = graph.getNumFusions();
			Canvas canvas = createGraphicsContext(graph);
			EmbeddingItem embeddingItem = new EmbeddingItem(canvas, graph, mbr, numEmbedding);
			listEmbeddingsItem.addAll(embeddingItem);
		}

		/* initialize and specify table column */
		// Column graph image
		TableColumn<EmbeddingItem, Canvas> graphImage = new TableColumn<EmbeddingItem, Canvas>("Fusion Embedding");
		graphImage.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Canvas>("canvas"));
		graphImage.setResizable(false);
		graphImage.setSortable(false);
		graphImage.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH);

		// Column MBR
		TableColumn<EmbeddingItem, Integer> minimumBoundingRec = new TableColumn<EmbeddingItem, Integer>("MBR");
		minimumBoundingRec.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Integer>("minimumBoundingRectangle"));
		minimumBoundingRec.setResizable(false);
		minimumBoundingRec.setSortable(true);
//		minimumBoundingRec.setStyle(Constants.LIST_EMBEDDING_COLUMN_CENTER_STYLE);
		minimumBoundingRec.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_MBR_WIDTH);

		// Column number of embedding abstraction
		TableColumn<EmbeddingItem, Integer> numberEmbedding = new TableColumn<EmbeddingItem, Integer>("Aggregations");
		numberEmbedding.setCellValueFactory(new PropertyValueFactory<EmbeddingItem, Integer>("numberEmbeddings"));
		numberEmbedding.setResizable(false);
		numberEmbedding.setSortable(true);
//		numberEmbedding.setStyle(Constants.LIST_EMBEDDING_COLUMN_CENTER_STYLE);
		numberEmbedding.setPrefWidth(Constants.LIST_EMBEDDING_COLUMN_AGGREGATION_WIDTH);

		/* Add columns to the tableview and set its items */
		getColumns().add(graphImage);
		getColumns().add(minimumBoundingRec);
		getColumns().add(numberEmbedding);
		setItems(listEmbeddingsItem);
		// setSelectionModel(null);
		// getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		// getSelectionModel().setCellSelectionEnabled(false);

		setRowFactory(new Callback<TableView<EmbeddingItem>, TableRow<EmbeddingItem>>() {
			public TableRow<EmbeddingItem> call(TableView<EmbeddingItem> tableView) {
				final TableRow<EmbeddingItem> row = new TableRow<EmbeddingItem>();
				row.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
							EmbeddingItem embeddingClickedRow = row.getItem();
							if (!listSelectedEmbeddingsItem.contains(embeddingClickedRow)) {
								if (listSelectedEmbeddingsItem
										.size() >= Constants.LIST_EMBEDDING_MAXIMUM_SELECTED_ITEMS)
									return;
								embeddingClickedRow.setBorderColor(getAvailableBorderColor());
								Canvas canvas = embeddingClickedRow.getCanvas();
								Color borderColor = embeddingClickedRow.getBorderColor();
								embeddingClickedRow.setCanvas(changeBorderCanvas(canvas, borderColor));
								listSelectedEmbeddingsItem.add(embeddingClickedRow);
							} else {
								setFreedomBorderColor(embeddingClickedRow.getBorderColor());
								Canvas canvas = embeddingClickedRow.getCanvas();
								Color borderColor = Color.WHITE;
								embeddingClickedRow.setCanvas(changeBorderCanvas(canvas, borderColor));
								listSelectedEmbeddingsItem.remove(embeddingClickedRow);
							}
							refresh();
						}
					}
				});
				return row;
			}
		});
	}

	/**
	 * Method to clear selected embedding item
	 */
	public void clearListSelectedEmbeddingItems() {
		for (EmbeddingItem embeddingItem : getItems()) {
			setFreedomBorderColor(embeddingItem.getBorderColor());
			Canvas canvas = embeddingItem.getCanvas();
			Color borderColor = Color.WHITE;
			embeddingItem.setCanvas(changeBorderCanvas(canvas, borderColor));
			listSelectedEmbeddingsItem.remove(embeddingItem);
		}
		getListSelectedEmbeddingsItem().clear();
	}

	/**
	 * @return The first available color
	 */
	public Color getAvailableBorderColor() {
		for (Map.Entry<Color, Boolean> entry : listBorderColors.entrySet()) {
			if (entry.getValue() == false) {
				entry.setValue(true);
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Method to make free (available) a color
	 * 
	 * @param color
	 *            Color to make available
	 */
	public void setFreedomBorderColor(Color color) {
		for (Map.Entry<Color, Boolean> entry : listBorderColors.entrySet()) {
			if (entry.getKey() == color) {
				entry.setValue(false);
				break;
			}
		}
	}

	/**
	 * @param canvas
	 *            Canvas
	 * @param borderColor
	 *            Border color
	 * @return
	 */
	private Canvas changeBorderCanvas(Canvas canvas, Color borderColor) {
		Canvas canvasWithBorder = canvas;
		GraphicsContext gc = canvasWithBorder.getGraphicsContext2D();
		double lineWidth = Constants.LIST_EMBEDDING_COLUMN_GRAPH_BORDER_SELECTED;

		gc.setStroke(borderColor);
		gc.setLineWidth(lineWidth);
		gc.strokeRect((lineWidth), lineWidth, (canvas.getWidth() - ((3 * lineWidth) / 2)),
				(canvas.getHeight() - ((3 * lineWidth) / 2)));
		return canvas;
	}

	/**
	 * Method to create a Graphics Context from a Graph g
	 * 
	 * @param g
	 *            A graph
	 * @return A graphics context of a graph G
	 */
	private Canvas createGraphicsContext(Graph g) {
		Canvas canvas = new Canvas(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH,
				Constants.LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double padding = Constants.LIST_EMBEDDING_COLUMN_GRAPH_PADDING;
		g.normalizationNodePosition(Constants.LIST_EMBEDDING_COLUMN_GRAPH_WIDTH,
				Constants.LIST_EMBEDDING_COLUMN_GRAPH_HEIGHT, padding, padding, padding, padding);
		g.setZoom(1.0d);
		for (Vertex v : g.getListNode()) {


			float radiusLerpColor = PApplet.map((float) v.getRadius(),
					(float) Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MIN,
					(float) Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MAX, (float) 0.0, (float) 1.0);
//			int colorFromLerp = ColorShape.getHSB_FromLerp();
//			int colorToLerp = ColorShape.getHSB_ToLerp();
//			int lerpColor = PApplet.lerpColor(colorFromLerp, colorToLerp, (float) radiusLerpColor, 1);
//			v.getFill().setFillColor(fillColor);
			
//			v.getFill().setFillColor(0);

//			v.setStroke(Constants.LIST_EMBEDDING_NODE_STROKE);
			v.getEtiqueta().setLabelled(true);
			v.getEtiqueta().setShowRectangle(false);
			v.setStroke(new Stroke(Constants.GRAPH_DB_NODE_PATTERN_STROKE));
			v.setShowImgBackground(true);
			v.setRadius(Constants.VISUAL_GRAPH_DB_PATTERN_NODE_RADIUS_MAX);
		}
		g.displayEdges(gc, 0, 0, false);
		g.displayNodes(gc, 0, 0, false);
		return canvas;
	}

	public List<EmbeddingItem> getListSelectedEmbeddingsItem() {
		return listSelectedEmbeddingsItem;
	}

}
