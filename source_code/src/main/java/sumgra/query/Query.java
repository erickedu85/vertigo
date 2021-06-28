package sumgra.query;

import com.gs.collections.api.iterator.IntIterator;
import com.gs.collections.api.iterator.MutableIntIterator;
import com.gs.collections.api.iterator.MutableShortIterator;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.api.set.primitive.MutableShortSet;
import com.gs.collections.impl.map.mutable.primitive.IntIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import com.gs.collections.impl.map.mutable.primitive.ShortIntHashMap;
import com.gs.collections.impl.set.mutable.primitive.ShortHashSet;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import com.gs.collections.impl.map.mutable.primitive.IntBooleanHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;

import sumgra.data.GraphDatabase;
import sumgra.data.Settings;
import sumgra.org.khelekore.prtree.SimpleMBR;
import sumgra.org.khelekore.prtree.SimplePointND;

public class Query implements Runnable {

	private IntObjectHashMap<IntHashSet> in_list; 
	private HashMap< Pair,ShortHashSet > pair2dims;
	private HashMap< Pair,short[] > pair2dims_vec;
	private ArrayList<String> headerList;
	private IntObjectHashMap<String> id2token;
	private ObjectIntHashMap<String> token2id;
	private ShortIntHashMap dim_stat;
	private GraphDatabase g;
	public IntObjectHashMap<IntHashSet> possible_candidates = new IntObjectHashMap<IntHashSet>();
	
	public Query(ShortIntHashMap property_stat, GraphDatabase g){
		in_list = new IntObjectHashMap<IntHashSet>();
		pair2dims = new HashMap< Pair,ShortHashSet >();
		pair2dims_vec = new HashMap< Pair,short[] >();
		headerList = new ArrayList<String>();
		id2token = new IntObjectHashMap<String>();
		token2id = new ObjectIntHashMap<String>();
		dim_stat = property_stat;
		this.g = g;
	}

