package app.test;

import app.graph.structure.PositionShape;
import app.utils.GeoAnalytic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class TestPieChart extends Application {

	PieChart pieChart = new PieChart();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// PieChart pieChart = new PieChart();
		pieChart.setData(getChartData());
		primaryStage.setTitle("PieChart");

		final Label caption = new Label("");
		caption.setTextFill(Color.BLACK);
		caption.setStyle("-fx-font: 24 arial;");

		final double width = 500;
		final double height = 500;
		final double hypotenuse = 100;

		for (final PieChart.Data data : pieChart.getData()) {

			data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					// System.out.println("aqui " + e);

					double boundsMinX = data.getNode().getBoundsInParent().getMinX();
					double boundsMaxX = data.getNode().getBoundsInParent().getMaxX();
					double boundsMinY = data.getNode().getBoundsInParent().getMinY();
					double boundsMaxY = data.getNode().getBoundsInParent().getMaxY();

					double angulo = (data.getPieValue() * 360) / 100;
					System.out.println("angle: " + angulo);

					PositionShape p = GeoAnalytic.getCoordinatesByAngle(new PositionShape(width / 2, height / 2),
							angulo, hypotenuse);

					// System.out.println(" boundsMin " + boundsMinX + ", " +
					// boundsMinY);
					// System.out.println(" boundsMax " + boundsMaxX + ", " +
					// boundsMaxY);
					System.out.println(p);

					caption.setTranslateX(e.getSceneX());
					caption.setTranslateY(e.getSceneY());
					caption.setText(String.valueOf("hoho") + "%");
				}
			});
		}

		pieChart.setMinWidth(width);
		pieChart.setMaxWidth(width);
		pieChart.setPrefWidth(width);
		pieChart.setMinHeight(height);
		pieChart.setMaxHeight(height);
		pieChart.setPrefHeight(height);
		pieChart.setPadding(new Insets(0, 0, 0, 0));
		pieChart.setLegendVisible(false);
		pieChart.setLabelsVisible(false);
		// pieChart.setLabelLineLength(50);
		// pieChart.setStartAngle(0);
		pieChart.setClockwise(false);

		// pieChart.getLayoutBounds().
		// pieChart.setLegendSide(Side.LEFT);

		StackPane root = new StackPane();
		root.getChildren().add(pieChart);
		Scene scene = new Scene(root, width, height);
		primaryStage.setScene(scene);

		System.out.println("width: " + pieChart.getWidth() + " height: " + pieChart.getHeight());

		scene.addEventFilter(ScrollEvent.ANY, onScrollEventHandler);

		primaryStage.show();

	}

	private ObservableList<Data> getChartData() {
		ObservableList<Data> answer = FXCollections.observableArrayList();
		answer.addAll(new PieChart.Data("JavaFx", 20), new PieChart.Data("JavaFx", 20), new PieChart.Data("JavaFx", 10),
				new PieChart.Data("java", 50));
		return answer;
	}

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

		public void handle(ScrollEvent event) {

			double delta = 1.2;
			double scale = pieChart.getScaleX();

			if (event.getDeltaY() < 0)
				scale /= delta;
			else
				scale *= delta;

			// pieChart.setScaleShape(true);
			// Creating the scale transformation
			Scale scalador = new Scale();
			// Setting the dimensions for the transformation
			scalador.setX(scale);
			scalador.setY(scale);

			// Setting the pivot point for the transformation
			scalador.setPivotX(event.getSceneX());
			scalador.setPivotY(event.getSceneY());

			pieChart.getTransforms().addAll(scalador);
			// pieChart.setScaleX(scale);
			// pieChart.setScaleY(scale);

		}

	};
}
