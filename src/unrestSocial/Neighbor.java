package unrestSocial;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
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
	
	/*
	For Tamil Nadu Maximum Spatial distance = 616.57, Vector distance = 47.58, nonWeighted Vector distance = 2233.18
	For Andhra Pradesh Maximum Spatial distance = 824.78 Vector distance = 62.6, nonWeighted Vector distance = 706.99
	For Himachal Pradesh Maximum Spatial distance = 245.8 Vector distance = 34.75 nonWeighted Vector distance = 1333.83
	*/
	
	/*
	 * For Tamil Nadu Mean Spatial Distance = 210.55 +/- 117.12 (327.67-93.43), Mean Vector distance = 11.37 +/- 6.74 (18.11-4.63)
	 * For Andhra Pradesh Mean Spatial Distance = 321.94 +/- 216.35 (538.29-105.59), Mean Vector distance = 17.29 +/- 9.27 (26.56-8.02)
	 * For Himachal Pradesh Mean Spatial Distance = 107.23 +/- 58.49 (165.72-48.74 ), Mean Vector distance = 9.32 +/- 5.66 (14.98-3.66)
	*/
	
	double maxspatialdistance = 327.67;
	double maxvecdistance = 18.11;
	
	

	// constructor
	public Neighbor(int r, int n, double size) {
		this.n = n;
		this.region = r;
		this.neighborsize = size;
	}
	
	// radius of the earth
	private static final int EARTH_RADIUS = 6371;
	double distance;

	
	String district_name;
	String path1 = "./data/tn/latlong.csv";
	String path2 = "./data/tn/pop_den.csv";
	
	CsvParser cr1 = new CsvParser(path1);
	CsvParser cr2 = new CsvParser(path2);
	List<List<Double>> data = cr1.csvReader();
	List<List<Double>> popden = cr2.csvReader();
	
	CsvParser c2 = new CsvParser();
	List<List<Double>> vector = c2.csvReader();
	
//	public List<Integer> getShared(){
//		List<Integer> n = new ArrayList<>();
//		for(int j=0; j<this.shared.size(); j++) {
//			int value = this.shared.get(j).get(0).intValue();
//			
//			if ( value== this.region) {
//				int dest = this.shared.get(j).get(1).intValue();
//				n.add(dest);
//			}
//		}
//		return n;
//	}
	
//	public List<Double> getSharedPercent(){
//		List<Double> n = new ArrayList<>();
//		for(int j=0; j<this.shared.size(); j++) {
//			int value = this.shared.get(j).get(0).intValue();
//			
//			if ( value== this.region) {
//				n.add(this.shared.get(j).get(2));
//			}
//		}
//		return n;
//	}
	

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
			
			distance = 0.5 * spatialdistance + 0.5 * vectordistance;
			
//			System.out.println("Distance From region: " + this.region + " to " + i +  " sd = " + spatialdistance + " vd = " + vectordistance + " td = " + distance);
			if(distance < this.neighborsize) {
				neighbor.add(i);
			}
		}
		return neighbor;
	}
	
//	public List<Double> getInfluence(){
//		List<Integer> neighbor = new ArrayList<>();
//		List<Integer> shared = new ArrayList<>();
//		List<Double> percent = new ArrayList<>();
////		List<List<String>> influence = new ArrayList<>();
//		List<Double> infvalue = new ArrayList<>();
//		
////		List<String> temp1 = new ArrayList<>();
////		List<String> temp2 = new ArrayList<>();
//		
//		neighbor = this.findNeighbor();
//		shared = this.getShared();
//		percent = this.getSharedPercent();
//		for(int i=0; i<neighbor.size(); i++) {
//			int n1 = neighbor.get(i);
//			if(shared.contains(n1)) {
//				int index = shared.indexOf(n1);
//				double p = percent.get(index);
//				infvalue.add(1+p/100);
//			}
//			else {
//				infvalue.add(1.0);
//			}
//		}
//		
//		return infvalue;
//	}
	
	public List<Double> getPopDensity(){
		List<Integer> neighbor = new ArrayList<>();

		List<Double> denvalue = new ArrayList<>();
		
		neighbor = this.findNeighbor();
		
		double regionden = this.popden.get(this.region).get(1);
		
		for(int i=0; i<neighbor.size(); i++) {
			int n1 = neighbor.get(i);
			double den = this.popden.get(n1).get(1);
			denvalue.add(den/regionden);
		}
		
//		List<String> temp1 = new ArrayList<>();
//		List<String> temp2 = new ArrayList<>();
		
		
		
		return denvalue;
	}
	
	public static void main(String[] args) {
	Neighbor n1 = new Neighbor(0, 5, 0.5);
	System.out.println(n1.data);
//	System.out.println(n1.getShared());
//	System.out.println(n1.getSharedPercent());
	System.out.println(n1.findNeighbor());
	System.out.print(n1.getPopDensity());
	}
}