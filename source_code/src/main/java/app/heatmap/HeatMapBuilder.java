package app.heatmap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.graph.structure.ColorShape;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.NodeType;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;
import processing.core.PApplet;

/**
 * Heat map builder algorithm
 * 
 * @author julien Gaffuri
 *
 */
public class HeatMapBuilder {

	public PApplet parent;
	private int w, h;
	private int wMin, hMin;
	private int radius;
	private int[][] pts;
	private float[] kernel;
	private Color[] colors;
	private Color backColor;
	private WritableRaster raster;
	private BufferedImage img;
	private ArrayList<Integer> vals;
	private int[] quantileValues;

	private List<Vertex> listNodeRaster = new ArrayList<Vertex>();

	public HeatMapBuilder(int wMin, int hMin, int w, int h, int[][] pts, int kernelRadius, PApplet parent) {
		this(wMin, hMin, w, h, pts, kernelRadius, null, parent);
//		colors = ColorUtil.getColors(
//				      new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED }, 6);

		
//		colors = ColorUtil.getColors(
//				new Color[] {
//						new Color(94,79,162),//blue
//						new Color(50,136,189),
//						new Color(102,194,165),
//						new Color(171,221,164),
//						new Color(230,245,152),
//						new Color(255,255,191),
//						new Color(254,224,139),
//						new Color(253,174,97),
//						new Color(244,109,67),
//						new Color(213,62,79),
//						new Color(158,1,66) //red
//				}, 
//				12);

		//VIRIDIS
//		<color>#440154FF </color> rgb(68, 1, 84)
//		<color>#481567FF </color>rgb(72, 21, 103)
//		<color>#482677FF </color>rgb(72, 38, 119)
//		<color>#453781FF </color>rgb(69, 55, 129)
//		<color>#404788FF </color>rgb(64, 71, 136)
//		<color>#39568CFF </color>rgb(57, 86, 140)
//		<color>#33638DFF </color>rgb(51, 99, 141)
//		<color>#2D708EFF </color>rgb(45, 112, 142)
//		<color>#287D8EFF </color>rgb(40, 125, 142)
//		<color>#238A8DFF </color>rgb(35, 138, 141)
//		<color>#1F968BFF </color>rgb(31, 150, 139)
//		<color>#20A387FF </color>rgb(32, 163, 135)
//		<color>#29AF7FFF </color>rgb(41, 175, 127)
//		<color>#3CBB75FF </color>rgb(60, 187, 117)
//		<color>#55C667FF </color>rgb(85, 198, 103)
//		<color>#73D055FF </color>rgb(115, 208, 85)
//		<color>#95D840FF </color>rgb(149, 216, 64)
//		<color>#B8DE29FF </color>rgb(184, 222, 41)
//		<color>#DCE319FF </color>rgb(220, 227, 25)
//		<color>#FDE725FF </color>rgb(253, 231, 37)

		//OPT 1 VIRIDIS
		colors = ColorUtil.getColors(
				new Color[] {
						new Color(68, 1, 84),
						new Color(72, 21, 103),
						new Color(72, 38, 119),
						new Color(69, 55, 129),
						new Color(64, 71, 136),
						new Color(57, 86, 140),
						new Color(51, 99, 141),
						new Color(45, 112, 142),
						new Color(40, 125, 142),
						new Color(35, 138, 141),
						new Color(31, 150, 139),
						new Color(32, 163, 135),
						new Color(41, 175, 127),
						new Color(60, 187, 117),
						new Color(85, 198, 103),
						new Color(115, 208, 85),
						new Color(149, 216, 64),
						new Color(184, 222, 41),
						new Color(220, 227, 25),
						new Color(253, 231, 37),
				}, 
				21);
		
		//https://www.kennethmoreland.com/color-advice/
		//OPT 2 VIRIDIS
		colors = ColorUtil.getColors(
				new Color[] {
						new Color(72,0,84),
						new Color(78,21,108),
						new Color(80,44,125),
						new Color(76,66,136),
						new Color(69,85,140),
						new Color(62,103,142),
						new Color(55,120,142),
						new Color(47,136,142),
						new Color(38,152,138),
						new Color(32,169,131),
						new Color(42,184,120),
						new Color(71,199,102),
						new Color(109,211,78),
						new Color(153,220,48),
						new Color(199,227,14),
						new Color(243,233,28),
				}, 
				17);
		
		
			
	}
	
