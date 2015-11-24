package module6;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PGraphics;

/**
 * Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 */
public class CityMarker extends CommonMarker {

	public static int TRI_SIZE = 5; // The size of the triangle marker

	public CityMarker(Location location) {
		super(location);
	}

	public CityMarker(Feature city) {
		super(((PointFeature) city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}

	// pg is the graphics object on which you call the graphics
	// methods. e.g. pg.fill(255, 0, 0) will set the color to red
	// x and y are the center of the object to draw.
	// They will be used to calculate the coordinates to pass
	// into any shape drawing methods.
	// e.g. pg.rect(x, y, 10, 10) will draw a 10x10 square
	// whose upper left corner is at position x, y
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		// System.out.println("Drawing a city");
		// Save previous drawing style
		pg.pushStyle();

		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 30, 30);
		pg.triangle(x, y - TRI_SIZE, x - TRI_SIZE, y + TRI_SIZE, x + TRI_SIZE, y + TRI_SIZE);

		// Restore previous drawing style
		pg.popStyle();
	}

	/**
	 * Show the title of the city if this marker is selected. For this method,
	 * parameters x and y are not the map positions of the outer object, but the
	 * screen positions of the marker.
	 */

	public void showTitle(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();

		String title = getCity() + ", " + getCountry() + "\nPop. " + getPopulation() + " million";

		// Adjust display of text inside the rectangle
		float dx = 3, dy = 12, h = 22;
		pg.strokeWeight(1);
		pg.textSize(10);

		// Drawing rectangle underneath the text
		pg.fill(255, 255, 224);
		pg.rect(x + radius + dx, y - h / 3 - dy, pg.textWidth(title) + 2 * dx, h + dy);

		pg.fill(0);
		pg.text(title, x + radius + 2 * dx, y - dy / 3);

		// Restore previous drawing style
		pg.popStyle();
	}

	private String getCity() {
		return getStringProperty("name");
	}

	private String getCountry() {
		return getStringProperty("country");
	}

	private float getPopulation() {
		return Float.parseFloat(getStringProperty("population"));
	}
}
