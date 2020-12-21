package TSP_solver.ABC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;

public class BeeColony implements Algorithm {
	private static final int NUM_BEE = 100; // number of bees
	private static final int trial_limit = 5;
	private static int num_iteration = 2000;
	private static Random rand = new Random();
	
	private ArrayList<Bee> foodSources;
	private Route bestRoute;
	private static int randomExclusiveNumber(int bound, int exp) {
		int r = -1;
		
		do {
			r = rand.nextInt(bound);
		} while (r == exp);
		
		return r;
	}
	
	private void sendEmployedBees() throws Exception {
		Bee currentBee = null, neighborBee = null;
		
		for (int i=0; i<NUM_BEE; ++i) {
			currentBee = this.foodSources.get(i);
			neighborBee = this.foodSources.get(randomExclusiveNumber(NUM_BEE, i));
			currentBee.work(neighborBee);
		}
	}
	
	private void updateProfitbaility() {
		double bestFit = -this.foodSources.get(0).getRoute().getFitness();
		double worstFit = -this.foodSources.get(0).getRoute().getFitness();
		for (int i=1; i<NUM_BEE;++i) {
			double fit = -this.foodSources.get(i).getRoute().getFitness();
			if (fit < bestFit)
				bestFit = fit;
			if (fit > worstFit)
				worstFit = fit;
		}
		
		for (int i=0; i<NUM_BEE; ++i) {
			this.foodSources.get(i).updateProfitability(bestFit, worstFit);
		}
	}
	
	private void sendOnlookerBees() throws Exception {
		Bee currentBee = null, neighborBee = null;
		int j = 0;
		
		for (int i=0; i<NUM_BEE; ++i) {
			boolean worked = false;
			
			while (!worked) {
				if (rand.nextDouble() < this.foodSources.get(j).getProfitbility()) {
					currentBee = this.foodSources.get(j);
					neighborBee = this.foodSources.get(randomExclusiveNumber(NUM_BEE, j));
					currentBee.work(neighborBee);
					worked = true;
				}
				
				j = (j+1)%NUM_BEE;
			}
		}
	}
	
	private void sendScoutBees() throws Exception {
		for (int i=0; i<NUM_BEE; ++i) {
			if (this.foodSources.get(i).getTrial() >= trial_limit) {
				this.foodSources.get(i).scout();
			}
		}
	}
	
	private static ArrayList<Bee> sort(ArrayList<Bee> routes) {
		if (routes.size() < 2)
			return routes;
		
		int middle = (int) Math.ceil(routes.size()/2);
		ArrayList<Bee> firstHalf = new ArrayList<Bee>();
		ArrayList<Bee> secondHalf = new ArrayList<Bee>();
		
		for (int i=0;i<routes.size();++i) {
			if (i < middle)
				firstHalf.add(routes.get(i));
			else
				secondHalf.add(routes.get(i));
		}
		
		ArrayList<Bee> firstHalfSorted = sort(firstHalf);
		ArrayList<Bee> secondHalfSorted = sort(secondHalf);
		
		ArrayList<Bee> sortedList = new ArrayList<Bee>();
		int index1 = 0, index2 = 0;
		
		while (sortedList.size() < routes.size()) {
			if (index1 >= firstHalfSorted.size())
				sortedList.add(secondHalfSorted.get(index2++));
			else if (index2 >= secondHalfSorted.size())
				sortedList.add(firstHalfSorted.get(index1++));
			else {
				Bee c1 = firstHalfSorted.get(index1);
				Bee c2 = secondHalfSorted.get(index2);
				if (c1.getRoute().compareTo(c2.getRoute()) > 0) {
					sortedList.add(c1);
					++index1;
				} else {
					sortedList.add(c2);
					++index2;
				}
			}
		}
		
		return sortedList;
	}
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		this.bestRoute = null;
		cities.size();
		
		this.foodSources = new ArrayList<Bee>();
		
		for (int i=0; i<NUM_BEE;++i) {
			foodSources.add(new Bee(cities));
			
			if (this.bestRoute == null || this.foodSources.get(i).getRoute().compareTo(bestRoute) > 0) {
				this.bestRoute = this.foodSources.get(i).getRoute().deepCopy();
			}
		}
		
		for (int i=0; i<num_iteration;++i) {
			this.foodSources = sort(this.foodSources);
			sendEmployedBees();
			updateProfitbaility();
			sendOnlookerBees();
			
			for (int j=0; j<NUM_BEE;++j) {
				if (this.foodSources.get(j).getRoute().compareTo(bestRoute) > 0) {
					this.bestRoute = this.foodSources.get(j).getRoute().deepCopy();
				}
			}
			
			sendScoutBees();
		}
		
		return this.bestRoute;
	}
}
