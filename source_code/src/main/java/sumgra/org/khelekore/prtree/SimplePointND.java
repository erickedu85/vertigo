package sumgra.org.khelekore.prtree;

/** One implementatoin of a point
 */
public class SimplePointND implements PointND {
    private short[] ords;

    /** Create a new SimplePointND using the given ordinates.
     * @param ords the ordinates
     */
    public SimplePointND (short... ords) {
	this.ords = ords;
    }

    public short[] getOrds(){
    	return this.ords;
    }
    
    public int getDimensions () {
	return ords.length;
    }

    public short getOrd (int axis) {
	return ords[axis];
    }
    
 
    public int hashCode(){
    	return java.util.Arrays.toString(ords).hashCode();
    	
    }

    
    public boolean equals(Object o){
    	SimplePointND t = (SimplePointND) o;
    	for (int i=0; i < ords.length; ++i){
    		if (ords[i] != t.ords[i])
    			return false;
    	}
    	return true;
    }
    
    public String toString(){
    	String st = ""+ords[0];
    	for (int i=1; i < ords.length; ++i)
    		st+=" "+ords[i];
    	return st;
    }
    
    
}
