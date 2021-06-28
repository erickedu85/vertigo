package app.graph.structure;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import app.gui.database.GuiGraphDB;
import app.gui.main.Constants;
import app.utils.GeoUtil;
import app.utils.GraphUtil;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

public class Vertex extends Shape {

	public static Logger logger = Logger.getLogger(Vertex.class);
	public static PApplet parent;
	private double radius;
	private NodeType type;
	private boolean isSelected = false;
	private boolean isFixed = false;
	private boolean isShowImgBackground = false;


	/**
	 * @param id
	 * @param etiqueta
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 * @param radius
	 */
	public Vertex(int id, Etiqueta etiqueta, PositionShape position, Stroke stroke, Fill fill, boolean isVisible,
			double radius, NodeType type) {
		super(id, etiqueta, position, stroke, fill, isVisible);
		this.radius = radius;
		this.type = type;
	}

	/**
	 * @param id
	 * @param etiqueta
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 * @param radius
	 * @param fixed
	 */
	public Vertex(int id, Etiqueta etiqueta, PositionShape position, Stroke stroke, Fill fill, boolean isVisible,
			double radius, boolean fixed) {
		super(id, etiqueta, position, stroke, fill, isVisible);
		this.radius = radius;
		this.isFixed = fixed;
	}
	

	/**
	 * Method to create a vertex from another vertex
	 * 
	 * @param v
	 *            Vertex
	 */
	public Vertex(Vertex v) {
		super(v.getId(), new Etiqueta(v.getEtiqueta()), new PositionShape(v.getPosition()), new Stroke(v.getStroke()),
				new Fill(v.getFill()), v.isVisible(), v.getWeight());
		this.radius = v.getRadius();
		this.type = v.getType();
	}

	/**
	 * @param startAngle
	 * @param strokeWeight
	 * @param radius
	 */
	public void displayArc(double startAngle) {
		parent.noFill();
		parent.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
		parent.strokeWeight((float) getStroke().getStrokeWeight());
		parent.arc((float) getPosition().getX1(), (float) getPosition().getY1(),
				(float) getRadius(), (float) getRadius(),
				(float) startAngle, (float) (Math.PI + startAngle));
	}

	// https://processing.org/tutorials/drawing/
	/**
	 * Method to display a vertex. Mode ellipseMode(Radius)
	 */
	public void display(double zoom) {
		if (isVisible()) {
			if (getStroke().isStroked()) {
				parent.stroke(getStroke().getStrokeColor(), (float) getStroke().getStrokeOpacity());
				parent.strokeWeight((float) getStroke().getStrokeWeight());
			} else {
				parent.noStroke();
			}
			if (getFill().isFilled()) {
				parent.fill(getFill().getFillColor(), (float) getFill().getFillOpacity());
			} else {
				parent.noFill();
			}
			parent.ellipse((float) getPosition().getX1(), (float) getPosition().getY1(), (float) getRadius(),
					(float) getRadius());
		}
		
		parent.pushMatrix();
		PositionShape centroid = getCentroid();
		parent.translate((float) centroid.getX1(), (float) centroid.getY1());
		double angleInRad = 0; // 0;// 90 * Math.PI/180;
		parent.rotate((float)angleInRad);
		
		if(isShowImgBackground()) {
			
			double facteur = 1.2;
			double widthImg = getRadius()/facteur;
			double heightImg = getRadius()/facteur;
			
			double imgCoordinateX = 0 - (widthImg / 2); //+ Math.cos(angleInRad) * getRadius();
			double imgCoordinateY = 0 - (heightImg / 2);// - Math.sin(angleInRad) * getRadius();	
			
			PImage cursorImg  = parent.loadImage(this.getClass().getClassLoader().getResource(Constants.RESOURCES_IMAGE_PATH_NODE_TYPES.concat(getType().getRelativePath())).getFile());
			parent.image(cursorImg,(float) imgCoordinateX, (float) imgCoordinateY,(float) widthImg, (float) heightImg);
		}
		
		// Possible to show only label and rectangle background
		if (getEtiqueta().isLabelled()) {

			float x1 = 0; // (float) (getPosition().getX1());
			float y1 = 0; // (float) (getPosition().getY1());

			if (getEtiqueta().isShowRectangle()) {
				// radius value for all four corners
				double radiusBorder = 7.0d;
				Double textWidth = (double) parent.textWidth(getEtiqueta().getText());
				parent.noStroke();
				parent.rectMode(PConstants.RADIUS);
				parent.fill(getEtiqueta().getFillRectangle().getFillColor(),(float)getEtiqueta().getFillRectangle().getFillOpacity());
				parent.rect(0, (float)(0+getRadius()/2), 
						(float) (textWidth.floatValue()/2 + getEtiqueta().getPaddingRectangle()),
						(float) (getEtiqueta().getHeight().floatValue()/2 + getEtiqueta().getPaddingRectangle()),
						(float) radiusBorder);
//				parent.rect(x1, (float)(y1+getRadius()/2), 
//						(float) (textWidth.floatValue()/2 + getEtiqueta().getPaddingRectangle()),
//						(float) (getEtiqueta().getHeight().floatValue()/2 + getEtiqueta().getPaddingRectangle()),
//						(float) radiusBorder);
			}
			//show label
			
			parent.textAlign(PConstants.CENTER, PConstants.CENTER);			
			parent.textSize((float)(getEtiqueta().getHeight().floatValue()));
			parent.fill(getEtiqueta().getFillLabel().getFillColor());
			
			parent.text(getEtiqueta().getText(), x1, (float)(y1+getRadius()/2));
//			parent.text(getEtiqueta().getText(), 0, 0);
			
		}
		parent.popMatrix();
	}

