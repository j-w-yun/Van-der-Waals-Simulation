package org.yoon_technology.simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.yoon_technology.engine.objects.DistributionGraph2D;
import org.yoon_technology.engine.objects.TimelineGraph2D;
import org.yoon_technology.engine.objects.World;
import org.yoon_technology.engine.objects.WorldObject;
import org.yoon_technology.engine.objects.WorldObjectProperty;
import org.yoon_technology.engine.objects.WorldText;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class MDS extends World {

	// Constants
	public static final double BOLTZMANN_K = 1.38066 * Math.pow(10.0, -23.0); // J / K
	// Default settings
	public static final double X_CONFINEMENT_SIZE = 1000.0;
	public static final double Y_CONFINEMENT_SIZE = 1000.0;
	public static final double Z_CONFINEMENT_SIZE = 1000.0;
	public static final double SCALE_RADIUS = 1.0;
	public static final double SCALE_MASS = 1.0;
	public static final int NUM_PARTICLES_INSERT = 100;
	public static final double SPEED_PARTICLES = 1000.0;
	public static final double RADIUS_PARTICLES = 10.0;
	public static final double MASS_PARTICLES = 10.0;
	public static final double X_POSITION_PARTICLES = 0.0;
	public static final double Y_POSITION_PARTICLES = 0.0;
	public static final double Z_POSITION_PARTICLES = 0.0;
	// Settings
	public double xMax;
	public double xMin;
	public double yMax;
	public double yMin;
	public double zMax;
	public double zMin;
	public double scaleRadius;
	public double scaleMass;
	public double xPosInsert;
	public double yPosInsert;
	public double zPosInsert;
	// Cumulatives
	public double cumulativePressure;
	public double simulationSecondsPressure;
	private double simulationSeconds;
	private int numParticles;
	private int milisecondsPassed;
	private long totalUpdateCounter;
	private double totalImpartedMomentum;
	private double moleculeCollisions;
	private double wallCollisions;
	private volatile double currentPressure;
	// GUI
	private static MDSGUI mdsgui;
	// Statistics
	private ArrayList<DistributionGraph2D> statGraphs;
	private ArrayList<TimelineGraph2D> timelineGraphs;
	private HashMap<Color, Integer> colorIDmap;

	public void addStatGraph(DistributionGraph2D statGraph) {
		statGraph.addUniqueObservation(Color.RED); // Add momentum imparted distribution display
		this.statGraphs.add(statGraph);
	}

	public void addTimelineGraph(TimelineGraph2D timelineGraph) {
		this.timelineGraphs.add(timelineGraph);
	}

	public MDS() {
		this.restoreWorldSettings();
		this.statGraphs = new ArrayList<>();
		this.timelineGraphs = new ArrayList<>();
		this.colorIDmap = new HashMap<>();
	}

	@Override
	public void restoreWorldSettings() {
		super.restoreWorldSettings();

		numParticles = 0;
		xMax = X_CONFINEMENT_SIZE / 2.0;
		xMin = -(X_CONFINEMENT_SIZE / 2.0);
		yMax = Y_CONFINEMENT_SIZE / 2.0;
		yMin = -(Y_CONFINEMENT_SIZE / 2.0);
		zMax = Z_CONFINEMENT_SIZE / 2.0;
		zMin = -(Z_CONFINEMENT_SIZE / 2.0);
		scaleRadius = SCALE_RADIUS;
		scaleMass = SCALE_MASS;
		xPosInsert = X_POSITION_PARTICLES;
		yPosInsert = Y_POSITION_PARTICLES;
		zPosInsert = Z_POSITION_PARTICLES;
	}

	@Override
	public double resized(int width, int height) {
		double xRange = (xMax - xMin);
		double yRange = (yMax - yMin);
		double zRange = (zMax - zMin);
		double maxRange = xRange >= yRange ? (xRange >= zRange ? xRange : zRange) : (yRange >= zRange ? yRange : zRange);
		double scale = width <= height ? width / maxRange : height / maxRange;
		return scale / 1.25;
	}

	public void updateWorldSettings(double scaleRadius, double scaleMass,
			double xMax, double xMin, double yMax, double yMin, double zMax, double zMin) {

		double lastScaleRadius = this.scaleRadius;
		this.scaleRadius = scaleRadius;
		double lastScaleMass = this.scaleMass;
		this.scaleMass = scaleMass;

		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;

		synchronized(this.objects) {
			for(WorldObject object : objects) {
				if(object.getVelocity() == null) // Skip static objects (e.g. axes)
					continue;
				object.setRadius(object.getRadius() * (scaleRadius/lastScaleRadius));
				object.setMass(object.getMass() * (scaleMass/lastScaleMass));
			}
		}
		//		createAxes(); // TODO
	}

	private void createAxes() {
		ArrayList<WorldObject> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		// X AXIS
		WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
		xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
		xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		WorldObject lineObject = new WorldObject();
		lineObject.setColor(Color.RED);
		lineObject.setPosition(new Vector3d(xMax, 0.0, 0.0));
		lineObject.setProperties(xAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.RED);
		lineObject.setPosition(new Vector3d(xMin, 0.0, 0.0));
		lineObject.setProperties(xAxisProperty2);
		objects.add(lineObject);
		// "X"
		WorldText textObject = new WorldText("X");
		textObject.setColor(Color.RED);
		textObject.setPosition(new Vector3d(xMax + 20.0, 0.0, 0.0));
		texts.add(textObject);

		// Y AXIS
		WorldObjectProperty yAxisProperty1 = new WorldObjectProperty();
		yAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty yAxisProperty2 = new WorldObjectProperty();
		yAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		lineObject = new WorldObject();
		lineObject.setColor(Color.GREEN);
		lineObject.setPosition(new Vector3d(0.0, yMax, 0.0));
		lineObject.setProperties(yAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.GREEN);
		lineObject.setPosition(new Vector3d(0.0, yMin, 0.0));
		lineObject.setProperties(yAxisProperty2);
		objects.add(lineObject);
		// "Y"
		textObject = new WorldText("Y");
		textObject.setColor(Color.GREEN);
		textObject.setPosition(new Vector3d(0.0, yMax + 20, 0.0));
		texts.add(textObject);

		// Z AXIS
		WorldObjectProperty zAxisProperty1 = new WorldObjectProperty();
		zAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty zAxisProperty2 = new WorldObjectProperty();
		zAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		lineObject = new WorldObject();
		lineObject.setColor(Color.BLUE);
		lineObject.setPosition(new Vector3d(0.0, 0.0, zMax));
		lineObject.setProperties(zAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.BLUE);
		lineObject.setPosition(new Vector3d(0.0, 0.0, zMin));
		lineObject.setProperties(zAxisProperty2);
		objects.add(lineObject);
		// "Z"
		textObject = new WorldText("Z");
		textObject.setColor(Color.BLUE);
		textObject.setPosition(new Vector3d(0.0, 0.0, zMax + 20));
		texts.add(textObject);

		/*
		 * Create box outline for boundary
		 */
		// Break continuum between this and next axis line

		WorldObjectProperty boxOutlineProperty1 = new WorldObjectProperty();
		boxOutlineProperty1.setDrawMode(WorldObjectProperty.LINES);

		WorldObjectProperty boxOutlineProperty2 = new WorldObjectProperty();
		boxOutlineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		// Top square
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax + 1, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);

		// Bottom square
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax-1, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);

		// Sides
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMin));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin+1, zMin));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);

		if(!this.texts.isEmpty()) { // TODO: If axis already created
			synchronized(this.objects) {
				for(int j = 0; j < objects.size(); j++) { // Update axis
					this.objects.remove(this.objects.size() - 1);
				}
				for(int j = 0; j < objects.size(); j++) {
					this.objects.add(objects.get(j));
				}
			}

			synchronized(this.texts) {
				for(int j = 0; j < texts.size(); j++) {	// Update axis texts
					this.texts.remove(this.texts.size() - 1);
				}
				for(int j = 0; j < texts.size(); j++) {
					this.texts.add(texts.get(j));
				}
			}
		} else { // If no axis is present, just add
			synchronized(this.objects) {
				for(int j = 0; j < objects.size(); j++) {
					this.addObject(objects.get(j));
				}
			}
			synchronized(this.texts) {
				for(int j = 0; j < texts.size(); j++) {
					this.addText(texts.get(j));
				}
			}
		}

	}

	public void insertParticles(int numParticlesToInsert, double speed, double radius, double mass,
			double xPosInsert, double yPosInsert, double zPosInsert,
			Color particleColor) {
		this.xPosInsert = xPosInsert;
		this.yPosInsert = yPosInsert;
		this.zPosInsert = zPosInsert;
		this.numParticles += numParticlesToInsert;

		// Add this color
		int id = statGraphs.get(0).addUniqueObservation(particleColor); // Returns the id of the unique observation
		statGraphs.get(1).addUniqueObservation(particleColor); // assert this equals id
		colorIDmap.put(particleColor, id);

		// Total aggrgate, always white
		id = statGraphs.get(0).addUniqueObservation(Color.WHITE);
		statGraphs.get(1).addUniqueObservation(Color.WHITE); // Assert this returns value same as id
		colorIDmap.put(Color.WHITE, id);

		ArrayList<WorldObject> objects = new ArrayList<>();

		WorldObjectProperty particleProperty = new WorldObjectProperty();
		particleProperty.setDrawMode(WorldObjectProperty.POINTS);

		for(WorldObject particle : this.objects) {
			if(particle.getVelocity() != null)
				objects.add(particle);
		}

		for(int j = 0; j < numParticlesToInsert; j++) {
			WorldObject particle = new WorldObject();
			particle.setColor(particleColor);
			particle.setProperties(particleProperty);
			particle.setPosition(new Vector3d(xPosInsert, yPosInsert, zPosInsert));

			double xAxRot = j%3 == 0 ? 1 : 0;
			double yAxRot = j%3 == 1 ? 1 : 0;
			double zAxRot = j%3 == 2 ? 1 : 0;

			double xNegFac = j%6 == 0 ? -1 : 1;
			double yNegFac = j%6 == 1 ? -1 : 1;
			double zNegFac = j%6 == 2 ? -1 : 1;

			double equiDist = Math.sqrt(speed * speed / 3.0);

			particle.setVelocity(new Vector3d(equiDist * xNegFac, equiDist * yNegFac, equiDist * zNegFac).rotate(new Vector3d(xAxRot, yAxRot, zAxRot), j));

			particle.setRadius(radius * scaleRadius);
			particle.setMass(mass * scaleMass);
			objects.add(particle);
		}

		synchronized(this.getObjects()) {
			this.clear();
			for(int j = 0; j < objects.size(); j++) {
				this.addObject(objects.get(j));
			}
			//			createAxes(); // TODO
		}
	}

	public void removeParticles(Color color) {
		ArrayList<WorldObject> objects = new ArrayList<>();

		WorldObjectProperty particleProperty = new WorldObjectProperty();
		particleProperty.setDrawMode(WorldObjectProperty.POINTS);

		numParticles = 0;
		for(WorldObject particle : this.objects) {
			if(particle.getVelocity() != null && !particle.getColor().equals(color)) {
				objects.add(particle);
				numParticles++;
			}
		}

		synchronized(this.getObjects()) {
			this.clear();
			for(int j = 0; j < objects.size(); j++) {
				this.addObject(objects.get(j));
			}
			//			createAxes(); // TODO
		}
	}

	public void initialize() {
		// TODO for each molecule
		numParticles = 0;
		totalUpdateCounter = 0;
		simulationSecondsPressure = 0.0000001;
		simulationSeconds = 0.0000001;
		totalImpartedMomentum = 0.0;
		cumulativePressure = 0.0;

		this.clear();
		//		createAxes(); TODO
	}

	@Override
	public void update(double timePassed) {
		totalUpdateCounter++;

		synchronized(this.objects) {
			for(int j = 0; j < objects.size(); j++) {
				if(objects.get(j).getVelocity() == null) // Static objects have null velocity
					continue;

				for(int k = j; k < objects.size(); k++) {
					if(k == j) // Skip collision with self
						continue;
					if(objects.get(k).getVelocity() == null) // Static objects have null velocity
						continue;

					// Intermolecular collisions
					if(checkCollision(objects.get(j), objects.get(k), timePassed)) {
						resolveCollision(objects.get(j), objects.get(k));
					}
				}

				// Wall collision
				checkCollision(objects.get(j), timePassed);
			}

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

	// Check collision with wall
	private void checkCollision(WorldObject a, double timePassed) {
		Vector3d nextPosition = a.getPosition().add(a.getVelocity().mul(timePassed));

		int id = colorIDmap.get(a.getColor());

		/*
		 * Reposition particle to inside the confinement dimensions
		 */
		Vector3d position = a.getPosition();
		// X clamp
		if(position.getX() + a.getRadius() > xMax)
			a.getPosition().setX(xMax - a.getRadius());
		else if(position.getX() - a.getRadius() < xMin)
			a.getPosition().setX(xMin + a.getRadius());
		// Y clamp
		if(position.getY() + a.getRadius() > yMax)
			a.getPosition().setY(yMax - a.getRadius());
		else if(position.getY() - a.getRadius() < yMin)
			a.getPosition().setY(yMin + a.getRadius());
		// Z clamp
		if(position.getZ() + a.getRadius() > zMax)
			a.getPosition().setZ(zMax - a.getRadius());
		else if(position.getZ() - a.getRadius() < zMin)
			a.getPosition().setZ(zMin + a.getRadius());

		double momentumImparted = 0.0;
		if(nextPosition.getX() + a.getRadius() >= xMax || nextPosition.getX() - a.getRadius() <= xMin) {
			a.getVelocity().setX(a.getVelocity().getX() * -1.0); // Change direction of Vx
			momentumImparted += Math.abs(a.getVelocity().getX() * a.getMass()); // Momentum imparted based on X velocity
			wallCollisions++;

			synchronized(statGraphs.get(1).getObservations()) {
				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.WHITE), momentumImparted);
			}
		}
		if(nextPosition.getY() + a.getRadius() >= yMax || nextPosition.getY() - a.getRadius() <= yMin) {
			a.getVelocity().setY(a.getVelocity().getY() * -1.0); // Change direction of Vy
			momentumImparted += Math.abs(a.getVelocity().getY() * a.getMass()); // Momentum imparted based on Y velocity
			wallCollisions++;

			synchronized(statGraphs.get(1).getObservations()) {
				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.WHITE), momentumImparted);
			}
		}
		if(nextPosition.getZ() + a.getRadius() >= zMax || nextPosition.getZ() - a.getRadius() <= zMin) {
			a.getVelocity().setZ(a.getVelocity().getZ() * -1.0); // Change direction of Vz
			momentumImparted += Math.abs(a.getVelocity().getZ() * a.getMass()); // Momentum imparted based on Z velocity
			wallCollisions++;

			synchronized(statGraphs.get(1).getObservations()) {
				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.WHITE), momentumImparted);
			}
		}

		totalImpartedMomentum += momentumImparted;
	}

	// Returns true if two are colliding
	private boolean checkCollision(WorldObject a, WorldObject b, double timePassed) {
		/*
		 * REJECT CASE NO. 1
		 *
		 * Check whether vector "..." is greater than or equal to vector "xxx"
		 * (has the capacity to reach distance between A and B)
		 *
		 * That is, if length of A's movement vector is less than
		 * 		distance(A, B) - (B.radius + A.radius)
		 * then collision could not happen
		 *
		 *		   .	 	B			where B is at (	0, 4)
		 *       .  	 x  ^			where A is at (-3, 0)
		 *     .    x		|
		 * 	 . x			|			then B - A is (3, 4) which represents vector "xxx"
		 * A <-------------( )origin	(vector whose length is the distance from A to B)
		 *
		 */

		// (Distance from A to B)^2 because sqrt is computationally expensive
		Vector3d deltaPosition = b.getPosition().sub(a.getPosition());
		double deltaPositionLengthSqSubR = deltaPosition.dot(deltaPosition);

		double sumRadii = a.getRadius() + b.getRadius();
		deltaPositionLengthSqSubR -= (sumRadii * sumRadii);

		// Movement vector spatially adjusted to have B stationary
		Vector3d movementVector = a.getVelocity().sub(b.getVelocity());

		// Account for delta time
		movementVector.set(movementVector.mul(timePassed));

		double movementVectorLengthSq = movementVector.dot(movementVector);

		if(movementVectorLengthSq < deltaPositionLengthSqSubR) {
			//			System.out.println("1");
			return false;
		}

		/*
		 * REJECT CASE NO. 2
		 *
		 * Check whether A is moving towards B
		 *
		 * That is, project the movement vector onto distance vector from A to B
		 * and see if it is greater than zero
		 * If not, the movement vector is conflicting directionally with distance vector
		 */

		Vector3d movementVectorScalar = movementVector.normalized();
		double d = movementVectorScalar.dot(deltaPosition);
		if(d <= 0) {
			//			System.out.println("2");
			return false;
		}

		/*
		 * TODO
		 * REJECT CASE NO. 3
		 */
		double deltaPositionLengthSq = deltaPosition.dot(deltaPosition);
		double f = deltaPositionLengthSq - (d * d);

		double sumRadiiSquared = sumRadii * sumRadii;
		if(f >= sumRadiiSquared) {
			//			System.out.println("3");
			return false;
		}

		// Find the third side of the right triangle, where f and sumRadii are other two
		double t = sumRadiiSquared - f;
		// See if the side is valid
		if(t < 0) {
			//			System.out.println("4");
			return false;
		}

		/*
		 * Computation heavy checking
		 */
		// Distance the circle has to travel along
		double distance = d - Math.sqrt(t);

		double mag = movementVector.length();

		// Make sure that the distance A has to move to touch B is no greater than the magnitue of the movement vector
		if(mag < distance) {
			//			System.out.println("5");
			return false;
		}

		// Point of contact
		a.setPosition(a.getPosition().add(
				a.getVelocity().normalized().mul(distance).mul(timePassed)
				));
		b.setPosition(b.getPosition().sub(
				b.getVelocity().normalized().mul(distance).mul(timePassed)
				));

		moleculeCollisions++;

		return true;
	}

	// Resolve collisions for both objects. Dont forget to remove redundance in nested loop when calling this!
	private static void resolveCollision(WorldObject a, WorldObject b) {
		// Directional vector along the point of contact
		Vector3d n = a.getPosition().sub(b.getPosition());
		n.set(n.normalized());

		// Project velocities of A and B onto n
		double a1 = a.getVelocity().dot(n);
		double a2 = b.getVelocity().dot(n);

		double p = (2.0 * (a1 - a2)) / (a.getMass() + b.getMass());

		Vector3d aVel = a.getVelocity().sub( n.mul(b.getMass() * p) );
		Vector3d bVel = b.getVelocity().add( n.mul(a.getMass() * p) );

		a.setVelocity(aVel);
		b.setVelocity(bVel);
	}

	@Override
	public void sendMiliSecondTick() {
		milisecondsPassed++;
		if(milisecondsPassed > 0) { // milisecondsPassed > 1000) {

			double pressure = calculatePressure();

			timelineGraphs.get(0).addPoint(pressure, 0);
			timelineGraphs.get(0).addPoint(cumulativePressure / simulationSecondsPressure, 1);
			timelineGraphs.get(1).addPoint(wallCollisions + moleculeCollisions, 0);
			timelineGraphs.get(1).addPoint(moleculeCollisions, 1);
			timelineGraphs.get(1).addPoint(wallCollisions, 2);

			wallCollisions = 0;
			moleculeCollisions = 0;

			for(TimelineGraph2D timelineGraph : timelineGraphs) {
				timelineGraph.updateContext();
			}

			milisecondsPassed = 0;
		}
	}

	@Override
	public void sendSecondTick() {
		ArrayList<String> labels = new ArrayList<>();
		ArrayList<String> numbers = new ArrayList<>();

		synchronized(this.objects) {
			// Simulation seconds
			simulationSeconds++;
			simulationSecondsPressure++;
			labels.add("\nSimulation Seconds:\n");
			numbers.add(Integer.toString((int)simulationSeconds));

			// Updates
			labels.add("Simulation Updates:\n");
			numbers.add(Long.toString(totalUpdateCounter));

			// N
			labels.add("N:\n");
			numbers.add(Integer.toString(numParticles));

			// Volume
			double volume = calculateVolume();
			labels.add("Volume:\n");
			numbers.add(Double.toString(volume));

			// Total energy
			double energy = calculateEnergy();
			labels.add("Total Energy:\n");
			numbers.add(Double.toString(energy)); // TODO
			//			// Error
			//			correctTotalEnergy = initSpeed * initSpeed * mass * 0.5 * numMolecules;
			//			labels.add("(actual):\n");
			//			numbers.add(Double.toString(correctTotalEnergy) + "\n");

			// RMS speed
			double rmsSpeed = calculateRMSSpeed();
			labels.add("RMS Speed:\n");
			numbers.add(Double.toString(rmsSpeed)); // TODO
			//			// Error
			//			labels.add("(actual):\n");
			//			numbers.add(Double.toString(initSpeed) + "\n");

			// Mean speed
			double meanSpeed = calculateMeanSpeed();
			labels.add("Mean Speed:\n");
			numbers.add(Double.toString(meanSpeed)); // TODO
			//			// Error
			//			double correctMeanSpeed = initSpeed / Math.sqrt(3.0 * Math.PI / 8.0);
			//			labels.add("(actual):\n");
			//			numbers.add(Double.toString(correctMeanSpeed) + "\n");


			// FLAG
			statGraphs.get(0).clearFlags();
			statGraphs.get(0).flag("RMS", rmsSpeed, Color.BLUE, 15);
			statGraphs.get(0).flag("Mean", meanSpeed, Color.RED, 0);


			//			// Kinetic temperature
			//			double temperature = calculateTemperature(meanSpeed);
			//			labels.add("T(mean speed):\n");
			//			numbers.add(Double.toString(temperature));	// TODO
			// Error
			//			double correctTemperature = (initSpeed * initSpeed * mass) / (3.0 * BOLTZMANN_K);
			//			labels.add("(actual):\n");
			//			numbers.add(Double.toString(correctTemperature) + "\n");


			// Average Pressure
			cumulativePressure += currentPressure;
			labels.add("Average Pressure:\n");
			numbers.add(Double.toString(cumulativePressure / simulationSecondsPressure));
			// Current Pressure
			labels.add("Current Pressure:\n");
			numbers.add(Double.toString(currentPressure) + "\n");
		}

		if(mdsgui != null)
			mdsgui.setText(labels.toArray(), numbers.toArray());

		for(DistributionGraph2D statGraph : statGraphs) {
			synchronized(statGraph.getObservations()) {
				statGraph.updateContext();
			}
		}
	}

	private double calculateEnergy() {
		double totalEnergy = 0.0;
		for(WorldObject object : objects) {
			if(object.getVelocity() == null) // Static objects have null velocity
				continue;

			totalEnergy += (0.5 * object.getVelocity().dot(object.getVelocity()) * object.getMass());
		}
		return totalEnergy;
	}

	private double calculatePressure() {
		double areaXY = (xMax - xMin) * (yMax - yMin);
		double areaXZ = (xMax - xMin) * (zMax - zMin);
		double areaYZ = (yMax - yMin) * (zMax - zMin);
		double totArea = 2.0 * (areaXY + areaXZ + areaYZ);
		currentPressure = (2.0 * totalImpartedMomentum) / totArea;

		totalImpartedMomentum = 0.0;

		return currentPressure;
	}

	private double calculateVolume() {
		return (xMax - xMin) * (yMax - yMin) * (zMax - zMin);
	}

	private double calculateAvgVelSq() {
		double totalSpeed = 0.0;
		for(WorldObject object : objects) {
			if(object.getVelocity() == null) // Static objects have null velocity
				continue;

			totalSpeed += (object.getVelocity().dot(object.getVelocity()));
		}
		return totalSpeed;
	}

	private double calculateRMSSpeed() {
		double totalSpeed = calculateAvgVelSq();
		return Math.sqrt(totalSpeed / numParticles);
	}

	private double calculateMeanSpeed() {
		double totalSpeed = 0.0;
		for(WorldObject object : objects) {
			if(object.getVelocity() == null) // Static objects have null velocity
				continue;

			double speed = object.getVelocity().length();

			Color color = object.getColor();

			// Sort according to molecule type (color)
			int id = colorIDmap.get(color);
			synchronized(statGraphs.get(0).getObservations()) {
				statGraphs.get(0).addObservation(id, speed); // TODO review
			}

			// Aggregate total speed
			id = colorIDmap.get(Color.WHITE);
			synchronized(statGraphs.get(0).getObservations()) {
				statGraphs.get(0).addObservation(id, speed); // TODO review
			}
			//			synchronized(statGraphs.get(2).getObservations()) {
			//				statGraphs.get(2).addObservation(0, speed);; // TODO review
			//			}

			totalSpeed += speed;
		}
		return totalSpeed / numParticles;
	}

	//		private double calculateTemperature(double meanSpeed) {
	//			return (meanSpeed * meanSpeed * Math.PI * mass) / (8.0 * BOLTZMANN_K);
	//		}

	public static void main(String[] args) {
		mdsgui = new MDSGUI();
	}
}
