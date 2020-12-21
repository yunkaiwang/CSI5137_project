package TSP_solver;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import TSP_solver.ABC.BeeColony;
import TSP_solver.ACO.AntColony;
import TSP_solver.Exception.InvalidFileException;
import TSP_solver.Exception.NoInputException;
import TSP_solver.PSO.ParticleSwarm;
import TSP_solver.SimpleAlgorithms.*;

public class TSPSolver {
	
	
	public static void main(String[] args) throws Exception {
		Algorithm al = null;
		String al_st = "";
		
		if (args.length == 0) {
			throw new NoInputException();
		} else {
			if (args[0].equalsIgnoreCase("ga")) {
				al = new GeneticAlgorithm();
				al_st = "Genetic Algorithm";
			}
			else if (args[0].equalsIgnoreCase("hc")) {
				al = new HillClimb();
				al_st = "Hill Climbing";
			}
			else if (args[0].equalsIgnoreCase("sa")) {
				al = new SimulatedAnnealing();
				al_st = "Simulated Annealing";
			}
			else if (args[0].equalsIgnoreCase("ts")) {
				al = new TabuSearch();
				al_st = "Tabu Search";
			}
			else if (args[0].equalsIgnoreCase("ca1")) {
				al = new CombinedAlgorithm1();
				al_st = "Combined Algorithm #1 (Hill Climb + GA)";
			}
			else if (args[0].equalsIgnoreCase("ca2")) {
				al = new CombinedAlgorithm2();
				al_st = "Combined Algorithm #2 (Simulated Annealing + GA)";
			}
			else if (args[0].equalsIgnoreCase("aco")) {
				al = new AntColony();
				al_st = "Ant Colony Optimization";
			}
			else if (args[0].equalsIgnoreCase("pso")) {
				al = new ParticleSwarm();
				al_st = "Particle Swarm Optimization";
			}
			else if (args[0].equalsIgnoreCase("abc")) {
				al = new BeeColony();
				al_st = "Artificial Bee Colony";
			}
			else
				throw new RuntimeException("Unknown algorithm");
		}
		
		System.out.println("Solve the TSP problem using " + al_st);
		
		if (args.length == 1) { // only the algorithm type is provided, then use the default list of cities
			ArrayList<City> cities = loadDefaultCities();
			solveTSP(cities, al);
		} else {
			for (int i=1; i<args.length;++i) {
				String arg = args[i];
				if (!arg.endsWith(".tsp")) {
					throw new InvalidFileException();
				}
				
				String filePath = System.getProperty("user.dir") + "\\data\\" + arg;
				
				File inputFile = new File(filePath);
				
				BufferedReader br = new BufferedReader(new FileReader(inputFile));
				
				String line = null;
				
				ArrayList<City> cities = new ArrayList<City>();
				
				// information that need to learn from the input file
				int dimension = -1;
				
				// load the input file, raise an exception in case of any error
				while ((line = br.readLine()) != null) {
					if (line.startsWith("TYPE")) {
						line = line.replaceAll("\\s+", "");
						String[] splitedLine = line.split(":");
						if (!splitedLine[splitedLine.length-1].equals("TSP")) {
							br.close();
							throw new InvalidFileException();
						}
					} else if (line.startsWith("EDGE_WEIGHT_TYPE")) {
						line = line.replaceAll("\\s+", "");
						String[] splitedLine = line.split(":");
						if (!splitedLine[splitedLine.length-1].equals("EUC_2D")) {
							br.close();
							throw new InvalidFileException();
						}
					} else if (line.startsWith("DIMENSION")) {
						line = line.replaceAll("\\s+", "");
						String[] splitedLine = line.split(":");
						try {
							dimension = Integer.parseInt(splitedLine[splitedLine.length-1]);
						} catch (Exception e) {
							br.close();
							throw new InvalidFileException();
						}
					} else if (line.equals("NODE_COORD_SECTION")) {
						if (dimension < 0) {
							br.close();
							throw new InvalidFileException();
						}
						
						int count = 0;
						while ((count++) < dimension && ((line=br.readLine())!=null)) {
							String[] splitedLine = line.trim().split("\\s+");
							if (splitedLine.length != 3) {
								br.close();
								throw new InvalidFileException();
							}
							try {
								City newCity = new City(Double.parseDouble(splitedLine[1]), Double.parseDouble(splitedLine[2]));
								cities.add(newCity);
							} catch (Exception e) {
								br.close();
								throw new InvalidFileException();
							}
						}
						break;
					}
				}
	
	
				// solve the TSP problem on the given set of cities
				solveTSP(cities, al);
				
				// close buffer reader
				br.close();
				
				// reset the City class for next file
				City.reset();
			}
		}
	}
	
	// returns a default list of cities in case there is no input file provided
	private static ArrayList<City> loadDefaultCities() {
		return new ArrayList<City>(Arrays.asList(
			new City(30, 5), new City(40, 10), new City(40, 20), new City(29, 25),
			new City(19, 25), new City(9, 19), new City(9, 9), new City(20, 5)
			));
	}
	
	private static void solveTSP(ArrayList<City> cities, Algorithm algo) throws Exception {
		Route geneticAlgorithmSol = algo.solveTSP(cities);
		
		System.out.println(geneticAlgorithmSol);
	}
	
	
}
