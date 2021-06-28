package app.graph.structure;

import processing.core.PApplet;

public class Fill {

	private boolean isFilled;
	private int fillColor;
	private double fillOpacity;

	public Fill() {
		// TODO Auto-generated constructor stub
	}

	public Fill(Fill fill) {
		this.isFilled = fill.isFilled;
		this.fillColor = fill.fillColor;
		this.fillOpacity = fill.fillOpacity;
	}

	/**
	 * @param isFilled
	 *            Boolean if fill is visible or not
	 * @param fillColor
	 *            Fill color
	 * @param fillOpacity
	 *            Fill opacity
	 */
	public Fill(boolean isFilled, int fillColor, double fillOpacity) {
		super();
		this.isFilled = isFilled;
		this.fillColor = fillColor;
		this.fillOpacity = fillOpacity;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public void setFilled(boolean isFilled) {
		this.isFilled = isFilled;
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public double getFillOpacity() {
		return fillOpacity;
	}

	public void setFillOpacity(double fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	@Override
	public String toString() {
		return "Fill [isFilled=" + isFilled + ", fillColor=" + PApplet.hex(fillColor) + ", fillOpacity=" + fillOpacity
				+ "]";
	}
}
