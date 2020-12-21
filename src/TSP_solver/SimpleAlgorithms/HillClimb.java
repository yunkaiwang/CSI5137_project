package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;
import java.util.Random;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.SolveTSPFailedException;

public class HillClimb implements Algorithm {
	private static int num_iteration = 5000;
	private static int num_neighbors = 100;
	private static final Random rand = new Random();
	// keep track of the best solution found so far
	protected Route bestRoute = null;
		
	public void reset() {
		bestRoute = null;
	}
	
	public Route getBestRoute() {
		return this.bestRoute;
	}
	
	public int hillClimb(Route startRoute, int remainingIterations) throws SolveTSPFailedException {
		ArrayList<Route> neighbors = null;
		Route currentRoute = startRoute;
		boolean improvedOverLastIteration = true;
		
		while (remainingIterations-- > 0) {
			if (!improvedOverLastIteration) {
				return remainingIterations;
			}
			
			improvedOverLastIteration = false;
			neighbors = Route.getNeighbors(currentRoute, num_neighbors);
			
			for (Route r: neighbors) {
				if (r.compareTo(currentRoute) > 0) {
					currentRoute = r;
					improvedOverLastIteration = true;
				}
			}
			
			if (bestRoute == null || currentRoute.compareTo(bestRoute) > 0) {
				bestRoute = currentRoute.deepCopy();
			}
		}
		
		return remainingIterations;
	}
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		reset();
		
		int ite = num_iteration;
		while ((ite=hillClimb(Route.generateRandomRoute(cities), ite)) > 0)
			;
		
		return bestRoute;
	}

}
