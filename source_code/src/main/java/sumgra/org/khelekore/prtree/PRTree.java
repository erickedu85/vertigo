package sumgra.org.khelekore.prtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** A Priority R-Tree, a spatial index, for N dimensions.
 *  This tree only supports bulk loading.
 *
 * @param <T> the data type stored in the PRTree
 */

// T = SimplePointND
//N = InternalNode<SimplePointND>
public class PRTree {

    private MBRConverter<SimplePointND> converter;
    private int branchFactor;

    private Node<SimplePointND> root;
    private int numLeafs;
    private int height;

    /** Create a new PRTree using the specified branch factor.
     * @param converter the MBRConverter to use for this tree
     * @param branchFactor the number of child nodes for each internal node.
     */
    public PRTree (MBRConverter<SimplePointND> converter, int branchFactor) {
	this.converter = converter;
	this.branchFactor = branchFactor;
    }

    /** Bulk load data into this tree.
     *
     *  Create the leaf nodes that each hold (up to) branchFactor data entries.
     *  Then use the leaf nodes as data until we can fit all nodes into
     *  the root node.
     *
     * @param data the collection of data to store in the tree.
     * @throws IllegalStateException if the tree is already loaded
     */
    public void load (Collection<SimplePointND> data) {
	if (root != null)
	    throw new IllegalStateException ("Tree is already loaded");
	numLeafs = data.size ();
	LeafBuilder lb = new LeafBuilder (converter.getDimensions(), branchFactor);
	
	List<LeafNode> leafNodes =
	    new ArrayList<LeafNode> (estimateSize (numLeafs));
	lb.buildLeafs (data, new DataComparators<SimplePointND> (converter),
		       new LeafNodeFactory (), leafNodes);

	height = 1;
	List<? extends Node<SimplePointND>> nodes = leafNodes;
	while (nodes.size () > branchFactor) {
	    height++;
	    List<InternalNode> internalNodes =
		new ArrayList<InternalNode> (estimateSize (nodes.size ()));
	    lb.buildLeafs (nodes, new InternalNodeComparators<SimplePointND> (converter),
			   new InternalNodeFactory (), internalNodes);
	    nodes = internalNodes;
	}
	setRoot (nodes);
    }

    private int estimateSize (int dataSize) {
	return (int)(1.0 / (branchFactor - 1) * dataSize);
    }

    private  void setRoot (List<? extends Node<SimplePointND>> nodes) {
	if (nodes.size () == 0)
	    root = new InternalNode (new Object[0]);
	else if (nodes.size () == 1) {
	    root = nodes.get (0);
	} else {
	    height++;
	    root = new InternalNode (nodes.toArray ());
	}
    }

    private class LeafNodeFactory
	implements NodeFactory<LeafNode> {
	public LeafNode create (Object[] data) {
	    return new LeafNode (data);
	}
    }

    private class InternalNodeFactory
	implements NodeFactory<InternalNode> {
	public InternalNode create (Object[] data) {
	    return new InternalNode (data);
	}
    }

     /** Get an N dimensional minimum bounding box of the data stored
     *  in this tree.
     * @return the MBR of the whole PRTree
     */
    public MBR getMBR () {
	return root.getMBR (converter);
    }

    /** Get the number of data leafs in this tree.
     * @return the total number of leafs in this tree
     */
    public int getNumberOfLeaves () {
	return numLeafs;
    }

    /** Check if this tree is empty
     * @return true if the number of leafs is 0, false otherwise
     */
    public boolean isEmpty () {
	return numLeafs == 0;
    }

    /** Get the height of this tree.
     * @return the total height of this tree
     */
    public int getHeight () {
	return height;
    }

    public void getNotDominatedPoints (SimplePointND query, List<SimplePointND> resultNodes){
    	SimpleMBR query_mbr = new SimpleMBR(query);
    	root.getDominatingPoints(query_mbr, converter, resultNodes);
    }
}
