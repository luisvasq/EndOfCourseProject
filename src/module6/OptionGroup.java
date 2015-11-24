package module6;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a group of option controls to allow an unique selection of an option
 * control within the group. Option groups are added to an </code>
 * ArrayList</code> structure, represented by the field
 * <code>optionControls</code>.
 * 
 * @author Luis Vásquez-Peña
 * @version 2015-11-15
 */
public class OptionGroup {

	private List<OptionControl> optionControls;

	/**
	 * Construct an empty OptionGroup object.
	 */
	public OptionGroup() {
		optionControls = new ArrayList<OptionControl>();
	}

	/**
	 * @return the optionControls
	 */
	public List<OptionControl> getOptionControls() {
		return optionControls;
	}

	/**
	 * Adds a new OptionControl object to the group.
	 * 
	 * @param opt
	 *            The OptionControl object to be added.
	 */
	public void add(OptionControl opt) {
		OptionGroup grpOld = opt.getRefToGroup();

		// Remove OptionControl opt from previous group
		if (grpOld != null)
			grpOld.remove(opt);

		// Add OptionControl opt reference to this group
		opt.setRefToGroup(this);
		optionControls.add(opt);
	}

	/**
	 * Casts a GUIControl to an OptionControl object and add it to the group.
	 * 
	 * @param opt
	 *            the GUIControl to be cast and added to the group.
	 */
	public void add(GUIControl opt) {
		// Useful cast shortcut
		add((OptionControl) opt);
	}

	/**
	 * Removes an OptionControl object from the group.
	 * 
	 * @param opt
	 *            The OptionControl object to be removed.
	 */
	public void remove(OptionControl opt) {
		// Set opt reference to group to null value (consistency)
		opt.setRefToGroup(null);
		optionControls.remove(opt);
	}

	/**
	 * Gets the selected option control from the group.
	 * 
	 * @return The OptionControl selected from the group.
	 */
	public OptionControl getSelectedOption() {
		for (OptionControl opt : optionControls)
			if (opt.getStatus())
				return opt;
		return null;
	}
}