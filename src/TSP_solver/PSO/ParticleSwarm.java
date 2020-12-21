package TSP_solver.PSO;

import java.util.ArrayList;
import java.util.Random;

import TSP_solver.Algorithm;
import TSP_solver.City;
import TSP_solver.Route;

public class ParticleSwarm implements Algorithm {
	private static final int NUM_PARTICLE = 100; // number of particles
	private static final int num_iteration = 2000;
	
	private Route bestRoute;
	private ArrayList<Particle> particles;
	public static final Random rand = new Random();
	
	@Override
	public Route solveTSP(ArrayList<City> cities) throws Exception {
		this.bestRoute = null;
		
		this.particles = new ArrayList<Particle>();
		for (int i=0; i<NUM_PARTICLE; ++i) {
			this.particles.add(new Particle(cities));
			
			if (this.bestRoute == null || this.particles.get(i).getRoute().compareTo(bestRoute) > 0) {
				this.bestRoute = this.particles.get(i).getRoute().deepCopy();
			}
		}
		
		for (int counter=0; counter<num_iteration; ++counter) {
			for (int i=0; i<NUM_PARTICLE; ++i) {
				this.particles.get(i).updateVelocity(this.bestRoute);
				this.particles.get(i).updatePosition();
			}
			
			for (int i=0; i<NUM_PARTICLE; ++i) {
				if (this.particles.get(i).getPBest().compareTo(bestRoute) > 0) {
					this.bestRoute = this.particles.get(i).getPBest().deepCopy();
				}
			}
		}
		
		return this.bestRoute;
	}
	
}
