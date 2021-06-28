package app.graph.structure;

public class PositionShape {

	private double x1;
	private double y1;
	private double x2;
	private double y2;

	public PositionShape() {
		super();
	}

	public PositionShape(double x1, double y1) {
		super();
		this.x1 = x1;
		this.y1 = y1;
	}

	public PositionShape(double x1, double y1, double x2, double y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * @param p1
	 * @param p2
	 */
	public PositionShape(PositionShape p1, PositionShape p2) {
		super();
		this.x1 = p1.x1;
		this.y1 = p1.y1;
		this.x2 = p2.x1;
		this.y2 = p2.y1;
	}

	/**
	 * @param p
	 */
	public PositionShape(PositionShape p) {
		super();
		this.x1 = p.x1;
		this.y1 = p.y1;
		this.x2 = p.x2;
		this.y2 = p.y2;
	}

	/**
	 * Method to sum two positions
	 * 
	 * @param a
	 *            Position A
	 * @param b
	 *            Position B
	 * @return Sum of positions
	 */
	public static PositionShape sum(PositionShape a, PositionShape b) {
		double nuevoX = a.getX1() + b.getX1();
		double nuevoY = a.getY1() + b.getY1();
		return new PositionShape(nuevoX, nuevoY);
	}

	/**
	 * Method to rest Position A - Position B
	 * 
	 * @param a
	 *            Position A
	 * @param b
	 *            Position B
	 * @return Rest of position A - position B
	 */
	public static PositionShape rest(PositionShape a, PositionShape b) {
		double nuevoX = a.getX1() - b.getX1();
		double nuevoY = a.getY1() - b.getY1();
		return new PositionShape(nuevoX, nuevoY);
	}

	/**
	 * Method to multiply a position for a factor
	 * 
	 * @param p
	 *            Position
	 * @param factor
	 *            Factor
	 * @return The multiplication of the position for a factor
	 */
	public static PositionShape multiply(PositionShape p, double factor) {
		double nuevoX = p.getX1() * factor;
		double nuevoY = p.getY1() * factor;
		return new PositionShape(nuevoX, nuevoY);
	}

	/**
	 * Method to divide a position for a factor
	 * 
	 * @param p
	 *            Position
	 * @param factor
	 *            Factor
	 * @return The division of the position for a factor
	 */
	public static PositionShape divide(PositionShape p, double factor) {
		double nuevoX = p.getX1() / factor;
		double nuevoY = p.getY1() / factor;
		return new PositionShape(nuevoX, nuevoY);
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
		this.x1 = x1;
	}

	public double getY1() {
		return y1;
	}

	public void setY1(double y1) {
		this.y1 = y1;
	}

	public double getX2() {
		return x2;
	}

	public void setX2(double x2) {
		this.x2 = x2;
	}

	public double getY2() {
		return y2;
	}

	public void setY2(double y2) {
		this.y2 = y2;
	}

	@Override
	public String toString() {
		// return "(" + Constants.DECIMAL_FORMAT.format(x1) + "," +
		// Constants.DECIMAL_FORMAT.format(y1) + ")";
//		return "PositionShape [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
		return "PositionShape [x1=" + x1 + ", y1=" + y1 + "]";
	}

}
