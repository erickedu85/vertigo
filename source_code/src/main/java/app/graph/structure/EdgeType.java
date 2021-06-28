package app.graph.structure;


import javafx.scene.paint.Color;

public enum EdgeType {

//	OfficerPanama(0, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(0)), "isOfficer_panama","type_".concat(String.valueOf(0)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(0)), 
//	IntermediaryPanama(1,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(1)), "isIntermediary_panama","type_".concat(String.valueOf(1)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(1)),
//	AdressePanama(2,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(2)), "hasAddress_panama","type_".concat(String.valueOf(2)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(2)),
//	OfficerOffshore(3, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(3)), "isOfficer_offshore","type_".concat(String.valueOf(3)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(3)), 
//	IntermediaryOffshore(4,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(4)), "isIntermediary_offshore","type_".concat(String.valueOf(4)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(4)),
//	AdresseOffshore(5,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(5)), "hasAddress_offshore","type_".concat(String.valueOf(5)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(5)),
//	OfficerBahamas(6, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(6)), "isOfficer_bahamas","type_".concat(String.valueOf(6)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(6)), 
//	IntermediaryBahamas(7,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(7)), "isIntermediary_bahamas","type_".concat(String.valueOf(7)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(7)),
//	AdresseBahamas(8,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(8)), "hasAddress_bahamas","type_".concat(String.valueOf(8)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(8)),	
//	OfficerParadise(9, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(9)), "isOfficer_paradise","type_".concat(String.valueOf(9)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(9)), 
//	IntermediaryParadise(10,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(10)), "isIntermediary_paradise","type_".concat(String.valueOf(10)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(10)),
//	AdresseParadise(11,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgePanamaPapersColorCategory(11)), "hasAddress_paradise","type_".concat(String.valueOf(11)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_PanamaPapersColorCategory(11));
	
	TVCG(0, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(0)), "TVCG","type_".concat(String.valueOf(0)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(0)), 
	InfoVis(1,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(1)), "InfoVis","type_".concat(String.valueOf(1)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(1)), 
	CGF(2,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(2)), "CGF","type_".concat(String.valueOf(2)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(2)), 
	EuroVis(3,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(3)), "EuroVis", "type_".concat(String.valueOf(3)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(3)), 
	PacificVis(4,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(4)),"PacificVis", "type_".concat(String.valueOf(4)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(4)), 
	GraphDrawing(5,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(5)),"GraphDrawing", "type_".concat(String.valueOf(5)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(5)), 
	CGA(6, ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(6)),"CGA", "type_".concat(String.valueOf(6)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(6)), 
	IV(7,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(7)),"IV","type_".concat(String.valueOf(7)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(7)), 
	DASFAA(8,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(8)),"DASFAA","type_".concat(String.valueOf(8)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(8)), 
	EDBT(9,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(9)),"EDBT","type_".concat(String.valueOf(9)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(9)), 
	ICDE(10,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(10)),"ICDE","type_".concat(String.valueOf(10)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(10)), 
	ICDM(11,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(11)),"ICDM","type_".concat(String.valueOf(11)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(11)), 
	ICDT(12,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(12)),"ICDT","type_".concat(String.valueOf(12)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(12)), 
	KDD(13,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(13)),"KDD","type_".concat(String.valueOf(13)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(13)), 
	SDM(14,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(14)),"SDM","type_".concat(String.valueOf(14)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(14)), 
	SSDBM(15,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(15)),"SSDBM","type_".concat(String.valueOf(15)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(15)), 
	SIGMOD(16,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(16)),"SIGMOD","type_".concat(String.valueOf(16)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(16)), 
	VLDB(17,ColorShape.parserColorProcessingToJavafx(ColorShape.getHSBGoogle_EdgeColorCategory(17)),"VLDB","type_".concat(String.valueOf(17)).concat(".png"),ColorShape.getHSBGoogle_CSSStyle_EdgeColorCategory(17));
	

	
	private int id;
	private Color color;
	private String label;
	private String relativePath;
	private String cssColorStyle;

	private EdgeType(int id, Color color, String label, String relativePath, String cssColorStyle) {
		this.id = id;
		this.color = color;
		this.label = label;
		this.relativePath = relativePath;
		this.cssColorStyle = cssColorStyle;
	}
	
	//BORRAR
	private EdgeType(int id, String label, String relativePath) {
		this.id = id;
		this.label = label;
		this.relativePath = relativePath;
	}

	/**
	 * Method to get the layerType by the id
	 * 
	 * @param id
	 * @return LayerType
	 */
	public static EdgeType getLayerTypeById(int id) {
		for (EdgeType layer : EdgeType.values()) {
			if (layer.getId() == id) {
				return layer;
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
