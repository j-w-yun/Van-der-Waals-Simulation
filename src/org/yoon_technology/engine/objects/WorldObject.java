package org.yoon_technology.engine.objects;

import java.awt.Color;
import java.util.ArrayList;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class WorldObject {

	protected ArrayList<WorldObject> objects;
	protected WorldObjectProperty properties;
	protected Vector3d position;
	protected double radius;
	protected Color color;

	public WorldObject() {
		objects = new ArrayList<>();
		properties = new WorldObjectProperty();
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public Vector3d getPosition() {
		return position;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setProperties(WorldObjectProperty properties) {
		this.properties = properties;
	}

	public WorldObjectProperty getProperties() {
		return properties;
	}

	public void addObject(WorldObject object) {
		synchronized(this.objects) {
			this.objects.add(object);
		}
	}

	public void clear() {
		synchronized(this.objects) {
			objects.clear();
		}
	}

	public void updatePosition(double timePassed) {
		synchronized(this.objects) {
			for(WorldObject object : objects) {
				object.updatePosition(timePassed);
			}
		}
	}
}
