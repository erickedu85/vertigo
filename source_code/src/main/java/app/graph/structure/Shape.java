package app.graph.structure;

public abstract class Shape {

	private int id = 0;
	private Etiqueta etiqueta = new Etiqueta();
	private PositionShape position = new PositionShape();
	private Stroke stroke = new Stroke();
	private Fill fill = new Fill();
	private boolean isVisible = false;
	private double weight = 1.0d; // default

	/**
	 * @param id
	 * @param etiqueta
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 * @param weight
	 */
	public Shape(int id, Etiqueta etiqueta, PositionShape position, Stroke stroke, Fill fill, boolean isVisible,
			double weight) {
		this.id = id;
		this.etiqueta = etiqueta;
		this.position = position;
		this.stroke = stroke;
		this.fill = fill;
		this.isVisible = isVisible;
		this.weight = weight;
	}

	/**
	 * @param id
	 * @param etiqueta
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 */
	public Shape(int id, Etiqueta etiqueta, PositionShape position, Stroke stroke, Fill fill, boolean isVisible) {
		this.id = id;
		this.etiqueta = etiqueta;
		this.position = position;
		this.stroke = stroke;
		this.fill = fill;
		this.isVisible = isVisible;
	}

	public abstract void display(double zoom);

	public abstract boolean isInsideScreen(double xView, double yView, double zoom, double screenWidth,
			double screenHeiht);

	public abstract boolean isAPointInside(double mouseX, double mouseY, double xView, double yView, double zoom);

	public abstract PositionShape getCentroid();

	public abstract double getArea();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PositionShape getPosition() {
		return position;
	}

	public void setPosition(PositionShape position) {
		this.position = position;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Fill getFill() {
		return fill;
	}

	public void setFill(Fill fill) {
		this.fill = fill;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Override
	public String toString() {
		return "Shape [id=" + id + ", label=" + etiqueta + ", position=" + position + ", stroke=" + stroke + ", fill="
				+ fill + ", isVisible=" + isVisible + "]";
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
