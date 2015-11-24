package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/**
 * Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 */
public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker> {

	// Did the earthquake occur on land? This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function
	// based on magnitude.
	protected float radius;

	// Records whether this earthquake was requested to be highlighted
	private boolean highlighted;

	// Constants for distance
	protected static final float kmPerMile = 1.609344f;

	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// Getters and setters for earthquake properties
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}

	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());
	}

	public String getTitle() {
		return (String) getProperty("title");

	}

	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}

	public boolean isOnLand() {
		return isOnLand;
	}

	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);

	// constructor
	public EarthquakeMarker(PointFeature feature) {
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2 * magnitude);
		setProperties(properties);
		this.radius = 1.75f * getMagnitude();
	}

	public int compareTo(EarthquakeMarker marker) {
		if (getMagnitude() < marker.getMagnitude())
			return -1;
		else if (getMagnitude() > marker.getMagnitude())
			return 1;
		else
			return 0;
	}

	// Calls abstract method drawEarthquake and then checks age and draws X if
	// needed
	public void drawMarker(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();

		// determine color of marker from depth
		colorDetermine(pg);

		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);

		// IMPLEMENT: add X over marker if within past day
		if (highlighted) {
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x - (radius + buffer), y - (radius + buffer), x + radius + buffer, y + radius + buffer);
			pg.line(x - (radius + buffer), y + (radius + buffer), x + radius + buffer, y - (radius + buffer));
		}

		// reset to previous styling
		pg.popStyle();
	}

	/**
	 * Show the title of the earthquake if this marker is selected. For this
	 * method, parameters x and y are not the map positions of the outer object,
	 * but the screen positions of the marker.
	 */
	public void showTitle(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();

		// Adjust display of text inside the rectangle
		float dx = 3, dy = 1, h = 14;
		pg.strokeWeight(1);
		pg.textSize(10);
		
		// Drawing rectangle underneath the text
		pg.fill(255, 255, 224);
		pg.rect(x + radius + dx, y - h / 2 - dy, pg.textWidth(getTitle()) + 2 * dx, h + 3*dy);

		pg.fill(0);
		pg.text(getTitle(), x + radius + 2 * dx, y);

		// Restore previous drawing style
		pg.popStyle();
	}

	/**
	 * Return the "threat circle" radius, or distance up to which this
	 * earthquake can affect things, for this earthquake. DISCLAIMER: this
	 * formula is for illustration purposes only and is not intended to be used
	 * for safety-critical or predictive applications.
	 */
	public double threatCircle() {
		double miles = 20.0f * Math.pow(1.8, 2 * getMagnitude() - 5);
		double km = (miles * kmPerMile);
		return km;
	}

	// determine color of marker from depth
	// We use: Deep = red, intermediate = blue, shallow = yellow
	public void colorDetermine(PGraphics pg) {
		float depth = getDepth();

		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		} else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		} else {
			pg.fill(255, 0, 0);
		}
	}

	// Overloaded method (includes opacity)
	public void colorDetermine(PGraphics pg, float opacity) {
		float depth = getDepth();

		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0, opacity);
		} else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255, opacity);
		} else {
			pg.fill(255, 0, 0, opacity);
		}
	}

	public void colorStrokeDetermine(PGraphics pg) {
		float depth = getDepth();

		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.stroke(255, 255, 0);
		} else if (depth < THRESHOLD_DEEP) {
			pg.stroke(0, 0, 255);
		} else {
			pg.stroke(255, 0, 0);
		}
	}

	/**
	 * toString Returns an earthquake marker's string representation
	 * 
	 * @return the string representation of an earthquake marker.
	 */
	public String toString() {
		return getTitle();
	}
}