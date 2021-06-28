package app.breadthfirstsearch;

public class Node {
    int n;
    String name;
    boolean visited;

    Node(int n, String name) {
        this.n = n;
        this.name = name;
        visited = false;
    }

    void visit() {
        visited = true;
    }

    void unvisit() {
        visited = false;
    }

	public boolean isVisited() {
		return visited;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + n;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (n != other.n)
			return false;
		return true;
	}
	
}