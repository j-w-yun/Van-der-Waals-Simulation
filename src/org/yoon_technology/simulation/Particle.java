package org.yoon_technology.simulation;

import org.yoon_technology.engine.objects.WorldObject;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Particle extends WorldObject {

	private Vector3d velocity;
	private Vector3d force;
	private double mass;

	public Particle() {
		this.force = new Vector3d(0.0, 0.0, 0.0);
	}

	public void setVelocity(Vector3d velocity) {
		this.velocity = velocity;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public void setForce(Vector3d force) {
		this.force = force;
	}

	public Vector3d getForce() {
		return force;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMass() {
		return mass;
	}

	public void updateForce(double timePassed) {

	}

	@Override
	public void updatePosition(double timePassed) {
		//		position.set(position.add(springVel.mul(timePassed)));

		position.set(position.add(velocity.mul(timePassed)));
	}

	// Returns true if two are colliding
	public synchronized boolean checkCollision(Particle other, double passedTime) {
		// Movement vector spatially adjusted to have B stationary
		Vector3d movementVector = this.velocity.sub(other.velocity);
		movementVector.set(movementVector.mul(passedTime));

		Vector3d deltaPosition = other.getPosition().sub(this.getPosition());
		double dist = deltaPosition.length();
		double sumRadii = this.getRadius() + other.getRadius();
		dist -= sumRadii;

		// Escape #1
		double mag = movementVector.length();
		if(mag < dist) {
			//			System.out.println("1");
			return false;
		}



		// Escape #2
		Vector3d N = movementVector.normalized();
		double D = N.dot(deltaPosition);
		if(D <= 0) {
			//			System.out.println("2");
			return false;
		}



		// Escape #3
		double F = (dist * dist) - (D * D);
		double sumRadiiSquared = sumRadii * sumRadii;
		if(F >= sumRadiiSquared) {
			//			System.out.println("3");
			return false;
		}



		// Escape #4
		double T = sumRadiiSquared - F;
		if(T < 0) {
			//			System.out.println("4");
			return false;
		}



		double distance = D - Math.sqrt(T);
		if(mag < distance) {
			//			System.out.println("5");
			return false;
		}

		movementVector.normalized();
		movementVector.mul(distance * passedTime);

		// Point of contact
		Vector3d thisPosition = this.getPosition().sub(movementVector);
		Vector3d otherPosition = other.getPosition().add(movementVector);

		// Directional vector along the point of contact
		Vector3d n = otherPosition.sub(thisPosition).normalized();

		// Project velocities of A and B onto n
		double a1 = this.getVelocity().dot(n);
		double a2 = other.getVelocity().dot(n);

		double p = (2.0 * (a1 - a2)) / (this.getMass() + other.getMass());

		Vector3d aVel = this.getVelocity().sub( n.mul(other.getMass() * p) );
		Vector3d bVel = other.getVelocity().add( n.mul(this.getMass() * p) );

		this.setVelocity(aVel);
		other.setVelocity(bVel);

		return true;
	}


	//	public synchronized boolean checkCollision(Particle other, double passedTime) {
	//		/*
	//		 * REJECT CASE NO. 1
	//		 *
	//		 * Check whether vector "..." is greater than or equal to vector "xxx"
	//		 * (has the capacity to reach distance between A and B)
	//		 *
	//		 * That is, if length of A's movement vector is less than
	//		 * 		distance(A, B) - (B.radius + A.radius)
	//		 * then collision could not happen
	//		 *
	//		 *		   .	 	B			where B is at (	0, 4)
	//		 *       .  	 x  ^			where A is at (-3, 0)
	//		 *     .    x		|
	//		 * 	 . x			|			then B - A is (3, 4) which represents vector "xxx"
	//		 * A <-------------( )origin	(vector whose length is the distance from A to B)
	//		 */
	//
	//		// (Distance from A to B)^2 because sqrt is computationally expensive
	//		Vector3d deltaPosition = other.getPosition().sub(this.getPosition());
	//		double deltaPositionLengthSqSubR = deltaPosition.dot(deltaPosition);
	//
	//		double sumRadii = this.getRadius() + other.getRadius();
	//		deltaPositionLengthSqSubR -= (sumRadii * sumRadii);
	//
	//		// Movement vector spatially adjusted to have B stationary
	//		Vector3d movementVector = this.velocity.sub(other.velocity);
	//
	//		// Account for delta time
	//		movementVector.set(movementVector.mul(passedTime));
	//
	//		double movementVectorLengthSq = movementVector.dot(movementVector);
	//
	//		if(movementVectorLengthSq < deltaPositionLengthSqSubR) {
	//			//			System.out.println("1");
	//			return false;
	//		}
	//
	//		/*b
	//		 * REJECT CASE NO. 2
	//		 *
	//		 * Check whether A is moving towards B
	//		 *
	//		 * That is, project the movement vector onto distance vector from A to B
	//		 * and see if it is greater than zero
	//		 * If not, the movement vector is conflicting directionally with distance vector
	//		 */
	//
	//		Vector3d movementVectorScalar = movementVector.normalized();
	//		double d = movementVectorScalar.dot(deltaPosition);
	//		if(d <= 0) {
	//			//			System.out.println("2");
	//			return false;
	//		}
	//
	//		/*
	//		 * REJECT CASE NO. 3
	//		 */
	//		double deltaPositionLengthSq = deltaPosition.dot(deltaPosition);
	//		double f = deltaPositionLengthSq - (d * d);
	//
	//		double sumRadiiSquared = sumRadii * sumRadii;
	//		if(f >= sumRadiiSquared) {
	//			//			System.out.println("3");
	//			return false;
	//		}
	//
	//		/*
	//		 * Computation heavy checking
	//		 */
	//		// Distance the circle has to travel along
	//		double t = sumRadiiSquared - f;
	//		double distance = d - Math.sqrt(t);
	//
	//		//		double mag = movementVector.length();
	//		//		if(mag < distance) {
	//		//			return false;
	//		//		}
	//
	//		// Point of contact
	//		this.setPosition(this.getPosition().add(
	//				this.getPosition().normalized().mul(distance).mul(passedTime)
	//				));
	//		other.setPosition(other.getPosition().sub(
	//				other.getPosition().normalized().mul(distance).mul(passedTime)
	//				));
	//
	//		// Directional vector along the point of contact
	//		Vector3d n = deltaPosition.normalized();
	//
	//		// Project velocities of A and B onto n
	//		double a1 = this.getVelocity().dot(n);
	//		double a2 = other.getVelocity().dot(n);
	//
	//		double p = (2.0 * (a1 - a2)) / (this.getMass() + other.getMass());
	//
	//		Vector3d aVel = this.getVelocity().sub( n.mul(other.getMass() * p) );
	//		Vector3d bVel = other.getVelocity().add( n.mul(this.getMass() * p) );
	//
	//		this.setVelocity(aVel);
	//		other.setVelocity(bVel);
	//
	//		return true;
	//	}
}