package app.test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.util.GeometricShapeFactory;

public class TestJTS {

	public static void testConvexHull(Coordinate[] coordinates) {
		Geometry lineString = new GeometryFactory().createLineString(coordinates);
		int bufferFactor = 1; // Positive buffer = dilation, Negative = erosion

		System.out.println(lineString.toText());
		System.out.println(lineString.buffer(bufferFactor).toText());
		System.out.println(lineString.convexHull().toText());
		System.out.println(lineString.convexHull().buffer(bufferFactor).toText());

		for (int i = 0; i < lineString.convexHull().buffer(5).getCoordinates().length; i++) {
			// System.out.println(geo.buffer(10).getCoordinates()[i]);
			Coordinate c = lineString.convexHull().buffer(1).getCoordinates()[i];
			// System.out.println("vertex(" + c.x + "," + c.y + ");");
		}
	}

	public static void testIntersectionTwoLines() {
		Coordinate[] coordLine1 = new Coordinate[] { new Coordinate(10, 10), new Coordinate(100, 100),
				new Coordinate(80, 20) };
		Coordinate[] coordLine2 = new Coordinate[] { new Coordinate(10, 100), new Coordinate(100, 10) };

		Geometry geoLine1 = new GeometryFactory().createLineString(coordLine1);
		Geometry geoLine2 = new GeometryFactory().createLineString(coordLine2);
		System.out.println("Line 1: " + geoLine1);
		System.out.println("Line 2: " + geoLine2);

		boolean isIntersected = geoLine1.intersects(geoLine2);
		System.out.println("Intersect? " + isIntersected);
		if (isIntersected) {
			Geometry intersection = geoLine1.intersection(geoLine2);
			System.out.println("Intersection in " + intersection.getNumGeometries() + " point: " + intersection);
		}
	}

	public static void testBufferLine() {
		Coordinate[] coordLine1 = new Coordinate[] { new Coordinate(10, 10), new Coordinate(100, 100),
				new Coordinate(80, 20) };
		double bufferDistance = 5;

		Geometry geoLine1 = new GeometryFactory().createLineString(coordLine1);

		BufferOp bufOp = new BufferOp(geoLine1);
		bufOp.setEndCapStyle(BufferOp.CAP_FLAT);
		Geometry geoBufferLine1 = bufOp.getResultGeometry(bufferDistance);
		System.out.println("Line 1: " + geoLine1);
		System.out.println("Buffer of Line 1: " + geoBufferLine1);
	}

	public static void testUnion() throws ParseException {
		Geometry g1 = new WKTReader()
				.read("LINESTRING (3236.918666724523 2086.9334016188805,3012.0738368720044 2093.8312099150057)");
		Geometry g2 = new WKTReader()
				.read("LINESTRING (3236.918666724523 2086.9334016188805,2887.962154016277 2206.2683865887807)");
		Geometry g3 = new WKTReader()
				.read("LINESTRING (3236.918666724523 2086.9334016188805,2959.017740846321 2120.785312502703)");
		Geometry g4 = new WKTReader()
				.read("LINESTRING (3236.918666724523 2086.9334016188805,2957.390555114398 2173.710079878074)");
		Geometry g5 = new WKTReader()
				.read("LINESTRING (3012.0738368720044 2093.8312099150057,2887.962154016277 2206.2683865887807)");
		Geometry g6 = new WKTReader()
				.read("LINESTRING (3012.0738368720044 2093.8312099150057,2959.017740846321 2120.785312502703)");
		Geometry g7 = new WKTReader()
				.read("LINESTRING (3012.0738368720044 2093.8312099150057,2957.390555114398 2173.710079878074)");
		Geometry g8 = new WKTReader()
				.read("LINESTRING (2887.962154016277 2206.2683865887807,2959.017740846321 2120.785312502703)");
		Geometry g9 = new WKTReader()
				.read("LINESTRING (2887.962154016277 2206.2683865887807,2957.390555114398 2173.710079878074)");
		Geometry g10 = new WKTReader()
				.read("LINESTRING (2887.962154016277 2206.2683865887807,2957.390555114398 2173.710079878074)");
		Geometry g11 = new WKTReader()
				.read("LINESTRING (2959.017740846321 2120.785312502703,2957.390555114398 2173.710079878074)");

		Geometry union = g1.union(g2).union(g3).union(g4).union(g5).union(g6).union(g7).union(g8).union(g9).union(g10)
				.union(g11).union();

		System.out.println(union);
	}

