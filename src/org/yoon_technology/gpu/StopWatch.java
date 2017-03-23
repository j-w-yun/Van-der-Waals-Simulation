package org.yoon_technology.gpu;

import java.text.DecimalFormat;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class StopWatch {

	private long start;
	private DecimalFormat df;

	public StopWatch() {
		df = new DecimalFormat("#");
		df.setMaximumFractionDigits(20);
		df.setMinimumIntegerDigits(1);
	}

	public void start() {
		start = System.nanoTime();
	}

	public double delta() {
		return (double)(System.nanoTime() - start) / 1000000000.0;
	}

	public void print() {
		System.out.println(df.format((double)(System.nanoTime() - start) / 1000000000.0));
	}

	public void print(String label) {
		System.out.println(label + ": " + df.format((double)(System.nanoTime() - start) / 1000000000.0));
	}
}
