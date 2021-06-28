package sumgra.application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gs.collections.impl.list.mutable.primitive.IntArrayList;

import app.gui.main.SumgraBuffer;
import sumgra.data.GraphDatabase;
import sumgra.otil.Otil;
import sumgra.query.ObjectPath;
import sumgra.query.Query;
import sumgra.queryparser.ParseException;

public class MainSumgra extends Thread {

	private static Logger logger = Logger.getLogger(MainSumgra.class);
	private static GraphDatabase graphDatabase;
//	private String pathGraphDatabase;
//	private String pathQueryGraph;
//	private int[] idNodeConstraints;
//	private int sleepTimeBacktracking;
	public static SumgraBuffer sumgraBuffer;

	/**
	 * @param pathMainGraph
	 * @param pathQueryGraph
	 * @param idNodeConstraints
	 * @param sleepTimeBacktracking
	 */
	public MainSumgra() {
	}

	@Override
	public void run() {
		try {
			logger.info("Start SuMGra process");
			beginProcess();
			logger.info("Finish SuMGra process");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void beginProcess() throws ParseException, IOException, InterruptedException {
//		logger.info(pathGraphDatabase  +" - "+ idNodeConstraints+" - "+ sleepTimeBacktracking);
		graphDatabase = new GraphDatabase();
		Query q = new Query(graphDatabase.property_stat, graphDatabase, graphDatabase.pathQueryGraph);
		Vector<ObjectPath> p = q.getQueryStructure();
		IntArrayList projection = q.selectVariableProjection();
		// Backtracking and Buffer process
		logger.info("Init backtratking quering");
		graphDatabase.query(projection, p, q);
	}

	/*
	 * public static void testQuery(){ String file2read =
	 * "/Users/dinoienco/Documents/workspace/Amber/query_example.txt";
	 * BufferedReader br = null; InputStreamReader isr = null; try { isr = new
	 * InputStreamReader(new FileInputStream(file2read)); br = new
	 * BufferedReader(isr); } catch (FileNotFoundException e) {
	 * e.printStackTrace(); } String cur; Query q = new Query(); try {
	 * while((cur=br.readLine()) != null){ String[] elements= cur.split(" ");
	 * int source = Integer.parseInt(elements[0]); int sink =
	 * Integer.parseInt(elements[1]); String [] preds = elements[2].split(",");
	 * short[] seq = new short[preds.length]; for (int i=0; i<preds.length;++i)
	 * { q.addTriple(""+source, ""+sink, Short.parseShort(preds[i]), true,
	 * true); } //System.out.println("add: "+Arrays.toString(seq)); }
	 * isr.close(); br.close(); }catch(Exception e){ e.printStackTrace(); }
	 * IntArrayList variables = new IntArrayList(); variables.add(1);
	 * variables.add(2); variables.add(3); variables.add(4); variables.add(5);
	 * q.setVariables(variables); int[] order = {2,3,5,1,4}; Vector<ObjectPath>
	 * p = q.getQueryStructure(order); for (ObjectPath op: p){
	 * System.out.println(op); }
	 * 
	 * }
	 */
	public static void testOtil() {
		String file2read = "examples/example_otil.txt";
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file2read));
			br = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String cur;
		Otil o = new Otil();
		try {
			while ((cur = br.readLine()) != null) {
				String[] elements = cur.split(" ");
				// int direction = Integer.parseInt(elements[0]);
				int neigh = Integer.parseInt(elements[1]);
				String[] preds = elements[2].split(",");
				short[] seq = new short[preds.length];
				for (int i = 0; i < preds.length; ++i)
					seq[i] = Short.parseShort(preds[i]);
				// System.out.println("add: "+Arrays.toString(seq));
				o.add(seq, neigh);
			}
			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		o.print();
		o.print2(1);

		short[] query1 = { 1, 3 };
		short[] query2 = { 4 };
		short[] query3 = { 5 };

		System.out.println("=============");
		System.out.println(o.query(query1));
		System.out.println(o.query(query2));
		System.out.println(o.query(query3));
		System.out.println("=============");
		short[] lab = { 2, 3, 4 };
		System.out.println(o.checkIFNeighExists(lab, 2));
	}

//	public String getPathMainGraph() {
//		return pathGraphDatabase;
//	}
//
//	public void setPathMainGraph(String pathMainGraph) {
//		this.pathGraphDatabase = pathMainGraph;
//	}
//
//	public String getPathQueryGraph() {
//		return pathQueryGraph;
//	}
//
//	public void setPathQueryGraph(String pathQueryGraph) {
//		this.pathQueryGraph = pathQueryGraph;
//	}
//
//	public int[] getConstraints() {
//		return idNodeConstraints;
//	}

}
