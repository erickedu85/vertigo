package app.gui.preferences;

import javafx.scene.Node;

@Deprecated
public class PreferencesItem {

	private String label;
	private Node feature;

	public PreferencesItem(String label, Node feature) {
		this.label = label;
		this.feature = feature;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Node getFeature() {
		return feature;
	}

	public void setFeature(Node feature) {
		this.feature = feature;
	}

}
