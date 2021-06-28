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
public class GUISliderVertex extends JDialog {

	private static final long serialVersionUID = 1L;
	public static PApplet parent;
	private JSlider sliderRadiusNode;
	private JSlider sliderOpacityNode;

	public GUISliderVertex() {
		super();
		setTitle("Vertices Features");
		setSize(280, 180);
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

		sliderRadiusNode = new JSlider(JSlider.HORIZONTAL, 0, 30, 5);
		sliderRadiusNode.setPaintTicks(true);
		sliderRadiusNode.setPaintLabels(true);
		sliderRadiusNode.setMinorTickSpacing(1);
		sliderRadiusNode.setMajorTickSpacing(5);
		sliderRadiusNode.addChangeListener((ChangeListener) parent);

		sliderOpacityNode = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
		sliderOpacityNode.setPaintTicks(true);
		sliderOpacityNode.setPaintLabels(true);
		sliderOpacityNode.setMinorTickSpacing(5);
		sliderOpacityNode.setMajorTickSpacing(20);
		sliderOpacityNode.addChangeListener((ChangeListener) parent);

		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(new JLabel("Radius: "), c);
		c.weightx = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		mainPanel.add(sliderRadiusNode, c);
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
		mainPanel.add(sliderOpacityNode, c);
		return mainPanel;
	}

	public JSlider getSliderRadiusNode() {
		return sliderRadiusNode;
	}

	public JSlider getSliderOpacityNode() {
		return sliderOpacityNode;
	}

}
