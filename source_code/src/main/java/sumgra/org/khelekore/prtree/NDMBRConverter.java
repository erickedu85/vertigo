package sumgra.org.khelekore.prtree;

public class NDMBRConverter implements MBRConverter<SimplePointND>{
	private int dim;
	
	public NDMBRConverter(int dim){
		this.dim = dim;
	}
	
	public int getDimensions() {
		return this.dim;
	}

	public short getMin(int axis, SimplePointND t) {
		return t.getOrd(axis);
	}

	public short getMax(int axis, SimplePointND t) {
		return t.getOrd(axis);
	}

}