	public Query(ShortIntHashMap property_stat, GraphDatabase g, String file) {
		in_list = new IntObjectHashMap<IntHashSet>();
		pair2dims = new HashMap< Pair,ShortHashSet >();
		pair2dims_vec = new HashMap< Pair,short[] >();
		headerList = new ArrayList<String>();
		id2token = new IntObjectHashMap<String>();
		token2id = new ObjectIntHashMap<String>();
		dim_stat = property_stat;
		this.g = g;
			
		
//		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		
		try  {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] temp = sCurrentLine.split(" "); //[A, <http://dbpedia.org/property/ushrProperty>, C, .]
				if (temp[0].equals("#")) continue;
				
				//headersList
				if(!headerList.contains(temp[0])){ //Vertex Source
					headerList.add(temp[0]);
				}
				if(!headerList.contains(temp[1])){ //Vertex Target
					headerList.add(temp[1]);
				}
				
				String [] predicats = temp[2].split(",");//Predicats
				for (String pred : predicats) {
					short currentPredicat = Short.parseShort(pred); 
					addTriple(temp[0],temp[1],currentPredicat,-1,-1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IntHashSet chooseLessMatchedNodes(IntHashSet already_considered, IntHashSet ranked_tie_connecitvity){
		MutableIntIterator itr_not_yet_ranked = ranked_tie_connecitvity.intIterator();
		int best_min_n_matches = Integer.MAX_VALUE;
		IntObjectHashMap<IntHashSet> prop2node = new IntObjectHashMap<IntHashSet>();
		while (itr_not_yet_ranked.hasNext()){
			int not_yet_ranked = itr_not_yet_ranked.next();
			MutableIntIterator itr_already = already_considered.intIterator();
			boolean connected = false;
			while (itr_already.hasNext() && !connected){
				int yet_ordered = itr_already.next();
				
				if (in_list.containsKey(not_yet_ranked) && in_list.get(not_yet_ranked).contains(yet_ordered)){
					connected = true;
				}
					
			}
			if (connected){
				int n_matches = possible_candidates.get(not_yet_ranked).size();
				if (!prop2node.containsKey(n_matches))
					prop2node.put(n_matches,new IntHashSet());
				prop2node.get(n_matches).add(not_yet_ranked);

				if (n_matches < best_min_n_matches) best_min_n_matches = n_matches;
			}
		}
		return prop2node.get(best_min_n_matches);
	}
	
/* HEURISTICS TO ORDER THE QUERY NODES FROM THE SECOND TO THE LAST */	
	public IntHashSet chooseMoreSelective(IntHashSet already_considered, IntHashSet ranked_tie_connecitvity){
		MutableIntIterator itr_not_yet_ranked = ranked_tie_connecitvity.intIterator();
		int best_min_dims = Integer.MAX_VALUE;
		
		IntObjectHashMap<IntHashSet> prop2node = new IntObjectHashMap<IntHashSet>();
		
		
		while (itr_not_yet_ranked.hasNext()){
			int not_yet_ranked = itr_not_yet_ranked.next();
			int min_dims = Integer.MAX_VALUE;
			MutableIntIterator itr_already = already_considered.intIterator();
			while (itr_already.hasNext()){
				int yet_ordered = itr_already.next();
				if (in_list.containsKey(not_yet_ranked) && in_list.get(not_yet_ranked).contains(yet_ordered)){
					
					Pair p_temp = new Pair(yet_ordered, not_yet_ranked);
					Pair p_temp_undirect = new Pair(not_yet_ranked,yet_ordered);
					MutableShortIterator itrs;
					if (pair2dims.containsKey(p_temp)){
						itrs = pair2dims.get(p_temp).shortIterator();
					} else{
						itrs = pair2dims.get(p_temp_undirect).shortIterator();
					}
					//MutableShortIterator itrs = pair2dims.get(new Pair(yet_ordered, not_yet_ranked)).shortIterator();
					while (itrs.hasNext()){
						min_dims = Math.min(min_dims,dim_stat.get(itrs.next()));
					}
				}
					
			}
			if (!prop2node.containsKey(min_dims))
				prop2node.put(min_dims,new IntHashSet());
			prop2node.get(min_dims).add(not_yet_ranked);

			if (min_dims < best_min_dims) best_min_dims = min_dims;
		}
		return prop2node.get(best_min_dims);
	}
	
	
	
	public IntHashSet chooseMoreNovelCoverage(IntHashSet already_considered, IntHashSet not_yet_consider){
		//System.out.println(not_yet_consider.size());
		MutableIntIterator itr_not_yet = not_yet_consider.intIterator();
		int max_links = 0;
		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
		
		MutableIntIterator itr_already = already_considered.intIterator();
		IntHashSet frontier = new IntHashSet();
		
		while (itr_already.hasNext()){
			int yet_ordered = itr_already.next();
			if (in_list.containsKey(yet_ordered))
				frontier.addAll(in_list.get(yet_ordered));
			
		}
		
		
		while (itr_not_yet.hasNext()){
			int not_yet_ordered = itr_not_yet.next();
			IntHashSet not_yet_ordered_frontier = new IntHashSet();
			if (in_list.containsKey(not_yet_ordered))
				not_yet_ordered_frontier.addAll(in_list.get(not_yet_ordered));
			
			not_yet_ordered_frontier.retainAll(frontier);
			
			if (!nlinks2node.containsKey(not_yet_ordered_frontier.size()))
				nlinks2node.put(not_yet_ordered_frontier.size(), new IntHashSet());
			
			nlinks2node.get(not_yet_ordered_frontier.size()).add(not_yet_ordered);
			if (not_yet_ordered_frontier.size() > max_links) max_links = not_yet_ordered_frontier.size();
		}

		return nlinks2node.get(max_links);
		
	}
	
	public IntHashSet connectivityTopRanked(IntHashSet already_considered, IntHashSet not_yet_consider){
		MutableIntIterator itr_not_yet = not_yet_consider.intIterator();
		int max_links = 0;
		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
		
		while (itr_not_yet.hasNext()){
			int not_yet_ordered = itr_not_yet.next();
			int count_links = 0;
			MutableIntIterator itr_already = already_considered.intIterator();
			while (itr_already.hasNext()){
				int yet_ordered = itr_already.next();
				if (in_list.containsKey(not_yet_ordered) && in_list.get(not_yet_ordered).contains(yet_ordered)){
					Pair p_temp = new Pair(yet_ordered, not_yet_ordered);
					Pair p_temp_undirect = new Pair(not_yet_ordered,yet_ordered);
					if (pair2dims.containsKey(p_temp)){
						count_links += pair2dims.get(p_temp).size();
					} else{
						count_links += pair2dims.get(p_temp_undirect).size();
					}
				}
//					count_links += pair2dims.get(new Pair(yet_ordered, not_yet_ordered)).size();
			}
			if (!nlinks2node.containsKey(count_links))
				nlinks2node.put(count_links, new IntHashSet());
			
			nlinks2node.get(count_links).add(not_yet_ordered);
			if (count_links > max_links) max_links = count_links;
		}

		return nlinks2node.get(max_links);
		
	}
	
	
	public IntHashSet connectivityTopRanked2Hop(IntHashSet already_considered, IntHashSet not_yet_consider){
		MutableIntIterator itr_not_yet = not_yet_consider.intIterator();
		int max_links = 0;
		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
		
		while (itr_not_yet.hasNext()){
			int not_yet_ordered = itr_not_yet.next();
			int count_links = 0;
			
			if (in_list.containsKey(not_yet_ordered)){
				MutableIntIterator itr_in_neighs_nod = in_list.get(not_yet_ordered).intIterator();
				MutableIntIterator itr_already = already_considered.intIterator();
				while (itr_already.hasNext()){
					int yet_ordered  = itr_already.next();
					while (itr_in_neighs_nod.hasNext()){
						int in_neigh = itr_in_neighs_nod.next();
						if (in_list.containsKey(in_neigh) && in_list.get(in_neigh).contains(yet_ordered)){
							Pair p_temp = new Pair(in_neigh, not_yet_ordered);
							Pair p_temp_undirect = new Pair(not_yet_ordered,in_neigh);
							if (pair2dims.containsKey(p_temp)){
								count_links += pair2dims.get(p_temp).size();
							} else{
								count_links += pair2dims.get(p_temp_undirect).size();
							}
						}
//							count_links += pair2dims.get(new Pair(in_neigh, not_yet_ordered)).size();
							
//						if (out_list.containsKey(in_neigh) && out_list.get(in_neigh).contains(yet_ordered))
//							count_links += pair2dims.get(new Pair(in_neigh,yet_ordered)).size();
					}
				}
			}
			
			if (!nlinks2node.containsKey(count_links))
				nlinks2node.put(count_links, new IntHashSet());
			
			nlinks2node.get(count_links).add(not_yet_ordered);
			if (count_links > max_links) max_links = count_links;
			
		}

		return nlinks2node.get(max_links);
		
	}
	
	
//	public IntHashSet chooseMaxSatNodes(IntHashSet already_considered, IntHashSet not_yet_consider){
//		MutableIntIterator itr_not_yet = not_yet_consider.intIterator();
//		int max_links = 0;
//		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
//		
//		while (itr_not_yet.hasNext()){
//			int not_yet_ordered = itr_not_yet.next();
//			int count_links = 0;
//			MutableIntIterator itr_already = already_considered.intIterator();
//			while (itr_already.hasNext()){
//				int yet_ordered = itr_already.next();
//				if (in_list.containsKey(not_yet_ordered) && in_list.get(not_yet_ordered).contains(yet_ordered))
//					count_links = Math.max(count_links, cores2sats.get(not_yet_ordered).size());
//					
//				if (out_list.containsKey(not_yet_ordered) && out_list.get(not_yet_ordered).contains(yet_ordered))
//					count_links = Math.max(count_links, cores2sats.get(not_yet_ordered).size());
//			}
//			if (!nlinks2node.containsKey(count_links))
//				nlinks2node.put(count_links, new IntHashSet());
//			
//			nlinks2node.get(count_links).add(not_yet_ordered);
//			if (count_links > max_links) max_links = count_links;
//		}
//		return nlinks2node.get(max_links);
//
//	}
	
	
	
	/* HEURISTICS TO ORDER THE FIRST QUERY NODE */
//	public IntHashSet chooseMaxLiteralorURI(MutableIntSet core_set){
//		int max_links_literal_uri = 0;
//		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
//		MutableIntIterator itr = core_set.intIterator();
//		while (itr.hasNext()){
//			int node_id = itr.next();
//			int current_links = 0;
//			if (in_list.containsKey(node_id)){
//				MutableIntIterator it1 = in_list.get(node_id).intIterator();
//				while (it1.hasNext()){
////					if (notVariable.containsKey(it1.next())){
////						current_links++;
////					}
//				}
//			}
//			
//			if (!nlinks2node.containsKey(current_links))
//				nlinks2node.put(current_links, new IntHashSet());
//			
//			nlinks2node.get(current_links).add(node_id);
//			
//			if (current_links > max_links_literal_uri) max_links_literal_uri = current_links;
//		}
//		return nlinks2node.get(max_links_literal_uri);
//	}
	
	
//	public IntHashSet chooseMaxLinks(MutableIntSet core_set){
//		int max_links = 0;
//		IntObjectHashMap<IntHashSet> nlinks2node = new IntObjectHashMap<IntHashSet>();
//		MutableIntIterator itr = core_set.intIterator();
//		while (itr.hasNext()){
//			int node_id = itr.next();
//			int current_links = 0;
//			if (in_list.containsKey(node_id)){
//				current_links += in_list.get(node_id).size();
//			}
//			if (out_list.containsKey(node_id)){
//				current_links += out_list.get(node_id).size();
//			}
//			if (!nlinks2node.containsKey(current_links))
//				nlinks2node.put(current_links, new IntHashSet());
//			
//			nlinks2node.get(current_links).add(node_id);	
//			if (current_links > max_links) max_links = current_links;
//		}
//		//System.out.println("max_links "+nlinks2node.get(max_links));
//		return nlinks2node.get(max_links);
//
//	}
	                  
	public int[] getOrdering(){
		
		int[] ordering = new int[id2token.size()];
		MutableIntSet core_set = id2token.keySet();
		MutableIntIterator itr = core_set.intIterator();
		int max_id = -1;
		boolean no_sol = false;
		while (itr.hasNext()){
			int node_id = itr.next();
			int counter = 0;
				// In the case we know that one of the query vertex node does not have any match in the dataset
				SimplePointND query_r_tree = getSynopsis(node_id);
				IntHashSet retrieved_vertices = g.getNodeList(query_r_tree);
				counter = retrieved_vertices.size();
				possible_candidates.put(node_id, retrieved_vertices);
			if (counter == 0){
					max_id = node_id;
					no_sol = true;
				}
		}

		//System.out.println("no_sol "+no_sol);
		if (no_sol){
			ordering[0] = max_id;
		}else{
			MutableIntSet chooseFirstNode = core_set;
			ordering[0] = chooseFirstNode.min();
		}
		
		IntHashSet already_considered = new IntHashSet();
		IntHashSet not_yet_consider = new IntHashSet();
		not_yet_consider.addAll(core_set);
		
		not_yet_consider.remove(ordering[0]);
		already_considered.add(ordering[0]);
			
		int i = 1;
		
		while (not_yet_consider.size() > 0){
			IntHashSet topRankedTie = not_yet_consider;
			topRankedTie = connectivityTopRanked(already_considered, topRankedTie);
			topRankedTie = connectivityTopRanked2Hop(already_considered, topRankedTie);			
			topRankedTie = chooseMoreSelective(already_considered, topRankedTie);
			//topRankedTie = chooseMoreNovelCoverage(already_considered, topRankedTie);
			int temp_node_id = topRankedTie.max();
			ordering[i] = temp_node_id;
			not_yet_consider.remove(temp_node_id);
			already_considered.add(temp_node_id);
			++i;
		}
		return ordering;
	}

	
	public IntArrayList selectVariableProjection(){
		IntArrayList res = new IntArrayList();
		for (String st: headerList){
			res.add(token2id.get(st));
		}
		res.sortThis();
		return res;
	}
	
	
	public void addSelect(String token){
		headerList.add(token);
	}
	
	public int size(){
		TreeSet<Integer> ris = new TreeSet<Integer>();
		MutableIntIterator it = in_list.keySet().intIterator();
		while( it.hasNext()) ris.add(it.next());
		//it = out_list.keySet().intIterator();
		while( it.hasNext()) ris.add(it.next());
		return ris.size();
	}
	
	
	private void createVectorRepresentation(){
		Set<sumgra.query.Pair> keys = pair2dims.keySet();
		for (sumgra.query.Pair el : keys){
			pair2dims_vec.put(el, pair2dims.get(el).toArray());
		}
	}
	
	public short[] getDims(int source, int sink){
		sumgra.query.Pair p = new sumgra.query.Pair(source, sink);
		if (pair2dims_vec.containsKey(p)){
			return pair2dims_vec.get(p); 
		}else{
			return new short[0];
		}
		
	}
	
	/*
	 * return the order to process the query nodes
	 *  
	 * */
	public Vector<ObjectPath> getQueryStructure(){
		createVectorRepresentation(); //pair2dims_vec
		int[] queryOrder = getOrdering();
		
		Vector<ObjectPath> path = new Vector<ObjectPath>();
		ObjectPath first = new ObjectPath(queryOrder[0],0);
		
		path.add(first);
		for (int i=1; i< queryOrder.length; ++i){
			int vertex_id1 = queryOrder[i];
			ObjectPath current = new ObjectPath(queryOrder[i],i);
			for (int j=0; j < i; ++j){
				int previous_id = queryOrder[j];
				if ( in_list.get(vertex_id1) != null && ((IntHashSet) in_list.get(vertex_id1)).contains(previous_id) )
					{
					short[] dims = pair2dims_vec.get( new Pair(previous_id,vertex_id1) );
					if(dims == null){
						dims = pair2dims_vec.get( new Pair(vertex_id1,previous_id) );
					}
					current.addLink(previous_id, j, dims);
					}
					
			}
			current.sort();
			path.add(current);
		}
		return path;
	}
	
	// feature 1: cardinality of vertex signature
	// feature 2: number of unique dimension in the vertex signature (f2 in amber)
	// feature 3: number of all occurrences of the dimensions (with repetition)
	// feature 4: minimum index of lexicographically ordered edge dimensions (f3 in amber)
	// feature 5: maximum index of lexicographically ordered edge dimensions (f4 in amber)
	// feature 6: maximum cardinality of the vertex sub-signature (f1 in amber)
	public SimplePointND getSynopsis(int v_id){
		short[] syn = new short[Settings.SYNOPSIS_SIZE];
		syn[3]  = Short.MIN_VALUE;//Feature 4
		syn[0] = 0;//Feature 1
		syn[2] = 0;//Feature 3
		MutableIntIterator itr = null;
		IntHashSet unique_dims = null;
		
		/* BUILD THE SYNOPSIS FOR INCOMING LIST */
		if (in_list.get(v_id) != null){
			itr = ( (IntHashSet) in_list.get(v_id)).intIterator();
			unique_dims = new IntHashSet();
			
			while (itr.hasNext()){
				syn[0] += 1; //Feature 1
				int vertexItr = itr.next();
				Pair p_temp = new Pair(vertexItr,v_id);
				Pair p_temp_undirect = new Pair(v_id,vertexItr);
				
				ShortHashSet dims;
				if (pair2dims.containsKey(p_temp)){
					dims = pair2dims.get(p_temp);
					syn[5] = (short) ((dims.size() > syn[5])?pair2dims.get(p_temp).size():syn[5]); //Feature 6
				} else{
					dims = pair2dims.get(p_temp_undirect);
					syn[5] = (short) ((dims.size() > syn[5])?pair2dims.get(p_temp_undirect).size():syn[5]); //Feature 6
				}
				
				MutableShortIterator it_short = dims.shortIterator();
				while (it_short.hasNext()){
					syn[2] += 1;//Feature 3 
					short s = it_short.next();
					syn[3] = (short) ((s < (-1*syn[3]))?(-1*s):syn[3]); //Feature 4
					syn[4] = (s > syn[4])?s:syn[4]; //Feature 5
					unique_dims.add(s);
				}
			}
			syn[1] = (short) unique_dims.size(); //Feature 2
		}
		
		/* BUILD THE SYNOPSIS FOR INCOMING LIST */
//			if (out_list.get(v_id) != null){
//				itr = ( (IntHashSet) out_list.get(v_id)).intIterator();
//				unique_dims = new IntHashSet();
//				while (itr.hasNext()){
//					syn[0] += 1; //Feature 1
//					Pair p_temp = new Pair(v_id,itr.next());
//					ShortHashSet dims = pair2dims.get(p_temp);
//					syn[5] = (short) ((dims.size() > syn[5])?pair2dims.get(p_temp).size():syn[5]); //Feature 6
//					MutableShortIterator it_short = dims.shortIterator();
//					while (it_short.hasNext()){
//						syn[2] += 1;//Feature 3 
//						short s = it_short.next();
//						syn[3] = (short) ((s < (-1*syn[3]))?(-1*s):syn[3]); //Feature 4
//						syn[4] = (s > syn[4])?s:syn[4]; //Feature 5
//						unique_dims.add(s);
//					}
//				}
//				syn[1] = (short) unique_dims.size(); //Feature 2
//			}
		return new SimplePointND(syn);
	}
	
	public void addTriple(String source, String sink, short dim, int subjectCode, int objectCode){		
		if (!token2id.containsKey(source)   ){
			int val = token2id.size();
			token2id.put(source, val);
			id2token.put(val,source);
		}
		
		if (!token2id.containsKey(sink)){
			int val = token2id.size();
			token2id.put(sink, val);
			id2token.put(val,sink);
		}
		int id_source = token2id.get(source);
		int id_sink = token2id.get(sink);
		
		if (!in_list.containsKey(id_sink))
			in_list.put(id_sink, new IntHashSet());
		in_list.get(id_sink).add(id_source);
		
		
		if (!in_list.containsKey(id_source))
			in_list.put(id_source, new IntHashSet());
		in_list.get(id_source).add(id_sink);
		
		Pair pair = new Pair(id_source,id_sink);
		if (!pair2dims.containsKey(pair))
			pair2dims.put(pair, new ShortHashSet());
		pair2dims.get(pair).add(dim);
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("inicio....................................................");
	}	
}

