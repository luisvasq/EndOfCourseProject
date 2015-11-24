package module6;

import processing.core.PGraphics;

/**
 * Emulates the behavior of a classic checkbox control used in GUIs.
 * 
 * @author Luis Vásquez-Peña
 */

public class CheckControl extends GUIControl {
	/** Serialization ID */
	private static final long serialVersionUID = 0;

	public static int SIZE = 5;
	public static int LABEL_OFFSET_X = 7;
	public static int LABEL_OFFSET_Y = -1;

	/**
	 * Construct a CheckControl object from the text label and the screen
	 * coordinates where the control symbol should be centered.
	 * 
	 * @param text
	 *            The String to be written on the control label.
	 * @param x
	 *            x-coordinate of the control.
	 * @param y
	 *            y-coordinate of the control.
	 */
	public CheckControl(String text, int x, int y) {
		super(text, x, y);
		setLabelWidth(0);
		setStatus(false);
	}

	/**
	 * Updates the status field in response to a mouse click event.
	 */
	public void click() {
		setStatus(!getStatus());
	}

	/**
	 * Draws the check control symbol and the text label.
	 * 
	 * @param pg
	 *            The PGraphics object to be used as a graphics buffer.
	 */
	public void drawControl(PGraphics pg) {
		pg.pushStyle();
		
		// Draw check box
		pg.stroke(0);
		pg.strokeWeight(2);
		pg.fill(255);
		pg.rect(getPosX() - SIZE, getPosY() - SIZE, 2 * SIZE, 2 * SIZE);
		
		// Fill check box if it is selected
		if (getStatus()) {
			pg.strokeWeight(2);
			pg.fill(255);
			pg.stroke(0, 200, 0);
			// pg.stroke(224, 0, 0);
			// Check mark
			pg.line(getPosX() - SIZE + 1, getPosY() - .1f * SIZE, getPosX() - .25f * SIZE, getPosY() + SIZE - 1);
			pg.line(getPosX() - .25f * SIZE, getPosY() + SIZE - 1, getPosX() + SIZE - 1, getPosY() - SIZE + 1);
		}
		
		// Draw text label
		pg.fill(0);
		pg.text(getLabel(), getPosX() + SIZE + LABEL_OFFSET_X, getPosY() + LABEL_OFFSET_Y);
		
		pg.popStyle();
	}

	/**
	 * @return the width of the check control, including label.
	 */
	public int controlWidth() {
		return 2 * SIZE + LABEL_OFFSET_X + getLabelWidth();
	}

	/**
	 * @return the height of the check control, including label.
	 */
	public int controlHeight() {
		return (int) max(getLabelHeight(), 2 * SIZE);
	}

	/**
	 * @return the left x-coordinate of the control.
	 */
	public int controlLeft() {
		return getPosX() - SIZE;
	}

	/**
	 * @return the top y-coordinate of the control.
	 */
	public int controlTop() {
		return getPosY() - SIZE;
	}
}
