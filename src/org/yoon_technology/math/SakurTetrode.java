package org.yoon_technology.math;

public class SakurTetrode {

	private static SakurTetrode instance = null;
	private static final double PLANKS_CONSTANT = 6.6260700404 * Math.pow(10, -34); // units: J / s
	private static final double BOLTZMANN_CONSTANT = 1.38 * Math.pow(10.0, -23.0);	// units: J / K

	public static SakurTetrode getInstance() {
		return instance == null ? instance = new SakurTetrode() : instance;
	}

	private SakurTetrode() {}

	//	public static double entropy(int numParticles, double mass, double energy, double volume) {
	//		double threeN = 3.0 * numParticles;
	//
	//		// (2*pi*h)^(3N)
	//		double result = (2.0 * Math.PI * PLANKS_CONSTANT);
	//		result = Math.pow(result, threeN);
	//
	//		// 1/N!
	//		result *= (1.0 / Function.factorial(numParticles));
	//
	//		// 0.5
	//		result *= 0.5;
	//
	//		// V^N
	//		result *= Math.pow(volume, numParticles);
	//
	//		// (2*m*E)^(3N/2)
	//		result *= Math.pow((2 * mass * energy), (threeN / 2));
	//
	//		// (2*pi)^(3N/2)
	//		result *= Math.pow((2 * Math.PI), (threeN / 2));
	//
	//		// div ((3N/2)-1)!
	//		result /= Function.factorial(((threeN / 2.0) - 1.0));
	//
	//		// log(result)
	//		result = Math.log(result);
	//
	//		// kB*result
	//		result = BOLTZMANN_CONSTANT * Math.log(result);
	//	}

	public static double entropy(int numParticles, double mass, double energy, double volume) {

		// (4*pi*m*E)
		double result = (4.0 * Math.PI * mass * energy);

		// div (3N*h^2)
		result /= (3.0 * numParticles * PLANKS_CONSTANT * PLANKS_CONSTANT);

		// result^(3/2)
		result = Math.pow(result, (3.0 / 2.0));

		// (v/N)
		result *= (volume / numParticles);

		// add (5/2)
		result += (5.0 / 2.0);

		// (N*kB)
		result *= (numParticles * BOLTZMANN_CONSTANT);

		return result;
	}
}
