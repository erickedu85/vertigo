package app.gui.query;

import app.gui.main.Constants;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class SceneGestures {

	private static final double MAX_SCALE = 10.0d;
	private static final double MIN_SCALE = .1d;

	private DragContext sceneDragContext = new DragContext();
	private PannableCanvas canvas;

	public SceneGestures(PannableCanvas canvas) {
		this.canvas = canvas;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {

			// right mouse button => panning
			if (!event.isSecondaryButtonDown())
				return;

			sceneDragContext.mouseAnchorX = event.getSceneX();
			sceneDragContext.mouseAnchorY = event.getSceneY();

			sceneDragContext.translateAnchorX = canvas.getTranslateX();
			sceneDragContext.translateAnchorY = canvas.getTranslateY();

		}

	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			// right mouse button => panning
			if (!event.isSecondaryButtonDown())
				return;

			canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
			canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

			event.consume();
		}
	};

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

		public void handle(ScrollEvent event) {

			double delta = 1.2;

			double scale = canvas.getScale(); // currently we only use Y, same
												// value is used for X

			double oldScale = scale;

			if (event.getDeltaY() < 0)
				scale /= delta;
			else
				scale *= delta;

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);
			double f = (scale / oldScale) - 1;

			double dx = (event.getSceneX()
					- (canvas.getBoundsInParent().getWidth() / 2 + canvas.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY()
					- (canvas.getBoundsInParent().getHeight() / 2 + canvas.getBoundsInParent().getMinY()));

			// double dx = (event.getSceneX()
			// - (Constants.VISUAL_CANVAS_WIDTH / 2 + Constants.VISUAL_GRAPH_QUERY_BTN_HEIGHT));
			// double dy = (event.getSceneY()
			// - (Constants.VISUAL_CANVAS_HEIGHT / 2 + Constants.VISUAL_GRAPH_QUERY_BTN_WIDTH));

			canvas.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			canvas.setPivotCustom(f * dx, f * dy);

			event.consume();

		}

	};

	public static double clamp(double value, double min, double max) {

		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}

}

/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {

	double mouseAnchorX;
	double mouseAnchorY;

	double translateAnchorX;
	double translateAnchorY;

}
