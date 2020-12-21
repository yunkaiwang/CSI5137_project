package TSP_solver.ACO;

import java.util.ArrayList;
import java.util.Random;

import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.ACOFailedException;

public class Ant implements Comparable<Ant> {
	private static Random rand = new Random();
	private static double random_factor = 0.01;
	private static double alpha = 1; // pheromone importance
	private static double beta = 2; // distance priority
	
	private ArrayList<City> cities;
	
	private int numCities;
	
	// the index of the city that the ant is currently in
	private int currentCityIndex;
	
	// Each index i in this array should store the index of the city we visit after we have visited city i
	private int[] order;
	
	// Each entry in this array correspond to one specific city, true if we have already visited the city
	private boolean[] visited;
	
	public Ant(ArrayList<City> cities) {
		this.cities = cities;
		this.numCities = cities.size();
		resetAnt();
	}
	
	public void resetAnt() {
		this.currentCityIndex = -1;
		this.visited = new boolean[numCities];
		this.order = new int[numCities];
		
		for (int i=0; i<numCities; ++i) {
			this.visited[i]=false;
			this.order[i]=-1;
		}
	}
	
	public int[] getOrder() {
		return this.order;
	}
	
	public boolean visitedCity(int cityIndex) {
		return this.visited[cityIndex];
	}
	
	public boolean finishedTour() {
		for (int i=0; i<this.numCities;++i) {
			if (!visited[i])
				return false;
		}
		return true;
	}
	
	// return the city we visit next after we visit the given city
	public int nextCity(int cityIndex) {
		return this.order[cityIndex];
	}
	
	private boolean visitCity(int cityIndex) {
		// check if the city has been visited
		if (cityIndex < 0 || cityIndex >= this.numCities || this.visitedCity(cityIndex)) {
			return false;
		}
		
		this.visited[cityIndex] = true;
		this.order[currentCityIndex] = cityIndex;
		this.currentCityIndex = cityIndex;
		return true;
	}
	
	public double travelledDistance() {
		if (!this.finishedTour()) {
			return -1;
		}
		int startIndex = 0;
		int prevIndex = startIndex, currIndex = order[startIndex];
		double distance = 0;
		
		while (currIndex != startIndex) {
			distance += cities.get(prevIndex).distance(cities.get(currIndex));
			prevIndex = currIndex;
			currIndex = order[currIndex];
		}
		
		distance += cities.get(prevIndex).distance(cities.get(startIndex));
		
		return distance;
	}
	
	public Route toRoute() {
		Integer[] visitedOrder = new Integer[this.numCities];
		
		int currentIndex = 0;
		
		for (int i=0; i<this.numCities;++i) {
			visitedOrder[i] = currentIndex;
			currentIndex = this.order[currentIndex];
		}
		
		return new Route(visitedOrder, this.cities, -this.travelledDistance());
	}

	@Override
	public int compareTo(Ant other) {
		if (!this.finishedTour())
			return -1;
		else if (!other.finishedTour())
			return 1;
		else {
			double d1 = this.travelledDistance();
			double d2 = other.travelledDistance();
			if (d1 > d2)
				return 1;
			else if (d1 == d2)
				return 0;
			else
				return -1;
		}
	}
	
	private int selectNextCity(double[][] pheromone) {
		if (rand.nextDouble() < random_factor) {
			int nextCity = rand.nextInt(numCities);
			if (!this.visitedCity(nextCity))
				return nextCity;
		}
		
		double sum_pheromone = 0;
		for (int i=0; i<numCities;++i) {
			if (!this.visitedCity(i))
				sum_pheromone += Math.pow(pheromone[this.currentCityIndex][i], alpha) *
					Math.pow(1/this.cities.get(this.currentCityIndex).distance(this.cities.get(i)), beta);
		}
		
		double current_prob_sum = 0.0, prob = rand.nextDouble();
		for (int i=0; i<numCities;++i) {
			if (!this.visitedCity(i)) {
				current_prob_sum += Math.pow(pheromone[this.currentCityIndex][i], alpha) *
						Math.pow(1/this.cities.get(this.currentCityIndex).distance(this.cities.get(i)), beta) /
						sum_pheromone;
				if (current_prob_sum >= prob)
					return i;
			}
		}
		
		for (int i=0; i<numCities;++i) {
			if (!this.visitedCity(i))
				return i;
		}
		return -1;
	}

	public void makeTour(double[][] pheromone) throws ACOFailedException {
		this.resetAnt();
		int firstCityIndex = rand.nextInt(this.numCities); // start the tour with a random city, this won't affect the result
		this.currentCityIndex = firstCityIndex;
		this.visited[this.currentCityIndex] = true;
		
		for (int i=0; i<this.numCities-1;++i) {
			if (!this.visitCity(this.selectNextCity(pheromone)))
				throw new ACOFailedException();
		}
		
		this.order[this.currentCityIndex] = firstCityIndex; // complete the tour
	}
	
	public Ant deepCopy() {
		Ant newAnt = new Ant(this.cities);
		
		for (int i=0; i<this.numCities;++i) {
			newAnt.visited[i] = true;
		}
		newAnt.order = this.order;
		newAnt.currentCityIndex = this.currentCityIndex;
		
		return newAnt;
	}
}
