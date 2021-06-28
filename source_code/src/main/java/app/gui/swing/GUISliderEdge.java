package app.gui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import processing.core.PApplet;

@Deprecated
public class GUISliderEdge extends JDialog {

	public static PApplet parent;
	private static final long serialVersionUID = 1L;
	private JSlider sliderOpacityEdge;

	public GUISliderEdge() {
		super();
		setTitle("Edge Features");
		setSize(280, 120);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setContentPane(sliderContentPane());
	}

	/**
	 * @return
	 */
	private JPanel sliderContentPane() {

		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		add(mainPanel);

		sliderOpacityEdge = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
		sliderOpacityEdge.setPaintTicks(true);
		sliderOpacityEdge.setPaintLabels(true);
		sliderOpacityEdge.setMinorTickSpacing(5);
		sliderOpacityEdge.setMajorTickSpacing(20);
		sliderOpacityEdge.addChangeListener((ChangeListener) parent);

		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		mainPanel.add(new JLabel("Opacity: "), c);
		c.weightx = 8;
		c.ipady = 40;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		mainPanel.add(sliderOpacityEdge, c);
		return mainPanel;
	}

	public JSlider getSliderOpacityEdge() {
		return sliderOpacityEdge;
	}

}
