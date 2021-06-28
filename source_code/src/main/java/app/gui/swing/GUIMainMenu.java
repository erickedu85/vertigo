package app.gui.swing;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import processing.core.PApplet;

@Deprecated
public class GUIMainMenu {

	public static PApplet parent;
	private MenuBar myMenu;
	private Menu fileMenu, saveAsMenu;
	private MenuItem open, exit, about, saveAsTif, saveAsTga, saveAsJpg, saveAsPng, viewNodeSizeOpacity,
			viewEdgeOpacity;// createQuery

	public GUIMainMenu() {
		createMainMenu();
	}

	public void createMainMenu() {
		// this doesn't demonstrate best coding practice, just a simple method
		// create the MenuBar Object
		myMenu = new MenuBar();

		// create the top level button
		fileMenu = new Menu("File");
		saveAsMenu = new Menu("Save As");

		// create all the Menu Items and add the menuListener to check their
		// state.

		open = new MenuItem("Open...");
		open.addActionListener((ActionListener) parent);
		//
		saveAsJpg = new MenuItem("Jpg");
		saveAsJpg.addActionListener((ActionListener) parent);
		saveAsPng = new MenuItem("Png");
		saveAsPng.addActionListener((ActionListener) parent);
		saveAsTif = new MenuItem("Tif");
		saveAsTif.addActionListener((ActionListener) parent);
		saveAsTga = new MenuItem("Tga");
		saveAsTga.addActionListener((ActionListener) parent);
		//
		about = new MenuItem("About");
		about.addActionListener((ActionListener) parent);
		//
		exit = new MenuItem("Exit");
		exit.addActionListener((ActionListener) parent);

		//
		viewNodeSizeOpacity = new MenuItem("Radius & Opacity");
		viewNodeSizeOpacity.addActionListener((ActionListener) parent);

		viewEdgeOpacity = new MenuItem("Opacity");
		viewEdgeOpacity.addActionListener((ActionListener) parent);
		//

		// Add File Menu
		fileMenu.add(open);
		fileMenu.add(saveAsMenu);
		fileMenu.addSeparator();
		fileMenu.add(about);
		fileMenu.addSeparator();
		fileMenu.add(exit);
		saveAsMenu.add(saveAsJpg);
		saveAsMenu.add(saveAsPng);
		saveAsMenu.add(saveAsTif);
		saveAsMenu.add(saveAsTga);

		// add the button to the menu
		myMenu.add(fileMenu);

		// add the menu to the frame!

		// JFrame frame = (JFrame) ((SmoothCanvas)
		// parent.getSurface().getNative()).getFrame();
		// frame.setMenuBar(myMenu);

		// PSurfaceAWT surf = (PSurfaceAWT) parent.getSurface();
		// PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas)
		// surf.getNative();
		// smoothCanvas.getFrame().setMenuBar(myMenu);

		JPanel buttonContainer = new JPanel();
		JToggleButton btnAddNode = new JToggleButton(new ImageIcon("./img/add-node.png"));
		JToggleButton btnAddEdge = new JToggleButton(new ImageIcon("./img/add-edge.png"));
		buttonContainer.add(btnAddNode);
		buttonContainer.add(btnAddEdge);
		// frame.getContentPane().add(buttonContainer, BorderLayout.PAGE_END);

		// smoothCanvas.getFrame().add(buttonContainer, BorderLayout.PAGE_END);
	}

	public MenuBar getMyMenu() {
		return myMenu;
	}

	public void setMyMenu(MenuBar myMenu) {
		this.myMenu = myMenu;
	}

	public Menu getFileMenu() {
		return fileMenu;
	}

	public void setFileMenu(Menu fileMenu) {
		this.fileMenu = fileMenu;
	}

	public Menu getSaveAsMenu() {
		return saveAsMenu;
	}

	public void setSaveAsMenu(Menu saveAsMenu) {
		this.saveAsMenu = saveAsMenu;
	}

	public MenuItem getOpen() {
		return open;
	}

	public void setOpen(MenuItem open) {
		this.open = open;
	}

	public MenuItem getExit() {
		return exit;
	}

	public void setExit(MenuItem exit) {
		this.exit = exit;
	}

	public MenuItem getAbout() {
		return about;
	}

	public void setAbout(MenuItem about) {
		this.about = about;
	}

	public MenuItem getSaveAsTif() {
		return saveAsTif;
	}

	public void setSaveAsTif(MenuItem saveAsTif) {
		this.saveAsTif = saveAsTif;
	}

	public MenuItem getSaveAsTga() {
		return saveAsTga;
	}

	public void setSaveAsTga(MenuItem saveAsTga) {
		this.saveAsTga = saveAsTga;
	}

	public MenuItem getSaveAsJpg() {
		return saveAsJpg;
	}

	public void setSaveAsJpg(MenuItem saveAsJpg) {
		this.saveAsJpg = saveAsJpg;
	}

	public MenuItem getSaveAsPng() {
		return saveAsPng;
	}

	public void setSaveAsPng(MenuItem saveAsPng) {
		this.saveAsPng = saveAsPng;
	}

	public MenuItem getViewNodeSizeOpacity() {
		return viewNodeSizeOpacity;
	}

	public void setViewNodeSizeOpacity(MenuItem viewNodeSizeOpacity) {
		this.viewNodeSizeOpacity = viewNodeSizeOpacity;
	}

	public MenuItem getViewEdgeOpacity() {
		return viewEdgeOpacity;
	}

	public void setViewEdgeOpacity(MenuItem viewEdgeOpacity) {
		this.viewEdgeOpacity = viewEdgeOpacity;
	}

}
