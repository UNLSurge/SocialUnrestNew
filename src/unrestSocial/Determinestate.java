package unrestSocial;

import java.util.List;
import java.util.ArrayList;

import unrestSocial.CsvParser;
import unrestSocial.Neighbor;

/*
This class determines the current state and list of Susceptible, Infected and Recovered
states at current time step and the list of neighbors which are in Susceptible, Infected and
Recovered state at current time step. 

*/

public class Determinestate {
	
	public Determinestate() {
		
	}
	
	CsvParser c1 = new CsvParser();
	List<List<Double>> data = c1.csvReader();
	
	
	int regions;
	int current;
	
	public Determinestate(int r, int n) {
		this.regions = r;
		this.current = n;
	}
	

	/*
	 * method to find the event count for each time step in the given region
	 * takes the region number(index) as the argument
	 * returns the list of event counts in that region for all time step
	 * 
	 */
	public List<Double> giveEventCount(int r){
		int t = 48;
		List<Double> region = new ArrayList<>();
		for(int i=0; i<t; i++) {
			int index = (r)*t + (i);
			double eventcount = this.data.get(index).get(6);
			region.add(eventcount);
		}
		return region;	
	}
	
	/*
	 * method which assigns "SIR" to each time step of the region
	 * if there are events the region is in infected state
	 * if there are no events the region is either susceptible or
	 * recovered
	 * takes a list of event counts as argument
	 * returns the list of state
	 */
	public List<String> giveState(int r){
		List<Double> reg = new ArrayList<>();
		reg = this.giveEventCount(r);
		int counter = 0;
		List<String> regstate = new ArrayList<>();
		for(int i=0; i<reg.size(); i++) {
			if (reg.get(i) == 0) {
				counter++;
				if(i == reg.size() -1) {
					for(int j=0; j<counter/2; j++) {
						regstate.add("R");
					}
					for(int k=0; k<counter-counter/2; k++) {
						regstate.add("S");
					}
				}
			}
			else {
				for(int l=0; l<counter/2; l++) {
					regstate.add("R");
				}
				for(int m=0; m<counter-counter/2; m++) {
					regstate.add("S");
				}
				regstate.add("I");
				counter = 0;
			}
		}
		for(int i=1; i<regstate.size()-1; i++) {
			if(regstate.get(i-1) == "I" && regstate.get(i) == "S" && regstate.get(i+1) == "I") {
				regstate.set(i, "I");
			}	
		}
		return regstate;
	}
	
	/*
	 * method to give the full list of state of each region for all time step
	 */
	public List<List<String>> giveFullstate(int num_r){
		List<List<String>> fullstate = new ArrayList<>();
		List<String> state = new ArrayList<>();
		for(int i=0; i<num_r; i++) {
			state = giveState(i);
			fullstate.add(state);
		}
		return fullstate;
		
	}
	
	/*
	 * method to get a list of regions which are in each state
	 * takes number of regions as argument and time stamp as an argument.
	 * return the list of region in three state
	 */
	public List<List<Integer>> determineStatelist(){
		
		List<List<String>> fullstate = new ArrayList<>();
		
//		fullstate = giveFullstate(32); // Tamil Nadu
		fullstate = giveFullstate(12); // Himachal Pradesh
//		fullstate = giveFullstate(13); // Andhra Pradesh 
		List<List<Integer>> state = new ArrayList<>();
		List<Integer> stateS = new ArrayList<>();
		List<Integer> stateI = new ArrayList<>();
		List<Integer> stateR = new ArrayList<>();
		
		for(int i=0; i<fullstate.size(); i++) {
			if(fullstate.get(i).get(this.current) == "R") {
				stateR.add(i);
			}
			else if(fullstate.get(i).get(this.current) == "I") {
				stateI.add(i);
			}
			else {
				stateS.add(i);
			}
		}
		state.add(stateS);
		state.add(stateI);
		state.add(stateR);
		
		return state;
	}
	
	/*
	 * method which finds the intersection between the list of region which are
	 * in each state (SIR) and the actual neighbors of region.
	 * 
	 * This method takes the neighborsize as input and returns the list of list
	 * of neighbor in each state.
	 * 
	 */
	
	public List<List<Integer>> neighborStatelist(double neighborsize){
		Neighbor nb = new Neighbor(this.regions, this.current,  neighborsize);
		List<Integer> neigh = new ArrayList<>();
		neigh = nb.findNeighbor();
		List<List<Integer>> state = new ArrayList<>();
		List<List<Integer>> neighstate = new ArrayList<>();
		List<Integer> neighS = new ArrayList<>();
		List<Integer> neighI = new ArrayList<>();
		List<Integer> neighR = new ArrayList<>();
		
		state = determineStatelist();
		for(Integer i:neigh) {
			for(int j=0; j<state.get(0).size(); j++) {
				if(i==state.get(0).get(j)) {
					neighS.add(i);
				}
			}
			
			for(int j=0; j<state.get(1).size(); j++) {
				if(i==state.get(1).get(j)) {
					neighI.add(i);
				}
			}
			for(int j=0; j<state.get(2).size(); j++) {
				if(i==state.get(2).get(j)) {
					neighR.add(i);
				}
			}
		}
		neighstate.add(neighS);
		neighstate.add(neighI);
		neighstate.add(neighR);
		
		return neighstate;
	}
	
	/*
	 * method to get a the current state the region is in.
	 * 
	 */
	public String regionCurrentstate() {
		String str = new String();
		List<List<Integer>> statelist = new ArrayList<>();
		statelist = determineStatelist();
		for(int i=0; i<statelist.get(0).size(); i++) {
//			System.out.println(statelist.get(0).get(i));
			if(statelist.get(0).get(i) == this.regions) {
				str = "S";
			}	
		}
		for(int i=0; i<statelist.get(1).size(); i++) {
			if(statelist.get(1).get(i) == this.regions) {
				str = "I";
			}	
		}
		for(int i=0; i<statelist.get(2).size(); i++) {
			if(statelist.get(2).get(i) == this.regions) {
				str = "R";
			}	
		}
		return str;
	}		
//	public static void main(String[] args) {
//		Determinestate d1 = new Determinestate(0, 0);
//		for(int i=0; i<13; i++) {
//			System.out.println(d1.giveEventCount(i));
//		}
//		System.out.println(d1.regionCurrentstate());
//		System.out.println(d1.giveFullstate(3));
//		d1.giveState(0);
//		System.out.println(d1.giveState(1));
//		System.out.println(d1.giveEventCount(0));
//	}
}

