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
 	
 	public Cindex(int i, int j, int n, int w, double neighborsize) { 
 		this.i = i; 
 		this.j = j;
 		this.n = n; 
 		this.w = w; 
 		this.neighborsize = neighborsize; 
 	}

 	public double cosine_sim(List<Double>l1, List<Double> l2) {
 
 		double dotProduct = 0; double norm1 = 0; double norm2 = 0;

 		for(int i=0; i< l1.size(); i++) {
 
 			dotProduct += l1.get(i) * l2.get(i);
 			norm1 += Math.pow(l1.get(i), 2);
 			norm2 += Math.pow(l2.get(i), 2);

 		}

 		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));

 	}

 	// method to calculate the cij
 	public double calc_c() {
 		
 		double cij = 0;
 		int sum_i = 0;
 		int sum_j = 0;
 		int diff = 0;
 		int state = 0;
 		
 		double li, lri, lj, lrj;
 		
 		List<Integer> samestate_i = new ArrayList<>();
 		List<Integer> samestate_j = new ArrayList<>();
 		
 		List<Double> gradient_i = new ArrayList<>();
 		List<Double> gradient_j = new ArrayList<>();
 		
		// if the time step is less than the window cij is initialized to zero.
		if(this.n <= this.w || this.w <2) {
			cij = 0;
		}
		
		else {
			
			// else we determine the state of the region i and region j of which
			// we are calculating  the cij for
	 		Determinestate di = new Determinestate(this.i, this.n);
			Determinestate dj = new Determinestate(this.j, this.n);
			
			Probability pi = new Probability(this.i, this.n, this.w, this.neighborsize);
			Probability pj = new Probability(this.i, this.n, this.w, this.neighborsize);
			
			if(di.regionCurrentstate() == dj.regionCurrentstate()) {
				li = pi.getLsave().get(0);
				lj = pj.getLsave().get(0);
				state = 0;
			}
			else {
				li = pi.getLsave().get(1);
				lj = pj.getLsave().get(1);
				state =1;
			}
			
			
			// for the time window
			for(int k=1; k<this.w; k++) {
				
				// determine the state of i and j in the in n-k time step
	 			Determinestate dri = new Determinestate(this.i, this.n-k);
	 			Determinestate drj = new Determinestate(this.j, this.n-k);
	 			
	 			Probability pri = new Probability(this.i, this.n, this.w, this.neighborsize);
				Probability prj = new Probability(this.i, this.n, this.w, this.neighborsize);
				
				lri = pri.getLsave().get(state);
				lrj = prj.getLsave().get(state);
	 			
	 			
	 			//if state matches for i make a list of time step in which they match and another list of gradient of Likelihood 
	 			if(di.regionCurrentstate() == dri.regionCurrentstate()){
	 				samestate_i.add(k);
	 				gradient_i.add(li - lri);
	 			}
	 			
	 			//if state matches for j make a list of time step in which they match and another list of gradient of Likelihood 
	 			if(di.regionCurrentstate() == drj.regionCurrentstate()){
	 				samestate_j.add(k);
	 				gradient_j.add(lj - lrj);
	 			}
	 			
	 		}
		}
			
		// if no match found return zero
		if(samestate_i.size() == 0 || samestate_j.size() == 0) {
			cij = 0;
		}
		
		// else calculate cij
		else {
			
			// if both have same length 
			if(samestate_i.size() == samestate_j.size()) {
				for(int i=0; i<samestate_i.size(); i++) {
					sum_i = sum_i + samestate_i.get(i);
					sum_j = sum_j + samestate_j.get(i);
				}
				diff = (sum_i%this.w - sum_j%this.w);
				if (diff == 0) {
					cij = cosine_sim(gradient_i, gradient_j);
				}
				else {
					cij = cosine_sim(gradient_i, gradient_j) * Math.abs(diff/(double)this.w);
				}
				
			}
			
			//if i is smaller
			else if(samestate_i.size() < samestate_j.size()) {
				for(int i=0; i<samestate_i.size(); i++) {
					sum_i = sum_i + samestate_i.get(i);
					sum_j = sum_j + samestate_j.get(i);
					
				}
				diff = (sum_i%this.w - sum_j%this.w);
				if (diff == 0) {
					cij = cosine_sim(gradient_i.subList(0, samestate_i.size()), gradient_j);
				}
				else {
					cij = cosine_sim(gradient_i.subList(0, samestate_i.size()), gradient_j) * diff/(double)this.w;
				}
			}
			
			// if j is smaller 
			else {
				for(int i=0; i<samestate_j.size(); i++) {
					sum_i = sum_i + samestate_i.get(i);
					sum_j = sum_j + samestate_j.get(i);
					
				}
				diff = (sum_i%this.w - sum_j%this.w);
				if (diff == 0) {
					cij = cosine_sim(gradient_i.subList(0, samestate_j.size()), gradient_j);
				}
				else {
					cij = cosine_sim(gradient_i.subList(0, samestate_j.size()), gradient_j) * diff/(double)this.w;
				}
			}
		}
		
		if (Double.isNaN(cij)) {
			return 0;
		}
		else {
			return cij;
		}
 	}
} 
