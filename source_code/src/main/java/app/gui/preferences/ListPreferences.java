package app.gui.preferences;

import java.util.List;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

@Deprecated
public class ListPreferences extends TableView<PreferencesItem> {

	public final static Logger logger = Logger.getLogger(ListPreferences.class);

	public ListPreferences(List<PreferencesItem> listPreferences) {

		setId("listPreferences");

		ObservableList<PreferencesItem> imgList = FXCollections.observableArrayList();

		for (PreferencesItem preferencesItem : listPreferences) {
			imgList.addAll(preferencesItem);
		}

		// Node String
		TableColumn<PreferencesItem, String> labelItem = new TableColumn<PreferencesItem, String>("MBR");
		labelItem.setCellValueFactory(new PropertyValueFactory<PreferencesItem, String>("label"));
		labelItem.setPrefWidth(200);
		labelItem.getStyleClass().add("labelItem");

		// Node Item
		TableColumn<PreferencesItem, Node> nodeItem = new TableColumn<PreferencesItem, Node>("Graph");
		nodeItem.setCellValueFactory(new PropertyValueFactory<PreferencesItem, Node>("feature"));
		nodeItem.setPrefWidth(200);

		getColumns().add(labelItem);
		getColumns().add(nodeItem);

		setItems(imgList);
	}
}
