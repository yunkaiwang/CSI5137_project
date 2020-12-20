package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;
import java.util.Random;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.SolveTSPFailedException;

public class SimulatedAnnealing implements Algorithm {
	private static int num_iteration = 100;
	private static int start_temp = 1000;
	private static double cooling_factor = 0.999;
	private static final Random rand = new Random();
	
	private Route bestRoute;
	
	public void reset() {
		this.bestRoute = null;
	}
	
	public Route getBestRoute() {
		return this.bestRoute;
	}
	
	/*
	 * Calculate the probability of accepting the neighbour given current solution's fitness.
	 * It's 1 if the neighbour is strictly better than the second solution.
	 * It's calculated using exponential function given the distance between current solution and the neighbor,
	 * divided by the current temperature
	 */
	private static double probability(double f1, double f2, double temp) {
		if (f1 > f2)
			return 1;
		return Math.exp((f1-f2)/temp);
	}
	
	private static Route getNeighbor(Route r) throws SolveTSPFailedException {
		Integer[] order = r.getOrderDeepCopy();
		int index1 = 0, index2 = 0;
		while (index1==index2) {
			index1 = rand.nextInt(order.length);
			index2 = rand.nextInt(order.length);
		}

		int temp = order[index2];
		order[index2] = order[index1];
		order[index1] = temp;
		
		return new Route(order, r.getCities());
	}
	
	public Route simulatedAnnealing(Route startRoute) throws SolveTSPFailedException {
		double temp = start_temp;
		Route currentRoute = startRoute, currentBestRoute = null;
		
		while (temp > 1) {
			Route neighbor = getNeighbor(currentRoute);
			if (rand.nextDouble() < probability(-currentRoute.getFitness(), -neighbor.getFitness(), temp)) {
				currentRoute = neighbor;
			}
			
			if (currentBestRoute == null || currentRoute.compareTo(currentBestRoute) > 0) {
				currentBestRoute = currentRoute.deepCopy();
			}
			
			temp *= cooling_factor;
		}
		
		if (bestRoute == null || currentBestRoute.compareTo(bestRoute) > 0) {
			bestRoute = currentBestRoute.deepCopy();
		}
		
		return currentBestRoute;
	}
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		reset();
		
		for (int i=0; i<num_iteration;++i) {
			simulatedAnnealing(Route.generateRandomRoute(cities));
		}
		
		return bestRoute;
	}

}
