package app.lib.ogdf;

/**
 * @author Erick Cuenca
 *
 */
public class Ogdf {

	/**
	 * 
	 * @throws UnsatisfiedLinkError
	 */
	public static void loadLibrary() throws UnsatisfiedLinkError {

		// String os = System.getProperty("os.name").toLowerCase();
		// System.out.println("OS " + os);
		// System.out.println("classloader " +
		// ClassLoader.getSystemResource("ogdf.dll").getFile());;

		// System.load(ClassLoader.getSystemResource("ogdf.dll").getFile()); //
		// Works
		// System.load(ClassLoader.getSystemResource("ogdf.dll").getPath());
		// //Works
		System.loadLibrary("lib/ogdf"); // WIN WORKS compile Visual studio

		// System.loadLibrary : absolute path (/home/erick/workspace....)
		// System.loadLibrary("ogdf.dll"); //LINUX
	}

	/**
	 * @param numNodes
	 *            the number of nodes
	 * @param nomEdges
	 *            the number of edges
	 */
	public native static void randomSimpleGraph(int numNodes, int nomEdges);

	/**
	 * 
	 * Method to create a gml file with FMMMLayout
	 * 
	 * @param gmlPathInput
	 * @param gmlPathOutput
	 * @param nodeWidth
	 * @param nodeHeight
	 * @param useHighLevelOptions
	 * @param unitEdgeLength
	 * @param newInitialPlacement
	 */
	public native static void createFMMMLayout(String gmlPathInput, String gmlPathOutput, float nodeWidth,
			float nodeHeight, boolean useHighLevelOptions, float unitEdgeLength, boolean newInitialPlacement)
			throws Exception;

	public native static void readGML(String gmlPathInput, String gmlPathOutput);

}