	/**
	 * Method to display a vertex, used in JAVAFX
	 * 
	 * @param gc
	 *            GraphicsContext
	 * @param mouseX
	 * @param mouseY
	 * @param animation
	 * @param xView
	 * @param yView
	 * @param zoom
	 */
	public void display(GraphicsContext gc, double mouseX, double mouseY, boolean animation, double xView, double yView,
			double zoom) {

		// JavaFX ellipse mode in the top-left corner
		double toDrawXPosition = getPosition().getX1() - getRadius();
		double toDrawYPosition = getPosition().getY1() - getRadius();
		double diameter =  getRadius() * 2;
	
		gc.setLineWidth(getStroke().getStrokeWeight());
		gc.setStroke(ColorShape.parserColorProcessingToJavafx(getStroke().getStrokeColor()));
		gc.strokeOval(toDrawXPosition, toDrawYPosition, diameter, diameter);
		
		if (animation) {
			if (isAPointInside(mouseX, mouseY, xView, yView, zoom)) {
				gc.setLineWidth(Constants.GRAPH_QUERY_NODE_STROKE_WEIGHT_HOVER);
				gc.setStroke(Color.BLACK);
				gc.strokeOval(toDrawXPosition, toDrawYPosition, diameter, diameter);
			} 
		}
		//Fill
		gc.setFill(ColorShape.parserColorProcessingToJavafx(getFill().getFillColor()));
		gc.fillOval(toDrawXPosition, toDrawYPosition, diameter, diameter);
		
		if(isShowImgBackground()) {			
			double angleInRad = 90 * Math.PI/180;
			double facteur = 1;
			double widthImg = getRadius()/facteur;
			double heightImg = getRadius()/facteur;
			
			double imgCoordinateX = getPosition().getX1() - (widthImg / 2);// + Math.cos(angleInRad) * getRadius();
			double imgCoordinateY = getPosition().getY1() - (heightImg / 2);// - Math.sin(angleInRad) * getRadius();
			
			gc.drawImage(new Image(this.getClass().getClassLoader().getResourceAsStream(Constants.RESOURCES_IMAGE_PATH_NODE_TYPES.concat(getType().getRelativePath()))),
					imgCoordinateX,
					imgCoordinateY,
					widthImg,heightImg);			
		}
		
		if (getEtiqueta().isLabelled()) {
			double x1 = getPosition().getX1();
			double y1 = getPosition().getY1();
			double widthTextMax = getRadius()*5;
			
			if(getEtiqueta().isShowRectangle()) {
				double radiusBorder = Constants.GRILLA*1.5;
				//to get the with of the Etiqueta text
				final Text text = new Text(" " + getEtiqueta().getText() + " ");
				text.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
			    final double widthText = Math.min(text.getLayoutBounds().getWidth(), widthTextMax);
				//
			    gc.setGlobalAlpha(0.3);
				gc.setFill(Color.WHITE);
				gc.fillRoundRect(x1 - widthText/2, y1 -Constants.GRILLA*1.5, widthText, Constants.GRILLA*3, radiusBorder, radiusBorder);
			}
			// show label
			gc.setTextAlign(TextAlignment.CENTER); //horizontal align
			gc.setTextBaseline(VPos.CENTER); //vertical align
			gc.setFont(Constants.GRAPH_QUERY_EDGE_LABEL_FONT);
			gc.setGlobalAlpha(1);
			gc.setFill(Color.BLACK);
			gc.fillText(getEtiqueta().getText(), x1, y1,widthTextMax);
		} 
	}

