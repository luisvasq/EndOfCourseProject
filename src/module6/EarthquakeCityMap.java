package module6;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import processing.core.PApplet;

/**
 * EarthquakeCityMap: An application with an interactive map displaying
 * earthquake data. Date: July 17, 2015
 * 
 * @author: UC San Diego Intermediate Software Development MOOC team
 * @author Luis Vásquez-Peña
 */
public class EarthquakeCityMap extends PApplet {

	// We will use member variables, instead of local variables, to store the
	// data that the setUp and draw methods will need to access (as well as
	// other methods).
	// You will use many of these variables, but the only one you should need to
	// add code to modify is countryQuakes, where you will store the number of
	// earthquakes per country.

	// You can ignore this. It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;

	/**
	 * This is where to find the local tiles, for working without an Internet
	 * connection
	 */
	public static String mbTilesString = "blankLight-1-3.mbtiles";

	// feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";

	// The map
	private UnfoldingMap map;

	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;

	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;

	// EXTENSION IN MODULE 6
	private List<GUIControl> mapControls;
	// Option controls
	private OptionGroup grpTop;
	private GUIControl optAll, optTop10, optTop100;
	// Check controls
	private GUIControl chkPastHour, chkPastDay, chkPastWeek, chkPastMonth;
	private GUIControl chkShowCities, chkPlaceX, chkOceanThreat, chkThreat, chkShowLoc;

	public List<Marker> getCityMarkers() {
		return cityMarkers;
	}

	public void setup() {
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
			earthquakesURL = "2.5_week.atom"; // The same feed, but saved August
												// 7, 2015
		} else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			// earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);

		// Optional feed
		// earthquakesURL =
		// "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_month.atom";

		// New data for this module
		earthquakesURL = "all_month.atom";

		// (2) Reading in earthquake data and geometric properties
		// STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);

		// STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for (Feature city : cities) {
			cityMarkers.add(new CityMarker(city));
		}

		// STEP 3: read in earthquake RSS feed
		List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		quakeMarkers = new ArrayList<Marker>();

		for (PointFeature feature : earthquakes) {
			// check if LandQuake
			if (isLand(feature)) {
				quakeMarkers.add(new LandQuakeMarker(feature));
			}
			// OceanQuakes
			else {
				quakeMarkers.add(new OceanQuakeMarker(feature));
			}
		}

		// could be used for debugging
		printInfo();
		sortAndPrint(20);

		// (3) Add markers to map
		// NOTE: Country markers are not added to the map. They are used
		// for their geometric properties
		map.addMarkers(quakeMarkers);
		map.addMarkers(cityMarkers);

		// Create GUI controls
		mapControls = new ArrayList<GUIControl>();

		int xbase = 50;
		int ybase = 340;

		// Option controls
		ybase += 20; // Updating info display base
		grpTop = new OptionGroup();
		optAll = new OptionControl("All", xbase, ybase + 115);
		optTop10 = new OptionControl("Top 10", xbase, ybase + 135);
		optTop100 = new OptionControl("Top 100", xbase, ybase + 155);
		grpTop.add(optAll);
		grpTop.add(optTop10);
		grpTop.add(optTop100);

		// Check controls
		chkPastHour = new CheckControl("Past hour", xbase, ybase + 20);
		chkPastDay = new CheckControl("Past day", xbase, ybase + 40);
		chkPastWeek = new CheckControl("Past week", xbase, ybase + 60);
		chkPastMonth = new CheckControl("Past month", xbase, ybase + 80);

		ybase += 20; // Updating info display base
		chkShowCities = new CheckControl("Show cities", xbase, ybase + 170);
		chkPlaceX = new CheckControl("Highlight quakes", xbase, ybase + 190);
		chkOceanThreat = new CheckControl("Ocean threat lines", xbase, ybase + 210);
		chkThreat = new CheckControl("Show threat circle", xbase, ybase + 230);
		chkShowLoc = new CheckControl("Show coordinates", xbase, ybase + 250);

		// Default status values
		chkPastHour.setStatus(true);
		chkPastDay.setStatus(true);
		chkPastWeek.setStatus(true);
		chkPastMonth.setStatus(false);
		optAll.setStatus(true);
		chkShowCities.setStatus(true);
		chkPlaceX.setStatus(true);
		chkOceanThreat.setStatus(true);
		chkThreat.setStatus(true);
		chkShowLoc.setStatus(false);

		// Adding GUI controls to map
		// Option controls
		mapControls.add(optAll);
		mapControls.add(optTop10);
		mapControls.add(optTop100);
		mapControls.add(chkPastHour);
		mapControls.add(chkPastDay);
		mapControls.add(chkPastWeek);
		mapControls.add(chkPastMonth);
		// Check controls
		mapControls.add(chkPlaceX);
		mapControls.add(chkOceanThreat);
		mapControls.add(chkThreat);
		mapControls.add(chkShowCities);
		mapControls.add(chkShowLoc);

		setVisibilityOfMarkers();
		highlightMarkers();
	} // End setup

	// Helper method which prints out some info about the earthquakes
	private void printInfo() {
		System.out.println("\n**********************\n***EARTHQUAKES DATA***\n**********************");
		printQuakes();
	}

	public void draw() {
		background(0);
		map.draw();

		// If some earthquake marker was clicked on
		if (lastClicked instanceof EarthquakeMarker) {
			// Get quake location on map
			Location quakeLoc = lastClicked.getLocation();
			ScreenPosition quakePos = map.getScreenPosition(quakeLoc);
			EarthquakeMarker quake = (EarthquakeMarker) lastClicked;
			double threatRadius = quake.threatCircle();

			// Saving graph style
			pushStyle();

			// Selected ocean quake affecting city
			if (lastClicked instanceof OceanQuakeMarker && chkOceanThreat.getStatus() && chkShowCities.getStatus()) {

				// Setting lines style to be the same as marker's
				strokeWeight(2);
				quake.colorStrokeDetermine(g);

				ScreenPosition cityPos;

				// Looping through cities
				for (Marker marker : cityMarkers) {
					if (lastClicked.getDistanceTo(marker.getLocation()) <= threatRadius) {
						cityPos = map.getScreenPosition(marker.getLocation());
						g.line(quakePos.x, quakePos.y, cityPos.x, cityPos.y);
					}
				}
			}

			// Show threat circle for selected earthquake
			// This is only valid for small threat radius
			if (chkThreat.getStatus()) {
				// Bisection method (analogous to binary search)
				int k = 0; // number of iterations
				float a = 0, b = 5000, diam = a + .5f * (b - a);
				double f = 0, eps = .001;

				while (b - a > eps && k++ < 1000)
					if ((f = quakeLoc.getDistance(map.getLocation(quakePos.x + (diam = a + .5f * (b - a)),
							quakePos.y + diam))) < threatRadius)
						a = diam;
					else if (f > threatRadius)
						b = diam;
					else
						break;
				diam *= 2 * sqrt(2);
				quake.colorDetermine(g, 50);
				quake.colorStrokeDetermine(g);
				ellipse(quakePos.x, quakePos.y, diam, diam);
			}

			// Recovering graph style
			popStyle();
		}

		if (chkShowLoc.getStatus()) {
			fill(0);
			// Gets geographic location pointed by cursor position
			Location locMouse = map.getLocation(mouseX, mouseY);
			DecimalFormat df = new DecimalFormat("0.000");
			String posInfo = "Lat: " + df.format(abs(locMouse.getLat())) + "° " + (locMouse.getLat() > 0 ? "N" : "S")
					+ ", Lon: " + df.format(abs(locMouse.getLon())) + "° " + (locMouse.getLon() > 0 ? "E" : "W");

			// If an earthquake was selected, gets the geographic distance
			// from the location pointed by the mouse cursor
			if (lastClicked instanceof EarthquakeMarker)
				posInfo += "\nDistance: " + df.format(lastClicked.getDistanceTo(locMouse)) + " km";

			text(posInfo, mouseX - textWidth(posInfo) / 2, mouseY + 20);
		}

		if (lastSelected != null && !lastSelected.isHidden()) {
			ScreenPosition selectedPos = map.getScreenPosition(lastSelected.getLocation());
			lastSelected.showTitle(g, selectedPos.x, selectedPos.y);
		}

		// Adding left panel (key and map options)
		addKey();
		addGUIControls();
	}

	private void sortAndPrint(int numToPrint) {
		Object[] quakes = quakeMarkers.toArray();
		int i = 0;

		// Built-in Java method for sorting an array of objects
		Arrays.sort(quakes, Collections.reverseOrder());

		while (i < quakes.length && i < numToPrint)
			System.out.println(quakes[i++]);
	}

	/**
	 * Event handler that gets called automatically when the mouse moves.
	 */
	@Override
	public void mouseMoved() {
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		}

		selectMarkerIfHover(cityMarkers);
		selectMarkerIfHover(quakeMarkers);
	}

	private void selectMarkerIfHover(List<Marker> markers) {
		if (lastSelected == null)
			for (Marker marker : markers) {
				if (marker.isInside(map, mouseX, mouseY)) {
					marker.setSelected(true);
					lastSelected = (CommonMarker) marker;
					break;
				}
			}
	}

	/**
	 * The event handler for mouse clicks It will display an earthquake and its
	 * threat circle of cities Or if a city is clicked, it will display all the
	 * earthquakes where the city is in the threat circle
	 */
	@Override
	public void mouseClicked() {
		// Checking controls
		if (!selectControlIfClicked())
			if (lastClicked == null) {
				// Checking markers
				selectMarkerIfClicked(cityMarkers);
				selectMarkerIfClicked(quakeMarkers);
				if (lastClicked != null)
					lastClicked.setClicked(true);
			} else {
				lastClicked.setClicked(false);
				lastClicked = null;
			}
		setVisibilityOfMarkers();

		int n = quakeMarkers.size();
		if (optTop10.getStatus())
			n = 10;
		else if (optTop100.getStatus())
			n = 100;

		printInfo();
		for (Marker marker : quakeMarkers) {
			if (!marker.isHidden()) {
				System.out.println(marker);
				if (--n < 1)
					break;
			}
		}
	}

	// Helper method that will check if any marker is clicked on
	public void selectMarkerIfClicked(List<Marker> markers) {
		if (lastClicked == null) {
			for (Marker marker : markers)
				if (marker.isInside(map, mouseX, mouseY) && !marker.isHidden()) {
					lastClicked = (CommonMarker) marker;
					break;
				}
		}
	}

	// Helper method that will select and check if any control is clicked on
	public boolean selectControlIfClicked() {
		for (GUIControl mapControl : mapControls)
			if (mapControl.contains(mouseX, mouseY)) {
				mapControl.click();
				if (mapControl instanceof CheckControl) {
					CheckControl chk = (CheckControl) mapControl;
					if (chk == chkPlaceX || chk == chkPastHour || chk == chkPastDay)
						highlightMarkers();
				} else if (mapControl instanceof OptionControl) {
					// Option controls
					OptionControl opt = (OptionControl) mapControl;
					if ((opt == optTop10 && optTop10.getStatus()) || (opt == optTop100 && optTop100.getStatus())) {
						quakeMarkers.sort(Collections.reverseOrder());
					}
				}
				return true;
			}
		// No control was clicked on
		return false;
	}

	// loop over and hide all markers
	private void hideMarkers() {

		for (Marker marker : quakeMarkers) {
			marker.setHidden(true);
		}

		for (Marker marker : cityMarkers) {
			marker.setHidden(true);
		}
	}

	// Helper method to determine which markers must be unhidden
	private void setVisibilityOfMarkers() {
		// Hide all markers
		hideMarkers();

		// Show clicked marker
		if (lastClicked != null)
			if (!(lastClicked instanceof CityMarker && !chkShowCities.getStatus()))
				lastClicked.setHidden(false);

		showValidMarkersOf(quakeMarkers);
		showValidMarkersOf(cityMarkers);
	}

	// Loop through quakeMarkers to determine which earthquakes should be
	// highlighted (add an X)
	private void highlightMarkers() {
		String age;
		for (Marker marker : quakeMarkers) {
			age = marker.getStringProperty("age");
			((EarthquakeMarker) marker)
					.setHighlighted(chkPlaceX.getStatus() && (age.equals("Past Hour") || age.equals("Past Day")));
		}
	}

	// Loop through quakeMarkers and cityMarkers to determine which
	// quakes or cities will be set unhidden
	private void showValidMarkersOf(List<Marker> markers) {
		int n = 0;
		Location loc = null;
		double threatRadius = 0;

		// Optimizing calculations
		if (lastClicked != null) {
			// Get location of an earthquake/city marker
			loc = lastClicked.getLocation();
			// Get the threat circle radius (constant when looping through
			// cityMarkers)
			if (lastClicked instanceof EarthquakeMarker)
				threatRadius = ((EarthquakeMarker) lastClicked).threatCircle();
		}

		for (Marker marker : markers) {
			if (marker instanceof EarthquakeMarker && !(lastClicked instanceof EarthquakeMarker)) {
				String age = marker.getStringProperty("age");

				if ((optAll.getStatus() || (optTop10.getStatus() && n < 10) || (optTop100.getStatus() && n < 100))
						&& ((chkPastHour.getStatus() && "Past Hour".equals(age))
								|| (chkPastDay.getStatus() && "Past Day".equals(age))
								|| (chkPastWeek.getStatus() && "Past Week".equals(age))
								|| (chkPastMonth.getStatus() && "Past Month".equals(age)))) {

					if (lastClicked != null)
						if (marker.getDistanceTo(loc) > ((EarthquakeMarker) marker).threatCircle())
							continue;

					if (!optAll.getStatus())
						n++;

					// Show quake marker
					marker.setHidden(false);
				}
			} else if (marker instanceof CityMarker && !(lastClicked instanceof CityMarker)) {
				// In case the user chose to hide all the cities
				if (!chkShowCities.getStatus())
					break;

				// Otherwise, all cities are unhidden unless they are not
				// contained within a selected earthquake threat circle
				if (lastClicked != null)
					if (marker.getDistanceTo(loc) > threatRadius)
						continue;

				// Show city marker
				marker.setHidden(false);
			}
		}
	}

	// helper method to draw key in GUI
	private void addKey() {

		text("4.0+ Magnitude", 75, 175);
		text("Below 4.0", 75, 225);
		fill(255, 250, 240);

		int xbase = 25;
		int ybase = 50;

		rect(xbase, ybase, 150, 250);

		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase + 25, ybase + 25);

		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase - CityMarker.TRI_SIZE, tri_xbase - CityMarker.TRI_SIZE,
				tri_ybase + CityMarker.TRI_SIZE, tri_xbase + CityMarker.TRI_SIZE, tri_ybase + CityMarker.TRI_SIZE);

		fill(0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		text("Land Quake", xbase + 50, ybase + 70);
		text("Ocean Quake", xbase + 50, ybase + 90);
		text("Size ~ Magnitude", xbase + 25, ybase + 110);

		text("Shallow", xbase + 50, ybase + 140);
		text("Intermediate", xbase + 50, ybase + 160);
		text("Deep", xbase + 50, ybase + 180);

		text("Past hour", xbase + 50, ybase + 200);

		fill(255);
		ellipse(xbase + 35, ybase + 70, 10, 10);
		rect(xbase + 35 - 5, ybase + 90 - 5, 10, 10);

		fill(color(255, 255, 0));
		ellipse(xbase + 35, ybase + 140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase + 35, ybase + 160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase + 35, ybase + 180, 12, 12);

		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase + 50, ybase + 140);
		text("Intermediate", xbase + 50, ybase + 160);
		text("Deep", xbase + 50, ybase + 180);

		text("Past hour", xbase + 50, ybase + 200);

		fill(255, 255, 255);
		int centerx = xbase + 35;
		int centery = ybase + 200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx - 8, centery - 8, centerx + 8, centery + 8);
		line(centerx - 8, centery + 8, centerx + 8, centery - 8);
	}

	// helper method to draw controls in GUI
	private void addGUIControls() {

		int xbase = 25;
		int ybase = 325;

		pushStyle();

		fill(255, 250, 240);
		rect(xbase, ybase, 150, 325);

		fill(0);
		textSize(13);
		text("Map options", xbase + 35, ybase + 15);
		textSize(12);

		ybase += 10;

		for (GUIControl mapControl : mapControls)
			mapControl.draw(g);

		// Title and age separation line
		strokeWeight(3);
		line(xbase, ybase += 25, xbase + 150, ybase);
		// Age and magnitude separation line
		line(xbase, ybase += 98, xbase + 150, ybase);
		// Magnitude and extra options separation line
		line(xbase, ybase += 75, xbase + 150, ybase);

		popStyle();
	}

	// Checks whether this quake occurred on land. If it did, it sets the
	// "country" property of its PointFeature to the country where it occurred
	// and returns true. Notice that the helper method isInCountry will
	// set this "country" property already. Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {

		// IMPLEMENT THIS: loop over all countries to check if location is in
		// any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this
		// country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}

		// not inside any country
		return false;
	}

	private void printQuakes() {
		Map<Object, Integer> quakesPerCtry = new HashMap<>();
		int quakesInOcean = 0;

		for (Marker feature : quakeMarkers) {
			if (!feature.isHidden()) {
				Object k = feature.getProperty("country");
				if (k == null) {
					// In the ocean
					quakesInOcean++;
				} else if (quakesPerCtry.containsKey(k)) {
					// Update number of earthquakes for country
					quakesPerCtry.put(k, quakesPerCtry.get(k) + 1);
				} else {
					// Add country to the Map data structure
					quakesPerCtry.put(k, 1);
				}
			}
		}

		for (Map.Entry<Object, Integer> entry : quakesPerCtry.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		System.out.println("OCEAN QUAKES: " + quakesInOcean);
	}

	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the
	// earthquake feature if
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use
		// isInsideByLoc
		if (country.getClass() == MultiMarker.class) {

			// looping over markers making up MultiMarker
			for (Marker marker : ((MultiMarker) country).getMarkers()) {

				// checking if inside
				if (((AbstractShapeMarker) marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));

					// return if is inside one
					return true;
				}
			}
		}

		// check if inside country represented by SimplePolygonMarker
		else if (((AbstractShapeMarker) country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));

			return true;
		}
		return false;
	}
}
