package TSP_solver.ACO;

import java.util.ArrayList;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;
import TSP_solver.Exception.ACOFailedException;

public class AntColony implements Algorithm {
	private static int num_iteration = 100;
	private static double ant_factor = 0.8; // number of node per city
	private static double evaporation = 0.5;
	private static double pheromone_level = 500; // pheromone each ant left on its path
	
	private double[][] pheromone;
	private Ant bestAnt;
	private ArrayList<Ant> ants;

	private void generateSolution() throws ACOFailedException {
		for (Ant ant: this.ants) {
			ant.makeTour(this.pheromone);
		}
	}
	
	private void pheromoneUpdate() {
		for (int i=0; i<this.pheromone.length;++i) {
			for (int j=0; j<this.pheromone.length; ++j) {
				this.pheromone[i][j] *= evaporation;
			}
		}
		
		for (Ant ant: this.ants) {
			int[] order = ant.getOrder();
			double traveledDistance = ant.travelledDistance();
			
			double contribution = pheromone_level / traveledDistance;
			
			for (int i=0; i<order.length-1;++i) {
				this.pheromone[order[i]][order[i+1]] += contribution;
				this.pheromone[order[i+1]][order[i]] += contribution;
			}
			this.pheromone[order[order.length-1]][order[0]] += contribution;
			this.pheromone[order[0]][order[order.length-1]] += contribution;
		}
	}
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		this.bestAnt = null;
		
		this.pheromone = new double[cities.size()][cities.size()];
		for (int i=0; i<cities.size();++i) {
			for (int j=0; j<cities.size(); ++j) {
				this.pheromone[i][j] = 1.0; // default pheromone on each edge
			}
		}
		
		int numAnt = (int) Math.ceil(cities.size() * ant_factor);
		this.ants = new ArrayList<Ant>();
		for (int i=0; i<numAnt; ++i)
			this.ants.add(new Ant(cities));
		
		for (int i=0; i<num_iteration; ++i) {
			this.generateSolution();
			this.pheromoneUpdate();
			for (Ant ant: this.ants) {
				if (bestAnt == null || ant.compareTo(bestAnt) < 0) {
					bestAnt = ant.deepCopy();
				}
			}
		}
		
		return this.bestAnt.toRoute();
	}

	
}