	public HeatMapBuilder(int wMin, int hMin, int w, int h, int[][] pts, int kernelRadius, Color[] colors,
			PApplet parent) {
		this(wMin, hMin, w, h, pts, kernelRadius, colors, new Color(255, 255, 255, 0), parent);
	}

	public HeatMapBuilder(int wMin, int hMin, int w, int h, int[][] pts, int kernelRadius, Color[] colors,
			Color backColor, PApplet parent) {
		this.parent = parent;
		this.wMin = wMin;
		this.hMin = hMin;
		this.w = w;
		this.h = h;
		this.pts = pts;
		this.radius = kernelRadius;
		this.colors = colors;
		this.backColor = Color.WHITE;
		if (radius <= 0)
			System.out.println("Warning: gaussian kernel with weird radius: " + radius);
		buildKernel();
		// setList();
	}

	private void buildKernel() {
		int size = radius * 2 + 1;
		kernel = new float[size];
		float sig = radius / 3; // Standard Deviation
		float sa = 2 * sig * sig; //
		float sb = (float) (2 * Math.PI * sig);
		float sc = (float) Math.sqrt(sb);
		float sum = 0;
		int j = 0;
		for (int i = -radius; i <= radius; i++) {
			float d = i * i;
			if (d > radius * radius) {
				kernel[j] = 0;
			} else {
				kernel[j] = (float) Math.exp(-(d) / sa) / sc;
			}
			sum += kernel[j];
			j++;
		}
		for (int i = 0; i < size; i++)
			kernel[i] /= sum;
	}

