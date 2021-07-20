package unrestSocial;

import java.util.List;

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
	int n, window;
	double neighborsize;
	
	// the constructor takes a region and list of it's neighbor which are in some state
	// time step, window size and the neighbor size.
	public Likelihood(int S1, List<Integer> S2, int n, int window, double neighborsize) {
		this.S1 = S1;
		this.S2 = S2;
		this.n = n;
		this.window = window;
		this.neighborsize = neighborsize;
	}
	

	public double likelihood() {
		Double sum = 0.0;
			for (Integer j:this.S2) {
				if(this.S1 != j) {
					Tsim t = new Tsim(this.S1, j, this.n-1);
					Bindex b = new Bindex(this.S1,j, this.n-1);
					sum = sum +  t.calc_t() * b.calc_b();	
				}
		}
		return sum;
	}
	
	
	public double calc_l() {
//		return cpart() + likelihood();
		return likelihood();
	}
	
	public double cpart() {
		double ctotal = 0;
		
		for (Integer j:this.S2) {
			if(this.S1 != j) {
				Cindex c = new Cindex(this.S1, j, this.n-1, this.window, this.neighborsize );
				ctotal = ctotal +  c.calc_c();	
			}
		}
		return ctotal;
	}
	
}

