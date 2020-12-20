package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;

import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.SolveTSPFailedException;

public class CombinedAlgorithm2 extends GeneticAlgorithm {

	@Override
	protected ArrayList<Route> initialize(ArrayList<City> cities) throws SolveTSPFailedException {
		ArrayList<Route> initialPool = new ArrayList<Route>();
		SimulatedAnnealing sa = new SimulatedAnnealing();
		
		for (int i=0; i<pool_size;++i) {
			Route randRoute = Route.generateRandomRoute(cities);
			
			sa.reset();
			sa.simulatedAnnealing(randRoute);
			
			initialPool.add(sa.getBestRoute());
		}
		return initialPool;
	}
}
