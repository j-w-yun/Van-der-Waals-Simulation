package org.yoon_technology.engine.objects;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.yoon_technology.data.Queue;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class TimelineGraph2D extends Graph2D {

	private String title;
	private ArrayList<Queue<Double>> observations;
	private ArrayList<String> observationLabels;
	private ArrayList<Color> observationColors;
	private int observationsToShow = 400;
	private int numObservations;
	private DecimalFormat df;

	public TimelineGraph2D(String title) {
		super();
		this.title = title;
		observations = new ArrayList<>();
		observationLabels = new ArrayList<>();
		observationColors = new ArrayList<>();
		numObservations = 0;

		df = new DecimalFormat("#");
		df.setMaximumFractionDigits(4);
		df.setMaximumIntegerDigits(10);
		//		df.setMinimumFractionDigits(5);
		df.setMinimumIntegerDigits(1);
	}

	// Returns the id of the unique observation
	public int addUniqueObservation(String label, Color color) {
		observationLabels.add(label);
		observationColors.add(color);
		observations.add(new Queue<>());
		return numObservations++;
	}

	public void addPoint(double point, int observationID) {
		observations.get(observationID).add(point);
		if(observations.get(observationID).size() > observationsToShow + 1)
			observations.get(observationID).remove();
	}

	public void clearObservations() {
		// TODO
	}

	public void updateContext() {
		ArrayList<WorldObject> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		double range = xMax - xMin;
		double stepRange = range / (observationsToShow + observationsToShow/4);

		double maxVal = 0.0;
		for(Queue<Double> uniqueObservation : observations) {
			// Find maximum to scale to that height
			for(int j = 0; j < uniqueObservation.size(); j++) {
				uniqueObservation.add(uniqueObservation.peek());
				Double observation = uniqueObservation.remove();
				if(observation > maxVal)
					maxVal = observation;
			}
		}
		if(maxVal == 0)
			maxVal = 0.000000001;

		int uniqueObservationsIndex = 0;
		for(Queue<Double> uniqueObservation : observations) {

			WorldObjectProperty lineProperty1 = new WorldObjectProperty();

			lineProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty lineProperty2 = new WorldObjectProperty();
			lineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			/*
			 * Set position of lines, normalized to maximum and stretched to (a little less than) screen height
			 */
			double average = 0; // For setting the height of the label
			int index = 0;
			for(int j = 0; j < uniqueObservation.size(); j++) {

				uniqueObservation.add(uniqueObservation.peek());
				Double observation = uniqueObservation.remove();
				if(j > uniqueObservation.size() - 10)
					average += observation;

				WorldObject object = new WorldObject();
				object.setColor(observationColors.get(uniqueObservationsIndex));
				object.setPosition(new Vector3d(
						xMin+(stepRange * index),
						(observation/maxVal)*(height-50) -(height/2)+10, // Offset by -(height/2)+10
						0.0));

				if(j + 1 >= uniqueObservation.size()) {
					object.setProperties(lineProperty2);

					/*
					 * String label of observation
					 */
					average = average/10.0;

					WorldText textObject = new WorldText(observationLabels.get(uniqueObservationsIndex) + " (" + df.format(observation) + ")");
					textObject.setColor(observationColors.get(uniqueObservationsIndex));
					textObject.setPosition(new Vector3d(
							xMin+(stepRange * index) + (observationsToShow/30.0)*stepRange, // Move the text a bit to the right
							average/maxVal*(height-50) - (height/2)+10,//(observation/maxVal)*(height-50) - (height/2)+10,
							0.0));
					textObject.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 10));
					texts.add(textObject);
				} else {
					object.setProperties(lineProperty1);
				}

				objects.add(object);

				index++;
			}
			uniqueObservationsIndex++; // Iterate through all types of observations
		} // End iteration through all queues


		// Title of graph
		WorldText textObject = new WorldText(title);
		textObject.setColor(Color.LIGHT_GRAY);
		textObject.setPosition(new Vector3d(-title.length()*3.5, (height/2) - 15, 0.0));
		textObject.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 12));
		texts.add(textObject);

		this.clear();
		//		createAxes(0, -(height/2)+10);
		synchronized(this.objects) {
			for(int j = 0; j < objects.size(); j++) {
				this.addObject(objects.get(j));
			}
		}

		for(int j = 0; j < texts.size(); j++) {
			this.addText(texts.get(j));
		}
	}

	@Override
	protected void createAxes(int originX, int originY) {
		ArrayList<WorldObject> objects = new ArrayList<>();

		// X AXIS
		WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
		xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
		// Break continuum
		WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
		xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

		WorldObject lineObject = new WorldObject();
		lineObject.setColor(Color.LIGHT_GRAY);
		lineObject.setPosition(new Vector3d(
				xMin,
				originY,
				0.0));
		lineObject.setProperties(xAxisProperty1);
		objects.add(lineObject);

		lineObject = new WorldObject();
		lineObject.setColor(Color.LIGHT_GRAY);
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
}
