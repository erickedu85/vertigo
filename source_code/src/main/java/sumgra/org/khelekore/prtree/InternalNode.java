package sumgra.org.khelekore.prtree;

import java.util.List;
import java.util.PriorityQueue;

class InternalNode extends NodeBase<Node<SimplePointND>, SimplePointND> {
    public InternalNode (Object[] data) {
	super (data);
    }

    @Override public MBR computeMBR (MBRConverter<SimplePointND> converter) {
	MBR ret = null;
	for (int i = 0, s = size (); i < s; i++)
	    ret = getUnion (ret, get (i).getMBR (converter));
	return ret;
    }

    public void expand (MBR mbr, NodeFilter<SimplePointND> filter,
			MBRConverter<SimplePointND> converter, List<SimplePointND> found,
			List<Node<SimplePointND>> nodesToExpand) {
	for (int i = 0, s = size (); i < s; i++) {
	    Node<SimplePointND> n = get (i);
	    if (mbr.intersects (n.getMBR (converter)))
		nodesToExpand.add (n);
	}
    }
    

    public void getDominatingPoints(MBR query, MBRConverter<SimplePointND> converter, List<SimplePointND> resultNodes){
    	for (int i = 0, s = size (); i < s; i++) {
    	    Node<SimplePointND> n = get (i);
    	    if (query.isProbablyDominated( n.getMBR(converter) )){
    	    	n.getDominatingPoints(query, converter, resultNodes);
    	    }
    	}
    }
    
}
