package app.utils;

import java.util.Arrays;

import app.graph.structure.PositionShape;

public class GeoAnalytic {

	/**
	 * Method to get the points tangents between 2 Circles with the same radius
	 * 
	 * @param x1
	 *            Circle 1 x coordinate
	 * @param y1
	 *            Circle 1 y coordinate
	 * @param r1
	 *            Circle 1 radius
	 * @param x2
	 *            Circle 2 x coordinate
	 * @param y2
	 *            Circle 2 y coordinate
	 * @param r2
	 *            Circle 2 radius
	 * @return Points of tangent between 2 Circles
	 */
	public static double[][] tangentPoints2Circles(double x1, double y1, double r1, double x2, double y2, double r2) {
		double d_sq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		if (d_sq <= (r1 - r2) * (r1 - r2))
			return new double[0][4];

		double d = Math.sqrt(d_sq);
		double vx = (x2 - x1) / d;
		double vy = (y2 - y1) / d;

		double[][] res = new double[4][4];
		int i = 0;

		// Let A, B be the centers, and C, D be points at which the tangent
		// touches first and second circle, and n be the normal vector to it.
		//
		// We have the system:
		// n * n = 1 (n is a unit vector)
		// C = A + r1 * n
		// D = B +/- r2 * n
		// n * CD = 0 (common orthogonality)
		//
		// n * CD = n * (AB +/- r2*n - r1*n) = AB*n - (r1 -/+ r2) = 0, <=>
		// AB * n = (r1 -/+ r2), <=>
		// v * n = (r1 -/+ r2) / d, where v = AB/|AB| = AB/d
		// This is a linear equation in unknown vector n.

		for (int sign1 = +1; sign1 >= -1; sign1 -= 2) {
			double c = (r1 - sign1 * r2) / d;

			// Now we're just intersecting a line with a circle: v*n=c, n*n=1

			if (c * c > 1.0)
				continue;
			double h = Math.sqrt(Math.max(0.0, 1.0 - c * c));

			for (int sign2 = +1; sign2 >= -1; sign2 -= 2) {
				double nx = vx * c - sign2 * h * vy;
				double ny = vy * c + sign2 * h * vx;

				double[] a = res[i++];
				a[0] = x1 + r1 * nx;
				a[1] = y1 + r1 * ny;
				a[2] = x2 + sign1 * r2 * nx;
				a[3] = y2 + sign1 * r2 * ny;
			}
		}

		return Arrays.copyOf(res, i);
	}

