package app.test;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tabs");
		Group root = new Group();
		Scene scene = new Scene(root, 600, 250, Color.WHITE);

		final TabPane tabPane = new TabPane();

		// tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
		// @Override
		// public void onChanged(ListChangeListener.Change<? extends Tab>
		// change) {
		// if (tabPane.getTabs().isEmpty()) {
		// }
		// }
		// });

		BorderPane borderPane = new BorderPane();
		for (int i = 0; i < 3; i++) {
			final Tab tab = new Tab();
			tab.setText("Tab" + i);
			HBox hbox = new HBox();
			hbox.getChildren().add(new Label("Tab" + i));
			hbox.setAlignment(Pos.CENTER);
			tab.setContent(hbox);
//			tab.setClosable(false);

			tab.setOnClosed(new EventHandler<Event>() {
				
				public void handle(Event event) {
					tab.setDisable(false);
					
				}
			});
			
//			tab.setOnSelectionChanged(new EventHandler<Event>() {
//				@Override
//				public void handle(Event event) {
//					tabPane.setMaxSize(200, 80);
//					tabPane.setMinSize(200, 80);
//					tabPane.setPrefSize(200, 80);
//				}
//			});
			tabPane.getTabs().add(tab);
		}

//		tabPane.setMaxSize(200, 30);
//		tabPane.setMinSize(200, 30);
//		tabPane.setPrefSize(200, 30);

//		tabPane.setTranslateY(250 / 2);
//		tabPane.setRotate(90);

		// bind to take available space
		borderPane.setCenter(tabPane);
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());
		root.getChildren().add(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}