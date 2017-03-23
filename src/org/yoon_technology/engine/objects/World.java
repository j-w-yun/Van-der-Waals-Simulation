package org.yoon_technology.engine.objects;

import java.util.ArrayList;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class World {

	protected ArrayList<WorldObject> points;
	protected ArrayList<WorldObject> lines;
	protected ArrayList<WorldObject> rectangles;
	protected ArrayList<WorldText> texts;
	protected Vector3d origin;
	protected int width, height;

	protected World() {
		restoreWorldSettings();
	}

	public void restoreWorldSettings() {
		points = new ArrayList<>();
		lines = new ArrayList<>();
		rectangles = new ArrayList<>();
		texts = new ArrayList<>();
		origin = new Vector3d(0.0, 0.0, 0.0);
	}

	// Returns scale factor to display
	public double resized(int width, int height) {
		this.width = width;
		this.height = height;
		return 1.0;
	}

	public static World createEmptyWorld() {
		return new World();
	}

	public synchronized void addPoint(WorldObject object) {
		this.points.add(object);
	}

	public synchronized void addLine(WorldObject line) {
		this.lines.add(line);
	}

	public synchronized void addRectangle(WorldObject rectangle) {
		this.rectangles.add(rectangle);
	}

	public synchronized void addText(WorldText text) {
		this.texts.add(text);
	}

	public synchronized void update(double timePassed) {
		for(WorldObject object : points) {
			object.updatePosition(timePassed);
		}
		for(WorldText text : texts) {
			text.updatePosition(timePassed);
		}
	}

	public void sendMiliSecondTick() {
		// Implement in extensions
	}

	public void sendSecondTick() {
		// Implement in extensions
	}

	public synchronized void clear() {
		points.clear();
		rectangles.clear();
		lines.clear();
		texts.clear();
	}

	public synchronized ArrayList<WorldObject> getPoints() {
		return points;
	}

	public synchronized ArrayList<WorldObject> getLines() {
		return lines;
	}

	public synchronized ArrayList<WorldObject> getRectangles() {
		return rectangles;
	}

	public synchronized ArrayList<WorldText> getTexts() {
		return texts;
	}

	public synchronized Vector3d getOrigin() {
		return origin;
	}

	public synchronized void setOrigin(Vector3d origin) {
		this.origin = origin;
	}
}
