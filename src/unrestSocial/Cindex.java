package unrestSocial;

import java.util.ArrayList;
import java.util.List;

/*
This class calculates the indirect contact between two regions, the contact we calculate is temporal contact
rather than the spatial contact. We look on the history if the two regions had similar transition in the
history we factor that to calculate indirect contact index based on the gradient of the likelihood.
*/
/*
Cindex involves calling Determinestate and Likelihood class multiple times and Likelihood class calls the Cindex
class back, as it is an iterative process and involves creating multiple instances of each class due to being 
recursive and memory intensive it takes a lot of time to compute cij and Likelihood which results in slow change
of tick count while simulation.

*/

public class Cindex { 
	int i,j,n,w;
	double neighborsize;
 	CsvParser cobj = new CsvParser(); List<List<Double>> data= cobj.csvReader();
 	
 	public Cindex(int i, int n, int w, double neighborsize) { 
 		this.i = i; 
// 		this.j = j;
 		this.n = n; 
 		this.w = w; 
 		this.neighborsize = neighborsize; 
 	}

 	// method to calculate the cij
 	public double calc_c() {
 		
 		double cij = 0;
 		
		// if the time step is less than the window cij is initialized to zero.
		if(this.n <= this.w || this.w <2) {
			cij = 0;
		}
		
		else {
			
			// else we determine the state of the region i and region j of which
			// we are calculating  the cij for
	 		Determinestate di = new Determinestate(this.i, this.n);
//			Determinestate dj = new Determinestate(this.j, this.n);
			
			List<String>iFullList = new ArrayList<>();
//			List<String>jFullList = new ArrayList<>();
			
			
			iFullList = di.giveState(this.i).subList(this.n-this.w, this.n);
//			jFullList = di.giveState(this.j).subList(this.n-this.w, this.n);
			
			
			String iCurrentState = di.regionCurrentstate();
//			String jCurrentState = dj.regionCurrentstate();
			
			int count = 0;
			for(int i=0; i<iFullList.size(); i++) {
				if(iFullList.get(i) == iCurrentState) {
					count++;
				}
			}
					
			cij = (double)count/(double)this.w;
		}
		return cij;	
		
		
 	}  
// 	public static void main(String[] args) {
// 		Cindex c = new Cindex(10, 30, 11, 6, 0.5);
// 		System.out.println(c.calc_c());
//
// 	}
} 
