package app.test;

import java.math.BigInteger;

import app.utils.GeoUtil;
import app.utils.GraphUtil;

public class TestGeoUtil {
	public static void main(String[] args) {
		// System.out.println(GeoUtil.calculateAngleBetweenPoints(220.0, 270.0,
		// 515.0, 50.0));
		// [x1=323.7578241867688, y1=330.28527116989335, x2=411.2421758132312,
		// y2=179.71472883010665],
		// System.out.println(GeoUtil.calculateDistanceBetweenPoints(323.7578241867688,
		// 330.28527116989335,
		// 411.2421758132312, 179.71472883010665));

		// System.out.println(GeoUtil.distanceBetweenTwoPoints(215.86452689922655,
		// 389.9287199224305,220.0, 270.0));

		// System.out.println(GeoUtil.angleSmallerBetweenTwoLines(215.86452689922655,
		// 389.9287199224305, 207.8592277483178,
		// 150.6157395251251, 220.0, 270.0));

		System.out.println(GraphUtil.pairingFunction(1,2));
		System.out.println(GraphUtil.pairingFunction(2,1));
		
	}
}
 