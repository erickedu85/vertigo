package app.test;

import app.graph.structure.PositionShape;
import app.utils.GeoAnalytic;

public class TestGeoAnalytic {

	public static void main(String[] args) {
		// Double result = GeoAnalytic.distanceTwoPoints(new
		// PositionShape((double) 2, (double) 5),
		// new PositionShape((double) 7, (double) 1));

		PositionShape[] result = GeoAnalytic.tangentInnerBetweenTwoCircles(new PositionShape((double) 50, (double) 100),
				new PositionShape((double) 150, (double) (50)), (double) 50, (double)50);
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
		System.out.println(result[3]);
		
		
		
		// PositionShape[] result = GeoAnalytic.tangentLineToPoint(new
		// PositionShape((double) 100, (double) -75),
		// new PositionShape((double) 225, (double) (-100)), (double)25);

		// PositionShape[] result =
		// GeoAnalytic.tangentOuterBetweenTwoCircles(new PositionShape((double)
		// -4, (double) -2),
		// new PositionShape((double) 3, (double) 4), (double) 3, (double) 3);
		// System.out.println(result[0]);
		// System.out.println(result[1]);
		// System.out.println(result[2]);
		// System.out.println(result[3]);

	}

}
