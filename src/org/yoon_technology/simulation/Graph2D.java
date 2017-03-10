package org.yoon_technology.simulation;

import java.awt.Color;
import java.util.ArrayList;

import org.yoon_technology.engine.World;
import org.yoon_technology.engine.WorldObject;
import org.yoon_technology.engine.WorldObjectProperty;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Graph2D extends World {
	protected double xMax;
	protected double xMin;

	public Graph2D() {
		this.restoreWorldSettings();
	}

	@Override
	public void restoreWorldSettings() {
		super.restoreWorldSettings();

		// TODO
	}

	@Override
	public double resized(int width, int height) {
		this.width = width;
		this.height = height;

		this.xMax = (double)width/2.0;
		this.xMin = (double)width/-2.0;

		//		createAxes(0, 0);

		return 1.0;
	}

	// Screen coordinates X, Y at which to place the origin
	protected void createAxes(int originX, int originY) {
		ArrayList<WorldObject> objects = new ArrayList<>();

		// X AXIS
		WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
		xAxisProperty1.addProperty(Color.LIGHT_GRAY);
		xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
		xAxisProperty2.addProperty(Color.LIGHT_GRAY);
		xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		WorldObject lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(
				xMin,
				originY,
				0.0));
		lineObject.setProperties(xAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setPosition(new Vector3d(
				xMax,
				originY,
				0.0));
		lineObject.setProperties(xAxisProperty2);
		objects.add(lineObject);


		// TODO Y Axis

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

	public void initialize() {
		ArrayList<WorldObject> objects = new ArrayList<>();

		WorldObjectProperty moleculeProperty = new WorldObjectProperty();
		moleculeProperty.addProperty(Color.WHITE);
		moleculeProperty.setDrawMode(WorldObjectProperty.POINTS);

		synchronized(this.getObjects()) {
			this.clear();
			for(int j = 0; j < objects.size(); j++) {
				this.addObject(objects.get(j));
			}
			createAxes(0, 0);
		}
	}

	@Override
	public void sendSecondTick() {

	}
}
