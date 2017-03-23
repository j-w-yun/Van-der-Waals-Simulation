package org.yoon_technology.simulation;

import java.util.ArrayList;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class PistonBox {

	// Piston resides on the lower axis value than the particles this holds
	// Lower pistons
	Piston pistonX1;
	Piston pistonY1;
	Piston pistonZ1;
	// Upper pistons
	Piston pistonX2;
	Piston pistonY2;
	Piston pistonZ2;
	// Lower boxes
	PistonBox boxX;
	PistonBox boxY;
	PistonBox boxZ;
	// Upper box
	ArrayList<Particle> particles;

	PistonBox() {
		this(null);
	}

	PistonBox(Piston piston) {
		this.particles = new ArrayList<>();
		if(piston != null)
			this.split(piston);
	}

	void split(Piston piston) {
		switch(piston.getDirection()) {
			case Piston.X:
				if(boxX == null) {
					boxX = new PistonBox();
					this.pistonX1 = piston;
					boxX.pistonX2 = piston;
				} else {
					boxX.split(piston);
				}
				if(boxY != null)
					boxY.split(piston);
				if(boxZ != null)
					boxZ.split(piston);
				break;
			case Piston.Y:
				if(boxX != null)
					boxX.split(piston);
				if(boxY == null) {
					boxY = new PistonBox();
					boxY.pistonX1 = this.pistonX1;
					boxY.pistonX2 = this.pistonX2;
					this.pistonY1 = piston;
					boxY.pistonY2 = piston;
				} else {
					boxY.split(piston);
				}
				if(boxZ != null)
					boxZ.split(piston);
				break;
			case Piston.Z:
				if(boxX != null)
					boxX.split(piston);
				if(boxY != null)
					boxY.split(piston);
				if(boxZ == null) {
					boxZ = new PistonBox();
					boxZ.pistonX1 = this.pistonX1;
					boxZ.pistonX2 = this.pistonX2;
					boxZ.pistonY1 = this.pistonY1;
					boxZ.pistonY2 = this.pistonY2;
					this.pistonZ1 = piston;
					boxZ.pistonZ2 = piston;
				} else {
					boxZ.split(piston);
				}
				break;
		}

	}

	/*
	 * If the particle is on a higher axis value (or equal), add to this
	 * If the particle is on a lower axis value, leave it to a lower box to figure out its placement
	 */
	void addParticle(Particle particle) {
		addParticleXDIR(particle);
	}

	private void addParticleXDIR(Particle particle) {
		if(pistonX1 != null) {
			double minX = pistonX1.getPosition().getX();
			if(particle.getPosition().getX() > minX) {
				addParticleYDIR(particle);
			} else {
				boxX.addParticleXDIR(particle);
			}
		} else {
			addParticleYDIR(particle);
		}
	}

	private void addParticleYDIR(Particle particle) {
		if(pistonY1 != null) {
			double minY = pistonY1.getPosition().getY();
			if(particle.getPosition().getY() > minY) {
				addParticleZDIR(particle);
			} else {
				boxY.addParticleYDIR(particle);
			}
		} else {
			addParticleZDIR(particle);
		}
	}

	private void addParticleZDIR(Particle particle) {
		if(pistonZ1 != null) {
			double minZ = pistonZ1.getPosition().getZ();
			if(particle.getPosition().getZ() > minZ) {
				this.particles.add(particle);
			} else {
				boxZ.addParticleZDIR(particle);
			}
		} else {
			this.particles.add(particle);	// Base
		}
	}

	ArrayList<Particle> getParticles() {
		return this.particles;
	}

	ArrayList<PistonBox> getBoxes() {
		ArrayList<PistonBox> boxes = new ArrayList<>();
		return this.getBoxes(boxes);
	}

	private ArrayList<PistonBox> getBoxes(ArrayList<PistonBox> boxes) {
		boxes.add(this);

		if(this.boxX != null)
			this.boxX.getBoxes(boxes);
		if(this.boxY != null)
			this.boxY.getBoxes(boxes);
		if(this.boxZ != null)
			this.boxZ.getBoxes(boxes);

		return boxes;
	}

	ArrayList<Piston> getPistons() {
		ArrayList<Piston> pistons = new ArrayList<>();
		return this.getPistons(pistons);
	}

	private ArrayList<Piston> getPistons(ArrayList<Piston> pistons) {
		if(pistonX1 != null)
			pistons.add(pistonX1);
		if(pistonY1 != null)
			pistons.add(pistonY1);
		if(pistonZ1 != null)
			pistons.add(pistonZ1);

		if(this.boxX != null)
			this.boxX.getPistons(pistons);
		if(this.boxY != null)
			this.boxY.getPistons(pistons);
		if(this.boxZ != null)
			this.boxZ.getPistons(pistons);

		return pistons;
	}

	public void clear() {
		if(boxX != null)
			boxX.clear();
		if(boxY != null)
			boxY.clear();
		if(boxZ != null)
			boxZ.clear();

		this.pistonX1 = null;
		this.pistonY1 = null;
		this.pistonZ1 = null;

		this.pistonX2 = null;
		this.pistonY2 = null;
		this.pistonZ2 = null;

		this.boxX = null;
		this.boxY = null;
		this.boxZ = null;
	}

	// Sole caller uses topBox
	void updateParticles(double passedTime) {
		for(PistonBox box : this.getBoxes()) {

			int index = 0;
			for(Particle particle : box.getParticles()) {

				if(box.pistonX1 != null)
					if(particle.getPosition().getX() < box.pistonX1.getPosition().getX())
						particle.getPosition().setX(box.pistonX1.getPosition().getX() + particle.getRadius());
				if(box.pistonY1 != null)
					if(particle.getPosition().getY() < box.pistonY1.getPosition().getY())
						particle.getPosition().setY(box.pistonY1.getPosition().getY() + particle.getRadius());
				if(box.pistonZ1 != null)
					if(particle.getPosition().getZ() < box.pistonZ1.getPosition().getZ())
						particle.getPosition().setZ(box.pistonZ1.getPosition().getZ() + particle.getRadius());

				if(box.pistonX2 != null)
					if(particle.getPosition().getX() > box.pistonX2.getPosition().getX())
						particle.getPosition().setX(box.pistonX2.getPosition().getX() - particle.getRadius());
				if(box.pistonY2 != null)
					if(particle.getPosition().getY() > box.pistonY2.getPosition().getY())
						particle.getPosition().setY(box.pistonY2.getPosition().getY() - particle.getRadius());
				if(box.pistonZ2 != null)
					if(particle.getPosition().getZ() > box.pistonZ2.getPosition().getZ())
						particle.getPosition().setZ(box.pistonZ2.getPosition().getZ() - particle.getRadius());

				Piston[] pistons = new Piston[6];
				pistons[0] = box.pistonZ1;
				pistons[1] = box.pistonY1;
				pistons[2] = box.pistonX1;
				pistons[3] = box.pistonZ2;
				pistons[4] = box.pistonY2;
				pistons[5] = box.pistonX2;

				for(int j = 0; j < 6; j++) {
					if(pistons[(index + j)%6] != null)
						updateParticle(particle, pistons[(index + j)%6], passedTime);
				}

				index++;
			}
		}
	}

	private static void updateParticle(Particle particle, Piston piston, double passedTime) {

		if(piston.getDirection() == Piston.X) {
			double movementVector = particle.getVelocity().getX() - piston.getVelocity();

			double xi = particle.getPosition().getX();
			double xf = particle.getPosition().getX() + (movementVector*passedTime);
			double xPiston = piston.getPosition().getX();

			// moving to greater axis value = +radius
			// moving to lesser axis value  = -radius
			double radius = xf - xi < 0 ? -particle.getRadius() : particle.getRadius();

			if( (xi-radius <= xPiston && xf+radius >= xPiston) || (xi-radius >= xPiston && xf+radius <= xPiston)) {
				particle.getPosition().setX(piston.getPosition().getX() - radius);

				final double v1 = particle.getVelocity().getX(); // X-component velocity of particle
				final double m1 = particle.getMass();
				final double v2 = piston.getVelocity();
				final double m2 = piston.getMass();

				particle.getVelocity().setX(v1 - (2.0*m2*(v1-v2))/(m1+m2));
				piston.setVelocity(v2 + (2.0*m1*(v1-v2))/(m1+m2));
			}

		} else if(piston.getDirection() == Piston.Y) {
			double movementVector = particle.getVelocity().getY() - piston.getVelocity();

			double yi = particle.getPosition().getY();
			double yf = particle.getPosition().getY() + (movementVector*passedTime);
			double yPiston = piston.getPosition().getY();

			// moving to greater axis value = +radius
			// moving to lesser axis value  = -radius
			double radius = yf - yi < 0 ? -particle.getRadius() : particle.getRadius();

			if( (yi-radius <= yPiston && yf+radius >= yPiston) || (yi-radius >= yPiston && yf+radius <= yPiston)) {
				particle.getPosition().setY(piston.getPosition().getY() - radius);

				final double v1 = particle.getVelocity().getY(); // Y-component velocity of particle
				final double m1 = particle.getMass();
				final double v2 = piston.getVelocity();
				final double m2 = piston.getMass();

				particle.getVelocity().setY(v1 - (2.0*m2*(v1-v2))/(m1+m2));
				piston.setVelocity(v2 + (2.0*m1*(v1-v2))/(m1+m2));
			}

		} else if(piston.getDirection() == Piston.Z) {
			double movementVector = particle.getVelocity().getZ() - piston.getVelocity();

			double zi = particle.getPosition().getZ();
			double zf = particle.getPosition().getZ() + (movementVector*passedTime);
			double zPiston = piston.getPosition().getZ();

			// moving to greater axis value = +radius
			// moving to lesser axis value  = -radius
			double radius = zf - zi < 0 ? -particle.getRadius() : particle.getRadius();

			if( (zi-radius <= zPiston && zf+radius >= zPiston) || (zi-radius >= zPiston && zf+radius <= zPiston)) {
				particle.getPosition().setZ(piston.getPosition().getZ() - radius);

				final double v1 = particle.getVelocity().getZ(); // Z-component velocity of particle
				final double m1 = particle.getMass();
				final double v2 = piston.getVelocity();
				final double m2 = piston.getMass();

				particle.getVelocity().setZ(v1 - (2.0*m2*(v1-v2))/(m1+m2));
				piston.setVelocity(v2 + (2.0*m1*(v1-v2))/(m1+m2));
			}
		}
	}
}