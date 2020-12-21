package TSP_solver.ABC;

import java.util.ArrayList;
import java.util.Random;

import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Swap;
import TSP_solver.Exception.SolveTSPFailedException;
import TSP_solver.SimpleAlgorithms.HillClimb;

public class Bee {
	public static final Random rand = new Random();
	private static final double random_factor = 0.1;
	private ArrayList<City> cities;

	private Route route;
	private int numTrial;
	private double profitability;
	
	
	public Bee(ArrayList<City> cities) throws SolveTSPFailedException {
		this.cities = cities;
		this.numTrial = 0;
		HillClimb hc = new HillClimb();
		Route randRoute = Route.generateRandomRoute(cities);
		hc.hillClimb(randRoute, 5000);
		this.route = hc.getBestRoute();
	}
	
	public void updateProfitability(double bestFit, double worstFit) {
		this.profitability = (-this.route.getFitness()-bestFit) / (worstFit-bestFit);
	}
	
	public double getProfitbility() {
		return this.profitability;
	}
	
	public Route getRoute() {
		return this.route;
	}
	
	public int getTrial() {
		return this.numTrial;
	}

	public void work(Bee neighborBee) throws Exception {
		ArrayList<Swap> swaps = Route.swapsBetweenRoutes(this.route, neighborBee.route);
		ArrayList<Swap> stepsToTake = new ArrayList<Swap>();
		double p = rand.nextDouble();
		
		for (Swap swap : swaps) {
			if (rand.nextDouble() < p)
				stepsToTake.add(swap);
		}
		
		if (rand.nextDouble() < random_factor) {
			int index1 = 0, index2 = 0;
			do {
				index1 = rand.nextInt(this.cities.size());
				index2 = rand.nextInt(this.cities.size());
			} while (index1 == index2);
			
			stepsToTake.add(new Swap(index1, index2));
		}
		
		Route newRoute = this.route.deepCopy().applyVelocities(stepsToTake);
		
		if (newRoute.compareTo(route) > 0) {
			numTrial = 0;
			this.route = newRoute;
		} else {
			++numTrial;
		}
	}
	
	public void scout() throws Exception {
		this.route = Route.generateRandomRoute(this.cities);
		this.numTrial = 0;
	}
	
}
