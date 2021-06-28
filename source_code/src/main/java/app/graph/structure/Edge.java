package app.graph.structure;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.gui.main.Constants;
import app.utils.GeoAnalytic;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * @author Erick Cuenca
 *
 */
public class Edge extends Shape {
	public static Logger logger = Logger.getLogger(Edge.class);
	public static PApplet parent;
	private EdgeType type;
	private int idSource;
	private int idTarget;
	private double overlapFactor = 0.0d; // overlap behavior
	private double desiredDistance = 0.0d; // overlap behavior
	private int stateGhost = Constants.GRAPH_EDGE_NEVER_GHOST;

	/**
	 * @param id
	 * @param etiqueta
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 * @param type
	 * @param source
	 * @param target
	 */
	public Edge(int id, Etiqueta etiqueta, PositionShape position, Stroke stroke, Fill fill, boolean isVisible,
			EdgeType type, int source, int target) {
		super(id, etiqueta, position, stroke, fill, isVisible);
		this.type = type;
		this.idSource = source;
		this.idTarget = target;
	}

	/**
	 * @param e
	 */
	public Edge(Edge e) {
		super(e.getId(), new Etiqueta(e.getEtiqueta()), new PositionShape(e.getPosition()), new Stroke(e.getStroke()),
				new Fill(e.getFill()), e.isVisible(), e.getWeight());
		this.type = e.getType();
		this.idSource = e.getIdSource();
		this.idTarget = e.getIdTarget();
		this.overlapFactor = e.getOverlapFactor();
		this.desiredDistance = e.getDesiredDistance();
		this.stateGhost = e.getStateGhost();
	}

	public void updateSourceOrTarget(int oldId, int newlyId){
		if(idSource == oldId){
			//source
			idSource = newlyId;
		}else{
			//target
			idTarget = newlyId;
		}
		//update edge id
		//this.setId(GraphUtil.edgeIdFunction(idSource, idTarget, type.getId()));
	}
	
	/**
	 * Method to display an edge. THERE IS NOT A FILL IN A LINE IN PROCESSING
	 */
	public void display(double zoom) {
		if (isVisible() && getStroke().isStroked()) {
			parent.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
			parent.strokeWeight((float) getStroke().getStrokeWeight());
			parent.line((float) getPosition().getX1(), (float) getPosition().getY1(), (float) getPosition().getX2(),
					(float) getPosition().getY2());
		}

		// label
		if (getEtiqueta().isLabelled()) {
			
			parent.pushMatrix();
			
			parent.textAlign(PConstants.CENTER, PConstants.CENTER);
			parent.textSize((float)(getEtiqueta().getHeight().floatValue())); 
			parent.fill(ColorShape.getHSB_Black());

			PositionShape centroid = getCentroid();
			parent.translate((float) centroid.getX1(), (float) centroid.getY1());
			double angleRadians = GeoUtil.angleBetweenTwoPoints((float) getPosition().getX1(),
					(float) getPosition().getY1(), (float) getPosition().getX2(), (float) getPosition().getY2());
			parent.rotate((float) angleRadians);
			parent.text(getEtiqueta().getText(), 0, 0);

			parent.popMatrix();
		}

	}

