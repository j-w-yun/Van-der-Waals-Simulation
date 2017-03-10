package org.yoon_technology.engine;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class WorldObjectProperty implements Iterable<Object> {

	public static final int POINTS = 0;
	public static final int LINES = 1;
	public static final int END_LINES = 2;

	private ArrayList<Object> properties;
	private int drawMode;

	public WorldObjectProperty() {
		this.properties = new ArrayList<>();
	}

	public void addProperty(Object property) {
		this.properties.add(property);
	}

	public void setProperty(ArrayList<Object> properties) {
		this.properties = properties;
	}

	public ArrayList<Object> getProperty() {
		return properties;
	}

	public void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
	}

	public int getDrawMode() {
		return drawMode;
	}

	@Override
	public Iterator<Object> iterator() {
		return properties.iterator();
	}
}
