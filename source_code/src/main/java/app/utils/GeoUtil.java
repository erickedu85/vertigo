package app.utils;

import java.util.HashMap;
import java.util.Map;

import app.graph.structure.PositionShape;

public class GeoUtil {

	public static double getY(double slope, double knowX, double knowY, double x) {
		// Y-Yo = m(X-Xo) => Y = m(X-Xo) + Yo
		double m = slope;
		double Xo = knowX;
		double Yo = knowY;

		double y = m * (x - Xo) + Yo;

		return y;
	}

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static Map<String, Double> calculateCuadraticEquation(double a, double b, double c) {
		// Ax^2 + Bx + C = 0
		// x = -b +- SQRT(b^2-4ac) / 2a
		Map<String, Double> solution = new HashMap<String, Double>();
		double x1 = 0.0;
		double x2 = 0.0;
		double discriminante = ((b * b) - (4 * a * c));

		x1 = ((-b) + Math.sqrt(discriminante)) / (2 * a);
		x2 = ((-b) + Math.sqrt(discriminante)) / (2 * a);

		solution.put("x1", x1);
		solution.put("x2", x2);

		return solution;
	}

	/**
	 * The smaller (acute) angle having as sides the half-lines starting from
	 * the intersection point of the lines and lying on those two lines, if the
	 * lines are not parallel.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param xIntersection
	 * @param yIntersection
	 * @return
	 */
	public static double angleSmallerBetweenTwoLines(double x1, double y1, double x2, double y2, double xIntersection,
			double yIntersection) {

		double slope1 = slopeBetweenTwoPoints(x1, y1, xIntersection, yIntersection);
		double slope2 = slopeBetweenTwoPoints(x2, y2, xIntersection, yIntersection);

		double angle = Math.atan(Math.abs((slope2 - slope1) / (1 + slope1 * slope2)));
		return angle;
	}

	public static PositionShape middlePointBetweenTwoPoints(double x1, double y1, double x2, double y2) {
		double x = (x1 + x2) / 2;
		double y = (y1 + y2) / 2;
		PositionShape middlePoint = new PositionShape(x, y);
		return middlePoint;
	}

	/**
	 * Method to get the angle theta of the polar coordinates (r,theta) signo
	 * del valor depende del cuadrante del angle theta in radians
	 * 
	 * @param xPoint
	 * @param yPoint
	 * @param xCircleCenter
	 * @param yCircleCenter
	 * @return angle theta in radian
	 */
	public static double angleOfPolarCoordinates(double xPoint, double yPoint, double xCircleCenter,
			double yCircleCenter) {
		double angle = Math.atan2(yPoint - yCircleCenter, xPoint - xCircleCenter);
		return (angle);
	}

	/**
	 * Method to get angle of two points in radians Take slope between this two
	 * points as reference
	 * 
	 * @param x1
	 *            Point 1
	 * @param y1
	 *            Point 1
	 * @param x2
	 *            Point 2
	 * @param y2
	 *            Point 2
	 * @return angle in radians
	 */
	public static double angleBetweenTwoPoints(double x1, double y1, double x2, double y2) {
		double angle = Math.atan(slopeBetweenTwoPoints(x1, y1, x2, y2));
		return angle;
	}

	/**
	 * Method to get the Slope between 2 points
	 * 
	 * @param x1
	 *            Point1
	 * @param y1
	 *            Point1
	 * @param x2
	 *            Point2
	 * @param y2
	 *            Point2
	 * @return -1 vertical line, 0 horizontal line, otherwise slope value
	 */
	public static double slopeBetweenTwoPoints(double x1, double y1, double x2, double y2) {
		double numerator = (y2 - y1);
		double denominator = (x2 - x1);

		if (numerator == 0 && denominator == 0) {
			// Vertical line = slope no defined
			return -1;
		} else if (numerator == 0) {
			// horizontal line = slope zero
			return 0;
		} else {
			return numerator / denominator;
		}
	}

	/**
	 * Method to return the degrees of a slope
	 * 
	 * @param slope
	 * @return Degrees of a slope
	 */
	public static double degreeOfSlope(double slope) {
		return Math.toDegrees(Math.atan(slope));
	}

	/**
	 * Calculate Rectangle Area
	 * 
	 * @param lenght
	 * @param width
	 * @return Area
	 */
	public static double calculateRectangleArea(double lenght, double width) {
		double area;
		area = lenght * width;
		return area;
	}
	
	/**
	 * Calculate Circle Area
	 * 
	 * @param radius
	 * @return
	 */
	public static double calculateCircleArea(double radius){
		double area;
		area = Math.PI * Math.pow(radius, 2);
		return area;
	}

	/**
	 * @param centerPoint
	 * @param width
	 * @param height
	 * @param factorIncreWidth
	 *            > 0
	 * @param factorIncreHeight
	 *            > 0
	 * @return
	 */
	public static PositionShape calculateRectangleByCenterPoint(PositionShape centerPoint, double width, double height,
			double factorIncreWidth, double factorIncreHeight) {

		// Bottom-left and top-right ends
		PositionShape rectangleEnd = new PositionShape();

		if (factorIncreWidth != 0) {
			width = width * factorIncreWidth;
		}
		if (factorIncreHeight != 0) {
			height = height * factorIncreHeight;
		}

		// bottom left
		rectangleEnd.setX1(centerPoint.getX1() - (width / 2));
		rectangleEnd.setY1(centerPoint.getY1() + (height / 2));
		// top-right
		rectangleEnd.setX2(centerPoint.getX1() + (width / 2));
		rectangleEnd.setY2(centerPoint.getY1() - (height / 2));

		return rectangleEnd;
	}

	// https://stackoverflow.com/questions/23302698/java-check-if-two-rectangles-overlap-at-any-point
	/**
	 * @param rect1
	 * @param rect2
	 * @return true if the rectangles are overlapping
	 */
	public static boolean isOverlappingTwoRectangles(PositionShape rect1, PositionShape rect2) {

		double x1 = rect1.getX1();
		double y1 = rect1.getY1();

		double x2 = rect1.getX2();
		double y2 = rect1.getY2();

		double x3 = rect2.getX1();
		double y3 = rect2.getY1();

		double x4 = rect2.getX2();
		double y4 = rect2.getY2();

		// <
		// >
		if (x3 > x2 || y3 < y2 || x1 > x4 || y1 < y4) {
			return false;
		}

		return true;
	}

}
