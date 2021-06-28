package app.gui.ghost;

import java.util.List;

import org.apache.log4j.Logger;

import app.graph.structure.PositionShape;
import app.gui.database.GuiGraphDB;
import app.gui.histogram.HistogramItem;
import app.gui.main.Constants;
import app.gui.query.GuiQuery;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Histoghost extends BarChart<String, Number> {

	public static final Logger logger = Logger.getLogger(Histoghost.class);
	public static GuiQuery designQuery; // handle action

	private final Glow glow = new Glow(0.6);
	private List<GhostSliceItem> listHistoghost;

	public Histoghost(Axis<String> xAxis, Axis<Number> yAxis, List<GhostSliceItem> edgeTypeOfNode) {
		super(xAxis, yAxis);

		setCategoryGap(1);
		setBarGap(1);
		// setTitle("Distribution of Embeddings");
		// xAxis.setLabel("Edge types");
		// yAxis.setLabel("Apariciones");
		XYChart.Series series1 = new XYChart.Series();

		for (GhostSliceItem ghostHistoItem : edgeTypeOfNode) {
			String xValue = ghostHistoItem.getLayerType().getLabel();
			int yValue = ghostHistoItem.getNumAppears();
			series1.getData().add(new XYChart.Data(xValue, yValue, ghostHistoItem));
		}

		getData().addAll(series1);
		setupMouseBehavior(series1);
	}

	private void setupMouseBehavior(XYChart.Series<String, Number> series) {
		for (final XYChart.Data<String, Number> dt : series.getData()) {
			final Node n = dt.getNode();

			n.setEffect(null);
			GhostSliceItem item = (GhostSliceItem) dt.getExtraValue();

			n.setStyle("-fx-bar-fill: " + item.getLayerType().getCssColorStyle() + ";");
			// if (item.isSelected()) {
			// n.setStyle(Constants.HISTOGRAM_SELECTED_STYLE);
			// } else {
			// n.setStyle(Constants.HISTOGRAM_DESELECTED_STYLE);
			// }

			n.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					n.setEffect(glow);
				}
			});
			n.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					n.setEffect(null);
				}
			});
			n.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.PRIMARY) {

						GhostSliceItem item = (GhostSliceItem) dt.getExtraValue();

						// HistogramItem item = (HistogramItem)
						// dt.getExtraValue();
						// item.setSelected(!item.isSelected());
						// dt.setExtraValue(item);
						// if (item.isSelected()) {
						// n.setStyle(Constants.HISTOGRAM_SELECTED_STYLE);
						// } else {
						// n.setStyle(Constants.HISTOGRAM_DESELECTED_STYLE);
						// }
						// PositionShape mousePosition = new
						// PositionShape(e.getX(), e.getY() + 40);
						PositionShape mousePosition = new PositionShape(e.getSceneX(), e.getSceneY());
						// PositionShape mousePosition = new
						// PositionShape(e.getScreenX(), e.getScreenY());

//						designQuery.addNodeEdgeGhost(mousePosition, item.getIdNodeParent(),
//								item.getEdgeType().getIdEdgeType());
						// designQuery.addNodesGhost(mousePosition);
						// designQuery.addEdgeGhost(item.getIdNodeParent(),
						// item.getEdgeType().getIdEdgeType());
						// processingApp.updateHeatmapByHistogram(listHistogram);
					}

				}
			});
		}
	}

}
