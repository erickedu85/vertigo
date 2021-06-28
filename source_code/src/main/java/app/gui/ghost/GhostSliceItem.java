package app.gui.ghost;

import app.graph.structure.EdgeType;

public class GhostSliceItem {

	private int idNodeParent;
	private EdgeType layerType;
	private int numAppears;
	private double angleAccumulate;

	public GhostSliceItem(int idNodeParent, EdgeType layerType, int numAppears) {
		super();
		this.idNodeParent = idNodeParent;
		this.layerType = layerType;
		this.numAppears = numAppears;
	}

	public int getNumAppears() {
		return numAppears;
	}

	public void setNumAppears(int numAppears) {
		this.numAppears = numAppears;
	}

	public int getIdNodeParent() {
		return idNodeParent;
	}

	public void setIdNodeParent(int idNodeParent) {
		this.idNodeParent = idNodeParent;
	}

	@Override
	public String toString() {
		return "GhostSliceItem [idNodeParent=" + idNodeParent + ", layerType=" + layerType + ", numAppears=" + numAppears
				+ "]";
	}

	public double getAngleAccumulate() {
		return angleAccumulate;
	}

	public void setAngleAccumulate(double angleAccumulate) {
		this.angleAccumulate = angleAccumulate;
	}

	public EdgeType getLayerType() {
		return layerType;
	}

	public void setLayerType(EdgeType layerType) {
		this.layerType = layerType;
	}

}