	public static void testIntersectionTwoPoints() {
		Geometry geo1 = new GeometryFactory().createPoint(new Coordinate(500, 500)).buffer(50);
		Geometry geo2 = new GeometryFactory().createPoint(new Coordinate(550, 500)).buffer(50);
		// GetBoundary get the boundary of this line
		System.out.println(geo1.getBoundary().intersection(geo2.getBoundary()));
	}

	public static void testIntersectionPointLine() {
		Coordinate[] coordLine1 = new Coordinate[] { new Coordinate(10, 10), new Coordinate(100, 200) };
		Geometry geoLine1 = new GeometryFactory().createLineString(coordLine1).buffer(10);

		Geometry circle = new GeometryFactory().createPoint(new Coordinate(70, 80)).buffer(60);

		System.out.println("Line " + geoLine1);
		System.out.println("point " + circle);

		boolean isIntersected = geoLine1.intersects(circle);
		if (isIntersected) {
			Geometry intersection = geoLine1.intersection(circle);
			System.out.println("Intersection in " + intersection.getNumPoints() + " points: " + intersection);
			
			for (int i = 0; i < intersection.getNumPoints(); i++) {
				System.out.println(intersection.getCoordinates()[i]);
			}
		} else {
			System.out.println("not intersection");
		}
	}

	public static void testPolygon() {
		Coordinate[] coordinates = new Coordinate[] { new Coordinate(582.1881, 375.3532),
				new Coordinate(541.7478, 376.59384), new Coordinate(519.42523, 396.81665),
				new Coordinate(532.2052, 381.44177), new Coordinate(531.91254, 390.96075) };

		Geometry buffer = new GeometryFactory().createLineString(coordinates);
		Polygon poli = (Polygon) buffer;
		System.out.println(poli);
		System.out.println(poli.getExteriorRing());
		System.out.println(poli.getInteriorRingN(0));
		System.out.println(poli.getNumInteriorRing());
		System.out.println(poli.getNumGeometries());
		System.out.println(poli.getGeometryN(0));
	}

	public static void createArc() {
		GeometricShapeFactory gsf = new GeometricShapeFactory();

		LineString lineString = gsf.createArc(0, 1);

		System.out.println(lineString);
	}

	public static void main(String[] args) {

		testIntersectionPointLine();
//		testPolygon();
		// createArc();
		// testIntersectionTwoLines();
		// testBufferLine();
		// testConvexHull();
	}

	public static void main2(String[] args) throws ParseException {

		// List<Coordinate> coord = new ArrayList<Coordinate>();
		// coord.add(new Coordinate(200, 100));
		// coord.add(new Coordinate(800, 250));
		// coord.add(new Coordinate(200, 400));
		// coord.add(new Coordinate(550, 700));
		//
		// //
		// VORONOI------------------------------------------------------------------------------
		// VoronoiDiagramBuilder voronoi = new VoronoiDiagramBuilder();
		// voronoi.setClipEnvelope(new Envelope(0, 1000, 0, 1000));
		// voronoi.setSites(coord);
		// voronoi.setTolerance(0.10);
		// Geometry voronoiResult = voronoi.getDiagram(new GeometryFactory());
		// System.out.println(voronoiResult);
		// for (int i = 0; i < voronoiResult.getNumGeometries(); i++) {
		// // Polygon poly = (Polygon) voronoiResult.getGeometryN(i);
		// }
		// -------------------------------------------------------------------------------------

	}

}
