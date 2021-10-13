package unrestSocial;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import unrestSocial.Tsim;
import unrestSocial.Bindex;
import unrestSocial.Cindex;

/*
This method calculates the likelihood of transmission for each region at given time step.
It takes uses the Cindex, Tsim and Bindex classes and calculates the final likelihood of
transmission.
*/


public class Likelihood {
	
	int S1;
	List<Integer> S2;
	List<Double> S3;
	int n, window;
	double neighborsize;
	boolean same;
	
	// the constructor takes a region and list of it's neighbor which are in some state
	// time step, window size and the neighbor size.
	public Likelihood(int S1, List<Integer> S2, List<Double>S3, int n, int window, double neighborsize, boolean same) {
		this.S1 = S1;
		this.S2 = S2;
		this.S3 = S3;
		this.n = n;
		this.window = window;
		this.neighborsize = neighborsize;
		this.same = same;  
	}
	

	public double likelihood() {
		Double sum = 0.0;
		for (int i=0; i<S2.size(); i++) {
			int neigh = S2.get(i);
			if(this.S1 != neigh) {
				Tsim t = new Tsim(this.S1, neigh, this.n-1);
				Bindex b = new Bindex(this.S1,neigh, this.n-1);
//				System.out.println(this.S1 + " " + t.calc_t() + " " + b.calc_b());
				if(this.same){
					if(this.S3.get(i) < 0) {
						sum = sum +  this.S3.get(i)*(t.calc_t() * b.calc_b());	
					} else {
						sum = sum +  this.S3.get(i)*(t.calc_t() * b.calc_b());	
					}
				} else {
					if(this.S3.get(i) < 0) {
						sum = sum +  this.S3.get(i)*(t.calc_t() * b.calc_b());	
					} else {
						sum = sum +  this.S3.get(i)*(t.calc_t() * b.calc_b());	
					}
				}
				
			}
		}
		return sum;
	}
	
	
	public double calc_l() {
		if(this.same) {
			return likelihood() - cpart() + this.S3.size();
		}
		else {
			return likelihood() + cpart();
		}
//		return likelihood();
	}
	
//	public double cpart() {
//		double ctotal = 0;
//		Cindex c = new Cindex(this.S1, this.n-1, this.window, this.neighborsize );
//		if(this.same) {
//			ctotal = c.calc_c();
//		}
//		else {
//			ctotal = 1 - c.calc_c();
//		}
//		return ctotal;a
//	}
	
	public double cpart() {
		double ctotal = 0;
		
		for (Integer j:this.S2) {
			if(this.S1 != j) {
				Cindex c = new Cindex(this.S1, this.n-1, this.window, this.neighborsize );
				ctotal = ctotal +  c.calc_c();	
			}
		}	
		return ctotal;
	}
	
// 	public static void main(String[] args) {
// 		List<Integer> S2 = new ArrayList<Integer>(Arrays.asList(3, 6, 7, 9));
//		Likelihood l1 = new Likelihood(10, S2, 11, 6, 0.5, true);
//		System.out.println(l1.cpart());
//		System.out.println(l1.calc_l());
//
//	}
	
}

