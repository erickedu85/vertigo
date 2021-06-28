package app.lib.ogdf;

import org.apache.log4j.Logger;

import app.graph.structure.Edge;
import app.graph.structure.Graph;
import app.gui.main.Constants;
import app.utils.DiversParser;
import app.utils.FileUtil;
import app.utils.In;

public class Layer {

	final static Logger logger = Logger.getLogger(Layer.class);

	/**
	 * Method to create a FM3 force algorithm FROM a GML file TO a GML file O
	 * 
	 * @param pathSource
	 *            Path source of the original Graph
	 * @param pathTarget
	 *            Path target of the FM3 Graph
	 * @param nodeRadius
	 *            Radius of the vertices
	 */
	public static void createFM3Layout(String pathSource, String pathTarget, Double nodeRadius, Double unitEdgeLength) {
		try {
			Ogdf.loadLibrary();
		} catch (UnsatisfiedLinkError u) {
			logger.error(u.getMessage());
		}
		try {

			boolean newInitialPlacement = true;
			boolean useHighLevelOptions = false;

			Ogdf.createFMMMLayout(pathSource, pathTarget, nodeRadius.floatValue(), nodeRadius.floatValue(),
					useHighLevelOptions, unitEdgeLength.floatValue(), newInitialPlacement);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	/**
	 * Method to execute the FM3 force to a Graph G
	 * 
	 * @param graph
	 *            Graph to execute the FM3
	 * @return a Graph with the FM3
	 */
	public static Graph executeFMMMLayout(Graph graph) {

		String pathSource = Constants.OGDF_PATH_TEMP_FM3;
		DiversParser.saveGML(graph, pathSource);

		// Creating FM3 layer to the path_temp_layout_fm3
		String pathTarget = Constants.OGDF_PATH_TEMP_LAYOUT_FM3;
		Layer.createFM3Layout(pathSource, pathTarget, 5.0d, 10.0d);

		// Charging in a new graph from the path_temp_layout_fm3
		In in = new In(pathTarget);
		Graph graphFM3 = new Graph(in);
		in.close();
		// Deleting the files temp and layout created
		// FileUtil.deleteFilesFromPath(Constants.PATH_TEMP_LAYOUT_FM3);
		deleteTempGraphFile();
		return graphFM3;
	}

	public static void deleteTempGraphFile() {
		FileUtil.deleteFilesFromPath(Constants.OGDF_PATH_TEMP_FM3);
		FileUtil.deleteFilesFromPath(Constants.OGDF_PATH_TEMP_LAYOUT_FM3);
	}

}
