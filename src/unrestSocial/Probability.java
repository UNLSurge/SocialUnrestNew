package unrestSocial;


import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Arrays;

//import myPackage.Tsim;
//import myPackage.Bindex;
import unrestSocial.Likelihood;
import unrestSocial.Determinestate;

/*
This class calculates the probability by normalizing the likelihood. Each region has two
transition option in each state i.e either to stay in the same state or to transition to different state.

*/
public class Probability {
	
	int i, n, w, run_index;
	double neighborsize;
	String path1 = "./data/random_num3.csv";
	
	public static List<List<Double>> lsave = new ArrayList<>();
	
	CsvParser cprob = new CsvParser(path1);
	List<List<Double>> rand = cprob.csvReader();
	
	//constructor
	public Probability(int i, int n, int w, double neighborhoodsize, int run_index) {
		this.i = i;
		this.n = n;
		this.w = w;
		this.neighborsize = neighborhoodsize;
		this.run_index = run_index;
	}
	
	public Probability(int i, int n, int w, double neighborhoodsize) {
		this.i = i;
		this.n = n;
		this.w = w;
		this.neighborsize = neighborhoodsize;
	}
	
	
//	random number 
	public double getRand(int i, int n, int r){
		int t = 48;
		int index = r*100 + ((i+5)*t + (n));
		return (rand.get(index).get(0));
	}
	
	// returns the list of two probabilities 
	public List<Double> calcProbability() {
		
		Determinestate d1 = new Determinestate(this.i, this.n);
		List<Integer>S = d1.neighborStatelist(this.neighborsize).get(0);		
		List<Integer>I = d1.neighborStatelist(this.neighborsize).get(1);
		
		List<Double>pS = d1.neighborPopDensitylist(this.neighborsize).get(0);		
		List<Double>pI = d1.neighborPopDensitylist(this.neighborsize).get(1);
		
		double nS = S.size();
		double nI = I.size();
		double ntotal = nS + nI;
		
//		System.out.println("Neighbor S: " + S);
//		System.out.println("Neighbor I: " + I);
//		System.out.println("Neighbor pS: " + pS);
//		System.out.println("Neighbor pI: " + pI);

		List<Double> prob = new ArrayList<>();
		double a_a = 0;
		double a_b = 0;
		double same = 0;
		double change = 0;
//		System.out.println("The regions current state is: "+ d1.regionCurrentstate());
		if(d1.regionCurrentstate() == "S") {
			Likelihood l1 = new Likelihood(this.i, S, pS,  this.n, this.w, this.neighborsize, true);
			Likelihood l2 = new Likelihood(this.i, I, pI, this.n, this.w, this.neighborsize, false);
			same = l1.calc_l() * (nS/ntotal);
			change = l2.calc_l() * (nI/ntotal);
//			System.out.println(same + " " + change + " " + (nS/ntotal) + " " + (nI/ntotal));
			a_a = same/(Math.abs(same)+ Math.abs(change));
			a_b = change/(Math.abs(same)+ Math.abs(change));
			setLsave(same, change);
			lsave = removeDuplicates(lsave);
		}
		if(d1.regionCurrentstate() == "I") {
			Likelihood l1 = new Likelihood(this.i, I, pI, this.n, this.w, this.neighborsize, true);
			Likelihood l2 = new Likelihood(this.i, S, pS, this.n, this.w, this.neighborsize, false);
			same = l1.calc_l() * (nS/ntotal);
			change = l2.calc_l() * (nS/ntotal);
//			System.out.println(same + " " + change + " " + (nS/ntotal) + " " + (nI/ntotal));
			a_a = same/(Math.abs(same)+ Math.abs(change));
			a_b = change/(Math.abs(same)+ Math.abs(change));
			
			setLsave(same, change);
			lsave = removeDuplicates(lsave);
		}
		prob.add(a_a);
		prob.add(a_b);
		System.out.println("Region " +this.i+ " in time step "+ this.n + " has neighbor "+ (S.size()+I.size()));
		return prob;
	}
	
