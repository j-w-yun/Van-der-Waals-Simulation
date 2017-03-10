package org.yoon_technology.simulation;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.PriorityQueue;

import org.yoon_technology.engine.WorldObject;
import org.yoon_technology.engine.WorldObjectProperty;
import org.yoon_technology.engine.WorldText;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class StatLineGraph2D extends Graph2D {

	private String title;
	private int numDivs; // Increases with increasing sample size
	private double xMinVal = 0; // TODO Min-max priority queue
	private double xMaxVal = 0;
	private double dX; // Decreases with increasing sample size
	private PriorityQueue<Double> observations;
	private ArrayList<Flag> flags;
	private int[] countsInIndex;

	public StatLineGraph2D(String title) {
		super();
		this.title = title;
		observations = new PriorityQueue<>();
		flags = new ArrayList<>();
	}

	public void clearObservations() {
		synchronized(this.observations) {
			xMinVal = 0;
			xMaxVal = 0;
			observations.clear();
		}
	}

	public void flag(String label, double xVal, Color color, double deltaHeight) {
		synchronized(this.flags) {
			flags.add(new Flag(label, xVal, color, deltaHeight));
		}
	}

	public void clearFlags() {
		synchronized(this.flags) {
			flags.clear();
		}
	}

	// No point in updating according to the engine, so update when values are updated
	public void updateContext() {
		if(observations.isEmpty())
			return;

		/*
		 * Count each occurance and place into its respective bin
		 */
		PriorityQueue<Double> backup = new PriorityQueue<>();
		synchronized(this.observations) {
			this.numDivs = (int)Math.log((observations.size()+1.0)*500.0)+1;

			// Calculate dX
			dX = (xMaxVal - xMinVal) / (double)numDivs;

			// Init observation counter
			countsInIndex = new int[numDivs + 2];
			for(int j = 0; j <= numDivs; j++) {
				countsInIndex[j] = 0;
			}

			int k = 0;
			while(!observations.isEmpty()) {
				Double observation = observations.remove();
				backup.add(observation);

				if(observation > xMinVal + (dX * k))
					k++;

				countsInIndex[k]++;
			}
		}
		this.observations = backup;

		/*
		 * Translate into screen coordinates
		 */
		ArrayList<WorldObject> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		WorldObjectProperty lineProperty1 = new WorldObjectProperty();
		lineProperty1.addProperty(Color.GREEN);
		lineProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty lineProperty2 = new WorldObjectProperty();
		lineProperty2.addProperty(Color.GREEN);
		lineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		double range = xMax - xMin;
		double stepRange = range / numDivs;

		/*
		 * Set position of lines, normalized to maximum and stretched to (a little less than) screen height
		 */
		int maxCount = 0;
		for(int j = 0; j <= numDivs; j++) {
			if(countsInIndex[j] > maxCount)
				maxCount = countsInIndex[j];
		}
		for(int j = 0; j <= numDivs; j++) {
			WorldObject object = new WorldObject();
			object.setPosition(new Vector3d(
					xMin+(stepRange * j),
					(countsInIndex[j]/(double)maxCount)*(height-50) -(height/2)+10, // Offset by -(height/2)+10
					0.0));

			if(j == numDivs)
				object.setProperties(lineProperty2);
			else
				object.setProperties(lineProperty1);

			objects.add(object);
		}

		/*
		 * Draw flags
		 */
		synchronized(this.flags) {
			for(Flag flag : flags) {
				WorldObjectProperty flagProperty1 = new WorldObjectProperty();
				flagProperty1.addProperty(flag.color);
				flagProperty1.setDrawMode(WorldObjectProperty.LINES);
				// Break continuum
				WorldObjectProperty flagProperty2 = new WorldObjectProperty();
				flagProperty2.addProperty(flag.color);
				flagProperty2.setDrawMode(WorldObjectProperty.END_LINES);

				// Project the fraction of flag.xVal/valRange onto screen's width range
				double valRange = xMaxVal - xMinVal;
				double fractionToProject = flag.xVal / valRange;
				double projectedX = (fractionToProject * range) - (width/2);

				WorldObject flagObject1 = new WorldObject();
				flagObject1.setPosition(new Vector3d(
						projectedX,
						-(height/2) + 5,
						0.0));
				flagObject1.setProperties(flagProperty1);

				WorldObject flagObject2 = new WorldObject();
				flagObject2.setPosition(new Vector3d(
						projectedX,
						(height/2)-20 - flag.deltaHeight,
						0.0));
				flagObject2.setProperties(flagProperty2);

				objects.add(flagObject1);
				objects.add(flagObject2);

				// String label flag
				WorldObjectProperty textObjectProperty = new WorldObjectProperty();
				textObjectProperty.addProperty(flag.color);

				WorldText textObject = new WorldText(flag.label);
				textObject.setPosition(new Vector3d(projectedX + 5, (height/2) - 30 - flag.deltaHeight, 0.0));
				textObject.setProperties(textObjectProperty);
				textObject.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 12));
				texts.add(textObject);
			}
		}

		// Title of graph
		WorldObjectProperty textObjectProperty = new WorldObjectProperty();
		textObjectProperty.addProperty(Color.LIGHT_GRAY);

		WorldText textObject = new WorldText(title);
		textObject.setPosition(new Vector3d(-title.length()*3.5, (height/2) - 15, 0.0));
		textObject.setProperties(textObjectProperty);
		textObject.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 12));
		texts.add(textObject);

		this.clear();
		createAxes(0, -(height/2)+10);
		for(int j = 0; j < objects.size(); j++) {
			this.addObject(objects.get(j));
		}
		for(int j = 0; j < texts.size(); j++) {
			this.addText(texts.get(j));
		}
	}

	public void addObservation(Double observation) {
		synchronized(this.observations) {
			if(observation > xMaxVal)
				xMaxVal = observation;
			else if(observation < xMinVal)
				xMinVal = observation;

			observations.add(observation);
		}
	}

	@Override
	protected void createAxes(int originX, int originY) {
		super.createAxes(originX, originY);

		ArrayList<WorldObject> objects = new ArrayList<>();

		double range = xMax - xMin;
		double stepRange = range / numDivs;

		for(int j = 0; j <= numDivs; j++) {
			WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
			xAxisProperty1.addProperty(Color.GRAY);
			xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
			xAxisProperty2.addProperty(Color.GRAY);
			xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			WorldObject lineObject = new WorldObject();
			lineObject.setPosition(new Vector3d(
					xMin+(stepRange*j),
					originY + 3, // Offset by -(height/2)+10
					0.0));
			lineObject.setProperties(xAxisProperty1);
			objects.add(lineObject);

			lineObject = new WorldObject();
			lineObject.setPosition(new Vector3d(
					xMin+(stepRange*j),
					originY - 3, // Offset by -(height/2)+10
					0.0));
			lineObject.setProperties(xAxisProperty2);
			objects.add(lineObject);
		}

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

	private class Flag {
		String label;
		double xVal;
		Color color;
		double deltaHeight;

		Flag(String label, double xVal, Color color, double deltaHeight) {
			this.label = label;
			this.xVal = xVal;
			this.color = color;
			this.deltaHeight = deltaHeight;
		}
	}
}
