//package org.yoon_technology.calculator;
//
//import java.awt.Color;
//import java.util.HashMap;
//
//import javax.swing.JTextPane;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
//
//import org.yoon_technology.data.Queue;
//import org.yoon_technology.data.Stack;
//import org.yoon_technology.engine.Engine;
//
///**
// * Refer to LICENSE
// *
// * @author Jaewan Yun (jay50@pitt.edu)
// */
//
//public class CalculatorConsole {
//
//	private static JTextPane output;
//	private static final String[] keywords = {"help",
//	                                          "debug",
//	                                          "fn",
//
//	};
//	private static final String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}; // In absence of whitespace distinction
//	private static final String[][] operands = {
//	                                            {"^", "E"},
//	                                            {"!", "*", "/"},
//	                                            {"+", "-"},
//	                                            {"[", "("},
//	                                            {"]", ")"},
//	                                            {"ln(", "log(", "cos(", "sin(", "tan("}
//	};
//	public static final HashMap<String, Double> constants;
//	static {
//		constants = new HashMap<>();
//		constants.put("e", Math.E);
//		constants.put("k", 1.38 * Math.pow(10.0, -23.0)); // J / K
//		constants.put("pi", Math.PI);
//		constants.put("h", 6.6260700404 * Math.pow(10, -34)); // J / s
//	}
//
//	private static Queue<String> printQueue;
//
//	static {
//		SimpleAttributeSet left = new SimpleAttributeSet();
//		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
//		StyleConstants.setForeground(left, Color.GREEN);
//		printQueue = new Queue<>();
//
//		// Keep trying to print the print queue
//		new Thread(() -> {
//			// TODO
//			while(true) {
//				if(printQueue.isEmpty())
//					continue;
//
//				String toPrint = printQueue.remove();
//
//				try {
//					output.getStyledDocument().setParagraphAttributes(output.getStyledDocument().getLength(),
//							1,
//							left,
//							false);
//					for(int j = 0; j < toPrint.length(); j++) {
//						output.getStyledDocument().insertString(output.getStyledDocument().getLength(),
//								Character.toString(toPrint.charAt(j)),
//								left);
//						Thread.sleep(10);
//
//						output.setCaretPosition(output.getDocument().getLength());
//					}
//					output.getStyledDocument().insertString(output.getStyledDocument().getLength(),
//							"\n",
//							left);
//				} catch (BadLocationException e1) {} catch (InterruptedException e) {}
//
//			}
//		}).start();
//	}
//
//	public static void setOutput(JTextPane output) {
//		CalculatorConsole.output = output;
//	}
//
//	public static void getCommand(String command, Calculator calculator, Engine engine) {
//		if(command == null)
//			return;
//
//		String[] strings = command.split(" ");
//
//		int result = parseCommand(strings[0]);	// Command is the first word
//
//		System.out.println("Contents:");
//		for(int j = 0; j < strings.length; j++) {
//			System.out.println("\t\"" + strings[j] + "\"");
//		}
//		System.out.println("End contents\n\n");
//
//		if(result == -1)
//			notUnderstood(strings);
//
//		switch(result) {
//			case 0:
//				help();
//				break;
//			case 1:
//				debug();
//				break;
//			case 2:
//				evaluateExpression(strings, calculator, engine);
//
//			default:
//
//				break;
//		}
//	}
//
//	// Returns the index of the keyword found in the message; -1 if not found
//	private static int parseCommand(String toParse) {
//		for(int k = 0; k < keywords.length; k++) {
//			if(toParse.equals(keywords[k]))
//				return k;
//		}
//		return -1;
//	}
//
//	// Return the priority level associated with that operand; -1 if not found
//	private static int parseOperand(String toParse) {
//		for(int j = 0; j < operands.length; j++) {
//			for(int k = 0; k < operands[j].length; k++) {
//				if(toParse.equals(operands[j][k]))
//					return j;
//			}
//		}
//
//		return -1;
//	}
//
//	private synchronized static void print(String out) {
//		printQueue.add(out);
//	}
//
//	private static void help() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("Operands include:\n");
//		sb.append("\t");
//		for(int j = 0; j < operands.length; j++) {
//			for(int k = 0; k < operands[j].length; k++) {
//				sb.append(operands[j][k]);
//				if(j + 1 == operands.length && k + 1 == operands[j].length)
//					sb.append("\n");
//				else
//					sb.append(", ");
//			}
//		}
//
//		sb.append("Constants include:\n");
//		sb.append("\t");
//		for(String constant : constants.keySet()) {
//			sb.append(constant + ", ");
//		}
//		sb.deleteCharAt(sb.length() - 1);
//		sb.deleteCharAt(sb.length() - 1);
//		sb.append("\n");
//
//		sb.append("Keywords include:\n");
//		sb.append("\t");
//		for(int j = 0; j < keywords.length; j++) {
//			sb.append(keywords[j]);
//			if(j + 1 == keywords.length)
//				sb.append("\n");
//			else
//				sb.append(", ");
//		}
//
//		print(sb.toString());
//	}
//
//	private static void debug() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("DEBUG");
//		print(sb.toString());
//	}
//
//	private static void evaluateExpression(String[] expression, Calculator calculator, Engine engine) {
//		Stack<String> operandStack = new Stack<>();
//		Stack<String> postFixList = new Stack<>();
//
//		for(int j = 1; j < expression.length; j++) { // Skip keyword
//			int idValue = parseOperand(expression[j]);
//
//			System.out.println("Evaluating: " + expression[j] + "\tID:\t" + idValue);
//
//			if(idValue == -1) {
//				postFixList.push(expression[j]);
//			} else {
//
//				// (, ...
//				if(idValue == 3) {
//					operandStack.push(expression[j]);
//				}
//
//				// ln(, ...
//				else if(idValue == 5) {
//					operandStack.push(expression[j]); // Same as above
//				}
//
//				// ), ...
//				else if(idValue == 4) {
//					String popped = operandStack.pop();
//					int poppedIdVal = parseOperand(popped);
//					while(poppedIdVal != 3 && poppedIdVal != 5) {
//						postFixList.push(popped);
//						popped = operandStack.pop();
//						poppedIdVal = parseOperand(popped);
//
//					}
//					if(poppedIdVal == 5)
//						postFixList.push(popped.substring(0, popped.length() - 1));
//				}
//
//				// Other operands, sorting order depends on the operand priority
//				else {
//					while((!operandStack.isEmpty()) &&
//							parseOperand(operandStack.peek()) < parseOperand(expression[j])) {
//						postFixList.push(operandStack.pop());
//					}
//					operandStack.push(expression[j]);
//				}
//			}
//		}
//
//		// Add the rest of the operands remaining in the operand stack
//		while(!operandStack.isEmpty()) {
//			postFixList.push(operandStack.pop());
//		}
//
//		// Print in reverse stack order
//		Stack<String> printStack = new Stack<>();
//		while(!postFixList.isEmpty()) {
//			printStack.push(postFixList.pop());
//		}
//		StringBuilder sb = new StringBuilder();
//		while(!printStack.isEmpty()) {
//			sb.append(printStack.pop() + " ");
//		}
//
//		// Print to console
//		print("Postfix: " + sb.toString());
//
//		// Add to the list of functions
//		calculator.addFunction(sb.toString());
//
//		// Update world
//		calculator.calculate(engine.getWorld().get(0));
//	}
//
//	private static void notUnderstood(String[] strings) {
//		StringBuffer sb = new StringBuffer();
//		sb.append("NOT UNDERSTOOD: ");
//		for(int j = 0; j < strings.length; j++) {
//			sb.append(strings[j] + " ");
//		}
//		print(sb.toString());
//	}
//}
