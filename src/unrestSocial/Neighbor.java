package unrestSocial;

import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;

import unrestSocial.CsvParser;


/*
This class finds the Neighbor of each region in the given time period it uses both the spatial distance and
the vector distance. Vector distance consider all the SCEIGE factors. The default weigh we use is 0.5 for
spatial distance and 0.5 for all other factors which could be later changed.

*/

public class Neighbor {
	
	int region;
	double neighborsize;
	int n;
	
	//this is the maximum spatial distance between each region which is used to normalize the spatial distance
	//between 0 and 1.	
	double maxspatialdistance = 630.1269428; 	// tamil nadu
	double maxvecdistance = 47.58; // tn

	// constructor
	public Neighbor(int r, int n, double size) {
		this.n = n;
		this.region = r;
		this.neighborsize = size;
	}
	
	// radius of the earth
	private static final int EARTH_RADIUS = 6371;
	double distance;

	String path = "./data/latlong.csv";
	
	CsvParser c1 = new CsvParser(path);
	List<List<Double>> data = c1.csvReader();
	
	CsvParser c2 = new CsvParser();
	List<List<Double>> vector = c2.csvReader();

	public List<Double> getVector(int i, int n){
		int t = 48;
		int index = (i)*t + (n);
//		System.out.println("for i: "+ i + " for n: " + n + " index: " + index);
		if(index < 0) {
			return this.vector.get(0).subList(1, 6);
		}
		else {
			return this.vector.get(index).subList(1, 6);
		}
	}
	
	// method to calculate the distance between each vector.
	public double calcvectordistance(List<Double>v1, List<Double>v2) {
		double sum = 0;
		for(int i=0; i<v1.size(); i++) {
			sum = sum +  Math.pow((v1.get(i) - v2.get(i)), 2);
		}
		return Math.sqrt(sum)/maxvecdistance;
	}
	
	public double haversine(double val) {
		return Math.pow(Math.sin(val/2),2);
	}
	
	// method to calculate spatial distance between two regions
	public double haversine_km(double slat, double slong, double elat, double elong) {
		double dlat = Math.toRadians(elat - slat);
		double dlong = Math.toRadians(elong - slong);
		
		slat = Math.toRadians(slat);
		elat = Math.toRadians(elat);
		
		double a = haversine(dlat) + Math.cos(slat) * Math.cos(elat) *  haversine(dlong);
		double b = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return EARTH_RADIUS * b/maxspatialdistance;
	}
	
	// method that combines two distance and compares it with the neighborsize and returns the neighbor
	public List<Integer> findNeighbor(){
		List<Integer> neighbor = new ArrayList<>();
		double distance = 0;
		double spatialdistance = 0;
		double vectordistance = 0;
		for(int i=0; i< this.data.size(); i++) {
			spatialdistance = haversine_km(this.data.get(this.region).get(0), this.data.get(this.region).get(1)
					, this.data.get(i).get(0), this.data.get(i).get(1));
			
			vectordistance = calcvectordistance(getVector(this.region, this.n), getVector(i, this.n));
			
//			System.out.println("sppatialdistance is "+ spatialdistance + " vector distance"+ vectordistance);
			
//			System.out.println("Spatial Distance is: " + spatialdistance);
//			System.out.println("Vector Distance is: "+ vectordistance);
			distance = 0.5 * spatialdistance + 0.5 * vectordistance;
			if(distance < this.neighborsize) {
//				System.out.println(distance);
//				System.out.println(neighborsize);
				neighbor.add(i);
			}
		}
		return neighbor;
	}
}