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
	
	private static ArrayList<Route> getNeighbors(Route r) throws SolveTSPFailedException {
		ArrayList<Route> neighbors = new ArrayList<Route>();
		
		while (neighbors.size() < num_neighbors) {
			Integer[] order = r.getOrderDeepCopy();
			int index1 = rand.nextInt(order.length);
			int index2 = rand.nextInt(order.length);
			int temp = order[index2];
			order[index2] = order[index1];
			order[index1] = temp;
		
			neighbors.add(new Route(order, r.getCities()));
		}
		
		
		return neighbors;
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
			neighbors = getNeighbors(currentRoute);
			
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
