package org.yoon_technology.simulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.yoon_technology.engine.Concurrency;
import org.yoon_technology.engine.objects.DistributionGraph2D;
import org.yoon_technology.engine.objects.TimelineGraph2D;
import org.yoon_technology.engine.objects.World;
import org.yoon_technology.engine.objects.WorldObject;
import org.yoon_technology.engine.objects.WorldObjectProperty;
import org.yoon_technology.engine.objects.WorldText;
import org.yoon_technology.gpu.GPU;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class MDS extends World {

	// Override
	protected ArrayList<Particle> particles;
	//	protected Piston piston;
	private PistonBox topBox;
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
	private volatile double simulationSeconds;
	private volatile int milisecondsPassed;
	private volatile double moleculeCollisions;
	private volatile double wallCollisions;
	private volatile double currentPressure;
	public double cumulativePressure;
	public double simulationSecondsPressure;
	private int numParticles;
	private long totalUpdateCounter;
	private double totalImpartedMomentum;
	// GUI
	private static MDSGUI mdsgui;
	// Statistics
	private ArrayList<DistributionGraph2D> statGraphs;
	private ArrayList<TimelineGraph2D> timelineGraphs;
	private HashMap<Color, Integer> colorIDmap;
	// Threading
	private Concurrency.WorkQueue workQueue;
	// GPU acceleration
	private boolean gpuAccel;

	public void GPUOn(boolean on) {
		this.gpuAccel = on;
	}

	public boolean isGPUOn() {
		return this.gpuAccel;
	}

	public void addStatGraph(DistributionGraph2D statGraph) {
		statGraph.addUniqueObservation(Color.RED); // Add momentum imparted distribution display
		this.statGraphs.add(statGraph);
	}

	public void addTimelineGraph(TimelineGraph2D timelineGraph) {
		this.timelineGraphs.add(timelineGraph);
	}

	public MDS() {
		this.restoreWorldSettings();
		this.particles = new ArrayList<>();
		this.statGraphs = new ArrayList<>();
		this.timelineGraphs = new ArrayList<>();
		this.colorIDmap = new HashMap<>();
		this.workQueue = new Concurrency.WorkQueue(128);
	}

	@Override
	public synchronized void restoreWorldSettings() {
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
	public synchronized double resized(int width, int height) {
		double xRange = (xMax - xMin);
		double yRange = (yMax - yMin);
		double zRange = (zMax - zMin);
		double maxRange = xRange >= yRange ? (xRange >= zRange ? xRange : zRange) : (yRange >= zRange ? yRange : zRange);
		double scale = width <= height ? width / maxRange : height / maxRange;
		return scale / 1.5;
	}

	public synchronized void updateWorldSettings(double scaleRadius, double scaleMass,
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

		for(Particle particle : this.particles) {
			particle.setRadius(particle.getRadius() * (scaleRadius/lastScaleRadius));
			particle.setMass(particle.getMass() * (scaleMass/lastScaleMass));
		}
		createAxes(); // TODO
	}

	private void createAxes() {
		ArrayList<WorldObject> container = new ArrayList<>();
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
		container.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.RED);
		lineObject.setPosition(new Vector3d(0.0, 0.0, 0.0));
		lineObject.setProperties(xAxisProperty2);
		container.add(lineObject);
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
		container.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.GREEN);
		lineObject.setPosition(new Vector3d(0.0, 0.0, 0.0));
		lineObject.setProperties(yAxisProperty2);
		container.add(lineObject);
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
		container.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.BLUE);
		lineObject.setPosition(new Vector3d(0.0, 0.0, 0.0));
		lineObject.setProperties(zAxisProperty2);
		container.add(lineObject);
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
		container.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax + 1, yMax, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		container.add(lineObject);

		// Bottom square
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax-1, yMin, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);

		// Sides
		// 1
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		container.add(lineObject);
		// 2
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMax));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin+1, zMax));
		lineObject.setProperties(boxOutlineProperty2);
		container.add(lineObject);
		// 3
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMax, yMin+1, zMin));
		lineObject.setProperties(boxOutlineProperty2);
		container.add(lineObject);
		// 4
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMax-1, zMin));
		lineObject.setProperties(boxOutlineProperty1);
		container.add(lineObject);
		lineObject = new WorldObject();
		lineObject.setColor(Color.WHITE);
		lineObject.setPosition(new Vector3d(xMin, yMin+1, zMin));
		lineObject.setProperties(boxOutlineProperty2);
		container.add(lineObject);

		this.lines.clear();
		for(int j = 0; j < container.size(); j++) {
			this.addLine(container.get(j));
		}
		this.texts.clear();
		for(int j = 0; j < texts.size(); j++) {
			this.addText(texts.get(j));
		}
	}

	public synchronized void insertParticles(int numParticlesToInsert, double speed, double radius, double mass,
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

		ArrayList<Particle> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		for(Particle particle : this.particles) {
			objects.add(particle);
		}

		for(WorldText text : this.texts) {
			texts.add(text);
		}


		for(int j = 0; j < numParticlesToInsert; j++) {
			Particle particle = new Particle();
			particle.setColor(particleColor);
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

			this.topBox.addParticle(particle);
		}

		this.clear();
		for(int j = 0; j < objects.size(); j++) {
			this.addParticle(objects.get(j));
		}
		for(int j = 0; j < texts.size(); j++) {
			this.addText(texts.get(j));
		}
		createAxes();
	}

	public synchronized void addParticle(Particle particle) {
		this.particles.add(particle);
	}

	@Override
	public synchronized ArrayList<WorldObject> getPoints() {
		return (ArrayList)this.particles;
	}

	@Override
	public synchronized void clear() {
		super.clear();
		this.particles.clear();
	}

	public synchronized void removeParticles(Color color) { // TODO
		ArrayList<Particle> particles = new ArrayList<>();

		numParticles = 0;
		for(Particle particle : this.particles) {
			if(particle.getVelocity() != null && !particle.getColor().equals(color)) {
				particles.add(particle);
				numParticles++;
			}
		}

		this.clear();
		for(int j = 0; j < particles.size(); j++) {
			this.addParticle(particles.get(j));
			topBox.addParticle(particles.get(j));
		}
	}

	public synchronized void initialize() {
		// TODO for each molecule
		numParticles = 0;
		totalUpdateCounter = 0;
		simulationSecondsPressure = 0.0000001;
		simulationSeconds = 0.0000001;
		totalImpartedMomentum = 0.0;
		cumulativePressure = 0.0;

		topBox = new PistonBox();

		this.clear();
		createAxes();

		topBox.clear();
		// Experimental
		Piston piston1 = new Piston(100000, Piston.X);
		piston1.setPosition(new Vector3d(150, 0.0, 0.0));
		piston1.setName("Piston #1");
		topBox.split(piston1);
		//
		Piston piston2 = new Piston(100000, Piston.X);
		piston2.setPosition(new Vector3d(-150, 0.0, 0.0));
		piston2.setName("Piston #2");
		topBox.split(piston2);
		//
		Piston piston3 = new Piston(100000, Piston.Y);
		piston3.setPosition(new Vector3d(0.0, 150, 0));
		piston3.setName("Piston #3");
		topBox.split(piston3);
		//
		Piston piston4 = new Piston(100000, Piston.Y);
		piston4.setPosition(new Vector3d(0.0, -150, 0));
		piston4.setName("Piston #4");
		topBox.split(piston4);
		//
		Piston piston5 = new Piston(100000, Piston.Z);
		piston5.setPosition(new Vector3d(0.0, 0.0, 150));
		piston5.setName("Piston #3");
		topBox.split(piston5);
		//
		Piston piston6 = new Piston(100000, Piston.Z);
		piston6.setPosition(new Vector3d(0.0, 0.0, -150));
		piston6.setName("Piston #4");
		topBox.split(piston6);

		// Total aggrgate, always white
		int id = statGraphs.get(0).addUniqueObservation(Color.GRAY);
		statGraphs.get(1).addUniqueObservation(Color.GRAY); // Assert this returns value same as id
		colorIDmap.put(Color.GRAY, id);
	}

	@Override
	public synchronized void update(double passedTime) {
		totalUpdateCounter++;

		// TODO collision.cl


		for(PistonBox box : topBox.getBoxes()) {
			ArrayList<Particle> particles = box.getParticles();
			for(int j = 0; j < particles.size(); j++) {
				for(int k = j+1; k < particles.size()-1; k++) {
					// Intermolecular collisions
					if((particles.get(j)).checkCollision(particles.get(k), passedTime))
						moleculeCollisions++;
				}
			}
		}

		checkCollisionPiston(passedTime); // Piston

		checkCollisionWall(passedTime); // Wall collision

		for(Particle particle : this.particles) {
			particle.updatePosition(passedTime);
		}

		for(Particle particle : this.particles) {
			particle.updateForce(passedTime);
		}
	}

	// Check collision with wall
	private synchronized void checkCollisionWall(double passedTime) {
		for(Particle particle : particles) {

			Vector3d nextPosition = particle.getPosition().add(particle.getVelocity().mul(passedTime));

			int id = colorIDmap.get(particle.getColor());

			/*
			 * Reposition particle to inside the confinement dimensions
			 */
			Vector3d position = particle.getPosition();
			// X clamp
			if(position.getX() + particle.getRadius() > xMax)
				particle.getPosition().setX(xMax - particle.getRadius());
			else if(position.getX() - particle.getRadius() < xMin)
				particle.getPosition().setX(xMin + particle.getRadius());
			// Y clamp
			if(position.getY() + particle.getRadius() > yMax)
				particle.getPosition().setY(yMax - particle.getRadius());
			else if(position.getY() - particle.getRadius() < yMin)
				particle.getPosition().setY(yMin + particle.getRadius());
			// Z clamp
			if(position.getZ() + particle.getRadius() > zMax)
				particle.getPosition().setZ(zMax - particle.getRadius());
			else if(position.getZ() - particle.getRadius() < zMin)
				particle.getPosition().setZ(zMin + particle.getRadius());

			double momentumImparted = 0.0;
			if(nextPosition.getX() + particle.getRadius() >= xMax || nextPosition.getX() - particle.getRadius() <= xMin) {
				particle.getVelocity().setX(particle.getVelocity().getX() * -1.0); // Change direction of Vx
				momentumImparted += Math.abs(particle.getVelocity().getX() * particle.getMass()); // Momentum imparted based on X velocity
				wallCollisions++;

				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.GRAY), momentumImparted);
			}
			if(nextPosition.getY() + particle.getRadius() >= yMax || nextPosition.getY() - particle.getRadius() <= yMin) {
				particle.getVelocity().setY(particle.getVelocity().getY() * -1.0); // Change direction of Vy
				momentumImparted += Math.abs(particle.getVelocity().getY() * particle.getMass()); // Momentum imparted based on Y velocity
				wallCollisions++;

				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.GRAY), momentumImparted);
			}
			if(nextPosition.getZ() + particle.getRadius() >= zMax || nextPosition.getZ() - particle.getRadius() <= zMin) {
				particle.getVelocity().setZ(particle.getVelocity().getZ() * -1.0); // Change direction of Vz
				momentumImparted += Math.abs(particle.getVelocity().getZ() * particle.getMass()); // Momentum imparted based on Z velocity
				wallCollisions++;

				statGraphs.get(1).addObservation(id, momentumImparted); // TODO review
				statGraphs.get(1).addObservation(colorIDmap.get(Color.GRAY), momentumImparted);
			}

			totalImpartedMomentum += momentumImparted;
		}
	}

	private synchronized void checkCollisionPiston(double passedTime) {
		topBox.updateParticles(passedTime); // Update particle position and velocity if they are outside confinement

		this.rectangles.clear();
		for(Piston piston : topBox.getPistons()) {

			Color color = null;
			switch(piston.getDirection()) {
				case Piston.X:
					color = Color.RED;
					break;
				case Piston.Y:
					color = Color.GREEN;
					break;
				case Piston.Z:
					color = Color.BLUE;
					break;
			}

			WorldObject rectanglePoints1 = new WorldObject();
			WorldObject rectanglePoints2 = new WorldObject();
			WorldObject rectanglePoints3 = new WorldObject();
			WorldObject rectanglePoints4 = new WorldObject();
			rectanglePoints1.setColor(color);
			rectanglePoints2.setColor(color);
			rectanglePoints3.setColor(color);
			rectanglePoints4.setColor(color);
			if(piston.getDirection() == Piston.X) {
				rectanglePoints1.setPosition(new Vector3d(piston.getPosition().getX(), yMin-50, zMin-50));
				rectanglePoints2.setPosition(new Vector3d(piston.getPosition().getX(), yMax+50, zMin-50));
				rectanglePoints3.setPosition(new Vector3d(piston.getPosition().getX(), yMax+50, zMax+50));
				rectanglePoints4.setPosition(new Vector3d(piston.getPosition().getX(), yMin-50, zMax+50));
			} else if(piston.getDirection() == Piston.Y) {
				rectanglePoints1.setPosition(new Vector3d(xMin-50, piston.getPosition().getY(), zMin-50));
				rectanglePoints2.setPosition(new Vector3d(xMax+50, piston.getPosition().getY(), zMin-50));
				rectanglePoints3.setPosition(new Vector3d(xMax+50, piston.getPosition().getY(), zMax+50));
				rectanglePoints4.setPosition(new Vector3d(xMin-50, piston.getPosition().getY(), zMax+50));
			} else if(piston.getDirection() == Piston.Z) {
				rectanglePoints1.setPosition(new Vector3d(xMin-50, yMin-50, piston.getPosition().getZ()));
				rectanglePoints2.setPosition(new Vector3d(xMax+50, yMin-50, piston.getPosition().getZ()));
				rectanglePoints3.setPosition(new Vector3d(xMax+50, yMax+50, piston.getPosition().getZ()));
				rectanglePoints4.setPosition(new Vector3d(xMin-50, yMax+50, piston.getPosition().getZ()));
			}
			addRectangle(rectanglePoints1);
			addRectangle(rectanglePoints2);
			addRectangle(rectanglePoints3);
			addRectangle(rectanglePoints4);

			//			WorldText worldText = new WorldText(piston.getName());
			//			worldText.setPosition(new Vector3d(piston.getPosition().getX(), yMin-50, zMin-50));
			//			worldText.setColor(color);
			//			worldText.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 10));
			//			texts.add(worldText);

			piston.updatePosition(passedTime); // TODO in loop
		}
	}

	@Override
	public synchronized void sendMiliSecondTick() {

		if(milisecondsPassed >= 100) {

			workQueue.execute(() -> {
				calculateMeanSpeed();
			});

			workQueue.execute(() -> {
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
			});

			for(DistributionGraph2D statGraph : statGraphs) {
				workQueue.execute(() -> {
					statGraph.updateContext();
				});
			}

			milisecondsPassed = 0;
		}
		milisecondsPassed++;
	}

	@Override
	public synchronized void sendSecondTick() {

		ArrayList<String> labels = new ArrayList<>();
		ArrayList<String> numbers = new ArrayList<>();

		// Simulation seconds
		simulationSeconds++;
		simulationSecondsPressure++;
		labels.add("\nSimulation Time:\n");
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
		statGraphs.get(0).flag("RMS", rmsSpeed, Color.LIGHT_GRAY, 15);
		statGraphs.get(0).flag("Mean", meanSpeed, Color.LIGHT_GRAY, 0);


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

		if(mdsgui != null)
			mdsgui.setText(labels.toArray(), numbers.toArray());
	}

	private double calculateEnergy() {
		double totalEnergy = 0.0;

		for(Particle particle : this.particles) {
			totalEnergy += (0.5 * particle.getVelocity().dot(particle.getVelocity()) * particle.getMass());
		}

		for(Piston piston : topBox.getPistons()) {
			totalEnergy += (0.5 * piston.getVelocity() * piston.getVelocity() * piston.getMass());
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
		for(Particle particle : this.particles) {
			if(particle.getVelocity() == null) // Static objects have null velocity
				continue;

			totalSpeed += (particle.getVelocity().dot(particle.getVelocity()));
		}
		return totalSpeed;
	}

	private double calculateRMSSpeed() {
		double totalSpeed = calculateAvgVelSq();
		return Math.sqrt(totalSpeed / numParticles);
	}

	@SuppressWarnings("null")
	private double calculateMeanSpeed() {
		if(numParticles == 0)
			return 0.0;

		float[] xVel = new float[numParticles];
		float[] yVel = new float[numParticles];
		float[] zVel = new float[numParticles];

		for(int j = 0; j < numParticles; j++) {
			xVel[j] = (float)particles.get(j).getVelocity().getX();
			yVel[j] = (float)particles.get(j).getVelocity().getY();
			zVel[j] = (float)particles.get(j).getVelocity().getZ();
		}

		float[] len = null;
		if(gpuAccel)
			len = GPU.length(xVel, yVel, zVel);


		double totalSpeed = 0.0;
		for(int j = 0; j < numParticles; j++) {
			double speed = 0.0;
			if(gpuAccel)
				speed = len[j];
			else
				speed = particles.get(j).getVelocity().length();

			Color color = particles.get(j).getColor();
			int id = colorIDmap.get(color);
			statGraphs.get(0).addObservation(id, speed); // TODO review
			id = colorIDmap.get(Color.GRAY); // Aggregate total speed
			statGraphs.get(0).addObservation(id, speed);

			totalSpeed += speed;
		}

		return totalSpeed / numParticles;
	}

	public static void main(String[] args) {
		mdsgui = new MDSGUI();
	}
}