	/**
	 * Method to get the tangent points from a POINT to a CIRCLE
	 * 
	 * @param point
	 *            Point
	 * @param circle
	 *            Circle center
	 * @param radius
	 *            Circle radius
	 * @return Array of Tangents Points like (x1,y1) and (x2,y2)
	 */
	public static PositionShape[] tangentPointToCircle(PositionShape point, PositionShape circle, Double radius) {
		// http://www.ambrsoft.com/TrigoCalc/Circles2/CirclePoint/CirclePointDistance.htm
		Double a = circle.getX1();
		Double b = circle.getY1();
		Double xp = point.getX1();
		Double yp = point.getY1();
		Double r = radius;
		PositionShape[] p = new PositionShape[2];

		Double x1 = ((Math.pow(r, 2) * (xp - a)
				+ r * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r, 2)))
				/ (Math.pow((xp - a), 2) + Math.pow((yp - b), 2))) + a;

		Double y1 = ((Math.pow(r, 2) * (yp - b)
				- r * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r, 2)))
				/ (Math.pow((xp - a), 2) + Math.pow((yp - b), 2))) + b;

		Double x2 = ((Math.pow(r, 2) * (xp - a)
				- r * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r, 2)))
				/ (Math.pow((xp - a), 2) + Math.pow((yp - b), 2))) + a;

		Double y2 = ((Math.pow(r, 2) * (yp - b)
				+ r * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r, 2)))
				/ (Math.pow((xp - a), 2) + Math.pow((yp - b), 2))) + b;

		p[0] = new PositionShape(x1, y1);
		p[1] = new PositionShape(x2, y2);

		return p;
	}

	/**
	 * Get the outer tangent between two circles with different radius. WHERE r0
	 * > r1
	 * 
	 * @param circle0
	 * @param circle1
	 * @param radio0
	 * @param radio1
	 * @return
	 */
	public static PositionShape[] tangentOuterBetweenTwoCircles(PositionShape circle0, PositionShape circle1,
			Double radio0, Double radio1) {
		// http://www.ambrsoft.com/TrigoCalc/Circles2/Circles2Tangent_.htm

		Double a = circle0.getX1();
		Double b = circle0.getY1();
		Double r0 = radio0;
		Double c = circle1.getX1();
		Double d = circle1.getY1();
		Double r1 = radio1;
		PositionShape[] p = new PositionShape[4];

		// Step 1
		Double xp = (c * r0 - a * r1) / (r0 - r1);
		Double yp = (d * r0 - b * r1) / (r0 - r1);

		// Step 2
		// Calculate outer tangency point in circle0
		Double xt1 = ((Math.pow(r0, 2) * (xp - a)
				+ r0 * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + a;

		Double yt1 = ((Math.pow(r0, 2) * (yp - b)
				- r0 * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + b;

		Double xt2 = ((Math.pow(r0, 2) * (xp - a)
				- r0 * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + a;

		Double yt2 = ((Math.pow(r0, 2) * (yp - b)
				+ r0 * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + b;

		// Step 3
		// Calculate outer tangency point in circle1
		Double xt3 = ((Math.pow(r1, 2) * (xp - c)
				+ r1 * (yp - d) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + c;

		Double yt3 = ((Math.pow(r1, 2) * (yp - d)
				- r1 * (xp - c) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + d;

		Double xt4 = ((Math.pow(r1, 2) * (xp - c)
				- r1 * (yp - d) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + c;

		Double yt4 = ((Math.pow(r1, 2) * (yp - d)
				+ r1 * (xp - c) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + d;

		p[0] = new PositionShape(xt1, yt1);
		p[1] = new PositionShape(xt2, yt2);
		p[2] = new PositionShape(xt3, yt3);
		p[3] = new PositionShape(xt4, yt4);

		return p;
	}

	/**
	 * Get the inner tangent between two circles
	 * 
	 * @param circle0
	 * @param circle1
	 * @param radio0
	 * @param radio1
	 * @return
	 */
	public static PositionShape[] tangentInnerBetweenTwoCircles(PositionShape circle0, PositionShape circle1,
			Double radio0, Double radio1) {
		// http://www.ambrsoft.com/TrigoCalc/Circles2/Circles2Tangent_.htm

		Double a = circle0.getX1();
		Double b = circle0.getY1();
		Double r0 = radio0;
		Double c = circle1.getX1();
		Double d = circle1.getY1();
		Double r1 = radio1;
		PositionShape[] p = new PositionShape[4];

		// Step 1
		Double xp = (c * r0 + a * r1) / (r0 + r1);
		Double yp = (d * r0 + b * r1) / (r0 + r1);

		// Step 2
		// Calculate outer tangency point in circle0
		Double xt1 = ((Math.pow(r0, 2) * (xp - a)
				+ r0 * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + a;

		Double yt1 = ((Math.pow(r0, 2) * (yp - b)
				- r0 * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + b;

		Double xt2 = ((Math.pow(r0, 2) * (xp - a)
				- r0 * (yp - b) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + a;

		Double yt2 = ((Math.pow(r0, 2) * (yp - b)
				+ r0 * (xp - a) * Math.sqrt(Math.pow((xp - a), 2) + Math.pow((yp - b), 2) - Math.pow(r0, 2)))
				/ (Math.pow(xp - a, 2) + Math.pow(yp - b, 2))) + b;

		// Step 3
		// Calculate outer tangency point in circle1
		Double xt3 = ((Math.pow(r1, 2) * (xp - c)
				+ r1 * (yp - d) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + c;

		Double yt3 = ((Math.pow(r1, 2) * (yp - d)
				- r1 * (xp - c) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + d;

		Double xt4 = ((Math.pow(r1, 2) * (xp - c)
				- r1 * (yp - d) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + c;

		Double yt4 = ((Math.pow(r1, 2) * (yp - d)
				+ r1 * (xp - c) * Math.sqrt(Math.pow((xp - c), 2) + Math.pow((yp - d), 2) - Math.pow(r1, 2)))
				/ (Math.pow(xp - c, 2) + Math.pow(yp - d, 2))) + d;

		p[0] = new PositionShape(xt1, yt1);
		p[1] = new PositionShape(xt2, yt2);
		p[2] = new PositionShape(xt3, yt3);
		p[3] = new PositionShape(xt4, yt4);

		return p;
	}

	/**
	 * Method to get the Euclidean distance between 2 points
	 * 
	 * @param x1
	 *            Point1
	 * @param y1
	 *            Point1
	 * @param x2
	 *            Point2
	 * @param y2
	 *            Point2
	 * @return the Euclidean distance
	 */
	public static double euclideanDistanceBetweenTwoPoints(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	// anti clockwise
	public static PositionShape getCoordinatesByAngle(PositionShape pInicial, double angleAntiClockwise,
			double hypotenuse) {

		// Hay 4 casos, cuando el angulo cae en:
		// 0-90
		// 90-180
		// 180-270
		// 270-360

		PositionShape nuevaPosition;
		double adjacente;
		double opposite;

		double x1 = 0;
		double y1 = 0;

		if (angleAntiClockwise >= 0 && angleAntiClockwise < 90) {
			opposite = Math.abs(Math.sin(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			adjacente = Math.abs(Math.cos(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			x1 = pInicial.getX1() + adjacente;
			y1 = pInicial.getY1() - opposite;
		} else if (angleAntiClockwise >= 90 && angleAntiClockwise < 180) {
			angleAntiClockwise = 180 - angleAntiClockwise;
			opposite = Math.abs(Math.sin(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			adjacente = Math.abs(Math.cos(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			x1 = pInicial.getX1() - adjacente;
			y1 = pInicial.getY1() - opposite;
		} else if (angleAntiClockwise >= 180 && angleAntiClockwise < 270) {
			angleAntiClockwise = angleAntiClockwise - 180;
			opposite = Math.abs(Math.sin(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			adjacente = Math.abs(Math.cos(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			x1 = pInicial.getX1() - adjacente;
			y1 = pInicial.getY1() + opposite;
		} else if (angleAntiClockwise >= 270 && angleAntiClockwise <= 360) {
			angleAntiClockwise = 360 - angleAntiClockwise;
			opposite = Math.abs(Math.sin(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			adjacente = Math.abs(Math.cos(Math.toRadians(angleAntiClockwise)) * hypotenuse);
			x1 = pInicial.getX1() + adjacente;
			y1 = pInicial.getY1() + opposite;
		}

		nuevaPosition = new PositionShape(x1, y1);
		return nuevaPosition;
	}

}
