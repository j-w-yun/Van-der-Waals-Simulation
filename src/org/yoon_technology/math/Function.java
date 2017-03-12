package org.yoon_technology.math;

public class Function {

	private static long currentN;

	// Inclusive
	public static double clamp(double value, double min, double max) {
		if(value <= min)
			return min;
		else if(value >= max)
			return max;
		else return value;
	}

	public static long factorial(int n) {
		if(n < 2)
			return 1;

		long p = 1;
		long r = 1;
		currentN = 1;

		int h = 0;
		int shift = 0;
		int high = 1;

		int log2n = (int) (Math.log10(n) / Math.log10(2));

		while(h != n) {
			shift += h;
			h = n >> log2n--;;
			int len = high;
			high = (h - 1) | 1;
			len = (high - len) / 2;

			if(len > 0) {
				p *= product(len);
				r *= p;
			}
		}

		return r << shift;
	}

	private static long product(int n) {
		int m = n / 2;
		if(m == 0)
			return currentN += 2;
		if(n == 2)
			return (currentN += 2) * (currentN += 2);
		return product(n - m) * product(m);
	}


	public static void main(String[] args) {
		System.out.println(factorial(15));
	}
}
