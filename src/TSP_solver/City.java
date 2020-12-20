package TSP_solver;

import java.lang.Math;

public class City {
	private static int cityNum = 1;
	private int label; //label each city with a positive integer
	private double x;
	private double y;
	
	public City(double x, double y) {
		this.label = cityNum++;
		this.x = x;
		this.y = y;
	}
	
	public City() {
		this(0, 0);
	}
	
	public int getLabel() {
		return this.label;
	}
	
	public static void reset() {
		cityNum = 1;
	}
	
	public double distance(City destination) {
		return Math.sqrt(Math.pow(this.x-destination.x, 2) + Math.pow(this.y-destination.y, 2));
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.label) + " " + Double.toString(this.x) + " " + Double.toString(this.y);
	}
}