	/**
	 * Method to get if a node is inside the screen.
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
	 * Collision detection. Verify if a node in the graph database is near to
	 * the mouse pointer
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param xView
	 * @param yView
	 * @param zoom
	 * @return true if the node is near of the mouse, otherwise false
	 */
	public boolean isAPointInside(double mouseX, double mouseY, double xView, double yView, double zoom) {
		boolean isCollision = GraphUtil.isTwoPointDistSmallTolerance(getPosition().getX1() + (-xView * zoom),
				getPosition().getY1() + (-yView * zoom), mouseX, mouseY, getRadius());
		return isCollision;
	}

	/**
	 * Method to get the Geometry of a vertex
	 * 
	 * @return
	 */
	public Geometry getGeometry() {
		Geometry geometry = new GeometryFactory()
				.createPoint(new Coordinate(getPosition().getX1(), getPosition().getY1()))
				.buffer(getRadius() / 2 - 1.5); // 1.5
		return geometry;
	}

	/**
	 * Method to get the Rectangle End of the label
	 * 
	 * @return
	 */
	public PositionShape getRectangleEnds() {
		double factWidth = 1;
		double factHeight = 1;

		Double height = getEtiqueta().getHeight();
		
		parent.textSize((float) (height.floatValue()));
		double width = parent.textWidth(getEtiqueta().getText());

		PositionShape rectangleEnds = GeoUtil.calculateRectangleByCenterPoint(getPosition(), width, height, factWidth,
				factHeight);

		return rectangleEnds;
	}

	@Override
	public PositionShape getCentroid() {
		return getPosition();
	}

	@Override
	public double getArea() {
		return Math.PI * Math.pow(getRadius(), 2);
	}

	/*
	 * Two vertices are equals if they have the same IDs
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof Vertex) {
			Vertex element = (Vertex) obj;
			if (element.getId() == this.getId()) {
				return true;
			}
		}
		return false;
	}

	public double getDiameter() {
		return getRadius()*2;				
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isFixed() {
		return isFixed;
	}

	public void setFixed(boolean isFixed) {
		this.isFixed = isFixed;
	}
	
	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}
	

	public boolean isShowImgBackground() {
		return isShowImgBackground;
	}

	public void setShowImgBackground(boolean isShowImgBackground) {
		this.isShowImgBackground = isShowImgBackground;
	}

	@Override
//	public String toString() {
//		return "Vertex [radius=" + radius + ", type=" + type + ", isSelected=" + isSelected + ", isFixed=" + isFixed +"]";
//	}

	
	
	
//	@Override
	public String toString() {
		// return "Vertex [getId()=" + getId() + ", getEtiqueta()=" +
		// getEtiqueta().getText() + "]";
		return "[id="+ getId() + ", label="+  getEtiqueta().getText() + ", type="+ getType() + ", weight="+ getWeight() + "]";
	}
}
