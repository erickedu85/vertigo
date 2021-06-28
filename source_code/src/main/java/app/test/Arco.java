package app.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.stage.Stage;

public class Arco extends Application {
	  public static void main(String[] args) {
		    Application.launch(args);
		  }

		  @Override
		  public void start(Stage stage) {
		    ArcTo arcTo = new ArcTo();
		    arcTo.setX(20);
		    arcTo.setY(30);
		    
		    arcTo.setRadiusX(30);
		    arcTo.setRadiusY(30);
		    
		    Path path = new Path(new MoveTo(0, 0),
		                         new VLineTo(100), 
		                         new HLineTo(100), 
		                         new VLineTo(50),
		                         arcTo);
		    HBox box = new HBox(path);
		 
		    box.setSpacing(10);    
		    
		    Scene scene = new Scene(box);
		    stage.setScene(scene);
		    stage.setTitle("Test");
		    stage.show();
		  }
		}
