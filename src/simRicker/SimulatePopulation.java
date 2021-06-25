package simRicker;

import java.util.*;

public class SimulatePopulation {
	
	/* 
	 * Run S simulations of one population
	*/
	public static void main(String[] args) {
		double mu = 1.1;
		double sigma = 0.5;
		double N0 = 10;
		double K = 500;
		int maxYears = 10;
		boolean envStoch = true;
		int S = 10000;
		boolean logSummaries = false;
		
		List<List<Integer>> S_N = new ArrayList<List<Integer>>();
		for(int s = 0; s < S; s++) {
			S_N.add(simulateOnePopulation(mu, sigma, N0, K, maxYears, envStoch));
		}
		
		ArrayList<Double> S_Mean = meanAcrossSimulations(S_N, maxYears, logSummaries);
		ArrayList<Double> S_Var = varianceAcrossSimulations(S_N, S_Mean, logSummaries);
		
		System.out.println(S + " simulations with mu=" + mu + ", sigma=" + sigma + ", envStoch=" + envStoch);
		System.out.println("\nMeans across simulations by year:");
		S_Mean.forEach(value -> System.out.print(Math.round(value) + " "));
		System.out.println("\n\nVariances across simulations by year:");
		S_Var.forEach(value-> System.out.print(Math.round(value) + " "));
	}
	
	
	
	/* 
	 * Run one simulation of one population
	*/
	public static ArrayList<Integer> simulateOnePopulation(double mu, double sigma, double N0, 
			double K, int maxYears, boolean envStoch) {
		
		ArrayList<Integer> N = new ArrayList<Integer>();
		N.add((int) Math.round(N0));
		for(int i = 0; i < maxYears; i++) {
			double i_r = assignGrowthRate(mu, sigma, N.get(i), K, envStoch);
			N.add((int) Math.max(Math.round(iterateYear(i_r, N.get(i), K)), 0));
		}
		return N;
	}
	
	
	
	/* 
	 * Iterate population for a single year
	*/
	public static double iterateYear(double r, double N0, double K) {
		
		double i_N = N0 * Math.exp(r);
		return i_N;
		
	}
	
	
	
	/* 
	 * Assign growth rate for year i
	*/
	public static double assignGrowthRate(double mu, double sigma, double i_N, double K, boolean envStoch) {
		double i_mu;
		if(envStoch) {
			Random i_random = new Random();
			i_mu = i_random.nextGaussian() * sigma + mu;
		} else {
			i_mu = mu;
		}
		double i_r = i_mu * (1 - i_N/K);
		return i_r;
	}
	
	
	
	/* 
	 * Calculate mean N across simulations
	*/
	public static ArrayList<Double> meanAcrossSimulations(List<List<Integer>> S_N, int maxYears, boolean log) {
		ArrayList<Double> mean_N = new ArrayList<Double>();
		
		for(int i = 0; i <= maxYears; i++) {
			double i_totN = 0; 
			for(int s = 0; s < S_N.size(); s++) {
				if(log) {
					i_totN += Math.log(S_N.get(s).get(i));
				} else {					
					i_totN += S_N.get(s).get(i);
				}
			}
			mean_N.add(i_totN / S_N.size());
		}
		return mean_N;
	}
	
	
	
	/* 
	 * Calculate var(N) across simulations
	*/
	public static ArrayList<Double> varianceAcrossSimulations(List<List<Integer>> S_N, ArrayList<Double> mean_N, boolean log) {
		
		ArrayList<Double> var_N = new ArrayList<Double>();
		
		for(int i = 0; i < mean_N.size(); i++) {
			double sumOfSquaredErrors = 0; 
			for(int s = 0; s < S_N.size(); s++) {
				if(log) {					
					sumOfSquaredErrors += Math.pow(mean_N.get(i) - Math.log(S_N.get(s).get(i)), 2);
				} else {
					sumOfSquaredErrors += Math.pow(mean_N.get(i) - S_N.get(s).get(i), 2);
				}
			}
			var_N.add(sumOfSquaredErrors / S_N.size());
		}
		return var_N;
	}
	
}

