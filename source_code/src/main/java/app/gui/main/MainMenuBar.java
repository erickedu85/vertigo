package app.gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@Deprecated
public class MainMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	JMenu dsMenu = new JMenu("File");
	JMenuItem dsOpenFile = new JMenuItem("Open");

	public MainMenuBar() {

		dsMenu.add(dsOpenFile);

		dsOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				switch (fileChooser.showOpenDialog(null)) {
				case JFileChooser.APPROVE_OPTION:
					// Open file...
					break;
				}
			}
		});
		add(dsMenu);
	}
}
