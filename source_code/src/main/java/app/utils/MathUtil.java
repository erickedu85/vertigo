package app.utils;

import java.util.HashMap;
import java.util.Map;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;

import app.graph.structure.Vertex;

public class MathUtil {

	/**
	 * @param slope
	 * @param x
	 * @param x1
	 * @param y1
	 * @return
	 */
	public static double calculateYEquation(double slope, double x, double x1, double y1) {
		double y = (slope * (x - x1)) + y1;
		return y;
	}

	/**
	 * @param slope
	 * @param y
	 * @param x1
	 * @param y1
	 * @return
	 */
	public static double calculateXEquation(double slope, double y, double x1, double y1) {
		double x = (y + (slope * x1) - y1) / slope;
		return x;
	}

	/**
	 * Calculate Hypotenuse
	 * 
	 * @param opposite
	 *            leg A
	 * @param adjacent
	 *            leg B
	 * @return hypotenuse
	 */
	public static double calculateHypotenuse(double opposite, double adjacent) {
		double hypotenuse;
		int sqrtPow = 2;
		// théorème de pythagore
		hypotenuse = Math.sqrt(Math.pow(opposite, sqrtPow) + Math.pow(adjacent, sqrtPow));
		return hypotenuse;
	}

	/**
	 * Calculate tangent angle in radians
	 * 
	 * @param opposite
	 *            leg A
	 * @param adjacent
	 *            leg B
	 * @return tangent angle in radians
	 */
	public static double calculateTangent(double opposite, double adjacent) {
		double radians;
		radians = Math.atan(opposite / adjacent);
		return radians;
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException nfe) {
		}
		return false;
	}

	/**
	 * Calculate the MBR Minimal Bounding Rectangle of a list of Nodes
	 * 
	 * @param intObjectHashMap
	 * @return the MBR of the list of Nodes
	 */
	public static double calculateMinimalBoundingRectangle(IntObjectHashMap<Vertex> intObjectHashMap) {

		Map<String, Double> threshold = getPositionThresholds(intObjectHashMap);

		double width = threshold.get("xMax") - threshold.get("xMin");
		double lenght = threshold.get("yMax") - threshold.get("yMin");

		double result = GeoUtil.calculateRectangleArea(lenght, width);

		return result;
	}

	/**
	 * Calculate the position thresholds of a list of Nodes
	 * 
	 * @param intObjectHashMap
	 * @return a Map of the position thresholds "xMin", "xMax", "yMin", "yMax"
	 */
	public static Map<String, Double> getPositionThresholds(IntObjectHashMap<Vertex> intObjectHashMap) {

		Map<String, Double> result = new HashMap<String, Double>();

		double minX = Float.MAX_VALUE;
		double maxX = -Float.MAX_VALUE;
		double minY = Float.MAX_VALUE;
		double maxY = -Float.MAX_VALUE;

		for (Vertex node : intObjectHashMap) {
			double x = node.getPosition().getX1();
			double y = node.getPosition().getY1();
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}

		result.put("xMin", minX);
		result.put("xMax", maxX);
		result.put("yMin", minY);
		result.put("yMax", maxY);

		return result;
	}

}
