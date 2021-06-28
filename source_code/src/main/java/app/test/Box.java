package app.test;

import org.apache.log4j.Logger;

import app.graph.structure.ColorShape;
import processing.core.PApplet;
import processing.core.PConstants;

public class Box {

	public static Logger logger = Logger.getLogger(Box.class);
	public static PApplet parent;
	public float bx = 50;
	private float by = 50;
	private int boxWidth = 150;
	private int boxHeight = 50;
	public String idsNodes = "";

	public Box() {
		// TODO Auto-generated constructor stub
	}

	public void display() {
		parent.fill(ColorShape.getHSB_Green());
		parent.strokeWeight(1);
		if (isMouseOverBox()) {
			parent.stroke(ColorShape.getHSB_White());
		} else {
			parent.stroke(ColorShape.getHSB_Black());
		}
		parent.rect(bx, by, boxWidth, boxHeight);

		// Label
		parent.fill(ColorShape.getHSB_Black());
		parent.textSize(10);
		parent.textAlign(PConstants.CENTER, PConstants.BOTTOM);
		parent.text("[" + idsNodes + "]" , (boxWidth / 2) + bx, (boxHeight / 2) + by);
		parent.textSize(12);
		parent.textAlign(PConstants.CENTER, PConstants.BOTTOM);
		parent.text("Show Embedding's" , (boxWidth / 2) + bx, (boxHeight) + by);
	}

	public boolean isMouseOverBox() {
		if (parent.mouseX >= bx && parent.mouseX <= bx + boxWidth && parent.mouseY >= by
				&& parent.mouseY <= by + boxHeight) {
			return true;
		}
		return false;
	}

}
