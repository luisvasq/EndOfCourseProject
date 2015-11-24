package module6;

import processing.core.PGraphics;

/**
 * Emulates the behavior of a classic radio/option button used in GUIs. This
 * class introduces the field <code>OptionGroup refToGroup</code> which
 * associates every option control with its corresponding group.
 * 
 * @author Luis Vásquez-Peña
 * @version 2015-11-15
 */
public class OptionControl extends GUIControl {
	/** Serialization ID */
	private static final long serialVersionUID = 0;

	private OptionGroup refToGroup;

	public static int RADIUS = 11;
	public static int LABEL_OFFSET_X = 1;
	public static int LABEL_OFFSET_Y = -1;

	/**
	 * Construct an OptionControl object from the text label and the screen
	 * coordinates where the control symbol should be centered.
	 * 
	 * @param text
	 *            The String to be written on the control label.
	 * @param x
	 *            x-coordinate of the control.
	 * @param y
	 *            y-coordinate of the control.
	 */
	public OptionControl(String text, int x, int y) {
		super(text, x, y);
		setStatus(false);
		setLabelWidth(0);
		refToGroup = null;
	}

	/**
	 * @return the refToGroup
	 */
	public OptionGroup getRefToGroup() {
		return refToGroup;
	}

	/**
	 * @param refToGroup
	 *            the refToGroup to set
	 */
	public void setRefToGroup(OptionGroup refToGroup) {
		this.refToGroup = refToGroup;
	}

	/**
	 * Overrides the setStatus method defined in parent class. This method
	 * complies with some particular behavior for option controls.
	 * 
	 * @param status
	 *            the status to set
	 */
	@Override
	public void setStatus(boolean status) {
		if (status)
			// Same that click on control option
			click();
		else
			// All option controls might be deselected
			super.setStatus(false);
	}

	/**
	 * Updates the option control and its container option group in response to
	 * a mouse click event.
	 */
	public void click() {
		OptionControl opt = refToGroup.getSelectedOption();
		// Deselect current option control chosen in the group
		if (opt != null)
			opt.setStatus(false);
		// Select this option control
		super.setStatus(true);
	}

	/**
	 * Draws the option control symbol and the text label.
	 * 
	 * @param pg
	 *            The PGraphics object to be used as a graphics buffer.
	 */
	public void drawControl(PGraphics pg) {
		pg.pushStyle();

		// Draw option control.
		pg.stroke(0);
		pg.strokeWeight(2);
		pg.fill(255);
		pg.ellipse(getPosX(), getPosY(), RADIUS, RADIUS);
		// Fill option control if it is selected.
		if (getStatus()) {
			pg.noStroke();
			pg.fill(224, 0, 0);
			pg.ellipse(getPosX(), getPosY(), .4f * RADIUS, .4f * RADIUS);
		}
		// Draw text label.
		pg.fill(0);
		pg.text(getLabel(), getPosX() + RADIUS + LABEL_OFFSET_X, getPosY() + LABEL_OFFSET_Y);

		pg.popStyle();
	}

	/**
	 * @return the width of the option control, including label.
	 */
	public int controlWidth() {
		return 2 * RADIUS + LABEL_OFFSET_X + getLabelWidth();
	}

	/**
	 * @return the height of the option control, including label.
	 */
	public int controlHeight() {
		return (int) max(getLabelHeight(), 2 * RADIUS);
	}

	/**
	 * @return the left x-coordinate of the control.
	 */
	public int controlLeft() {
		return getPosX() - RADIUS;
	}

	/**
	 * @return the top y-coordinate of the control.
	 */
	public int controlTop() {
		return getPosY() - RADIUS;
	}
}