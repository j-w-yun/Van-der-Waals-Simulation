package org.yoon_technology.simulation;

import java.awt.Color;
import java.util.ArrayList;

import org.yoon_technology.engine.World;
import org.yoon_technology.engine.WorldObject;
import org.yoon_technology.engine.WorldObjectProperty;
import org.yoon_technology.engine.WorldText;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class MDS extends World {

	// Constants
	public static final double BOLTZMANN_K = 1.38066 * Math.pow(10.0, -23.0); // J / K
	// Settings
	public int numMolecules;
	public double initSpeed;
	public double radius;
	public double mass;
	public double xMax;
	public double xMin;
	public double yMax;
	public double yMin;
	public double zMax;
	public double zMin;
	// Counters
	private long totalUpdateCounter;
	private int simulationSeconds;
	private double correctTotalEnergy;
	private double momentumTransferred;
	private double cumulativePressure;
	private double moleculeCollisions;
	private double wallCollisions;
	// GUI
	private static MDSGUI mdsgui;
	// Statistics
	private ArrayList<StatLineGraph2D> statGraphs;
	private ArrayList<SlidingLineGraph2D> timelineGraphs;
	private ArrayList<Integer> timeKeepers;

	public void addStatGraph(StatLineGraph2D statGraph) {
		this.statGraphs.add(statGraph);
		timeKeepers.add(new Integer(0));
	}

	public void addTimelineGraph(SlidingLineGraph2D timelineGraph) {
		this.timelineGraphs.add(timelineGraph);
		timeKeepers.add(new Integer(0));
	}

	public MDS() {
		this.restoreWorldSettings();
		this.statGraphs = new ArrayList<>();
		this.timelineGraphs = new ArrayList<>();
		timeKeepers = new ArrayList<>();
	}

	@Override
	public void restoreWorldSettings() {
		super.restoreWorldSettings();

		numMolecules = 1000;
		initSpeed = 1000.0;
		radius = 5.0;
		mass = 10.0;
		xMax = 500.0;
		xMin = -500.0;
		yMax = 250.0;
		yMin = -250.0;
		zMax = 500.0;
		zMin = -500.0;
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

	public void updateWorldSettings(int numMolecules, double initSpeed, double radius, double mass,
			double xMax, double xMin, double yMax, double yMin, double zMax, double zMin) {

		this.numMolecules = numMolecules;
		this.initSpeed = initSpeed;

		this.radius = radius;
		this.mass = mass;

		this.xMax = xMax;
		this.xMin = xMin;
		this.yMax = yMax;
		this.yMin = yMin;
		this.zMax = zMax;
		this.zMin = zMin;

		synchronized(this.objects) {
			for(WorldObject object : objects) {
				object.setRadius(radius);
				object.setMass(mass);

				Vector3d position = object.getPosition();
				// X clamp
				if(position.getX() + radius > xMax)
					object.getPosition().setX(xMax - radius * 2);
				else if(position.getX() - radius < xMin)
					object.getPosition().setX(xMin + radius * 2);
				// Y clamp
				if(position.getY() + radius > yMax)
					object.getPosition().setY(yMax - radius * 2);
				else if(position.getY() - radius < yMin)
					object.getPosition().setY(yMin + radius * 2);
				// Z clamp
				if(position.getZ() + radius > zMax)
					object.getPosition().setZ(zMax - radius * 2);
				else if(position.getZ() - radius < zMin)
					object.getPosition().setZ(zMin + radius * 2);
			}
		}
		createAxes();
	}

	private void createAxes() {
		ArrayList<WorldObject> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		// X AXIS
		WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
		xAxisProperty1.addProperty(Color.RED);
		xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
		xAxisProperty2.addProperty(Color.RED);
		xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		WorldObject lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, 0.0, 0.0));
		lineObject.setProperties(xAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, 0.0, 0.0));
		lineObject.setProperties(xAxisProperty2);
		objects.add(lineObject);
		// "X"
		WorldObjectProperty xAxisTextProperty = new WorldObjectProperty();
		xAxisTextProperty.addProperty(Color.RED);

		WorldText textObject = new WorldText("X");
		textObject.setPosition(new Vector3d(xMax + 20.0, 0.0, 0.0));
		textObject.setProperties(xAxisTextProperty);
		texts.add(textObject);

		// Y AXIS
		WorldObjectProperty yAxisProperty1 = new WorldObjectProperty();
		yAxisProperty1.addProperty(Color.GREEN);
		yAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty yAxisProperty2 = new WorldObjectProperty();
		yAxisProperty2.addProperty(Color.GREEN);
		yAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(0.0, yMax, 0.0));
		lineObject.setProperties(yAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(0.0, yMin, 0.0));
		lineObject.setProperties(yAxisProperty2);
		objects.add(lineObject);
		// "Y"
		WorldObjectProperty yAxisTextProperty = new WorldObjectProperty();
		yAxisTextProperty.addProperty(Color.GREEN);

		textObject = new WorldText("Y");
		textObject.setPosition(new Vector3d(0.0, yMax + 20, 0.0));
		textObject.setProperties(yAxisTextProperty);
		texts.add(textObject);

		// Z AXIS
		WorldObjectProperty zAxisProperty1 = new WorldObjectProperty();
		zAxisProperty1.addProperty(Color.BLUE);
		zAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty zAxisProperty2 = new WorldObjectProperty();
		zAxisProperty2.addProperty(Color.BLUE);
		zAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(0.0, 0.0, zMax));
		lineObject.setProperties(zAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(0.0, 0.0, zMin));
		lineObject.setProperties(zAxisProperty2);
		objects.add(lineObject);
		// "Z"
		WorldObjectProperty zAxisTextProperty = new WorldObjectProperty();
		zAxisTextProperty.addProperty(Color.BLUE);

		textObject = new WorldText("Z");
		textObject.setPosition(new Vector3d(0.0, 0.0, zMax + 20));
		textObject.setProperties(zAxisTextProperty);
		texts.add(textObject);

		/*
		 * Create box outline for boundary
		 */
		// Break continuum between this and next axis line

		WorldObjectProperty boxOutlineProperty1 = new WorldObjectProperty();
		boxOutlineProperty1.addProperty(Color.WHITE);
		boxOutlineProperty1.setDrawMode(WorldObjectProperty.LINES);

		WorldObjectProperty boxOutlineProperty2 = new WorldObjectProperty();
		boxOutlineProperty2.addProperty(Color.WHITE);
		boxOutlineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		// Top square
		// 1
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax + 1, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);

		// Bottom square
		// 1
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax-1, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);

		// Sides
		// 1
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMin));
		lineObject.setProperties(boxOutlineProperty2);
		objects.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		objects.add(lineObject);
		lineObject = new WorldObject();
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

	public void initialize() {
		ArrayList<WorldObject> objects = new ArrayList<>();

		WorldObjectProperty moleculeProperty = new WorldObjectProperty();
		moleculeProperty.addProperty(Color.CYAN);
		moleculeProperty.setDrawMode(WorldObjectProperty.POINTS);

		for(int j = 0; j < numMolecules; j++) {
			WorldObject molecule = new WorldObject();
			molecule.setProperties( moleculeProperty );
			molecule.setPosition(new Vector3d(0.0, 0.0, 0.0));

			double xAxRot = j%3 == 0 ? 1 : 0;
			double yAxRot = j%3 == 1 ? 1 : 0;
			double zAxRot = j%3 == 2 ? 1 : 0;

			double xNegFac = j%6 == 0 ? -1 : 1;
			double yNegFac = j%6 == 1 ? -1 : 1;
			double zNegFac = j%6 == 2 ? -1 : 1;

			double equiDist = Math.sqrt(initSpeed * initSpeed / 3.0);

			molecule.setVelocity(new Vector3d(equiDist * xNegFac, equiDist * yNegFac, equiDist * zNegFac).rotate(new Vector3d(xAxRot, yAxRot, zAxRot), j));

			molecule.setRadius(radius);
			molecule.setMass(mass);
			objects.add(molecule);
		}

		totalUpdateCounter = 0;
		simulationSeconds = 0;
		correctTotalEnergy = 0.0;
		momentumTransferred = 0.0;
		cumulativePressure = 0.0;

		synchronized(this.getObjects()) {
			this.clear();
			for(int j = 0; j < objects.size(); j++) {
				this.addObject(objects.get(j));
			}
			createAxes();
		}
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
		if(nextPosition.getX() + a.getRadius() >= xMax || nextPosition.getX() - a.getRadius() <= xMin) {
			a.getVelocity().setX(a.getVelocity().getX() * -1.0);
			momentumTransferred += a.getVelocity().dot(a.getVelocity()) * a.getMass();

			wallCollisions++;
		}
		if(nextPosition.getY() + a.getRadius() >= yMax || nextPosition.getY() - a.getRadius() <= yMin) {
			a.getVelocity().setY(a.getVelocity().getY() * -1.0);
			momentumTransferred += a.getVelocity().dot(a.getVelocity()) * a.getMass();

			wallCollisions++;
		}
		if(nextPosition.getZ() + a.getRadius() >= zMax || nextPosition.getZ() - a.getRadius() <= zMin) {
			a.getVelocity().setZ(a.getVelocity().getZ() * -1.0);
			momentumTransferred += a.getVelocity().dot(a.getVelocity()) * a.getMass();

			wallCollisions++;
		}

		if(timeKeepers.get(1).intValue() < 1000) {
			statGraphs.get(1).addObservation(momentumTransferred); // TODO
			Integer count = timeKeepers.remove(1);
			timeKeepers.add(1, new Integer(count+1));
		}
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

	// Resolve collisions for both objects. Be sure to leave out redundance in nested for loop
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
	public void sendSecondTick() {
		ArrayList<String> labels = new ArrayList<>();
		ArrayList<String> numbers = new ArrayList<>();

		synchronized(this.timeKeepers) {
			int numTimeKeepers = timeKeepers.size();
			timeKeepers.clear();
			for(int j = 0; j < numTimeKeepers; j++) {
				timeKeepers.add(new Integer(0));
			}
		}

		synchronized(this.objects) {
			// Simulation seconds
			simulationSeconds++;
			labels.add("\nSimulation Seconds:\n");
			numbers.add(Integer.toString(simulationSeconds));

			// Updates
			labels.add("Simulation Updates:\n");
			numbers.add(Long.toString(totalUpdateCounter) + "\n");

			// N
			labels.add("N:\n");
			numbers.add(Integer.toString(objects.size() - 24) + "\n");

			// Volume
			double volume = calculateVolume();
			labels.add("Volume:\n");
			numbers.add(Double.toString(volume) + "\n");

			// Total energy
			double energy = calculateEnergy();
			labels.add("Total Energy:\n");
			numbers.add(Double.toString(energy));
			// Error
			correctTotalEnergy = initSpeed * initSpeed * mass * 0.5 * numMolecules;
			labels.add("(actual):\n");
			numbers.add(Double.toString(correctTotalEnergy) + "\n");

			// RMS speed
			double rmsSpeed = calculateRMSSpeed();
			labels.add("RMS Speed:\n");
			numbers.add(Double.toString(rmsSpeed));
			// Error
			labels.add("(actual):\n");
			numbers.add(Double.toString(initSpeed) + "\n");

			// Mean speed
			double meanSpeed = calculateMeanSpeed();
			labels.add("Mean Speed:\n");
			numbers.add(Double.toString(meanSpeed));
			// Error
			double correctMeanSpeed = initSpeed / Math.sqrt(3.0 * Math.PI / 8.0);
			labels.add("(actual):\n");
			numbers.add(Double.toString(correctMeanSpeed) + "\n");

			// FLAG
			statGraphs.get(0).clearFlags();
			statGraphs.get(0).flag("RMS Speed", rmsSpeed, Color.BLUE, 70);
			statGraphs.get(0).flag("Mean Speed", meanSpeed, Color.RED, 40);

			// Kinetic temperature
			double temperature = calculateTemperature(meanSpeed);
			labels.add("T(mean speed):\n");
			numbers.add(Double.toString(temperature));
			// Error
			double correctTemperature = (initSpeed * initSpeed * mass) / (3.0 * BOLTZMANN_K);
			labels.add("(actual):\n");
			numbers.add(Double.toString(correctTemperature) + "\n");

			// Average Pressure
			double pressure = calculatePressure();
			cumulativePressure += pressure;
			labels.add("Average Pressure:\n");
			numbers.add(Double.toString(cumulativePressure / simulationSeconds));
			// Current Pressure
			labels.add("Current Pressure:\n");
			numbers.add(Double.toString(pressure) + "\n");

			timelineGraphs.get(0).addPoint(pressure, 0);
			timelineGraphs.get(1).addPoint(wallCollisions + moleculeCollisions, 0);
			timelineGraphs.get(1).addPoint(moleculeCollisions, 1);
			timelineGraphs.get(1).addPoint(wallCollisions, 2);

			wallCollisions = 0;
			moleculeCollisions = 0;
		}

		if(mdsgui != null)
			mdsgui.setText(labels.toArray(), numbers.toArray());

		for(StatLineGraph2D statGraph : statGraphs) {
			statGraph.updateContext();
		}

		for(SlidingLineGraph2D timelineGraph : timelineGraphs) {
			timelineGraph.updateContext();
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

		double pressure = (2.0 * momentumTransferred) / totArea;
		momentumTransferred = 0.0;

		return pressure;
	}

	private double calculateVolume() {
		return (xMax - xMin) * (yMax - yMin) * (zMax - zMin);
	}

	private double calculateRMSSpeed() {
		double totalSpeed = 0.0;
		for(WorldObject object : objects) {
			if(object.getVelocity() == null) // Static objects have null velocity
				continue;
			totalSpeed += (object.getVelocity().dot(object.getVelocity()));
		}
		return Math.sqrt(totalSpeed / numMolecules);
	}

	private double calculateMeanSpeed() {
		double totalSpeed = 0.0;
		for(WorldObject object : objects) {
			if(object.getVelocity() == null) // Static objects have null velocity
				continue;
			double speed = object.getVelocity().length();

			statGraphs.get(0).addObservation(speed); // TODO

			totalSpeed += speed;
		}

		return totalSpeed / numMolecules;
	}

	private double calculateTemperature(double meanSpeed) {
		return (meanSpeed * meanSpeed * Math.PI * mass) / (8.0 * BOLTZMANN_K);
	}

	public static void main(String[] args) {
		mdsgui = new MDSGUI();
	}
}
