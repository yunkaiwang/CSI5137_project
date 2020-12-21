package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;

public class TabuSearch implements Algorithm {
	
	private static final int MAX_TABULIST_SIZE = 100;
	private static final int NUMBER_ITERATION = 2000;
	private static final int NUM_NEIGHBORS = 100;
	protected Route bestRoute = null;
	private ArrayList<Double> tabuList;
	
	public void reset() {
		bestRoute = null;
		tabuList = new ArrayList<Double>();
	}
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		reset();
		Route currentRoute = Route.generateRandomRoute(cities);
		bestRoute = currentRoute;
		this.tabuList.add(this.bestRoute.getFitness());
		
		for (int i=0; i<NUMBER_ITERATION;++i) {
			ArrayList<Route> neighbors = Route.getNeighbors(currentRoute, NUM_NEIGHBORS);
			currentRoute = neighbors.get(0);
			
			for (int j=1; j<NUM_NEIGHBORS;++j) {
				if (!tabuList.contains(neighbors.get(j).getFitness()) && neighbors.get(j).compareTo(currentRoute) > 0)
					currentRoute = neighbors.get(j);
			}
			
			if (currentRoute.compareTo(bestRoute) > 0)
				bestRoute = currentRoute;
			
			if (!tabuList.contains(currentRoute.getFitness()))
				tabuList.add(currentRoute.getFitness());
			
			if (tabuList.size() > MAX_TABULIST_SIZE)
				tabuList.remove(0);
		}
		
		return bestRoute;
	}

}
