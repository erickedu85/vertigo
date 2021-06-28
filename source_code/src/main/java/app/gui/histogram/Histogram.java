package app.gui.histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.Stroke;
import app.gui.database.GuiGraphDB;
import app.gui.main.Constants;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Histogram extends BarChart<String, Number> {

	public static final Logger logger = Logger.getLogger(Histogram.class);
	public static GuiGraphDB processingApp; // handle action

	private final Glow glow = new Glow(0.3);
	private List<HistogramItem> listHistogram;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Histogram(Axis<String> xAxis, Axis<Number> yAxis, List<Graph> listGraphHistogram, int numberOfCategories) {
		super(xAxis, yAxis);
		if (listGraphHistogram == null) {
			return;
		}

		double[][] categories = new double[numberOfCategories][3];
		// Sort ascending by mbr
		Collections.sort(listGraphHistogram, Constants.GRAPH_MBR_COMPARATOR);

		// Filling the listMbrs with the MBR's values of listGraphHistogram
		double[] listMbrs = new double[listGraphHistogram.size()];
		for (int g = 0; g < listGraphHistogram.size(); g++) {
			listMbrs[g] = listGraphHistogram.get(g).getMbr();
		}
		// Getting the value of an interval, the listGraphHistogram must be
		// already sorted
		double pivot = Math.ceil(listMbrs[listMbrs.length - 1]) - Math.floor(listMbrs[0]);
		double valueOfInterval = pivot / numberOfCategories;

		// Creation of categories [row][column]
		for (int row = 0; row < numberOfCategories; row++) {
			for (int column = 0; column < 2; column++) {
				if (row == 0 && column == 0) {
					categories[row][column] = Math.floor(listMbrs[0]);
				} else if (column == 1) {
					categories[row][column] = categories[row][0] + valueOfInterval;
				} else {
					categories[row][column] = categories[row - 1][1];
				}
			}
		}

		// Creation a list of Histogram
		listHistogram = new ArrayList<HistogramItem>();
		for (int n = 0; n < numberOfCategories; n++) {
			Fill fill = new Fill(true, 0, 100);
			Stroke stroke = new Stroke(true, 0, 100, 1);
			double intervalBegin = categories[n][0];
			double intervalEnd = categories[n][1];
			boolean isVisible = true;
			boolean isSelected = true;
			HistogramItem histogram = new HistogramItem(n, new Etiqueta("Histogram " + n, false), 0, null, stroke, fill,
					isVisible, new ArrayList<Graph>(), 0, 0, intervalBegin, intervalEnd, isSelected);
			// Placing the graphs in the histogram according to their MBR value
			for (Graph g : listGraphHistogram) {
				if (g.getMbr() >= histogram.getIntervalBegin() && g.getMbr() < histogram.getIntervalEnd()) {
					histogram.getListGraph().add(g);
				}
			}
			// setting the height as the number of list graph
			histogram.setHeight(histogram.getListGraph().size());
			// adding the histogram to list of histogram
			listHistogram.add(histogram);
		}

		setId("histogramGraphic");
		setCategoryGap(0);
		setBarGap(0);
		setLegendVisible(false);
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarkVisible(false);

		setTitle(Constants.HISTOGRAM_TITLE);
		xAxis.setLabel(Constants.HISTOGRAM_X_LABEL);
		yAxis.setLabel(Constants.HISTOGRAM_Y_LABEL);
		XYChart.Series series1 = new XYChart.Series();

		for (HistogramItem histogramItem : listHistogram) {
			Double intervalBegin = histogramItem.getIntervalBegin();
			String xValue = String.valueOf(intervalBegin.intValue());
			int yValue = histogramItem.getListGraph().size();
			series1.getData().add(new XYChart.Data(xValue, yValue, histogramItem));
		}

		getData().addAll(series1);
		setupMouseBehavior(series1);
	}

	private void setupMouseBehavior(XYChart.Series<String, Number> series) {
		for (final XYChart.Data<String, Number> dt : series.getData()) {
			final Node n = dt.getNode();

			n.setEffect(null);
			HistogramItem item = (HistogramItem) dt.getExtraValue();
			if (item.isSelected()) {
				n.setStyle(Constants.HISTOGRAM_ITEM_STYLE_SELECTED);
			} else {
				n.setStyle(Constants.HISTOGRAM_ITEM_STYLE_DESELECTED);
			}

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
						HistogramItem item = (HistogramItem) dt.getExtraValue();
						item.setSelected(!item.isSelected());
						dt.setExtraValue(item);
						if (item.isSelected()) {
							n.setStyle(Constants.HISTOGRAM_ITEM_STYLE_SELECTED);
						} else {
							n.setStyle(Constants.HISTOGRAM_ITEM_STYLE_DESELECTED);
						}
						processingApp.filterEmbeddingsByHistogram(listHistogram);
					}
				}
			});
		}
	}

}
