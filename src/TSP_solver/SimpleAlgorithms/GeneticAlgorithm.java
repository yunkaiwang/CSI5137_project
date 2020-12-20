package TSP_solver.SimpleAlgorithms;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.SolveTSPFailedException;

public class GeneticAlgorithm implements Algorithm {
	protected static int pool_size = 1000;
	protected static int mating_pool_size = 100;
	protected static double mutation = 0.1;
	protected static int num_iteration = 500;
	protected static final Random rand = new Random();
	
	// keep track of the best solution found so far
	protected Route bestRoute = null;
	
	// reset the best solution
	protected void reset() {
		bestRoute = null;
	}
	
	protected ArrayList<Route> initialize(ArrayList<City> cities) throws Exception {
		ArrayList<Route> initialPool = new ArrayList<Route>();
		for (int i=0; i<pool_size;++i) {
			initialPool.add(Route.generateRandomRoute(cities));
		}
		return initialPool;
	}
	
	private ArrayList<Route> selection(ArrayList<Route> pool) throws SolveTSPFailedException {
		ArrayList<Route> matingPool = new ArrayList<Route>();
		
		double totalFitness = 0.0;
		double matingPoolFitness = 0.0;
		PriorityQueue<Route> pq = new PriorityQueue<Route>();
		
		for (int i=0; i<mating_pool_size; ++i) {
			Route r = pool.get(i);
			
			totalFitness -= r.getFitness();
			matingPoolFitness -= r.getFitness();
			
			pq.add(r);
			
			if (bestRoute==null || r.compareTo(bestRoute) > 0) {
				bestRoute = r.deepCopy();
			}
		}
		
		for (int i=mating_pool_size; i<pool_size;++i) {
			Route r = pool.get(i);
			
			totalFitness -= r.getFitness();
			
			if (r.compareTo(pq.peek()) > 0) {
				matingPoolFitness += pq.poll().getFitness();
				pq.add(r);
				matingPoolFitness -= r.getFitness();
			}
			
			if (bestRoute==null || r.compareTo(bestRoute) > 0) {
				bestRoute = r.deepCopy();
			}
		}
		
		while (!pq.isEmpty())
			matingPool.add(pq.poll());
		
		System.out.println("Average fitness is " + totalFitness/pool_size + ", average mating pool average is " + matingPoolFitness/mating_pool_size);
		
		return matingPool;
	}
	
	private static int nextPos(Integer[] cycles) {
		for (int i=0; i<cycles.length;++i) {
			if (cycles[i] == -1) 
				return i;
		}
		return -1;
	}
	
	private static int findPosInPerm(int pos, Integer[] perm) {
		for (int i=0; i<perm.length;++i) {
			if (perm[i]==pos)
				return i;
		}
		return -1;
	}
	
	private static ArrayList<Route> crossover(Route r1, Route r2) throws SolveTSPFailedException {
		assert r1.getCities().size()==r2.getCities().size() : "Unmached number of cities in permutations";
		ArrayList<Route> children = new ArrayList<Route>();
		int length = r1.getCities().size();
		
		Integer[] child1 = new Integer[length];
		Integer[] child2 = new Integer[length];
		
		Integer[] cycles = new Integer[length];
		for (int i=0; i<length;++i){
			cycles[i] = -1;
		}
		
		int cycle_count = 1;
		int pos = -1;
		
		while ((pos=nextPos(cycles)) > 0) {
			while (cycles[pos] < 0) {
				cycles[pos] = cycle_count;
				pos = findPosInPerm(r2.getOrder()[pos], r1.getOrder());
			}
			
			cycle_count += 1;
		}
		
		for (int i=0; i<length;++i) {
			if (cycles[i]%2==1) {
				child1[i] = r1.getOrder()[i];
				child2[i] = r2.getOrder()[i];
			} else {
				child1[i] = r2.getOrder()[i];
				child2[i] = r1.getOrder()[i];
			}
		}
		
		children.add(new Route(child1, r1.getCities()));
		children.add(new Route(child2, r2.getCities()));
		
		return children;
	}
	
	private static Route mutation(Route r) throws SolveTSPFailedException {
		if (rand.nextDouble() < mutation) {
			Integer[] order = r.getOrder();
			int index1 = rand.nextInt(order.length);
			int index2 = rand.nextInt(order.length);
			int temp = order[index2];
			order[index2] = order[index1];
			order[index1] = temp;
			
			r.setOrder(order);
		}
		
		return r;
	}
	
	protected Route geneticAlgorithm(ArrayList<Route> initialPool) throws Exception {
		ArrayList<Route> currentPool = initialPool;
		ArrayList<Route> matingPool, nextPool;
		for (int i=0; i<num_iteration;++i) {
			
			matingPool = selection(currentPool);
			nextPool = new ArrayList<Route>();
			
			while (nextPool.size() < pool_size) {
				int index1 = rand.nextInt(mating_pool_size);
				int index2 = rand.nextInt(mating_pool_size);
				while (index2 == index1) {
					index2 = rand.nextInt(mating_pool_size);
				}
				
				ArrayList<Route> child = crossover(matingPool.get(index1), matingPool.get(index2));
				nextPool.add(mutation(child.get(0)));
				nextPool.add(mutation(child.get(1)));
			}
			
			currentPool = nextPool;
		}
		
		return bestRoute;
	}

	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		reset();
		
		return geneticAlgorithm(this.initialize(cities));
	}
}
