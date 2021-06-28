package sumgra.org.khelekore.prtree;

/** A minimum bounding box for n dimensions.
 */ 
public interface MBR {
    /**
     * @return the number of dimensions this bounding box has 
     */
    int getDimensions ();

    /** Get the minimum value for the given axis
     * @param axis the axis to use
     * @return the min value
     */
    short getMin (int axis);

    /** Get the maximum value for the given axis
     * @param axis the axis to use
     * @return the x max value
     */
    short getMax (int axis);

    /** Return a new MBR that is the union of this mbr and the other 
     * @param mbr the MBR to create a union with
     * @return the new MBR
     */
    MBR union (MBR mbr);

    /** Check if the other MBR intersects this one
     * @param other the MBR to check against
     * @return true if the given MBR intersects with this MBR
     */
    boolean intersects (MBR other);
    
    
    boolean isProbablyDominated (MBR other);
    
    <T> boolean isDominatedBy(T t, MBRConverter<T> converter);

}