	/**
	 * Display the edge. Used in JAVAFX
	 * 
	 * @param gc
	 */
	public void display(GraphicsContext gc) {
		if (isVisible() && getStroke().isStroked()) {
			double x1 = getPosition().getX1();
			double y1 = getPosition().getY1();
			double x2 = getPosition().getX2();
			double y2 = getPosition().getY2();

			gc.save();
			if (getStateGhost() == Constants.GRAPH_EDGE_IS_GHOST) {
				gc.setLineDashes(Constants.GRAPH_QUERY_EDGE_STROKE_GHOST_DASHES);
			}
			gc.setStroke(ColorShape.parserColorProcessingToJavafx(getStroke().getStrokeColor()));
			gc.setLineWidth(getStroke().getStrokeWeight()*getWeight());
			gc.setGlobalAlpha(Constants.GRAPH_QUERY_EDGE_ALPHA);
			gc.strokeLine(x1, y1, x2, y2);
			gc.restore();

			// label
			if (getEtiqueta().isLabelled()) {
				gc.save();
				gc.setFill(Color.BLACK);
				gc.setStroke(Color.WHITE);
				gc.setTextAlign(TextAlignment.CENTER);
				gc.setTextBaseline(VPos.CENTER);
				gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);

				PositionShape centroid = getCentroid();
				double slope = GeoUtil.slopeBetweenTwoPoints(x1, y1, x2, y2);
				double degreeSlope = GeoUtil.degreeOfSlope(slope);

				gc.translate(centroid.getX1(), centroid.getY1());
				gc.rotate(degreeSlope);
				gc.fillText(getEtiqueta().getText(), 0, 0);
				gc.restore();
			}

		}
	}

	public Geometry getGeometry() {
		Coordinate[] coordinatesEdge = new Coordinate[] { new Coordinate(getPosition().getX1(), getPosition().getY1()),
				new Coordinate(getPosition().getX2(), getPosition().getY2()) };
		Geometry geometry = new GeometryFactory().createLineString(coordinatesEdge)
				.buffer(getStroke().getStrokeWeight() / 2 - 1.5); // 2
		return geometry;
	}

	/**
	 * Method to get if an edge is inside the screen.
	 * 
	 * @param xView
	 * @param yView
	 * @param zoom
	 * @param screenWidth
	 * @param screenHeiht
	 * @return
	 */
	public boolean isInsideScreen(double xView, double yView, double zoom, double screenWidth, double screenHeiht) {
		// parent.getWidth() - Constants.LEFT_PANEL_WIDTH
		return ((getPosition().getX1() + (-xView * zoom) >= 0)
				&& (getPosition().getX1() + (-xView * zoom) < screenWidth))
				&& (((getPosition().getY1() + (-yView * zoom) >= 0))
						&& (getPosition().getY1() + (-yView * zoom) < screenHeiht));
	}

	/**
	 * Collision detection. Method to verify if the edge is near to the mouse
	 * pointer
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param xView
	 * @param yView
	 * @param zoom
	 * @return true if the edge is near of the mouse, otherwise false
	 */
	public boolean isAPointInside(double mouseX, double mouseY, double xView, double yView, double zoom) {

		PVector p1 = new PVector((int) (getPosition().getX1() + (-xView * zoom)),
				(int) (getPosition().getY1() + (-yView * zoom)));
		PVector p2 = new PVector((int) (getPosition().getX2() + (-xView * zoom)),
				(int) (getPosition().getY2() + (-yView * zoom)));
		PVector mouse = new PVector((int) mouseX, (int) mouseY);

		boolean isCollision = GraphUtil.isOnePointDistSmallTolerToLine(mouse, p1, p2,
				getStroke().getStrokeWeight() / 2);
		return isCollision;
	}

	/**
	 * Method to know if the edge contains the BOTH idVertex1 and idVertex2 as
	 * source OR target
	 * 
	 * @param idVertex1
	 *            Vertex 1
	 * @param idVertex2
	 *            Vertex 2
	 * @return True if the edge contains the both V1 and V2 as source or target
	 */
	public boolean containVertices(int idVertex1, int idVertex2) {
		if (containOneVertex(idVertex1) && containOneVertex(idVertex2)) {
			return true;
		}
		return false;
	}

	/**
	 * Method to know if the edge contains AT LEAST Vertex1
	 * 
	 * @param idVertex
	 * @return True if the edge contains at least this vertex
	 */
	public boolean containOneVertex(int idVertex) {
		if ((idSource == idVertex) || (idTarget == idVertex)) {
			return true;
		}
		return false;
	}

	/**
	 * Method to get the adjacent vertex of the idVertex
	 * 
	 * @param idVertex
	 * @return Adjacent vertex id
	 */
	public int adjacentVertex(int idVertex) {
		if (idSource == idVertex) {
			return idTarget;
		} else {
			return idSource;
		}
	}

	/**
	 * Method to get the Euclidean Distance
	 * 
	 * @return
	 */
	public double getDistance() {
		return GeoAnalytic.euclideanDistanceBetweenTwoPoints(getPosition().getX1(), getPosition().getY1(),
				getPosition().getX2(), getPosition().getY2());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (!this.containVertices(other.idSource, other.idTarget))
			return false;
		if (getType().getId() != other.getType().getId()) // Type
			return false;
		return true;
	}

	public int getIdSource() {
		return idSource;
	}

	public void setIdSource(int idSource) {
		this.idSource = idSource;
	}

	public int getIdTarget() {
		return idTarget;
	}

	public void setIdTarget(int idTarget) {
		this.idTarget = idTarget;
	}

	public double getOverlapFactor() {
		return overlapFactor;
	}

	public void setOverlapFactor(double overlapFactor) {
		this.overlapFactor = overlapFactor;
	}

	public double getDesiredDistance() {
		return desiredDistance;
	}

	public void setDesiredDistance(double desiredDistance) {
		this.desiredDistance = desiredDistance;
	}

	public int getStateGhost() {
		return stateGhost;
	}

	public void setStateGhost(int stateGhost) {
		this.stateGhost = stateGhost;
	}

	public EdgeType getType() {
		return type;
	}

	public void setType(EdgeType type) {
		this.type = type;
	}

	@Override
	public PositionShape getCentroid() {
		return GeoUtil.middlePointBetweenTwoPoints(getPosition().getX1(), getPosition().getY1(), getPosition().getX2(),
				getPosition().getY2());
	}

	@Override
	public double getArea() {
		return 0;
	}

	@Override
	public String toString() {
//		return "Edge [type=" + type + ", idSource=" + idSource + ", idTarget=" + idTarget + ", overlapFactor="
//				+ overlapFactor + ", desiredDistance=" + desiredDistance + ", stateGhost=" + stateGhost + ", getId()="
//				+ getId() + ", getPosition()=" + getPosition() + ", getStroke()=" + getStroke() + ", getFill()="
//				+ getFill() + ", isVisible()=" + isVisible() + ", getEtiqueta()=" + getEtiqueta() + ", toString()="
//				+ super.toString() + ", getWeight()=" + getWeight() + ", getClass()=" + getClass() + ", hashCode()="
//				+ hashCode() + "]";
//		
		return "Edge [type=" + type + ", idSource=" + idSource + ", idTarget=" + idTarget + ", getWeight()=" + getWeight() + "]";
	}

//	@Override
//	public String toString() {
//		return "[id=" + getId() + ", idSource=" + getIdSource() + ", idTarget=" + getIdTarget() +  ", type=|" + getType().getId() + "|, weight=" + getWeight()+ "]";
//	}
	
	

}
