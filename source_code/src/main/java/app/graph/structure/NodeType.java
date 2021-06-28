package app.graph.structure;


import javafx.scene.paint.Color;

public enum NodeType {

	Person(0,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_NodeColorCategory(0)),"Person","type_node_".concat(String.valueOf(0)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_NodeColorCategory(0)),
	Address(1,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_NodeColorCategory(1)),"Address","type_node_".concat(String.valueOf(1)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_NodeColorCategory(1)),
	Company(2,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_NodeColorCategory(2)),"Company","type_node_".concat(String.valueOf(2)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_NodeColorCategory(2)),
	Client(3,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_NodeColorCategory(3)),"Client","type_node_".concat(String.valueOf(3)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_NodeColorCategory(3));
	
	private int id;
	private Color color;
	private String label;
	private String relativePath;
	private String cssColorStyle;

	private NodeType(int id, Color color, String label, String relativePath, String cssColorStyle) {
		this.id = id;
		this.color = color;
		this.label = label;
		this.relativePath = relativePath;
		this.cssColorStyle = cssColorStyle;
	}
	
	/**
	 * Method to get the nodeType by the id
	 * 
	 * @param id
	 * @return LayerType
	 */
	public static NodeType getNodeTypeById(int id) {
		for (NodeType nodeType : NodeType.values()) {
			if (nodeType.getId() == id) {
				return nodeType;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public Color getColor() {
		return color;
	}
	
	public String getLabel() {
		return label;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public String getCssColorStyle() {
		return cssColorStyle;
	}

	
	
}
