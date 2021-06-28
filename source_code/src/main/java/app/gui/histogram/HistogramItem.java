package app.gui.histogram;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;

import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Shape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;

public class HistogramItem extends Shape {

	public static Logger logger = Logger.getLogger(HistogramItem.class);
	private List<Graph> listGraph;
	private double width;
	private double height;
	private double intervalBegin;
	private double intervalEnd;
	private boolean isSelected;

	/**
	 * @param id
	 * @param etiqueta
	 * @param type
	 * @param position
	 * @param stroke
	 * @param fill
	 * @param isVisible
	 * @param listGraph
	 * @param width
	 * @param height
	 * @param intervalBegin
	 * @param intervalEnd
	 * @param isSelected
	 */
	public HistogramItem(int id, Etiqueta etiqueta, int type, PositionShape position, Stroke stroke, Fill fill,
			boolean isVisible, List<Graph> listGraph, double width, double height, double intervalBegin,
			double intervalEnd, boolean isSelected) {
		super(id, etiqueta, position, stroke, fill, isVisible, 0);
		this.listGraph = listGraph;
		this.width = width;
		this.height = height;
		this.intervalBegin = intervalBegin;
		this.intervalEnd = intervalEnd;
		this.isSelected = isSelected;
	}

	/**
	 * Method to get the vertices of the all graphs in an histogram item
	 * 
	 * @return a list of vertex
	 */
	public List<Vertex> getAllVertexOfListGraph() {
		List<Vertex> result = new ArrayList<Vertex>();
		for (Graph g : listGraph) {
			for (Vertex v : g.getListNode()){
				result.add(v);
			}
			//result.addAll(g.getListNode());
		}
		return result;
	}
	
//	public IntObjectHashMap<Vertex> getAllVertexOfListGraph() {
//		IntObjectHashMap<Vertex> result = new IntObjectHashMap();
//		for (Graph g : listGraph) {
//			for (Vertex node : g.getListNode()) {
//				result.put(node.getId(), node);
//			}
//		}
//		return result;
//	}

	public List<Graph> getListGraph() {
		return listGraph;
	}

	public void setListGraph(List<Graph> listGraph) {
		this.listGraph = listGraph;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getIntervalBegin() {
		return intervalBegin;
	}

	public void setIntervalBegin(double intervalBegin) {
		this.intervalBegin = intervalBegin;
	}

	public double getIntervalEnd() {
		return intervalEnd;
	}

	public void setIntervalEnd(double intervalEnd) {
		this.intervalEnd = intervalEnd;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public void display(double zoom) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isInsideScreen(double xView, double yView, double zoom, double screenWidth, double screenHeiht) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "HistogramItem [getId()=" + getId() + ", intervalBegin=" + intervalBegin + ", intervalEnd=" + intervalEnd
				+ "]";
	}

	@Override
	public boolean isAPointInside(double mouseX, double mouseY, double xView, double yView, double zoom) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PositionShape getCentroid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

}
