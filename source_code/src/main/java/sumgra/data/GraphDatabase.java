/**
 * 
 * Class that implements the Graph Database model. This class contains all the procedure 
 * to transform an RDF dataset into its corresponding mulitgraph representation 
 * 
 * 
 * 
 * */

package sumgra.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectShortHashMap;
import com.gs.collections.impl.map.mutable.primitive.ShortIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.ShortObjectHashMap;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;

import app.graph.structure.Relationships;
import app.gui.main.SumgraBuffer;
import app.gui.query.GuiQuery;
import sumgra.org.khelekore.prtree.NDMBRConverter;
import sumgra.org.khelekore.prtree.PRTree;
import sumgra.org.khelekore.prtree.SimplePointND;
import sumgra.otil.Otil;
import sumgra.query.LinkObjectPath;
import sumgra.query.ObjectPath;
import sumgra.query.Query;

public class GraphDatabase {

	public static Logger logger = Logger.getLogger(GraphDatabase.class);
	/*
	 * the R-Tree employed to store the synonpsis representaiton of the vertex
	 */
	public PRTree prtree;
	/*
	 * An inverted list that given a synopsis return the list of the vertices
	 * corresponding a that synopsis In the worst case this list contains as
	 * many synopsis as the number of vertices in the multigraph. Practically,
	 * many vertices have the same synopsis
	 * 
	 */
	public HashMap<SimplePointND, IntArrayList> compacted_synopsis;

	/*
	 * HashTable to map string to integer (internal representation) and
	 * viceversa Two hash Tables for Subjects/Objects (String -> Int and Int ->
	 * String) Two hash Tables for Predicates (String -> Short and Short ->
	 * String) We made the hypothesis that there are no that 64K predicates in
	 * our RDF dataset
	 */
	public ObjectIntHashMap<String> string2intSO;
	public IntObjectHashMap int2stringSO;
	public ObjectShortHashMap<String> string2ShortP;
	private ShortObjectHashMap Short2stringP;

	public FastList<int[]> results = new FastList<int[]>();

	public static String pathGraphDatabase;
	public static String pathQueryGraph;
	
	public static int[] constraintsVertices;
	public static int sleepTimeBacktraking;
	public static SumgraBuffer sumgraBuffer;

	/*
	 * The neigborhood indexing schema employed to retrieve the list of
	 * neighbors given a node and a particular set of predicates.
	 * 
	 */
	IntObjectHashMap<Otil> neigh_index;

	/*
	 * An Hash Table that supply has the selectivity for each of the predicate
	 * in the RDF data
	 */
	public ShortIntHashMap property_stat;

	/*
	 * @param synopsis: a synopsis representation of a vertex
	 * 
	 * @return the list of vertices, in the RDF multigraph, that match the
	 * specific synopsis
	 */

	public IntHashSet getNodeList(SimplePointND synopsis) {
		List<SimplePointND> resultNodes = new FastList<SimplePointND>();
		prtree.getNotDominatedPoints(synopsis, resultNodes);
		IntHashSet possible_initial_matches = new IntHashSet();
		for (SimplePointND t : resultNodes) {
			possible_initial_matches.addAll(compacted_synopsis.get(t));
		}
		return possible_initial_matches;
	}

	/*
	 * first Integer : Subject/Object ID second Integer : Predicate ID
	 * LinkedList<Integer> : list of nodes connected by the specific predicate
	 */

