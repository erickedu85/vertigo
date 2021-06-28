package app.graph.structure;

import processing.core.PApplet;

public class Stroke {

	private boolean isStroked;
	private int strokeColor;
	private double strokeOpacity;
	private double strokeWeight;

	public Stroke() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param stroke
	 */
	public Stroke(Stroke stroke) {
		this.isStroked = stroke.isStroked;
		this.strokeColor = stroke.strokeColor;
		this.strokeOpacity = stroke.strokeOpacity;
		this.strokeWeight = stroke.strokeWeight;
	}

	/**
	 * @param isStroked
	 *            Boolean if stroke is visible or not
	 * @param strokeColor
	 *            Stroke color in HSB
	 * @param strokeOpacity
	 *            Stroke opacity in HSB
	 * @param strokeWeight
	 *            Stroke weight
	 */
	public Stroke(boolean isStroked, int strokeColor, double strokeOpacity, double strokeWeight) {
		super();
		this.isStroked = isStroked;
		this.strokeColor = strokeColor;
		this.strokeOpacity = strokeOpacity;
		this.strokeWeight = strokeWeight;
	}

	public boolean isStroked() {
		return isStroked;
	}

	public void setStroked(boolean isStroked) {
		this.isStroked = isStroked;
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public double getStrokeOpacity() {
		return strokeOpacity;
	}

	public void setStrokeOpacity(double strokeOpacity) {
		this.strokeOpacity = strokeOpacity;
	}

	public double getStrokeWeight() {
		return strokeWeight;
	}

	public void setStrokeWeight(double strokeWeight) {
		this.strokeWeight = strokeWeight;
	}

	@Override
	public String toString() {
		return "Stroke [isStroked=" + isStroked + ", strokeColor=" + PApplet.hex(strokeColor) + ", strokeOpacity="
				+ strokeOpacity + ", strokeWeight=" + strokeWeight + "]";
	}

}
