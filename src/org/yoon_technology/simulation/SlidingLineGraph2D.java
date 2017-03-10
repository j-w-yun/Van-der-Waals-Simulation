package org.yoon_technology.simulation;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.yoon_technology.data.Queue;
import org.yoon_technology.engine.WorldObject;
import org.yoon_technology.engine.WorldObjectProperty;
import org.yoon_technology.engine.WorldText;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class SlidingLineGraph2D extends Graph2D {

	private String title;
	private ArrayList<Queue<Double>> observations;
	private ArrayList<String> observationLabels;
	private ArrayList<Color> observationColors;
	private int observationsToShow = 50;
	private int numObservations;
	private DecimalFormat df;

	public SlidingLineGraph2D(String title) {
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
		//		df.setGroupingUsed(true);
		//		df.setGroupingSize(3);
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

		Double maxVal = 0.001;
		for(Queue<Double> uniqueObservation : observations) {
			// Find maximum to scale to that height
			for(int j = 0; j < uniqueObservation.size(); j++) {
				uniqueObservation.add(uniqueObservation.peek());
				Double observation = uniqueObservation.remove();
				if(observation > maxVal)
					maxVal = observation;
			}
		}

		int uniqueObservationsIndex = 0;
		for(Queue<Double> uniqueObservation : observations) {

			WorldObjectProperty lineProperty1 = new WorldObjectProperty();
			lineProperty1.addProperty(observationColors.get(uniqueObservationsIndex));
			lineProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty lineProperty2 = new WorldObjectProperty();
			lineProperty2.addProperty(observationColors.get(uniqueObservationsIndex));
			lineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			/*
			 * Set position of lines, normalized to maximum and stretched to (a little less than) screen height
			 */
			int index = 0;
			for(int j = 0; j < uniqueObservation.size(); j++) {

				uniqueObservation.add(uniqueObservation.peek());
				Double observation = uniqueObservation.remove();


				WorldObject object = new WorldObject();
				object.setPosition(new Vector3d(
						xMin+(stepRange * index),
						(observation/maxVal)*(height-50) -(height/2)+10, // Offset by -(height/2)+10
						0.0));

				if(j + 1 >= uniqueObservation.size()) {
					object.setProperties(lineProperty2);

					/*
					 * String label of observation
					 */
					WorldObjectProperty textObjectProperty = new WorldObjectProperty();
					textObjectProperty.addProperty(observationColors.get(uniqueObservationsIndex));

					WorldText textObject = new WorldText(observationLabels.get(uniqueObservationsIndex) + " (" + df.format(observation) + ")");
					textObject.setPosition(new Vector3d(
							xMin+(stepRange * index),
							(observation/maxVal)*(height-50) - (height/2)+10,
							0.0));
					textObject.setProperties(textObjectProperty);
					textObject.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 10));
					texts.add(textObject);
				} else {
					object.setProperties(lineProperty1);
				}

				objects.add(object);

				index++;
			}

			uniqueObservationsIndex++;
		} // End iteration through all queues


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
}
