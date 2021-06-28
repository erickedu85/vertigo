package app.gui.ghost;

import java.util.List;

import org.apache.log4j.Logger;

import app.gui.main.Constants;
import app.gui.query.GuiQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PieGhost extends PieChart {

	public final static Logger logger = Logger.getLogger(PieGhost.class);
	private final Glow glow = new Glow(0.6);
	private Label caption = new Label("");
	public static GuiQuery designQuery; // handle action
	private int idNodeParent;
	private int sumEdgeTypeValues;
	private List<GhostSliceItem> listSliceGhost;

	public PieGhost(List<GhostSliceItem> listSliceGhost, Label caption) {
		this.caption = caption;
		this.idNodeParent = listSliceGhost.get(0).getIdNodeParent();
		this.listSliceGhost = listSliceGhost;
		init();
	}

	private void init() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

		for (GhostSliceItem ghostSliceItem : listSliceGhost) {
			pieChartData
					.add(new PieChart.Data(ghostSliceItem.getLayerType().getLabel(), ghostSliceItem.getNumAppears()));

			sumEdgeTypeValues = sumEdgeTypeValues + ghostSliceItem.getNumAppears();
		}

		// Setting the angle accumulate
		double angleAccumulate = 0;
		for (final PieChart.Data data : pieChartData) {
			final GhostSliceItem sliceItem = getGhostSliceItemByName(data.getName());

			double angle = (sliceItem.getNumAppears() * 360 / sumEdgeTypeValues);
			double angleMiddle = angle / 2;
			sliceItem.setAngleAccumulate(angleAccumulate + angleMiddle);
			angleAccumulate = angle + angleAccumulate;

		}

		setLabelsVisible(false);
		setClockwise(false);
		setStartAngle(Constants.VISUAL_GRAPH_QUERY_PIE_GHOST_ANGLE_START);
		setData(pieChartData);
		setupMouseBehavior(pieChartData);
	}

	private void setupMouseBehavior(final ObservableList<PieChart.Data> pieChartData) {
		for (final PieChart.Data data : pieChartData) {

			final Node n = data.getNode();

			
			
			final GhostSliceItem sliceItem = getGhostSliceItemByName(data.getName());

			n.setEffect(null);
			n.setStyle("-fx-pie-color: " + sliceItem.getLayerType().getCssColorStyle() + ";");

			// MouseEnter
			n.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					double dataPercentage = (double) (data.getPieValue() * 100 / sumEdgeTypeValues);
					String txtToolTip = data.getName() + " " + Constants.DECIMAL_FORMAT.format(dataPercentage) + "%";
					Tooltip toolTip = new Tooltip(txtToolTip);
					Tooltip.install(n, toolTip);
					caption.setText(txtToolTip);
					//designQuery.displayTooltipPieGhost(e.getScreenX(), e.getScreenY() - 160);
					n.setEffect(glow);
				}
			});

			// MouseExited
			n.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					n.setEffect(null);
					caption.setText("");
				}
			});

			// Mouse Clicked
			n.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.PRIMARY) {
						designQuery.addExternalGhostNodeEdge(sliceItem.getAngleAccumulate(), idNodeParent,
								sliceItem.getLayerType());
					}

				}
			});
		}

	}

	private GhostSliceItem getGhostSliceItemByName(String dataName) {
		for (GhostSliceItem sliceItem : listSliceGhost) {
			if (dataName == sliceItem.getLayerType().getLabel()) {
				return sliceItem;
			}
		}
		return null;
	}

}
