package sumgra.org.khelekore.prtree;

import java.util.List;
import java.util.PriorityQueue;

/** A node in a Priority R-Tree
 * @param <T> the data type of the elements stored in this node
 */
interface Node<T> {
    /** Get the size of the node, that is how many data elements it holds
     * @return the number of elements (leafs or child nodes) that this node has
     */
    int size ();

    /** Get the MBR of this node
     * @param converter the MBR converter to use for the actual objects
     * @return the MBR for this node
     */
    MBR getMBR (MBRConverter<T> converter);
    /**
     * @param the MBR of the query
     * @param converter the MBR converter to use for the actual objects
     * @param resultNodes the set of leaves nodes satisfying the search
     * */
    
    void getDominatingPoints(MBR query, MBRConverter<T> converter, List<T> resultNodes);
    
}
