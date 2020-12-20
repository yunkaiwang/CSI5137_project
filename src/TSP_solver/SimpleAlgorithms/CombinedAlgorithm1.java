package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;

import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.SolveTSPFailedException;

public class CombinedAlgorithm1 extends GeneticAlgorithm {
	
	@Override
	protected ArrayList<Route> initialize(ArrayList<City> cities) throws Exception {
		ArrayList<Route> initialPool = new ArrayList<Route>();
		HillClimb hc = new HillClimb();
		
		for (int i=0; i<pool_size;++i) {
			Route randRoute = Route.generateRandomRoute(cities);
			
			hc.reset();
			hc.hillClimb(randRoute, 5000);
			
			initialPool.add(hc.getBestRoute());
		}
		return initialPool;
	}
}