	public void buildSynopsis(String fileName, int n_synopsis,
			HashMap<SimplePointND, IntArrayList> compacted_synopsis) {
		short[][] synopsis;
		synopsis = new short[n_synopsis][Settings.N_FEAT];
		// Setting the feature 4: minimum index of lexicographically ordered
		// edge dimensions
		for (int i = 0; i < synopsis.length; ++i)
			synopsis[i][3] = Short.MIN_VALUE;

		// feature 1: cardinality of vertex signature
		// feature 2: number of unique dimension in the vertex signature (f2 in
		// amber)
		// feature 3: number of all occurrences of the dimensions (with
		// repetition)
		// feature 4: minimum index of lexicographically ordered edge dimensions
		// (f3 in amber)
		// feature 5: maximum index of lexicographically ordered edge dimensions
		// (f4 in amber)
		// feature 6: maximum cardinality of the vertex sub-signature (f1 in
		// amber)

		IntObjectHashMap predsSetIn = new IntObjectHashMap(); // positive -
																// object

		// Initializing the predsSetIn array
		for (int i = 0; i < synopsis.length; ++i) {
			predsSetIn.put(i, new HashSet<Short>());
		}

		String file2read = fileName;
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file2read));
			br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String cur;
		try {
			int k = 0;
			while ((cur = br.readLine()) != null) {
				if (k % Settings.INIT_SIZE == 0)
					k++;
				String[] elements = cur.split(" ");
				int subj = Integer.parseInt(elements[0]);// Subjet
				int obj = Integer.parseInt(elements[1]);// Object
				String[] preds = elements[2].split(",");// Predicats
				short[] dims = new short[preds.length];
				// System.out.println("subj " + subj);
				// System.out.println("obj " + obj);
				for (int j = 0; j < preds.length; ++j) {
					dims[j] = Short.parseShort(preds[j]);
					// System.out.println("pred " + preds[j]);
				}
				((Otil) neigh_index.get(subj)).add(dims, obj);
				((Otil) neigh_index.get(obj)).add(dims, subj);
				// System.out.println("---");

				// feature 1 - cardinality of vertex signature (num of edges
				// sets)
				synopsis[obj][0] = (short) (synopsis[obj][0] + 1);
				synopsis[subj][0] = (short) (synopsis[subj][0] + 1);

				// feature 3 - number of all occurrences of the dimensions (with
				// repetitions)
				synopsis[obj][2] = (short) (synopsis[obj][2] + preds.length);
				synopsis[subj][2] = (short) (synopsis[subj][2] + preds.length);

				// feature 6 - maximum cardinality
				if (synopsis[obj][5] < preds.length)
					synopsis[obj][5] = (short) preds.length;
				if (synopsis[subj][5] < preds.length)
					synopsis[subj][5] = (short) preds.length;

				for (int j = 0; j < preds.length; ++j) {
					short pred = Short.parseShort(preds[j]);
					((HashSet<Short>) predsSetIn.get(obj)).add(pred);// undirected
																		// graph
					((HashSet<Short>) predsSetIn.get(subj)).add(pred);// undirected
																		// graph

					// feature 4: minimum index value of the edge type
					if (synopsis[obj][3] < (pred * -1))
						synopsis[obj][3] = (short) (pred * -1);

					if (synopsis[subj][3] < (pred * -1))
						synopsis[subj][3] = (short) (pred * -1);
					/**/

					// feature 5: maximum index value of the edge type
					if (synopsis[obj][4] < pred)
						synopsis[obj][4] = pred;

					if (synopsis[subj][4] < pred)
						synopsis[subj][4] = pred;
				}
			}
			isr.close();
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// feature 2: number of unique dimension in the vertex signature
		// Construction of the compacted_synopsis
		for (int i = 0; i < synopsis.length; ++i) {
			// feature 2
			synopsis[i][1] = (short) ((HashSet<Short>) predsSetIn.get(i)).size();
			// synopsis[i][1+Settings.N_FEAT] = (short) ((HashSet<Short>)
			// predsSetOut.get(i)).size();
			// Construction of compacted_synopsis
			SimplePointND temp = new SimplePointND(synopsis[i]);
			if (!compacted_synopsis.containsKey(temp))
				compacted_synopsis.put(temp, new IntArrayList());
			compacted_synopsis.get(temp).add(i);
		}
	}

	public void query(IntArrayList projection, Vector<ObjectPath> path, Query queryStruct) throws InterruptedException {
		// IntObjectHashMap<IntHashSet> sats_list = new
		// IntObjectHashMap<IntHashSet>();
		logger.info("here in query");
		IntObjectHashMap buffer_neigh = new IntObjectHashMap();
		List<SimplePointND> resultNodes = new FastList<SimplePointND>();
		IntHashSet possible_initial_matches = new IntHashSet();
		 System.out.println("path size: "+path.size());
		ObjectPath constraint = path.get(0);

		// otherwise if it is a variable ?x, query the R-TREE structure and
		// retrieve the possible matches
		SimplePointND query_r_tree = queryStruct.getSynopsis(constraint.id);
		prtree.getNotDominatedPoints(query_r_tree, resultNodes);
		for (SimplePointND t : resultNodes) {
			possible_initial_matches.addAll(compacted_synopsis.get(t));
		}

		int[] current_sol = new int[queryStruct.size()];
		MutableIntIterator itr = possible_initial_matches.intIterator();
		while (itr.hasNext()) {
			int current_val = itr.next();
			current_sol[constraint.id] = current_val;
			backtrackingCheck(current_sol, 1, path, queryStruct, buffer_neigh, projection, "\t");
			buffer_neigh.remove(constraint.id);
		}
		sumgraBuffer.finishProcess();
	}

	// DELETE-manage
	/* The heuristics to order the nodes of the query from the 2 to the last */
	// private boolean checkSatNodes(int vertex_id, ObjectPath constraint,
	// IntObjectHashMap<IntHashSet> sats_list, IntObjectHashMap<Integer>
	// notVariable){
	// boolean meet_constraints = true;
	// IntObjectHashMap<IntHashSet> loc_sat_list = new
	// IntObjectHashMap<IntHashSet>();
	// MutableIntIterator in_it =
	// constraint.satellites_in.keySet().intIterator();
	// while (in_it.hasNext() && meet_constraints){
	// IntHashSet temp = null;
	// int sat_id = in_it.next();
	// if (notVariable.containsKey(sat_id)){
	// meet_constraints = meet_constraints &&
	// neigh_index.get(vertex_id).checkIFNeighExists(constraint.satellites_in.get(sat_id),
	// notVariable.get(sat_id));
	// if (meet_constraints){
	// temp = new IntHashSet();
	// temp.add(notVariable.get(sat_id));
	// }
	// }else{
	// temp =
	// neigh_index.get(vertex_id).query(constraint.satellites_in.get(sat_id));
	// if (temp.size() == 0) meet_constraints = false;
	// }
	// loc_sat_list.put(sat_id, temp);
	// }
	//
	// MutableIntIterator out_it =
	// constraint.satellites_out.keySet().intIterator();
	// while (out_it.hasNext() && meet_constraints){
	// int sat_id = out_it.next();
	// IntHashSet temp = null;
	// if (notVariable.containsKey(sat_id)){
	// meet_constraints = meet_constraints &&
	// neigh_index.get(vertex_id).checkIFNeighExists(constraint.satellites_out.get(sat_id),
	// notVariable.get(sat_id));
	// if (meet_constraints){
	// temp = new IntHashSet();
	// temp.add(notVariable.get(sat_id));
	// }
	// }else{
	// temp =
	// neigh_index.get(vertex_id).query(constraint.satellites_out.get(sat_id));
	// if (temp.size() == 0) meet_constraints = false;
	// }
	//
	// if (loc_sat_list.containsKey(sat_id) && meet_constraints){
	// loc_sat_list.get(sat_id).retainAll(temp);
	// if (loc_sat_list.get(sat_id).size() == 0) meet_constraints = false;
	// }else{
	// loc_sat_list.put(sat_id, temp);
	// }
	// }
	//
	// if (meet_constraints){
	// MutableIntIterator it = loc_sat_list.keySet().intIterator();
	// while (it.hasNext()){
	// int val = it.next();
	// sats_list.put(val, loc_sat_list.get(val));
	// }
	// }
	// return meet_constraints;
	// }

	// CAUTION DELETE
	private short[] generateLabelsDirection(short[] temp) {
		short[] result = new short[temp.length];
		for (int i = 0; i < temp.length; ++i)
			result[i] = (short) (temp[i]);
		return result;
	}

	// CD
	// public void combineCoreAndSatellites(int[] current_sol,
	// IntObjectHashMap<IntHashSet> sat_list, FastList<int[]> results,
	// IntArrayList projection){
	// //System.out.println(Arrays.toString(current_sol));
	// //int[] temp_sol = Arrays.copyOf(current_sol, current_sol.length);
	// int[] sat_position = sat_list.keySet().toArray();
	// int sat_idx = 0;
	// MutableIntIterator itr =
	// sat_list.get(sat_position[sat_idx]).intIterator();
	// while (itr.hasNext()){
	// current_sol[sat_position[sat_idx]] = itr.next();
	// generateSol(current_sol, sat_idx+1, sat_position, results, sat_list,
	// projection);
	// }
	// }

	public void generateSol(int[] current_sol, int sat_idx, int[] sat_position, FastList<int[]> results,
			IntObjectHashMap<IntHashSet> sat_list, IntArrayList projection) {
		if (sat_idx == sat_position.length) {
			int[] proj_res = new int[projection.size()];
			MutableIntIterator itr = projection.intIterator();
			int i = 0;
			while (itr.hasNext()) {
				proj_res[i++] = current_sol[itr.next()];
			}
			// System.out.println(current_sol.length+" "+proj_res.length);
			// System.out.println(Arrays.toString(proj_res));
			results.add(proj_res);
			/*
			 * if (results.size() % 10 == 0){ System.out.println(
			 * "results.size(): "+results.size()); }
			 */
		} else {
			MutableIntIterator itr = sat_list.get(sat_position[sat_idx]).intIterator();
			while (itr.hasNext()) {
				current_sol[sat_position[sat_idx]] = itr.next();
				generateSol(current_sol, sat_idx + 1, sat_position, results, sat_list, projection);
			}
		}
	}

	private boolean validConstraints(int[] sol) {
		// As sol[] and constraintsVertices[] have the same lengths
		for (int index = 0; index < sol.length; index++) {
			if (constraintsVertices[index] != -1 && constraintsVertices[index] != sol[index]) {
				return false;
			}
		}

		return true;

		// for (int c : constraintsVertices) {
		// if (!valid(c, sol)) {
		// return false;
		// }
		// }
		// return true;
	}

	private boolean valid(int constra, int[] sol) {
		for (int i : sol) {
			if (i == constra) {
				return true;
			}
		}
		return false;
	}

	// main loop
	public void backtrackingCheck(int[] current_sol, int level, Vector<ObjectPath> path, Query queryStruct,
			IntObjectHashMap buffer_neigh, IntArrayList projection, String offset) throws InterruptedException {

		if (level == path.size()) {
			int[] sol = Arrays.copyOfRange(current_sol, 0, current_sol.length);
			if (validConstraints(sol)) {
				sumgraBuffer.putOn(sol);
			}
			Thread.sleep(sleepTimeBacktraking);
			return;
		}

//		logger.info("level "+level);
		ObjectPath constraints = path.get(level);
		LinkObjectPath link = constraints.previous_links_cores.get(0);

		IntHashSet current_match_set = null;
		current_match_set = neigh_index.get(current_sol[path.get(link.rank_previous_id).id]).query(link.dims);
		current_match_set.retainAll(queryStruct.possible_candidates.get(constraints.id));

		// check homomorphism - CHECK WITH DINO
		for (int i = constraints.previous_links_cores.size(); --i >= 1 && current_match_set.size() > 0;) {
			int prev_node_id = path.get(constraints.previous_links_cores.get(i).rank_previous_id).id;
			int prev_vertex_id = current_sol[prev_node_id];
			IntHashSet constraint_match_set = null;

			SimplePointND transformed = new SimplePointND(constraints.previous_links_cores.get(i).dims);
			if (buffer_neigh.containsKey(prev_node_id)) {
				if (((HashMap<SimplePointND, IntHashSet>) buffer_neigh.get(prev_node_id)).containsKey(transformed)) {
					constraint_match_set = ((HashMap<SimplePointND, IntHashSet>) buffer_neigh.get(prev_node_id))
							.get(transformed);
				} else {
					constraint_match_set = neigh_index.get(prev_vertex_id)
							.query(constraints.previous_links_cores.get(i).dims);
					((HashMap<SimplePointND, IntHashSet>) buffer_neigh.get(prev_node_id)).put(transformed,
							constraint_match_set);
				}
			} else {
				constraint_match_set = neigh_index.get(prev_vertex_id)
						.query(constraints.previous_links_cores.get(i).dims);
				buffer_neigh.put(prev_node_id, new HashMap<SimplePointND, IntHashSet>());
				((HashMap<SimplePointND, IntHashSet>) buffer_neigh.get(prev_node_id)).put(transformed,
						constraint_match_set);
			}
			current_match_set.retainAll(constraint_match_set);
		}

		// BEGIN ISO
		IntHashSet already_considered = new IntHashSet();
		for (int i = 0; i < constraints.id; ++i)
			already_considered.add(current_sol[i]);

		current_match_set.removeAll(already_considered);
		// END ISO

		MutableIntIterator it2 = current_match_set.intIterator();
		while (it2.hasNext()) {
			int current_val = it2.next();
			current_sol[constraints.id] = current_val;
			backtrackingCheck(current_sol, level + 1, path, queryStruct, buffer_neigh, projection, offset + "\t");
			buffer_neigh.remove(constraints.id);
		}
	}

	/*
	 * @param fileName The name of the .nt file that contains the RDF dataset
	 */
	public GraphDatabase() throws IOException {

		logger.info("STEP 1");
		compacted_synopsis = new HashMap<SimplePointND, IntArrayList>();

		string2intSO = new ObjectIntHashMap<String>(Settings.INIT_SIZE);
		string2ShortP = new ObjectShortHashMap<String>(Settings.INIT_SIZE);
		property_stat = new ShortIntHashMap(Settings.INIT_SIZE);

		int2stringSO = new IntObjectHashMap(Settings.INIT_SIZE);
		Short2stringP = new ShortObjectHashMap(Settings.INIT_SIZE);
		neigh_index = new IntObjectHashMap<Otil>(Settings.INIT_SIZE);
		logger.info("STEP 2");
		LoadRDFMappingAndWriteInternalFormat(pathGraphDatabase, string2intSO, string2ShortP, int2stringSO, Short2stringP);
		logger.info("STEP 3");
		MutableIntSet keys = int2stringSO.keySet();
		MutableIntIterator itr = keys.intIterator();
		while (itr.hasNext()) {
			neigh_index.put(itr.next(), new Otil());// OTIL tree for each vertex
		}

		// KEY Y VALUES DEBEN SER IGUALES
//		 for (int i = 0; i < int2stringSO.size(); ++i) {
//		 logger.info("key: " + i + " value " + int2stringSO.get(i));
//		 }
		 System.out.println("size neigh_index " + neigh_index.size());

		buildSynopsis(pathGraphDatabase, int2stringSO.size(), compacted_synopsis);
		prtree = new PRTree(new NDMBRConverter(Settings.SYNOPSIS_SIZE), 10);
		prtree.load(compacted_synopsis.keySet());

	}

	private void LoadRDFMappingAndWriteInternalFormat(String fileName, ObjectIntHashMap<String> string2intSO,
			ObjectShortHashMap<String> string2ShortP, IntObjectHashMap<String> int2stringSO,
			ShortObjectHashMap<String> Short2stringP) {
		IntObjectHashMap<IntObjectHashMap<ShortArrayList>> adjlistOut;

		// adjlistIn = new IntObjectHashMap(INIT_SIZE);
		adjlistOut = new IntObjectHashMap<IntObjectHashMap<ShortArrayList>>(Settings.INIT_SIZE);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String cur;
		int i = 0;
		try {

			while ((cur = br.readLine()) != null) {
				

				// Amber
				// A <http://dbpedia.org/property/ushrProperty> C .

				// Sumgra
				// 0 1 1,2,3,4...
				String[] temp = cur.split(" ");
				if (temp[0].equals("#"))
					continue;
				// for (int j = 3; j < temp.length - 1; ++j) {
				// temp[2] = temp[2] + " " + temp[j];
				// }

				// temp[0] Source
				// temp[1] Target
				// temp[2] Predicats

//				Relationships edgesTwoNodes = new Relationships(temp[0], temp[1]);
//				relacionDosNodosEdges.put(edgesTwoNodes, temp[2]);

				if (!string2intSO.containsKey(temp[0])) {
					int key = string2intSO.size();

					// string2intSO.put(temp[0], Integer.valueOf(temp[0]));
					// int2stringSO.put(Integer.valueOf(temp[0]), temp[0]);

					string2intSO.put(temp[0], key);
					int2stringSO.put(key, temp[0]);
				}

				if (!string2intSO.containsKey(temp[1])) {
					int key = string2intSO.size();

					// string2intSO.put(temp[1], Integer.valueOf(temp[1]));
					// int2stringSO.put(Integer.valueOf(temp[1]), temp[1]);

					string2intSO.put(temp[1], key);
					int2stringSO.put(key, temp[1]);
				}

				if (!string2ShortP.containsKey(temp[2])) {
					short key = (short) (string2ShortP.size() + 1);
					string2ShortP.put(temp[2], key);
					Short2stringP.put(key, temp[2]);
					property_stat.put(key, 0);
				}

				int subj = string2intSO.get(temp[0]);
				int obj = string2intSO.get(temp[1]);
				short pred = string2ShortP.get(temp[2]);
				property_stat.put(pred, property_stat.get(pred) + 1);

				if (!adjlistOut.containsKey(subj)) {
					adjlistOut.put(subj, new IntObjectHashMap<ShortArrayList>());
					adjlistOut.get(subj).put(obj, new ShortArrayList());
					adjlistOut.get(subj).get(obj).add(pred);
				} else if (!adjlistOut.get(subj).containsKey(obj)) {
					adjlistOut.get(subj).put(obj, new ShortArrayList());
					adjlistOut.get(subj).get(obj).add(pred);
				} else {
					adjlistOut.get(subj).get(obj).add(pred);
				}

				++i;
//				logger.info("i: "+i);
				if (i % Settings.INIT_SIZE == 0) {
					logger.info("garbage collector: "+i);
					System.gc();
				}
			}
			logger.info("relationships count: "+i);
			// isr.close();
			br.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// output file TEMP

		// String outFile = fileName+Settings.TEMP_EXTENSION;
		// OutputStreamWriter osr = null;
		// try {
		// osr = new OutputStreamWriter(new FileOutputStream(outFile));
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// PrintWriter out = new PrintWriter(osr, true);
		//
		// MutableIntIterator itr = adjlistOut.keySet().intIterator();
		//
		// while(itr.hasNext()) {
		// int source = itr.next();
		// IntObjectHashMap<ShortArrayList> neighs = adjlistOut.get(source);
		// MutableIntIterator itr2 = neighs.keySet().intIterator();
		// while(itr2.hasNext()) {
		// int sink = itr2.next();
		// ShortArrayList preds = neighs.get(sink);
		// String preds_to_print = "";
		// for (int j=0; j < preds.size()-1; ++j){
		// preds_to_print +=preds.get(j)+",";
		// }
		// preds_to_print+=preds.get(preds.size()-1);
		// out.println(source+" "+sink+" "+preds_to_print);
		// //System.out.println(source+" "+sink+" "+preds_to_print);
		// }
		//
		// }
		// out.close();
		// isr.close();
	}

//	public static Map<Relationships, String> getRelacionDosNodosEdges() {
//		return relacionDosNodosEdges;
//	}
//
//	public static void setRelacionDosNodosEdges(Map<Relationships, String> relacionDosNodosEdges) {
//		GraphDatabase.relacionDosNodosEdges = relacionDosNodosEdges;
//	}

}
