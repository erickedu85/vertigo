package app.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gs.collections.impl.map.mutable.primitive.IntObjectHashMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.EdgeType;
import app.graph.structure.Etiqueta;
import app.graph.structure.Fill;
import app.graph.structure.Multiedge;
import app.graph.structure.NodeType;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;

/**
 * <i>Input</i>. This class provides methods for reading strings and numbers
 * from standard input, file input, URLs, and sockets.
 * <p>
 * The Locale used is: language = English, country = US. This is consistent with
 * the formatting conventions with Java floating-point literals, command-line
 * arguments (via {@link Double#parseDouble(String)}) and standard output.
 * <p>
 * For additional documentation, see
 * <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of
 * <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by
 * Robert Sedgewick and Kevin Wayne.
 * <p>
 * Like {@link Scanner}, reading a token also consumes preceding Java
 * whitespace, reading a full line consumes the following end-of-line delimeter,
 * while reading a character consumes nothing extra.
 * <p>
 * Whitespace is defined in {@link Character#isWhitespace(char)}. Newlines
 * consist of \n, \r, \r\n, and Unicode hex code points 0x2028, 0x2029, 0x0085;
 * see <tt><a href="http://www.docjar.com/html/api/java/util/Scanner.java.html">
 *  Scanner.java</a></tt> (NB: Java 6u23 and earlier uses only \r, \r, \r\n).
 *
 * @author David Pritchard
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class In {

	///// begin: section (1 of 2) of code duplicated from In to StdIn.

	// assume Unicode UTF-8 encoding
	private static final String CHARSET_NAME = "UTF-8";

	// assume language = English, country = US for consistency with System.out.
	private static final Locale LOCALE = Locale.US;

	// the default token separator; we maintain the invariant that this value
	// is held by the scanner's delimiter between calls
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");

	// makes whitespace characters significant
	private static final Pattern EMPTY_PATTERN = Pattern.compile("");

	// used to read the entire input. source:
	// http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");

	//// end: section (1 of 2) of code duplicated from In to StdIn.

	private Scanner scanner;
	//
	private static final Pattern NODE_PATTERN = Pattern
			.compile("(node.\\[)(.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*)");

	private static final Pattern EDGE_PATTERN = Pattern.
			compile("(edge.\\[)(.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*)");

	private static final Pattern NODE_PATTERN_SIN_FM3 = Pattern
			.compile("(node.\\[)(.*s*.*)");
//	
	private static final Pattern EDGE_PATTERN_SIN_FM3 = Pattern.
			compile("(edge.\\[)(.*.*)");
	
	

	/**
	 * Initializes an input stream from standard input.
	 */
	public In() {
		scanner = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
		scanner.useLocale(LOCALE);
	}

	/**
	 * Initializes an input stream from a socket.
	 *
	 * @param socket
	 *            the socket
	 * @throws IllegalArgumentException
	 *             if cannot open {@code socket}
	 * @throws NullPointerException
	 *             if {@code socket} is {@code null}
	 */
	public In(Socket socket) {
		if (socket == null)
			throw new NullPointerException("argument is null");
		try {
			InputStream is = socket.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + socket);
		}
	}

	/**
	 * Initializes an input stream from a URL.
	 *
	 * @param url
	 *            the URL
	 * @throws IllegalArgumentException
	 *             if cannot open {@code url}
	 * @throws NullPointerException
	 *             if {@code url} is {@code null}
	 */
	public In(URL url) {
		if (url == null)
			throw new NullPointerException("argument is null");
		try {
			URLConnection site = url.openConnection();
			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + url);
		}
	}

	/**
	 * Initializes an input stream from a file.
	 *
	 * @param file
	 *            the file
	 * @throws IllegalArgumentException
	 *             if cannot open {@code file}
	 * @throws NullPointerException
	 *             if {@code file} is {@code null}
	 */
	public In(File file) {
		if (file == null)
			throw new NullPointerException("argument is null");
		try {
			scanner = new Scanner(file, CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + file);
		}
	}

	/**
	 * Initializes an input stream from a filename or web page name.
	 *
	 * @param name
	 *            the filename or web page name
	 * @throws IllegalArgumentException
	 *             if cannot open {@code name} as a file or URL
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}
	 */
	public In(String name) {
		if (name == null)
			throw new NullPointerException("argument is null");
		try {
			// first try to read file from local file system
			File file = new File(name);
			if (file.exists()) {
				scanner = new Scanner(file, CHARSET_NAME);
				scanner.useLocale(LOCALE);
				return;
			}

			// next try for files included in jar
			URL url = getClass().getResource(name);

			// or URL from web
			if (url == null) {
				url = new URL(name);
			}

			URLConnection site = url.openConnection();

			// in order to set User-Agent, replace above line with these two
			// HttpURLConnection site = (HttpURLConnection)
			// url.openConnection();
			// site.addRequestProperty("User-Agent", "Mozilla/4.76");

			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + name);
		}
	}

	/**
	 * Initializes an input stream from a given {@link Scanner} source; use with
	 * <tt>new Scanner(String)</tt> to read from a string.
	 * <p>
	 * Note that this does not create a defensive copy, so the scanner will be
	 * mutated as you read on.
	 *
	 * @param scanner
	 *            the scanner
	 * @throws NullPointerException
	 *             if {@code scanner} is {@code null}
	 */
	public In(Scanner scanner) {
		if (scanner == null)
			throw new NullPointerException("argument is null");
		this.scanner = scanner;
	}

	/**
	 * Returns true if this input stream exists.
	 *
	 * @return <tt>true</tt> if this input stream exists; <tt>false</tt>
	 *         otherwise
	 */
	public boolean exists() {
		return scanner != null;
	}

	//// begin: section (2 of 2) of code duplicated from In to StdIn,
	//// with all methods changed from "public" to "public static".

	/**
	 * Returns true if input stream is empty (except possibly whitespace). Use
	 * this to know whether the next call to {@link #readString()},
	 * {@link #readDouble()}, etc will succeed.
	 *
	 * @return <tt>true</tt> if this input stream is empty (except possibly
	 *         whitespace); <tt>false</tt> otherwise
	 */
	public boolean isEmpty() {
		return !scanner.hasNext();
	}

	/**
	 * Returns true if this input stream has a next line. Use this method to
	 * know whether the next call to {@link #readLine()} will succeed. This
	 * method is functionally equivalent to {@link #hasNextChar()}.
	 *
	 * @return <tt>true</tt> if this input stream is empty; <tt>false</tt>
	 *         otherwise
	 */
	public boolean hasNextLine() {
		return scanner.hasNextLine();
	}

	/**
	 * Returns true if this input stream has more inputy (including whitespace).
	 * Use this method to know whether the next call to {@link #readChar()} will
	 * succeed. This method is functionally equivalent to {@link #hasNextLine()}
	 * .
	 * 
	 * @return <tt>true</tt> if this input stream has more input (including
	 *         whitespace); <tt>false</tt> otherwise
	 */
	public boolean hasNextChar() {
		scanner.useDelimiter(EMPTY_PATTERN);
		boolean result = scanner.hasNext();
		scanner.useDelimiter(WHITESPACE_PATTERN);
		return result;
	}

	/**
	 * Reads and returns the next line in this input stream.
	 *
	 * @return the next line in this input stream; <tt>null</tt> if no such line
	 */
	public String readLine() {
		String line;
		try {
			line = scanner.nextLine();
		} catch (NoSuchElementException e) {
			line = null;
		}
		return line;
	}

	/**
	 * Reads and returns the next character in this input stream.
	 *
	 * @return the next character in this input stream
	 */
	public char readChar() {
		scanner.useDelimiter(EMPTY_PATTERN);
		String ch = scanner.next();
		assert ch.length() == 1 : "Internal (Std)In.readChar() error!" + " Please contact the authors.";
		scanner.useDelimiter(WHITESPACE_PATTERN);
		return ch.charAt(0);
	}

	/**
	 * Reads and returns the remainder of this input stream, as a string.
	 *
	 * @return the remainder of this input stream, as a string
	 */
	public String readAll() {
		if (!scanner.hasNextLine())
			return "";

		String result = scanner.useDelimiter(EVERYTHING_PATTERN).next();
		// not that important to reset delimeter, since now scanner is empty
		scanner.useDelimiter(WHITESPACE_PATTERN); // but let's do it anyway
		return result;
	}

	/**
	 * Reads the next token from this input stream and returns it as a
	 * <tt>String</tt>.
	 *
	 * @return the next <tt>String</tt> in this input stream
	 */
	public String readString() {
		return scanner.next();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>int</tt>,
	 * and returns the <tt>int</tt>.
	 *
	 * @return the next <tt>int</tt> in this input stream
	 */
	public int readInt() {
		return scanner.nextInt();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>double</tt>, and returns the <tt>double</tt>.
	 *
	 * @return the next <tt>double</tt> in this input stream
	 */
	public double readDouble() {
		return scanner.nextDouble();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>float</tt>, and returns the <tt>float</tt>.
	 *
	 * @return the next <tt>float</tt> in this input stream
	 */
	public float readFloat() {
		return scanner.nextFloat();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>long</tt>
	 * , and returns the <tt>long</tt>.
	 *
	 * @return the next <tt>long</tt> in this input stream
	 */
	public long readLong() {
		return scanner.nextLong();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>short</tt>, and returns the <tt>short</tt>.
	 *
	 * @return the next <tt>short</tt> in this input stream
	 */
	public short readShort() {
		return scanner.nextShort();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>byte</tt>
	 * , and returns the <tt>byte</tt>.
	 * <p>
	 * To read binary data, use {@link BinaryIn}.
	 *
	 * @return the next <tt>byte</tt> in this input stream
	 */
	public byte readByte() {
		return scanner.nextByte();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>boolean</tt> (interpreting either <tt>"true"</tt> or <tt>"1"</tt> as
	 * <tt>true</tt>, and either <tt>"false"</tt> or <tt>"0"</tt> as
	 * <tt>false</tt>).
	 *
	 * @return the next <tt>boolean</tt> in this input stream
	 */
	public boolean readBoolean() {
		String s = readString();
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		if (s.equals("1"))
			return true;
		if (s.equals("0"))
			return false;
		throw new InputMismatchException();
	}

	/**
	 * Reads all remaining tokens from this input stream and returns them as an
	 * array of strings.
	 *
	 * @return all remaining tokens in this input stream, as an array of strings
	 */
	public String[] readAllStrings() {
		// we could use readAll.trim().split(), but that's not consistent
		// since trim() uses characters 0x00..0x20 as whitespace
		String[] tokens = WHITESPACE_PATTERN.split(readAll());
		if (tokens.length == 0 || tokens[0].length() > 0)
			return tokens;
		String[] decapitokens = new String[tokens.length - 1];
		for (int i = 0; i < tokens.length - 1; i++)
			decapitokens[i] = tokens[i + 1];
		return decapitokens;
	}

	/**
	 * Reads all remaining lines from this input stream and returns them as an
	 * array of strings.
	 *
	 * @return all remaining lines in this input stream, as an array of strings
	 */
	public String[] readAllLines() {
		ArrayList<String> lines = new ArrayList<String>();
		while (hasNextLine()) {
			lines.add(readLine());
		}
		return lines.toArray(new String[0]);
	}

	/**
	 * Reads all remaining tokens from this input stream, parses them as
	 * integers, and returns them as an array of integers.
	 *
	 * @return all remaining lines in this input stream, as an array of integers
	 */
	public int[] readAllInts() {
		String[] fields = readAllStrings();
		int[] vals = new int[fields.length];
		for (int i = 0; i < fields.length; i++)
			vals[i] = Integer.parseInt(fields[i]);
		return vals;
	}

	/**
	 * Reads all remaining tokens from this input stream, parses them as
	 * doubles, and returns them as an array of doubles.
	 *
	 * @return all remaining lines in this input stream, as an array of doubles
	 */
	public double[] readAllDoubles() {
		String[] fields = readAllStrings();
		double[] vals = new double[fields.length];
		for (int i = 0; i < fields.length; i++)
			vals[i] = Double.parseDouble(fields[i]);
		return vals;
	}

	///// end: section (2 of 2) of code duplicated from In to StdIn */

	/**
	 * Closes this input stream.
	 */
	public void close() {
		scanner.close();
	}
	
	/**
	 * 
	 * Method to get a short label in uppercase
	 * 
	 * @param label
	 * @return a short label
	 */
	public String getUpperCaseShortLabel(String label) {
		System.out.println(label);
		String splitCharacter = " ";
		String abreviationCharacter = ".";
		String[] labelSplited = label.split(splitCharacter);
		System.out.println(labelSplited.length);
		System.out.println(labelSplited[0]);
		String result="";
		
		if(labelSplited.length==1) {
			return label;
		}
		for(int i=0; i<labelSplited.length;i++) {
			if(i+1<labelSplited.length) {
				result = result + labelSplited[i].charAt(0)+abreviationCharacter+splitCharacter;
			}else {
				result = result + labelSplited[i];
			}
		}
		System.out.println(result.toUpperCase());
		return result.toUpperCase();
	}

	
	/**
	 * 
	 * Method to get all nodes of a text line
	 * 
	 * @param nodeString
	 * @return a List of vertices
	 */
	public IntObjectHashMap<Vertex> getNodesSinFm3(String nodeString) {
		Matcher m;
		Matcher x;
		m = NODE_PATTERN_SIN_FM3.matcher(nodeString);
		Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pLabel = Pattern.compile("(label.)(\")(.*)(\")");
		//ITS TYPE
		Pattern pType = Pattern.compile("(weight.)(\\d*)"); 
		//***************
	
		IntObjectHashMap<Vertex> listNodes = new IntObjectHashMap<Vertex>();
		while (m.find()) {
			int id = 0;
			int type = 0;
			String label = "";
			double positionX = 0;
			double positionY = 0;

			x = pId.matcher(m.group(2));
			if (x.find()) {
				id = Integer.parseInt(x.group(2));
			}
			x = pLabel.matcher(m.group(2));
			if (x.find()) {
				label = x.group(3);
			}
			if (label == null) {
				label = String.valueOf(id);
			}
			x = pType.matcher(m.group(2));
			if (x.find()) {
				type = Integer.parseInt(x.group(2));
			}
//			System.out.println(id+" "+label+" "+type);
//			System.out.println(label+ " --- type: "+type);
			NodeType nodeType = NodeType.getNodeTypeById(type);

			Etiqueta etiqueta = new Etiqueta(label, false);
			etiqueta.setHeight(Constants.GRAPH_DB_NODE_TEXT_SIZE);
			PositionShape position = new PositionShape(positionX, positionY);
			Stroke stroke = new Stroke(Constants.GRAPH_DB_NODE_STROKE);
						
			Fill fill = new Fill(true,ColorShape.getHSBGoogle_NodeColorCategory(type),100);
			boolean isVisible = true;
			Vertex v = new Vertex(id, etiqueta, position, stroke, fill, isVisible, Constants.GRAPH_DB_NODE_RADIUS,nodeType);
			listNodes.put(id,v);
			
		}
		System.out.println("size listNodes : " + listNodes.size());
		return listNodes;
	}
	
	/**
	 * Method to get all Multiedges of a text line
	 * 
	 * @param edgeString
	 * @return List of edges
	 */
	public LongObjectHashMap<Multiedge> getMultiedgesSinFm3(String edgeString) {
		Matcher m;
		Matcher x;
		m = EDGE_PATTERN_SIN_FM3.matcher(edgeString);
		// Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pNodeSource = Pattern.compile("(source.)(\\d*)");
		Pattern pNodeTarget = Pattern.compile("(target.)(\\d*)");
		Pattern pTypeEdge = Pattern.compile("(subgraph.)(\\d*)"); 

		String text;
		int type;
		int nodeSource;
		int nodeTarget;
		List<Edge> listEdges = new ArrayList<Edge>();

		while (m.find()) {

			text = null;
			nodeSource = 0;
			nodeTarget = 0;
			type = 0;

			x = pNodeSource.matcher(m.group(2));
			if (x.find()) {
				nodeSource = Integer.parseInt(x.group(2));
			}
			x = pNodeTarget.matcher(m.group(2));
			if (x.find()) {
				nodeTarget = Integer.parseInt(x.group(2));
			}
			x = pTypeEdge.matcher(m.group(2));
			if (x.find()) {
				type = Integer.parseInt(x.group(2));
			}
			
//			System.out.println(nodeSource+" "+nodeTarget+" "+type);

			if (text == null || text.equals("")) {
				text = EdgeType.getLayerTypeById(type).getLabel();
				// text = "Edge from " + nodeSource + " to " + nodeTarget;
			}

			Etiqueta etiqueta = new Etiqueta(text, false);
			etiqueta.setHeight(Constants.GRAPH_DB_EDGE_TEXT_SIZE);
			PositionShape position = new PositionShape();
//			Stroke stroke = new Stroke(true, ColorShape.getHSBGoogle_EdgeColorCategory(type),
			Stroke stroke = new Stroke(true, ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(type),
					Constants.GRAPH_DB_EDGE_STROKE_OPACITY, Constants.GRAPH_DB_EDGE_STROKE_WEIGHT);
			Fill fill = new Fill(); // LINES IN PROCESSING HAVE NOT FILL
			boolean isVisible = true;
			EdgeType edgeType = EdgeType.getLayerTypeById(type);
			int id = edgeType.getId(); // GraphUtil.edgeIdFunction(nodeSource, nodeTarget, edgeType.getId());
			listEdges.add(new Edge(id, etiqueta, position, stroke, fill, isVisible, edgeType, nodeSource, nodeTarget));
		}

		LongObjectHashMap<Multiedge> listMultiedge = new LongObjectHashMap();
		for (Edge edge : listEdges) {

			long keyMultiedge = GraphUtil.pairingFunction(edge.getIdSource(), edge.getIdTarget());

			if (!listMultiedge.containsKey(keyMultiedge)) {
				// add a new multiedge
				Multiedge multiedge = new Multiedge(keyMultiedge, edge.getIdSource(), edge.getIdTarget());
				// And include their first edge
				multiedge.addEdgeSecure(edge);
				listMultiedge.put(keyMultiedge, multiedge);
			} else {
				// add new edge
				listMultiedge.get(keyMultiedge).addEdgeSecure(edge);
			}
		}
		System.out.println("size listmultiedge : " + listMultiedge.size() );
		return listMultiedge;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Method to get all nodes of a text line
	 * 
	 * @param nodeString
	 * @return a List of vertices
	 */
	public IntObjectHashMap<Vertex> getNodes(String nodeString) {
		Matcher m;
		Matcher x;
		m = NODE_PATTERN.matcher(nodeString);
		Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pLabel = Pattern.compile("(label.)(\")(.*)(\")");
		//ITS TYPE
		Pattern pType = Pattern.compile("(weight.)(\\d*)"); 
		//***************
		Pattern pXPosition = Pattern.compile("(graphics.*\\s*.*\\s.*x\\s)(\\d.*)");
		Pattern pYPosition = Pattern.compile("(graphics.*\\s*.*\\s.*\\s*y\\s)(\\d.*)"); 
		
		IntObjectHashMap<Vertex> listNodes = new IntObjectHashMap();

		while (m.find()) {
			
			int id = 0;
			int type = 0;
			String label = "";
			double positionX = 0;
			double positionY = 0;

			x = pId.matcher(m.group(2));
			if (x.find()) {
				id = Integer.parseInt(x.group(2));
			}
			x = pLabel.matcher(m.group(2));
			if (x.find()) {
				label = x.group(3);
			}
			if (label == null) {
				label = String.valueOf(id);
			}
			x = pXPosition.matcher(m.group(2));
			if (x.find()) {
				positionX = Float.parseFloat(x.group(2));
			}
			x = pYPosition.matcher(m.group(2));
			if (x.find()) {
				positionY = Float.parseFloat(x.group(2));
			}
			x = pType.matcher(m.group(2));
			if (x.find()) {
				type = Integer.parseInt(x.group(2));
			}
//			System.out.println(label+ " --- type: "+type);
			NodeType nodeType = NodeType.getNodeTypeById(type);

			Etiqueta etiqueta = new Etiqueta(label, false);
			etiqueta.setHeight(Constants.GRAPH_DB_NODE_TEXT_SIZE);
			PositionShape position = new PositionShape(positionX, positionY);
			Stroke stroke = new Stroke(Constants.GRAPH_DB_NODE_STROKE);
						
			Fill fill = new Fill(true,ColorShape.getHSBGoogle_NodeColorCategory(type),100);
			boolean isVisible = true;
			Vertex v = new Vertex(id, etiqueta, position, stroke, fill, isVisible, Constants.GRAPH_DB_NODE_RADIUS,nodeType);
			listNodes.put(id,v);
			
		}
		System.out.println("size listNodes : " + listNodes.size());
		return listNodes;
	}

	/**
	 * Method to get all Multiedges of a text line
	 * 
	 * @param edgeString
	 * @return List of edges
	 */
	public LongObjectHashMap<Multiedge> getMultiedges(String edgeString) {
		Matcher m;
		Matcher x;
		m = EDGE_PATTERN.matcher(edgeString);
		// Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pLabel = Pattern.compile("(label.)(\")(.*)(\")");
		Pattern pNodeSource = Pattern.compile("(source.)(\\d*)");
		Pattern pNodeTarget = Pattern.compile("(target.)(\\d*)");
		Pattern pTypeEdge = Pattern.compile("(subgraph.)(\\d*)"); 

		String text;
		int type;
		int nodeSource;
		int nodeTarget;
		List<Edge> listEdges = new ArrayList<Edge>();

		while (m.find()) {

			text = null;
			nodeSource = 0;
			nodeTarget = 0;
			type = 0;

			x = pLabel.matcher(m.group(2));
			if (x.find()) {
				text = x.group(3);
			}
			x = pNodeSource.matcher(m.group(2));
			if (x.find()) {
				nodeSource = Integer.parseInt(x.group(2));
			}
			x = pNodeTarget.matcher(m.group(2));
			if (x.find()) {
				nodeTarget = Integer.parseInt(x.group(2));
			}
			x = pTypeEdge.matcher(m.group(2));
			if (x.find()) {
				type = Integer.parseInt(x.group(2));
			}

			if (text == null || text.equals("")) {
				text = EdgeType.getLayerTypeById(type).getLabel();
				// text = "Edge from " + nodeSource + " to " + nodeTarget;
			}

			Etiqueta etiqueta = new Etiqueta(text, false);
			etiqueta.setHeight(Constants.GRAPH_DB_EDGE_TEXT_SIZE);
			PositionShape position = new PositionShape();
//			Stroke stroke = new Stroke(true, ColorShape.getHSBGoogle_EdgeColorCategory(type),
			Stroke stroke = new Stroke(true, ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(type),
					Constants.GRAPH_DB_EDGE_STROKE_OPACITY, Constants.GRAPH_DB_EDGE_STROKE_WEIGHT);
			Fill fill = new Fill(); // LINES IN PROCESSING HAVE NOT FILL
			boolean isVisible = true;
			EdgeType edgeType = EdgeType.getLayerTypeById(type);
			int id = edgeType.getId(); // GraphUtil.edgeIdFunction(nodeSource, nodeTarget, edgeType.getId());
			listEdges.add(new Edge(id, etiqueta, position, stroke, fill, isVisible, edgeType, nodeSource, nodeTarget));
		}

		LongObjectHashMap<Multiedge> listMultiedge = new LongObjectHashMap();
		for (Edge edge : listEdges) {

			long keyMultiedge = GraphUtil.pairingFunction(edge.getIdSource(), edge.getIdTarget());

			if (!listMultiedge.containsKey(keyMultiedge)) {
				// add a new multiedge
				Multiedge multiedge = new Multiedge(keyMultiedge, edge.getIdSource(), edge.getIdTarget());
				// And include their first edge
				multiedge.addEdgeSecure(edge);
				listMultiedge.put(keyMultiedge, multiedge);
			} else {
				// add new edge
				listMultiedge.get(keyMultiedge).addEdgeSecure(edge);
			}
		}
		System.out.println("size listmultiedge : " + listMultiedge.size() );
		return listMultiedge;
	}

}