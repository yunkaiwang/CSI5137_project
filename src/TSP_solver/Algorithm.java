package TSP_solver;

import java.util.ArrayList;

public interface Algorithm {
	public Route solveTSP(ArrayList<City> cities) throws Exception;
}
