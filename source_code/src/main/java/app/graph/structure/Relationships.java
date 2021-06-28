package app.graph.structure;

public class Relationships {

	/**
	 * Like relationships.txt
	 */
	private int idSource;
	private int idTarget;

	public Relationships(int idSource, int idTarget) {
		super();
		this.idSource = idSource;
		this.idTarget = idTarget;
	}

	public Relationships(String idSource, String idTarget) {
		this.idSource = Integer.valueOf(idSource);
		this.idTarget = Integer.valueOf(idTarget);
	}

	/**
	 * Method to confirm is the edge contains the BOTH vertices Vertex1 and
	 * Vertex2 as source OR target
	 * 
	 * @param idVertex1
	 *            Vertex 1
	 * @param idVertex2
	 *            Vertex 2
	 * @return True if the edge contains the both V1 and V2 as source or target
	 *         whatever
	 */
	public boolean containVertices(int idVertex1, int idVertex2) {
		if ((idSource == idVertex1 && idTarget == idVertex2) || (idSource == idVertex2 && idTarget == idVertex1)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relationships other = (Relationships) obj;
		if (!this.containVertices(other.idSource, other.idTarget))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return (int) (Math.pow(this.idSource, 2) + Math.pow(this.idTarget, 2));
	}

	@Override
	public String toString() {
		return "Relationships [idSource=" + idSource + ", idTarget=" + idTarget + "]";
	}

	public int getIdSource() {
		return idSource;
	}

	public int getIdTarget() {
		return idTarget;
	}

}
