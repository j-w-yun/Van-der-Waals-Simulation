package org.yoon_technology.math;

class Boltzmann {

	private static Boltzmann instance = null;
	private static final double BOLTZMANN_CONSTANT = 1.38 * Math.pow(10.0, -23.0);	// units: J / K

	public static Boltzmann getInstance() {
		return instance == null ? instance = new Boltzmann() : instance;
	}

	private Boltzmann() {}

	// units: J, K
	public static double pOfParticleEnergy(double normalizationConstant, double energy, double temperature) {
		// probability that a particle with given energy E can be found increases with temperature
		double result = (BOLTZMANN_CONSTANT * temperature);
		// probability for occupying a give energy state decreases exponentially with energy
		result = Math.exp(energy / result);
		// normalize
		result *= normalizationConstant;
		// inverse
		result = 1.0 / result;

		return result;
	}
}
