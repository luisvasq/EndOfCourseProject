package module6;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * Defines the common properties and behavior for option and check controls, as
 * well as any other control which could be included in further extensions.
 * Fields <code>posX</code> and <code>posY</code> are the screen coordinates
 * which center the control is drawn to. Field <code>label</code> is a string
 * containing the text included with the control, <code>labelWidth</code> and
 * <code>labelHeight</code> are the dimensions of the text label, and
 * <code>status</code> is a boolean defining whether a control is selected or
 * not.
 * <p>
 * An abstract <code>method void click()</code> defines the control behavior
 * after a mouse click action. Other abstract methods related to the calculation
 * of label dimensions are included and implemented in derived classes.
 * 
 * @author Luis Vásquez-Peña
 * @version 2015-11-15
 */
public abstract class GUIControl extends PApplet {
	/** Serialization ID */
	private static final long serialVersionUID = 0;

	private int posX, posY;
	private int labelWidth;
	private int labelHeight;
	private String label;
	private boolean status;

	/**
	 * Construct an GUIControl object from the text label and the screen
	 * coordinates where the control symbol should be centered.
	 * 
	 * @param text
	 *            The String to be written on the control label.
	 * @param x
	 *            x-coordinate of the control.
	 * @param y
	 *            y-coordinate of the control.
	 */
	public GUIControl(String text, int x, int y) {
		posX = x;
		posY = y;
		label = text;
	}

	/**
	 * @return the width of the text label.
	 */
	public int getLabelWidth() {
		return labelWidth;
	}

	/**
	 * @param labelWidth
	 *            the labelWidth to set
	 */
	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	/**
	 * @return the height of the text label.
	 */
	public int getLabelHeight() {
		return labelHeight;
	}

	/**
	 * @param labelHeight
	 *            the labelHeight to set
	 */
	public void setLabelHeight(int labelHeight) {
		this.labelHeight = labelHeight;
	}

	/**
	 * @return the status of the control.
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the x-coordinate center of the control symbol.
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @param x
	 *            the x-coordinate center of the control symbol to set
	 */
	public void setPosX(int x) {
		posX = x;
	}

	/**
	 * @return the y-coordinate center of the control symbol.
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * @param y
	 *            the y-coordinate center of the control symbol to set
	 */
	public void setPosY(int y) {
		posY = y;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param text
	 *            the label text to set
	 */
	public void setLabel(String text) {
		label = text;
	}

	/**
	 * Draws the control symbol and the text label.
	 * 
	 * @param pg
	 *            The PGraphics object to be used as a graphics buffer.
	 */
	public void draw(PGraphics pg) {
		// Draw control symbol and label text
		drawControl(pg);

		// Set control dimensions
		labelWidth = (int) pg.textWidth(getLabel());
		labelHeight = (int) pg.textAscent();
	}

	/**
	 * Checks whether a screen position (x, y) is within the control.
	 * 
	 * @param x
	 *            x-coordinate to be checked.
	 * @param y
	 *            y-coordinate to be checked.
	 */
	public boolean contains(int x, int y) {
		return !(x < controlLeft() || x > controlLeft() + controlWidth() || y < controlTop()
				|| y > controlTop() + controlHeight());
	}

	public abstract void click();

	public abstract int controlLeft();

	public abstract int controlTop();

	public abstract int controlWidth();

	public abstract int controlHeight();

	public abstract void drawControl(PGraphics pg);

}