	// determines the next state of each region based on the random number generated and probability of transmission.
	public String nextState() {
		String next_state;
		double diff;
		double randnum = getRand(this.i, this.n, this.run_index);

		Determinestate d = new Determinestate(this.i, this.n);
		String current_state = d.regionCurrentstate();
		List<Double> proba = calcProbability();
		
		diff = proba.get(1) - proba.get(0);
		
		if(diff > randnum) {
			if(current_state == "S") {
				next_state = "I";
			}
			else {
				next_state = "S";
			}
		}
		
		else {
			next_state = current_state;
		}
		
		if(current_state == "S") {
			if (proba.get(0) > randnum + 0.55) {
				next_state = "S";
			}
			else {
				next_state = "I";
			}
		}
		if(current_state == "I") {
			if(diff > 0.1 && diff < 0.5) {
				next_state = "S";
			}
			else {
				next_state = "I";
			}
		}
		
		
//		if(proba.get(0) == proba.get(1)) {
//			next_state = current_state;
//		}
//		else if(proba.get(0) < proba.get(1)) {
//			if(randnum < proba.get(0)) {
//				next_state = current_state;
//			}
//			else {
//				if(current_state == "S") {
//					next_state = "I";
//				}
//				else {
//					next_state = "S";
//				}
//			}
//		}
//		else {
//			if(randnum < proba.get(1)) {
//				if(current_state == "S") {
//					next_state = "I";
//				}
//				else {
//					next_state = "S";
//				}
//			}
//			else {
//				next_state = current_state;
//			}
//		}
		System.out.println("Current state for region " +this.i+ "in time step "+ this.n + " is "+ current_state + " Next state is " + next_state);
		return next_state;
	}
	public List<Double> getLsave() {
		List<Double> getl = new ArrayList<>();
		for(int k=0; k<lsave.size(); k++) {
			if(lsave.get(k).get(0) == (double)this.i && lsave.get(k).get(1) == (double)this.n) {
				getl = lsave.get(k).subList(2, 4);
			}
		}
		return getl;
	}
	
	public void setLsave(double lsame, double lchange) {
		List<Double> row = Arrays.asList((double)this.i, (double)this.n, lsame, lchange);
		lsave.add(row);
	}
	
	public List<List<Double>> removeDuplicates(List<List<Double>> list) 
    { 
		List<List<Double>> newlist = new ArrayList<>(); 
        for (List<Double> l: list) { 
            if (!newlist.contains(l)) { 
                newlist.add(l); 
            } 
        } 

        return newlist; 
    } 
	
//	public double chosen() {
//		List<Double> proba1 = calcProbability();
//		Determinestate d1 = new Determinestate(this.i, this.n);
//		String current_state1 = d1.regionCurrentstate();
//		String next_state1 = nextState();
//		
//		if(next_state1 == current_state1) {
//			return proba1.get(0);
//		}
//		else {
//			return proba1.get(1);
//		}
//	}
	public static void main(String[] args) {
		for(int i = 0; i < 48; i++) {
			Probability pexi = new Probability(11, i, 5, 0.5);
			pexi.nextState();
			System.out.println(pexi.calcProbability());
		}
//		Probability pex0 = new Probability(11, 9, 8, 0.5);
//		Probability pex1 = new Probability(11, 10, 8, 0.5);
//		Probability pex2 = new Probability(11, 11, 8, 0.5);
//		Probability pex3 = new Probability(11, 10, 8, 0.5);
//		Probability pex4 = new Probability(11, 12, 8, 0.5);
//		System.out.println(pex0.calcProbability());
//		System.out.println(pex1.calcProbability());
//		System.out.println(pex2.calcProbability());
//		System.out.println(pex3.calcProbability());
//		System.out.println(pex4.calcProbability());
	}
}