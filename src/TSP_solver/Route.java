package TSP_solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import TSP_solver.Exception.SolveTSPFailedException;

public class Route implements Comparable<Route> {
	private Integer[] order;
	private ArrayList<City> cities;
	private Double fitness;
	public static final Random rand = new Random();
	
	public Route(Integer[] order, ArrayList<City> cities) throws SolveTSPFailedException {
		this.fitness = -fitness(cities, order);
		this.order = order;
		this.cities = cities;
	}
	
	public Route(Integer[] order, ArrayList<City> cities, Double fitness) {
		this.fitness = fitness;
		this.order = order;
		this.cities = cities;
	}
	
	public Double getFitness() {
		return this.fitness;
	}
	
	public Integer[] getOrder() {
		return this.order;
	}
	
	public void changeOrder(int i1, int i2) throws SolveTSPFailedException {
		Integer[] newOrder = new Integer[this.cities.size()];
		
		for (int i=0; i<this.cities.size();++i) {
			if (i==i1)
				newOrder[i] = this.order[i2];
			else if (i==i2)
				newOrder[i] = this.order[i1];
			else
				newOrder[i] = this.order[i];
		}
		this.setOrder(newOrder);
	}
	
	public void setOrder(Integer[] newOrder) throws SolveTSPFailedException {
		assert this.order.length == newOrder.length;
		this.order = newOrder;
		this.fitness = -fitness(cities, order); // re-compute fitness
	}
	
	public ArrayList<City> getCities() {
		return cities;
	}
	
	@Override
	public int compareTo(Route other) {
		return this.getFitness().compareTo(other.getFitness());
	}
	
	@Override
	public String toString() {
		String rep = "";
		
		rep += "Fitness: " + String.format("%.2f", -this.getFitness()) + "\nPermutation:";
		for (int i=0; i<order.length;++i) {
			rep += " " + Integer.toString(order[i] + 1);
		}
		rep += "\n";
		
		return rep;
	}
	
	public static final double fitness(ArrayList<City> cities, Integer[] order) throws SolveTSPFailedException {
		double totalDistance = 0;
		
		if (cities.size() != order.length) {
			throw new SolveTSPFailedException();
		}
		
		for (int i=0; i<order.length-1; ++i) {
			totalDistance += cities.get(order[i]).distance(cities.get(order[i+1]));
		}
		
		totalDistance += cities.get(order[order.length-1]).distance(cities.get(order[0]));
		
		return totalDistance;
	}
	
	private static Integer[] randArray(int numCities) {
		Integer[] intArray = new Integer[numCities];
		for (int i=0; i<numCities; ++i) {
			intArray[i] = i;
		}
		List<Integer> intList = Arrays.asList(intArray);
		
		Collections.shuffle(intList);
		
		for (int i=0; i<numCities; ++i) {
			intArray[i] = intList.get(i);
		}
		
		return intArray;
	}
	
	public static Route generateRandomRoute(ArrayList<City> cities) throws SolveTSPFailedException {
		return new Route(randArray(cities.size()), cities);
	}
	
	public Route deepCopy() throws SolveTSPFailedException {
		return new Route(this.getOrderDeepCopy(), this.cities);
	}
	
	public static void printOrder(Integer[] order) {
		String st = "";
		for (int i=0;i<order.length;++i) {
			st += order[i] + " ";
		}
		st += "\n";
		System.out.println(st);
	}

	public Integer[] getOrderDeepCopy() {
		Integer[] order_copy = new Integer[this.order.length];
		for (int i=0; i<this.order.length;++i) {
			order_copy[i] = this.order[i];
		}
		return order_copy;
	}
	
	// get the new route after the set of swaps has been applied, useful for the PSO algorithm
	public Route applyVelocities(ArrayList<Swap> velocity) throws SolveTSPFailedException {
		Integer[] order_copy = new Integer[this.cities.size()];
		
		for (int i=0; i<this.cities.size();++i) {
			order_copy[i] = this.order[i];
		}
		
		for (Swap swap: velocity) {
			int i1 = swap.getFirstIndex(), i2 = swap.getSecondIndex();
			
			Integer temp = order_copy[i1];
			order_copy[i1] = order_copy[i2];
			order_copy[i2] = temp;
		}
		
		return new Route(order_copy, cities);
	}
	
	private static int findIndexInArray(Integer[] arr, int target) {
		for (int i=0; i<arr.length;++i) {
			if (arr[i]==target)
				return i;
		}
		return -1;
	}
	
	/*
	 * This function computes the list of swaps required to turn r2 into r1 (
	 * such that r1 and r2 have the same order)
	 */
	public static ArrayList<Swap> swapsBetweenRoutes(Route r1, Route r2) {
		ArrayList<Swap> swaps = new ArrayList<Swap>();
		
		Integer[] r1_order = r1.getOrder();
		Integer[] r2_order = r2.getOrderDeepCopy();
		
		for (int i=0; i<r1_order.length;++i) {
			int i1 = findIndexInArray(r1_order, i);
			int i2 = findIndexInArray(r2_order, i);
			if (i1==i2)
				continue;
			else {
				swaps.add(new Swap(i1, i2));
				int temp = r2_order[i1];
				r2_order[i1] = r2_order[i2];
				r2_order[i2] = temp;
			}
		}
		
		return swaps;
	}
	
	public static Route getNeighbor(Route r) throws SolveTSPFailedException {
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
	
	public static ArrayList<Route> getNeighbors(Route r, int num_neighbors) throws SolveTSPFailedException {
		ArrayList<Route> neighbors = new ArrayList<Route>();
		
		while (neighbors.size() < num_neighbors) {
			neighbors.add(getNeighbor(r));
		}
		
		return neighbors;
	}
}