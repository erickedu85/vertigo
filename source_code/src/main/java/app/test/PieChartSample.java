package app.test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.Node;

public class PieChartSample extends Application {

	private final Glow glow = new Glow(0.3);

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new Group());
		stage.setTitle("Imported Fruits");
		stage.setWidth(500);
		stage.setHeight(500);

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Grapefruit", 43), new PieChart.Data("Oranges", 25), new PieChart.Data("Plums", 10),
				new PieChart.Data("Pears", 42), new PieChart.Data("Apples", 40));

		final PieChart chart = new PieChart(pieChartData);
		chart.setTitle("Imported Fruits");

		final Label caption = new Label("");
		caption.setTextFill(Color.DARKORANGE);
		caption.setStyle("-fx-font: 24 arial;");

		for (final PieChart.Data data : chart.getData()) {
			final Node n = data.getNode();
			n.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					// System.out.println("data: " + data);
					caption.setTranslateX(245);
					caption.setTranslateY(168.5);
					caption.setText(String.valueOf(data.getPieValue()) + "%");
				}
			});

			n.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					n.setEffect(glow);
				}
			});
		}

		((Group) scene.getRoot()).getChildren().addAll(chart, caption);
		stage.setScene(scene);
		stage.show();

		final double a = chart.getData().get(3).getNode().getLayoutX();
		final double b = chart.getData().get(3).getNode().getLayoutY();
		System.out.println(a + ", " + b);
	}

	public static void main(String[] args) {
		launch(args);
	}
}