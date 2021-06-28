package app.gui.embedding;

import app.graph.structure.Graph;
import app.gui.main.Constants;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import processing.core.PApplet;

public class EmbeddingItem {
	private Canvas canvas;
	private GraphicsContext gc;
	private ImageView image;
	private Graph graph;
	private int minimumBoundingRectangle;
	private int numberEmbeddings;
	private Color borderColor;

	/**
	 * @param canvas
	 * @param graph
	 * @param minimumBoundingRectangle
	 * @param numberEmbeddings
	 */
	EmbeddingItem(Canvas canvas, Graph graph, double minimumBoundingRectangle, int numberEmbeddings) {
		this.canvas = canvas;
		this.graph = graph;
		this.minimumBoundingRectangle = Integer.parseInt(Constants.DECIMAL_FORMAT.format(minimumBoundingRectangle));
		this.numberEmbeddings = numberEmbeddings;
	}

	public void setImage(ImageView value) {
		image = value;
	}

	public ImageView getImage() {
		return image;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public int getMinimumBoundingRectangle() {
		return minimumBoundingRectangle;
	}

	public void setMinimumBoundingRectangle(int minimumBoundingRectangle) {
		this.minimumBoundingRectangle = minimumBoundingRectangle;
	}

	public int getNumberEmbeddings() {
		return numberEmbeddings;
	}

	public void setNumberEmbeddings(int numberEmbeddings) {
		this.numberEmbeddings = numberEmbeddings;
	}

	public GraphicsContext getGc() {
		return gc;
	}

	public void setGc(GraphicsContext gc) {
		this.gc = gc;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

}
