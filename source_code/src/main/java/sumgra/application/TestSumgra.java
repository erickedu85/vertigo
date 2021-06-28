package sumgra.application;

import app.gui.main.SumgraBuffer;
import sumgra.data.GraphDatabase;

public class TestSumgra {

	public static void main(String[] args) {

		String pathMainGraph = "data/panama/relationships.txt";
		String pathQueryGraph = "data/panama/query.txt";
		
//		String pathMainGraph = "data/dblp_vis_bd/relationships.txt";
//		String pathQueryGraph = "data/dblp_vis_bd/query.txt";

		SumgraBuffer sumgraBuffer = new SumgraBuffer(100);
		GraphDatabase.sumgraBuffer = sumgraBuffer;
		int[] constraints = {-1,-1,-1}; //same lenght (num nodes query)
//		int[] constraints = {-1,-1,-1}; //same lenght (num nodes query)
		int sleepTimeBacktracking = 1;

		// In this case the buffer is filled and their is not a pickUp call
		
		//FALTA INSTANCIAR
		GraphDatabase.pathGraphDatabase = pathMainGraph;
		GraphDatabase.pathQueryGraph = pathQueryGraph;
		GraphDatabase.constraintsVertices = constraints;
		GraphDatabase.sleepTimeBacktraking = sleepTimeBacktracking;
				
		//pathMainGraph, pathQueryGraph, constraints, sleepTimeBacktracking
		
		MainSumgra main = new MainSumgra();
		main.start();
	}
}
