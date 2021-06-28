package app.test;

import app.utils.GeoAnalytic;
import app.utils.GraphUtil;

public class TestTangent {

	public static void main(String[] args) {

		//
		double[] nuevosRadius = GraphUtil.divideDiameterByN(6, 50);
		for (double d : nuevosRadius) {
			// if (d > 0)
			// System.out.println(d);
		}
		//
		//
		//
		//
		double[][] d = GeoAnalytic.tangentPoints2Circles(50, 50, 0, 150, 50, 0);

		System.out.println("POINT(" + d[0][0] + " " + d[0][1] + ")");
		System.out.println("POINT(" + d[1][0] + " " + d[1][1] + ")");
		System.out.println("POINT(" + d[0][2] + " " + d[0][3] + ")");
		System.out.println("POINT(" + d[1][2] + " " + d[1][3] + ")");
		//
		// System.out.println(GeoAnalytic.euclideanDistanceBetweenTwoPoints(d[0][0],
		// d[0][1], d[1][0], d[1][1]));
		// System.out.println(GeoAnalytic.euclideanDistanceBetweenTwoPoints(d[0][2],
		// d[0][3], d[1][2], d[1][3]));
		//
		// System.out.println("-------------------------");

		// System.out.println("POINT(" + d[2][0] + " " + d[2][1] + ")");
		// System.out.println("POINT(" + d[2][2] + " " + d[2][3] + ")");
		// System.out.println("POINT(" + d[3][0] + " " + d[3][1] + ")");
		// System.out.println("POINT(" + d[3][2] + " " + d[3][3] + ")");

	}

}
