//package org.yoon_technology.calculator;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.util.ArrayList;
//
//import org.yoon_technology.data.Stack;
//import org.yoon_technology.engine.objects.World;
//import org.yoon_technology.engine.objects.WorldObject;
//import org.yoon_technology.engine.objects.WorldObjectProperty;
//import org.yoon_technology.engine.objects.WorldText;
//import org.yoon_technology.math.Vector2d;
//import org.yoon_technology.math.Vector3d;
//
///**
// * Refer to LICENSE
// *
// * @author Jaewan Yun (jay50@pitt.edu)
// */
//
//public class Calculator {
//
//	public double axialLength;
//	public double xMin;
//	public double xMax;
//	public double yMin;
//	public double yMax;
//	public double zMin;
//	public double zMax;
//	public double xStep;
//	public double yStep;
//	public double zStep;
//	public double varXminRange;
//	public double varXmaxRange;
//	public double varYminRange;
//	public double varYmaxRange;
//	public double scaleFactor;
//	private Vector3d origin;
//
//	private String function;
//
//	// TODO Parsing calculator input
//	public Calculator() {
//		restoreWorldDefault();
//	}
//
//	private void createAxes(World world) {
//		ArrayList<WorldObject> objects = new ArrayList<>();
//		ArrayList<WorldText> texts = new ArrayList<>();
//
//
//
//		/*
//		 * Draw axis lines
//		 */
//
//		// X AXIS
//		Vector2d proportion = axialProportion(xMin, xMax);
//		int minIndex = (int) (proportion.getX() * axialLength);
//		int maxIndex = (int) (proportion.getY() * axialLength);
//
//		WorldObjectProperty xAxisProperty1 = new WorldObjectProperty();
//		xAxisProperty1.addProperty(Color.RED);
//		xAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
//		// Break continuum between this and next axis line
//		WorldObjectProperty xAxisProperty2 = new WorldObjectProperty();
//		xAxisProperty2.addProperty(Color.RED);
//		xAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);
//
//		WorldObject lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(minIndex, 0.0, 0.0));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(xAxisProperty1);
//		objects.add(lineObject);
//
//		lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(maxIndex, 0.0, 0.0));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(xAxisProperty2);
//		objects.add(lineObject);
//
//		// "X"
//		WorldObjectProperty xAxisTextProperty = new WorldObjectProperty();
//		xAxisTextProperty.addProperty(Color.RED);
//
//		WorldText textObject = new WorldText("X");
//		textObject.setPosition(new Vector3d((double) maxIndex + 20, 0.0, 0.0));
//		textObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		textObject.setProperties(xAxisTextProperty);
//		texts.add(textObject);
//
//
//		// Y AXIS
//		proportion = axialProportion(yMin, yMax);
//		minIndex = (int) (proportion.getX() * axialLength);
//		maxIndex = (int) (proportion.getY() * axialLength);
//
//		WorldObjectProperty yAxisProperty1 = new WorldObjectProperty();
//		yAxisProperty1.addProperty(Color.GREEN);
//		yAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
//		// Break continuum between this and next axis line
//		WorldObjectProperty yAxisProperty2 = new WorldObjectProperty();
//		yAxisProperty2.addProperty(Color.GREEN);
//		yAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);
//
//		lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(0.0, minIndex, 0.0));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(yAxisProperty1);
//		objects.add(lineObject);
//
//		lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(0.0, maxIndex, 0.0));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(yAxisProperty2);
//		objects.add(lineObject);
//
//
//		// "Y"
//		WorldObjectProperty yAxisTextProperty = new WorldObjectProperty();
//		yAxisTextProperty.addProperty(Color.GREEN);
//
//		textObject = new WorldText("Y");
//		textObject.setPosition(new Vector3d(0.0, (double) maxIndex + 20, 0.0));
//		textObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		textObject.setProperties(yAxisTextProperty);
//		texts.add(textObject);
//
//
//		// Z AXIS
//		proportion = axialProportion(zMin, zMax);
//		minIndex = (int) (proportion.getX() * axialLength);
//		maxIndex = (int) (proportion.getY() * axialLength);
//
//		WorldObjectProperty zAxisProperty1 = new WorldObjectProperty();
//		zAxisProperty1.addProperty(Color.BLUE);
//		zAxisProperty1.setDrawMode(WorldObjectProperty.LINES);
//		// Break continuum between this and next axis line
//		WorldObjectProperty zAxisProperty2 = new WorldObjectProperty();
//		zAxisProperty2.addProperty(Color.BLUE);
//		zAxisProperty2.setDrawMode(WorldObjectProperty.END_LINES);
//
//		lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(0.0, 0.0, minIndex));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(zAxisProperty1);
//		objects.add(lineObject);
//
//		lineObject = new WorldObject();
//		lineObject.setPosition(new Vector3d(0.0, 0.0, maxIndex));
//		lineObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		lineObject.setProperties(zAxisProperty2);
//		objects.add(lineObject);
//
//
//		// "Z"
//		WorldObjectProperty zAxisTextProperty = new WorldObjectProperty();
//		zAxisTextProperty.addProperty(Color.BLUE);
//
//		textObject = new WorldText("Z");
//		textObject.setPosition(new Vector3d(0.0, 0.0, (double) maxIndex + 20));
//		textObject.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//		textObject.setProperties(zAxisTextProperty);
//		texts.add(textObject);
//
//
//
//		/*
//		 * Label steps in the axis
//		 */
//
//		// Start stepping from origin, hence need for 2 loops
//		double scaleX = axialLength / (xMax - xMin);
//		for(double j = 0; j <= xMax; j+= xStep) {
//			if(Math.abs(j) < xStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d((double)j, 0.0, 0.0).mul(scaleX));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(xAxisTextProperty);
//			texts.add(text);
//		}
//		for(double j = xMin; j <= 0; j+= xStep) {
//			if(Math.abs(j) < xStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d((double)j, 0.0, 0.0).mul(scaleX));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(xAxisTextProperty);
//			texts.add(text);
//		}
//
//
//		double scaleY = axialLength / (yMax - yMin);
//		for(double j = 0; j <= yMax; j+= yStep) {
//			if(Math.abs(j) < yStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d(0.0, (double)j, 0.0).mul(scaleY));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(yAxisTextProperty);
//			texts.add(text);
//		}
//		for(double j = yMin; j <= 0; j+= yStep) {
//			if(Math.abs(j) < yStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d(0.0, (double)j, 0.0).mul(scaleY));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(yAxisTextProperty);
//			texts.add(text);
//		}
//
//
//		double scaleZ = axialLength / (zMax - zMin);
//		for(double j = 0; j <= zMax; j+= zStep) {
//			if(Math.abs(j) < zStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d(0.0, 0.0, (double)j).mul(scaleZ));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(zAxisTextProperty);
//			texts.add(text);
//		}
//		for(double j = zMin; j <= 0; j+= zStep) {
//			if(Math.abs(j) < zStep / 2.0) // Remove labels that are near the origin
//				continue;
//
//			String labelNumberString = Double.toString(j);
//			if(labelNumberString.length() > 7)
//				labelNumberString = labelNumberString.substring(0, 7);
//			WorldText text = new WorldText("" + labelNumberString);	// Label the steps with numbers
//			text.setPosition(new Vector3d(0.0, 0.0, (double)j).mul(scaleZ));	// Remember to scale up
//			text.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//			text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
//			text.setProperties(zAxisTextProperty);
//			texts.add(text);
//		}
//
//		// Add to world
//		for(int j = 0; j < objects.size(); j++) {
//			world.addObject(objects.get(j));
//		}
//		for(int j = 0; j < texts.size(); j++) {
//			world.addText(texts.get(j));
//		}
//	}
//
//	// Used to make axis
//	private static Vector2d axialProportion(double min, double max) {
//		if(min >= 0.0) // Still include the origin
//			return new Vector2d(0.0, 1.0);
//		if(max <= 0.0)
//			return new Vector2d(-1.0, 0.0);
//
//		double total = max - min;
//		return new Vector2d(min / total, max / total);
//	}
//
//	// Graphs a function stored in functionOutput()
//	public void calculate(World world) {
//		ArrayList<WorldObject> objects = new ArrayList<>();
//
//		double scaleX = Math.abs(axialLength / (xMax - xMin));
//		double scaleY = Math.abs(axialLength / (yMax - yMin));
//		double scaleZ = Math.abs(axialLength / (zMax - zMin));
//
//		WorldObjectProperty objectProperty = new WorldObjectProperty();
//		objectProperty.addProperty(Color.CYAN);
//
//		for(double j = varXminRange; j < varXmaxRange; j+=1.0) {
//			for(double k = varYminRange; k < varYmaxRange; k+=1.0) {
//
//				WorldObject obj = new WorldObject();
//
//				double varX = j / scaleFactor;
//				double varY = k / scaleFactor;
//
//				// TODO for each function
//				if(function != null) {
//					obj.setPosition(new Vector3d(varX, varY, functionOutput(varX, varY, function)));
//					obj.setPosition(new Vector3d(
//							obj.getPosition().getX() * scaleX,
//							obj.getPosition().getY() * scaleY,
//							obj.getPosition().getZ() * scaleZ));
//
//					obj.setVelocity(new Vector3d(0.0, 0.0, 0.0));
//					obj.setRadius(2.0);
//					obj.setProperties(objectProperty);
//					objects.add(obj);
//				}
//			}
//		}
//		synchronized(world.getObjects()) {
//			world.clear();
//			for(int j = 0; j < objects.size(); j++) {
//				world.addObject(objects.get(j));
//			}
//			createAxes(world);
//		}
//	}
//
//
//	public double functionOutput(double x, double y, String function) {
//
//		Stack<Double> numberStack = new Stack<>();
//		String[] cutString = function.split(" ");
//
//		for(String string : cutString) {
//			// Binary operations
//			if(string.equals("+")) {
//				double numOne = numberStack.pop();
//				double numTwo = numberStack.pop();
//				numberStack.push(numTwo + numOne);
//
//			} else if(string.equals("-")) {
//				double numOne = numberStack.pop();
//				double numTwo = numberStack.pop();
//				numberStack.push(numTwo - numOne);
//
//			} else if(string.equals("*")) {
//				double numOne = numberStack.pop();
//				double numTwo = numberStack.pop();
//				numberStack.push(numTwo * numOne);
//
//			} else if(string.equals("/")) {
//				double numOne = numberStack.pop();
//				double numTwo = numberStack.pop();
//				numberStack.push(numTwo / numOne);
//
//			} else if(string.equals("^")) {
//				double numOne = numberStack.pop();
//				double numTwo = numberStack.pop();
//				numberStack.push(Math.pow(numTwo, numOne));
//
//			} else if(string.equals("E")) {
//				double numOne = numberStack.pop();
//				numOne = Math.pow(10, numOne);
//				double numTwo = numberStack.pop();
//				numberStack.push(Math.pow(numTwo, numOne));
//
//			}
//
//			// Unary operations
//			else if(string.equals("ln")) {
//				numberStack.push(Math.log(numberStack.pop()));
//
//			} else if(string.equals("log")) {
//				numberStack.push(Math.log10(numberStack.pop()));
//
//			} else if(string.equals("sin")) {
//				numberStack.push(Math.sin(numberStack.pop()));
//
//			} else if(string.equals("cos")) {
//				numberStack.push(Math.cos(numberStack.pop()));
//
//			} else if(string.equals("tan")) {
//				numberStack.push(Math.tan(numberStack.pop()));
//
//			}
//
//			// Variable
//			else if(string.equals("X")) {
//				numberStack.push(x);
//
//			} else if(string.equals("Y")) {
//				numberStack.push(y);
//			}
//
//			// TODO
//			else if(string.equals(" ")) {
//				System.out.println("Space detected!");
//			}
//
//			// Constants
//			else {
//				if(CalculatorConsole.constants.containsKey(string))
//					numberStack.push(CalculatorConsole.constants.get(string));
//				else
//					numberStack.push(Double.parseDouble(string));
//			}
//		}
//
//		if(numberStack.peek() == null)
//			return 0.0;
//		return numberStack.pop();
//	}
//
//	public void addFunction(String function) {
//		//		functions.add(function);
//		this.function = function;
//	}
//
//	public void restoreWorldDefault() {
//		// Restore world settings
//		axialLength = 500.0;
//		xMin = -5.0;
//		xMax = 5.0;
//		yMin = -5.0;
//		yMax = 5.0;
//		zMin = -5.0;
//		zMax = 5.0;
//		xStep = 5.0;
//		yStep = 5.0;
//		zStep = 5.0;
//		varXminRange = -100.0;
//		varXmaxRange = 100.0;
//		varYminRange = -100.0;
//		varYmaxRange = 100.0;
//		scaleFactor = 50.0;
//		origin = new Vector3d(0.0, 0.0, 0.0);
//	}
//
//	public Vector3d getOrigin() {
//		return origin;
//	}
//
//	public static void main(String[] args) {
//		CalculatorGUI.startGUI();
//	}
//}
