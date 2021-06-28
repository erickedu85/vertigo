package app.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ColorPickerDemo extends Application {

	@Override
	public void start(Stage stage) {

		final ColorPicker colorPicker = new ColorPicker();
		colorPicker.setValue(Color.RED);

		final Circle circle = new Circle(50);
		circle.setFill(colorPicker.getValue());

		colorPicker.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				System.out.println("aqui");
				circle.setFill(colorPicker.getValue());
			}
		});

		FlowPane root = new FlowPane();
		root.setPadding(new Insets(10));
		root.setHgap(10);
		root.getChildren().addAll(circle, colorPicker);

		Scene scene = new Scene(root, 400, 300);

		stage.setTitle("JavaFX ColorPicker (o7planning.org)");

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}