/**
 * 
 */
package app.heatmap;

import java.awt.Color;

/**
 * @author julien Gaffuri
 *
 */
public class ColorUtil {

	/**
	 * get colors between colors given in a sample (included)
	 * 
	 * @param colSample
	 * @param nb
	 * @return
	 */
	public static Color[] getColors(Color[] colSample, int nb){
		return getColors(colSample, nb, -1);
	}

	public static Color[] getColors(Color[] colSample, int nb, double alpha){
		if(nb<=1) {
			System.err.println("Warning: color number should be greater than 1. Value=" + nb);
			return colSample;
		}
		if(colSample.length <= 1) {
			System.err.println("Warning: color sample number should be greater than 1. Value=" + colSample.length);
			return colSample;
		}
		Color[] cols = new Color[(colSample.length-1)*(nb-1)+1];
		for(int i=0;i<colSample.length-1;i++){
			Color[] cols_ = getColors(colSample[i], colSample[i+1], nb, alpha);
			for(int j=0;j<nb;j++)
				cols[i*(nb-1)+j] = cols_[j];
		}
		return cols;
	}

	/**
	 * get colors between col1 and col2 (included)
	 * 
	 * @param col1
	 * @param col2
	 * @param nb
	 * @return
	 */
	public static Color[] getColors(Color col1, Color col2, int nb){
		return getColors(col1, col2, nb, -1);
	}

	public static Color[] getColors(Color col1, Color col2, int nb, double alpha){
		Color[] cols = new Color[nb];
		for(int i=0; i<nb; i++){
			double t= i/(nb-1.0);
			int a = 0;
			if(alpha<0 || alpha>1)
				a = (int) ((1-t)*col1.getAlpha()+t*col2.getAlpha());
			else 
				a = (int) (255*alpha);
			cols[i] = new Color(
					(int) ((1-t)*col1.getRed()+t*col2.getRed()),
					(int) ((1-t)*col1.getGreen()+t*col2.getGreen()),
					(int) ((1-t)*col1.getBlue()+t*col2.getBlue()),
					a
					);
		}
		return cols;
	}



	/**
	 * Conversion from java color to HTML color.
	 * 
	 * @param col
	 * @return
	 */
	public static String colorToHTMLCode(Color col) {
		//AARRGGBB
		String s = Integer.toHexString( col.getRGB() );
		if(s.length() == 8) return s;
		String s_ = "";
		for(int i=0; i<8-s.length(); i++) s_ += "0";
		return s_+s;
	}

	/**
	 * Conversion from java color to KML color.
	 * 
	 * @param col
	 * @return
	 */
	public static String colorToKMLCode(Color col) {
		//AABBGGRR
		String s = colorToHTMLCode(col);
		return new StringBuffer()
		.append(s.substring(0, 2))
		.append(s.substring(6, 8))
		.append(s.substring(4, 6))
		.append(s.substring(2, 4))
		.toString();
	}

}
