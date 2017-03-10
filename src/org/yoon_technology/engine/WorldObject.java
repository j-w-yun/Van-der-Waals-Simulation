package org.yoon_technology.engine;

import java.util.ArrayList;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class WorldObject {

	private ArrayList<WorldObject> objects;
	private WorldObjectProperty properties;
	private Vector3d position;
	private Vector3d velocity;
	private double radius;
	private double mass;

	public WorldObject() {
		objects = new ArrayList<>();
		//		properties = new WorldObjectProperty();
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public Vector3d getPosition() {
		return position;
	}

	public void setVelocity(Vector3d velocity) {
		this.velocity = velocity;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMass() {
		return mass;
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

	public void update(double timePassed) {
		if(velocity == null) // Static objects have null velocity
			return;

		position.set(position.add(velocity.mul(timePassed)));

		synchronized(this.objects) {
			for(WorldObject object : objects) {
				object.update(timePassed);
			}
		}
	}
}
