package org.yoon_technology.engine;

import java.util.ArrayList;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class World {

	protected ArrayList<WorldObject> objects;
	protected ArrayList<WorldText> texts;
	protected Vector3d origin;
	protected int width, height;

	protected World() {
		restoreWorldSettings();
	}

	public void restoreWorldSettings() {
		objects = new ArrayList<>();
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

	public void addObject(WorldObject object) {
		synchronized(this.objects) {
			this.objects.add(object);
		}
	}

	public void addText(WorldText text) {
		synchronized(this.texts) {
			this.texts.add(text);
		}
	}

	public void update(double timePassed) {
		synchronized(this.objects) {
			for(WorldObject object : objects) {
				object.update(timePassed);
			}
		}
		synchronized(this.texts) {
			for(WorldText text : texts) {
				text.update(timePassed);
			}
		}
	}

	public void sendSecondTick() {
		// Implement in extensions
	}

	public void clear() {
		synchronized(this.objects) {
			objects.clear();
		}
		synchronized(this.texts) {
			texts.clear();
		}
	}

	public ArrayList<WorldObject> getObjects() {
		return objects;
	}

	public ArrayList<WorldText> getTexts() {
		return texts;
	}

	public Vector3d getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3d origin) {
		this.origin = origin;
	}
}
