package sumgra.org.khelekore.prtree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class LeafNode extends NodeBase<SimplePointND, SimplePointND> {

    public LeafNode (Object[] data) {
	super (data);
    }

    public MBR getMBR (SimplePointND t, MBRConverter<SimplePointND> converter) {
	return new SimpleMBR (t, converter);
    }

    @Override public MBR computeMBR (MBRConverter<SimplePointND> converter) {
	MBR ret = null;
	for (int i = 0, s = size (); i < s; i++)
	    ret = getUnion (ret, getMBR (get (i), converter));
	return ret;
    }

    public void getDominatingPoints(MBR query, MBRConverter<SimplePointND> converter, List<SimplePointND> result){
    	for (int i = 0, s = size (); i < s; i++) {
    		SimplePointND  t  = get (i);
    		if (query.isDominatedBy(t, converter)){
    			result.add(t);
    		}
    	}
    }
    
     
}
