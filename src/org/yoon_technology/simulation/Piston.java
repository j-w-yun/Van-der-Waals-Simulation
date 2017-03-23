package org.yoon_technology.simulation;

import org.yoon_technology.engine.objects.WorldObject;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Piston extends WorldObject {

	public static final int X = 1;
	public static final int Y = 2;
	public static final int Z = 3;
	private int direction;
	private double velocity;
	private double mass;
	private String name;

	public Piston(double mass, int direction) {
		this.mass = mass;
		this.direction = direction;

		this.velocity = 0.0;
	}

	@Override
	public synchronized void updatePosition(double timePassed) {
		if(direction == Piston.X)
			this.getPosition().setX(this.position.getX() + (this.velocity*timePassed));
		else if(direction == Piston.Y)
			this.getPosition().setY(this.position.getY() + (this.velocity*timePassed));
		else if(direction == Piston.Z)
			this.getPosition().setZ(this.position.getZ() + (this.velocity*timePassed));
	}

	public int getDirection() {
		return this.direction;
	}

	public synchronized double getVelocity() {
		return velocity;
	}

	public synchronized void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public synchronized double getMass() {
		return mass;
	}

	public synchronized void setMass(double mass) {
		this.mass = mass;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
