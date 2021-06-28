package app.graph.structure;

import app.gui.main.Constants;

public class Etiqueta {

	private int pinId = Constants.GRAPH_DB_NODE_PIN_ID; // -1 id of the Entity
	private boolean isLabelled = false;
	private String text = "";
	private Double height = 1.0d;
	private boolean showRectangle = false;
	private Fill fillRectangle = new Fill();
	private Fill fillLabel = new Fill();
	

	private double paddingRectangle = 0.0d;

	public Etiqueta() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param text
	 * @param isLabelled
	 */
	public Etiqueta(String text, boolean isLabelled) {
		this.text = text;
		this.isLabelled = isLabelled;
	}

	/**
	 * @param etiqueta
	 */
	public Etiqueta(Etiqueta etiqueta) {
		this.pinId = etiqueta.pinId;
		this.isLabelled = etiqueta.isLabelled;
		this.text = etiqueta.text;
		this.height = etiqueta.height;
		this.showRectangle = etiqueta.showRectangle;
		this.fillRectangle = etiqueta.fillRectangle;
		this.paddingRectangle = etiqueta.paddingRectangle;
		this.fillLabel = etiqueta.fillLabel;
	}

	public int getPinId() {
		return pinId;
	}

	public void setPinId(int pinId) {
		this.pinId = pinId;
	}

	public boolean isLabelled() {
		return isLabelled;
	}

	public void setLabelled(boolean isLabelled) {
		this.isLabelled = isLabelled;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public boolean isShowRectangle() {
		return showRectangle;
	}

	public void setShowRectangle(boolean showRectangle) {
		this.showRectangle = showRectangle;
	}

	public Fill getFillRectangle() {
		return fillRectangle;
	}

	public void setFillRectangle(Fill fillRectangle) {
		this.fillRectangle = fillRectangle;
	}

	public double getPaddingRectangle() {
		return paddingRectangle;
	}

	public void setPaddingRectangle(double paddingRectangle) {
		this.paddingRectangle = paddingRectangle;
	}
	
	public Fill getFillLabel() {
		return fillLabel;
	}

	public void setFillLabel(Fill fillLabel) {
		this.fillLabel = fillLabel;
	}

}
