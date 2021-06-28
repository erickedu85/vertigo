package sumgra.query;

import java.util.Arrays;
import java.util.Comparator;

public class LinkObjectPath implements Comparator<LinkObjectPath>, Comparable<LinkObjectPath>{
	public int previous_id;
	public int rank_previous_id;
	public short[] dims;
	
	public LinkObjectPath(int previous_id, int rank_previous_id, short[] dims){
		this.previous_id = previous_id;
		this.rank_previous_id = rank_previous_id;
		this.dims = dims;
	}
	
	// Overriding the compareTo method
	public int compareTo(LinkObjectPath d){
	      return -1*(this.dims.length - d.dims.length);
	}

	   // Overriding the compare method to sort the age 
	public int compare(LinkObjectPath d, LinkObjectPath d1){
	      return -1*(d.dims.length - d1.dims.length);
	}
	
	public String toString(){
		return "RANK Prev ID: "+rank_previous_id+" DIM: "+Arrays.toString(dims);
	}
	
}