	@SuppressWarnings("unused")
	private float[] buildGaussianKernel(int radius) {
		// Create kernel size from radio
		int size = radius * 2 + 1;
		// Init of kernel array with sizeKernel
		float[] kernel = new float[size];
		//
		int j = 0;
		float sum = 0;
		for (int i = -radius; i <= radius; i++) {
			kernel[j] = gaussianOperator(i, radius);
			j++;
			sum += kernel[j];
		}

		// Normalise the kernel
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] /= sum;
		}
		return kernel;
	}

	/**
	 * Gaussian Operator, reading from :
	 * http://homepages.inf.ed.ac.uk/rbf/HIPR2/gsmooth.htm
	 * 
	 * @param x
	 * @param radius
	 * @return
	 */
	private float gaussianOperator(int x, int radius) {
		// Standar Desviation
		float st = radius / 3;
		float result = (float) Math.exp(-(Math.pow(x, 2)) / (2 * Math.pow(st, 2)))
				/ (float) (Math.sqrt(2 * Math.PI * st));
		return result;
	}

	// Appliying convolution
	public WritableRaster getRaster() {
		if (raster == null) {
			// make raster (a bit bigger than the initial one, according to the
			// kernel radius)
			raster = new BufferedImage(w + 2 * radius, h + 2 * radius, BufferedImage.TYPE_INT_ARGB).getAlphaRaster();

			// fill initial raster
			for (int k = 0; k < pts.length; k++) {
				int i = pts[k][0];
				int j = pts[k][1];
				try {
					raster.setDataElements(i + radius, j + radius,
							new int[] { ((int[]) raster.getDataElements(i, j, null))[0] + 1000 });
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}

			// apply convolution
			if (radius > 0) {
				ConvolveOp f;
				f = new ConvolveOp(new Kernel(1, kernel.length, kernel), ConvolveOp.EDGE_ZERO_FILL, null);
				raster = f.filter(raster, null);
				f = new ConvolveOp(new Kernel(kernel.length, 1, kernel), ConvolveOp.EDGE_ZERO_FILL, null);
				raster = f.filter(raster, null);
			}
		}
		return raster;
	}

	/**
	 * Used in quantileValues Method
	 * 
	 * @return
	 */
	public ArrayList<Integer> getValues() {
		if (vals == null) {
			// System.out.println("getting values");
			// retrieve all non null values
			vals = new ArrayList<Integer>();
			for (int i = wMin; i < w; i++) {
				for (int j = hMin; j < h; j++) {
					int v = ((int[]) getRaster().getDataElements(i + radius, j + radius, null))[0];
					if (v == 0)
						continue;
					vals.add(new Integer(v));
				}
			}
		}
		return vals;
	}

	public int[] getQuantileValues() {
		if (quantileValues == null) {
			// sort values
			Collections.sort(getValues());
			int nbPerColor = getValues().size() / colors.length;
			quantileValues = new int[colors.length - 1];
			for (int i = 0; i < quantileValues.length; i++) {
				quantileValues[i] = getValues().get((i + 1) * nbPerColor);
			}
		}
		return quantileValues;
	}

	public void fillListRastered() {
		for (int i = wMin; i < w; i++) {
			for (int j = hMin; j < h; j++) {
				try {
					double v = ((int[]) getRaster().getDataElements(i + radius, j + radius, null))[0];
					if (v != 0) {

						int red = getColor(v, getQuantileValues()).getRed();
						int green = getColor(v, getQuantileValues()).getGreen();
						int blue = getColor(v, getQuantileValues()).getBlue();

						float[] k = Color.RGBtoHSB(red, green, blue, null);
						int colorProcessing = parent.color(k[0] * 360, k[1] * 100, k[2] * 100);

						Stroke stroke = new Stroke(false, ColorShape.getHSB_Blue(),
								Constants.GRAPH_DB_NODE_STROKE.getStrokeOpacity(),
								Constants.GRAPH_DB_NODE_STROKE.getStrokeWeight());
						Fill fill = new Fill(true, colorProcessing, 100);// 100-alpha
						boolean isVisible = true;
						double radius = 1.0d;
						Vertex a = new Vertex(listNodeRaster.size(), new Etiqueta("heatmap", false),
								new PositionShape((double) i, (double) j), stroke, fill, isVisible, radius,NodeType.getNodeTypeById(0));

						listNodeRaster.add(a);
					}
				} catch (Exception e) {
				}
			}
		}

	}

	public BufferedImage getImage() {

		if (img == null) {
			// may happen...
			if (getValues().size() == 0) {
				BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics g = img.getGraphics();
				g.setColor(backColor);
				g.fillRect(0, 0, w, h);
				return img;
			}

			// build image
			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// draw image pixel by pixel
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					double v = ((int[]) getRaster().getDataElements(i + radius, j + radius, null))[0];
					// g.setColor(getColor(v, getQuantileValues()));
					if (v != 0) {
						// g.setColor(getColor(v, getQuantileValues()));
						// g.fillRect(i, j, 1, 1);

						// int red = getColor(v, getQuantileValues()).getRed();
						// int green = getColor(v,
						// getQuantileValues()).getGreen();
						// int blue = getColor(v,
						// getQuantileValues()).getBlue();
						// parent.colorMode(PConstants.RGB, 255, 255, 255, 255);
						// int colorProcessing = parent.color(red, green, blue,
						// 255);

						// int id = listNode.size() + 1;
						// Node a = new Node(id, "test", i, j, 1, 1,
						// colorProcessing);
						// listNode.add(a);

						// System.out.println("i: " + i + " j: " + j + " v: " +
						// v);
					} else {
						// g.setColor(Color.WHITE);
					}
				}
			}
		}
		return img;
	}

	private Color getColor(double v, int[] indexValues) {
		if (v == 0)
			return backColor;
		return colors[getColorIndex(v, indexValues)];
	}

	private int getColorIndex(double v, int[] indexValues) {
		int i = 0;
		while (i < indexValues.length && v > indexValues[i])
			i++;
		return i;
	}

	public List<Vertex> getListNodeRaster() {
		return listNodeRaster;
	}

}
