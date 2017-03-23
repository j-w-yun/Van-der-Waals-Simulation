package org.yoon_technology.engine.objects;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class DistributionGraph2D extends Graph2D {

	private String title;
	private int numDivs; // Increases with increasing sample size
	private double xMinVal = 0; // TODO Min-max priority queue
	private double xMaxVal = 0;
	private double dX; // Decreases with increasing sample size
	private ArrayList<PriorityQueue<Double>> observations;
	//	private int totalObservations; TODO
	private ArrayList<int[]> countsInIndex;
	private HashMap<Color, Integer> colorToID;
	private HashMap<Integer, Color> idToColor;
	private ArrayList<Flag> flags;
	private volatile boolean recalculateCount; // Set to true if min/max values change or if dX changes
	//	private static final int maxObservations = 1000; // TODO

	public DistributionGraph2D(String title) {
		super();
		this.title = title;
		observations = new ArrayList<>();
		flags = new ArrayList<>();
		countsInIndex = new ArrayList<>();
		colorToID = new HashMap<>();
		idToColor = new HashMap<>();
	}

	public synchronized void flag(String label, double xVal, Color color, double deltaHeight) {
		flags.add(new Flag(label, xVal, color, deltaHeight));
	}

	@Override
	public synchronized void clear() {
		super.clear();
		lines.clear();
	}

	public synchronized void clearFlags() {
		flags.clear();
	}

	public synchronized void clearObservations() {
		xMinVal = 0;
		xMaxVal = 0;
		//		totalObservations = 0; TODO
		for(PriorityQueue<Double> uniqueObservation : observations) {
			uniqueObservation.clear();
		}
	}

	public synchronized int addUniqueObservation(Color color) {
		if(colorToID.containsKey(color))
			return colorToID.get(color);

		colorToID.put(color, observations.size());
		idToColor.put(observations.size(), color);

		observations.add(new PriorityQueue<>());
		countsInIndex.add(new int[numDivs]);
		return observations.size()-1;
	}

	public synchronized void addObservation(int id, Double observation) {
		recalculateCount = true;
		if(observation > xMaxVal)
			xMaxVal = observation;
		else if(observation < xMinVal)
			xMinVal = observation;
		else
			recalculateCount = false;

		observations.get(id).offer(observation);

		// If there is no need to recalculate counts in bins, just place them in the array here
		if(!recalculateCount) {
			for(int j = 0; j < countsInIndex.get(id).length; j++) {
				if(observation <= xMinVal + (dX * j)) { // Check starting from smallest bin if observation belongs in that bin
					countsInIndex.get(id)[j]++;
					break;
				}
			}
		}
		//		totalObservations++; TODO
	}

	public synchronized ArrayList<PriorityQueue<Double>> getObservations() {
		return observations;
	}

	// No point in updating according to the engine, so update when values are updated
	public synchronized void updateContext() {
		if(observations.isEmpty())
			return;

		/*
		 * Count each occurance and place into its respective bin
		 */
		numDivs = 200; // Constant for now

		// Calculate dX
		double newDX = (xMaxVal - xMinVal) / (double)numDivs;
		if(newDX != dX) {
			recalculateCount = true;
			this.dX = newDX;
		}

		int observationsSize = observations.size();
		for(int j = 0; j < observationsSize; j++) { // For each color
			// If we need to replace it then computation heavy lines need to be run
			if(recalculateCount) {
				// Clear observation count array
				countsInIndex.remove(j);
				countsInIndex.add(j, new int[numDivs+1]);

				PriorityQueue<Double> backup = new PriorityQueue<>();
				int n = 0;
				while(!observations.get(j).isEmpty()) {
					Double observation = observations.get(j).remove();
					backup.offer(observation);

					if(observation > xMinVal + (dX * n))
						n++;
					countsInIndex.get(j)[n]++;
				}
				this.observations.remove(j);
				this.observations.add(j, backup); // Observations always stay stored in queue
			}
		}

		/*
		 * Translate into screen coordinates
		 */
		ArrayList<WorldObject> objects = new ArrayList<>();
		ArrayList<WorldText> texts = new ArrayList<>();

		/*
		 * For setting the position of lines, normalized to maximum and stretched to (a little less than) screen height
		 */
		int maxCount = 0;
		for(int[] currentCountsInIndex : countsInIndex) { // Get maximum count in all observations
			for(int j = 0; j < currentCountsInIndex.length; j++) { // TODO array index out of bounds: 1 ???
				if(currentCountsInIndex[j] > maxCount)
					maxCount = currentCountsInIndex[j];
			}
		}

		double range = xMax - xMin;
		double stepRange = (range / numDivs);
		int id = 0;
		for(int j = 0; j < countsInIndex.size(); j++) {
			WorldObjectProperty lineProperty1 = new WorldObjectProperty();
			lineProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty lineProperty2 = new WorldObjectProperty();
			lineProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			for(int k = 0; k < countsInIndex.get(j).length; k++) {
				WorldObject object = new WorldObject();
				object.setColor(idToColor.get(new Integer(id)));
				object.setPosition(new Vector3d(
						xMin+(stepRange * k),
						(countsInIndex.get(j)[k]/(double)maxCount)*(height-50) -(height/2)+10, // Offset by -(height/2)+10
						0.0));

				if(k + 1 >= countsInIndex.get(j).length)
					object.setProperties(lineProperty2);
				else
					object.setProperties(lineProperty1);
				objects.add(object);
			}
			id++;
		}

		/*
		 * Draw flags
		 */
		for(Flag flag : flags) {
			WorldObjectProperty flagProperty1 = new WorldObjectProperty();
			flagProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty flagProperty2 = new WorldObjectProperty();
			flagProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			// Project the fraction of flag.xVal/valRange onto screen's width range
			double valRange = xMaxVal - xMinVal;
			double fractionToProject = flag.xVal / valRange;
			double projectedX = (fractionToProject * range) - (width/2);

			WorldObject flagObject1 = new WorldObject();
			flagObject1.setColor(flag.color);
			flagObject1.setPosition(new Vector3d(
					projectedX,
					-(height/2),
					0.0));
			flagObject1.setProperties(flagProperty1);

			WorldObject flagObject2 = new WorldObject();
			flagObject2.setColor(flag.color);
			flagObject2.setPosition(new Vector3d(
					projectedX,
					(height/2)-20 - flag.deltaHeight,
					0.0));
			flagObject2.setProperties(flagProperty2);

			objects.add(flagObject1);
			objects.add(flagObject2);

			// String label flag
			WorldText textObject = new WorldText(flag.label);
			textObject.setColor(flag.color);
			textObject.setPosition(new Vector3d(projectedX + 5, (height/2) - 30 - flag.deltaHeight, 0.0));
			textObject.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 14));
			texts.add(textObject);
		}

		// Title of graph
		WorldText textObject = new WorldText(title);
		textObject.setColor(Color.LIGHT_GRAY);
		textObject.setPosition(new Vector3d(-title.length()*3.0, (height/2) - 15, 0.0));
		textObject.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 10));
		texts.add(textObject);

		this.clear();
		createAxes(0, -(height/2)+10);
		for(int j = 0; j < objects.size(); j++) {
			this.addLine(objects.get(j));
		}
		for(int j = 0; j < texts.size(); j++) {
			this.addText(texts.get(j));
		}
	}

	@Override
	protected synchronized void createAxes(int originX, int originY) {
		super.createAxes(originX, originY);

		ArrayList<WorldObject> objects = new ArrayList<>();

		double range = xMax - xMin;
		double stepRange = range / numDivs;
		for(int j = 0; j <= numDivs; j++) {
			WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
			xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
			// Break continuum
			WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
			xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);

			WorldObject lineObject = new WorldObject();
			lineObject.setColor(Color.GRAY);
			lineObject.setPosition(new Vector3d(
					xMin+(stepRange*j),
					originY - 5, // Offset by -(height/2)+10
					0.0));
			lineObject.setProperties(xAxisProperty1);
			objects.add(lineObject);

			lineObject = new WorldObject();
			lineObject.setColor(Color.GRAY);
			lineObject.setPosition(new Vector3d(
					xMin+(stepRange*j),
					originY - 10, // Offset by -(height/2)+10
					0.0));
			lineObject.setProperties(xAxisProperty2);
			objects.add(lineObject);
		}
		for(int j = 0; j < objects.size(); j++) {
			this.addLine(objects.get(j));
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
