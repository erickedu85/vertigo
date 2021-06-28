package app.gui.query;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

public class PannableCanvas extends Pane {

	DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	boolean isDraggable;

	public PannableCanvas() {
		// setPrefSize(1200, 900);
		// setStyle("-fx-background-color: lightgrey; -fx-border-color: blue;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);
	}

	public double getScale() {
		return myScale.get();
	}

	public void setScale(double scale) {
		myScale.set(scale);
	}

	public void setPivotCustom(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}
}
