package sumgra.query;

public class Pair{
	public int first;
	public int second;
	
	public Pair(int first, int second){
		this.first = first;
		this.second = second;
	}
	
	public int hashCode(){
		return (int) (Math.pow(first, 2)+Math.pow(second, 3));
	}
	
	public boolean equals(Object o){
		Pair p = (Pair) o;
		return (first == p.first) && (second == p.second);
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
	
}